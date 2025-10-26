-- 제약 이름 붙이고 권한부여해서 테이블 만들어 보기
-- 자동증가 시퀀스도 만들기
-- 데이터 10개 입력 하기
CREATE TABLE MEMBER (
                        id NUMBER,
                        userid varchar2(100),
                        username varchar2(100)
		CONSTRAINT member_username_nn NOT NULL,
                        useremail varchar2(100)
		CONSTRAINT member_useremail_nn_unq NOT NULL UNIQUE,
                        CONSTRAINT member_id_userid_pk PRIMARY KEY (id,userid)
);
CREATE SEQUENCE member_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 99999999999999
    MINVALUE 1
    nocycle;
INSERT INTO MEMBER (id,userid, username,useremail) VALUES (member_seq.nextval,'jjang051', '장성호', 'jjang051@hanmail.net');
INSERT INTO MEMBER (id,userid, username,useremail) VALUES (member_seq.nextval,'hong', '홍길동', 'hong@hanmail.net');
INSERT INTO MEMBER (id,userid, username,useremail) VALUES (member_seq.nextval,'kim', '김유신', 'kim@hanmail.net');
COMMIT;
SELECT * FROM MEMBER;

ALTER TABLE MEMBER
    ADD userpw varchar2(100) DEFAULT '1234'
CONSTRAINT member_userpw_nn NOT NULL;

SELECT * FROM MEMBER WHERE userid = 'jjang051' AND userpw = '1234';


CREATE TABLE board  (
                        id NUMBER CONSTRAINT board_id_pk PRIMARY KEY,
                        writer  varchar2(100) CONSTRAINT board_writer_nn NOT NULL,
                        title   varchar2(3000) CONSTRAINT board_title_nn NOT NULL,
                        content varchar2(3000) CONSTRAINT board_content_nn NOT NULL,
                        regdate DATE DEFAULT sysdate,
                        hit NUMBER,
                        password varchar2(100)
);

CREATE SEQUENCE board_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 99999999999999
    MINVALUE 1
    nocycle;
insert into board(id,title,content,writer,regdate,hit,password) values
    (board_seq.nextval,'테스트','테스트','테스트유저',sysdate,1,'1234');
select * FROM board;


-- 새로 만든 sql table

CREATE TABLE MEMBER (
                        id        NUMBER CONSTRAINT member_id_pk PRIMARY KEY,
                        userid    VARCHAR2(100),
                        username  VARCHAR2(100),
                        useremail VARCHAR2(100) CONSTRAINT member_email_uk UNIQUE,
                        userpw    VARCHAR2(100),
                        regdate   DATE DEFAULT SYSDATE
);

CREATE SEQUENCE member_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 9999999999
    MINVALUE 1
    NOCYCLE;

CREATE TABLE BOARD (
                       id           NUMBER PRIMARY KEY,
                       member_id    NUMBER CONSTRAINT board_member_fk REFERENCES MEMBER(id),
                       title        VARCHAR2(3000),
                       content      VARCHAR2(3000),
                       secret_flag  NUMBER(1) DEFAULT 0
                 CONSTRAINT board_secret_ck CHECK (secret_flag IN (0, 1)),
                       regdate      DATE DEFAULT SYSDATE,
                       hit          NUMBER DEFAULT 0
);


CREATE SEQUENCE board_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 9999999999
    MINVALUE 1
    NOCYCLE;

INSERT INTO member (id, userid, username, useremail, userpw)
VALUES (member_seq.NEXTVAL, 'ghost', '장세영', 'ghost@gmail.com', '1234');

INSERT INTO member (id, userid, username, useremail, userpw)
VALUES (member_seq.NEXTVAL, 'ghost1', '장세영1', 'ghost1@gmail.com', '1234');

INSERT INTO member (id, userid, username, useremail, userpw)
VALUES (member_seq.NEXTVAL, 'test', 'test', 'test@gmail.com', '1234');

INSERT INTO board (id, member_id, title, content, secret_flag)
VALUES (board_seq.NEXTVAL, 1, '테스트글', 'FK 테스트 내용', 0);
INSERT INTO board (id, member_id, title, content, secret_flag)
VALUES (board_seq.NEXTVAL, 1, '테스트글2', 'FK 테스트 내용2', 0);
INSERT INTO board (id, member_id, title, content, secret_flag)
VALUES (board_seq.NEXTVAL, 1, '테스트글3', 'FK 테스트 내용3', 0);
INSERT INTO board (id, member_id, title, content, secret_flag)
VALUES (board_seq.NEXTVAL, 1, '테스트글4', 'FK 테스트 내용3 삭제용', 0);

SELECT * FROM MEMBER;
SELECT * FROM board;

COMMIT;

SELECT b.id, b.title, b.content, b.regdate, b.hit, m.username AS writer
FROM board b
         JOIN member m ON b.member_id = m.id;

SELECT b.id, b.member_id, m.id AS member_id_in_member
FROM board b
         LEFT JOIN member m ON b.member_id = m.id;

BEGIN
FOR i IN 1..200 LOOP
        INSERT INTO board (
            id,
            member_id,
            title,
            content,
            secret_flag,
            regdate,
            hit
        )
        VALUES (
            board_seq.NEXTVAL,
            1,  -- 테스트용 작성자 ID (member.id=1 기준)
            '테스트 게시글 ' || i,
            '이것은 테스트용 게시글 내용입니다. 번호: ' || i,
            0,
            SYSDATE - DBMS_RANDOM.VALUE(0, 30),  -- 최근 30일 내 랜덤 날짜
            TRUNC(DBMS_RANDOM.VALUE(0, 500))     -- 랜덤 조회수
        );
END LOOP;
COMMIT;
END;
/

BEGIN
FOR i IN 1..200 LOOP
        INSERT INTO board (
            id,
            member_id,
            title,
            content,
            secret_flag,
            regdate,
            hit
        )
        VALUES (
            board_seq.NEXTVAL,
            TRUNC(DBMS_RANDOM.VALUE(1, 6)),  -- 🔹 1~5 사이의 정수 (랜덤 member_id)
            '테스트 게시글 ' || i,
            '이것은 테스트용 게시글 내용입니다. 번호: ' || i,
            0,
            SYSDATE - DBMS_RANDOM.VALUE(0, 30),  -- 최근 30일 내 랜덤 날짜
            TRUNC(DBMS_RANDOM.VALUE(0, 500))     -- 0~500 사이의 조회수
        );
END LOOP;
COMMIT;
END;
/

SELECT * FROM board WHERE SECRET_FLAG = 1;

ALTER TABLE board ADD secret_pw VARCHAR2(12);

UPDATE board SET secret_pw = '1234' WHERE secret_pw IS NULL;
COMMIT;

ALTER TABLE board MODIFY secret_pw NOT NULL;

ALTER TABLE board MODIFY secret_pw VARCHAR2(100) DEFAULT '1234';

SELECT * FROM MEMBER;

ALTER TABLE MEMBER
    ADD CONSTRAINT members_userid_unq UNIQUE (userid);