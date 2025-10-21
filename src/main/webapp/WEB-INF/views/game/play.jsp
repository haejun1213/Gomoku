<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<%--   --%>
<meta charset="UTF-8">
<title>오목 대전 - 방 #${room.roomId}</title>
<style>
body {
	font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
		"Helvetica Neue", Arial, sans-serif;
	display: flex;
	justify-content: center;
	align-items: flex-start;
	gap: 30px;
	margin-top: 20px;
	background-color: #f4f4f9;
}

#game-container {
	text-align: center;
	background: white;
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

#lobby-container {
	width: 300px;
	background: white;
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
	display: flex;
	flex-direction: column;
}

#game-info {
	display: flex;
	justify-content: space-between;
	margin-bottom: 10px;
	font-size: 1.1em;
	padding: 0 10px;
	min-height: 24px;
}

#game-board-container {
	position: relative;
	padding: 15px;
}

#game-board {
	border-collapse: collapse;
	/* background-color: #e4b56a;  <- 이 속성 제거 */
	border: 1px solid #333;
}

#game-board td {
	width: 30px;
	height: 30px;
	border: 1px solid #8b5e34;
	position: relative;
	cursor: pointer;
	background-color: #e4b56a; /* <- 여기에 배경색 추가 */
}
/* ★ 1. 안정적인 CSS 호버 효과 ★ */
/* 내 턴일 때, 돌이 없는 칸에만 호버 효과가 나타나도록 JavaScript로 클래스를 제어합니다. */
#game-board td.hoverable:hover::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	width: 28px;
	height: 28px;
	background-color: rgba(255, 255, 255, 0.4);
	border-radius: 50%;
	transform: translate(-50%, -50%);
	z-index: 5;
}

#game-board tr td:last-child, #game-board tr:last-child td {
	background-color: transparent !important;
	border-color: transparent !important;
}

.stone {
	width: 26px;
	height: 26px;
	border-radius: 50%;
	display: block;
	box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
	position: absolute;
	top: 0;
	left: 0;
	transform: translate(-50%, -50%);
	z-index: 10;
	box-sizing: border-box;
}

.stone.black {
	background-color: #111;
}

.stone.white {
	background-color: #fff;
	border: 1px solid #888;
}

#status-panel, #lobby-container h3 {
	text-align: center;
}

#participants-list {
	width: 100%;
	list-style: none;
	padding: 0;
	border: 1px solid #ccc;
	height: 150px;
	overflow-y: auto;
	border-radius: 4px;
	margin-bottom: 10px;
}

#participants-list li {
	padding: 8px;
	border-bottom: 1px solid #eee;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

#participants-list .ready {
	color: #28a745;
	font-weight: bold;
}

#participants-list button {
	font-size: 0.8em;
	padding: 2px 5px;
	cursor: pointer;
}

#chat-window {
	width: 100%;
	box-sizing: border-box;
	flex-grow: 1;
	border: 1px solid #ccc;
	overflow-y: auto;
	max-height: 360px;
	margin-top: 10px;
	padding: 8px;
	border-radius: 4px;
}

#chat-window p {
	margin: 0 0 5px 0;
	word-wrap: break-word;
}

#chat-input-container {
	display: flex;
	margin-top: 10px;
}

#chat-input {
	flex-grow: 1;
	border: 1px solid #ccc;
	border-radius: 4px 0 0 4px;
	padding: 8px;
}

#chat-send-btn {
	border: 1px solid #007bff;
	background: #007bff;
	color: white;
	padding: 8px 12px;
	border-radius: 0 4px 4px 0;
	cursor: pointer;
}

#ready-btn, #leave-btn, #surrender-btn {
	margin-top: 10px;
	width: 100%;
	padding: 10px;
	font-size: 1em;
	cursor: pointer;
	border: none;
	border-radius: 4px;
	color: white;
}

#ready-btn {
	background-color: #28a745;
}

#ready-btn.ready {
	background-color: #dc3545;
}

#leave-btn {
	background-color: #6c757d;
}

