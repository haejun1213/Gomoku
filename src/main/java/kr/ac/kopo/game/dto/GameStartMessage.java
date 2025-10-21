package kr.ac.kopo.game.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameStartMessage {
    private final String type = "GAME_START";
    private Long blackPlayerId;
    private String blackPlayerNickname;
    private Long whitePlayerId;
    private String whitePlayerNickname;
    
    private List<Map<String, Object>> participants;
    private boolean gameInProgress;
}