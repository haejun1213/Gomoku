package kr.ac.kopo.game.websocket;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import kr.ac.kopo.game.dto.BaseMessage;
import kr.ac.kopo.game.dto.ChatMessage;
import kr.ac.kopo.game.dto.ClientChatMessage;
import kr.ac.kopo.game.dto.GameOverMessage;
import kr.ac.kopo.game.dto.GameStartMessage;
import kr.ac.kopo.game.dto.JoinAsPlayerMessage;
import kr.ac.kopo.game.dto.LeaveAsPlayerMessage;
import kr.ac.kopo.game.dto.MoveMessage;
import kr.ac.kopo.game.dto.ReadyMessage;
import kr.ac.kopo.game.dto.SurrenderMessage;
import kr.ac.kopo.game.dto.UpdateStateMessage;
import kr.ac.kopo.game.service.AIService;
import kr.ac.kopo.game.service.GameService;
import kr.ac.kopo.game.vo.GameHistoryVO;
import kr.ac.kopo.game.vo.GameMoveVO;
import kr.ac.kopo.game.vo.GameRoomVO;
import kr.ac.kopo.game.vo.RoomParticipantVO;
import kr.ac.kopo.user.vo.UserVO;
import lombok.Getter;
import oracle.jdbc.proxy.annotation.OnError;

@Component
@ServerEndpoint(value = "/ws/game/{roomId}", configurator = WebSocketConfig.class)
public class GameWebSocketHandler implements DisposableBean {

	private static final Map<Integer, Room> rooms = new ConcurrentHashMap<>();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static GameService gameService;
	private static AIService aiService;
	private static final Timer roomResetScheduler = new Timer("RoomResetScheduler", true);

	private static final Set<String> BANNED_WORDS = new HashSet<>(Arrays.asList(
			"바보", "멍청이", "등신", "병신", "지랄", "염병", "엿먹어", "꺼져",
	        "씨발", "시발", "개새끼", "새끼", "미친", "미친놈", "미친년", 
	        "ㅅㅂ", "ㅆㅂ", "tlqkf", "ㅈㄴ", "존나", "졸라", "개새", "ㄱㅅㄲ", "ㅁㅊ", "ㅂㅅ", "ㅈㄹ"
			)); // 예시 단어

	@Autowired
	public void setGameService(GameService gameService) {
		GameWebSocketHandler.gameService = gameService;
	}

	@Autowired
	public void setAiService(AIService aiService) {
		GameWebSocketHandler.aiService = aiService;
	}

