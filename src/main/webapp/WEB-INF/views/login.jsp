<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<style>
body {
	font-family: Arial, sans-serif;
	text-align: center;
	margin-top: 100px;
	background-color: #f4f4f4;
}

.container {
	background-color: #fff;
	padding: 30px;
	border-radius: 8px;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	display: inline-block;
	text-align: left;
}

.container span {
	display: block;
	text-align: center;
	margin-top: 15px; /* 필요에 따라 상단 여백 조정 */
}

h1 {
	color: #333;
	text-align: center;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 20px;
}

table td {
	padding: 10px;
	vertical-align: top;
}

table input[type="email"], table input[type="password"] {
	width: calc(100% - 22px);
	padding: 10px;
	margin-bottom: 5px;
	border: 1px solid #ddd;
	border-radius: 4px;
}

table input[type="submit"] {
	width: 100%;
	padding: 10px;
	background-color: #007bff;
	color: white;
	border: none;
	border-radius: 4px;
	cursor: pointer;
	font-size: 16px;
	margin-top: 10px;
}

table input[type="submit"]:hover {
	background-color: #0056b3;
}

.error {
	color: red;
	font-size: 0.9em;
	margin-top: 5px;
	display: block;
}

.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px 0;
	text-align: center;
}

.kakao-login-btn img {
	height: 24px;
	margin: 5%;
	width: 90%;
	height: 45px;
}


</style>
</head>
<body>
	<div class="container">
		<h1>로그인</h1>

		<c:if test="${not empty errorMessage}">
			<div class="errorblock">${errorMessage}</div>
		</c:if>
		<c:if test="${not empty message}">
			<div class="success-message">${message}</div>
		</c:if>

		<form:form modelAttribute="userLoginDTO"
			action="${pageContext.request.contextPath}/login" method="POST">
			<table>
				<tr>
					<td><label for="email">이메일:</label></td>
					<td><form:input path="email" type="email" id="email" /> <form:errors
							path="email" cssClass="error" /></td>
				</tr>
				<tr>
					<td><label for="password">비밀번호:</label></td>
					<td><form:password path="password" id="password" /> <form:errors
							path="password" cssClass="error" /></td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" value="로그인" /></td>
				</tr>
			</table>
		</form:form>

		<a
			href="https://kauth.kakao.com/oauth/authorize?client_id=f8409b6bd370b9e3952337f27e82187c&redirect_uri=https://172.31.57.22:8443/Gomoku/kakao/callback&response_type=code"
			class="kakao-login-btn"> <img 
			src="${pageContext.request.contextPath}/img/kakao_login_medium_narrow.png"
			alt="카카오 로그인 아이콘">
		</a> 
		<a href="${pageContext.request.contextPath}/face-login" class="btn-face-login"><span>얼굴 인식으로 로그인</span></a>
		<a href="${pageContext.request.contextPath}/register"><span>회원가입</span></a>
		<a href="${pageContext.request.contextPath}/"><span>메인으로</span></a>
		
            <a href="${pageContext.request.contextPath}/guest-login"><span>게스트 로그인</span></a>
	</div>
</body>
</html>