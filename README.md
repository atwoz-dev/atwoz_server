# deepple

딥플은 사용자의 가치관과 취향을 분석하여 최적의 매칭을 제공하는 데이팅 서비스입니다.

## 기술 스택

- JDK 21, Spring Boot 3, JPA, QueryDSL, Flyway
- AWS EC2, ECR, RDS(MySQL), ElastiCache(Valkey), S3
- Docker, GitHub Actions, Nginx
- Firebase Cloud Messaging, Apple App Store Server API, Bizgo SMS

## 주요 기능

- **회원 관리**: SMS 인증, 프로필 관리, 프로필 이미지 업로드, 이상형 설정
- **매칭 시스템**: 다중 필터 기반 이성 소개 (등급별, 취미, 종교, 지역), 오늘의 카드, 소울메이트 매칭, 연애고사 기반 분석, 프로필 블러 해제
- **매칭 메시지**: 매칭 요청 메시지 전송/수락/거절, 메시지 내역 관리
- **하트 및 결제**: 앱 내 재화 시스템, Apple App Store 결제 연동, 미션 보상
- **커뮤니티**: 셀프 소개, 프로필 교환, 좋아요
- **관리자**: 회원 프로필 심사, 회원 관리, 신고 처리, 회원 정지 및 경고

## 시스템 아키텍처 및 인프라

AWS 기반 클라우드 인프라와 GitHub Actions 기반 CI/CD 파이프라인을 구축했습니다.
자세한 내용은 [여기](https://github.com/atwoz-dev/atwoz_server/wiki)를 참고하세요.

## 소프트웨어 아키텍처

도메인 주도 설계(DDD)와 코드 레벨 CQRS를 적용하여 비즈니스 로직을 명확히 분리했습니다.
자세한 내용은 [여기](https://github.com/atwoz-dev/atwoz_server/wiki)를 참고하세요.

## Contributors

- [공태현](https://github.com/Kong-TaeHyeon): 회원, 매칭, 커뮤니티 도메인
- [정호윤](https://github.com/stemmmm): 알림, 관리자, 좋아요 도메인, 인프라(AWS), CI/CD
- [하인호](https://github.com/hainho): 하트, 결제, 설문, 연애고사, 차단, 신고 도메인
