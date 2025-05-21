DROP DATABASE IF EXISTS DB2025Team05;
DROP USER IF EXISTS DB2025Team05@localhost;
CREATE USER DB2025Team05@localhost IDENTIFIED WITH mysql_native_password BY 'DB2025Team05';
CREATE DATABASE DB2025Team05;
GRANT ALL PRIVILEGES ON DB2025Team05.* TO DB2025Team05@localhost WITH GRANT OPTION;
COMMIT;

USE DB2025Team05;

CREATE TABLE DB2025_USER (
    user_id integer primary key,
    name varchar(10) not null,
    role varchar(9) not null,
    email varchar(30) unique,
)

CREATE TABLE DB2025_COMPANY_INFO (
    business_id varchar(12) primary key,
    user_id INTEGER,
    company_name varchar(30) not null,
    ceo_name varchar(10) not null,
    ceo_contact varchar(13) not null,
    foreign key (user_id) references DB2025_USER(user_id) on delete cascade on update cascade
)

CREATE TABLE DB2025_POPUP_MANAGEMENT (
    popup_id integer primary key,
    user_id integer,
    popup_start_date date,
    popup_end_date date,
    popup_address varchar(50) not null,
    popup_name varchar(50),
    foreign key (user_id) references DB2025_USER(user_id) on delete cascade on update cascade
)

CREATE TABLE DB2025_WASTE (
    waste_id integer primary key,
    waste_amount integer,
    waste_type varchar
)
    

CREATE TABLE DB2025_DISPOSAL_RECORD (
    disposal_record_id integer primary key,
    user_id integer, 
    popup_id integer,
    waste_id integer,
    status varchar,
    remover_date datetime,
    foreign key (user_id) references DB2025_DB_USER(user_id) on delete cascade on update cascade,
    foreign key (popup_id) references DB2025_POPUP_MANAGEMENT(popup_id) on delete cascade on update cascade,
    foreign key (waste_id) references DB2025_WASTE(waste_id) on delete cascade on update cascade
)

CREATE VIEW DB2025_POPUP_COMPANY_VIEW 
as select 
pm.popup_id, 
pm.popup_address,
pm.popup_start_date,
pm.popup_end_date,
u.name as user_name,
ci.company_name
from DB2025_POPUP_MANAGEMENT pm
join DB2025_USER u on pm.user_id=u.user_id
join DB2025_COMPANY_INFO ci on u.user_id=ci.user_id;

CREATE VIEW DB2025_DISPOSAL_COMPANY_VIEW 
as select
dr.disposal_record_id,
dr.user_id,
u.name as user_name,
ci.company_name,
dr.popup_id,
dr.status,
dr.remover_date
from DB2025_DISPOSAL_RECORD dr
join DB2025_USER u on dr.user_id=u.user_id
join DB2025_COMPANY_INFO ci on u.id=ci.user_id;

-- 팝업스토어 이름 인덱스
CREATE INDEX idx_popup_name ON DB2025_POPUP_MANAGEMENT(popup_name);
-- 철거일자 인덱스 (월별 통계, 검색 성능 향상)
CREATE INDEX idx_remove_date ON DB2025_DISPOSAL_RECORD(remover_date);
-- 배출자 이메일 인덱스 (join 및 조건검색 최적화)
CREATE INDEX idx_producer_email ON DB2025_USER(email);
-- 폐기물 종류 인덱스 (통계 집계용)
CREATE INDEX idx_waste_type ON DB2025_WASTE(waste_type);