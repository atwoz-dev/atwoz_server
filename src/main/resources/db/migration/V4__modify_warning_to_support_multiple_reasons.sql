-- 기존 warnings 테이블의 reasonType 컬럼 제거 및 새로운 warning_reasons 테이블 생성

-- 1. 기존 reasonType 컬럼 제거
ALTER TABLE warnings DROP COLUMN reasonType;

-- 2. warning_reasons 테이블 생성 (다중 경고 사유를 위한 별도 테이블)
CREATE TABLE warning_reasons
(
    warning_id  BIGINT      NOT NULL,
    reason_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (warning_id) REFERENCES warnings (id) ON DELETE CASCADE,
    INDEX       idx_warning_id (warning_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;