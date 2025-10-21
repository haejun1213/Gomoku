package kr.ac.kopo.game.vo;

import java.util.Date;

import kr.ac.kopo.user.vo.UserVO;
import lombok.Data;

@Data
public class RoomParticipantVO {
    private int roomId;
    private Long userId;
    private String role; // HOST, CHALLENGER, SPECTATOR
    private Date joinedDatetime;
    private UserVO user;
}