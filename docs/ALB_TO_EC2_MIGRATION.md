# ALB → EC2 직접 연결 전환 가이드

## 개요

현재 ALB를 통해 트래픽을 받고 있는 구조에서 EC2로 직접 트래픽을 받는 구조로 전환하는 가이드입니다.

### 현재 구조

```
인터넷 → ALB (HTTPS/HTTP) → Target Group → EC2 (Nginx) → Spring Boot (8080)
```

### 목표 구조

```
인터넷 → Route 53 → EC2 Elastic IP (HTTPS 443) → Nginx → Spring Boot (8080)
```

### 전환 이유

- ALB 비용 절감 ($16-20/월 절약)
- 개발 서버에는 단일 EC2로 충분
- 운영 서버는 ALB + Auto Scaling 유지 예정

---

## 사전 준비

### 1. 현재 상태 백업

다음 정보를 기록해두세요:

```bash
# 1. 현재 도메인 확인
nslookup dev.atwoz.com  # 또는 사용 중인 도메인

# 2. 현재 Nginx 설정 백업
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup
sudo cp /etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf.backup  # 있다면

# 3. 현재 설정 확인
sudo nginx -T > ~/nginx-config-backup.txt

# 4. 현재 보안 그룹 ID 확인 (AWS Console에서)
# EC2 → 인스턴스 → 보안 탭에서 보안 그룹 ID 메모
```

### 2. 필요 정보 확인

- [ ] 사용 중인 도메인명 (예: dev.atwoz.com)
- [ ] EC2 인스턴스 ID
- [ ] 현재 EC2 보안 그룹 ID
- [ ] Route 53 호스팅 영역 ID
- [ ] 현재 ALB DNS 이름

---

## Step 1: EC2 Elastic IP 할당 (없는 경우)

### 1.1 Elastic IP가 이미 있는지 확인

```bash
# EC2 콘솔에서 확인
EC2 → 네트워크 및 보안 → 탄력적 IP

# 또는 CLI로 확인
aws ec2 describe-addresses --region ap-northeast-2
```

### 1.2 Elastic IP 할당 (없다면)

**AWS Console:**

1. EC2 → 네트워크 및 보안 → 탄력적 IP
2. "탄력적 IP 주소 할당" 클릭
3. 네트워크 경계 그룹: ap-northeast-2
4. "할당" 클릭
5. 할당된 IP 주소 메모 (예: 3.35.123.45)

### 1.3 EC2 인스턴스에 연결

**AWS Console:**

1. 탄력적 IP 선택
2. 작업 → 탄력적 IP 주소 연결
3. 리소스 유형: 인스턴스
4. 인스턴스: 개발 서버 EC2 선택
5. "연결" 클릭

**중요:** Elastic IP 연결 시 EC2의 Public IP가 변경되므로, SSH 재접속 필요합니다.

```bash
# 새로운 Elastic IP로 SSH 접속
ssh -i ~/.ssh/your-key.pem ec2-user@3.35.123.45
```

---

## Step 2: EC2 보안 그룹 수정

### 2.1 현재 보안 그룹 확인

**AWS Console:**

```
EC2 → 인스턴스 → 개발 서버 선택 → 보안 탭 → 보안 그룹 클릭
```

### 2.2 인바운드 규칙 추가

기존에 ALB에서만 받던 트래픽을 이제 인터넷에서 직접 받아야 합니다.

**추가할 규칙:**

| 유형    | 프로토콜 | 포트 범위 | 소스        | 설명                       |
|-------|------|-------|-----------|--------------------------|
| HTTP  | TCP  | 80    | 0.0.0.0/0 | HTTP 트래픽 (HTTPS로 리다이렉트용) |
| HTTP  | TCP  | 80    | ::/0      | HTTP 트래픽 IPv6            |
| HTTPS | TCP  | 443   | 0.0.0.0/0 | HTTPS 트래픽                |
| HTTPS | TCP  | 443   | ::/0      | HTTPS 트래픽 IPv6           |

**기존 규칙 확인:**

- SSH (22): 0.0.0.0/0 → 유지
- Spring Boot (8080): ALB 보안 그룹 소스 → **삭제 또는 0.0.0.0/0로 변경 (테스트용)**

**AWS Console 작업:**

1. 보안 그룹 → 인바운드 규칙 탭
2. "인바운드 규칙 편집" 클릭
3. 위 4개 규칙 추가
4. "규칙 저장" 클릭

---

## Step 3: Nginx 설정 수정

### 3.1 현재 Nginx 설정 확인

