package kr.ac.kopo.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.ObjectMapper; // ObjectMapper 임포트

import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.game.dto.GameReplayDTO;
import kr.ac.kopo.game.service.GameService;
import kr.ac.kopo.user.vo.UserVO;

@Controller
public class ReplayController {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private ObjectMapper objectMapper; // JSON 변환을 위해 주입받거나, new ObjectMapper()로 생성

    @GetMapping("/replay/{gameId}")
    public String replayView(@PathVariable("gameId") int gameId, HttpSession session, Model model) {
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        GameReplayDTO replayData = gameService.getGameReplayData(loginUser.getUserId(), gameId);
        
        if (replayData == null) {
            return "redirect:/mypage";
        }
        
        try {
            // ★★★ 핵심 수정: moves 리스트를 JSON 문자열로 변환 ★★★
            String movesJson = objectMapper.writeValueAsString(replayData.getMoves());
            model.addAttribute("movesJson", movesJson);
            // ★★★ 수정 끝 ★★★

        } catch (Exception e) {
            e.printStackTrace();
            // JSON 변환 실패 시 에러 처리
            model.addAttribute("errorMessage", "기보 데이터를 불러오는 중 오류가 발생했습니다.");
            return "errorPage";
        }
        
        model.addAttribute("replayData", replayData);
        return "replay/view";
    }
}