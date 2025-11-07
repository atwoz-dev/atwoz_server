# AWS 운영 서버 세팅 가이드 (DEEPPLE)

## 📋 목차

1. [환경 구성 개요](#1-환경-구성-개요)
2. [AWS 계정 및 기본 설정](#2-aws-계정-및-기본-설정)
3. [네트워크 구성 (VPC)](#3-네트워크-구성-vpc)
4. [데이터베이스 구성 (RDS)](#4-데이터베이스-구성-rds)
5. [캐시 서버 구성 (ElastiCache)](#5-캐시-서버-구성-elasticache)
6. [스토리지 구성 (S3)](#6-스토리지-구성-s3)
7. [컴퓨팅 리소스 (EC2)](#7-컴퓨팅-리소스-ec2)
8. [로드 밸런서 (ALB)](#8-로드-밸런서-alb)
9. [도메인 및 SSL 인증서](#9-도메인-및-ssl-인증서)
10. [CI/CD 파이프라인 구성](#10-cicd-파이프라인-구성)
11. [모니터링 및 로깅](#11-모니터링-및-로깅)
12. [백업 및 재해 복구](#12-백업-및-재해-복구)
13. [보안 설정](#13-보안-설정)
14. [운영 배포 체크리스트](#14-운영-배포-체크리스트)
15. [비용 최적화](#15-비용-최적화)
16. [문제 해결 가이드](#16-문제-해결-가이드)

---

## 1. 환경 구성 개요

### 1.1 환경 전략

DEEPPLE 프로젝트는 3가지 환경으로 구성됩니다:

| 환경              | 브랜치       | 인프라            | 배포 방식             | 도메인                 | 목적         |
|-----------------|-----------|----------------|-------------------|---------------------|------------|
| **Local**       | feature/* | Docker Compose | 수동                | localhost:8080      | 개발자 로컬 개발  |
| **Development** | develop   | AWS (최소 사양)    | 자동 (develop 머지 시) | dev-api.deepple.com | 통합 테스트, QA |
| **Production**  | main      | AWS (고가용성)     | 자동 (main 머지 시)    | api.deepple.com     | 실제 서비스     |

### 1.2 환경별 차이점

**Local (로컬 개발)**:

- MySQL, Redis: Docker Compose로 로컬 실행
- S3: LocalStack 또는 실제 개발용 S3 버킷
- 로그 레벨: DEBUG
- Swagger: 활성화

**Development (개발 서버)**:

- EC2: t3.small (1대)
- RDS: db.t3.micro (Single-AZ)
- ElastiCache: cache.t3.micro
- 로그 레벨: INFO
- Swagger: 활성화 (인증 필요)

**Production (운영 서버)**:

- EC2: t3.large x2 (Auto Scaling)
- RDS: db.t3.large (Multi-AZ)
- ElastiCache: cache.t3.small (복제본)
- 로그 레벨: WARN
- Swagger: 비활성화

---

## 2. AWS 계정 및 기본 설정

### 2.1 AWS 계정 생성 및 IAM 설정

**개념**: IAM(Identity and Access Management)은 AWS 리소스에 대한 접근 권한을 안전하게 관리하는 서비스입니다.

#### 단계:

1. **루트 계정 보안 강화**

    ```
    - MFA(Multi-Factor Authentication) 활성화
    - 루트 계정 액세스 키 삭제
    - 강력한 비밀번호 정책 설정
    ```

2. **애플리케이션용 IAM Role 생성**

    - Role 이름: `deepple-app-role`
    - 신뢰할 수 있는 엔터티: EC2
    - 권한:
        - `AmazonS3FullAccess` (S3 업로드용)
        - `CloudWatchLogsFullAccess` (로깅용)

### 2.2 리전 선택

**권장 리전**: `ap-northeast-2` (서울)

- 이유: 한국 사용자 대상 서비스이므로 레이턴시 최소화
- 프로젝트 설정에 이미 `ap-northeast-2`로 되어 있음

---

## 3. 네트워크 구성 (VPC)

### 3.1 VPC란?

**개념**: Virtual Private Cloud는 AWS 클라우드에서 논리적으로 격리된 네트워크 공간입니다. 여러분만의 데이터 센터를 클라우드에 만드는 것과 같습니다.

### 3.2 개발/운영 환경별 VPC

**Development VPC**:

- 이름: `deepple-dev-vpc`
- CIDR: `10.1.0.0/16`
- 간소화된 구성 (비용 절감)

**Production VPC**:

- 이름: `deepple-prod-vpc`
- CIDR: `10.0.0.0/16`
- 완전한 고가용성 구성

### 3.3 Production VPC 생성

1. **VPC 생성**

    - AWS Console → VPC → Create VPC
    - 이름: `deepple-prod-vpc`
    - IPv4 CIDR: `10.0.0.0/16` (65,536개의 IP 주소)

2. **서브넷 생성**

   **퍼블릭 서브넷** (인터넷 접근 가능):

    ```
    - deepple-prod-public-subnet-1a
      - 가용 영역: ap-northeast-2a
      - CIDR: 10.0.1.0/24
      - 용도: EC2, ALB

    - deepple-prod-public-subnet-1c
      - 가용 영역: ap-northeast-2c
      - CIDR: 10.0.2.0/24
      - 용도: EC2, ALB (고가용성)
    ```

   **프라이빗 서브넷** (인터넷 직접 접근 불가):

    ```
    - deepple-prod-private-subnet-1a
      - 가용 영역: ap-northeast-2a
      - CIDR: 10.0.11.0/24
      - 용도: RDS, ElastiCache

    - deepple-prod-private-subnet-1c
      - 가용 영역: ap-northeast-2c
      - CIDR: 10.0.12.0/24
      - 용도: RDS, ElastiCache (고가용성)
    ```

3. **인터넷 게이트웨이 생성**

    - 이름: `deepple-prod-igw`
    - VPC에 연결

4. **NAT 게이트웨이 생성** (선택사항)

    - 프라이빗 서브넷이 외부로 통신해야 할 때 필요
    - 위치: 퍼블릭 서브넷
    - Elastic IP 할당
    - **비용**: 월 ~$40 (선택적)

5. **라우팅 테이블 설정**

   **퍼블릭 라우팅 테이블**:

    ```
    - 이름: deepple-prod-public-rt
    - 라우트: 0.0.0.0/0 → Internet Gateway
    - 연결: 퍼블릭 서브넷들
    ```

   **프라이빗 라우팅 테이블**:

    ```
    - 이름: deepple-prod-private-rt
    - 라우트: 0.0.0.0/0 → NAT Gateway (선택사항)
    - 연결: 프라이빗 서브넷들
    ```

---

## 4. 데이터베이스 구성 (RDS)

### 4.1 RDS란?

**개념**: Relational Database Service는 AWS가 관리하는 관계형 데이터베이스 서비스입니다. 백업, 패치, 모니터링을 자동으로 처리해줍니다.

### 4.2 RDS MySQL 인스턴스 생성 (Production)

1. **기본 설정**

    ```
    - 엔진: MySQL 8.0
    - 템플릿: 프로덕션
    - DB 인스턴스 식별자: deepple-prod-db
    - 마스터 사용자 이름: admin
    - 마스터 암호: [강력한 비밀번호 생성]
    ```

2. **인스턴스 구성**

    ```
    - 인스턴스 클래스: db.t3.medium (시작용)
      * 추후 부하에 따라 db.t3.large, db.m5.large 등으로 확장
    - 스토리지:
      * 유형: gp3 (최신, 비용 효율적)
      * 크기: 100GB (초기)
      * 자동 확장: 활성화, 최대 500GB
    ```

3. **가용성 및 내구성**

    ```
    - Multi-AZ 배포: 예 (필수!)
      * 개념: 다른 가용 영역에 대기 DB를 자동으로 복제
      * 장점: 장애 시 자동 페일오버, 무중단 서비스
    ```

4. **연결 설정**

    ```
    - VPC: deepple-prod-vpc
    - 서브넷 그룹: 프라이빗 서브넷들로 구성
    - 퍼블릭 액세스: 아니요 (보안상 중요!)
    - VPC 보안 그룹: deepple-prod-db-sg (새로 생성)
    ```

5. **보안 그룹 설정** (`deepple-prod-db-sg`)

    ```
    인바운드 규칙:
    - 유형: MySQL/Aurora (3306)
    - 소스: deepple-prod-app-sg (EC2 보안 그룹)
    ```

6. **백업 설정**

    ```
    - 자동 백업: 활성화
    - 백업 기간: 7일 (권장: 7-30일)
    - 백업 시간: 새벽 3시-4시 (트래픽 적은 시간)
    - 스냅샷 복사: 다른 리전에 복사 (재해 복구용, 선택)
    ```

7. **모니터링**

    ```
    - Enhanced Monitoring: 활성화 (60초 단위)
    - Performance Insights: 활성화 (7일 무료)
    ```

8. **데이터베이스 초기화**

    ```bash
    # Bastion 호스트 또는 VPN을 통해 접속
    mysql -h deepple-prod-db.xxxxx.ap-northeast-2.rds.amazonaws.com -u admin -p

    # 데이터베이스 생성
    CREATE DATABASE deepple CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    # 애플리케이션용 사용자 생성
    CREATE USER 'deepple_app'@'%' IDENTIFIED BY '[강력한 비밀번호]';
    GRANT ALL PRIVILEGES ON deepple.* TO 'deepple_app'@'%';
    FLUSH PRIVILEGES;
    ```

### 4.3 RDS 엔드포인트 확인

```
라이터(쓰기) 엔드포인트: deepple-prod-db.xxxxx.ap-northeast-2.rds.amazonaws.com
리더(읽기) 엔드포인트: deepple-prod-db-ro.xxxxx.ap-northeast-2.rds.amazonaws.com
```

### 4.4 Flyway 마이그레이션 확인 (DEEPPLE 특화)

**중요**: DEEPPLE 프로젝트는 Flyway를 사용하여 데이터베이스 스키마를 관리합니다.

```bash
# RDS 접속 후 마이그레이션 상태 확인
mysql -h deepple-prod-db.xxxxx.ap-northeast-2.rds.amazonaws.com -u deepple_app -p

USE deepple;
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;

# 예상 결과:
# - V1__insert_default_interview_questions.sql
# - V2__instert_default_missions.sql
# - V3__insert_default_dating_exams.sql
```

**주의사항**:

- 운영 환경에서는 `JPA_DDL_AUTO=validate`만 사용
- 스키마 변경은 반드시 Flyway 마이그레이션 스크립트로 관리
- 롤백 스크립트도 함께 준비

---

## 5. 캐시 서버 구성 (ElastiCache)

### 5.1 ElastiCache란?

**개념**: 인메모리 캐시 서비스로, Redis나 Memcached를 관리형으로 제공합니다. 데이터베이스 부하를 줄이고 응답 속도를 높입니다.

### 5.2 ElastiCache Redis 클러스터 생성

1. **기본 설정**

    ```
    - 클러스터 엔진: Redis
    - 위치: AWS 클라우드
    - 클러스터 모드: 비활성화 (간단한 구성)
    - 이름: deepple-prod-redis
    - 엔진 버전: 7.0 (최신 안정 버전)
    ```

2. **노드 구성**

    ```
    - 노드 유형: cache.t3.small (운영 권장)
      * 개발: cache.t3.micro
    - 복제본 수: 1개 (읽기 확장 및 고가용성)
    ```

3. **고급 설정**

    ```
    - 서브넷 그룹: 프라이빗 서브넷들
    - Multi-AZ: 활성화 (자동 장애 조치)
    - 보안 그룹: deepple-prod-redis-sg (새로 생성)
    ```

4. **보안 그룹 설정** (`deepple-prod-redis-sg`)

    ```
    인바운드 규칙:
    - 유형: 사용자 지정 TCP (6379)
    - 소스: deepple-prod-app-sg (EC2 보안 그룹)
    ```

5. **보안 설정**

    ```
    - 전송 중 암호화: 활성화
    - 저장 데이터 암호화: 활성화
    - AUTH 토큰: 설정 (비밀번호 보호)
    ```

6. **백업 설정**

    ```
    - 자동 백업: 활성화
    - 백업 기간: 3일
    ```

### 5.3 Redis 엔드포인트 확인

```
기본 엔드포인트: deepple-prod-redis.xxxxx.cache.amazonaws.com:6379
읽기 엔드포인트: deepple-prod-redis-ro.xxxxx.cache.amazonaws.com:6379
```

---

## 6. 스토리지 구성 (S3)

### 6.1 S3란?

**개념**: Simple Storage Service는 객체 스토리지 서비스로, 이미지, 동영상 등의 파일을 저장합니다. 무제한 용량에 고가용성을 제공합니다.

### 6.2 S3 버킷 생성

1. **버킷 생성**

    ```
    - 버킷 이름: deepple-prod-storage (전세계 고유해야 함)
    - 개발용: deepple-dev-storage
    - 리전: ap-northeast-2
    - 객체 소유권: ACL 비활성화
    - 퍼블릭 액세스 차단: 모두 차단 (보안)
    ```

2. **버저닝 설정**

    ```
    - 버전 관리: 활성화
    - 이유: 실수로 파일 삭제/변경 시 복구 가능
    ```

3. **암호화 설정**

    ```
    - 기본 암호화: SSE-S3 (Amazon S3 관리형 키)
    - 버킷 키: 활성화 (비용 절감)
    ```

4. **수명 주기 정책** (비용 절감)

    ```
    규칙 1: 오래된 버전 삭제
    - 이전 버전은 90일 후 삭제

    규칙 2: 미완료 멀티파트 업로드 정리
    - 7일 후 자동 삭제
    ```

5. **CORS 설정** (웹/앱에서 접근 시)

    ```json
    [
      {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
        "AllowedOrigins": ["https://yourdomain.com"],
        "ExposeHeaders": ["ETag"],
        "MaxAgeSeconds": 3000
      }
    ]
    ```

6. **CloudFront CDN 연동** (선택사항, 권장)

    - **개념**: CDN(Content Delivery Network)은 전세계에 파일을 캐싱하여 빠른 전송을 제공
    - S3를 오리진으로 CloudFront 배포 생성
    - 이점: 속도 향상, S3 비용 절감, DDoS 보호

### 6.3 IAM 정책 설정

애플리케이션이 S3에 접근하도록 EC2 IAM Role에 권한 추가:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::deepple-prod-storage",
        "arn:aws:s3:::deepple-prod-storage/*"
      ]
    }
  ]
}
```

---

## 7. 컴퓨팅 리소스 (EC2)

### 7.1 EC2란?

**개념**: Elastic Compute Cloud는 가상 서버입니다. 여러분의 애플리케이션이 실행되는 컴퓨터라고 생각하면 됩니다.

### 7.2 보안 그룹 생성

**애플리케이션 보안 그룹** (`deepple-prod-app-sg`):

```
인바운드 규칙:
1. SSH (22) - 소스: 관리자 IP만 (보안상 중요!)
2. HTTP (80) - 소스: deepple-prod-alb-sg (로드 밸런서)
3. HTTPS (443) - 소스: deepple-prod-alb-sg
4. Custom (8080) - 소스: deepple-prod-alb-sg (Spring Boot 포트)

아웃바운드 규칙:
- 모든 트래픽 허용 (0.0.0.0/0)
```

### 7.3 EC2 인스턴스 생성

1. **AMI 선택**

    ```
    - Amazon Linux 2023 또는 Ubuntu 22.04 LTS
    - 64비트 (x86)
    ```

2. **인스턴스 유형**

    ```
    Production:
    - 시작: t3.medium (2 vCPU, 4GB RAM)
    - 추천: t3.large (2 vCPU, 8GB RAM)

    Development:
    - t3.small (2 vCPU, 2GB RAM)
    ```

3. **키 페어**

    ```
    - 새 키 페어 생성: deepple-prod-key
    - 유형: RSA
    - 형식: .pem
    - 다운로드 후 안전하게 보관! (분실 시 서버 접속 불가)
    ```

4. **네트워크 설정**

    ```
    - VPC: deepple-prod-vpc
    - 서브넷: deepple-prod-public-subnet-1a
    - 퍼블릭 IP 자동 할당: 활성화
    - 보안 그룹: deepple-prod-app-sg
    ```

5. **스토리지 구성**

    ```
    - 루트 볼륨: 30GB, gp3
    - 추가 볼륨: 필요시 데이터용 50GB
    ```

6. **고급 세부 정보**

    ```
    - IAM 인스턴스 프로파일: deepple-app-role
    - 사용자 데이터 (초기 설정 스크립트):
    ```

    ```bash
    #!/bin/bash
    # 시스템 업데이트
    yum update -y  # Ubuntu는 apt update -y && apt upgrade -y

    # Docker 설치
    yum install -y docker
    systemctl start docker
    systemctl enable docker
    usermod -aG docker ec2-user  # Ubuntu는 ubuntu

    # Docker Compose 설치
    curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose

    # CloudWatch Logs 에이전트 설치 (선택)
    wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
    rpm -U ./amazon-cloudwatch-agent.rpm
    ```

### 7.4 EC2 초기 설정

SSH로 접속 후:

```bash
# 키 파일 권한 설정 (로컬)
chmod 400 deepple-prod-key.pem

# SSH 접속
ssh -i deepple-prod-key.pem ec2-user@[EC2_PUBLIC_IP]

# Docker 확인
docker --version
docker-compose --version

# 작업 디렉토리 생성
mkdir -p /home/ec2-user/deepple
cd /home/ec2-user/deepple
```

### 7.5 DEEPPLE 프로젝트 필수 파일 배치

**중요**: DEEPPLE 프로젝트는 Firebase와 App Store 인증서 파일이 필요합니다.

#### 1. Firebase 인증서 배치

```bash
# EC2에서 실행
mkdir -p /home/ec2-user/secrets
chmod 700 /home/ec2-user/secrets

# 로컬에서 파일 전송
scp -i deepple-prod-key.pem firebase-adminsdk.json ec2-user@[EC2_IP]:/home/ec2-user/secrets/

# EC2에서 권한 설정
chmod 400 /home/ec2-user/secrets/firebase-adminsdk.json
```

#### 2. App Store 인증서 배치

```bash
# EC2에서 실행
mkdir -p /home/ec2-user/certs/appstore
chmod 755 /home/ec2-user/certs
chmod 755 /home/ec2-user/certs/appstore

# 로컬에서 파일 전송
scp -i deepple-prod-key.pem AppleRootCA-G2.pem ec2-user@[EC2_IP]:/home/ec2-user/certs/appstore/
scp -i deepple-prod-key.pem AppleRootCA-G3.pem ec2-user@[EC2_IP]:/home/ec2-user/certs/appstore/

# EC2에서 권한 설정
chmod 444 /home/ec2-user/certs/appstore/*.pem
```

#### 3. .env 파일 생성

```bash
# EC2에서 실행
nano /home/ec2-user/.env
```

**.env 파일 내용** (운영 환경):

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Server
SERVER_PORT=8080
APP_HOST_PORT=8080
APP_CONTAINER_PORT=8080

# MySQL (RDS 엔드포인트)
MYSQL_HOST=deepple-prod-db.xxxxx.ap-northeast-2.rds.amazonaws.com
MYSQL_PORT=3306
MYSQL_DATABASE=deepple
MYSQL_USER=deepple_app
MYSQL_PASSWORD=[RDS 비밀번호]

# JPA
JPA_DDL_AUTO=validate  # 중요: 운영은 validate만!
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false

# Flyway (데이터베이스 마이그레이션)
FLYWAY_ENABLED=true

# Redis (ElastiCache 엔드포인트)
REDIS_HOST=deepple-prod-redis.xxxxx.cache.amazonaws.com
REDIS_PORT=6379
REDIS_PASSWORD=[Redis AUTH 토큰]
REDIS_SSL_ENABLED=true

# AWS S3
AWS_S3_ACCESS_KEY=[EC2 IAM Role 사용시 불필요]
AWS_S3_SECRET_KEY=[EC2 IAM Role 사용시 불필요]
AWS_S3_BUCKET_NAME=deepple-prod-storage

# JWT (보안상 매우 중요! 강력한 값으로 변경)
JWT_SECRET=[256비트 이상의 랜덤 문자열]
JWT_ACCESS_TOKEN_EXPIRATION=1800
JWT_REFRESH_TOKEN_EXPIRATION=1209600

# Auth
AUTH_PREFIX_CODE=[인증 코드]

# Swagger (운영에서는 비활성화 권장)
SPRINGDOC_ENABLED=false
SWAGGER_UI_ENABLED=false

# App Store (프로덕션 설정)
APP_STORE_KEY_ID=[실제 키 ID]
APP_STORE_PRIVATE_KEY_STRING=[실제 Private Key]
APP_STORE_ISSUER_ID=[실제 Issuer ID]
APP_STORE_ENVIRONMENT=Production
APP_STORE_BUNDLE_ID=[실제 Bundle ID]
APP_STORE_APP_APPLE_ID=[실제 App ID]
APP_STORE_ROOT_CA_G2_PATH=/etc/certs/appstore/AppleRootCA-G2.pem
APP_STORE_ROOT_CA_G3_PATH=/etc/certs/appstore/AppleRootCA-G3.pem
APP_STORE_BASE_URL=https://api.storekit.itunes.apple.com

# Bizgo (SMS 발송)
BIZGO_API_URL=[실제 API URL]
BIZGO_CLIENT_ID=[실제 Client ID]
BIZGO_CLIENT_PASSWORD=[실제 Password]
BIZGO_FROM_PHONE_NUMBER=[실제 발신번호]

# Firebase (푸시 알림)
GOOGLE_APPLICATION_CREDENTIALS=/etc/credentials/firebase-adminsdk.json
```

**JWT 시크릿 생성**:

```bash
# 강력한 랜덤 시크릿 생성 (256비트)
openssl rand -base64 32
```

**파일 권한 설정**:

```bash
chmod 600 /home/ec2-user/.env
```

### 7.6 로컬에서 운영 환경 테스트

**운영 배포 전 로컬에서 테스트하는 방법**:

1. **로컬 테스트용 .env 파일 생성**

    ```bash
    # 프로젝트 루트에서
    cp .env .env.prod-test

    # .env.prod-test 수정:
    # - MYSQL_HOST를 localhost로 변경
    # - REDIS_HOST를 localhost로 변경
    # - 나머지는 운영 설정 그대로 (JWT_SECRET 등)
    ```

2. **로컬에서 MySQL, Redis 실행**

    ```bash
    # Docker Compose로 MySQL, Redis만 실행
    docker-compose up -d db redis
    ```

3. **애플리케이션 운영 모드로 실행**

    ```bash
    # .env.prod-test 파일을 .env로 복사
    cp .env.prod-test .env

    # Gradle로 실행
    ./gradlew bootRun
    ```

4. **헬스 체크 테스트**

    ```bash
    curl http://localhost:8080/actuator/health
    # 예상 결과: {"status":"UP"}
    ```

5. **주요 API 테스트**

    - 회원가입, 로그인 등 핵심 기능 테스트
    - Firebase 푸시 알림 테스트
    - App Store 인앱결제 검증 테스트

### 7.7 배포 스크립트 생성

```bash
# 배포 스크립트 생성
sudo nano /home/ec2-user/deploy_script.sh
```

```bash
#!/bin/bash

# 배포 스크립트
DOCKER_IMAGE="ggongtae/deepple"
CONTAINER_NAME="spring-app"
ENV_FILE="/home/ec2-user/.env"
LOG_FILE="/home/ec2-user/deploy.log"

echo "===== Starting Deployment $(date) =====" | tee -a $LOG_FILE

# 기존 컨테이너 중지 및 제거
echo "Stopping existing container..." | tee -a $LOG_FILE
docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true

# 최신 이미지 Pull
echo "Pulling latest image..." | tee -a $LOG_FILE
docker pull $DOCKER_IMAGE:latest

# 새 컨테이너 실행
echo "Starting new container..." | tee -a $LOG_FILE
docker run -d \
  --name $CONTAINER_NAME \
  --env-file $ENV_FILE \
  -p 8080:8080 \
  -v /home/ec2-user/secrets:/etc/credentials:ro \
  -v /home/ec2-user/certs:/etc/certs:ro \
  --log-driver=awslogs \
  --log-opt awslogs-region=ap-northeast-2 \
  --log-opt awslogs-group=/deepple/prod/application \
  --log-opt awslogs-stream=spring-app \
  --restart unless-stopped \
  $DOCKER_IMAGE:latest

# 오래된 이미지 정리
echo "Cleaning up old images..." | tee -a $LOG_FILE
docker image prune -f

# 헬스 체크
echo "Waiting for application to start..." | tee -a $LOG_FILE
sleep 15

# 헬스 체크 (최대 5분 대기)
HEALTH_CHECK_URL="http://localhost:8080/actuator/health"
MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
  ATTEMPT=$((ATTEMPT+1))
  echo "Health check attempt $ATTEMPT/$MAX_ATTEMPTS..." | tee -a $LOG_FILE

  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_CHECK_URL)

  if [ "$HTTP_CODE" = "200" ]; then
    echo "✓ Application is healthy!" | tee -a $LOG_FILE
    echo "===== Deployment Successful $(date) =====" | tee -a $LOG_FILE
    exit 0
  fi

  sleep 10
done

# 헬스 체크 실패
echo "✗ Health check failed after $MAX_ATTEMPTS attempts" | tee -a $LOG_FILE
echo "Container logs:" | tee -a $LOG_FILE
docker logs --tail 50 $CONTAINER_NAME | tee -a $LOG_FILE

echo "===== Deployment Failed $(date) =====" | tee -a $LOG_FILE
exit 1
```

```bash
# 실행 권한 부여
sudo chmod +x /home/ec2-user/deploy_script.sh
```

### 7.8 Auto Scaling 그룹 설정 (선택사항, 권장)

**개념**: 트래픽에 따라 서버를 자동으로 추가/제거하여 비용 최적화와 안정성을 동시에 확보합니다.

1. **AMI 생성** (현재 EC2를 이미지로 저장)

    - EC2 Console → 인스턴스 선택 → Actions → Image → Create Image

2. **시작 템플릿 생성**

    - 위에서 생성한 AMI 사용
    - 인스턴스 유형, 보안 그룹 등 설정

3. **Auto Scaling 그룹 생성**

    ```
    - 이름: deepple-prod-asg
    - 시작 템플릿: 위에서 생성한 템플릿
    - VPC 및 서브넷: 퍼블릭 서브넷들
    - 로드 밸런서: deepple-prod-alb (아래에서 생성)
    - 그룹 크기:
      * 원하는 용량: 2
      * 최소 용량: 2
      * 최대 용량: 4
    - 조정 정책:
      * 대상 추적 조정: CPU 사용률 70%
    ```

---

## 8. 로드 밸런서 (ALB)

### 8.1 로드 밸런서란?

**개념**: Application Load Balancer는 트래픽을 여러 서버에 분산시켜 안정성과 성능을 높입니다. SSL 인증서도 여기서 관리합니다.

### 8.2 보안 그룹 생성

**로드 밸런서 보안 그룹** (`deepple-prod-alb-sg`):

```
인바운드 규칙:
1. HTTP (80) - 소스: 0.0.0.0/0 (모든 곳)
2. HTTPS (443) - 소스: 0.0.0.0/0 (모든 곳)

아웃바운드 규칙:
- 모든 트래픽 허용
```

### 8.3 ALB 생성

1. **기본 구성**

    ```
    - 이름: deepple-prod-alb
    - 체계: Internet-facing (인터넷 연결)
    - IP 주소 유형: IPv4
    ```

2. **네트워크 매핑**

    ```
    - VPC: deepple-prod-vpc
    - 매핑: ap-northeast-2a, ap-northeast-2c (최소 2개 AZ)
    - 서브넷: 퍼블릭 서브넷들 선택
    ```

3. **보안 그룹**

    ```
    - deepple-prod-alb-sg
    ```

4. **리스너 및 라우팅**

   **HTTP 리스너 (포트 80)**:

    ```
    - 기본 작업: HTTPS로 리디렉션 (443 포트)
    ```

   **HTTPS 리스너 (포트 443)**:

    ```
    - 기본 작업: 대상 그룹으로 전달
    - SSL 인증서: ACM에서 발급 (아래 참조)
    ```

5. **대상 그룹 생성**

    ```
    - 이름: deepple-prod-tg
    - 대상 유형: 인스턴스
    - 프로토콜: HTTP
    - 포트: 8080
    - VPC: deepple-prod-vpc
    - 헬스 체크:
      * 프로토콜: HTTP
      * 경로: /actuator/health
      * 정상 임계값: 2
      * 비정상 임계값: 2
      * 제한 시간: 5초
      * 간격: 30초
    - 대상 등록: EC2 인스턴스 선택
    ```

### 8.4 Spring Boot Actuator 설정

**중요**: ALB 헬스체크가 작동하려면 Actuator 엔드포인트가 노출되어야 합니다.

`application-prod.yml` (운영 환경):

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized  # 보안상 중요!
  server:
    port: 8080  # 애플리케이션과 동일 포트
```

---

## 9. 도메인 및 SSL 인증서

### 9.1 도메인 준비

**도메인 구매** (가비아, Route 53, Cloudflare 등):

```
운영: api.deepple.com
개발: dev-api.deepple.com
```

### 9.2 Route 53 설정 (AWS DNS 서비스)

1. **호스팅 영역 생성**

    ```
    - 도메인 이름: deepple.com
    - 유형: 퍼블릭 호스팅 영역
    ```

2. **레코드 생성**

    ```
    레코드 1 (운영):
    - 이름: api.deepple.com
    - 유형: A (Alias)
    - 트래픽 라우팅 대상: ALB (deepple-prod-alb)

    레코드 2 (개발):
    - 이름: dev-api.deepple.com
    - 유형: A (Alias)
    - 트래픽 라우팅 대상: ALB (deepple-dev-alb)
    ```

3. **네임서버 설정**

    - Route 53의 NS 레코드 확인
    - 도메인 등록 업체에서 네임서버를 Route 53으로 변경

### 9.3 SSL 인증서 발급 (ACM)

**개념**: AWS Certificate Manager는 무료로 SSL/TLS 인증서를 발급하고 자동 갱신합니다.

1. **인증서 요청**

    ```
    - 리전: ap-northeast-2 (ALB와 동일 리전!)
    - 도메인 이름:
      * api.deepple.com
      * dev-api.deepple.com
      * *.deepple.com (와일드카드, 선택)
    - 검증 방법: DNS 검증 (권장)
    ```

2. **DNS 검증**

    - ACM이 제공하는 CNAME 레코드를 Route 53에 추가
    - Route 53 사용 시 "Route 53에서 레코드 생성" 버튼으로 자동 추가

3. **인증서 상태 확인**

    - 5-10분 후 "발급됨" 상태로 변경

4. **ALB에 인증서 연결**

    - ALB → 리스너 → HTTPS:443 편집
    - 기본 SSL 인증서: 위에서 발급한 인증서 선택

---

## 10. CI/CD 파이프라인 구성

### 10.1 브랜치 전략

**Git Flow 간소화 버전**:

```
main (운영 배포)
  ↑
develop (개발 배포)
  ↑
feature/기능명 (기능 개발)
```

**브랜치별 역할**:

- **main**: 운영 환경 배포용, 자동 배포, Protected
- **develop**: 개발 환경 배포용, 자동 배포
- **feature/\***: 기능 개발용, develop으로 PR

### 10.2 GitHub Secrets 설정

**개발 환경 Secrets**:

```
GitHub Repository → Settings → Secrets and variables → Actions

Development:
1. DOCKER_USERNAME: Docker Hub 사용자명
2. DOCKER_PASSWORD: Docker Hub 비밀번호 또는 액세스 토큰
3. DEV_SSH_PEM_KEY: 개발 서버 EC2 접속용 키
4. DEV_EC2_USERNAME: ec2-user (또는 ubuntu)
5. DEV_EC2_HOST: 개발 서버 EC2 IP
6. DEV_ENV: 개발 환경 .env 파일 전체 내용
```

**운영 환경 Secrets**:

```
Production:
1. PROD_SSH_PEM_KEY: 운영 서버 EC2 접속용 키
2. PROD_EC2_USERNAME: ec2-user
3. PROD_EC2_HOST: 운영 서버 EC2 IP
4. PROD_ENV: 운영 환경 .env 파일 전체 내용
```

### 10.3 GitHub Environments 설정

**중요**: 운영 배포는 반드시 수동 승인이 필요합니다.

1. **GitHub Repository → Settings → Environments**

2. **Development 환경 생성**

    ```
    - 이름: development
    - Deployment protection rules: 없음 (자동 배포)
    - Environment secrets: DEV_* 시크릿 추가
    ```

3. **Production 환경 생성**

    ```
    - 이름: production
    - Deployment protection rules:
      * Required reviewers: 최소 1명 (팀 리더, DevOps 담당자)
      * Wait timer: 0분 (또는 원하는 대기 시간)
    - Environment secrets: PROD_* 시크릿 추가
    - Environment URL: https://api.deepple.com
    ```

### 10.4 CI/CD 워크플로우 구조

**파일 구조**:

```
.github/
└── workflows/
    ├── ci.yml              # 모든 PR에서 실행 (테스트)
    ├── deploy-dev.yml      # develop 브랜치 자동 배포
    └── deploy-prod.yml     # main 브랜치 수동 배포
```

**워크플로우 상세는 별도 파일 참조** (프로젝트에 생성됨)

### 10.5 배포 프로세스

**개발 배포**:

```
1. feature 브랜치에서 작업
2. develop으로 PR 생성
3. CI 자동 실행 (테스트)
4. 리뷰 후 develop 머지
5. 자동으로 개발 서버 배포
```

**운영 배포**:

```
1. develop이 안정화되면 main으로 PR
2. CI 실행 + 코드 리뷰
3. main 머지
4. GitHub Actions에서 "Deploy to Production" 워크플로우 수동 실행
5. 승인자가 배포 승인
6. 운영 서버 배포
7. Slack 알림
```

---

## 11. 모니터링 및 로깅

### 11.1 CloudWatch 로그 설정

**개념**: CloudWatch는 AWS의 모니터링 서비스로, 로그 수집, 메트릭 추적, 알람 설정을 제공합니다.

1. **로그 그룹 생성**

    ```
    운영:
    - 로그 그룹: /deepple/prod/application
    - 보존 기간: 30일

    개발:
    - 로그 그룹: /deepple/dev/application
    - 보존 기간: 7일 (비용 절감)
    ```

2. **애플리케이션 로그 전송**

   Docker 컨테이너에서 CloudWatch Logs로 전송 (배포 스크립트에 이미 포함):

    ```bash
    docker run -d \
      --log-driver=awslogs \
      --log-opt awslogs-region=ap-northeast-2 \
      --log-opt awslogs-group=/deepple/prod/application \
      --log-opt awslogs-stream=spring-app \
      ...
    ```

### 11.2 메트릭 및 대시보드

1. **주요 메트릭 모니터링**

    ```
    - EC2: CPU, 메모리, 디스크, 네트워크
    - RDS: CPU, 커넥션 수, Read/Write IOPS
    - ElastiCache: CPU, 메모리, Evictions
    - ALB: 요청 수, 타겟 응답 시간, 5xx 에러
    ```

2. **CloudWatch 대시보드 생성**

    - 이름: deepple-prod-dashboard
    - 위젯 추가: 주요 메트릭 시각화

### 11.3 알람 설정

**중요 알람**:

```
1. EC2 CPU 사용률 > 80%
   - 알람 이름: deepple-prod-ec2-high-cpu
   - 기간: 5분 연속
   - 작업: SNS 토픽으로 이메일/SMS 발송

2. RDS CPU 사용률 > 80%
   - 알람 이름: deepple-prod-rds-high-cpu

3. RDS 프리 스토리지 < 10GB
   - 알람 이름: deepple-prod-rds-low-storage

4. ALB 5xx 에러 > 100건/5분
   - 알람 이름: deepple-prod-alb-5xx-errors

5. ALB 타겟 Unhealthy 수 > 0
   - 알람 이름: deepple-prod-alb-unhealthy-targets
```

**SNS 토픽 생성**:

```
- 토픽 이름: deepple-prod-alerts
- 구독: 운영팀 이메일, Slack 웹훅 등
```

---

## 12. 백업 및 재해 복구

### 12.1 데이터베이스 백업

**자동 백업** (이미 설정):

- RDS 자동 백업: 7일
- 스냅샷: 주간 수동 스냅샷 생성 권장

**백업 검증**:

```bash
# 월 1회 백업 복원 테스트 (개발 환경에서)
aws rds restore-db-instance-from-db-snapshot \
  --db-instance-identifier deepple-dev-restore-test \
  --db-snapshot-identifier [스냅샷 ID]
```

### 12.2 애플리케이션 백업

- **Docker 이미지**: Docker Hub에 이미 저장됨 (태그 관리 필수)
- **환경 설정**: .env 파일을 안전한 곳에 백업 (AWS Secrets Manager 또는 암호화된 S3)

### 12.3 재해 복구 계획 (DR)

**RPO/RTO 정의**:

- RPO (Recovery Point Objective): 데이터 손실 허용 시간 → 1시간
- RTO (Recovery Time Objective): 서비스 복구 목표 시간 → 2시간

**DR 시나리오**:

1. **EC2 장애**

    - Auto Scaling 사용 시: 자동 복구 (5분 이내)
    - 단일 인스턴스: 새 인스턴스 생성 및 배포 (30분)

2. **RDS 장애**

    - Multi-AZ 사용 시: 자동 페일오버 (1-2분)
    - 스냅샷 복원: 30분-1시간

3. **리전 장애** (극단적 상황)

    - RDS 스냅샷을 다른 리전에 복사 (선택)

---

## 13. 보안 설정

### 13.1 네트워크 보안

**보안 그룹 최소 권한 원칙**:

```
✅ 올바른 예:
- RDS: EC2 보안 그룹에서만 접근
- Redis: EC2 보안 그룹에서만 접근
- EC2: ALB 보안 그룹에서만 접근

❌ 잘못된 예:
- RDS: 0.0.0.0/0 (전세계에서 접근 가능)
- SSH: 0.0.0.0/0 (모든 곳에서 SSH 접근 가능)
```

**Bastion 호스트** (선택사항):

- 프라이빗 서브넷 리소스에 안전하게 접근하기 위한 점프 서버
- SSH 접근은 Bastion을 통해서만

### 13.2 애플리케이션 보안

1. **환경 변수 보안**

    ```
    - .env 파일 권한: chmod 600
    - Git에 .env 파일 커밋 금지 (.gitignore에 추가)
    - AWS Secrets Manager 사용 권장
    ```

2. **JWT 시크릿**

    ```bash
    # 강력한 랜덤 시크릿 생성 (256비트)
    openssl rand -base64 32
    ```

3. **API Rate Limiting**

    - Spring Boot에서 Bucket4j 또는 Resilience4j 사용
    - ALB 수준: AWS WAF로 Rate Limiting

4. **SQL Injection 방어**

    - JPA/QueryDSL 사용으로 기본 방어됨
    - 직접 쿼리 사용 시 Prepared Statement 필수

### 13.3 AWS 보안 서비스

1. **AWS WAF** (Web Application Firewall)

    ```
    - ALB에 연결
    - 룰 설정:
      * SQL Injection 차단
      * XSS 차단
      * Rate Limiting
      * IP 블랙리스트
    ```

2. **AWS Shield** (DDoS 보호)

    - Standard: 자동 활성화 (무료)
    - Advanced: 대규모 DDoS 보호 (유료)

3. **AWS Secrets Manager**

    ```
    - 민감한 정보 저장 (DB 비밀번호, API 키 등)
    - 자동 로테이션 기능
    - 애플리케이션에서 동적으로 가져오기
    ```

### 13.4 규정 준수

**개인정보보호법 (한국)**:

- 개인정보 암호화: RDS/S3/ElastiCache 모두 암호화 활성화
- 접근 로그: CloudTrail 활성화
- 데이터 보관 기간: S3 수명 주기 정책으로 관리

---

## 14. 운영 배포 체크리스트

### 14.1 배포 전 체크리스트

**인프라**:

- [ ] VPC 및 서브넷 생성 완료
- [ ] RDS Multi-AZ 활성화 및 백업 설정
- [ ] ElastiCache 클러스터 생성 및 암호화 활성화
- [ ] S3 버킷 생성 및 버저닝 활성화
- [ ] EC2 또는 Auto Scaling 그룹 생성
- [ ] ALB 생성 및 헬스 체크 설정
- [ ] 보안 그룹 최소 권한 설정 검증

**네트워크**:

- [ ] 도메인 네임서버 설정 완료
- [ ] Route 53 레코드 생성
- [ ] SSL 인증서 발급 및 ALB 연결
- [ ] HTTPS 리디렉션 설정

**애플리케이션 (DEEPPLE 특화)**:

- [ ] 운영 환경 변수 설정 (.env)
- [ ] JPA_DDL_AUTO=validate 확인 (중요!)
- [ ] FLYWAY_ENABLED=true 확인
- [ ] Swagger 비활성화 (SPRINGDOC_ENABLED=false)
- [ ] 로그 레벨 WARN으로 설정
- [ ] App Store 프로덕션 설정 확인
- [ ] Firebase 프로덕션 인증서 확인 (/home/ec2-user/secrets/firebase-adminsdk.json)
- [ ] App Store 인증서 확인 (/home/ec2-user/certs/appstore/)
- [ ] Flyway 마이그레이션 스크립트 확인 (V1, V2, V3)

**보안**:

- [ ] 모든 보안 그룹 규칙 검토
- [ ] RDS 퍼블릭 액세스 비활성화 확인
- [ ] JWT_SECRET 강력한 값으로 변경
- [ ] .env 파일 권한 600 설정
- [ ] SSH 키 안전하게 보관

**모니터링**:

- [ ] CloudWatch 로그 그룹 생성
- [ ] CloudWatch 알람 설정
- [ ] SNS 토픽 및 구독 설정
- [ ] 대시보드 생성

**CI/CD**:

- [ ] GitHub Secrets 모두 설정
- [ ] GitHub Environments 설정 (development, production)
- [ ] Production 환경 승인자 설정
- [ ] Docker Hub 로그인 확인
- [ ] EC2 SSH 접속 테스트
- [ ] 배포 스크립트 테스트

### 14.2 배포 순서

```
1단계: 인프라 구축 (1-2일)
  ├─ VPC 및 네트워크
  ├─ RDS
  ├─ ElastiCache
  └─ S3

2단계: 컴퓨팅 리소스 (1일)
  ├─ EC2 인스턴스
  ├─ ALB
  └─ Auto Scaling (선택)

3단계: 도메인 및 SSL (0.5일)
  ├─ Route 53 설정
  └─ ACM 인증서 발급

4단계: DEEPPLE 프로젝트 설정 (0.5일)
  ├─ Firebase 인증서 배치
  ├─ App Store 인증서 배치
  ├─ .env 파일 설정
  └─ 배포 스크립트 설정

5단계: 수동 배포 테스트 (0.5일)
  ├─ Docker 이미지 빌드
  ├─ EC2에 수동 배포
  ├─ 헬스 체크 확인
  └─ Flyway 마이그레이션 확인

6단계: CI/CD 구성 (0.5일)
  ├─ GitHub Secrets 설정
  ├─ GitHub Environments 설정
  └─ 자동 배포 테스트

7단계: 모니터링 설정 (0.5일)
  ├─ CloudWatch 설정
  └─ 알람 테스트

8단계: 최종 검증 (1일)
  ├─ 부하 테스트
  ├─ 장애 시나리오 테스트
  └─ 보안 점검
```

### 14.3 배포 후 체크리스트

**즉시 확인**:

- [ ] 애플리케이션 정상 구동 (헬스 체크)
- [ ] ALB를 통한 접속 테스트
- [ ] HTTPS 정상 작동
- [ ] 데이터베이스 연결 확인
- [ ] Redis 연결 확인
- [ ] S3 업로드 테스트
- [ ] 주요 API 엔드포인트 테스트
- [ ] Firebase 푸시 알림 테스트
- [ ] App Store 인앱결제 검증 테스트

**24시간 내**:

- [ ] CloudWatch 로그 정상 수집 확인
- [ ] 메트릭 모니터링
- [ ] 알람 테스트 (임계값 조정)
- [ ] 응답 시간 측정
- [ ] 에러 로그 확인

**1주일 내**:

- [ ] 백업 정상 동작 확인
- [ ] 백업 복원 테스트 (개발 환경)
- [ ] Auto Scaling 동작 테스트 (설정 시)
- [ ] 비용 모니터링 (Cost Explorer)
- [ ] 보안 점검 (AWS Trusted Advisor)

### 14.4 롤백 계획

**배포 실패 시**:

```bash
# 이전 버전으로 즉시 롤백
docker stop spring-app
docker rm spring-app
docker run -d \
  --name spring-app \
  --env-file /home/ec2-user/.env \
  -p 8080:8080 \
  -v /home/ec2-user/secrets:/etc/credentials:ro \
  -v /home/ec2-user/certs:/etc/certs:ro \
  --restart unless-stopped \
  ggongtae/deepple:[이전 태그]
```

**데이터베이스 마이그레이션 실패 시**:

- Flyway 롤백 스크립트 준비 (Undo 스크립트)
- 또는 RDS 스냅샷으로 복원

---

## 15. 비용 최적화

### 15.1 예상 비용 (월간, 서울 리전 기준)

**Development 환경**:

```
- EC2 t3.small (1대): $15
- RDS db.t3.micro (Single-AZ): $15
- ElastiCache cache.t3.micro: $12
- S3 50GB + 전송: $3
- ALB: $20
- Route 53: $1
─────────────────────
합계: 약 $66/월 (₩86,000)
```

**Production 환경 (최소 구성)**:

```
- EC2 t3.medium (1대): $30
- RDS db.t3.medium (Multi-AZ): $120
- ElastiCache cache.t3.small: $30
- S3 100GB + 전송: $5
- ALB: $20
- Route 53: $1
- 데이터 전송: $10
─────────────────────
합계: 약 $216/월 (₩280,000)
```

**Production 환경 (권장 구성)**:

```
- EC2 t3.large x2 (Auto Scaling): $120
- RDS db.t3.large (Multi-AZ): $250
- ElastiCache cache.t3.small (복제본): $30
- S3 500GB + 전송: $15
- ALB: $25
- CloudWatch: $10
- NAT Gateway: $40 (선택)
- Route 53: $1
─────────────────────
합계: 약 $491/월 (₩640,000)
```

### 15.2 비용 절감 방법

1. **예약 인스턴스** (1-3년 약정)

    - EC2, RDS 최대 72% 할인
    - 트래픽이 안정화되면 적용

2. **Savings Plans**

    - 유연한 할인 (EC2, Lambda 등)

3. **개발 환경 자동 종료**

    - Lambda + EventBridge로 업무 시간 외 EC2 자동 중지
    - 월 ~$40 절감

4. **S3 스토리지 클래스**

    - 오래된 파일: S3 Intelligent-Tiering
    - 아카이브: S3 Glacier

5. **CloudWatch 로그 보존 기간 조정**

    - 개발: 7일
    - 운영: 30일

6. **불필요한 리소스 정리**

    - 매주 Cost Explorer로 비용 분석
    - 사용하지 않는 EBS 볼륨, 스냅샷 삭제

---

## 16. 문제 해결 가이드

### 16.1 애플리케이션이 시작되지 않을 때

```bash
# 1. Docker 로그 확인
docker logs spring-app

# 2. 컨테이너 상태 확인
docker ps -a

# 3. 환경 변수 확인
docker exec spring-app env | grep MYSQL

# 4. 네트워크 연결 테스트
docker exec spring-app ping atwoz-prod-db.xxxxx.rds.amazonaws.com
```

### 16.2 데이터베이스 연결 실패

```bash
# RDS 보안 그룹 확인
# EC2에서 MySQL 접속 테스트
nc -zv atwoz-prod-db.xxxxx.rds.amazonaws.com 3306

# 또는
telnet atwoz-prod-db.xxxxx.rds.amazonaws.com 3306
```

### 16.3 ALB 헬스 체크 실패

```bash
# EC2에서 직접 헬스 체크 엔드포인트 확인
curl http://localhost:8080/actuator/health

# ALB 대상 그룹 상태 확인
aws elbv2 describe-target-health \
  --target-group-arn [대상 그룹 ARN]
```

### 16.4 Flyway 마이그레이션 실패

```bash
# Flyway 히스토리 확인
mysql -h [RDS_HOST] -u deepple_app -p
USE deepple;
SELECT * FROM flyway_schema_history;

# 실패한 마이그레이션 수동 수정
# 1. 실패 상태 레코드 삭제
DELETE FROM flyway_schema_history WHERE success = 0;

# 2. 마이그레이션 스크립트 수정 후 재배포
```

### 16.5 Firebase 푸시 알림 실패

```bash
# Firebase 인증서 파일 확인
docker exec spring-app ls -la /etc/credentials/

# 파일이 없으면 다시 복사
scp -i deepple-prod-key.pem firebase-adminsdk.json ec2-user@[EC2_IP]:/home/ec2-user/secrets/

# 컨테이너 재시작
docker restart spring-app
```

---

## 17. 참고 자료

### 17.1 AWS 공식 문서

- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [RDS Best Practices](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_BestPractices.html)
- [ElastiCache Best Practices](https://docs.aws.amazon.com/AmazonElastiCache/latest/red-ug/BestPractices.html)

### 17.2 Spring Boot 프로덕션 가이드

- [Spring Boot Production Ready Features](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Spring Boot Docker](https://spring.io/guides/topicals/spring-boot-docker/)

### 17.3 보안 체크리스트

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [AWS Security Best Practices](https://docs.aws.amazon.com/securityhub/latest/userguide/securityhub-standards.html)

---

## 결론

이 가이드를 따라 단계별로 진행하면 DEEPPLE 프로젝트를 안정적이고 확장 가능한 AWS 운영 환경에 배포할 수 있습니다.

**핵심 포인트**:

1. **보안**: 최소 권한 원칙, 암호화, Multi-AZ
2. **안정성**: Auto Scaling, 로드 밸런싱, 백업
3. **모니터링**: CloudWatch, 알람, 로그
4. **비용**: 단계적 확장, 예약 인스턴스
5. **DEEPPLE 특화**: Firebase, App Store 인증서, Flyway 마이그레이션

**다음 단계**:

1. 개발 환경에서 먼저 테스트
2. 프로덕션 배포 전 체크리스트 완료
3. 배포 후 24시간 모니터링
4. 1주일 후 성능 및 비용 리뷰