```bash
# EC2에 SSH 접속
ssh -i ~/.ssh/your-key.pem ec2-user@3.35.123.45

# Nginx 설정 파일 위치 확인
sudo nginx -T | grep "configuration file"

# 일반적으로 다음 중 하나:
# /etc/nginx/nginx.conf
# /etc/nginx/conf.d/default.conf
# /etc/nginx/sites-enabled/default
```

### 3.2 현재 설정 예시 (ALB 사용 시)

기존 설정은 아마 이런 형태일 것입니다:

```nginx
server {
    listen 80;
    server_name dev.atwoz.com;

    # ALB에서 넘어온 헤더 처리
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 3.3 새 Nginx 설정 (EC2 직접 연결 + HTTPS)

**Step 3.3.1: 먼저 HTTP만 설정 (Let's Encrypt 인증서 발급용)**

```bash
# Nginx 설정 파일 편집 (파일 경로는 실제 확인 후 사용)
sudo vi /etc/nginx/conf.d/default.conf
```

**임시 HTTP 설정:**

```nginx
server {
    listen 80;
    server_name dev.atwoz.com;  # 실제 도메인으로 변경

    # Let's Encrypt ACME 챌린지용 (인증서 발급 시 필요)
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    # 나머지 트래픽은 Spring Boot로
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 지원 (필요시)
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        # 타임아웃 설정
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 파일 업로드 크기 제한 (application-*.yml의 max-http-form-post-size와 맞춤)
    client_max_body_size 50M;
}
```

**설정 적용:**

```bash
# 설정 파일 문법 검사
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx

# Nginx 상태 확인
sudo systemctl status nginx
```

---

## Step 4: Route 53 DNS 레코드 변경

### 4.1 현재 DNS 레코드 확인

**AWS Console:**

```
Route 53 → 호스팅 영역 → atwoz.com (실제 도메인) → 레코드
```

현재 `dev.atwoz.com` 레코드를 찾으세요:

- 유형: A 또는 ALIAS
- 값: ALB DNS 이름 (예: my-alb-123456.ap-northeast-2.elb.amazonaws.com)

### 4.2 DNS 레코드 변경

**중요:** 이 단계부터 트래픽이 EC2로 직접 들어옵니다.

**AWS Console 작업:**

1. `dev.atwoz.com` 레코드 선택
2. "편집" 클릭
3. **레코드 유형:** A
4. **값:** EC2 Elastic IP (예: 3.35.123.45)
5. **TTL:** 300 (5분) - 문제 발생 시 빠른 롤백을 위해
6. **라우팅 정책:** 단순 라우팅
7. "변경 사항 저장" 클릭

### 4.3 DNS 전파 확인

```bash
# DNS 전파 확인 (최대 5분 소요)
nslookup dev.atwoz.com

# 또는
dig dev.atwoz.com

# 결과에 EC2 Elastic IP가 나와야 함
# 예: Answer Section에 3.35.123.45
```

### 4.4 HTTP 접속 테스트

```bash
# HTTP로 접속 테스트 (HTTPS는 아직 설정 전)
curl -I http://dev.atwoz.com

# Spring Boot Health Check
curl http://dev.atwoz.com/actuator/health

# 웹 브라우저로도 확인
# http://dev.atwoz.com
```

**예상 결과:**

```
HTTP/1.1 200 OK
Server: nginx
...
```

**문제 발생 시 롤백:**

```
Route 53에서 A 레코드를 다시 ALB ALIAS로 변경
```

---

## Step 5: Let's Encrypt SSL 인증서 설정

### 5.1 Certbot 설치

```bash
# EC2에 SSH 접속
ssh -i ~/.ssh/your-key.pem ec2-user@<EC2 Elastic IP>

# EPEL 리포지토리 추가
sudo yum install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-9.noarch.rpm

# Certbot 및 Nginx 플러그인 설치
sudo yum install -y certbot python3-certbot-nginx

# 설치 확인
certbot --version
```

### 5.2 Certbot으로 SSL 인증서 발급

**자동 설정 방식 (권장):**

```bash
# Certbot이 자동으로 Nginx 설정 수정 및 인증서 발급
sudo certbot --nginx -d dev.atwoz.com

# 프롬프트 응답:
# 1. 이메일 주소 입력: your-email@example.com
# 2. 약관 동의: Y
# 3. 마케팅 이메일 수신: N (선택사항)
# 4. HTTP를 HTTPS로 리다이렉트 할지: 2 (Redirect)
```

**수동 설정 방식:**

```bash
# 인증서만 발급 (Nginx 설정은 수동)
sudo certbot certonly --nginx -d dev.atwoz.com
```

### 5.3 인증서 발급 확인

```bash
# 인증서 파일 확인
sudo ls -la /etc/letsencrypt/live/dev.atwoz.com/

