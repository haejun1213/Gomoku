package kr.ac.kopo.game.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JoinAsPlayerMessage extends BaseMessage {
    public JoinAsPlayerMessage() {
        setType("JOIN_AS_PLAYER");
    }
}