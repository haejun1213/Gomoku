package kr.ac.kopo.game.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SurrenderMessage extends BaseMessage {
    public SurrenderMessage() {
        setType("SURRENDER");
    }
}