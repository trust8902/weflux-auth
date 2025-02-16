-- V1__Init.sql

CREATE TABLE IF NOT EXISTS member (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    auth_token VARCHAR(255) NOT NULL
);

COMMENT ON TABLE member IS '사용자';
COMMENT ON COLUMN member.id IS '사용자 ID';
COMMENT ON COLUMN member.username IS '사용자명';
COMMENT ON COLUMN member.password IS '비밀번호';
COMMENT ON COLUMN member.name IS '이름';
COMMENT ON COLUMN member.auth_token IS '인증 토큰';