# 다음 파일들이 생성되어야 함:
# - fullchain.pem (인증서 체인)
# - privkey.pem (개인 키)
# - cert.pem (인증서)
# - chain.pem (중간 인증서)
```

### 5.4 Nginx HTTPS 설정 (자동 설정 확인 또는 수동 설정)

**Certbot이 자동으로 수정한 설정 확인:**

```bash
sudo nginx -T
```

**또는 수동으로 HTTPS 설정:**

```bash
sudo vi /etc/nginx/conf.d/default.conf
```

**최종 Nginx 설정:**

```nginx
# HTTP → HTTPS 리다이렉트
server {
    listen 80;
    server_name dev.atwoz.com;

    # Let's Encrypt ACME 챌린지는 HTTP로 유지
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    # 나머지는 HTTPS로 리다이렉트
    location / {
        return 301 https://$server_name$request_uri;
    }
}

# HTTPS 서버
server {
    listen 443 ssl http2;
    server_name dev.atwoz.com;

    # SSL 인증서 설정
    ssl_certificate /etc/letsencrypt/live/dev.atwoz.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/dev.atwoz.com/privkey.pem;

    # SSL 보안 설정 (Certbot이 자동 생성)
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    # Reverse Proxy to Spring Boot
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;

        # WebSocket 지원
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        # 타임아웃 설정
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 파일 업로드 크기 제한
    client_max_body_size 50M;
}
```

**설정 적용:**

```bash
# 설정 검사
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx

# 상태 확인
sudo systemctl status nginx
```

### 5.5 자동 갱신 설정 확인

Let's Encrypt 인증서는 90일마다 갱신이 필요합니다. Certbot이 자동으로 갱신 타이머를 설정합니다.

```bash
# 자동 갱신 타이머 확인
sudo systemctl status certbot-renew.timer

# 자동 갱신 테스트 (dry-run)
sudo certbot renew --dry-run

# 예상 출력:
# Congratulations, all simulated renewals succeeded
```

**자동 갱신 타이머가 없다면:**

```bash
# Cron으로 자동 갱신 설정
sudo crontab -e

# 다음 라인 추가 (매일 오전 3시 갱신 시도)
0 3 * * * certbot renew --quiet --post-hook "systemctl reload nginx"
```

---

## Step 6: HTTPS 접속 테스트

### 6.1 기본 테스트

```bash
# HTTPS 접속 테스트
curl -I https://dev.atwoz.com

# 인증서 정보 확인
curl -vI https://dev.atwoz.com 2>&1 | grep -i 'subject\|issuer\|expire'

# Health Check
curl https://dev.atwoz.com/actuator/health

# HTTP → HTTPS 리다이렉트 확인
curl -I http://dev.atwoz.com
# 예상: HTTP/1.1 301 Moved Permanently
# Location: https://dev.atwoz.com/
```

### 6.2 웹 브라우저 테스트

1. 웹 브라우저에서 `https://dev.atwoz.com` 접속
2. 자물쇠 아이콘 확인 (안전한 연결)
3. 인증서 정보 확인:
    - 발급자: Let's Encrypt
    - 유효 기간: 90일

### 6.3 SSL 등급 확인 (선택사항)

```bash
# SSL Labs 테스트 (웹 브라우저에서)
# https://www.ssllabs.com/ssltest/analyze.html?d=dev.atwoz.com

# 목표: A 또는 A+ 등급
```

---

## Step 7: ALB 리소스 정리

**HTTPS가 정상 작동하는 것을 최소 1-2일 확인 후 진행하세요.**

### 7.1 ALB 타겟 그룹에서 EC2 제거

**AWS Console:**

1. EC2 → 로드 밸런싱 → 대상 그룹
2. ALB에 연결된 대상 그룹 선택
3. "대상" 탭 → EC2 인스턴스 선택
4. "등록 취소" 클릭

### 7.2 ALB 리스너 삭제

**AWS Console:**

1. EC2 → 로드 밸런싱 → 로드 밸런서
2. ALB 선택
3. "리스너" 탭
4. HTTP:80, HTTPS:443 리스너 선택
5. "삭제" 클릭

### 7.3 ALB 삭제

**AWS Console:**

1. EC2 → 로드 밸런싱 → 로드 밸런서
2. ALB 선택
3. "작업" → "삭제"
4. 삭제 확인

**비용 절감:**

```
ALB 기본 비용: $16.20/월
+ 데이터 전송: ~$1-2/월
총 절감: ~$18/월
```

