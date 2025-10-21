package kr.ac.kopo.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private final String type = "CHAT";
    private Long senderId;
    private String sender;  // 보낸 사람 닉네임
    private String content; // 메시지 내용
}