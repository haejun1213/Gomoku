package kr.ac.kopo.game.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveAsPlayerMessage extends BaseMessage {
    public LeaveAsPlayerMessage() {
        setType("LEAVE_AS_PLAYER");
    }
}