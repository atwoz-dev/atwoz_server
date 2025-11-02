# ALB → EC2 직접 연결 전환 - 단계별 실행 가이드

## 개요

- **도메인**: api.atwoz.kr
- **Spring Boot 포트**: 8080 ↔ 8081 (Blue-Green 배포)
- **배포 방식**: deploy_script.sh (sed로 포트 전환)
- **Let's Encrypt 인증서**: 이미 존재 (2025년 5월까지 유효)

## 목표 구조

```
현재: ALB → EC2 Nginx (nginx.conf에 설정) → Spring Boot (8080/8081)
목표: EC2 Nginx (표준 sites-available 구조 + HTTPS) → Spring Boot (8080/8081)
```

---

## Phase 1: 사전 준비 (5분)

### 1.1 현재 상태 확인 및 백업

```bash
# EC2에 SSH 접속
ssh -i ~/.ssh/your-key.pem ubuntu@<EC2-IP>

# 현재 작업 디렉토리 확인
cd ~

# 현재 어떤 포트에서 실행 중인지 확인
docker ps
sudo netstat -tulpn | grep -E '8080|8081'

# Nginx 설정 백업
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup-$(date +%Y%m%d-%H%M%S)
sudo cp /etc/nginx/sites-available/default /etc/nginx/sites-available/default.backup

# 배포 스크립트 백업
cp ~/deploy_script.sh ~/deploy_script.sh.backup

# 백업 확인
ls -la /etc/nginx/*.backup*
ls -la ~/deploy_script.sh.backup

echo "✅ Phase 1 완료: 백업 완료"
```

---

## Phase 2: Nginx 표준 구조로 전환 (10분)

### 2.1 sites-available에 새 설정 파일 생성

```bash
# 새 설정 파일 생성
sudo nano /etc/nginx/sites-available/api.atwoz.kr
```

**다음 내용 붙여넣기:**

```nginx
# Blue-Green 배포: sed로 포트 변경 가능하도록 설정
# deploy_script.sh가 127.0.0.1:8080 <-> 127.0.0.1:8081 교체

# HTTP → HTTPS 리다이렉트
server {
    listen 80;
    listen [::]:80;
    server_name api.atwoz.kr;

    # Let's Encrypt ACME 챌린지 (인증서 갱신용)
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    # 나머지는 HTTPS로 리다이렉트
    location / {
        return 301 https://$host$request_uri;
    }
}

# HTTPS 서버
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name api.atwoz.kr;

    # SSL 인증서 (기존 Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/api.atwoz.kr/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.atwoz.kr/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    # Reverse Proxy to Spring Boot
    # ⚠️ 이 줄을 deploy_script.sh가 수정합니다
    location / {
        proxy_pass http://127.0.0.1:8081;
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
        proxy_connect_timeout 5s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;

        # 버퍼링 비활성화
        proxy_request_buffering off;
        proxy_buffering off;
    }

    # 파일 업로드 크기 제한
    client_max_body_size 50M;
}
```

**Ctrl+O (저장) → Enter → Ctrl+X (종료)**

### 2.2 nginx.conf에서 server 블록 제거

현재 nginx.conf 파일에는 **http 블록 안에 server 블록이 2개** 있습니다. 이 2개를 **모두 삭제**해야 합니다.

```bash
# nginx.conf 편집
sudo nano /etc/nginx/nginx.conf
```

**현재 nginx.conf 구조 (http 블록 안):**

```nginx
http {
    client_max_body_size 50M;
    # ... 기타 설정들 ...

    include /etc/nginx/conf.d/*.conf;
    include /etc/nginx/sites-enabled/*;

    # ========== 여기서부터 삭제 시작 (server 블록 1) ==========
    server {
        server_name ec2-43-200-3-181.ap-northeast-2.compute.amazonaws.com, api.atwoz.kr;

        location / {
            proxy_pass http://127.0.0.1:8081;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_request_buffering off;
        }

        # listen 443 ssl; # managed by Certbot
        # ssl_certificate /etc/letsencrypt/live/api.atwoz.kr/fullchain.pem;
        # ssl_certificate_key /etc/letsencrypt/live/api.atwoz.kr/privkey.pem;
        # include /etc/letsencrypt/options-ssl-nginx.conf;
        # ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;
    }

    # ========== server 블록 2 (이것도 삭제) ==========
    server {
        listen 80;
        server_name api.atwoz.kr;

        location / {
            proxy_pass http://127.0.0.1:8081;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_request_buffering off;
        }
    }
    # ========== 여기까지 삭제 끝 ==========

}  # ← 이 http 블록 닫는 괄호는 남겨둠!
```

