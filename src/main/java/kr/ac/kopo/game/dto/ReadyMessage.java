package kr.ac.kopo.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // 이 import 문을 추가하세요.
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReadyMessage extends BaseMessage {
    
    // ★★★ 이 어노테이션 한 줄을 추가해주세요 ★★★
    @JsonProperty("isReady")
    private boolean isReady;

    public ReadyMessage() {
        setType("READY");
    }
}