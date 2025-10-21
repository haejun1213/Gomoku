package kr.ac.kopo.game.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.ac.kopo.game.dto.GameRoomInfoDTO;
import kr.ac.kopo.game.vo.GameHistoryVO;
import kr.ac.kopo.game.vo.GameMoveVO;
import kr.ac.kopo.game.vo.GameRoomVO;
import kr.ac.kopo.game.vo.RoomParticipantVO;

@Mapper
public interface GameDAO {
    // 대기 중인 방 찾기 (가장 오래된 방 1개)
    GameRoomVO findWaitingRoom();

    // 새로운 방 생성
    void createRoom(GameRoomVO room);

    // 방에 참가자 추가
    void addParticipant(RoomParticipantVO participant);
    
    // 방 정보 조회
    GameRoomVO getRoomById(int roomId);

    // 방 상태 업데이트
    void updateRoomStatus(GameRoomVO room);
    
    // 방 참가자 목록 조회
    List<RoomParticipantVO> getParticipantsByRoomId(int roomId);
    
    void insertGameHistory(GameHistoryVO gameHistory);
    
    void insertGameMoves(List<GameMoveVO> moves);
    
    List<GameRoomInfoDTO> findJoinableRooms();
    
    void deleteParticipant(@Param("roomId") int roomId, @Param("userId") Long userId);

    // 특정 방의 현재 인원수 조회
    // int countParticipantsInRoom(int roomId);
    
    // 특정 방을 삭제
    void deleteRoom(int roomId);
    
    //replay
    List<GameMoveVO> findMovesByGameId(int gameId);
    
    // 특정 방의 모든 참가자를 삭제 (방을 삭제하기 전에 실행)
    void deleteAllParticipantsByRoomId(int roomId);
}