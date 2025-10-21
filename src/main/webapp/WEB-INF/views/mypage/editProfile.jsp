<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>프로필 수정</title>
<style>
    body { font-family: sans-serif; background-color: #f4f4f9; display: flex; justify-content: center; align-items: center; padding: 40px; }
    .container { max-width: 500px; width: 100%; background: white; padding: 30px 40px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
    h1 { text-align: center; color: #2980b9; }
    .form-group { margin-bottom: 20px; }
    .form-group label { display: block; font-weight: bold; margin-bottom: 5px; }
    .form-group input { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
    .form-group .description { font-size: 0.9em; color: #888; margin-top: 5px; }
    .btn-submit { width: 100%; padding: 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; font-size: 1.1em; cursor: pointer; }
    .btn-cancel { display: block; text-align: center; margin-top: 15px; color: #6c757d; }
    .error-message { color: #dc3545; font-weight: bold; margin-bottom: 15px; text-align: center; }
</style>
</head>
<body>
    <div class="container">
        <h1>프로필 수정</h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/mypage/edit" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="nickname">닉네임</label>
                <input type="text" id="nickname" name="nickname" value="${sessionScope.loggedInUser.nickname}" required>
            </div>
            <div class="form-group">
                <label for="profileImageFile">프로필 사진 변경</label>
                <input type="file" id="profileImageFile" name="profileImageFile" accept="image/*">
                <p class="description">새로운 프로필 사진을 업로드하세요. (선택 사항)</p>
            </div>

            <%-- ★★★ 일반 회원에게만 비밀번호 관련 필드를 보여줌 ★★★ --%>
            <c:if test="${sessionScope.loggedInUser.isSocialLogin == 'N'}">
                <hr>
                <div class="form-group">
                    <label for="newPassword">새 비밀번호</label>
                    <input type="password" id="newPassword" name="newPassword" placeholder="변경할 경우에만 입력">
                    <p class="description">비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다.</p>
                </div>
                <div class="form-group">
                    <label for="confirmNewPassword">새 비밀번호 확인</label>
                    <input type="password" id="confirmNewPassword" name="confirmNewPassword" placeholder="새 비밀번호를 다시 입력">
                </div>
                <hr>
                <div class="form-group">
                    <label for="currentPassword">현재 비밀번호</label>
                    <input type="password" id="currentPassword" name="currentPassword" required placeholder="정보를 변경하려면 현재 비밀번호를 입력하세요">
                </div>
            </c:if>
            
            <%-- ★★★ 소셜 회원은 비밀번호 입력 없이 수정 가능 ★★★ --%>
            <c:if test="${sessionScope.loggedInUser.isSocialLogin == 'Y'}">
                 <p class="description">소셜 로그인 회원은 닉네임과 프로필 사진만 변경할 수 있습니다.</p>
            </c:if>

            <button type="submit" class="btn-submit">수정 완료</button>
            <a href="${pageContext.request.contextPath}/mypage" class="btn-cancel">취소</a>
        </form>
    </div>
</body>
</html>