#surrender-btn {
	background-color: #ffc107;
	color: black;
}

#room-capacity-info {
	text-align: right;
	padding: 0 5px 5px 0;
	font-size: 0.9em;
	color: #555;
}

#kakao-invite-btn {
	display: flex; /* 아이콘과 텍스트를 정렬하기 위해 flex 사용 */
	align-items: center;
	justify-content: center;
	gap: 8px; /* 아이콘과 텍스트 사이 간격 */
	background-color: #FEE500; /* 카카오 공식 노란색 */
	color: #191919; /* 카카오가 사용하는 텍스트 색상 */
	font-weight: bold;
	border: none;
	margin-top: 10px;
	width: 100%;
	padding: 10px;
	font-size: 1em;
	cursor: pointer;
	border-radius: 4px;
	transition: background-color 0.2s;
}

#kakao-invite-btn::before {
	content: '';
	display: inline-block;
	width: 20px;
	height: 20px;
	background-color: #191919;
	/* 카카오 말풍선 아이콘 모양 */
	mask-image:
		url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath d='M12 2C6.48 2 2 5.58 2 10c0 3.03 1.86 5.68 4.5 6.91V22l3.58-2.05C10.61 19.95 11.29 20 12 20c5.52 0 10-3.58 10-8s-4.48-8-10-8z'/%3E%3C/svg%3E");
	mask-size: contain;
	-webkit-mask-image:
		url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath d='M12 2C6.48 2 2 5.58 2 10c0 3.03 1.86 5.68 4.5 6.91V22l3.58-2.05C10.61 19.95 11.29 20 12 20c5.52 0 10-3.58 10-8s-4.48-8-10-8z'/%3E%3C/svg%3E");
	-webkit-mask-size: contain;
}

#kakao-invite-btn:hover {
	background-color: #F8D700; /* 살짝 어두운 노란색 */
}

.stone.last-move::after {
	content: '';
	position: absolute;
	top: 50%;
	left: 50%;
	width: 8px;
	height: 8px;
	background-color: red;
	border: 1px solid white;
	border-radius: 50%;
	transform: translate(-50%, -50%);
	z-index: 15; /* 돌 위에 보이도록 z-index 설정 */
}

