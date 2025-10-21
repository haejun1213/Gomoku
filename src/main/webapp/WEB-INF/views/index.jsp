<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gomoku - 메인 페이지</title>
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
        }
        h1 {
            color: #333;
        }
      .button-group a {
            display: inline-block;
            margin: 10px;
            padding: 12px 25px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }
      .button-group a:hover {
            background-color: #0056b3;
        }
      .button-group a.guest-btn {
            background-color: #6c757d;
        }
      .button-group a.guest-btn:hover {
            background-color: #5a6268;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Gomoku 게임에 오신 것을 환영합니다!</h1>
        <p>시작하려면 다음 옵션 중 하나를 선택하세요:</p>
        <div class="button-group">
            <a href="${pageContext.request.contextPath}/login">로그인</a>
            <a href="${pageContext.request.contextPath}/register">회원가입</a>
            <a href="${pageContext.request.contextPath}/guest-login" class="guest-btn">게스트 로그인</a>
        </div>
    </div>
</body>
</html>