package kr.ac.kopo.game.dto;

import kr.ac.kopo.game.vo.GameRoomVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameRoomInfoDTO extends GameRoomVO {
    private int currentParticipants;
}