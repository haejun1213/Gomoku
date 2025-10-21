package kr.ac.kopo.game.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameOverMessage {
    private final String type = "GAME_OVER";
    private Long winnerId;
    private String winnerNickname;
    private String winColor;
    private String reason;
}