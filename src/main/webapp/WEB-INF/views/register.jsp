<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<style>
body {
	font-family: Arial, sans-serif;
	text-align: center;
	margin-top: 50px;
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

table input[type="text"], table input[type="email"], table input[type="password"]
	{
	width: calc(100% - 22px);
	padding: 10px;
	margin-bottom: 5px;
	border: 1px solid #ddd;
	border-radius: 4px;
}

table input[type="submit"] {
	width: 100%;
	padding: 10px;
	background-color: #28a745;
	color: white;
	border: none;
	border-radius: 4px;
	cursor: pointer;
	font-size: 16px;
	margin-top: 10px;
}

table input[type="submit"]:hover {
	background-color: #218838;
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

.success-message {
	color: green;
	font-weight: bold;
	text-align: center;
	margin-bottom: 15px;
}

.info-message {
	color: blue;
	font-size: 0.9em;
	margin-top: 5px;
	display: block;
}

.container span {
	display: block;
	text-align: center;
	margin-top: 15px; /* 필요에 따라 상단 여백 조정 */
}
</style>
</head>
<body>
	<div class="container">
		<h1>회원가입</h1>

		<c:if test="${not empty errorMessage}">
			<div class="errorblock">${errorMessage}</div>
		</c:if>
		<c:if test="${not empty message}">
			<div class="success-message">${message}</div>
		</c:if>

		<form:form modelAttribute="user"
			action="${pageContext.request.contextPath}/register" method="POST">
			<form:errors path="*" cssClass="errorblock" element="div" />
			<table>
				<tr>
					<td><form:label path="email">이메일:</form:label></td>
					<td><form:input path="email" type="email" id="email" /> <form:errors
							path="email" cssClass="error" /> <span id="emailCheckMessage"
						class="info-message"></span></td>
				</tr>
				<tr>
					<td><form:label path="password">비밀번호:</form:label></td>
					<td><form:password path="password" id="password" /> <form:errors
							path="password" cssClass="error" /></td>
				</tr>
				<tr>
					<td><label for="confirmPassword">비밀번호 확인:</label></td>
					<td><input type="password" id="confirmPassword"
						name="confirmPassword" /> <span id="passwordMatchMessage"
						class="error"></span></td>
				</tr>
				<tr>
					<td><form:label path="nickname">닉네임:</form:label></td>
					<td><form:input path="nickname" type="text" id="nickname" />
						<form:errors path="nickname" cssClass="error" /> <span
						id="nicknameCheckMessage" class="info-message"></span></td>
				</tr>
				<%-- 
                <tr>
                    <td><form:label path="profileImage">프로필 이미지 URL (선택 사항):</form:label></td>
                    <td>
                        <form:input path="profileImage" type="text" />
                        <form:errors path="profileImage" cssClass="error" />
                    </td>
                </tr> --%>
				<tr>
					<td colspan="2"><input type="submit" value="회원가입" /></td>
				</tr>
			</table>
		</form:form>

		<a href="${pageContext.request.contextPath}/login"><span>로그인</span></a>
	</div>

	<script>
		$(document)
				.ready(
						function() {
							// 이메일 중복 확인
							$('#email')
									.on(
											'keyup',
											function() {
												var email = $(this).val();
												if (email.length > 0) {
													$
															.ajax({
																url : '${pageContext.request.contextPath}/checkEmail',
																type : 'GET',
																data : {
																	email : email
																},
																success : function(
																		response) {
																	if (response.success) {
																		$(
																				'#emailCheckMessage')
																				.text(
																						response.message)
																				.css(
																						'color',
																						'green');
																	} else {
																		$(
																				'#emailCheckMessage')
																				.text(
																						response.message)
																				.css(
																						'color',
																						'red');
																	}
																},
																error : function() {
																	$(
																			'#emailCheckMessage')
																			.text(
																					'이메일 확인 중 오류가 발생했습니다.')
																			.css(
																					'color',
																					'red');
																}
															});
												} else {
													$('#emailCheckMessage')
															.text('');
												}
											});

							// 닉네임 중복 확인
							$('#nickname')
									.on(
											'keyup',
											function() {
												var nickname = $(this).val();
												if (nickname.length > 0) {
													$
															.ajax({
																url : '${pageContext.request.contextPath}/checkNickname',
																type : 'GET',
																data : {
																	nickname : nickname
																},
																success : function(
																		response) {
																	if (response.success) {
																		$(
																				'#nicknameCheckMessage')
																				.text(
																						response.message)
																				.css(
																						'color',
																						'green');
																	} else {
																		$(
																				'#nicknameCheckMessage')
																				.text(
																						response.message)
																				.css(
																						'color',
																						'red');
																	}
																},
																error : function() {
																	$(
																			'#nicknameCheckMessage')
																			.text(
																					'닉네임 확인 중 오류가 발생했습니다.')
																			.css(
																					'color',
																					'red');
																}
															});
												} else {
													$('#nicknameCheckMessage')
															.text('');
												}
											});

							// 비밀번호 확인 일치 여부
							$('#confirmPassword')
									.on(
											'keyup',
											function() {
												var password = $('#password')
														.val();
												var confirmPassword = $(this)
														.val();
												if (password !== confirmPassword) {
													$('#passwordMatchMessage')
															.text(
																	'비밀번호가 일치하지 않습니다.');
												} else {
													$('#passwordMatchMessage')
															.text('');
												}
											});
						});
	</script>
</body>
</html>