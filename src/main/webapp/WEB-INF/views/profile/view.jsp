<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${profileUser.nickname}님의 프로필</title>
<%-- 마이페이지와 동일한 CSS 사용 --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">
<style>
    /* 프로필 페이지 전용 스타일 */
    .btn-report { background-color: #dc3545; /* 빨간색 */ }
    .btn-report:hover { background-color: #c82333; }
</style>
</head>
<body>
    <div class="container">
        <h1>${profileUser.nickname}님의 프로필</h1>
        
        <div class="mypage-layout">
            <div class="left-column">
                <div class="profile-section">
                    <h2>기본 정보</h2>
                    <%-- 이메일, 가입일 등 민감 정보는 제거 --%>
                    <p><strong>닉네임:</strong> <c:out value="${profileUser.nickname}"/></p>
                    <p><strong>역할:</strong>
                        <c:choose>
                            <c:when test="${profileUser.role == 'A'}">관리자</c:when>
                            <c:otherwise>일반</c:otherwise>
                        </c:choose>
                    </p>
                    <%-- 신고 버튼 추가 --%>
                    <button class="btn-edit-profile btn-report" onclick="reportUser(${profileUser.userId})">이 사용자 신고하기</button>
                </div>
                <div class="stats-section">
                    <h2>대전 통계</h2>
                    <%-- 마이페이지와 동일한 통계 UI --%>
                </div>
            </div>
            <div class="right-column">
                <div class="history-section">
                    <h2>최근 대전 기록</h2>
                    <%-- 마이페이지와 동일한 기록 테이블 및 페이징 UI --%>
                </div>
            </div>
        </div>
        <div style="text-align: center;">
            <a href="javascript:history.back()" class="btn-main">뒤로가기</a>
        </div>
    </div>
<script>
    function reportUser(userId) {
        // 나중에 구현할 신고 처리 페이지로 이동
        alert(`사용자 ID ${userId}님에 대한 신고 기능은 현재 준비중입니다.`);
        // location.href = '${pageContext.request.contextPath}/report/user/' + userId;
    }
</script>
</body>
</html>