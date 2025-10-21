package kr.ac.kopo.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

@Service
public class AIService {

    @Autowired
    private RestTemplate restTemplate;

    // 172.31.57.22아니고  로컬인데 왜데지
    private final String AI_SERVER_URL = "http://localhost:5000/predict";

    // int[][] 배열을 List<List<Integer>>로 변환하는 메서드
    private List<List<Integer>> convertBoardToList(int[][] board) {
        List<List<Integer>> list = new ArrayList<>();
        for (int y = 0; y < board.length; y++) {
            List<Integer> row = new ArrayList<>();
            for (int x = 0; x < board[y].length; x++) {
                row.add(board[y][x]);
            }
            list.add(row);
        }
        return list;
    }

    public Point calculateNextMove(int[][] board, int aiPlayer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("board", convertBoardToList(board));  // 변환 후 전송
        requestData.put("aiPlayer", aiPlayer);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

        try {
            // Python API 서버에 POST 요청
            Map<String, Integer> response = restTemplate.postForObject(AI_SERVER_URL, request, Map.class);
            System.out.println("AI 서버 응답: " + response);
            
            if (response != null && response.containsKey("x") && response.containsKey("y")) {
                return new Point(response.get("x"), response.get("y"));
            }
        } catch (Exception e) {
            System.err.println("AI 서버 연결 실패: " + e.getMessage());
            // 비상시 랜덤하게 두는 로직으로 대체
            return calculateRandomMove(board);
        }
        
        return calculateRandomMove(board);
    }
    
    private Point calculateRandomMove(int[][] board) {
        List<Point> emptyCells = new ArrayList<>();
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (board[y][x] == 0) {
                    emptyCells.add(new Point(x, y));
                }
            }
        }
        return emptyCells.isEmpty() ? null : emptyCells.get(new Random().nextInt(emptyCells.size()));
    }
}
