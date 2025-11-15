# atwoz

연애의 모든 것, 에이투지는 나와 상대방의 가치관과 취향을 분석하여 최적의 이상형을 매칭해주는 서비스입니다.  
도메인 주도 설계, 로컬 이벤트, 코드 레벨 CQRS 등을 사용했으며, 추후 MSA로 고도화 예정입니다.

<br>

## 담당 역할

- 공태현: 회원 도메인, 매칭 도메인, CI/CD
- 정호윤: 알림 도메인, 관리자 도메인
- 하인호: 인앱 결제 도메인, 하트(앱 내 재화) 도메인, 컨텐츠 도메인, 추천 도메인

<br>

## 기술 스택

- Java 21, Spring Boot 3
- JPA, QueryDSL
- MySQL, Docker, AWS

<br>

## 시스템 아키텍처

- CI/CD 관련 내용은 [여기](https://github.com/atwoz-dev/atwoz_server/pull/46)를 참고해주세요.
  <img width="792" src="https://github.com/user-attachments/assets/e38d4208-03cc-45c5-8792-eaac02eecaf8"/>

<br>

## 컨텍스트 맵

<img width="587" alt="Screenshot 2025-03-09 at 6 59 11 PM" src="https://github.com/user-attachments/assets/06e8217f-45b2-4fd6-a5fe-d84a5ef45cc3" />

<br>

## 패키지 구조

도메인 주도 설계, 코드 레벨의 CQRS가 적용된 구조입니다.  
상위 패키지는 하위 도메인을 기준으로 분리되어 있습니다.  
아래는 예시입니다.

```
member                  // 하위 도메인
├── presentation        // API 계층
├── command             // 명령 처리 계층
│   ├── application     // 애플리케이션 계층
│   ├── domain          // 도메인 계층
│   └── infra           // 인프라 계층
└── query               // 조회 전용 계층
```

<br>

## ERD

- 25.03.09 기준

![awtoz_erd_250309](https://github.com/user-attachments/assets/366fd8d4-8c50-4f4a-ba6d-f746061c7349)

<br>

## 실행 방법

이 프로젝트는 Java/Spring 기반의 백엔드 서비스로, Docker 컨테이너 환경에서 실행됩니다.  
아래 내용을 참고하여 환경 변수 설정 및 실행 방법을 확인하세요.

<br>

### 1. 환경 변수 설정

#### .env 파일 생성 및 수정

프로젝트 루트 디렉토리에 있는 `.env.example` 파일을 참고하여 `.env` 파일을 생성합니다.
예시:

```bash
cp .env.example .env
```

생성된 `.env` 파일에서 다음과 같이 각 환경 변수의 값을 실제 환경에 맞게 수정합니다.

#### Spring Profiles

| 변수명                        | 설명       | 옵션               |
|----------------------------|----------|------------------|
| **SPRING_PROFILES_ACTIVE** | 실행 환경 지정 | local, dev, prod |

#### Server 설정

| 변수명                    | 설명                    | 예시   |
|------------------------|-----------------------|------|
| **SERVER_PORT**        | Spring Boot 애플리케이션 포트 | 8080 |
| **APP_HOST_PORT**      | 호스트 머신에서 접근할 포트       | 8080 |
| **APP_CONTAINER_PORT** | Docker 컨테이너 내부 포트     | 8080 |

#### MySQL

| 변수명 | 설명 | 예시 |
|--------------------------|---------|----|------|
| **MYSQL_HOST**           | MySQL 서버 호스트 (로컬: db, 서버: RDS 엔드포인트) | db |
| **MYSQL_PORT**           | MySQL 서버 포트 | 3306 |
| **MYSQL_HOST_PORT**      | 호스트 머신에서 MySQL 접근 포트 | 3308 |
| **MYSQL_CONTAINER_PORT** | MySQL 컨테이너 내부 포트 | 3306 |
| **MYSQL_DATABASE**       | 데이터베이스 이름 | atwoz |
| **MYSQL_USER**           | MySQL 사용자명 | user |
| **MYSQL_PASSWORD**       | MySQL 비밀번호 | 1234 |
| **MYSQL_ROOT_PASSWORD**  | MySQL root 비밀번호 | 1234 |

#### JPA/Hibernate

| 변수명 | 설명 | 예시 |
|--------------------|-----------------------------|----|------|
| **JPA_DDL_AUTO**   | 스키마 자동 생성 모드 (create/update/validate) | create |
| **JPA_SHOW_SQL**   | SQL 로그 출력 여부 | true |
| **JPA_FORMAT_SQL** | SQL 포매팅 여부 | true |

#### Flyway

| 변수명                | 설명                   | 예시    |
|--------------------|----------------------|-------|
| **FLYWAY_ENABLED** | 데이터베이스 마이그레이션 활성화 여부 | false |

#### Redis

| 변수명                      | 설명                                    | 예시    |
|--------------------------|---------------------------------------|-------|
| **REDIS_HOST**           | Redis 서버 호스트                          | redis |
| **REDIS_PORT**           | Spring Boot가 연결할 Redis 포트 (컨테이너 내부)   | 6379  |
| **REDIS_HOST_PORT**      | 호스트 머신에서 Redis 접근 포트                  | 6381  |
| **REDIS_CONTAINER_PORT** | Redis 컨테이너 내부 포트                      | 6379  |
| **REDIS_PASSWORD**       | Redis 비밀번호 (선택)                       |       |
| **REDIS_SSL_ENABLED**    | Redis SSL 활성화 (AWS ElastiCache는 true) | false |

#### AWS S3

| 변수명                    | 설명       | 예시 |
|------------------------|----------|----|
| **AWS_S3_BUCKET_NAME** | S3 버킷 이름 |    |

#### JWT

| 변수명                              | 설명                      | 예시                                          |
|----------------------------------|-------------------------|---------------------------------------------|
| **JWT_SECRET**                   | JWT 서명 키                | this-is-secret-key-value-at-least-128-bytes |
| **JWT_ACCESS_TOKEN_EXPIRATION**  | Access Token 만료 시간 (초)  | 1800                                        |
| **JWT_REFRESH_TOKEN_EXPIRATION** | Refresh Token 만료 시간 (초) | 1209600                                     |

#### Auth

| 변수명                  | 설명        | 예시     |
|----------------------|-----------|--------|
| **AUTH_PREFIX_CODE** | 인증 코드 접두사 | 008008 |

#### Swagger

| 변수명                    | 설명                | 예시   |
|------------------------|-------------------|------|
| **SPRINGDOC_ENABLED**  | Swagger 문서 활성화 여부 | true |
| **SWAGGER_UI_ENABLED** | Swagger UI 활성화 여부 | true |

#### App Store

| 변수명                              | 설명                                | 예시                                            |
|----------------------------------|-----------------------------------|-----------------------------------------------|
| **APP_STORE_KEY_ID**             | App Store Connect API Key ID      |                                               |
| **APP_STORE_PRIVATE_KEY_STRING** | App Store Connect Private Key     |                                               |
| **APP_STORE_ISSUER_ID**          | App Store Connect Issuer ID       |                                               |
| **APP_STORE_ENVIRONMENT**        | App Store 환경 (Sandbox/Production) | Sandbox                                       |
| **APP_STORE_BUNDLE_ID**          | 앱 Bundle ID                       |                                               |
| **APP_STORE_APP_APPLE_ID**       | 앱 Apple ID                        |                                               |
| **APP_STORE_ROOT_CA_G2_PATH**    | Apple Root CA G2 인증서 경로           | /etc/certs/appstore/AppleRootCA-G2.pem        |
| **APP_STORE_ROOT_CA_G3_PATH**    | Apple Root CA G3 인증서 경로           | /etc/certs/appstore/AppleRootCA-G3.pem        |
| **APP_STORE_BASE_URL**           | App Store Server API URL          | https://api.storekit-sandbox.itunes.apple.com |

#### Bizgo

| 변수명                         | 설명               | 예시 |
|-----------------------------|------------------|----|
| **BIZGO_API_URL**           | Bizgo API URL    |    |
| **BIZGO_CLIENT_ID**         | Bizgo 클라이언트 ID   |    |
| **BIZGO_CLIENT_PASSWORD**   | Bizgo 클라이언트 비밀번호 |    |
| **BIZGO_FROM_PHONE_NUMBER** | 발신 전화번호          |    |

#### Firebase

| 변수명                                | 설명                | 예시 |
|------------------------------------|-------------------|----|
| **GOOGLE_APPLICATION_CREDENTIALS** | Firebase 인증 파일 경로 |    |

> 각 변수는 실제 운영 환경 및 개발 환경에 맞게 적절히 변경해 주세요.

<br>

### 2. Docker Compose를 이용한 실행

프로젝트 루트 디렉토리에 있는 `docker-compose.yml` 파일을 통해 아래와 같이 컨테이너들을 실행할 수 있습니다.

```bash
docker compose --env-file .env up -d
```

위 명령어는 다음의 서비스를 포함합니다:

- **app**: Java Spring 백엔드 애플리케이션
- **db**: MySQL 데이터베이스
- **redis**: Redis 캐시 서버

#### 실행 확인

- 애플리케이션 실행 후 웹 브라우저 또는 API 클라이언트를 통해 `http://localhost:8080/swagger-ui.html` (또는 `.env`에 설정한 포트)로 접근하여 서비스가 정상적으로 동작하는지
  확인합니다.
- Swagger UI 설정이 활성화된 경우, API 문서를 확인할 수 있습니다.
- 
