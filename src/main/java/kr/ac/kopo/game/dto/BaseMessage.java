package kr.ac.kopo.game.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MoveMessage.class, name = "MOVE"),
    @JsonSubTypes.Type(value = ReadyMessage.class, name = "READY"),
    @JsonSubTypes.Type(value = ClientChatMessage.class, name = "CHAT"),
    @JsonSubTypes.Type(value = JoinAsPlayerMessage.class, name = "JOIN_AS_PLAYER"),
    @JsonSubTypes.Type(value = LeaveAsPlayerMessage.class, name = "LEAVE_AS_PLAYER"),
    @JsonSubTypes.Type(value = SurrenderMessage.class, name = "SURRENDER") // 이 부분 추가

})
public abstract class BaseMessage {
    private String type;
}