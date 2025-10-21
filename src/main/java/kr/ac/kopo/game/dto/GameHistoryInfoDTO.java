package kr.ac.kopo.game.dto;

import kr.ac.kopo.game.vo.GameHistoryVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameHistoryInfoDTO extends GameHistoryVO {
	private String blackPlayerNickname;
    private String whitePlayerNickname;
    private String myColor;
    private String result;
}