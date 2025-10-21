<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관리자 - 전체 방 목록</title>
<style>
    /* 기본 레이아웃 및 컨테이너 스타일 */
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
    p {
        text-align: center;
        margin-bottom: 30px;
        color: #555;
    }

    /* 방 목록 테이블 스타일 */
    .room-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }
    .room-table th, .room-table td {
    	padding: 8px 15px;
    	font-size: 0.95em;
        border-bottom: 1px solid #ddd;
        text-align: center;
        vertical-align: middle;
    }
    .room-table th {
        background-color: #f2f2f2;
        font-weight: bold;
    }
    .room-table tbody tr:hover {
        background-color: #f9f9f9;
    }
    .room-table td.room-title {
        text-align: left;
        max-width: 300px;
        word-break: break-all;
    }
    .room-table .status-waiting { color: #28a745; font-weight: bold; }
    .room-table .status-playing { color: #ffc107; font-weight: bold; }
    .room-table .status-finished { color: #6c757d; font-weight: bold; }
    .room-table .type-private { color: #e74c3c; font-weight: bold; }
    .room-table .type-public { color: #2980b9; font-weight: bold; }

    .room-table .btn-manage {
        padding: 5px 10px;
        border: none;
        border-radius: 4px;
        color: white;
        cursor: pointer;
        font-size: 0.9em;
    }
    .room-table .btn-delete { background-color: #dc3545; }
    .room-table .btn-delete:hover { background-color: #c82333; }
    
    /* 페이징 컨트롤 스타일 */
    .pagination { display: flex; justify-content: center; margin-top: 30px; list-style: none; padding: 0;}
    .pagination li { margin: 0 4px; }
    .pagination a { padding: 8px 14px; border: 1px solid #ddd; text-decoration: none; color: #007bff; border-radius: 4px; transition: background-color 0.2s; display: block; }
    .pagination a:hover { background-color: #f0f0f0; }
    .pagination li.active a { background-color: #007bff; color: white; border-color: #007bff; }
    
    /* 하단 버튼 스타일 */
    .btn-main { 
        display: inline-block; 
        padding: 10px 20px; 
        margin-top: 40px; 
        background-color: #6c757d; 
        color: white; 
        text-align: center; 
        text-decoration: none; 
        border: none; 
        border-radius: 5px; 
        font-size: 1em; 
        font-weight: bold; 
    }
    .btn-main:hover { background-color: #5a6268; }
    
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
</style>
</head>
<body>
    <div class="container">
        <h1>전체 방 목록</h1>
        
        <table class="room-table">
            <thead>
                <tr>
                    <th>방 ID</th>
                    <th class="room-title">방 제목</th>
                    <th>상태</th>
                    <th>인원</th>
                    <th>공개여부</th>
                    <th>생성일</th>
                    <th>관리</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty allRooms}">
                        <c:forEach var="room" items="${allRooms}">
                            <tr>
                                <td>${room.roomId}</td>
                                <td class="room-title"><c:out value="${room.title}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${room.status == 'WAITING'}"><span class="status-waiting">대기중</span></c:when>
                                        <c:when test="${room.status == 'PLAYING'}"><span class="status-playing">게임중</span></c:when>
                                        <c:otherwise><span class="status-finished">${room.status}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${room.currentParticipants} / ${room.maxParticipants}</td>
                                <td>
                                    <c:if test="${room.isPrivate == 'Y'}"><span class="type-private">비공개</span></c:if>
                                    <c:if test="${room.isPrivate == 'N'}"><span class="type-public">공개</span></c:if>
                                </td>
                                <td><fmt:formatDate value="${room.createdDatetime}" pattern="yy-MM-dd HH:mm"/></td>
                                <td>
                                    <button class="btn-manage btn-delete" onclick="deleteRoom(${room.roomId})">강제 삭제</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" style="text-align:center; padding: 50px; color: #888;">생성된 방이 없습니다.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
        
        <!-- 페이징 컨트롤 UI -->
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
        <div class="admin-links">
                    <a href="${pageContext.request.contextPath}/admin/users">회원 관리</a>
                    <a href="${pageContext.request.contextPath}/admin/reports">신고 처리</a>
                </div>
        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/main" class="btn-main">메인으로 돌아가기</a>
        </div>
    </div>

<script>
function deleteRoom(roomId) {
    if (!confirm(`정말로 방 ID ${roomId}를 삭제하시겠습니까?\n이 방에 있던 모든 참가자 및 게임 기록과의 연결이 끊어질 수 있습니다.`)) {
        return;
    }

    const params = new URLSearchParams();
    params.append('roomId', roomId);

    fetch('${pageContext.request.contextPath}/admin/rooms/delete', {
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
