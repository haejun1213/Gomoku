package kr.ac.kopo.game.dto;

import java.util.List;
import kr.ac.kopo.game.vo.GameMoveVO;
import lombok.Data;

@Data
public class GameReplayDTO {
    // GameHistoryInfoDTO를 사용하여 상대방 닉네임 등 상세 정보를 담습니다.
    private GameHistoryInfoDTO gameInfo; 
    
    // 해당 게임의 모든 착수 기록을 담습니다.
    private List<GameMoveVO> moves;
}