	@Override
	public void destroy() throws Exception {
		roomResetScheduler.cancel();
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("roomId") int roomId) {
		Room room = rooms.computeIfAbsent(roomId, k -> new Room(roomId));
		UserVO user = (UserVO) ((HttpSession) session.getUserProperties().get("httpSession"))
				.getAttribute("loggedInUser");

		if (user == null) {
			try {
				session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "User not logged in"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		// gameService.addUserToRoom(user, roomId);
		room.addSession(session, user);

		List<RoomParticipantVO> participantsInDb = gameService.getRoomParticipants(roomId);
		Optional<UserVO> aiBotOptional = participantsInDb.stream().map(RoomParticipantVO::getUser)
				.filter(u -> "B".equals(u.getRole())).findFirst();

		if (aiBotOptional.isPresent()) {
			UserVO aiBot = aiBotOptional.get();
			room.setPlayerSlot(user);
			room.setPlayerSlot(aiBot);
			room.setPlayerReady(aiBot.getUserId(), true); // AI는 항상 준비상태
			room.startGame(true);
			broadcast(roomId, room.createGameStartMessage());
		} else {
			broadcast(roomId, room.createRoomStateMessage());
		}
	}

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("roomId") int roomId) throws IOException {
		Room room = rooms.get(roomId);
		UserVO user = (UserVO) session.getUserProperties().get("user");
		if (room == null || user == null)
			return;

		BaseMessage baseMsg = objectMapper.readValue(message, BaseMessage.class);

		if (baseMsg instanceof ClientChatMessage) {
			String originalContent = ((ClientChatMessage) baseMsg).getContent();

			
			String filteredContent = originalContent;
			for (String bannedWord : BANNED_WORDS) {
				if (filteredContent.contains(bannedWord)) {
					String asterisks = String.join("", Collections.nCopies(bannedWord.length(), "*"));
					filteredContent = filteredContent.replaceAll(bannedWord, asterisks);
				}
			}

			broadcast(roomId, new ChatMessage(user.getUserId(), user.getNickname(), filteredContent));
		} else if (baseMsg instanceof JoinAsPlayerMessage) {
			room.setPlayerSlot(user);
			broadcast(roomId, room.createRoomStateMessage());
		} else if (baseMsg instanceof LeaveAsPlayerMessage) {
			room.removePlayerSlot(user);
			broadcast(roomId, room.createRoomStateMessage());
		} else if (baseMsg instanceof ReadyMessage) {
			room.setPlayerReady(user.getUserId(), ((ReadyMessage) baseMsg).isReady());

			if (room.isGameReadyToStart()) {
				// AI 대전 재시작인지, PvP 시작인지 구분
				boolean isAiMatch = room.isUserPlayer(0L); // USER_ID 0L아니고 105임, 근데 읽힘, 뭐지

				room.startGame(isAiMatch); // AI 대전이면 사람이 선공, 아니면 랜덤

				gameService.updateRoomStatus(new GameRoomVO(roomId, "PLAYING"));
				broadcast(roomId, room.createGameStartMessage());

				// AI가 선공일 경우는 없으므로, AI 턴 트리거는 Move 다음에만 필요
				// #나중에 ai별 난이도 나누면 로직 그거 추가
				triggerAiMoveIfNeeded(room, roomId);
			} else {
				broadcast(roomId, room.createRoomStateMessage());
			}
		} else if (baseMsg instanceof SurrenderMessage) {
			if (room.isGameInProgress() && room.isUserPlayer(user.getUserId())) {
				UserVO winner = room.getOpponent(user);
				handleGameEnd(room, roomId, "상대방의 항복", winner);
			}
		} else if (baseMsg instanceof MoveMessage) {
			if (room.isMyTurn(user.getUserId())) {
				MoveMessage move = (MoveMessage) baseMsg;
				if (room.placeStone(move.getX(), move.getY())) {

					broadcast(roomId, room.createUpdateStateMessage(move.getX(), move.getY()));

					if (room.checkWin(move.getX(), move.getY())) {
						handleGameEnd(room, roomId, "오목 완성", room.getCurrentTurnPlayer());
					} else if (room.isBoardFull()) {
						handleGameEnd(room, roomId, "무승부", null);
					} else {
						room.nextTurn();
						triggerAiMoveIfNeeded(room, roomId);
					}
				}
			}
		}
	}

	private void handleGameEnd(Room room, int roomId, String reason, UserVO winner) {
		room.finishGame(winner);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		broadcast(roomId, room.createGameOverMessage(reason));
		saveGame(room);
		scheduleRoomReset(roomId);
	}

	private void triggerAiMoveIfNeeded(Room room, int roomId) {
		if (room == null || !room.isGameInProgress())
			return;

		UserVO currentPlayer = room.getCurrentTurnPlayer();
		if (currentPlayer != null && "B".equals(currentPlayer.getRole())) {
			// AI가 생각하는 것처럼 보이게 딜레이
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			int aiPlayerNumber = room.getBlackPlayer().getUserId().equals(currentPlayer.getUserId()) ? 1 : 2;
			Point aiMove = aiService.calculateNextMove(room.getBoard(), aiPlayerNumber);

			if (aiMove != null && room.placeStone(aiMove.x, aiMove.y)) {
				// AI가 둔 수를 클라이언트에게 전파
				broadcast(roomId, room.createUpdateStateMessage(aiMove.x, aiMove.y));

				// AI가 이겼는지 확인
				if (room.checkWin(aiMove.x, aiMove.y)) {
					handleGameEnd(room, roomId, "AI 승리!", room.getCurrentTurnPlayer());
					return;
				}
				// 무승부 확인
				if (room.isBoardFull()) {
					handleGameEnd(room, roomId, "무승부", null);
					return;
				}
				// AI의 수가 끝났으므로 턴을 다시 사람에게 넘김
				room.nextTurn();
			}
		}
	}

	// 방에서 누가 나갈때
	@OnClose
	public void onClose(Session session, @PathParam("roomId") int roomId) {
		Room room = rooms.get(roomId);
		if (room == null)
			return;

		UserVO user = (UserVO) session.getUserProperties().get("user");
		if (user == null) {
			// 비정상적인 세션(로그인 안된 유저 등)이면, 그냥 세션 제거
			room.removeSession(session, null);
			if (room.isEmpty()) {
				rooms.remove(roomId);
			}
			return;
		}

		// 유저가 나가기 전, 이 유저가 게임 중인 플레이어였는지 미리 확인
		boolean wasPlayerInGame = room.isGameInProgress() && room.isUserPlayer(user.getUserId());

		// 1. DB의 ROOM_PARTICIPANTS 테이블에서 나간 유저를 삭제
		gameService.leaveRoom(user, roomId);

		// 2. 메모리(Room 객체)에서도 유저를 제거
		room.removeSession(session, user);

		// 3. 유저가 나간 후, 방이 완전히 비었는지 확인
		if (room.isEmpty()) {
			// 방이 비었으면 DB와 메모리에서 모두 삭제
			// #Room객체랑 GameRoom클래스 같이 다뤄야할듯.
			gameService.deleteRoom(roomId);
			rooms.remove(roomId);
			System.out.println("Room " + roomId + " is empty and has been deleted.");
			return; // 모든 처리가 끝났으므로 함수 종료
		}

		// --- 방에 아직 다른 유저가 남아있는 경우 ---
		// 4. 만약 진행중이던 게임에서 '플레이어'가 나갔다면, 상대방을 승자로 처리
		if (wasPlayerInGame) {
			UserVO opponent = room.getOpponent(user);
			if (opponent != null) {
				// 이 메서드는 게임 종료, 결과 저장, 3초 후 리셋
				handleGameEnd(room, roomId, "상대방의 연결 끊김", opponent);
			}
		} else {
			// 5. 게임 중이 아니었거나, 관전자가 나갔다면,
			// 변경된 참가자 목록을 남은 사람들에게 쏴줌
			broadcast(roomId, room.createRoomStateMessage());
		}
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	private void saveGame(Room room) {
		if (room.getBlackPlayer() == null || room.getWhitePlayer() == null)
			return;
		GameHistoryVO history = room.createGameHistory();
		gameService.saveGameResult(history, room.getMoves());
	}

	private void broadcast(int roomId, Object messageObject) {
		Room room = rooms.get(roomId);
		if (room != null && messageObject != null) {
			try {
				String jsonMessage = objectMapper.writeValueAsString(messageObject);
				room.getSessions().forEach(s -> {
					try {
						if (s.isOpen())
							s.getBasicRemote().sendText(jsonMessage);
					} catch (IOException e) {
						/* 이미 닫힌 세션에 보내려 할 때의 예외는 무시 */ }
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void scheduleRoomReset(int roomId) {
		roomResetScheduler.schedule(new TimerTask() {
			@Override
			public void run() {
				Room room = rooms.get(roomId);
				if (room != null) {
					room.resetForNewGame();
					GameRoomVO roomVO = new GameRoomVO();
					roomVO.setRoomId(roomId);
					roomVO.setStatus("WAITING");
					gameService.updateRoomStatus(roomVO);
					broadcast(roomId, room.createRoomStateMessage());
				}
			}
		}, 3000);
	}

	// GameWebSocketHandler 클래스 내부에 위치

	@Getter
	private static class Room {
		private final int roomId;
		private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
		private final Map<Long, UserVO> participants = new ConcurrentHashMap<>();
		private final Map<Long, Boolean> readyStatus = new ConcurrentHashMap<>();

		private UserVO player1;
		private UserVO player2;

		private int[][] board;
		private UserVO blackPlayer;
		private UserVO whitePlayer;
		private UserVO winner;
		private String currentTurn;
		private boolean gameInProgress;
		private Date startTime;
		private List<GameMoveVO> moves;

		public Room(int roomId) {
			this.roomId = roomId;
			resetForNewGame(); // 처음 생성 시 모든 상태를 초기화합니다.
		}

		/**
		 * 1. addSession, removeSession: 유저 접속/종료 시 세션과 참가자 정보 관리 (새로고침 시 발생하는 유령 세션을
		 * 정리하는 로직 포함)
		 */
		public void addSession(Session session, UserVO user) {
			if (user != null && participants.containsKey(user.getUserId())) {
				sessions.stream().filter(s -> {
					UserVO existingUser = (UserVO) s.getUserProperties().get("user");
					return existingUser != null && user.getUserId().equals(existingUser.getUserId());
				}).findFirst().ifPresent(oldSession -> {
					try {
						oldSession.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					sessions.remove(oldSession);
				});
			}
			session.getUserProperties().put("user", user);
			sessions.add(session);
			if (user != null) {
				participants.putIfAbsent(user.getUserId(), user);
			}
		}

		public void removeSession(Session session, UserVO user) {
			if (user != null) {
				removePlayerSlot(user); // 플레이어였다면 슬롯에서 제거
				participants.remove(user.getUserId());
			}
			sessions.remove(session);
		}

		public void setPlayerSlot(UserVO user) {
			if (gameInProgress || isUserPlayer(user.getUserId()))
				return;
			if (player1 == null)
				player1 = user;
			else if (player2 == null)
				player2 = user;
		}

		public void removePlayerSlot(UserVO user) {
			if (user == null || gameInProgress)
				return;
			if (player1 != null && player1.getUserId().equals(user.getUserId())) {
				player1 = null;
				readyStatus.remove(user.getUserId());
			} else if (player2 != null && player2.getUserId().equals(user.getUserId())) {
				player2 = null;
				readyStatus.remove(user.getUserId());
			}
		}

		/**
		 * 2. setPlayerReady: 유저의 준비상태 변경
		 */
		public void setPlayerReady(Long userId, boolean isReady) {
			if (gameInProgress)
				return;
			if (isUserPlayer(userId)) {
				readyStatus.put(userId, isReady);
			}
		}

		/**
		 * 3. isGameReadyToStart: 두 명의 플레이어가 모두 준비했는지 확인
		 */
		public boolean isGameReadyToStart() {
			if (gameInProgress || player1 == null || player2 == null)
				return false;
			return readyStatus.getOrDefault(player1.getUserId(), false)
					&& readyStatus.getOrDefault(player2.getUserId(), false);
		}

		/**
		 * 4. startGame: 흑/백 랜덤 배정 후 게임 시작
		 */
		public void startGame(boolean isAiMatch) {
			this.gameInProgress = true;
			this.startTime = new Date();

			if (isAiMatch) {
				// AI 대전: 사람이 흑, AI가 백으로 고정
				if (player1 != null && "B".equals(player1.getRole())) {
					this.blackPlayer = player2;
					this.whitePlayer = player1;
				} else {
					this.blackPlayer = player1;
					this.whitePlayer = player2;
				}
			} else {
				// PvP: 흑/백 랜덤 배정
				List<UserVO> players = new ArrayList<>(Arrays.asList(player1, player2));
				Collections.shuffle(players);
				this.blackPlayer = players.get(0);
				this.whitePlayer = players.get(1);
			}

			this.currentTurn = "BLACK";
		}

		public void startGame() {
			startGame(false); // 기본값은 PvP
		}

		/* 실제 인게임 관련 함수 들 */

		// 게임 종료.
		public void finishGame(UserVO winner) {
			this.gameInProgress = false;
			this.winner = winner;
		}

		// 게임 종료 후 대기실로 돌아가기 위한 초기화 메서드
		public void resetForNewGame() {
			this.board = new int[15][15];
			this.blackPlayer = null;
			this.whitePlayer = null;
			this.winner = null;
			this.currentTurn = null;
			this.gameInProgress = false;
			this.startTime = null;
			this.moves = new ArrayList<>();

			// 모든 플레이어의 준비 상태를 '준비 안됨'으로 초기화
			this.readyStatus.replaceAll((k, v) -> false);

			// AI는 자동으로 다시 준비 완료 상태로 만듦
			if (player1 != null && "B".equals(player1.getRole())) {
				readyStatus.put(player1.getUserId(), true);
			}
			if (player2 != null && "B".equals(player2.getRole())) {
				readyStatus.put(player2.getUserId(), true);
			}
		}

		// 현재 자기 턴인지
		public boolean isMyTurn(Long userId) {
			if (!gameInProgress)
				return false;
			UserVO turnPlayer = getCurrentTurnPlayer();
			return turnPlayer != null && turnPlayer.getUserId().equals(userId);
		}

		// 전 턴 누군지
		public UserVO getCurrentTurnPlayer() {
			if ("BLACK".equals(currentTurn))
				return blackPlayer;
			return whitePlayer;
		}

		// 탈주하면 상대방 반환 ( 승자 반환 )
		public UserVO getOpponent(UserVO leaver) {
			if (leaver == null || player1 == null || player2 == null)
				return null;
			if (leaver.getUserId().equals(player1.getUserId()))
				return player2;
			if (leaver.getUserId().equals(player2.getUserId()))
				return player1;
			return null;
		}

		// 담턴
		public void nextTurn() {
			this.currentTurn = "BLACK".equals(currentTurn) ? "WHITE" : "BLACK";
		}

		// 돌 두기
		// 현재는 범위 내인지, 돌 색이 뭔지, 복기용 기록 저장만.
		public boolean placeStone(int x, int y) {
			if (x < 0 || x >= 15 || y < 0 || y >= 15 || board[y][x] != 0)
				return false;
			board[y][x] = "BLACK".equals(currentTurn) ? 1 : 2;
			GameMoveVO move = new GameMoveVO();
			move.setMoveOrder(moves.size() + 1);
			move.setXCoord(x);
			move.setYCoord(y);
			move.setColor(currentTurn);
			moves.add(move);
			return true;
		}

		// 승리 조건 체크
		public boolean checkWin(int x, int y) {
			int color = board[y][x];
			if (color == 0)
				return false;
			return (countConsecutive(x, y, color, 1, 0) >= 5 || countConsecutive(x, y, color, 0, 1) >= 5
					|| countConsecutive(x, y, color, 1, 1) >= 5 || countConsecutive(x, y, color, 1, -1) >= 5);
		}

		/* # 흑돌이면 33안되고 44안되고 6목 안되는 로직 만들어야함. 언젠가 */

		// 승리 조건 체크
		private int countConsecutive(int x, int y, int color, int dx, int dy) {
			int count = 1;
			for (int i = 1; i < 5; i++) {
				int nx = x + i * dx, ny = y + i * dy;
				if (nx >= 0 && nx < 15 && ny >= 0 && ny < 15 && board[ny][nx] == color)
					count++;
				else
					break;
			}
			for (int i = 1; i < 5; i++) {
				int nx = x - i * dx, ny = y - i * dy;
				if (nx >= 0 && nx < 15 && ny >= 0 && ny < 15 && board[ny][nx] == color)
					count++;
				else
					break;
			}
			return count;
		}

		// 무승부 조건
		public boolean isBoardFull() {
			for (int y = 0; y < 15; y++) {
				for (int x = 0; x < 15; x++) {
					if (board[y][x] == 0) {
						return false; // 빈 칸이 하나라도 있으면 무승부가 아님
						// #나중에 33, 44, 6목 적용하면 수정해야함.
					}
				}
			}
			return true; // 모든 칸이 다 찼음
		}

		// 게임 종료
		public void finishGame() {
			this.gameInProgress = false;
			this.winner = null; // 승자가 없음
		}

		public Map<String, Object> createRoomStateMessage() {
			List<Map<String, Object>> participantInfos = participants.values().stream().filter(Objects::nonNull)
					.map(p -> {
						Map<String, Object> info = new HashMap<>();
						info.put("userId", p.getUserId());
						info.put("nickname", p.getNickname());
						String role = "관전자";
						if (player1 != null && p.getUserId().equals(player1.getUserId()))
							role = "플레이어1";
						else if (player2 != null && p.getUserId().equals(player2.getUserId()))
							role = "플레이어2";
						info.put("role", role);
						info.put("isReady", readyStatus.getOrDefault(p.getUserId(), false));
						return info;
					}).collect(Collectors.toList());

			Map<String, Object> message = new HashMap<>();
			message.put("type", "ROOM_STATE");
			message.put("participants", participantInfos);
			message.put("gameInProgress", this.gameInProgress);
			return message;
		}

		public GameStartMessage createGameStartMessage() {
			List<Map<String, Object>> participantInfos = this.participants.values().stream().filter(Objects::nonNull)
					.map(p -> {
						Map<String, Object> info = new HashMap<>();
						info.put("userId", p.getUserId());
						info.put("nickname", p.getNickname());
						String role = "관전자";
						if (player1 != null && p.getUserId().equals(player1.getUserId()))
							role = "플레이어1";
						else if (player2 != null && p.getUserId().equals(player2.getUserId()))
							role = "플레이어2";
						info.put("role", role);
						info.put("isReady", readyStatus.getOrDefault(p.getUserId(), false));
						return info;
					}).collect(Collectors.toList());

			return GameStartMessage.builder().blackPlayerId(blackPlayer.getUserId())
					.blackPlayerNickname(blackPlayer.getNickname())// 테이블에 추가하는게 좋은거같긴함
					.whitePlayerId(whitePlayer.getUserId())//
					.whitePlayerNickname(whitePlayer.getNickname()).participants(participantInfos) // 참가자 목록 추가
					.gameInProgress(this.gameInProgress) // 게임 진행 상태 추가
					.build();
		}

		// #방에 중간에 입장하면 전 돌이 안보임. createUpdateAllStateMessage 추가 요함
		public UpdateStateMessage createUpdateStateMessage(int x, int y) {
			String placedColor = this.currentTurn;
			String nextPlayerTurn = "BLACK".equals(placedColor) ? "WHITE" : "BLACK";
			return UpdateStateMessage.builder().x(x).y(y).color(placedColor).nextTurn(nextPlayerTurn).build();
		}

		public GameOverMessage createGameOverMessage(String reason) {
			if (this.winner == null)
				return null;
			String winColor = (this.winner.getUserId().equals(blackPlayer.getUserId())) ? "BLACK" : "WHITE";
			return GameOverMessage.builder().winnerId(winner.getUserId()).winnerNickname(winner.getNickname())
					.winColor(winColor).reason(reason).build();
		}

		//
		public GameHistoryVO createGameHistory() {
			GameHistoryVO history = new GameHistoryVO();
			history.setRoomId(this.roomId);
			history.setBlackUserId(this.blackPlayer.getUserId());
			history.setWhiteUserId(this.whitePlayer.getUserId());
			if (this.winner != null)
				history.setWinnerId(this.winner.getUserId());
			history.setStartDatetime(this.startTime);
			history.setEndDatetime(new Date());
			return history;
		}

		public boolean isEmpty() {
			return participants.isEmpty();
		}

		private boolean isUserPlayer(Long userId) {
			return (player1 != null && player1.getUserId().equals(userId))
					|| (player2 != null && player2.getUserId().equals(userId));
		}

		// AI만 방에 남아있는지 확인하는 헬퍼 메서드
		public boolean isOnlyAiLeft() {
			if (participants.size() == 1) {
				UserVO lastUser = participants.values().iterator().next();
				return "B".equals(lastUser.getRole());
			}
			return false;
		}
	}

}