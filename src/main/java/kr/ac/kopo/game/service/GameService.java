package kr.ac.kopo.game.service;

import java.util.List;

import kr.ac.kopo.game.dto.GameReplayDTO;
import kr.ac.kopo.game.dto.GameRoomInfoDTO;
import kr.ac.kopo.game.vo.GameHistoryVO;
import kr.ac.kopo.game.vo.GameMoveVO;
import kr.ac.kopo.game.vo.GameRoomVO;
import kr.ac.kopo.game.vo.RoomParticipantVO;
import kr.ac.kopo.user.vo.UserVO;

public interface GameService {
    // 대전 방 생성
	GameRoomVO createGameRoom(UserVO user, String title, int maxParticipants, String isPrivate);
	GameRoomVO createGameRoom(UserVO user, String title);
    // 빠른 대전 (대기방 찾거나 새로 생성)
    GameRoomVO matchOrCreateRoom(UserVO user);
    void addUserToRoom(UserVO user, int roomId);
    // 쓰기 전에 commit쓰기 전에 commit쓰기 전에 commit
    void updateRoomStatus(GameRoomVO room);
    // 방 정보 가져오기
    GameRoomVO getRoomDetails(int roomId);
    // 받아오능ㄴ고ㅓㅁ앙ㄴㅁ
    void saveGameResult(GameHistoryVO history, List<GameMoveVO> moves);

    // 이상함. currentP count 오류
    List<RoomParticipantVO> getRoomParticipants(int roomId);
    // WAITING 이외, 검색 로직 수정. 해야함
    List<GameRoomInfoDTO> getJoinableRooms();
    //void handleUserLeave(UserVO user, int roomId);
    void leaveRoom(UserVO user, int roomId); // 참가자 1명 퇴장
    //다나가면 방삭제.
    void deleteRoom(int roomId);
    GameRoomVO createAiGameRoom(UserVO humanPlayer, UserVO aiPlayer);
    //복기
    GameReplayDTO getGameReplayData(Long userId, int gameId);
   
}