**⚠️ 중요: 위의 server 블록 2개를 모두 삭제하세요!**

**삭제 후 최종 모습:**

```nginx
user www-data;
worker_processes auto;
pid /run/nginx.pid;
error_log /var/log/nginx/error.log;
include /etc/nginx/modules-enabled/*.conf;

events {
    worker_connections 768;
}

http {
    client_max_body_size 50M;

    ##
    # Basic Settings
    ##
    sendfile on;
    tcp_nopush on;
    types_hash_max_size 2048;
    server_names_hash_bucket_size 128;

    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    ##
    # SSL Settings
    ##
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;

    ##
    # Logging Settings
    ##
    access_log /var/log/nginx/access.log;

    ##
    # Gzip Settings
    ##
    gzip on;

    ##
    # Virtual Host Configs
    ##
    include /etc/nginx/conf.d/*.conf;
    include /etc/nginx/sites-enabled/*;  # ← 여기서 api.atwoz.kr 설정 불러옴

    # ✅ server 블록 2개 모두 삭제! (이제 sites-available/api.atwoz.kr에 있음)

}  # ← http 블록 닫는 괄호
```

**Ctrl+O → Enter → Ctrl+X (저장 후 종료)**

### 2.3 심볼릭 링크 설정

```bash
# 기존 default 비활성화
sudo rm /etc/nginx/sites-enabled/default

# 새 설정 활성화
sudo ln -s /etc/nginx/sites-available/api.atwoz.kr /etc/nginx/sites-enabled/

# 확인
ls -la /etc/nginx/sites-enabled/
# 출력: api.atwoz.kr -> /etc/nginx/sites-available/api.atwoz.kr

echo "✅ Phase 2 완료: Nginx 표준 구조 전환 완료"
```

---

## Phase 3: 배포 스크립트 수정 (5분)

### 3.1 deploy_script.sh 수정

```bash
# 배포 스크립트 편집
nano ~/deploy_script.sh
```

**3번째 줄 수정:**

```bash
# 변경 전:
NGINX_CONF="/etc/nginx/nginx.conf"

# 변경 후:
NGINX_CONF="/etc/nginx/sites-available/api.atwoz.kr"
```

**전체 스크립트 (수정 후):**

```bash
#!/bin/bash

NGINX_CONF="/etc/nginx/sites-available/api.atwoz.kr"

OLD_PORT=8080
NEW_PORT=8081

echo "Pull Docker Image"
docker pull ggongtae/atwoz:latest

echo "Checking if the server is running on port $OLD_PORT"

if ! curl -s http://127.0.0.1:$OLD_PORT > /dev/null; then
    echo "No Server found on port $OLD_PORT. Changing NEW_PORT TO $OLD_PORT"

    TEMP=$NEW_PORT
    NEW_PORT=$OLD_PORT
    OLD_PORT=$TEMP
else
    echo "Server is already running on port $OLD_PORT."
fi

echo "Starting new container on port $NEW_PORT"
docker run -d \
  -p $NEW_PORT:8080 \
  --env-file .env \
  -v /home/ubuntu/secrets:/etc/credentials:ro \
  -v /home/ubuntu/certs:/etc/certs:ro \
  ggongtae/atwoz:latest

echo "Checking if the server on port $NEW_PORT is up..."

MAX_RETRIES=10
RETRY_INTERVAL=5
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://127.0.0.1:$NEW_PORT > /dev/null; then
        echo "New Server is running on port $NEW_PORT"
        break
    else
        echo "Attempt $((RETRY_COUNT + 1)): New Server not yet available"
        RETRY_COUNT=$((RETRY_COUNT + 1))
        sleep $RETRY_INTERVAL
    fi
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "Error: New Server Failed to start on port $NEW_PORT"
    exit 1
fi

echo "Updating NGINX configuration to route traffic to port $NEW_PORT"
sudo sed -i "s/127.0.0.1:$OLD_PORT/127.0.0.1:$NEW_PORT/" $NGINX_CONF

echo "Reloading NGINX to apply changes"
sudo nginx -t && sudo systemctl reload nginx

echo "Stopping old container..."
docker ps --filter "publish=$OLD_PORT" --format "{{.ID}}" | tee /dev/tty | xargs -I {} docker stop {}

echo "Remove stopped containers..."
docker ps -a --filter "status=exited" --format "{{.ID}}" | tee /dev/tty | xargs -I {} docker rm {}

echo "Remove Dangling Image"
docker image prune -f

echo "Deployment completed. Traffic is now routed to port $NEW_PORT"
exit 0
```

