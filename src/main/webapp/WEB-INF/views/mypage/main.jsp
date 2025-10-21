<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gomoku - 마이페이지</title>
<style>
    /* 전체 페이지 레이아웃 */
    body {
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        background-color: #f4f4f9;
        color: #333;
        margin: 0;
        padding: 40px;
        display: flex;
        justify-content: center;
    }
    .container {
        width: 100%;
        max-width: 1100px; /* 너비 확장 */
        background: white;
        padding: 30px 40px;
        border-radius: 8px;
        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
    }
    a {
        color: #007bff;
        text-decoration: none;
    }
    a:hover {
        text-decoration: underline;
    }

    /* 제목 스타일 */
    h1, h2 {
        color: #2980b9;
        border-bottom: 2px solid #3498db;
        padding-bottom: 10px;
        margin-top: 0; /* 첫 h2의 상단 마진 제거 */
        margin-bottom: 20px;
    }
    h1 {
        text-align: center;
        font-size: 2.5em;
        margin-bottom: 40px;
    }
    h2 {
        font-size: 1.8em;
    }
    
    /* 2단 레이아웃 스타일 */
    .mypage-layout {
        display: flex;
        flex-wrap: wrap; /* 창이 좁아지면 아래로 떨어지도록 */
        gap: 40px; /* 컬럼 사이 간격 */
    }
    .left-column {
        flex: 1; /* 비율 1 */
        min-width: 300px; /* 최소 너비 */
    }
    .right-column {
        flex: 2; /* 비율 2 */
    }

    /* 섹션 스타일 */
    .profile-section p {
        font-size: 1.1em;
        line-height: 1.6;
    }
    .profile-section strong {
        display: inline-block;
        min-width: 80px;
        color: #555;
    }
    .btn-edit-profile {
        display: inline-block;
        margin-top: 10px;
        padding: 8px 15px;
        background-color: #3498db;
        color: white;
        border-radius: 5px;
        font-weight: bold;
    }
    
    .btn-edit-profile:hover{
    background-color: #2980b9;
    }

    .stats-summary {
        background-color: #e9f5ff;
        border-left: 5px solid #3498db;
        padding: 20px;
        margin: 30px 0 0 0;
        font-size: 1.2em;
        font-weight: 500;
    }

    /* 대전 기록 테이블 스타일 */
    .history-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }
    .history-table th, .history-table td {
        padding: 12px 15px;
        border-bottom: 1px solid #ddd;
        text-align: center;
    }
    .history-table th {
        background-color: #f2f2f2;
        font-weight: bold;
    }
    .history-table tbody tr:hover {
        background-color: #f9f9f9;
    }
    .history-table td:nth-child(2) { text-align: left; }
    .history-table .result-win { color: #007bff; font-weight: bold; }
    .history-table .result-loss { color: #dc3545; font-weight: bold; }
    .history-table .result-draw { color: #6c757d; font-weight: bold; }
    .history-table .btn-replay {
        padding: 5px 10px;
        background-color: #6c757d;
        color: white;
        border-radius: 4px;
        font-size: 0.9em;
    }
    .empty-history {
        text-align: center;
        padding: 40px;
        color: #888;
        font-style: italic;
    }

    /* 페이징 컨트롤 스타일 */
    .pagination { display: flex; justify-content: center; margin-top: 30px; list-style: none; padding: 0;}
    .pagination li a { padding: 8px 14px; margin: 0 4px; border: 1px solid #ddd; text-decoration: none; color: #007bff; border-radius: 4px; transition: background-color 0.2s; }
    .pagination li a:hover { background-color: #f0f0f0; }
    .pagination li.active a { background-color: #007bff; color: white; border-color: #007bff; }
    .pagination li.disabled a { color: #ccc; pointer-events: none; }

    /* 메인으로 돌아가기 버튼 */
    .btn-main { display: inline-block; padding: 10px 20px; margin-top: 40px; background-color: #6c757d; color: white; text-align: center; text-decoration: none; border: none; border-radius: 5px; font-size: 1em; font-weight: bold; cursor: pointer; transition: background-color 0.2s ease-in-out; }
    .btn-main:hover { background-color: #5a6268; }


	.profile-image-wrapper {
        width: 150px;
        height: 150px;
        border-radius: 50%; /* 컨테이너를 완벽한 원으로 만듭니다. */
        overflow: hidden; /* 원 밖으로 튀어나오는 이미지 부분을 잘라냅니다. */
        border: 4px solid #3498db;
        margin: 10px auto 20px auto; /* 위아래, 그리고 자동으로 좌우 여백을 주어 가운데 정렬 */
        box-shadow: 0 4px 8px rgba(0,0,0,0.1); /* 입체감을 위한 그림자 효과 */
    }

    /* 컨테이너 안의 img 태그 스타일 */
    .profile-image-wrapper img {
        width: 100%;
        height: 100%;
        object-fit: cover; /* ★ 이미지 비율을 유지하면서 원을 가득 채우는 핵심 속성 ★ */
    }
    
    .face-reg-section { margin-top: 40px; }
    .face-reg-area { text-align:center; }
    .face-reg-area video { border: 1px solid #ddd; margin-top: 10px; border-radius: 4px; background-color: #000; }
    .face-reg-area button { 
        margin-top: 10px; 
        padding: 8px 15px; 
        cursor: pointer; 
        border: 1px solid #ccc; 
        border-radius: 4px;
        font-weight: bold;
        transition: background-color 0.2s;
    }
    #start-camera {
        background-color: #007bff;
        color: white;
        border-color: #007bff;
    }
    #start-camera:hover {
        background-color: #0056b3;
    }
    #click-photo {
        background-color: #28a745;
        color: white;
        border-color: #28a745;
    }
    #click-photo:hover {
        background-color: #218838;
    }
    .btn-delete-account {
    display: inline-block;
    padding: 8px 15px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 0.9em;
    font-weight: bold;
    text-decoration: none;
    transition: background-color 0.2s ease-in-out;
    
    /* 핵심 스타일: 빨간색 계열 */
    background-color: #dc3545; /* 부트스트랩의 'danger' 색상 */
    color: white;
}

.btn-delete-account:hover {
    background-color: #c82333; /* 마우스를 올렸을 때 살짝 어두운 빨간색 */
}
</style>
</head>
<body>
    <div class="container">
        <h1>마이페이지</h1>
        
        <div class="mypage-layout">
            
            <div class="left-column">
                <div class="profile-section">
                    <h2>내 정보</h2>
                    <c:if test="${not empty sessionScope.loggedInUser.profileImage}">
				        <div class="profile-image-wrapper">
				            <img src="${pageContext.request.contextPath}${sessionScope.loggedInUser.profileImage}" alt="프로필 이미지">
				        </div>
				    </c:if>
                    <p><strong>닉네임:</strong> <c:out value="${sessionScope.loggedInUser.nickname}"/></p>
                    <p><strong>이메일:</strong>
					    <c:choose>
					        <%-- 1. 이메일이 있으면, 이메일을 그대로 표시합니다. --%>
					        <c:when test="${not empty sessionScope.loggedInUser.email}">
					            <c:out value="${sessionScope.loggedInUser.email}"/>
					        </c:when>
					        
					        <%-- 2. 이메일이 없고 역할(ROLE)이 'G'(Guest)이면, '게스트 계정'으로 표시합니다. --%>
					        <c:when test="${sessionScope.loggedInUser.role == 'G'}">
					            게스트 계정
					        </c:when>
					        
					        <%-- 3. 이메일이 없고 소셜 로그인('Y')이면, 소셜 로그인 정보와 함께 표시합니다. --%>
					        <c:when test="${sessionScope.loggedInUser.isSocialLogin == 'Y'}">
					            소셜 로그인 (<c:out value="${sessionScope.loggedInUser.socialType}"/>)
					        </c:when>
					        
					        <%-- 4. 위 모든 조건에 해당하지 않는 경우 --%>
					        <c:otherwise>
					            정보 없음
					        </c:otherwise>
					    </c:choose>
					</p>
                    <p><strong>가입일:</strong> <fmt:formatDate value="${sessionScope.loggedInUser.joinedDate}" pattern="yyyy년 MM월 dd일"/></p>
                    <c:if test="${sessionScope.loggedInUser.role != 'G'}">
                    	<a href="${pageContext.request.contextPath}/mypage/edit" class="btn-edit-profile">프로필 수정</a>
                    	
                    	<c:if test="${sessionScope.loggedInUser.isSocialLogin == 'N'}">
                                <button class="btn-delete-account" onclick="confirmDeleteAccount()">계정 탈퇴</button>
                            </c:if>
                    </c:if>
                    
                </div>

                <div class="stats-section">
                    <h2>대전 통계</h2>
                    <div class="stats-summary">
                        총 ${stats.totalGames}전 ${stats.wins}승 ${stats.losses}패 ${stats.draws}무 
                        <br>
                        (승률: <fmt:formatNumber value="${stats.winRate}" pattern="#.00"/>%)
                    </div>
                </div>
                <c:if test="${sessionScope.loggedInUser.role == 'C' and sessionScope.loggedInUser.isSocialLogin == 'N'}">
                <div class="profile-section face-reg-section">
                    <h2>얼굴 인식 로그인 관리</h2>
                    <c:choose>
                        <c:when test="${not empty sessionScope.loggedInUser.faceEncoding}">
                            <p style="color: green; font-weight: bold;">✔️ 얼굴 정보가 등록되어 있습니다.</p>
                            <p>새로운 얼굴로 변경하시려면 아래에서 다시 등록해주세요.</p>
                        </c:when>
                        <c:otherwise>
                            <p>아직 등록된 얼굴 정보가 없습니다. 얼굴을 등록하여 간편하게 로그인하세요.</p>
                        </c:otherwise>
                    </c:choose>
                    
                    <div id="face-reg-area">
                        <video id="video" width="320" height="240" autoplay muted playsinline></video>
                        <canvas id="canvas" width="320" height="240" style="display:none;"></canvas>
                        <div>
                            <button id="start-camera">카메라 켜기</button>
                            <button id="click-photo" style="display:none;">이 얼굴로 등록하기</button>
                        </div>
                    </div>
                </div>
                </c:if>
            </div>

            <div class="right-column">
                <div class="history-section">
                    <h2>최근 대전 기록</h2>
                    <table class="history-table">
                        <thead>
                            <tr>
                                <th>게임 날짜</th>
                                <th>상대</th>
                                <th>결과</th>
                                <th>복기</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty historyList}">
                                    <c:forEach var="history" items="${historyList}">
                                    <tr>
                                        <td><fmt:formatDate value="${history.startDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
                                        <td>
                                    <%-- 내가 흑돌이면 내 닉네임에 볼드 처리 --%>
                                    <span <c:if test="${history.myColor == 'BLACK'}">style="font-weight: bold;"</c:if>>
                                        <c:out value="${history.blackPlayerNickname}"/> (흑)
                                    </span>
                                    vs
                                    <%-- 내가 백돌이면 내 닉네임에 볼드 처리 --%>
                                    <span <c:if test="${history.myColor == 'WHITE'}">style="font-weight: bold;"</c:if>>
                                        <c:out value="${history.whitePlayerNickname}"/> (백)
                                    </span>
                                </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${history.result == '승리'}"><span class="result-win">승리</span></c:when>
                                                <c:when test="${history.result == '패배'}"><span class="result-loss">패배</span></c:when>
                                                <c:otherwise><span class="result-draw">무승부</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><a href="${pageContext.request.contextPath}/replay/${history.gameId}" class="btn-replay">복기 보기</a></td>

                                    </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" class="empty-history">대전 기록이 없습니다.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>

                    <ul class="pagination">
                        <c:if test="${pageMaker.prev}">
                            <li class="page-item"><a href="?page=${pageMaker.startPage - 1}">&laquo;</a></li>
                        </c:if>

                        <c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage}" var="pageNum">
                            <li class="page-item ${pageMaker.cri.page == pageNum ? 'active' : ''}">
                                <a href="?page=${pageNum}">${pageNum}</a>
                            </li>
                        </c:forEach>

                        <c:if test="${pageMaker.next}">
                             <li class="page-item"><a href="?page=${pageMaker.endPage + 1}">&raquo;</a></li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
        
        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/main" class="btn-main">메인으로 돌아가기</a>
        </div>
    </div>
    <script>
    const video = document.getElementById('video');
    const canvas = document.getElementById('canvas');
    const startCameraBtn = document.getElementById('start-camera');
    const clickPhotoBtn = document.getElementById('click-photo');

    if (startCameraBtn) {
        startCameraBtn.addEventListener('click', async () => {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({ video: true });
                video.srcObject = stream;
                startCameraBtn.style.display = 'none';
                clickPhotoBtn.style.display = 'inline-block';
            } catch (err) {
                console.error("카메라 접근 오류:", err);
                alert('카메라를 사용할 수 없습니다. (페이지가 https 또는 localhost인지 확인하세요)');
            }
        });
    }

    if (clickPhotoBtn) {
        clickPhotoBtn.addEventListener('click', () => {
            clickPhotoBtn.disabled = true;
            clickPhotoBtn.textContent = '등록 중...';

            canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);
            const imageDataUrl = canvas.toDataURL('image/jpeg');

            fetch('${pageContext.request.contextPath}/face/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                body: `imageDataUrl=\${encodeURIComponent(imageDataUrl)}`
            })
            .then(res => res.json())
            .then(data => {
                alert(data.message);
                if (data.success) {
                    window.location.reload();
                }
            })
            .catch(err => {
                console.error('얼굴 등록 오류:', err);
                alert('얼굴 등록 중 오류가 발생했습니다.');
            })
            .finally(() => {
                clickPhotoBtn.disabled = false;
                clickPhotoBtn.textContent = '이 얼굴로 등록하기';
            });
        });
    }
    
    function confirmDeleteAccount() {
        const password = prompt("계정을 삭제하시려면 현재 비밀번호를 입력해주세요.\n이 작업은 되돌릴 수 없습니다.");
        
        // 사용자가 취소를 누르거나 아무것도 입력하지 않으면 중단
        if (password === null || password.trim() === "") {
            return;
        }

        const params = new URLSearchParams();
        params.append('password', password);

        fetch('${pageContext.request.contextPath}/mypage/delete-account', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) {
                // 성공 시 메인 페이지로 이동
                window.location.href = "${pageContext.request.contextPath}";
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('계정 삭제 처리 중 오류가 발생했습니다.');
        });
    }
</script>
</body>
</html>