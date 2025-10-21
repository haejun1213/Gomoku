package kr.ac.kopo.game.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStateMessage {
    private final String type = "UPDATE_STATE";
    private int x;
    private int y;
    private String color;
    private String nextTurn;
}