**Ctrl+O → Enter → Ctrl+X**

### 3.2 스크립트 권한 확인

```bash
chmod +x ~/deploy_script.sh

echo "✅ Phase 3 완료: 배포 스크립트 수정 완료"
```

---

## Phase 4: Nginx 설정 테스트 (5분)

### 4.1 문법 검사

```bash
# Nginx 설정 문법 검사
sudo nginx -t

# 예상 출력:
# nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
# nginx: configuration file /etc/nginx/nginx.conf test is successful
```

**❌ 에러 발생 시:**

```bash
# 에러 메시지 확인
sudo nginx -t

# 백업에서 복원
sudo cp /etc/nginx/nginx.conf.backup-* /etc/nginx/nginx.conf
sudo systemctl reload nginx
# 다시 Phase 2부터 진행
```

### 4.2 현재 설정 확인

```bash
# 전체 설정 확인
sudo nginx -T | grep -A 30 "server_name api.atwoz.kr"

# proxy_pass 확인 (현재 포트)
sudo nginx -T | grep "proxy_pass"
# 출력: proxy_pass http://127.0.0.1:8081; (또는 8080)
```

### 4.3 Nginx 재시작 (아직 외부 접속은 안 됨)

```bash
# Nginx reload
sudo systemctl reload nginx

# 상태 확인
sudo systemctl status nginx

# 로그 확인
sudo tail -f /var/log/nginx/error.log
# Ctrl+C로 종료

echo "✅ Phase 4 완료: Nginx 설정 테스트 성공"
```

---

## Phase 5: Route 53 DNS 변경 (10분)

**⚠️ 이 단계부터 실제 트래픽이 영향을 받습니다.**

### 5.1 현재 Route 53 레코드 확인

**AWS Console:**

1. Route 53 → 호스팅 영역 → atwoz.kr
2. `api.atwoz.kr` 레코드 찾기
3. 현재 값 확인:
    - 유형: A 또는 ALIAS
    - 값: ALB DNS 이름 (예: xxx.elb.amazonaws.com)

### 5.2 EC2 Elastic IP 확인

```bash
# EC2에서 Public IP 확인
curl http://checkip.amazonaws.com

# 또는 AWS Console에서:
# EC2 → 인스턴스 → 개발 서버 선택 → 탄력적 IP 확인
```

**Elastic IP 메모:** (예: 43.200.3.181)

### 5.3 Route 53 A 레코드 변경

**AWS Console:**

1. `api.atwoz.kr` 레코드 선택
2. "편집" 클릭
3. **레코드 유형:** A
4. **값:** EC2 Elastic IP (예: 43.200.3.181)
5. **TTL:** 60 (1분) - 빠른 롤백을 위해
6. **라우팅 정책:** 단순 라우팅
7. "변경 사항 저장" 클릭

### 5.4 DNS 전파 확인 (최대 5분)

```bash
# 로컬 컴퓨터에서 실행 (EC2 아님!)
nslookup api.atwoz.kr

# 결과에 EC2 Elastic IP가 나와야 함
# Address: 43.200.3.181 (예시)

# 또는
dig api.atwoz.kr +short
# 출력: 43.200.3.181
```

**DNS가 변경되지 않았다면:**

```bash
# 로컬 DNS 캐시 초기화
# macOS:
sudo dscacheutil -flushcache; sudo killall -HUP mDNSResponder

# Windows:
ipconfig /flushdns

# 1-2분 대기 후 재확인
```

