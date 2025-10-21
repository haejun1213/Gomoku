package kr.ac.kopo.game.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientChatMessage extends BaseMessage {
    private String content;
    public ClientChatMessage() { setType("CHAT"); }
}