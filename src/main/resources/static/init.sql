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