### 5.5 HTTP 접속 테스트 (HTTPS는 아직 안 됨)

```bash
# EC2에서 테스트
curl -I http://api.atwoz.kr

# 예상 결과: 301 Redirect to HTTPS
# HTTP/1.1 301 Moved Permanently
# Location: https://api.atwoz.kr/
```

**❌ 접속 안 되면:**

```bash
# EC2 보안 그룹 확인 필요
# AWS Console → EC2 → 보안 그룹
# 인바운드 규칙에 80, 443 포트가 0.0.0.0/0로 열려있는지 확인
```

---

## Phase 6: EC2 보안 그룹 수정 (5분)

### 6.1 현재 보안 그룹 확인

**AWS Console:**

```
EC2 → 인스턴스 → 개발 서버 선택 → 보안 탭 → 보안 그룹 클릭
```

### 6.2 인바운드 규칙 추가

**필요한 규칙:**

| 유형    | 프로토콜 | 포트 범위 | 소스        | 설명                      |
|-------|------|-------|-----------|-------------------------|
| SSH   | TCP  | 22    | 0.0.0.0/0 | SSH 접속 (이미 있음)          |
| HTTP  | TCP  | 80    | 0.0.0.0/0 | HTTP 트래픽 (HTTPS 리다이렉트용) |
| HTTPS | TCP  | 443   | 0.0.0.0/0 | HTTPS 트래픽               |

**80, 443 포트가 없다면 추가:**

1. 보안 그룹 → 인바운드 규칙 탭
2. "인바운드 규칙 편집" 클릭
3. "규칙 추가" 클릭
    - 유형: HTTP, 포트: 80, 소스: 0.0.0.0/0
4. "규칙 추가" 클릭
    - 유형: HTTPS, 포트: 443, 소스: 0.0.0.0/0
5. "규칙 저장" 클릭

### 6.3 접속 재테스트

```bash
# HTTP 접속
curl -I http://api.atwoz.kr

# 예상: 301 Redirect
```

**✅ 301 응답이 나오면 성공!**

```bash
echo "✅ Phase 6 완료: EC2 보안 그룹 설정 완료"
```

---

## Phase 7: HTTPS 활성화 테스트 (5분)

### 7.1 인증서 유효성 확인

```bash
# EC2에서 실행
sudo certbot certificates

# 출력 예시:
# Certificate Name: api.atwoz.kr
#   Domains: api.atwoz.kr
#   Expiry Date: 2025-05-13 (VALID: 89 days)
```

**인증서가 만료되었다면:**

```bash
# 인증서 갱신
sudo certbot renew

# Nginx reload
sudo systemctl reload nginx
```

### 7.2 HTTPS 접속 테스트

```bash
# 로컬 컴퓨터 또는 EC2에서 실행
curl -I https://api.atwoz.kr

# 예상 출력:
# HTTP/2 401
# server: nginx
# ...
# {"code":"401001","message":"Missing Access Token","status":401}
```

**✅ 401 응답 = HTTPS 작동 성공!** (Spring Boot가 인증 토큰 없어서 401 반환하는 것)

### 7.3 웹 브라우저 테스트

1. 웹 브라우저에서 `https://api.atwoz.kr` 접속
2. 자물쇠 아이콘 확인 (안전한 연결)
3. 인증서 클릭:
    - 발급자: Let's Encrypt
    - 유효 기간 확인

### 7.4 HTTP → HTTPS 리다이렉트 확인

```bash
curl -I http://api.atwoz.kr

# 예상:
# HTTP/1.1 301 Moved Permanently
# Location: https://api.atwoz.kr/
```

**✅ 모두 작동하면 성공!**

```bash
echo "✅ Phase 7 완료: HTTPS 정상 작동 확인"
```

---

## Phase 8: Blue-Green 배포 테스트 (10분)

### 8.1 현재 상태 확인

```bash
# EC2에서 실행
docker ps

# 출력 예시:
# PORTS: 0.0.0.0:8081->8080/tcp

# 현재 Nginx 설정 확인
sudo grep "proxy_pass" /etc/nginx/sites-available/api.atwoz.kr
# 출력: proxy_pass http://127.0.0.1:8081;
```

현재 포트 메모: **8081** (예시)

