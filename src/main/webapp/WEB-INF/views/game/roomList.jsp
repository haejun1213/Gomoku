<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gomoku - 방 목록</title>
<style>
body {
	font-family: sans-serif;
	background-color: #f4f4f9;
	color: #333;
}

.container {
	max-width: 800px;
	margin: 50px auto;
	padding: 20px;
	background: white;
	border-radius: 8px;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h1 {
	text-align: center;
	margin-bottom: 30px;
}

.room-table {
	width: 100%;
	border-collapse: collapse;
}

.room-table th, .room-table td {
	padding: 12px 15px;
	border-bottom: 1px solid #ddd;
	text-align: center;
}

.room-table th {
	background-color: #f8f8f8;
}

.room-table tr:hover {
	background-color: #f1f1f1;
}

.room-title {
	text-align: left;
}

.status-waiting {
	color: #28a745;
	font-weight: bold;
}

.btn {
	padding: 6px 12px;
	color: white;
	background-color: #007bff;
	border: none;
	border-radius: 4px;
	text-decoration: none;
	cursor: pointer;
}

.btn:hover {
	background-color: #0056b3;
}

.no-rooms {
	text-align: center;
	color: #777;
	padding: 50px;
}

.btn-main {
	display: inline-block; /* 다른 요소와 한 줄에 배치되면서 크기 속성을 가짐 */
	padding: 10px 20px;
	margin-top: 20px; /* 위 요소와의 간격 */
	background-color: #6c757d; /* 차분한 회색 계열 (secondary) */
	color: white; /* 글자색 */
	text-align: center;
	text-decoration: none; /* 링크 밑줄 제거 */
	border: none;
	border-radius: 5px; /* 모서리를 둥글게 */
	font-size: 1em;
	font-weight: bold;
	cursor: pointer;
	transition: background-color 0.2s ease-in-out; /* 부드러운 색상 전환 효과 */
}

.btn-main:hover {
	background-color: #5a6268; /* 마우스를 올렸을 때 살짝 어두워짐 */
}

.btn-create {
	display: inline-block;
	padding: 10px 20px;
	margin-top: 20px;
	margin-right: 15px; /* 다른 버튼과의 간격 */
	background-color: #3498db;
	color: white;
	text-align: center;
	text-decoration: none;
	border: none;
	border-radius: 5px;
	font-size: 1em;
	font-weight: bold;
	cursor: pointer;
	transition: background-color 0.2s ease-in-out;
}

.btn-create:hover {
	background-color: #2980b9;
}
</style>
</head>
<body>
	<div class="container">
		<h1>입장 가능한 방 목록</h1>
		<table class="room-table">
			<thead>
				<tr>
					<th>방 번호</th>
					<th class="room-title">방 제목</th>
					<th>상태</th>
					<th>인원</th>
					<th>입장</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${not empty roomList}">
						<c:forEach var="room" items="${roomList}">
							<tr>
								<td>${room.roomId}</td>
								<td class="room-title"><c:out value="${room.title}" /></td>
								<td><span class="status-waiting">${room.status}</span></td>
								<td>${room.currentParticipants}/ ${room.maxParticipants}</td>
								<td><c:if
										test="${room.currentParticipants < room.maxParticipants}">
										<a
											href="${pageContext.request.contextPath}/game/play/${room.roomId}"
											class="btn">입장</a>
									</c:if> <c:if
										test="${room.currentParticipants >= room.maxParticipants}">
										<span>(꽉 참)</span>
									</c:if></td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="5" class="no-rooms">입장 가능한 방이 없습니다.</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
		<div style="text-align: center;">
			<a href="${pageContext.request.contextPath}/game/room/create"
				class="btn-create">대전 방 생성</a> <a
				href="${pageContext.request.contextPath}/main" class="btn-main">메인으로
				돌아가기</a>
		</div>
	</div>
</body>
</html>