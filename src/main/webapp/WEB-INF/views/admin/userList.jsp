<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관리자 - 회원 관리</title>
<style>
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
        max-width: 1200px;
        background: white;
        padding: 30px 40px;
        border-radius: 8px;
        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
    }
    h1 {
        text-align: center;
        font-size: 2.5em;
        margin-top: 0;
        margin-bottom: 40px;
        color: #d35400; /* 관리자 페이지용 포인트 색상 */
    }
    a {
        color: #007bff;
        text-decoration: none;
    }
    a:hover {
        text-decoration: underline;
    }

    /* 검색 폼 스타일 */
    .search-form {
        display: flex;
        justify-content: center;
        gap: 10px;
        margin-bottom: 30px;
    }
    .search-form select, .search-form input[type="text"] {
        padding: 8px 12px;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 1em;
    }
    .search-form input[type="text"] {
        min-width: 300px;
    }
    .search-form button {
        padding: 8px 20px;
        border: none;
        border-radius: 4px;
        background-color: #2980b9;
        color: white;
        font-size: 1em;
        cursor: pointer;
    }
    
    .admin-links {
    display: flex;
    justify-content: center;
    gap: 20px;
    flex-wrap: wrap;
}
.admin-links a {
    background-color: #e67e22;
    color: white;
    padding: 12px 25px;
    text-decoration: none;
    border-radius: 5px;
    font-size: 1.1em;
    transition: background-color 0.3s ease;
}
.admin-links a:hover {
    background-color: #d35400;
}

    /* 회원 목록 테이블 스타일 */
    .user-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }
    .user-table th, .user-table td {
        padding: 12px 15px;
        border-bottom: 1px solid #ddd;
        text-align: center;
        vertical-align: middle;
    }
    .user-table th {
        background-color: #f2f2f2;
        font-weight: bold;
    }
    .user-table tbody tr:hover {
        background-color: #f9f9f9;
    }
    .user-table td.email, .user-table td.nickname {
        text-align: left;
    }
    .user-table .guest-label {
        color: #888;
        font-style: italic;
    }
    .user-table .status-active { color: #28a745; font-weight: bold; }
    .user-table .status-inactive { color: #dc3545; font-weight: bold; }
    .user-table .role-admin { color: #e67e22; font-weight: bold; }
    .user-table .role-bot { color: #9b59b6; font-weight: bold; }
    
    /* ★★★ 관리 버튼 CSS 추가 ★★★ */
    .user-table .btn-manage {
        padding: 5px 10px;
        border: none;
        border-radius: 4px;
        color: white;
        cursor: pointer;
        font-size: 0.9em;
        transition: opacity 0.2s;
    }
    .user-table .btn-manage:hover {
        opacity: 0.8;
    }
    .user-table .btn-deactivate { background-color: #dc3545; } /* 정지 버튼 (빨간색) */
    .user-table .btn-activate { background-color: #28a745; } /* 활성화 버튼 (녹색) */
    
    /* 페이징 및 하단 버튼 스타일 */
    .pagination { display: flex; justify-content: center; margin-top: 30px; list-style: none; padding: 0; }
    .pagination li a { padding: 8px 14px; margin: 0 4px; border: 1px solid #ddd; text-decoration: none; color: #007bff; border-radius: 4px; }
    .pagination li a:hover { background-color: #f0f0f0; }
    .pagination li.active a { background-color: #007bff; color: white; border-color: #007bff; }
    .btn-main { display: inline-block; padding: 10px 20px; margin-top: 40px; background-color: #6c757d; color: white; text-align: center; text-decoration: none; border: none; border-radius: 5px; font-size: 1em; font-weight: bold; }

</style>
</head>
<body>
    <div class="container">
        <h1>회원 관리</h1>

        <form id="searchForm" action="users" method="get" class=search-form>
            <input type="hidden" name="page" value="1">
            <select name="searchType">
                <option value="nickname" ${pageMaker.cri.searchType == 'nickname' ? 'selected' : ''}>닉네임</option>
                <option value="email" ${pageMaker.cri.searchType == 'email' ? 'selected' : ''}>이메일</option>
            </select>
            <input type="text" name="keyword" value="${pageMaker.cri.keyword}" placeholder="검색어를 입력하세요">
            <button type="submit">검색</button>
        </form>

        <table class="user-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th class="email">이메일</th>
                    <th class="nickname">닉네임</th>
                    <th>가입일</th>
                    <th>역할</th>
                    <th>상태</th>
                    <th>관리</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${userList}">
                    <tr>
                        <td>${user.userId}</td>
                         <td class="email">
                            <c:choose>
                                <c:when test="${not empty user.email}">
                                    <c:out value="${user.email}"/>
                                </c:when>
                                <c:when test="${user.role == 'G'}">
                                    <span class="guest-label">게스트</span>
                                </c:when>
                                <c:when test="${user.isSocialLogin == 'Y'}">
                                    <span class="guest-label">소셜 로그인 (${user.socialType})</span>
                                </c:when>
                                <c:when test="${user.role == 'B'}">
                                    <span class="guest-label">봇</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="guest-label">정보 없음</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="nickname"><c:out value="${user.nickname}"/></td>
                        <td><fmt:formatDate value="${user.joinedDate}" pattern="yyyy-MM-dd"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${user.role == 'A'}"><span class="role-admin">관리자</span></c:when>
                                <c:when test="${user.role == 'B'}"><span class="role-bot">AI 봇</span></c:when>
                                <c:otherwise>일반</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${user.isActive == 'Y'}"><span class="status-active">활동중</span></c:if>
                            <c:if test="${user.isActive == 'N'}"><span class="status-inactive">정지됨</span></c:if>
                        </td>
                        <td>
                            <c:if test="${user.role != 'A' and user.role != 'B'}">
                                <c:if test="${user.isActive == 'Y'}">
                                    <button class="btn-manage btn-deactivate" onclick="updateStatus(${user.userId}, 'N')">정지</button>
                                </c:if>
                                <c:if test="${user.isActive == 'N'}">
                                    <button class="btn-manage btn-activate" onclick="updateStatus(${user.userId}, 'Y')">활성화</button>
                                </c:if>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <ul class="pagination">
            <c:if test="${pageMaker.prev}">
                <li><a href="users?page=${pageMaker.startPage - 1}&searchType=${pageMaker.cri.searchType}&keyword=${pageMaker.cri.keyword}">&laquo;</a></li>
            </c:if>
            <c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage}" var="pageNum">
                <li class="${pageMaker.cri.page == pageNum ? 'active' : ''}">
                    <a href="users?page=${pageNum}&searchType=${pageMaker.cri.searchType}&keyword=${pageMaker.cri.keyword}">${pageNum}</a>
                </li>
            </c:forEach>
            <c:if test="${pageMaker.next}">
                <li><a href="users?page=${pageMaker.endPage + 1}&searchType=${pageMaker.cri.searchType}&keyword=${pageMaker.cri.keyword}">&raquo;</a></li>
            </c:if>
        </ul>
        <div class="admin-links">
                	<a href="${pageContext.request.contextPath}/admin/rooms">방 관리</a>
                    <a href="${pageContext.request.contextPath}/admin/reports">신고 처리</a>
                </div>
        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/main" class="btn-main">메인으로 돌아가기</a>
        </div>
    </div>
<script>
    function updateStatus(userId, status) {
        const actionText = status === 'Y' ? '활성화' : '정지';
        if (!confirm(`사용자 ID ${userId} 계정을 정말로 ${actionText}하시겠습니까?`)) {
            return;
        }

        const params = new URLSearchParams();
        params.append('userId', userId);
        params.append('status', status);

        fetch('${pageContext.request.contextPath}/admin/users/update-status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.status === 'success') {
                window.location.reload();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생했습니다. 다시 시도해주세요.');
        });
    }
</script>
</body>
</html>