### 8.2 배포 스크립트 실행 (테스트)

```bash
cd ~
./deploy_script.sh
```

**예상 동작:**

1. Docker 이미지 pull
2. 현재 8081 실행 중 확인
3. 새 컨테이너 8080으로 시작
4. 헬스체크 (최대 50초)
5. Nginx 설정 변경: 127.0.0.1:8081 → 127.0.0.1:8080
6. Nginx reload
7. 기존 8081 컨테이너 종료
8. 정리

### 8.3 배포 후 확인

```bash
# 새 컨테이너 확인
docker ps
# 출력: PORTS: 0.0.0.0:8080->8080/tcp

# Nginx 설정 확인
sudo grep "proxy_pass" /etc/nginx/sites-available/api.atwoz.kr
# 출력: proxy_pass http://127.0.0.1:8080;

# HTTPS 접속 테스트
curl -I https://api.atwoz.kr
# 예상: HTTP/2 401 (정상)
```

**✅ 포트가 8080으로 변경되고 HTTPS 접속 정상이면 성공!**

### 8.4 재배포 테스트 (8080 → 8081)

```bash
# 다시 배포 실행
./deploy_script.sh

# 확인
docker ps
# 출력: PORTS: 0.0.0.0:8081->8080/tcp

# Nginx 설정 확인
sudo grep "proxy_pass" /etc/nginx/sites-available/api.atwoz.kr
# 출력: proxy_pass http://127.0.0.1:8081;

# HTTPS 접속
curl -I https://api.atwoz.kr
# 예상: HTTP/2 401
```

**✅ Blue-Green 배포 정상 작동!**

```bash
echo "✅ Phase 8 완료: Blue-Green 배포 테스트 성공"
```

---

## Phase 9: ALB 리소스 정리 (10분)

**⚠️ HTTPS와 배포가 1-2일 정상 작동하는 것을 확인 후 진행하세요.**

### 9.1 ALB 타겟 그룹에서 EC2 제거

**AWS Console:**

1. EC2 → 로드 밸런싱 → 대상 그룹
2. ALB에 연결된 대상 그룹 선택
3. "대상" 탭
4. EC2 인스턴스 선택
5. "등록 취소" 클릭
6. 확인

### 9.2 ALB 리스너 확인

**AWS Console:**

1. EC2 → 로드 밸런싱 → 로드 밸런서
2. ALB 선택
3. "리스너" 탭
4. 리스너 목록 확인 (HTTP:80, HTTPS:443 등)

### 9.3 ALB 삭제

**AWS Console:**

1. EC2 → 로드 밸런싱 → 로드 밸런서
2. ALB 선택
3. "작업" → "삭제"
4. 삭제 확인 입력: `delete`
5. "삭제" 클릭

**삭제 완료까지 약 5분 소요**

### 9.4 대상 그룹 삭제

**AWS Console:**

1. EC2 → 로드 밸런싱 → 대상 그룹
2. 사용하지 않는 대상 그룹 선택
3. "작업" → "삭제"
4. 확인

### 9.5 ALB 보안 그룹 정리 (선택사항)

더 이상 사용하지 않는다면:

**AWS Console:**

1. EC2 → 네트워크 및 보안 → 보안 그룹
2. ALB 보안 그룹 찾기 (이름에 "alb" 포함)
3. 선택 → "작업" → "보안 그룹 삭제"

**비용 절감:**

```
ALB: $16-20/월 절감
```

```bash
echo "✅ Phase 9 완료: ALB 리소스 정리 완료"
```

---

## Phase 10: 최종 검증 (10분)

### 10.1 전체 체크리스트

```bash
# 1. DNS 확인
nslookup api.atwoz.kr
# 결과: EC2 Elastic IP

# 2. HTTPS 접속
curl -I https://api.atwoz.kr
# 결과: HTTP/2 401 (정상)

# 3. HTTP → HTTPS 리다이렉트
curl -I http://api.atwoz.kr
# 결과: 301 → https://api.atwoz.kr/

# 4. SSL 인증서
curl -vI https://api.atwoz.kr 2>&1 | grep -i 'subject\|issuer'
# 결과: issuer=C=US; O=Let's Encrypt

# 5. Nginx 설정
sudo nginx -T | grep "server_name api.atwoz.kr" -A 5

# 6. 배포 스크립트
cat ~/deploy_script.sh | grep NGINX_CONF
# 결과: NGINX_CONF="/etc/nginx/sites-available/api.atwoz.kr"

# 7. Docker 컨테이너
docker ps
# 결과: 1개 컨테이너 실행 중 (8080 또는 8081)

# 8. Nginx 프로세스
sudo systemctl status nginx
# 결과: active (running)

# 9. Certbot 자동 갱신
sudo certbot renew --dry-run
# 결과: Congratulations, all simulated renewals succeeded
```

