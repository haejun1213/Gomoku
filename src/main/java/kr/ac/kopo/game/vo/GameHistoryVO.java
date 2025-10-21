package kr.ac.kopo.game.vo;

import lombok.Data;
import java.util.Date;

@Data
public class GameHistoryVO {
    private int gameId;
    private int roomId;
    private Long blackUserId;
    private Long whiteUserId;
    private Long winnerId;
    private Date startDatetime;
    private Date endDatetime;
}