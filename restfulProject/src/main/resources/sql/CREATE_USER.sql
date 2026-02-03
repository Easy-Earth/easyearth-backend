-- ==================================================
-- [DB 사용자 생성 스크립트]
-- 이 스크립트는 'SYSTEM' 또는 'ADMIN' 계정으로 실행해야 합니다!
-- ==================================================

-- 1. 기존 사용자가 있다면 삭제 (실수로 지우는 것을 방지하려면 주석 처리하세요)
-- DROP USER EASYEARTH CASCADE;

-- 2. 사용자 생성 (아이디: EASYEARTH / 비번: EASYEARTH)
-- 12c, 19c 이상 버전에서 공통 계정 생성 에러 방지를 위해 c## 생략 설정
ALTER SESSION SET "_ORACLE_SCRIPT"=true;

CREATE USER EASYEARTH IDENTIFIED BY EASYEARTH DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;

-- 3. 권한 부여
GRANT CONNECT, RESOURCE TO EASYEARTH;
GRANT CREATE VIEW, CREATE SEQUENCE, CREATE TRIGGER TO EASYEARTH;
GRANT CREATE SESSION TO EASYEARTH;
GRANT CREATE TABLE TO EASYEARTH;

-- 완료 메시지 확인
SELECT username, account_status FROM dba_users WHERE username = 'EASYEARTH';
