package kr.ac.kopo.user.service; // 패키지 경로 변경

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; // UUID 임포트 추가
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import kr.ac.kopo.common.dto.Criteria;
import kr.ac.kopo.common.dto.PageMaker;
import kr.ac.kopo.game.dto.GameHistoryInfoDTO;
import kr.ac.kopo.game.vo.GameHistoryVO;
import kr.ac.kopo.user.dao.UserMapper; // import 경로 변경
import kr.ac.kopo.user.dto.UserStatsDTO;
import kr.ac.kopo.user.vo.UserVO; // import 경로 변경

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;


    private final String UPLOAD_PATH = "C:/gomoku_uploads/profiles/";
    @Autowired
    private RestTemplate restTemplate;
    
    private final String AI_SERVER_URL = "http://localhost:5000";

    @Transactional
    public void deleteUserAccount(Long userId, String password) throws Exception {
        // 1. 비밀번호 확인을 위해 현재 사용자 정보를 가져옴
        UserVO currentUser = userMapper.findUserById(userId);
        if (currentUser == null || !passwordEncoder.matches(password, currentUser.getPassword())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        // 2. 이 유저와 관련된 신고 내역을 먼저 삭제 (참조 무결성)
        userMapper.deleteUserReports(userId);
        
        // 3. DB의 ON DELETE 옵션에 따라, USER가 삭제되면
        //    - ROOM_PARTICIPANTS의 기록은 자동으로 삭제되고 (CASCADE)
        //    - GAME_HISTORY의 기록은 user_id가 NULL로 변경됩니다 (SET NULL)

        // 4. 최종적으로 USERS 테이블에서 해당 유저를 삭제
        userMapper.deleteUser(userId);
    }
    
    @Transactional
    public void registerUserFace(Long userId, String imageDataUrl) throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("imageDataUrl", imageDataUrl);
        // Python 서버에 인코딩 요청
        Map<String, Object> response = restTemplate.postForObject(AI_SERVER_URL + "/encode-face", requestData, Map.class);

        if (response != null && (Boolean) response.get("success")) {
            List<Double> encodingList = (List<Double>) response.get("encoding");
            // 인코딩 숫자 리스트를 콤마로 구분된 긴 문자열로 변환
            String encodingString = encodingList.stream().map(String::valueOf).collect(Collectors.joining(","));
            
            // DB의 FACE_ENCODING 컬럼에 저장
            userMapper.updateFaceEncoding(userId, encodingString);
        } else {
            throw new Exception(response != null ? (String)response.get("error") : "얼굴 인코딩 실패");
        }
    }

    @Transactional
    public UserVO loginWithFace(String email, String targetImageDataUrl) throws Exception {
        UserVO user = userMapper.findUserByEmail(email);
        if (user == null || user.getFaceEncoding() == null || user.getFaceEncoding().isEmpty()) {
            throw new Exception("얼굴 정보가 등록되지 않았거나 존재하지 않는 이메일입니다.");
        }
        
        // DB에서 가져온 인코딩 문자열을 다시 숫자 리스트로 변환
        List<Double> knownEncoding = Arrays.stream(user.getFaceEncoding().split(","))
                                           .map(Double::valueOf).collect(Collectors.toList());
        
        // Python 서버에 비교 요청
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("knownEncoding", knownEncoding);
        requestData.put("targetImageDataUrl", targetImageDataUrl);
        
        Map<String, Object> response = restTemplate.postForObject(AI_SERVER_URL + "/verify-face", requestData, Map.class);

        if (response != null && (Boolean) response.getOrDefault("verified", false)) {
            userMapper.updateLastLoginDatetime(user.getUserId());
            return user;
        } else {
            String errorMessage = "얼굴이 일치하지 않습니다.";
            if (response != null && response.containsKey("error")) {
                errorMessage = (String) response.get("error");
            }
            throw new Exception(errorMessage);
        }
    }
	// 사용자 등록 (전통적인 방식)
	@Transactional
	public void registerUser(UserVO user) throws Exception {
		// 이메일 중복 확인
		if (userMapper.findUserByEmail(user.getEmail()) != null) {
			throw new Exception("이미 존재하는 이메일입니다.");
		}
		// 닉네임 중복 확인
		if (userMapper.findUserByNickname(user.getNickname()) != null) {
			throw new Exception("이미 사용 중인 닉네임입니다.");
		}

		// 비밀번호 해싱 [1]
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setIsSocialLogin("N");
		user.setRole("C"); // 일반 사용자
		user.setIsActive("Y");
		user.setJoinedDate(new Date());
		userMapper.insertUser(user);
	}

	// 사용자 로그인 (전통적인 방식)
	@Transactional
	public UserVO loginUser(String email, String plaintextPassword) throws Exception {
		UserVO user = userMapper.findUserByEmail(email);
		if (user == null || !passwordEncoder.matches(plaintextPassword, user.getPassword())) {
            // 2. 동일한 에러 메시지를 발생시킴
            throw new Exception("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
		
		if (!"Y".equals(user.getIsActive())) {
            throw new Exception("정지된 계정입니다. 관리자에게 문의하세요.");
        }

		// 마지막 로그인 시간 업데이트
		userMapper.updateLastLoginDatetime(user.getUserId());
		user.setLastLoginDatetime(new Date()); // 객체에도 업데이트
		return user;
	}

	// 소셜 로그인 처리 (카카오) - 닉네임 생성 로직 수정
	@Transactional
    public UserVO processSocialLogin(String socialType, String socialId, String nickname, String email, String profileImage) throws Exception {
    	UserVO existingUser = userMapper.findUserBySocialId(socialType, socialId);

        if (existingUser!= null) {
            // 이미 존재하는 소셜 사용자: 로그인 처리
        	if (!"Y".equals(existingUser.getIsActive())) {
                throw new Exception("정지된 계정입니다. 관리자에게 문의하세요.");
            }
        	
            userMapper.updateLastLoginDatetime(existingUser.getUserId());
            existingUser.setLastLoginDatetime(new Date());
            return existingUser;
        } else {
            // 새로운 소셜 사용자: 회원가입 처리
        	UserVO newUser = new UserVO();
            newUser.setIsSocialLogin("Y");
            newUser.setSocialType(socialType);
            newUser.setSocialId(socialId);
            newUser.setProfileImage(profileImage);
            newUser.setRole("C"); // 일반 사용자
            newUser.setIsActive("Y");
            newUser.setJoinedDate(new Date());

            // --- 닉네임 처리 수정 부분 ---
            String proposedNickname;

            if (nickname != null && !nickname.isEmpty()) {
                // 카카오에서 받은 닉네임이 있으면 그것을 제안 닉네임으로 사용
                proposedNickname = nickname;
            } else {
                // 카카오에서 닉네임을 받지 못했다면 (동의 안 함 등), "Kakao_" + UUID의 일부로 초기 닉네임 생성
                proposedNickname = "Kakao_" + UUID.randomUUID().toString().substring(0, 8);
            }

            String finalNickname = proposedNickname;
            int counter = 0;
            // DB에서 닉네임 중복 확인 (이미 존재하는지)
            while (userMapper.findUserByNickname(finalNickname) != null) {
                counter++;
                finalNickname = proposedNickname + "_" + counter; // 중복 시 숫자 추가
            }
            newUser.setNickname(finalNickname);
            // --- 닉네임 처리 수정 끝 ---

            // 이메일 처리: 카카오에서 이메일 동의를 받았다면 사용, 아니면 null
            newUser.setEmail(email); // 이메일이 null일 수 있음

            // 소셜 로그인 사용자는 비밀번호가 없음
            newUser.setPassword(null);
            System.out.println(newUser);
            userMapper.insertUser(newUser);
            UserVO userVO = userMapper.findUserBySocialId(socialType, socialId);
            return userVO;
        }
    }

	// 게스트 사용자 생성
	@Transactional
    public UserVO createGuestUser() throws Exception {
        UserVO guestUser = new UserVO();
        guestUser.setRole("G");
        guestUser.setIsSocialLogin("N");
        guestUser.setIsActive("Y");
        guestUser.setJoinedDate(new Date());
        guestUser.setLastLoginDatetime(new Date());

        // 고유한 게스트 닉네임 생성
        String baseNickname = "Guest";
        String uniqueNickname;
        do {
            uniqueNickname = baseNickname + "_" + UUID.randomUUID().toString().substring(0, 8);
        } while (userMapper.findUserByNickname(uniqueNickname) != null);
        
        guestUser.setNickname(uniqueNickname);
        guestUser.setEmail(null);
        guestUser.setPassword(null);

        // ★★★ 핵심 수정 ★★★
        // 이전에 적용한 <selectKey> 또는 useGeneratedKeys 덕분에,
        // 이 메서드가 실행된 직후 guestUser 객체에는 DB가 생성한 userId가 담겨있습니다.
        // 따라서 별도로 다시 조회할 필요가 없습니다.
        userMapper.insertUser(guestUser);
        UserVO guestUserVO = userMapper.findUserByNickname(uniqueNickname);
        return guestUserVO; // userId가 포함된 객체를 바로 반환
    }

	@Transactional
    public UserVO findGuestByNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return null;
        }
        UserVO user = userMapper.findUserByNickname(nickname);
        if (user != null) {
            // 마지막 로그인 시간 업데이트
            userMapper.updateLastLoginDatetime(user.getUserId());
        }
        return user;
    }
	
	
    public UserStatsDTO getUserStats(Long userId) {
        // ★ 1. 모든 기록을 가져오기 위한 Criteria 객체 생성 ★
        // 한 페이지에 매우 큰 값을 주어 사실상 모든 데이터를 한 번에 조회합니다.
        Criteria allRecordsCriteria = new Criteria();
        allRecordsCriteria.setPage(1);
        allRecordsCriteria.setPerPageNum(Integer.MAX_VALUE); // 한 번에 모든 데이터를 가져오도록 설정

        // ★ 2. 변경된 메서드 시그니처에 맞게 호출하고, 반환 타입을 DTO로 변경 ★
        // GameHistoryInfoDTO는 GameHistoryVO를 상속받았으므로, List<GameHistoryVO>로 받아도 됩니다.
        List<GameHistoryVO> histories = userMapper.findGameHistoryListByUserIdWithPaging(userId, allRecordsCriteria)
                                                  .stream()
                                                  .collect(Collectors.toList());
        
        // 아래 계산 로직은 수정할 필요 없이 그대로 동작합니다.
        int totalGames = histories.size();
        int wins = 0;
        int draws = 0;

        for (GameHistoryVO history : histories) {
            if (history.getWinnerId() != null && history.getWinnerId().equals(userId)) {
                wins++;
            } else if (history.getWinnerId() == null) {
                draws++;
            }
        }

        int losses = totalGames - wins - draws;
        double winRate = (totalGames > 0) ? ((double) wins / totalGames) * 100 : 0;

        UserStatsDTO stats = new UserStatsDTO();
        stats.setTotalGames(totalGames);
        stats.setWins(wins);
        stats.setLosses(losses);
        stats.setDraws(draws);
        stats.setWinRate(winRate);
        
        
        return stats;
    }
    
    /**
     * 사용자의 대전 기록 목록을 가공하여 가져오는 메서드
     */

    
    public Map<String, Object> getGameHistoryWithPaging(Long userId, Criteria cri) {
        Map<String, Object> resultMap = new HashMap<>();
        
        // 1. 페이지 정보를 만들기 위해 전체 기록 수를 가져옴
        PageMaker pageMaker = new PageMaker();
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(userMapper.countGameHistories(userId));

        // 2. 현재 페이지에 해당하는 기록 목록만 가져옴
        List<GameHistoryInfoDTO> historyList = userMapper.findGameHistoryListByUserIdWithPaging(userId, cri);
        
        resultMap.put("historyList", historyList);
        resultMap.put("pageMaker", pageMaker);
        
        return resultMap;
    }
    
    @Transactional
    public String updateProfile(UserVO loginUser, String newNickname, String currentPassword, 
                              String newPassword, String confirmNewPassword, MultipartFile profileImageFile) throws Exception {
        
        // ★ 1. 일반 로그인 유저일 경우에만 현재 비밀번호 검증 ★
        if ("N".equals(loginUser.getIsSocialLogin())) {
            if (currentPassword == null || currentPassword.isEmpty()) {
                throw new Exception("정보를 변경하려면 현재 비밀번호를 입력해야 합니다.");
            }
            UserVO currentUserFromDb = userMapper.findUserById(loginUser.getUserId());
            if (currentUserFromDb == null || !passwordEncoder.matches(currentPassword, currentUserFromDb.getPassword())) {
                throw new Exception("현재 비밀번호가 일치하지 않습니다.");
            }
        }
        
        // 2. 닉네임 변경 처리 (기존과 동일)
        if (newNickname != null && !newNickname.isEmpty() && !newNickname.equals(loginUser.getNickname())) {
            if (userMapper.findUserByNickname(newNickname) != null) {
                throw new Exception("이미 사용 중인 닉네임입니다.");
            }
            userMapper.updateNickname(loginUser.getUserId(), newNickname);
        }

        // 3. 비밀번호 변경 처리 (일반 회원만 가능)
        if ("N".equals(loginUser.getIsSocialLogin()) && newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmNewPassword)) {
                throw new Exception("새 비밀번호와 확인이 일치하지 않습니다.");
            }
            // 비밀번호 정규식 검증 추가 가능
            String hashedNewPassword = passwordEncoder.encode(newPassword);
            userMapper.updatePassword(loginUser.getUserId(), hashedNewPassword);
        }

        // 4. 프로필 사진 변경 처리 (기존과 동일)
        String newProfileImageUrl = null;
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            String originalFileName = profileImageFile.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String savedFileName = UUID.randomUUID().toString() + fileExtension;
            
            File saveFile = new File(UPLOAD_PATH, savedFileName);
            saveFile.getParentFile().mkdirs(); // 폴더가 없으면 생성
            profileImageFile.transferTo(saveFile);

            newProfileImageUrl = "/profile-images/" + savedFileName;
            userMapper.updateProfileImage(loginUser.getUserId(), newProfileImageUrl);
        }
        
        return newProfileImageUrl;
    }
    /**
     * 닉네임 변경 (중복 확인 포함)
     */
    public void updateUserNickname(Long userId, String newNickname) throws Exception {
        if (isNicknameDuplicated(newNickname)) {
            throw new Exception("이미 사용 중인 닉네임입니다.");
        }
        userMapper.updateNickname(userId, newNickname);
    }
	
	
	// 이메일 중복 확인 (AJAX용)
	public boolean isEmailDuplicated(String email) {
		return userMapper.findUserByEmail(email) != null;
	}

	// 닉네임 중복 확인 (AJAX용)
	public boolean isNicknameDuplicated(String nickname) {
		return userMapper.findUserByNickname(nickname) != null;
	}
	
    public UserVO findAiBot() {
        return userMapper.findAiBot();
    }

	public UserVO findUserById(Long userId) {
		return userMapper.findUserById(userId);
	}
}