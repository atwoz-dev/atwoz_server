# ATWOZ 개발 프로세스 가이드

## 📋 목차

1. [개요](#1-개요)
2. [브랜치 전략](#2-브랜치-전략)
3. [개발 워크플로우](#3-개발-워크플로우)
4. [코드 리뷰 프로세스](#4-코드-리뷰-프로세스)
5. [배포 프로세스](#5-배포-프로세스)
6. [데이터베이스 마이그레이션](#6-데이터베이스-마이그레이션)
7. [환경 관리](#7-환경-관리)
8. [테스트 전략](#8-테스트-전략)
9. [문서화 규칙](#9-문서화-규칙)
10. [장애 대응](#10-장애-대응)

---

## 1. 개요

### 1.1 개발 환경

ATWOZ 프로젝트는 3가지 환경으로 구성됩니다:

| 환경              | 브랜치       | 배포 방식               | 용도         |
|-----------------|-----------|---------------------|------------|
| **Local**       | feature/* | 수동 (Docker Compose) | 로컬 개발      |
| **Development** | develop   | 자동 (GitHub Actions) | 통합 테스트, QA |
| **Production**  | main      | 자동 (GitHub Actions) | 실제 서비스     |

### 1.2 기술 스택

- **Language**: Java 21
- **Framework**: Spring Boot 3
- **Build Tool**: Gradle
- **Database**: MySQL 8.0 (Flyway 마이그레이션)
- **Cache**: Redis 7.2
- **Architecture**: DDD + CQRS (code-level)
- **CI/CD**: GitHub Actions
- **Container**: Docker

---

## 2. 브랜치 전략

### 2.1 Git Flow 간소화 버전

```
main (운영)
  ↑
develop (개발)
  ↑
feature/기능명 (기능 개발)
```

### 2.2 브랜치 규칙

#### main 브랜치

- **목적**: 운영 환경 배포
- **특징**:
    - Protected branch (직접 push 금지)
    - develop에서만 머지 허용
    - 모든 PR은 최소 1명의 승인 필요
    - CI 테스트 통과 필수
- **배포**: main 머지 시 자동으로 운영 서버 배포
- **네이밍**: `main`

#### develop 브랜치

- **목적**: 개발 통합 브랜치
- **특징**:
    - feature 브랜치들이 여기로 머지
    - CI 자동 실행
    - 개발 서버에 자동 배포
- **배포**: develop 머지 시 자동으로 개발 서버 배포
- **네이밍**: `develop`

#### feature 브랜치

- **목적**: 새 기능 개발
- **특징**:
    - develop에서 분기
    - 개발 완료 후 develop으로 PR
- **네이밍 규칙**:
    - `feature/기능명` (예: `feature/matching-algorithm-v2`)
    - `feature/도메인/기능명` (예: `feature/member/profile-verification`)
    - **좋은 예**:
        - `feature/heart-transaction`
        - `feature/admin/user-screening`
        - `feature/notification/push-retry`
    - **나쁜 예**:
        - `feature/fix` (너무 모호)
        - `feature/update` (무엇을 업데이트?)
        - `test` (브랜치 타입 명시 안 됨)

#### hotfix 브랜치 (긴급 수정)

- **목적**: 운영 환경 긴급 버그 수정
- **특징**:
    - main에서 분기
    - main과 develop 모두에 머지
- **네이밍**: `hotfix/버그명` (예: `hotfix/payment-validation-error`)
- **프로세스**:
  ```bash
  # main에서 분기
  git checkout main
  git pull origin main
  git checkout -b hotfix/버그명

  # 수정 후 커밋
  git add .
  git commit -m "hotfix: 버그 설명"

  # main으로 PR (긴급 리뷰 후 머지)
  # develop으로도 PR (동기화)
  ```

### 2.3 커밋 메시지 규칙

**Conventional Commits** 형식을 따릅니다:

```
<type>: <subject>

[optional body]

[optional footer]
```

**Type 종류**:

- `feat`: 새 기능
- `fix`: 버그 수정
- `refactor`: 리팩토링 (기능 변경 없음)
- `style`: 코드 포맷팅 (세미콜론, 공백 등)
- `test`: 테스트 코드 추가/수정
- `docs`: 문서 수정
- `chore`: 빌드, 설정 파일 수정
- `perf`: 성능 개선

**예시**:

```bash
# 좋은 예
feat: 매칭 알고리즘 v2 구현
fix: 하트 거래 시 잔액 검증 오류 수정
refactor: 회원 도메인 서비스 레이어 분리
test: 좋아요 기능 단위 테스트 추가

# 나쁜 예
update: 코드 수정
fix bug
회원가입 기능 (영어로 작성)
```

---

## 3. 개발 워크플로우

### 3.1 새 기능 개발 프로세스

#### 1단계: 이슈 생성 (선택사항)

```
GitHub Issues에서 작업 내용 정의
- 제목: [기능] 명확한 기능명
- 내용: 요구사항, 구현 방법, 체크리스트
```

#### 2단계: feature 브랜치 생성

```bash
# develop에서 최신 코드 받기
git checkout develop
git pull origin develop

# feature 브랜치 생성
git checkout -b feature/user-verification
```

#### 3단계: 로컬 개발

```bash
# 로컬 환경 실행
docker-compose up -d db redis
./gradlew bootRun

# 개발 진행...
```

#### 4단계: 로컬 테스트

```bash
# 테스트 실행
./gradlew test

# 빌드 확인
./gradlew build
```

#### 5단계: 커밋 및 푸시

```bash
git add .
git commit -m "feat: 사용자 본인 인증 기능 구현"
git push origin feature/user-verification
```

#### 6단계: Pull Request 생성

- **Base**: develop
- **Compare**: feature/user-verification
- **PR 템플릿 작성** (자동 생성됨, 아래 참조)

#### 7단계: 코드 리뷰

- CI 자동 실행 (테스트, 빌드)
- 리뷰어 지정
- 리뷰 피드백 반영

#### 8단계: develop 머지

- 리뷰 승인 후 "Squash and merge"
- feature 브랜치 삭제

#### 9단계: 개발 서버 확인

- GitHub Actions가 자동으로 개발 서버 배포
- 배포 완료 후 개발 서버에서 테스트

### 3.2 운영 배포 프로세스

#### 1단계: develop → main PR

```bash
# develop이 안정화되면 main으로 PR 생성
```

#### 2단계: 코드 리뷰 및 QA

- 전체 기능 회귀 테스트
- 성능 테스트 (선택)
- 보안 점검

#### 3단계: main 머지

- 리뷰 승인 후 머지
- GitHub Actions가 자동으로 운영 서버 배포 시작

#### 4단계: 배포 후 모니터링

- 헬스 체크 확인
- CloudWatch 로그 모니터링
- 에러 알람 확인

#### 5단계: 태그 생성 (버전 관리)

```bash
git checkout main
git pull origin main
git tag v1.2.0
git push origin v1.2.0
```

---

## 4. 코드 리뷰 프로세스

### 4.1 PR 체크리스트

모든 PR은 다음을 확인해야 합니다:

**기능**:

- [ ] 요구사항을 충족하는가?
- [ ] 엣지 케이스가 처리되었는가?
- [ ] 에러 핸들링이 적절한가?

**코드 품질**:

- [ ] DDD 원칙을 따르는가? (도메인 로직은 domain 패키지에)
- [ ] CQRS 패턴을 따르는가? (command/query 분리)
- [ ] 비즈니스 로직이 도메인 엔티티에 있는가?
- [ ] 중복 코드가 없는가?
- [ ] 변수명, 메서드명이 명확한가?

**테스트**:

- [ ] 단위 테스트가 작성되었는가?
- [ ] 테스트 커버리지가 충분한가?
- [ ] 모든 테스트가 통과하는가?

**보안**:

- [ ] SQL Injection 취약점이 없는가?
- [ ] XSS 취약점이 없는가?
- [ ] 민감한 정보가 로그에 남지 않는가?
- [ ] 권한 검증이 적절한가?

**성능**:

- [ ] N+1 쿼리 문제가 없는가?
- [ ] 불필요한 DB 조회가 없는가?
- [ ] 캐싱이 적절히 사용되었는가?

**데이터베이스**:

- [ ] Flyway 마이그레이션 스크립트가 있는가? (스키마 변경 시)
- [ ] 롤백 스크립트가 준비되었는가?

**문서화**:

- [ ] API 명세가 업데이트되었는가? (Swagger)
- [ ] README가 업데이트되었는가? (필요 시)

### 4.2 리뷰 가이드라인

**리뷰어**:

- 24시간 이내에 리뷰 완료
- 건설적인 피드백 제공
- 코드 스타일뿐만 아니라 로직, 설계 검토
- 리뷰 코멘트는 명확하고 친절하게

**작성자**:

- 리뷰 피드백에 빠르게 응답
- 동의하지 않는 부분은 토론
- 모든 코멘트에 대응 후 머지 요청

---

## 5. 배포 프로세스

### 5.1 자동 배포 (Development)

**트리거**: develop 브랜치에 push/merge

**프로세스**:

```
1. GitHub Actions 트리거
2. 테스트 실행
3. Docker 이미지 빌드
4. Docker Hub에 푸시
5. EC2 SSH 접속
6. 배포 스크립트 실행
7. 헬스 체크
8. Slack 알림 (선택)
```

**모니터링**:

- GitHub Actions 로그 확인
- 개발 서버 로그 확인: `ssh -i key.pem ec2-user@dev-server`
  ```bash
  docker logs spring-app
  ```

### 5.2 자동 배포 (Production)

**트리거**: main 브랜치에 push/merge

**프로세스**:

```
1. GitHub Actions 트리거 (main 브랜치 push 시)
2. 테스트 실행
3. Docker 이미지 빌드 (운영 태그 with 버전)
4. Docker Hub에 푸시
5. EC2 SSH 접속
6. 배포 스크립트 실행
7. 헬스 체크 (최대 5분 대기)
8. 성공 시 배포 완료
9. 실패 시 자동 롤백
```

**참고**: 수동 배포가 필요한 경우 GitHub Actions의 "Deploy to Production" 워크플로우를 수동으로 실행할 수도 있습니다.

**배포 전 체크리스트**:

- [ ] develop 브랜치가 안정적인가?
- [ ] 모든 테스트가 통과하는가?
- [ ] Flyway 마이그레이션이 준비되었는가?
- [ ] 롤백 계획이 있는가?
- [ ] 배포 시간이 적절한가? (트래픽 낮은 시간)

**배포 후 체크리스트**:

- [ ] 헬스 체크 성공
- [ ] 주요 API 엔드포인트 정상 작동
- [ ] 데이터베이스 연결 정상
- [ ] Redis 연결 정상
- [ ] CloudWatch 로그 정상 수집
- [ ] 에러 알람 없음

### 5.3 롤백 프로세스

**자동 롤백** (배포 스크립트에서 헬스 체크 실패 시):

- 배포 스크립트가 자동으로 이전 컨테이너 유지

**수동 롤백**:

```bash
# EC2에 SSH 접속
ssh -i atwoz-prod-key.pem ec2-user@[EC2_IP]

# 이전 버전으로 롤백
docker stop spring-app
docker rm spring-app
docker run -d \
  --name spring-app \
  --env-file /home/ec2-user/.env \
  -p 8080:8080 \
  -v /home/ec2-user/secrets:/etc/credentials:ro \
  -v /home/ec2-user/certs:/etc/certs:ro \
  --restart unless-stopped \
  ggongtae/atwoz:[이전_태그]

# 헬스 체크
curl http://localhost:8080/actuator/health
```

---

## 6. 데이터베이스 마이그레이션

### 6.1 Flyway 사용 규칙

**마이그레이션 파일 위치**:

```
src/main/resources/db/migration/
```

**파일 명명 규칙**:

```
V{버전}__{설명}.sql

예시:
V1__insert_default_interview_questions.sql
V2__instert_default_missions.sql
V3__insert_default_dating_exams.sql
V4__add_user_verification_table.sql
V5__add_index_to_match_table.sql
```

**주의사항**:

- 버전 번호는 순차적으로 증가
- 한 번 적용된 마이그레이션은 절대 수정 금지
- 새로운 변경은 새로운 마이그레이션 파일로 생성

### 6.2 마이그레이션 작성 예시

**테이블 생성**:

```sql
-- V4__add_user_verification_table.sql
CREATE TABLE user_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    verification_type VARCHAR(50) NOT NULL,
    verification_data TEXT,
    verified_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_verification_user FOREIGN KEY (user_id) REFERENCES member(id)
);

CREATE INDEX idx_user_verification_user_id ON user_verification(user_id);
```

**인덱스 추가**:

```sql
-- V5__add_index_to_match_table.sql
CREATE INDEX idx_match_created_at ON match_table(created_at);
CREATE INDEX idx_match_status ON match_table(status);
```

**데이터 삽입**:

```sql
-- V6__insert_new_missions.sql
INSERT INTO mission (name, description, reward_hearts, created_at) VALUES
('첫 프로필 작성', '프로필을 완성하세요', 50, NOW()),
('첫 매칭 신청', '첫 매칭을 신청하세요', 100, NOW());
```

### 6.3 마이그레이션 프로세스

#### 로컬 개발

```bash
# 마이그레이션 파일 작성
# V4__add_user_verification_table.sql

# 로컬 DB에서 테스트
docker-compose up -d db
./gradlew bootRun

# Flyway가 자동으로 마이그레이션 실행
# 로그에서 "Migrating schema..." 확인
```

#### 개발 서버

```
develop 브랜치에 머지 → 자동 배포 → Flyway 자동 실행
```

#### 운영 서버

```
main 브랜치에 머지 → 자동 배포 → Flyway 자동 실행
```

### 6.4 마이그레이션 실패 시 대응

```bash
# RDS 접속
mysql -h atwoz-prod-db.xxxxx.rds.amazonaws.com -u atwoz_app -p

# Flyway 히스토리 확인
USE atwoz;
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;

# 실패한 마이그레이션 확인
SELECT * FROM flyway_schema_history WHERE success = 0;

# 실패 레코드 삭제 (주의!)
DELETE FROM flyway_schema_history WHERE version = '4' AND success = 0;

# 마이그레이션 스크립트 수정 후 재배포
```

---

## 7. 환경 관리

### 7.1 환경 변수 관리

**로컬 개발**:

- `.env` 파일 사용 (Git에 커밋 금지)
- `.env.example` 파일 유지 (샘플)

**개발/운영 서버**:

- GitHub Secrets에 저장
- EC2에서 `/home/ec2-user/.env` 파일로 관리

**환경 변수 추가 시**:

1. `.env.example` 업데이트
2. README에 설명 추가
3. GitHub Secrets 업데이트 (DEV_ENV, PROD_ENV)
4. EC2 서버의 .env 파일 업데이트

### 7.2 Spring Profile 관리

**로컬**:

```yaml
# application-local.yml
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop  # 로컬만!

logging:
  level:
    root: DEBUG
```

**개발**:

```yaml
# application-dev.yml
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate  # 중요!

logging:
  level:
    root: INFO
```

**운영**:

```yaml
# application-prod.yml
spring:
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate  # 필수!

logging:
  level:
    root: WARN
```

---

## 8. 테스트 전략

### 8.1 테스트 피라미드

```
        /\
       /  \  E2E 테스트 (소수)
      /    \
     /------\  통합 테스트 (중간)
    /--------\
   /----------\ 단위 테스트 (다수)
```

### 8.2 단위 테스트

**작성 대상**:

- 도메인 엔티티 (비즈니스 로직)
- Value Object
- 도메인 서비스

**예시**:

```java
@Test
@DisplayName("하트 차감 시 잔액이 부족하면 예외 발생")
void deductHeart_InsufficientBalance_ThrowsException() {
    // given
    Heart heart = Heart.of(memberId, 50);

    // when & then
    assertThatThrownBy(() -> heart.deduct(100))
        .isInstanceOf(InsufficientHeartException.class);
}
```

### 8.3 통합 테스트

**작성 대상**:

- Repository (JPA)
- 외부 API 연동 (Mocking)
- 트랜잭션 처리

**예시**:

```java
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원 조회")
    void findByEmail() {
        // given
        Member member = Member.builder()
            .email("test@example.com")
            .build();
        memberRepository.save(member);

        // when
        Optional<Member> found = memberRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
    }
}
```

### 8.4 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests MemberServiceTest

# 테스트 커버리지 확인 (JaCoCo)
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

---

## 9. 문서화 규칙

### 9.1 코드 문서화

**JavaDoc 작성 대상**:

- Public API (Controller, Service)
- 복잡한 비즈니스 로직
- 도메인 이벤트

**예시**:

```java
/**
 * 하트를 차감합니다.
 *
 * @param amount 차감할 하트 수량
 * @throws InsufficientHeartException 하트 잔액이 부족한 경우
 */
public void deduct(int amount) {
    if (this.balance < amount) {
        throw new InsufficientHeartException();
    }
    this.balance -= amount;
}
```

### 9.2 API 문서화

**Swagger 사용**:

```java
@Operation(summary = "회원 프로필 조회", description = "회원 ID로 프로필 정보를 조회합니다.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "조회 성공"),
    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
})
@GetMapping("/{memberId}")
public ResponseEntity<MemberProfileResponse> getMemberProfile(@PathVariable Long memberId) {
    // ...
}
```

**Swagger 접근**:

- 로컬: http://localhost:8080/swagger-ui.html
- 개발: https://dev-api.atwoz.com/swagger-ui.html
- 운영: 비활성화 (보안)

### 9.3 README 업데이트

**업데이트 시기**:

- 새로운 환경 변수 추가
- 설정 파일 변경
- 외부 의존성 추가
- 개발 프로세스 변경

---

## 10. 장애 대응

### 10.1 장애 등급

**P0 (Critical)**:

- 서비스 전체 다운
- 데이터 손실
- 보안 취약점 발생
- **대응 시간**: 즉시 (24/7)

**P1 (High)**:

- 주요 기능 동작 안 함
- 다수 사용자 영향
- **대응 시간**: 1시간 이내

**P2 (Medium)**:

- 일부 기능 동작 안 함
- 소수 사용자 영향
- **대응 시간**: 4시간 이내

**P3 (Low)**:

- 마이너 버그
- 사용자 영향 미미
- **대응 시간**: 다음 릴리즈

### 10.2 장애 대응 프로세스

#### 1단계: 감지

- CloudWatch 알람
- Slack 알림
- 사용자 제보

#### 2단계: 초기 대응

```bash
# 로그 확인
ssh -i key.pem ec2-user@[서버]
docker logs spring-app --tail 100

# CloudWatch 로그 확인
# AWS Console → CloudWatch → Log groups → /atwoz/prod/application

# 메트릭 확인
# AWS Console → CloudWatch → Dashboards
```

#### 3단계: 임시 조치

- 롤백 (심각한 경우)
- 트래픽 제한 (과부하 시)
- 장애 기능 비활성화 (Feature Flag)

#### 4단계: 근본 원인 분석

- 로그 분석
- 데이터베이스 슬로우 쿼리 확인
- APM 데이터 분석 (선택)

#### 5단계: 수정 및 배포

- hotfix 브랜치 생성
- 수정 후 긴급 배포
- main과 develop 모두에 머지

#### 6단계: Post-mortem 작성

```markdown
# 장애 보고서

## 개요
- 발생 시간: 2025-01-15 14:30
- 종료 시간: 2025-01-15 15:00
- 영향 범위: 전체 사용자 회원가입 불가
- 장애 등급: P1

## 원인
- JWT 토큰 생성 시 시크릿 키 로드 실패

## 조치 사항
- 환경 변수 수정 및 재배포
- 롤백 후 정상화

## 재발 방지
- 환경 변수 검증 로직 추가
- 배포 전 체크리스트 강화
```

---

## 11. Best Practices

### 11.1 코딩 규칙

**DDD 원칙**:

- 비즈니스 로직은 도메인 엔티티에 작성
- Presentation, Application, Domain, Infra 계층 분리
- 도메인 이벤트로 도메인 간 결합도 낮추기

**CQRS 패턴**:

- 쓰기(Command)와 읽기(Query) 분리
- Command는 트랜잭션 처리
- Query는 성능 최적화 (캐싱, 읽기 전용)

**예시**:

```java
// Command
public class CreateMemberCommand {
    private String email;
    private String nickname;
}

// Query
public class MemberQuery {
    public MemberDto findById(Long id) {
        // 읽기 전용, 캐싱 가능
    }
}
```

### 11.2 성능 최적화

**N+1 문제 방지**:

```java
// 나쁜 예
List<Member> members = memberRepository.findAll();
for (Member member : members) {
    member.getIntroductions().size(); // N번 쿼리
}

// 좋은 예
List<Member> members = memberRepository.findAllWithIntroductions(); // Fetch Join
```

**캐싱 전략**:

```java
@Cacheable(value = "members", key = "#id")
public Member findById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(MemberNotFoundException::new);
}
```

### 11.3 보안

**민감 정보 로깅 금지**:

```java
// 나쁜 예
log.info("User password: {}", password);

// 좋은 예
log.info("User login attempt: {}", email);
```

**권한 검증**:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long userId) {
    // ...
}
```

---

## 12. 참고 자료

### 12.1 내부 문서

- `CLAUDE.md`: 프로젝트 개요 및 아키텍처
- `README.md`: 프로젝트 설정 및 실행 방법
- `운영 서버 세팅.md`: AWS 인프라 세팅 가이드 (Obsidian)

### 12.2 외부 자료

- [Spring Boot Best Practices](https://spring.io/guides/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Flow](https://nvie.com/posts/a-successful-git-branching-model/)

---

## 결론

이 가이드를 따라 일관된 개발 프로세스를 유지하면:

- **품질 향상**: 체계적인 리뷰와 테스트로 버그 감소
- **협업 효율**: 명확한 규칙으로 커뮤니케이션 비용 감소
- **안전한 배포**: 자동화와 승인 프로세스로 운영 안정성 확보
- **빠른 대응**: 장애 대응 프로세스로 신속한 복구

**궁금한 점이나 개선 제안은 팀 회의나 Slack에서 논의해주세요!**