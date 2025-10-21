package kr.ac.kopo.user.dao; // 패키지 경로 변경

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.ac.kopo.common.dto.Criteria;
import kr.ac.kopo.game.dto.GameHistoryInfoDTO;
import kr.ac.kopo.game.vo.GameHistoryVO;
import kr.ac.kopo.user.vo.UserVO; // import 경로 변경

// @Mapper 어노테이션은 Spring Boot에서 주로 사용되지만,
// XML 설정에서는 MapperScannerConfigurer가 스캔하므로 필수는 아님.
// 명시적으로 표시하여 가독성 향상.
@Mapper
public interface UserMapper {
	void insertUser(UserVO user);

	UserVO findUserByEmail(String email);

	UserVO findUserByNickname(String nickname);

	UserVO findUserBySocialId(@Param("socialType") String socialType, @Param("socialId") String socialId);

	void updateLastLoginDatetime(Long userId);

	void updateUser(UserVO user); // 프로필 업데이트 등 일반적인 사용자 정보 업데이트
	// 특정 유저의 모든 게임 기록 조회

	List<GameHistoryVO> findAllGameHistoriesByUserId(Long userId);

	// 닉네임 변경
	void updateNickname(@Param("userId") Long userId, @Param("nickname") String nickname);

	// 비밀번호 변경
	void updatePassword(@Param("userId") Long userId, @Param("password") String password);

	// 프로필 이미지 변경
	void updateProfileImage(@Param("userId") Long userId, @Param("profileImage") String profileImage);

	UserVO findUserById(Long opponentId);

	List<GameHistoryInfoDTO> findGameHistoryListByUserIdWithPaging(@Param("userId") Long userId,
			@Param("cri") Criteria cri);

	// ★ 전체 기록 수를 세는 메서드 추가 ★
	int countGameHistories(Long userId);

	UserVO findAiBot();

	GameHistoryInfoDTO findGameHistoryByGameId(@Param("userId") Long userId, @Param("gameId") int gameId);

	void updateFaceEncoding(@Param("userId") Long userId, @Param("encoding") String encoding);

	void deleteUserReports(Long userId);

	// 특정 유저를 삭제
	void deleteUser(Long userId);
}