package kr.ac.kopo.game.vo;

import java.util.Date;

import lombok.Data;

@Data
public class GameRoomVO {
    public GameRoomVO(int roomId2, String string) {
		// TODO Auto-generated constructor stub
	}
	public GameRoomVO() {
		// TODO Auto-generated constructor stub
	}
	private int roomId;
    private String title;
    private Long hostUserId;
    private String status; // WAITING, PLAYING, FINISHED
    private String inviteCode;
    private Date createdDatetime;
    private int maxParticipants;
    private String isPrivate; // "Y" 또는 "N"
}