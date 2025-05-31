/* root 계정으로 접속, DB2025Team00 데이터베이스 생성, DB2025Team00 계정 생성 */
/* MySQL Workbench에서 초기화면에서 +를 눌러 root connection을 만들어 접속한다. */
DROP DATABASE IF EXISTS DB2025Team05;
DROP USER IF EXISTS DB2025Team05@localhost;
CREATE USER DB2025Team05@localhost IDENTIFIED WITH mysql_native_password BY 'DB2025Team05';
CREATE DATABASE DB2025Team05;
GRANT ALL PRIVILEGES ON DB2025Team05.* TO DB2025Team05@localhost WITH GRANT OPTION;
COMMIT;

USE DB2025Team05;

/* user 테이블 */
CREATE TABLE DB2025_USER (
    id integer primary key auto_increment,
    name varchar(10) not null,
    role varchar(9) not null,
    email varchar(30) unique
);

/* company 테이블, 회사정보가 있는 user의 회사정보 */
CREATE TABLE DB2025_COMPANY_INFO (
    id integer primary key auto_increment,
    user_id INTEGER,
    business_number varchar(12) unique,
    company_name varchar(30) not null,
    representative_name varchar(10) not null,
    representative_phone varchar(13) not null,
    address varchar(100),
    foreign key (user_id) references DB2025_USER(id) on delete cascade on update cascade
);

/* 팝업 관리 테이블 */
CREATE TABLE DB2025_POPUP_MANAGEMENT (
    id integer primary key auto_increment,
    user_id integer,
    name varchar(50),
    address varchar(50) not null,
    start_date date,
    end_date date,
    foreign key (user_id) references DB2025_USER(id) on delete cascade on update cascade
);

/* 폐기물 관리 테이블 */
CREATE TABLE DB2025_WASTE (
    id integer primary key auto_increment,
    amount integer,
    type varchar(50)
);   

/* 철거 기록 테이블 */
CREATE TABLE DB2025_DISPOSAL_RECORD (
    id integer primary key auto_increment,
    user_id integer, /* 폐기물 처리 업체의 user id */
    popup_id integer,
    waste_id integer,
    status varchar(20),
    disposal_date datetime,
    foreign key (user_id) references DB2025_USER(id) on delete cascade on update cascade,
    foreign key (popup_id) references DB2025_POPUP_MANAGEMENT(id) on delete cascade on update cascade,
    foreign key (waste_id) references DB2025_WASTE(id) on delete cascade on update cascade
);

/* 팝업 & 관리 회사 정보 통합 조회용 뷰 */
CREATE VIEW DB2025_POPUP_COMPANY_VIEW 
as select 
pm.id as popup_id,
pm.name as popup_name,
ci.business_number,
ci.company_name,
ci.representative_name,
ci.representative_phone,
ci.address as company_address
from DB2025_POPUP_MANAGEMENT pm
join DB2025_COMPANY_INFO ci on pm.user_id=ci.user_id;

/* 폐기물 처리 통합 조회용 뷰 */
CREATE VIEW DB2025_DISPOSAL_VIEW
as select
ci.company_name,
pm.name as popup_name,
w.amount,
w.type,
dr.status,
dr.disposal_date
from DB2025_DISPOSAL_RECORD dr
join DB2025_POPUP_MANAGEMENT pm on dr.popup_id=pm.id
join DB2025_COMPANY_INFO ci on dr.user_id=ci.user_id
join DB2025_WASTE w on dr.waste_id=w.id;


/* 팝업스토어 이름 인덱스 */
CREATE INDEX DB2025_POPUP_NAME_INDEX ON DB2025_POPUP_MANAGEMENT(name);
/* 철거일자 인덱스 (월별 통계, 검색 성능 향상) */
CREATE INDEX DB2025_REMOVE_DATE ON DB2025_DISPOSAL_RECORD(disposal_date);
/* 배출자 이메일 인덱스 (join 및 조건검색 최적화) */
CREATE INDEX DB2025_PRODUCER_EMAIL ON DB2025_USER(email);
/* 폐기물 종류 인덱스 (통계 집계용) */
CREATE INDEX DB2025_WASTE_TYPE ON DB2025_WASTE(type);


/* 데이터 삽입 */
INSERT INTO DB2025_USER (name, role, email) 
VALUES ("김민지", "producer", "minji@example.com"), 
("박서은", "processor", "seoun@example.com"), 
("김정수", "manager", "jeongsu@example.com"), 
("서현지", "producer", "hyeonji@example.com"),
("김은빈", "manager", "eunbin@example.com"),
("박은서", "producer", "eunseo@example.com"),
("김지민", "producer", "jimin@example.com"),
("서지현", "processor", "jihyeon@example.com"),
("김수정", "manager", "sujeong@example.com"),
("김은비", "producer", "eunbee@example.com");

INSERT INTO DB2025_COMPANY_INFO (user_id, business_number, company_name, representative_name, representative_phone, address) 
VALUES (1, "204958329392", "맛있는쿠키", "김민지", "01000000000", "서울특별시 서대문구"), 
(2, "204958329393", "서은클린", "박서은", "01000000001", "경기도 이천시"), 
(4, "204958329394", "샤넬메이크업", "서현지", "01000000002", "대구광역시 수성구"), 
(6, "204958329395", "향긋한커피", "박은서", "01000000003", "대전광역시 중구"), 
(7, "204958329396", "디올코스매틱", "김지민", "01000000004", "제주특별자치도 서귀포시"),
(8, "204958329397", "깨끗한처리", "서지현", "01000000005", "경상북도 안동군"),
(10, "204958329398", "감자튀김킹", "김은비", "01000000006", "강원도 춘천시");

INSERT INTO DB2025_POPUP_MANAGEMENT(user_id, name, address, start_date, end_date)
VALUES (1, "쿠키 신메뉴 시식전", "서울특별시 마포구 홍익로 23", '2025-05-01', '2025-05-03'),
(4, "샤넬 메이크업 2025 F/W", "서울특별시 서대문구 연희로 45", '2025-04-09', '2025-04-30'),
(6, "향긋한커피 원두 박람회", "부산광역시 해운대구 센텀중앙로 97", '2025-03-17', '2025-03-20'),
(7, "디올리비에라", "인천광역시 남동구 인하로 100", '2025-06-03', '2025-06-04'),
(10, "감튀월드", "대구광역시 중구 동성로 3길 15", '2025-07-01', '2025-07-03');

INSERT INTO DB2025_WASTE(amount, type)
VALUES (5, "plastic"),
(3, "paper"),
(8, "metal"),
(5, "glass"),
(3, "organic");

INSERT INTO DB2025_DISPOSAL_RECORD(user_id, popup_id, waste_id, status, disposal_date)
VALUES (1, 1, 1, "pending", '2025-05-04'),
(3, 2, 2, "processing", '2025-05-07'),
(1, 1, 3, "completed", '2025-05-10'),
(6, 3, 4, "cancelled", '2025-05-20'),
(6, 3, 5, "processing", '2025-05-27');