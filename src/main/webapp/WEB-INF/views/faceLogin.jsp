<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>얼굴 인식 로그인</title>
<style>
    body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; background-color: #f4f4f4; }
    .container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); display: inline-block; text-align: left; max-width: 400px; width: 100%; }
    h1 { color: #333; text-align: center; }
    .form-group { margin-bottom: 20px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
    .form-group input[type="email"] { width: calc(100% - 22px); padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
    .video-container { text-align: center; margin-top: 20px; }
    .video-container video { border: 1px solid #ddd; border-radius: 4px; background-color: #000; }
    .btn-group { text-align: center; margin-top: 15px; }
    .btn-group button { padding: 10px 20px; font-size: 1em; cursor: pointer; border: 1px solid #ccc; border-radius: 4px; }
    #face-login-btn { background-color: #007bff; color: white; border-color: #007bff; }
    .btn-cancel { display: block; text-align: center; margin-top: 20px; color: #6c757d; }
</style>
</head>
<body>
	<div class="container">
		<h1>얼굴 인식 로그인</h1>
		
		<div class="form-group">
			<label for="email">이메일</label>
			<input type="email" id="emailInput" placeholder="얼굴을 등록한 이메일을 입력하세요" required>
		</div>

		<div class="video-container">
            <p style="font-size: 0.9em; color: #777;">카메라를 정면으로 응시해주세요.</p>
            <video id="login-video" width="320" height="240" autoplay muted playsinline></video>
            <canvas id="login-canvas" width="320" height="240" style="display:none;"></canvas>
            <div class="btn-group">
                <button type="button" id="start-login-camera">카메라 켜기</button>
                <button type="button" id="face-login-btn" style="display:none;">얼굴로 로그인</button>
            </div>
		</div>
        
        <a href="${pageContext.request.contextPath}/login" class="btn-cancel">다른 방법으로 로그인</a>
	</div>

<script>
    const loginVideo = document.getElementById('login-video');
    const loginCanvas = document.getElementById('login-canvas');
    const startLoginCameraBtn = document.getElementById('start-login-camera');
    const faceLoginBtn = document.getElementById('face-login-btn');
    const emailInput = document.getElementById('emailInput');

    startLoginCameraBtn.addEventListener('click', async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ video: true });
            loginVideo.srcObject = stream;
            startLoginCameraBtn.style.display = 'none';
            faceLoginBtn.style.display = 'inline-block';
        } catch (err) {
            console.error("카메라 접근 오류:", err);
            alert('카메라를 사용할 수 없습니다. (페이지가 https 또는 localhost인지 확인하세요)');
        }
    });

    faceLoginBtn.addEventListener('click', () => {
        const email = emailInput.value;
        if (!email) {
            alert('먼저 이메일을 입력해주세요.');
            emailInput.focus();
            return;
        }

        faceLoginBtn.disabled = true;
        faceLoginBtn.textContent = '인식 중...';

        loginCanvas.getContext('2d').drawImage(loginVideo, 0, 0, loginCanvas.width, loginCanvas.height);
        const imageDataUrl = loginCanvas.toDataURL('image/jpeg');

        fetch('${pageContext.request.contextPath}/face/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
            body: `email=\${encodeURIComponent(email)}&imageDataUrl=\${encodeURIComponent(imageDataUrl)}`
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert('얼굴 인식 성공! 로그인합니다.');
                window.location.href = "${pageContext.request.contextPath}" + data.redirectUrl;
            } else {
                alert('로그인 실패: ' + data.message);
            }
        })
        .catch(err => {
            console.error("얼굴 로그인 오류:", err);
            alert('로그인 처리 중 오류가 발생했습니다.');
        })
        .finally(() => {
            faceLoginBtn.disabled = false;
            faceLoginBtn.textContent = '얼굴로 로그인';
        });
    });
</script>
</body>
</html>
