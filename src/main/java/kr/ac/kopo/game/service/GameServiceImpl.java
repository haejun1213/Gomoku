package kr.ac.kopo.game.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ac.kopo.game.dao.GameDAO;
import kr.ac.kopo.game.dto.GameHistoryInfoDTO;
import kr.ac.kopo.game.dto.GameReplayDTO;
import kr.ac.kopo.game.dto.GameRoomInfoDTO;
import kr.ac.kopo.game.vo.GameHistoryVO;
import kr.ac.kopo.game.vo.GameMoveVO;
import kr.ac.kopo.game.vo.GameRoomVO;
import kr.ac.kopo.game.vo.RoomParticipantVO;
import kr.ac.kopo.user.dao.UserMapper;
import kr.ac.kopo.user.vo.UserVO;

@Service
public class GameServiceImpl implements GameService {

	@Autowired
	private GameDAO gameDAO;

	@Autowired
    private UserMapper userMapper;
	
	@Override
    @Transactional
    public GameRoomVO createAiGameRoom(UserVO humanPlayer, UserVO aiPlayer) {
        // 1. AI 대전용 방 생성
        // 기본값으로 2인용, 비공개방으로 생성합니다.
        String title = humanPlayer.getNickname() + " vs " + aiPlayer.getNickname();
        GameRoomVO aiRoom = this.createGameRoom(humanPlayer, title, 2, "Y");
        
        // 2. AI를 참가자로 DB에 추가
        // (사람 플레이어는 createGameRoom 메서드 안에서 이미 방장(HOST)으로 추가되었습니다)
        RoomParticipantVO aiParticipant = new RoomParticipantVO();
        aiParticipant.setRoomId(aiRoom.getRoomId());
        aiParticipant.setUserId(aiPlayer.getUserId()); // Long -> int 변환 불필요 시 .intValue() 제거
        aiParticipant.setRole("CHALLENGER");
        gameDAO.addParticipant(aiParticipant);
        
        // 3. 생성된 방의 상태를 즉시 'PLAYING'으로 업데이트
        aiRoom.setStatus("PLAYING");
        gameDAO.updateRoomStatus(aiRoom);
        
        return aiRoom;
    }
	
	@Override
    @Transactional
    public GameRoomVO createGameRoom(UserVO user, String title) {
        // 완전한 버전을 호출하되, 기본값(최대 8명, 공개 'N')을 여기서 지정해줍니다.
        return this.createGameRoom(user, title, 8, "N");
    }
	
	@Override
	@Transactional
	public GameRoomVO createGameRoom(UserVO user, String title, int maxParticipants, String isPrivate) {
		GameRoomVO newRoom = new GameRoomVO();
		newRoom.setTitle(title);
		newRoom.setHostUserId(user.getUserId());
		// 새로 받은 값 설정
		newRoom.setMaxParticipants(maxParticipants);
		newRoom.setIsPrivate(isPrivate);

		gameDAO.createRoom(newRoom);

		RoomParticipantVO hostParticipant = new RoomParticipantVO();
		hostParticipant.setRoomId(newRoom.getRoomId());
		hostParticipant.setUserId(user.getUserId());
		hostParticipant.setRole("HOST");
		gameDAO.addParticipant(hostParticipant);

		return newRoom;
	}

	@Override
    @Transactional
    public GameRoomVO matchOrCreateRoom(UserVO user) {
        // 1. 대기 중인 방을 찾는다.
        GameRoomVO waitingRoom = gameDAO.findWaitingRoom();

        // --- Case 1: 대기 중인 방이 있을 경우 ---
        if (waitingRoom != null) {
            List<RoomParticipantVO> participants = gameDAO.getParticipantsByRoomId(waitingRoom.getRoomId());

            // 1-1. 이미 내가 그 방에 들어가 있는지 확인 (중복 클릭 방지)
            boolean isAlreadyParticipant = participants.stream()
                    .anyMatch(p -> p.getUserId().equals(user.getUserId()));
            if (isAlreadyParticipant) {
                return waitingRoom; // 이미 참가자면, 그냥 해당 방 정보를 반환
            }

            // 1-2. 방이 꽉 차지 않았다면 참가
            if (participants.size() < waitingRoom.getMaxParticipants()) {
                RoomParticipantVO challenger = new RoomParticipantVO();
                challenger.setRoomId(waitingRoom.getRoomId());
                challenger.setUserId(user.getUserId());
                challenger.setRole("CHALLENGER");
                gameDAO.addParticipant(challenger);

                // 참가 후 인원이 2명이 되면 즉시 게임 시작 상태로 변경 (PvP 자동매칭의 경우)

                
                return waitingRoom;
            }
        }

        // --- Case 2: 대기 중인 방이 없거나, 꽉 찼을 경우 ---
        // 새로운 방을 생성합니다.
        String title = user.getNickname() + "님의 방";
        return createGameRoom(user, title, 8, "N"); // 기본값으로 방 생성
    }
	@Override
	@Transactional
	public void saveGameResult(GameHistoryVO history, List<GameMoveVO> moves) {
		// 1. 게임 히스토리 저장 (이 때 history 객체에 gameId가 채워짐)
		gameDAO.insertGameHistory(history);
		int gameId = history.getGameId();

		// 2. 각 기보에 gameId를 설정
		if (moves != null && !moves.isEmpty()) {
			for (GameMoveVO move : moves) {
				move.setGameId(gameId);
			}
			// 3. 기보 목록 한 번에 저장
			gameDAO.insertGameMoves(moves);
		}
	}
	