### 10.2 모니터링 설정

```bash
# Nginx 로그 실시간 확인
sudo tail -f /var/log/nginx/access.log

# Docker 로그 확인
docker logs -f $(docker ps -q)

# 시스템 리소스
htop
```

### 10.3 Swagger 접속 테스트

```bash
# 웹 브라우저에서 접속
# https://api.atwoz.kr/swagger-ui.html
```

---

## 최종 요약

### ✅ 완료 항목

1. ✅ Nginx 설정을 표준 구조(sites-available)로 전환
2. ✅ Blue-Green 배포 스크립트 수정 (새 경로 적용)
3. ✅ Route 53 DNS를 ALB에서 EC2로 변경
4. ✅ HTTPS (Let's Encrypt) 활성화
5. ✅ Blue-Green 배포 (8080↔8081) 테스트 성공
6. ✅ ALB 리소스 제거 (비용 절감)

### 비용 절감

```
ALB 제거: $16-20/월 절감
현재 비용: EC2 t4g.micro $6/월
```

### 구조 변경

```
변경 전: 인터넷 → ALB → EC2 Nginx (nginx.conf) → Spring Boot
변경 후: 인터넷 → EC2 Nginx (sites-available) → Spring Boot (8080↔8081)
```

---

## 트러블슈팅

### 문제 1: DNS가 변경되지 않음

```bash
# 로컬 DNS 캐시 초기화
# macOS:
sudo dscacheutil -flushcache; sudo killall -HUP mDNSResponder

# Windows:
ipconfig /flushdns

# Route 53 TTL 확인 (60초로 설정했는지)
```

### 문제 2: Nginx 502 Bad Gateway

```bash
# Spring Boot 상태 확인
docker ps
docker logs $(docker ps -q)

# 포트 확인
sudo netstat -tulpn | grep -E '8080|8081'

# Nginx 설정 확인
sudo nginx -T | grep "proxy_pass"

# Spring Boot 재시작
./deploy_script.sh
```

### 문제 3: HTTPS 인증서 오류

```bash
# 인증서 확인
sudo certbot certificates

# 인증서 갱신
sudo certbot renew

# Nginx reload
sudo systemctl reload nginx
```

### 문제 4: 배포 스크립트 실패

```bash
# 로그 확인
./deploy_script.sh

# 권한 확인
ls -la ~/deploy_script.sh

# sed 명령 수동 실행
sudo sed -i 's/127.0.0.1:8080/127.0.0.1:8081/' /etc/nginx/sites-available/api.atwoz.kr
sudo nginx -t
sudo systemctl reload nginx
```

### 긴급 롤백 (Phase 5 이후)

**Route 53을 ALB로 되돌리기:**

1. Route 53 → 호스팅 영역 → api.atwoz.kr
2. A 레코드 편집
3. 유형: ALIAS
4. 값: ALB DNS 이름
5. 저장
6. 5분 대기

---

## 다음 단계 (CI/CD 개선)

ALB 제거 완료 후:

1. **GitHub Actions / Jenkins 설정**
    - ECR 이미지 빌드 자동화
    - EC2 SSH 배포 자동화

2. **모니터링 강화**
    - CloudWatch 알람
    - 로그 수집

3. **백업 자동화**
    - RDS 자동 백업
    - 설정 파일 버전 관리

---

## 연락처

문제 발생 시:

- Nginx 로그: `/var/log/nginx/error.log`
- Docker 로그: `docker logs <container-id>`
- 이 문서: `/Users/hoyunjung/Developer/atwoz_server/docs/ALB_TO_EC2_MIGRATION_STEPBYSTEP.md`