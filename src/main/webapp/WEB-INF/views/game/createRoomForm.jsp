<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gomoku - 방 만들기</title>
<style>
    body {
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        margin: 0;
        background-color: #f4f4f9;
    }
    .form-container {
        background: white;
        padding: 40px;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        width: 100%;
        max-width: 400px;
        text-align: center;
    }
    h1 {
        margin-bottom: 30px;
        color: #333;
    }
    .form-group {
        margin-bottom: 20px;
        text-align: left;
    }
    label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
        color: #555;
    }
    input[type="text"] {
        width: 100%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .btn-container {
        margin-top: 30px;
    }
    button {
        width: 100%;
        padding: 12px;
        border: none;
        border-radius: 4px;
        background-color: #3498db;
        color: white;
        font-size: 1.1em;
        cursor: pointer;
        transition: background-color 0.3s;
    }
    button:hover {
        background-color: #2980b9;
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
</style>
</head>
<body>
    <div class="form-container">
        <h1>방 만들기</h1>
        <form action="${pageContext.request.contextPath}/game/room/create" method="post">
            <div class="form-group">
                <label for="title">방 제목</label>
                <input type="text" id="title" name="title" required maxlength="50" placeholder="방 제목을 입력하세요">
            </div>
            
            <%-- ★★★ 신규 입력 필드 추가 ★★★ --%>
            <div class="form-group">
                <label for="maxParticipants">최대 인원</label>
                <select id="maxParticipants" name="maxParticipants">
                    <option value="2">2명</option>
                    <option value="4">4명</option>
                    <option value="6">6명</option>
                    <option value="8" selected>8명</option>
                </select>
            </div>
            <div class="form-group">
                <label>공개 설정</label>
                <div class="radio-group">
                    <label><input type="radio" name="isPrivate" value="N" checked> 공개</label>
                    <label><input type="radio" name="isPrivate" value="Y"> 비공개</label>
                </div>
            </div>
            <%-- ★★★ 추가 끝 ★★★ --%>

            <div class="btn-container">
                <button type="submit">만들기</button>
            </div>
        </form>
        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/main" class="btn-main">메인으로 돌아가기</a>
        </div>
    </div>
</body>
</html>
</html>