	@Override
    public List<RoomParticipantVO> getRoomParticipants(int roomId) {
        // DAO를 호출하여 DB에서 참가자 목록을 가져옵니다.
        // 이제 이 목록에는 각 참가자의 UserVO 정보도 포함되어 있습니다.
        return gameDAO.getParticipantsByRoomId(roomId);
    }

	@Override
    @Transactional
    public void addUserToRoom(UserVO user, int roomId) {
        if (user == null) {
            return; // 유저 정보 없으면 중단
        }

        // 1. 방 정보를 가져옴
        GameRoomVO room = gameDAO.getRoomById(roomId);
        if (room == null) {
            return; // 방이 없으면 중단
        }

        // 2. 현재 참가자 목록을 가져옴
        List<RoomParticipantVO> participants = gameDAO.getParticipantsByRoomId(roomId);

        // 3. 이미 참가자인지 확인
        boolean isAlreadyIn = participants.stream()
                .anyMatch(p -> p.getUserId() == user.getUserId());
        if (isAlreadyIn) {
            return; // 이미 있으면 중단
        }

        // 4. 방이 꽉 찼는지 확인
        if (participants.size() >= room.getMaxParticipants()) {
            // 이 경우, 관전도 불가능하게 처리하려면 여기서 중단
            // (관전은 가능하게 하려면 이 로직은 불필요)
            System.out.println("방이 꽉 찼습니다. (roomId: " + roomId + ")");
            return;
        }

        // 5. 모든 검사를 통과했으면, DB에 참가자로 추가
        RoomParticipantVO newParticipant = new RoomParticipantVO();
        newParticipant.setRoomId(roomId);
        newParticipant.setUserId(user.getUserId());
        newParticipant.setRole("SPECTATOR"); // 입장 시 기본 역할은 관전자
        gameDAO.addParticipant(newParticipant);
    }
	
	/*
	 * @Override
	 * 
	 * @Transactional public void handleUserLeave(UserVO user, int roomId) { if
	 * (user == null) return;
	 * 
	 * // 1. ROOM_PARTICIPANTS 테이블에서 해당 유저를 삭제 gameDAO.deleteParticipant(roomId,
	 * user.getUserId());
	 * 
	 * // 2. 방에 남은 인원수를 확인 int remainingParticipants =
	 * gameDAO.countParticipantsInRoom(roomId);
	 * 
	 * // 3. 만약 남은 인원이 0명이라면, GAME_ROOMS 테이블에서 방 자체를 삭제 if (remainingParticipants ==
	 * 0) { gameDAO.deleteRoom(roomId); } }
	 */
	
	@Override
    public GameReplayDTO getGameReplayData(Long userId, int gameId) {
        // 1. 특정 게임의 기록을 가져옴 (상대방 닉네임 포함)
        GameHistoryInfoDTO gameInfo = userMapper.findGameHistoryByGameId(userId, gameId); // 이 DAO 메서드는 아래에 정의
        if (gameInfo == null) return null; // 해당 유저의 게임이 아니거나 없는 게임이면 null 반환

        // 2. 해당 게임의 모든 수순을 가져옴
        List<GameMoveVO> moves = gameDAO.findMovesByGameId(gameId);
        
        // 3. DTO에 담아서 반환
        GameReplayDTO replayData = new GameReplayDTO();
        replayData.setGameInfo(gameInfo);
        replayData.setMoves(moves);
        System.out.println(replayData);
        return replayData;
    }
	
	@Override
    @Transactional
    public void leaveRoom(UserVO user, int roomId) {
        if (user == null) return;
        gameDAO.deleteParticipant(roomId, user.getUserId());
    }

    @Override
    @Transactional
    public void deleteRoom(int roomId) {
        //gameDAO.deleteAllParticipantsByRoomId(roomId);
        gameDAO.deleteRoom(roomId);
    }
	
	@Override
    public List<GameRoomInfoDTO> getJoinableRooms() {
        return gameDAO.findJoinableRooms();
    }
	
	@Override
	public GameRoomVO getRoomDetails(int roomId) {
		return gameDAO.getRoomById(roomId);
	}

	@Override
	public void updateRoomStatus(GameRoomVO room) {
	    gameDAO.updateRoomStatus(room);
	}
	
}