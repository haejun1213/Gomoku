package kr.ac.kopo.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.game.dto.GameRoomInfoDTO;
import kr.ac.kopo.game.service.GameService;
import kr.ac.kopo.game.vo.GameRoomVO;
import kr.ac.kopo.game.vo.RoomParticipantVO;
import kr.ac.kopo.user.service.UserService;
import kr.ac.kopo.user.vo.UserVO;

@Controller
@RequestMapping("/game")
public class GameController {

	@Autowired
	private GameService gameService;

    @Autowired
    private UserService userService;
	// "실시간 오목 대전" 메뉴 클릭 시
	@GetMapping("/realtime")
	public String realtimeMatch(HttpSession session) {
		UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
		if (loginUser == null) {
			return "redirect:/login"; // 로그인 페이지로
		}

		// 서비스 로직을 통해 방을 찾거나 생성합니다.
		GameRoomVO room = gameService.matchOrCreateRoom(loginUser);

		// 올바른 게임 방 URL로 리다이렉트합니다.
		return "redirect:/game/play/" + room.getRoomId();
	}

	// "대전 방 생성" 메뉴 클릭 시 (폼을 보여주는 역할)
	@GetMapping("/room/create")
	public String createRoomForm() {
		return "game/createRoomForm"; // 방 제목 등을 입력할 폼 JSP (필요시 생성)
	}

	// 방 생성 폼 제출 시
	@PostMapping("/room/create")
	public String createRoom(HttpSession session, @RequestParam("title") String title,
			@RequestParam("maxParticipants") int maxParticipants, @RequestParam("isPrivate") String isPrivate) {

		UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 서비스 호출 시 새로운 파라미터 전달
		GameRoomVO room = gameService.createGameRoom(loginUser, title, maxParticipants, isPrivate);
		System.out.println(room);
		return "redirect:/game/play/" + room.getRoomId();
	}

	// 실제 오목 게임을 둘 화면
	@GetMapping("/play/{roomId}")
    public String gamePlay(@PathVariable("roomId") int roomId, Model model, HttpServletRequest request,
                           HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
        if (loginUser == null) {
            String destination = request.getRequestURI();
            session.setAttribute("destination", destination);
            return "redirect:/login";
        }

        GameRoomVO room = gameService.getRoomDetails(roomId);
        if (room == null) {
            return "redirect:/main";
        }
        
        // ★★★ AI 대전방인지 확인하는 로직 추가 ★★★
        List<RoomParticipantVO> participants = gameService.getRoomParticipants(roomId);
        boolean isAiMatch = participants.stream()
                                        .anyMatch(p -> "B".equals(p.getUser().getRole()));
        
        model.addAttribute("room", room);
        model.addAttribute("isAiMatch", isAiMatch); // 결과를 모델에 담아 JSP로 전달

        final String BASE_URL = "https://172.31.57.22:8443"; // 예시 IP 주소
        
        // 2. contextPath와 roomId를 조합하여 완전한 URL을 만듭니다.
        String contextPath = request.getContextPath(); // "/Gomoku"
        String fullInvitationUrl = BASE_URL + contextPath + "/game/play/" + roomId;
        
        // 3. 모델에 'invitationUrl'이라는 이름으로 완성된 URL을 담아 JSP로 전달합니다.
        model.addAttribute("invitationUrl", fullInvitationUrl);
        
        // 서버 로그에 찍어서 값이 올바른지 확인
        System.out.println("생성된 초대 URL: " + fullInvitationUrl);
        
        return "game/play";
    }

	@GetMapping("/room/join")
	public String roomList(Model model) {
		List<GameRoomInfoDTO> roomList = gameService.getJoinableRooms();
		model.addAttribute("roomList", roomList);
		return "game/roomList"; // /WEB-INF/views/game/roomList.jsp 렌더링
	}
	
	@GetMapping("/ai")
    public String startAiMatch(HttpSession session, Model model) {
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        try {
            UserVO aiBot = userService.findAiBot();
            if (aiBot == null) {
                throw new Exception("AI 봇 유저가 DB에 없습니다. USER_ID=0으로 생성해주세요.");
            }
            GameRoomVO aiRoom = gameService.createAiGameRoom(loginUser, aiBot);
            return "redirect:/game/play/" + aiRoom.getRoomId();
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "errorPage"; // 에러 페이지로 이동
        }
    }

}