.modal-overlay {
        position: fixed; top: 0; left: 0;
        width: 100%; height: 100%;
        background-color: rgba(0, 0, 0, 0.6);
        display: none; /* 평소에는 숨김 */
        justify-content: center;
        align-items: center;
        z-index: 1000;
    }
    .modal-content {
        background-color: white; padding: 30px 40px;
        border-radius: 8px; width: 90%; max-width: 500px;
        position: relative; box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        text-align: center; /* 내부 요소 가운데 정렬 */
    }
    .modal-close {
        position: absolute; top: 10px; right: 20px;
        font-size: 2em; font-weight: bold; color: #aaa; cursor: pointer;
    }
    .modal-close:hover { color: #333; }
    
    #modal-body-content h2 { color: #2980b9; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
    #modal-body-content p { font-size: 1.1em; line-height: 1.6; margin: 10px 0; }
    #modal-body-content strong { min-width: 80px; display: inline-block; color: #555; }
    .btn-report { background-color: #dc3545; color: white; padding: 8px 15px; border-radius: 5px; border: none; cursor: pointer; }

    /* 모달 내부 프로필 이미지 스타일 */
    .modal-profile-image-wrapper {
        width: 120px;
        height: 120px;
        border-radius: 50%;
        overflow: hidden;
        border: 4px solid #3498db;
        margin: 0 auto 20px auto; /* 가운데 정렬 */
        background-color: #f0f0f0; /* 이미지가 없을 때를 대비한 배경색 */
    }
    .modal-profile-image-wrapper img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }
</style>
<script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.1/kakao.min.js"
	integrity="sha384-kDljxUXHaJ9xAb2AzRd59KxjrFjzHa5TAoFQ6GbYTCAG0bjM55XohjjDT7tDDC01"
	crossorigin="anonymous"></script>
</head>
<body>
	<div id="game-container">
		<h2>
			<c:out value="${room.title}" />
		</h2>
		<div id="game-info">
			<span id="blackPlayerInfo">흑: -</span> <span id="whitePlayerInfo">백:
				-</span>
		</div>
		<div id="game-board-container">
			<table id="game-board"></table>
		</div>
		<div id="status-panel">
			<p id="turnIndicator">대기실에서 준비해주세요.</p>
		</div>
		<button id="surrender-btn" style="display: none;">항복</button>
	</div>
	<div id="lobby-container">
		<h3>참가자 목록</h3>
		<div id="room-capacity-info">참여 인원: - / -</div>

		<ul id="participants-list"></ul>
		<button id="ready-btn" style="display: none;">게임 준비</button>
		
	<c:if test="${!isAiMatch}">
		<h3>채팅</h3>
		<div id="chat-window"></div>
		<div id="chat-input-container">
			<input type="text" id="chat-input" placeholder="메시지 입력...">
			<button id="chat-send-btn">전송</button>
		</div>
		<button id="kakao-invite-btn">카카오톡으로 초대하기</button>

		</c:if>
		<button id="leave-btn">방 나가기</button>
	</div>
	
	<div id="profile-modal-overlay" class="modal-overlay">
        <div class="modal-content">
            <span class="modal-close">&times;</span>
            <div id="modal-body-content">
                <%-- 여기에 프로필 내용이 동적으로 채워집니다 --%>
            </div>
        </div>
    </div>
	<script>
    // --- 1. 변수 및 UI 요소 초기화 ---
    const boardElement = document.getElementById('game-board');
    const roomId = "${room.roomId}";
    const contextPath = "${pageContext.request.contextPath}";
    const loginUserId = "${sessionScope.loggedInUser.userId}";
    const maxParticipants = parseInt('${room.maxParticipants}');
    const roomTitle = "${room.title}";
    const invitationUrl = "${invitationUrl}";
    const isAiMatch = "${isAiMatch}";
    
    const kakaoInviteBtn = document.getElementById('kakao-invite-btn');
    const capacityInfo = document.getElementById('room-capacity-info');
    const participantsList = document.getElementById('participants-list');
    const readyBtn = document.getElementById('ready-btn');
    const leaveBtn = document.getElementById('leave-btn');
    const surrenderBtn = document.getElementById('surrender-btn');
    const chatWindow = document.getElementById('chat-window');
    const chatInput = document.getElementById('chat-input');
    const chatSendBtn = document.getElementById('chat-send-btn');
    const turnIndicator = document.getElementById('turnIndicator');
    const blackPlayerInfo = document.getElementById('blackPlayerInfo');
    const whitePlayerInfo = document.getElementById('whitePlayerInfo');
    

    const modalOverlay = document.getElementById('profile-modal-overlay');
    const modalContent = document.getElementById('modal-body-content');
    const modalCloseBtn = document.querySelector('.modal-close');

    let lastMoveStoneElement = null;
    let myRole = 'SPECTATOR';
    let isMyTurn = false;
    let gameInProgress = false;

    // --- 2. 오목판 그리기 ---
    for (let i = 0; i < 15; i++) {
        const tr = document.createElement('tr');
        for (let j = 0; j < 15; j++) {
            const td = document.createElement('td');
            td.dataset.x = j;
            td.dataset.y = i;
            tr.appendChild(td);
        }
        boardElement.appendChild(tr);
    }
    
    // --- 3. 웹소켓 ---
    const websocket = new WebSocket(`wss://\${window.location.host}\${contextPath}/ws/game/\${roomId}`);
    websocket.onopen = () => appendSystemMessage('서버에 연결되었습니다.');
    websocket.onclose = () => { appendSystemMessage('서버와 연결이 끊겼습니다.'); turnIndicator.innerText = '연결 종료'; };
    websocket.onerror = (error) => console.error('WebSocket Error:', error);

    websocket.onmessage = (event) => {
        const msg = JSON.parse(event.data);
        console.log('Received:', msg);
        switch(msg.type) {
            case 'ROOM_STATE': updateLobby(msg); break;
            case 'CHAT':
                appendChatMessage(msg.senderId, msg.sender, msg.content); 
                break;
            case 'GAME_START': handleGameStart(msg); break;
            case 'UPDATE_STATE': handleUpdateState(msg); break;
            case 'GAME_OVER': handleGameOver(msg); break;
        }
    };
    
    
    if (chatWindow) {
        chatWindow.addEventListener('click', function(e) {
            // 클릭된 요소가 'btn-chat-report' 클래스를 가졌는지 확인
            if (e.target.classList.contains('btn-chat-report')) {
                const targetId = e.target.dataset.targetId;
                const targetNickname = e.target.dataset.targetNickname;
                const reason = e.target.dataset.reason;

                if (confirm(`'\${targetNickname}'님의 채팅 내용을 신고하시겠습니까?\n\n내용: \${reason}`)) {
                    submitReport(targetId, reason);
                }
            }
        });
    }
    
    function submitReport(targetId, reason) {
        const reportData = {
            reporterId: loginUserId,
            targetId: targetId,
            reason: reason
        };

        fetch(`\${contextPath}/api/report/submit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(reportData)
        })
        .then(res => res.json())
        .then(data => {
            alert(data.message);
        })
        .catch(err => {
            alert('신고 중 오류가 발생했습니다.');
            console.error(err);
        });
    }


    // --- 4. 핵심 로직 함수들 ---
    function updateLobby(msg) {
    // 1. 서버가 보내준 최신 gameInProgress 상태를 JavaScript 전역 변수에 반영합니다.
    gameInProgress = msg.gameInProgress; 
    
    const participants = msg.participants;
    const lobbyContainer = document.getElementById('lobby-container');

    
    
    // 2. AI 대전일 때는 로비 UI가 없으므로, PvP일 때만 아래 로직을 실행합니다.
    if (lobbyContainer) {
        participantsList.innerHTML = '';
        let isLoginUserPlayer = false;
        
        const currentCount = participants.length;
        capacityInfo.innerText = `참여 인원: \${currentCount} / \${maxParticipants}`;
        
        participants.forEach(p => {
            const li = document.createElement('li');
            let nicknameHtml;

            // ★ 내 닉네임과 다른 사람 닉네임 모두 팝업을 띄우도록 수정 ★
            if (p.role === 'B' || p.role === 'G') {
                nicknameHtml = `<span>\${p.nickname}</span>`; // AI는 링크 없음
            } else {
            	nicknameHtml = `<a href="#" class="profile-link" data-userid="\${p.userId}" title="\${p.nickname}님의 프로필 보기"><span>\${p.nickname}</span></a>`;
            }
            
            let content = nicknameHtml;
            if (p.role !== '관전자') {
                content += ` <strong>[\${p.role}]</strong>`;
                if (p.isReady) content += ' <span class="ready">(준비완료)</span>';
            }

            if (p.userId == loginUserId) {
                li.style.fontWeight = 'bold';
                if (p.role !== '관전자') {
                    isLoginUserPlayer = true;
                    if (!gameInProgress) content += ` <button class="leave-btn">관전하기</button>`;
                }
            }
            li.innerHTML = content;
            participantsList.appendChild(li);
        });

        // '게임 참여하기' 버튼 표시 로직 (기존 로직 유지)
        if (!isLoginUserPlayer && participants.filter(p => p.role !== '관전자').length < 2 && !gameInProgress) {
            const joinLi = document.createElement('li');
            joinLi.innerHTML = `<button class="join-btn">게임 참여하기</button>`;
            participantsList.appendChild(joinLi);
        }
        
        // '준비' 버튼 표시/숨김 및 상태 업데이트 로직 (기존 로직 유지)
        readyBtn.style.display = isLoginUserPlayer && !gameInProgress ? 'block' : 'none';
        if (isLoginUserPlayer && !gameInProgress) {
            const myStatus = participants.find(p => p.userId == loginUserId);
            if (myStatus) {
                readyBtn.textContent = myStatus.isReady ? '준비 취소' : '게임 준비';
                readyBtn.classList.toggle('ready', myStatus.isReady);
            }
        }
    }
}
    
    function handleGameStart(msg) {
        // 1. updateLobby 함수를 호출하여 참가자 목록 UI를 새로고침합니다.
        //    이때 msg 객체 안에는 gameInProgress=true와 참가자 목록이 모두 들어있습니다.
        //    updateLobby 함수는 gameInProgress가 true이므로 알아서 버튼들을 숨겨줍니다.
        updateLobby(msg);
        
        // 2. 게임 시작에 필요한 나머지 UI 처리
        gameInProgress = true; // JS 전역 변수도 동기화
        
        blackPlayerInfo.innerText = `흑: \${msg.blackPlayerNickname}`;
        whitePlayerInfo.innerText = `백: \${msg.whitePlayerNickname}`;
        
        if (loginUserId == msg.blackPlayerId) myRole = 'BLACK';
        else if (loginUserId == msg.whitePlayerId) myRole = 'WHITE';
        else myRole = 'SPECTATOR';
        
        if (myRole !== 'SPECTATOR') {
            surrenderBtn.style.display = 'block';
        }
        
        isMyTurn = (myRole === 'BLACK');
        updateTurnIndicator('BLACK');
        clearBoard();
        
        boardElement.addEventListener('click', onBoardClick);
    }
    
    function handleUpdateState(msg) {
        drawStone(msg.x, msg.y, msg.color);
        isMyTurn = (myRole === msg.nextTurn);
        updateTurnIndicator(msg.nextTurn);
    }
    
    function handleGameOver(msg) {
        gameInProgress = false;
        isMyTurn = false;
        surrenderBtn.style.display = 'none';
        turnIndicator.style.color = 'green';
        
        // ★★★ 무승부 처리 로직 추가 ★★★
        if (msg.winnerId == null) {
            turnIndicator.innerText = "무승부입니다!";
        } else if (loginUserId == msg.winnerId) {
            turnIndicator.innerText = `승리! (\${msg.reason})`;
        } else {
            turnIndicator.innerText = `패배! 승자: \${msg.winnerNickname}`;
        }
        
        // 게임이 끝나면 오목판 클릭 이벤트 제거 (기존 로직 유지)
        boardElement.removeEventListener('click', onBoardClick);
        if (lastMoveStoneElement) {
            lastMoveStoneElement.classList.remove('last-move');
            lastMoveStoneElement = null;
        }
        
    }

    // ★ 3. onBoardClick을 dataset을 사용하는 단순한 방식으로 수정 ★
    function onBoardClick(event) {
        if (!isMyTurn) return;
        const target = event.target.closest('td');
        if (target && target.children.length === 0) {
            const x = parseInt(target.dataset.x);
            const y = parseInt(target.dataset.y);
            
            drawStone(x, y, myRole);
            websocket.send(JSON.stringify({ type: 'MOVE', x: x, y: y }));
            isMyTurn = false;
            updateTurnIndicator(myRole === 'BLACK' ? 'WHITE' : 'BLACK');
        }
    }
    boardElement.addEventListener('click', onBoardClick);
    
    participantsList.addEventListener('click', (e) => {
        const profileLink = e.target.closest('.profile-link');
        if (profileLink) {
            e.preventDefault();
            const userId = profileLink.dataset.userid;
            openProfileModal(userId);
            return;
        }
        if (e.target.classList.contains('join-btn')) {
            websocket.send(JSON.stringify({ type: 'JOIN_AS_PLAYER' }));
        }
        if (e.target.classList.contains('leave-btn')) {
            websocket.send(JSON.stringify({ type: 'LEAVE_AS_PLAYER' }));
        }
    });
    
    readyBtn.addEventListener('click', () => {
        const currentReadyState = readyBtn.classList.contains('ready');
        websocket.send(JSON.stringify({ type: 'READY', isReady: !currentReadyState }));
    });

    leaveBtn.addEventListener('click', () => {
    	if (confirm('방을 나가시겠습니까?')) {
    		websocket.close();
            window.location.href = `\${contextPath}/main`;
        }
        
    });

    surrenderBtn.addEventListener('click', () => {
        if (confirm('정말로 항복하시겠습니까?')) {
            websocket.send(JSON.stringify({ type: 'SURRENDER' }));
        }
    });
    
    chatSendBtn.addEventListener('click', () => {
        const content = chatInput.value.trim();
        if (content) {
            websocket.send(JSON.stringify({ type: 'CHAT', content: content }));
            chatInput.value = '';
        }
    });
    chatInput.addEventListener('keydown', (e) => { 
        if (e.key === 'Enter' && !e.isComposing) {
            e.preventDefault();
            chatSendBtn.click();
        } 
    });
    
    function drawStone(x, y, color) {
        const td = document.querySelector(`td[data-x='\${x}'][data-y='\${y}']`);
        if (td && td.children.length === 0) {
            const stone = document.createElement('div');
            stone.className = `stone \${color.toLowerCase()}`;
            td.appendChild(stone);

            if (lastMoveStoneElement) {
                lastMoveStoneElement.classList.remove('last-move');
            }
            
            stone.classList.add('last-move');
            
            lastMoveStoneElement = stone;
        }
    }
    
    function clearBoard() {
        boardElement.querySelectorAll('.stone').forEach(s => s.remove());
        if (lastMoveStoneElement) {
            lastMoveStoneElement.classList.remove('last-move');
            lastMoveStoneElement = null;
        }
    }

    function appendChatMessage(senderId, sender, content) {
        // 채팅창이 없는 AI 대전 모드에서는 아무것도 하지 않음
        if (!chatWindow) return;

        const chatLine = document.createElement('p');
        
        let reportBtnHtml = '';
        // 내가 보낸 메시지가 아니고, AI나 게스트가 아닌 경우에만 신고 버튼 표시
        // (AI나 게스트는 보통 ID가 0이거나 특정 규칙을 따르므로, senderId > 0 조건으로 일반 유저인지 확인)
        if (loginUserId != senderId && senderId > 0) { 
            // 신고에 필요한 정보를 버튼의 data- 속성에 저장합니다.
            // XSS 공격 방지를 위해 content를 속성에 넣을 때는 escape 처리가 필요하지만,
            // 이 프로젝트에서는 서버에서 필터링하므로 우선 그대로 사용합니다.
            reportBtnHtml = ` <button class="btn-chat-report" 
                                    data-target-id="\${senderId}" 
                                    data-target-nickname="\${sender}" 
                                    data-reason="\${content}">신고</button>`;
        }

        // JSP EL과 충돌하지 않도록 JavaScript 변수는 \${...}로 이스케이프 처리
        chatLine.innerHTML = `<strong>\${sender}:</strong> \${content} \${reportBtnHtml}`;
        chatWindow.appendChild(chatLine);
        
        // 스크롤을 항상 맨 아래로 이동
        chatWindow.scrollTop = chatWindow.scrollHeight;
    }
    
    function appendSystemMessage(content) {
        if (!chatWindow) return;

        chatWindow.innerHTML += `<p><em>-- \${content} --</em></p>`;
        chatWindow.scrollTop = chatWindow.scrollHeight;
    }

    function updateTurnIndicator(turnColor) {
        const allTds = boardElement.querySelectorAll('td');
        if (gameInProgress) {
            if (myRole !== 'SPECTATOR') {
                if (isMyTurn) {
                    turnIndicator.innerText = "당신의 차례입니다.";
                    turnIndicator.style.color = 'blue';
                    allTds.forEach(td => td.classList.add('hoverable')); // 내 턴일 때만 호버 활성화
                } else {
                    turnIndicator.innerText = `상대방(\${turnColor})의 차례입니다.`;
                    turnIndicator.style.color = 'red';
                    allTds.forEach(td => td.classList.remove('hoverable'));
                }
            } else {
                turnIndicator.innerText = `관전 중 (\${turnColor}의 차례)`;
                turnIndicator.style.color = 'gray';
                allTds.forEach(td => td.classList.remove('hoverable'));
            }
        } else {
            // 게임이 끝나거나 대기 중일 때
            turnIndicator.innerText = '대기실에서 준비해주세요.';
            turnIndicator.style.color = '#555';
            allTds.forEach(td => td.classList.remove('hoverable'));
        }
    }
    
    function openProfileModal(userId) {
        modalContent.innerHTML = '<p>정보를 불러오는 중...</p>';
        modalOverlay.style.display = 'flex';

        fetch(`\${contextPath}/api/profile/\${userId}`)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const user = data.profileUser;
                    const stats = data.stats;
                    const winRate = stats.totalGames > 0 ? (stats.wins / stats.totalGames * 100).toFixed(2) : 0;

                    let profileImageHtml = `
                        <div class="modal-profile-image-wrapper">
                            <!-- 이미지가 없을 때 기본 아이콘 또는 비워둠 -->
                        </div>
                    `;
                    if (user.profileImage) {
                        profileImageHtml = `
                            <div class="modal-profile-image-wrapper">
                                <img src="\${contextPath}\${user.profileImage}" alt="\${user.nickname}님의 프로필 이미지">
                            </div>
                        `;
                    }

                    modalContent.innerHTML = `
                        \${profileImageHtml}
                        <h2>\${user.nickname}님의 프로필</h2>
                        <p><strong>총 대전:</strong> \${stats.totalGames}전</p>
                        <p><strong>전적:</strong> \${stats.wins}승 \${stats.losses}패 \${stats.draws}무</p>
                        <p><strong>승률:</strong> \${winRate}%</p>
                        <hr>
                        
                        
                        <p style="text-align:center; margin-top:20px;">
                            <a href="\${contextPath}/report/user/\${user.userId}" class="btn-report">이 사용자 신고하기</a>
                        </p>
                    `;
                } else {
                    modalContent.innerHTML = `<p>\${data.message}</p>`;
                }
            })
            .catch(err => {
                console.error('프로필 정보 로딩 오류:', err);
                modalContent.innerHTML = '<p>정보를 불러오는 데 실패했습니다.</p>';
            });
    }

    // ★ 모달 닫기 및 신고하기 함수 ★
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal-close') || e.target === modalOverlay) {
            modalOverlay.style.display = 'none';
        }
    });
    
    function reportUser(userId) {
        alert(`사용자 ID \${userId} 신고 기능은 현재 준비 중입니다.`);
    }
    
 // ★ 1. SDK 초기화 ★
    // 붙여넣으실 때는 'YOUR_JAVASCRIPT_KEY' 부분을 실제 키 값으로 변경해야 합니다.
    Kakao.init('f8409b6bd370b9e3952337f27e82187c'); 
    const imageUrl = `\${window.location.origin}\${contextPath}/img/Gomoku.png`;
    // ★ 2. 초대 버튼에 클릭 이벤트 추가 ★
    kakaoInviteBtn.addEventListener('click', () => {
        // 초대 URL이 정상적으로 넘어왔는지 확인
        console.log("카카오톡 공유에 사용될 URL:", invitationUrl);

        Kakao.Share.sendDefault({
            objectType: 'feed',
            content: {
                title: '같이 오목 한 판 하실래요?',
                description: `방 제목: \${roomTitle}`,
                imageUrl: imageUrl,
                link: {
                    mobileWebUrl: invitationUrl,
                    webUrl: invitationUrl,
                },
            },
            buttons: [
                {
                    title: '게임 참가하기',
                    link: {
                        mobileWebUrl: invitationUrl,
                        webUrl: invitationUrl,
                    },
                },
            ],
        });
    });
</script>
</body>
</html>