### 7.4 대상 그룹 삭제

**AWS Console:**

1. EC2 → 로드 밸런싱 → 대상 그룹
2. 사용하지 않는 대상 그룹 선택
3. "작업" → "삭제"

### 7.5 ALB 보안 그룹 삭제 (선택사항)

더 이상 사용하지 않는다면:

1. EC2 → 네트워크 및 보안 → 보안 그룹
2. ALB 보안 그룹 선택
3. "작업" → "보안 그룹 삭제"

---

## Step 8: 최종 검증

### 8.1 체크리스트

- [ ] HTTPS 접속 정상 (https://dev.atwoz.com)
- [ ] HTTP → HTTPS 리다이렉트 정상
- [ ] SSL 인증서 유효 (Let's Encrypt)
- [ ] API 엔드포인트 정상 작동
- [ ] Swagger UI 접속 정상 (https://dev.atwoz.com/swagger-ui.html)
- [ ] 파일 업로드 테스트 (50MB 이하)
- [ ] WebSocket 연결 테스트 (사용 시)
- [ ] Certbot 자동 갱신 설정 확인

### 8.2 모니터링

```bash
# Nginx 로그 모니터링
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log

# Spring Boot 로그 모니터링
docker logs -f <container-name>

# 시스템 리소스 확인
htop
```

### 8.3 문제 발생 시 롤백 절차

**긴급 롤백 (5분 이내):**

1. Route 53 A 레코드를 ALB ALIAS로 되돌리기
2. ALB 타겟 그룹에 EC2 다시 등록
3. ALB 리스너 재생성

**Nginx 문제 시:**

```bash
# 백업 설정으로 복원
sudo cp /etc/nginx/nginx.conf.backup /etc/nginx/nginx.conf
sudo nginx -t
sudo systemctl restart nginx
```

---

## Step 9: 다음 단계 (CI/CD 개선)

ALB 제거가 완료되면 다음 작업 진행:

1. **GitHub Actions 또는 Jenkins 설정**
    - ECR 이미지 빌드 자동화
    - EC2 배포 자동화

2. **Blue-Green 배포 구현**
    - Docker Compose로 무중단 배포

3. **모니터링 강화**
    - CloudWatch 알람 설정
    - 로그 수집 (CloudWatch Logs)

---

## 트러블슈팅

### 1. DNS가 Elastic IP로 변경되지 않음

```bash
# DNS 캐시 초기화 (로컬 컴퓨터)
# macOS
sudo dscacheutil -flushcache; sudo killall -HUP mDNSResponder

# Windows
ipconfig /flushdns

# Linux
sudo systemd-resolve --flush-caches
```

### 2. Nginx 502 Bad Gateway

```bash
# Spring Boot 컨테이너 상태 확인
docker ps

# Spring Boot 로그 확인
docker logs <container-name>

# Spring Boot 재시작
docker restart <container-name>

# 포트 리스닝 확인
sudo netstat -tulpn | grep 8080
```

### 3. Let's Encrypt 인증서 발급 실패

```bash
# 80 포트가 열려있는지 확인
sudo netstat -tulpn | grep :80

# DNS가 올바른 IP를 가리키는지 확인
nslookup dev.atwoz.com

# Certbot 로그 확인
sudo tail -f /var/log/letsencrypt/letsencrypt.log

# 수동으로 재시도
sudo certbot --nginx -d dev.atwoz.com --debug
```

### 4. HTTPS 접속 시 인증서 오류

```bash
# Nginx에서 올바른 인증서 경로 사용하는지 확인
sudo nginx -T | grep ssl_certificate

# 인증서 파일 권한 확인
sudo ls -la /etc/letsencrypt/live/dev.atwoz.com/

# Nginx 재시작
sudo systemctl restart nginx
```

---

## 요약

### 전환 전

- 구조: ALB → EC2 (Nginx) → Spring Boot
- 비용: ALB $18/월 + EC2 $6/월 = $24/월

### 전환 후

- 구조: EC2 (Nginx + HTTPS) → Spring Boot
- 비용: Elastic IP $0 (사용 중) + EC2 $6/월 = $6/월
- **월 $18 절감**

### 핵심 변경사항

1. ✅ Elastic IP로 고정 IP 할당
2. ✅ EC2 보안 그룹에 80, 443 포트 오픈
3. ✅ Route 53 A 레코드를 ALB → EC2 IP로 변경
4. ✅ Nginx에서 직접 HTTPS 처리 (Let's Encrypt)
5. ✅ ALB 리소스 제거로 비용 절감