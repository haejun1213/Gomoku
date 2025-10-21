<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관리자 - 신고 목록</title>
<style>
    /* userList.jsp와 동일한 스타일을 사용합니다. */
    body { font-family: sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 40px; display: flex; justify-content: center; }
    .container { width: 100%; max-width: 1200px; background: white; padding: 30px 40px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
    h1 { text-align: center; font-size: 2.5em; margin-top: 0; margin-bottom: 40px; color: #d35400; }
    .report-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    .report-table th, .report-table td { padding: 12px 15px; border-bottom: 1px solid #ddd; text-align: center; vertical-align: middle; }
    .report-table th { background-color: #f2f2f2; font-weight: bold; }
    .report-table tbody tr:hover { background-color: #f9f9f9; }
    .report-table td.reason { text-align: left; max-width: 400px; word-break: break-all; }
    .report-table .status-pending { color: #e67e22; font-weight: bold; }
    .report-table .status-processed { color: #28a745; font-weight: bold; }
    .report-table .btn-action { padding: 5px 10px; border: none; border-radius: 4px; color: white; cursor: pointer; font-size: 0.9em; text-decoration: none; display: inline-block; margin: 2px; }
    .report-table .btn-view-user { background-color: #3498db; }
    .report-table .btn-delete { background-color: #dc3545; }
    .pagination { display: flex; justify-content: center; margin-top: 30px; list-style: none; padding: 0; }
    .pagination li a { padding: 8px 14px; margin: 0 4px; border: 1px solid #ddd; text-decoration: none; color: #007bff; border-radius: 4px; }
    .pagination li.active a { background-color: #007bff; color: white; border-color: #007bff; }
    .btn-main { display: inline-block; padding: 10px 20px; margin-top: 40px; background-color: #6c757d; color: white; text-align: center; text-decoration: none; border: none; border-radius: 5px; font-size: 1em; font-weight: bold; }
    
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
        <h1>신고 목록</h1>
        
        <table class="report-table">
            <thead>
                <tr>
                    <th>신고 ID</th>
                    <th>신고자</th>
                    <th>신고 대상</th>
                    <th class="reason">신고 사유 (채팅 내용)</th>
                    <th>신고일</th>
                    <th>관리</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty reportList}">
                        <c:forEach var="report" items="${reportList}">
                            <tr>
                                <td>${report.reportId}</td>
                                <td><c:out value="${report.reporterNickname}"/></td>
                                <td><c:out value="${report.targetNickname}"/></td>
                                <td class="reason"><c:out value="${report.reason}"/></td>
                                <td><fmt:formatDate value="${report.createdDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/users?searchType=nickname&keyword=${report.targetNickname}" class="btn-action btn-view-user">유저 보기</a>
                                    <button class="btn-action btn-delete" onclick="processReport(${report.reportId})">처리 완료 (삭제)</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" style="text-align:center; padding: 50px; color: #888;">접수된 신고가 없습니다.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>

        <!-- 페이징 컨트롤 -->
        <ul class="pagination">
            <c:if test="${pageMaker.prev}">
                <li><a href="?page=${pageMaker.startPage - 1}">&laquo;</a></li>
            </c:if>
            <c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage}" var="pageNum">
                <li class="${pageMaker.cri.page == pageNum ? 'active' : ''}">
                    <a href="?page=${pageNum}">${pageNum}</a>
                </li>
            </c:forEach>
            <c:if test="${pageMaker.next}">
                <li><a href="?page=${pageMaker.endPage + 1}">&raquo;</a></li>
            </c:if>
        </ul>
        <div class="admin-links">
                    <a href="${pageContext.request.contextPath}/admin/users">회원 관리</a>
                	<a href="${pageContext.request.contextPath}/admin/rooms">방 관리</a>
                </div>
        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/main" class="btn-main">메인으로 돌아가기</a>
        </div>
    </div>
<script>
    function processReport(reportId) {
        if (!confirm(`신고 ID ${reportId} 내역을 정말로 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) {
            return;
        }

        const params = new URLSearchParams();
        params.append('reportId', reportId);

        fetch('${pageContext.request.contextPath}/admin/reports/delete', {
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
                window.location.reload(); // 성공 시 페이지 새로고침
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
