# 배포 가이드 (Deployment Guide)

이 문서는 atwoz 서버의 배포 프로세스와 인프라 구성에 대한 상세 가이드입니다.

## 목차

- [개요](#개요)
- [배포 아키텍처](#배포-아키텍처)
- [개발 서버 배포](#개발-서버-배포)
- [프로덕션 서버 배포](#프로덕션-서버-배포)
- [트러블슈팅](#트러블슈팅)

---

## 개요

### 배포 전략

- **CI/CD 도구**: GitHub Actions
- **컨테이너**: Docker
- **레지스트리**: AWS ECR
- **배포 방식**: Blue-Green 무중단 배포
- **인프라**: AWS EC2, nginx

### 환경별 서버

| 환경   | 브랜치       | 도메인                   | 워크플로우         |
|------|-----------|-----------------------|---------------|
| 개발   | `develop` | dev-api.deepple.co.kr | `cd-dev.yml`  |
| 프로덕션 | `main`    | api.atwoz.kr          | `cd-prod.yml` |

---

## 배포 아키텍처

### Blue-Green 무중단 배포 흐름

```
[GitHub Push]
     ↓
[GitHub Actions 트리거]
     ↓
[1. Docker 이미지 빌드]
     ↓
[2. AWS ECR에 푸시]
     ↓
[3. EC2에서 이미지 pull]
     ↓
[4. 비활성 슬롯에 새 컨테이너 시작]
   Blue(8081) 또는 Green(8082)
     ↓
[5. Health Check (최대 60초)]
   ├─ 성공 → 다음 단계
   └─ 실패 → 배포 중단, 기존 컨테이너 유지
     ↓
[6. nginx proxy_pass 포트 전환]
     ↓
[7. nginx graceful reload]
   (기존 연결 유지, 새 연결은 새 컨테이너로)
     ↓
[8. 기존 컨테이너 제거]
     ↓
[배포 완료]
```

### 인프라 구성

```
                    [사용자]
                       ↓
                  [nginx:80/443]
                   (HTTPS, SSL)
                       ↓
        ┌──────────────┴──────────────┐
        ↓                             ↓
   [Blue Container]            [Green Container]
     Port: 8081                  Port: 8082
   deepple-app-blue          deepple-app-green
        ↓                             ↓
        └──────────────┬──────────────┘
                       ↓
                  [Docker Network]
                       ↓
              ┌────────┴────────┐
              ↓                 ↓
          [MySQL]           [Redis]
```

---

## 개발 서버 배포

### 자동 배포

**트리거 조건**

- `develop` 브랜치에 push
- `.md`, `docs/`, `.gitignore`, `LICENSE`, `.env.example` 파일 변경은 제외

**워크플로우 파일**: `.github/workflows/cd-dev.yml`

### 배포 프로세스 상세

#### 1. Docker 이미지 빌드 및 푸시

```yaml
- Docker Buildx 사용
- 플랫폼: linux/amd64
- 태그: dev-latest, dev-{git-sha}
- 캐시: GitHub Actions cache 활용
```

#### 2. EC2 배포 (SSM 사용)

```bash
# 현재 활성 슬롯 확인
if [Blue 컨테이너 실행 중]; then
  TARGET=Green (8082)
else
  TARGET=Blue (8081)
fi

# 새 컨테이너 시작
docker run -d \
  --name deepple-app-{TARGET} \
  -p {TARGET_PORT}:8080 \
  -v /home/ubuntu/secrets:/etc/credentials:ro \
  -v /home/ubuntu/certs:/etc/certs:ro \
  --env-file /home/ubuntu/.env \
  --restart unless-stopped \
  {ECR_IMAGE}
```

#### 3. Health Check

```bash
# 30회 재시도, 2초 간격 (최대 60초)
for i in 1..30; do
  curl -f http://localhost:{TARGET_PORT}/actuator/health
  if success; then break; fi
  sleep 2
done
```

#### 4. nginx 전환

```bash
# /etc/nginx/sites-available/dev-api.deepple.co.kr 수정
sed -i "s|proxy_pass http://127.0.0.1:[0-9]\+;|proxy_pass http://127.0.0.1:{TARGET_PORT};|g"

# nginx 검증 및 reload
nginx -t && nginx -s reload
```

#### 5. 정리

```bash
# 기존 컨테이너 제거
docker stop deepple-app-{OLD_SLOT}
docker rm deepple-app-{OLD_SLOT}

# 오래된 이미지 정리 (24시간 이상)
docker image prune -af --filter 'until=24h'
```

### 배포 모니터링

**GitHub Actions 로그**

```bash
# CLI로 확인
gh run list --workflow=cd-dev.yml
gh run view {run-id} --log

# 웹에서 확인
https://github.com/atwoz-dev/atwoz_server/actions
```

**주요 로그 포인트**

1. `Deployment output:` - EC2에서 실행된 전체 로그
2. `Health check attempt X/30` - Health check 진행 상황
3. `Nginx reloaded - traffic switched to {SLOT}` - 전환 완료
4. `Deployment completed successfully!` - 배포 성공

### 배포 실패 시 자동 롤백

Health check 실패 시:

```bash
ERROR: {TARGET} container health check failed after 30 attempts
Container logs:
[컨테이너 로그 100줄 출력]

Removing failed {TARGET} container...
{CURRENT} container remains running - ZERO downtime maintained
```

**결과**: 기존 컨테이너가 계속 실행되어 다운타임 0초 유지

---

## 프로덕션 서버 배포

### 자동 배포

**트리거 조건**

- `main` 브랜치에 push
- 워크플로우 파일: `.github/workflows/cd-prod.yml`

**배포 프로세스**

- 개발 서버와 동일한 Blue-Green 전략 사용
- 도메인: api.atwoz.kr
- nginx 설정 파일: `/etc/nginx/sites-available/api.atwoz.kr`

---

## 수동 배포

긴급 상황이나 롤백이 필요한 경우:

### 1. 특정 이미지로 배포

```bash
# EC2 접속
ssh ubuntu@{EC2_IP}

# ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | \
  docker login --username AWS --password-stdin {ECR_REGISTRY}

# 이미지 pull
docker pull {ECR_REGISTRY}/deepple:dev-{SHA}

# 비활성 슬롯에 컨테이너 시작
docker run -d \
  --name deepple-app-blue \
  -p 8081:8080 \
  -v /home/ubuntu/secrets:/etc/credentials:ro \
  -v /home/ubuntu/certs:/etc/certs:ro \
  --env-file /home/ubuntu/.env \
  --restart unless-stopped \
  {ECR_REGISTRY}/deepple:dev-{SHA}

# Health check
curl http://localhost:8081/actuator/health

# nginx 전환
sudo sed -i 's|proxy_pass http://127.0.0.1:[0-9]\+;|proxy_pass http://127.0.0.1:8081;|g' \
  /etc/nginx/sites-available/dev-api.deepple.co.kr
sudo nginx -t && sudo nginx -s reload

# 기존 컨테이너 제거
docker stop deepple-app-green
docker rm deepple-app-green
```

### 2. 이전 버전으로 롤백

```bash
# ECR에서 이전 이미지 태그 확인
aws ecr describe-images \
  --repository-name deepple \
  --query 'sort_by(imageDetails,& imagePushedAt)[-10:]' \
  --output table

# 위 1번 과정으로 이전 SHA 이미지 배포
```

---

## nginx 설정

### 개발 서버 설정

**파일 위치**: `/etc/nginx/sites-available/dev-api.deepple.co.kr`

```nginx
server {
    listen 443 ssl http2;
    server_name dev-api.deepple.co.kr;

    ssl_certificate /etc/letsencrypt/live/dev-api.deepple.co.kr/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/dev-api.deepple.co.kr/privkey.pem;

    location / {
        proxy_pass http://127.0.0.1:8081;  # ← Blue-Green 배포 시 8081 ↔ 8082 전환
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        proxy_connect_timeout 5s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    client_max_body_size 50M;
}
```

---

## 환경 변수 관리

### EC2에서 환경 변수 업데이트

```bash
# GitHub Secrets의 DEV_ENV 값을 EC2의 .env 파일에 자동 동기화
# 워크플로우에서 자동 처리되므로 수동 작업 불필요

# 수동으로 업데이트가 필요한 경우
vi /home/ubuntu/.env
# 수정 후 컨테이너 재시작
docker restart deepple-app-blue  # 또는 deepple-app-green
```

---

## 트러블슈팅

### 1. 배포가 시작되지 않음

**증상**: develop 브랜치에 push했는데 워크플로우가 트리거되지 않음

**원인 및 해결**:

- `.md` 파일만 변경: `paths-ignore`에 포함되어 무시됨
- 워크플로우 파일 문법 오류: GitHub Actions 탭에서 오류 확인
- 브랜치 보호 규칙: GitHub 레포지토리 설정 확인

### 2. Health Check 실패

**증상**: `ERROR: container health check failed after 30 attempts`

**원인**:

- 컨테이너 시작 실패 (메모리 부족, 환경 변수 오류 등)
- Actuator 비활성화
- 포트 충돌

**해결**:

```bash
# 컨테이너 로그 확인 (워크플로우 로그에 포함)
docker logs deepple-app-{SLOT} --tail 100

# 로컬에서 직접 확인
ssh ubuntu@{EC2_IP}
docker logs deepple-app-blue -f
```

### 3. nginx 전환 실패

**증상**: `ERROR: Nginx configuration test failed`

**원인**:

- nginx 설정 파일 문법 오류
- proxy_pass 라인 형식이 예상과 다름

**해결**:

```bash
# nginx 설정 확인
sudo nginx -t

# 설정 파일 직접 확인
sudo cat /etc/nginx/sites-available/dev-api.deepple.co.kr

# 수동으로 복구
sudo vi /etc/nginx/sites-available/dev-api.deepple.co.kr
# proxy_pass http://127.0.0.1:8081; (또는 8082)로 수정
sudo nginx -s reload
```

### 4. 컨테이너는 정상이지만 트래픽이 안 옴

**증상**: Health check는 성공했지만 실제 사용자 요청이 처리되지 않음

**원인**:

- nginx reload 실패
- 방화벽/보안그룹 설정

**해결**:

```bash
# nginx 상태 확인
sudo systemctl status nginx

# nginx 프로세스 확인
ps aux | grep nginx

# nginx 수동 reload
sudo nginx -s reload

# 포트 리스닝 확인
sudo netstat -tlnp | grep nginx
sudo netstat -tlnp | grep 808
```

### 5. 컨테이너 메모리 부족

**증상**: 컨테이너가 자주 재시작되거나 OOM Killed 발생

**해결**:

```bash
# EC2 메모리 사용량 확인
free -h
docker stats

# 오래된 컨테이너/이미지 정리
docker system prune -a
```

---

## 보안 고려사항

### Secrets 관리

- GitHub Secrets에 민감 정보 저장:
    - `AWS_ROLE_ARN`: AWS OIDC Role ARN
    - `DEV_EC2_INSTANCE_ID`: EC2 인스턴스 ID
    - `DEV_ENV`: 환경 변수 전체 내용
    - `PROD_*`: 프로덕션 서버 secrets

### EC2 권한

- ubuntu 유저는 `sudo NOPASSWD: ALL` 권한 보유
- SSM을 통해서만 명령 실행 (SSH 직접 접근 최소화)

### 인증서 관리

- Let's Encrypt 자동 갱신 설정 필요
- 인증서 경로: `/etc/letsencrypt/live/{DOMAIN}/`

---

## 참고 자료

- [GitHub Actions 문서](https://docs.github.com/en/actions)
- [AWS ECR 문서](https://docs.aws.amazon.com/ecr/)
- [AWS SSM 문서](https://docs.aws.amazon.com/systems-manager/)
- [nginx 문서](https://nginx.org/en/docs/)
- [Docker 문서](https://docs.docker.com/)