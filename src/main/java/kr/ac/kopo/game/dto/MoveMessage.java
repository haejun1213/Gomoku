package kr.ac.kopo.game.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MoveMessage extends BaseMessage {
    private int x;
    private int y;

    public MoveMessage() {
        setType("MOVE");
    }
}