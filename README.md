# 오목 게임 프로젝트 (Gomoku Game Project)

## 1. 프로젝트 개요

이 프로젝트는 웹 기반의 실시간 온라인 오목 게임입니다.  
사용자들은 회원가입 및 로그인을 통해 다른 사용자들과 오목을 두거나, 인공지능(AI)을 상대로 플레이할 수 있습니다.  
게임 관전, 기록 다시보기 등 다양한 부가 기능도 제공합니다.

**AI 오목 로직과 얼굴 인식 로그인 기능은 별도 저장소인 [GomokuAI](https://github.com/haejun1213/GomokuAI)에 구현되어 있습니다.**  
해당 저장소에서는 Python 기반 강화학습(DQN) AI와 DeepFace 얼굴 인식 모듈을 포함하고 있습니다.

---

## 2. 주요 기능

### 사용자 인증
- 일반 이메일/비밀번호 회원가입 및 로그인  
- Kakao 소셜 로그인  
- 얼굴 인식을 이용한 로그인 (DeepFace 기반, [GomokuAI 저장소](https://github.com/haejun1213/GomokuAI) 참고)

### 게임 플레이
- **실시간 멀티플레이**: WebSocket을 활용하여 다른 사용자와 실시간으로 대전  
- **AI 대전**: 인공지능(Bot)을 상대로 오목 플레이 ([GomokuAI 저장소](https://github.com/haejun1213/GomokuAI) 참고)  
- **게임방 관리**: 게임방 생성(공개/비공개), 목록 조회 및 참여 기능  
- **관전 기능**: 진행 중인 게임 관전 가능  

### 부가 기능
- 게임 기록 및 다시보기  
- 사용자 프로필(전적 확인, 정보 수정)  
- 비매너 사용자 신고 기능  

### 관리자 기능
- 사용자 목록 조회 및 관리  
- 전체 게임방 목록 조회  
- 신고 내역 관리  

---

## 3. 기술 스택

**Backend**
- Java 21  
- Spring MVC 6.2.5  
- Spring WebSocket  
- Spring Security (Crypto)  
- MyBatis 3  
- Lombok  

**Frontend**
- JSP (Jakarta Server Pages)  
- JSTL (Jakarta Standard Tag Library)  
- JavaScript  
- CSS  

**Database**
- Oracle Database  

**Build Tool**
- Apache Maven  

**Server**
- Apache Tomcat  

---

## 4. 데이터베이스 스키마

주요 테이블은 다음과 같습니다.

- `USERS`: 사용자 정보 (소셜 로그인, 얼굴 인식 데이터 포함)  
- `GAME_ROOMS`: 게임방 정보 (방 제목, 호스트, 상태 등)  
- `ROOM_PARTICIPANTS`: 게임방 참여자 정보  
- `GAME_HISTORY`: 대국 기록 (흑/백 플레이어, 승자, 시간 등)  
- `GAME_MOVES`: 대국의 각 수에 대한 기보 정보  
- `ROOM_CHAT`: 게임방 채팅 내역  
- `USER_REPORTS`: 사용자 신고 내역  
