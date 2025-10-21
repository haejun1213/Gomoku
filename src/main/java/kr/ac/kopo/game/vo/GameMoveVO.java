package kr.ac.kopo.game.vo;

import java.util.Date;
import lombok.Data;

@Data
public class GameMoveVO {
    private int moveId;
    private int gameId;
    private int moveOrder;
    private int xCoord;
    private int yCoord;
    private String color; // BLACK, WHITE
    private Date playedDatetime;
}