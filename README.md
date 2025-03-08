# atwoz
연애의 모든 것, 에이투지는 나와 상대방의 가치관과 취향을 분석하여 최적의 이상형을 매칭해주는 서비스입니다.  
도메인 주도 설계, 로컬 이벤트, 코드 레벨 CQRS 등을 사용했으며, 추후 MSA로 고도화 예정입니다.

<br>

## 역할
- 공태현:
- 정호윤: 알림 도메인 개발, 관리자 도메인 개발
- 하인호: 

<br>

## 기술 스택
- Java 21, Spring Boot 3
- JPA, QueryDSL
- MySQL, Docker, AWS

<br>

## 시스템 아키텍처

<br>

## 컨텍스트 맵

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
1. env 설정: .env.example 파일을 참고하여 .env 파일을 생성합니다.
2. docker-compose 실행
```bash
docker compose --env-file .env up -d
```
