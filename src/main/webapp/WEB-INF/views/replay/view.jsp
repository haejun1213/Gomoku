<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게임 복기 - Game ID: ${replayData.gameInfo.gameId}</title>
<style>
    body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; display: flex; justify-content: center; align-items: flex-start; gap: 30px; margin-top: 20px; background-color: #f4f4f9; }
    #game-container { text-align: center; background: white; padding: 20px 40px 40px 40px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    #game-info { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 1.2em; padding: 0 10px; min-height: 24px; font-weight: bold;}
    #game-board-container { position: relative; padding: 15px; } 
    #game-board { border-collapse: collapse; background-color: #e4b56a; border: 1px solid #333; }
    #game-board td { width: 30px; height: 30px; border: 1px solid #8b5e34; position: relative; }
    .stone { width: 26px; height: 26px; border-radius: 50%; display: block; box-shadow: 1px 1px 2px rgba(0,0,0,0.5); position: absolute; top: 0; left: 0; transform: translate(-50%, -50%); z-index: 10; box-sizing: border-box; }
    .stone.black { background-color: #111; }
    .stone.white { background-color: #fff; border: 1px solid #888; }
    .stone.last-move::after {
        content: ''; position: absolute; top: 50%; left: 50%;
        width: 8px; height: 8px; background-color: red;
        border: 1px solid white; border-radius: 50%;
        transform: translate(-50%, -50%); z-index: 15;
    }
    #status-panel { margin-top: 5px; font-size: 1.1em; font-weight: bold; height: 30px; color: #555; }
    .replay-controls { margin-top: 20px; display: flex; gap: 10px; justify-content: center; align-items: center; }
    .replay-controls button { padding: 10px 15px; font-size: 1em; cursor: pointer; border-radius: 5px; border: 1px solid #ccc; background-color: #f0f0f0; }
    .replay-controls button:hover { background-color: #e0e0e0; }
    .replay-controls button.playing { background-color: #ffc107; }
    .result-win { color: #007bff; }
    .result-loss { color: #dc3545; }
    .result-draw { color: #6c757d; }
    .btn-main { display: inline-block; margin-top: 30px; padding: 10px 20px; background-color: #6c757d; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }
</style>
</head>
<body>
    <div id="game-container">
        <h2>게임 복기 (Game ID: ${replayData.gameInfo.gameId})</h2>
        <div id="game-info">
            <span style="color: #333;">흑: <c:out value="${replayData.gameInfo.blackPlayerNickname}"/></span>
            <span class="${replayData.gameInfo.result eq '승리' ? 'result-win' : (replayData.gameInfo.result eq '패배' ? 'result-loss' : 'result-draw')}">
                <c:choose>
                    <c:when test="${replayData.gameInfo.winnerId == replayData.gameInfo.blackUserId}">[ 흑 승 ]</c:when>
                    <c:when test="${replayData.gameInfo.winnerId == replayData.gameInfo.whiteUserId}">[ 백 승 ]</c:when>
                    <c:otherwise>[ 무승부 ]</c:otherwise>
                </c:choose>
            </span>
            <span style="color: #333;">백: <c:out value="${replayData.gameInfo.whitePlayerNickname}"/></span>
        </div>
        <div id="game-board-container">
            <table id="game-board"></table>
        </div>
        <div id="status-panel">
            <p id="move-indicator">0 / ${replayData.moves.size()} 수</p>
        </div>
        <div class="replay-controls">
            <button id="btn-start">|&laquo;</button>
            <button id="btn-prev">&laquo;</button>
            <button id="btn-play">재생</button>
            <button id="btn-next">&raquo;</button>
            <button id="btn-end">&raquo;|</button>
        </div>
        <a href="${pageContext.request.contextPath}/mypage" class="btn-main">마이페이지로 돌아가기</a>
    </div>

<script>
    // --- 1. 데이터 및 변수 초기화 ---
    // 서버가 전달한 JSON 문자열을 안전하게 JavaScript 배열로 변환
    const moves = ${not empty movesJson ? movesJson : '[]'};
    
    const boardElement = document.getElementById('game-board');
    const moveIndicator = document.getElementById('move-indicator');
    const startBtn = document.getElementById('btn-start');
    const prevBtn = document.getElementById('btn-prev');
    const playBtn = document.getElementById('btn-play');
    const nextBtn = document.getElementById('btn-next');
    const endBtn = document.getElementById('btn-end');

    let currentMoveIndex = -1;
    let playInterval = null;

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

    // --- 3. 이벤트 리스너 바인딩 ---
    startBtn.addEventListener('click', goToStart);
    prevBtn.addEventListener('click', prevMove);
    playBtn.addEventListener('click', togglePlay);
    nextBtn.addEventListener('click', nextMove);
    endBtn.addEventListener('click', goToEnd);
    
    // --- 4. 수순 제어 함수들 ---
    function nextMove() {
        if (currentMoveIndex < moves.length - 1) {
            currentMoveIndex++;
            drawMove(currentMoveIndex);
        } else {
            if (playInterval) togglePlay(); // 마지막 수에 도달하면 자동 재생 중지
        }
    }

    function prevMove() {
        if (currentMoveIndex >= 0) {
            clearMove(currentMoveIndex);
            currentMoveIndex--;
            updateLastMoveMarker();
            updateMoveIndicator();
        }
    }

    function togglePlay() {
        if (playInterval) {
            clearInterval(playInterval);
            playInterval = null;
            playBtn.textContent = '재생';
            playBtn.classList.remove('playing');
        } else {
            if (currentMoveIndex >= moves.length - 1) {
                goToStart();
            }
            playInterval = setInterval(nextMove, 800);
            playBtn.textContent = '정지';
            playBtn.classList.add('playing');
        }
    }

    function goToStart() {
        if (playInterval) togglePlay();
        clearBoard();
        currentMoveIndex = -1;
        updateMoveIndicator();
    }

    function goToEnd() {
        if (playInterval) togglePlay();
        clearBoard();
        moves.forEach((move, index) => {
            drawStone(move.xcoord, move.ycoord, move.color);
        });
        currentMoveIndex = moves.length - 1;
        updateLastMoveMarker();
        updateMoveIndicator();
    }
    
    // --- 5. 화면 표시(DOM) 제어 함수들 ---
    function drawMove(index) {
        if (index < 0 || index >= moves.length) return;
        const move = moves[index];
        drawStone(move.xcoord, move.ycoord, move.color);
        updateLastMoveMarker();
        updateMoveIndicator();
    }

    function clearMove(index) {
        if (index < 0 || index >= moves.length) return;
        const move = moves[index];
        const td = document.querySelector(`td[data-x='\${move.xcoord}'][data-y='\${move.ycoord}']`);
        if (td) td.innerHTML = '';
    }
    
    function clearBoard() {
        boardElement.querySelectorAll('.stone').forEach(s => s.remove());
    }
    
    function updateMoveIndicator() {
        moveIndicator.innerText = `\${currentMoveIndex + 1} / \${moves.length} 수`;
    }

    function updateLastMoveMarker() {
        document.querySelectorAll('.stone.last-move').forEach(s => s.classList.remove('last-move'));
        if (currentMoveIndex >= 0) {
            const move = moves[currentMoveIndex];
            const td = document.querySelector(`td[data-x='\${move.xcoord}'][data-y='\${move.ycoord}']`);
            if (td && td.firstChild) {
                td.firstChild.classList.add('last-move');
            }
        }
    }
    
    function drawStone(x, y, color) {
        const td = document.querySelector(`td[data-x='\${x}'][data-y='\${y}']`);
        if (td && td.children.length === 0) {
            const stone = document.createElement('div');
            stone.className = `stone \${color.toLowerCase()}`;
            td.appendChild(stone);
        }
    }
</script>
</body>
</html>