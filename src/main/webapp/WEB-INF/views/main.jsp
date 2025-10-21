<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gomoku - 메인 로비</title>
    <%-- 외부 CSS 파일 참조 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css">
    <style>
    
    .profile-image-wrapper {
        width: 50px;
        height: 50px;
        border-radius: 50%; /* 컨테이너를 완벽한 원으로 만듭니다. */
        overflow: hidden; /* 원 밖으로 튀어나오는 이미지 부분을 잘라냅니다. */
        border: 4px solid #3498db;
        margin: auto; /* 위아래, 그리고 자동으로 좌우 여백을 주어 가운데 정렬 */
        box-shadow: 0 4px 8px rgba(0,0,0,0.1); /* 입체감을 위한 그림자 효과 */
    }
    
    .profile-image-wrapper img {
        width: 100%;
        height: 100%;
        object-fit: cover; /* ★ 이미지 비율을 유지하면서 원을 가득 채우는 핵심 속성 ★ */
    }
    
    </style>
</head><body>
    <div class="sidebar">
        <h2>Gomoku 메뉴</h2>
        <ul>
            <%-- 프로필 관리, 전적 조회, 복기 기능은 마이페이지로 이동 --%>
            <%-- <li><a href="${pageContext.request.contextPath}/profile">프로필 관리</a></li> --%>
            <%-- <li><a href="${pageContext.request.contextPath}/stats">전적 조회</a></li> --%>
            <%-- <li><a href="${pageContext.request.contextPath}/replay">복기 기능</a></li> --%>

            <li><a href="${pageContext.request.contextPath}/game/realtime">실시간 오목 대전</a></li>
            <li><a href="${pageContext.request.contextPath}/game/ai">AI 봇 대전</a></li>
            <li><a href="${pageContext.request.contextPath}/game/room/create">대전 방 생성</a></li>
            <li><a href="${pageContext.request.contextPath}/game/room/join">대전 방 입장</a></li>
            <%-- 실시간 채팅은 메인 화면에 직접 표시되므로 사이드바에서 제거 --%>
            <%-- <li><a href="${pageContext.request.contextPath}/chat">실시간 채팅</a></li> --%>
            <li><a href="${pageContext.request.contextPath}/mypage">마이페이지</a></li>
            

            <%-- 관리자 전용 메뉴 (역할이 'A'인 경우에만 표시) --%>
            <c:if test="${loggedInUser.role == 'A'}">
                <li style="background-color: #555; color: #f1c40f; font-weight: bold; padding-left: 10px;">관리자 메뉴</li>
                <li><a href="${pageContext.request.contextPath}/admin/users">회원 관리</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/rooms">방 관리</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/reports">신고 처리</a></li>
            </c:if>
        </ul>
    </div>

    <div class="main-content">
        <div class="header">
            <h1>환영합니다, <c:out value="${loggedInUser.nickname}" />님!</h1>
            <div class="user-info-header">
                <%-- 닉네임 클릭 시 마이페이지로 이동 (게스트가 아닐 경우에만) --%>
                <c:if test="${not empty sessionScope.loggedInUser.profileImage}">
				        <div class="profile-image-wrapper">
				            <img src="${pageContext.request.contextPath}${sessionScope.loggedInUser.profileImage}" alt="프로필 이미지">
				        </div>
				    </c:if>
                <strong>
                            <a href="${pageContext.request.contextPath}/mypage"><c:out value="${loggedInUser.nickname}" /></a>
                </strong>
                <a href="${pageContext.request.contextPath}/logout" 
   class="logout-btn" 
   onclick="return confirm('정말로 로그아웃 하시겠습니까?');">로그아웃</a>
            </div>
        </div>

        <p>Gomoku 게임을 시작할 준비가 되셨습니다. 왼쪽 메뉴에서 원하는 기능을 선택해주세요.</p>

        

        <h2 class="section-title">주요 기능</h2>
        <div class="feature-box-container">
            <div class="feature-box">
                <h3>실시간 오목 대전</h3>
                <p>다른 플레이어와 실시간으로 오목을 즐기세요. 매칭 시스템을 통해 적합한 상대를 찾을 수 있습니다.</p>
                <a href="${pageContext.request.contextPath}/game/realtime">대전 시작하기</a>
            </div>
            <div class="feature-box">
                <h3>AI 봇 대전</h3>
                <p>다양한 난이도의 AI 봇과 실력을 겨뤄보세요. 연습하거나 새로운 전략을 시험해볼 수 있습니다.</p>
                <a href="${pageContext.request.contextPath}/game/ai">봇과 대전하기</a>
            </div>
            <div class="feature-box">
                <h3>대전 방 생성 및 입장</h3>
                <p>친구들과 함께 플레이하고 싶으신가요? 나만의 방을 만들거나 친구의 방에 입장하여 함께 즐기세요.</p>
                <a href="${pageContext.request.contextPath}/game/room/create">방 만들기</a>
                <a href="${pageContext.request.contextPath}/game/room/join" style="margin-left: 10px;">방 입장하기</a>
            </div>
        </div>


        <%-- 관리자 섹션 --%>
        <c:if test="${loggedInUser.role == 'A'}">
            <div class="admin-section">
                <h2 class="section-title" style="border-bottom-color: #e67e22; color: #e67e22;">관리자 전용 기능</h2>
                <div class="admin-links">
                    <a href="${pageContext.request.contextPath}/admin/users">회원 관리</a>
                	<a href="${pageContext.request.contextPath}/admin/rooms">방 관리</a>
                    <a href="${pageContext.request.contextPath}/admin/reports">신고 처리</a>
                </div>
            </div>
        </c:if>

    </div>

</body>
</html>