# AWS 세팅 가이드

이 가이드는 DEEPPLE 프로젝트의 운영 환경 AWS를 처음부터 구축하는 단계별 가이드입니다.

## 목차

1. [환경 구성 개요](#1-환경-구성-개요)
2. [AWS 계정 및 IAM 설정](#2-aws-계정-및-iam-설정)
3. [네트워크 구성 (VPC)](#3-네트워크-구성-vpc)
4. [데이터베이스 (RDS)](#4-데이터베이스-rds)
5. [캐시 서버 (ElastiCache)](#5-캐시-서버-elasticache)
6. [스토리지 (S3)](#6-스토리지-s3)
7. [컴퓨팅 리소스 (EC2)](#7-컴퓨팅-리소스-ec2)
8. [로드 밸런서 (ALB)](#8-로드-밸런서-alb)
9. [도메인 및 SSL](#9-도메인-및-ssl)

---

## 1. 환경 구성 개요

### 1.1 환경 전략

| 환경    | 브랜치       | 인프라            | 배포 방식             | 목적     |
|-------|-----------|----------------|-------------------|--------|
| local | feature/* | Docker Compose | 수동                | 로컬 개발  |
| dev   | develop   | AWS            | 자동 (develop 머지 시) | QA     |
| prod  | main      | AWS            | 자동 (main 머지 시)    | 운영 서비스 |

### 1.2 AWS 리소스 구성

```
인터넷
  ↓
Route 53 (도메인)
  ↓
ALB (로드밸런서)
  ↓
EC2 (애플리케이션)
  ↓
RDS (MySQL) + ElastiCache (Redis) + S3 (파일 저장소)
```

### 1.3 비용 예상 (월간)

**개발 환경 (dev)**

- EC2 t3.small: $15
- RDS db.t3.micro (Single-AZ): $15
- ElastiCache cache.t3.micro: $12
- NAT Gateway: $40
- 기타: $5
- **합계: 약 $87/월 (~₩116,000)**

**운영 환경 (prod)**

- EC2 t3.medium: $30
- RDS db.t3.medium (Multi-AZ): $120
- ElastiCache cache.t3.small: $50
- NAT Gateway: $40
- ALB: $20
- Route 53: $1
- 기타: $10
- **합계: 약 $271/월 (~₩362,000)**

---

## 2. AWS 계정 및 IAM 설정

### 2.1 리전 선택

⚠️ **중요**: 모든 작업 전에 반드시 **서울 리전 (ap-northeast-2)** 선택!

### 2.2 IAM Role 생성

**IAM Role이란?**
EC2 인스턴스가 AWS 서비스(S3, CloudWatch 등)에 안전하게 접근할 수 있도록 권한을 부여합니다.

**왜 IAM Role을 사용하나?**

- 코드에 액세스 키를 넣지 않아도 됨 (보안 강화)
- EC2에서 자동으로 AWS 서비스 접근
- 자격 증명 자동 회전

**생성 단계**:

1. IAM 콘솔 → 역할 → 역할 만들기
2. 신뢰할 수 있는 엔터티: **AWS 서비스** → **EC2**
3. 권한 정책 연결:
    - `AmazonS3FullAccess` (파일 업로드/다운로드)
    - `CloudWatchLogsFullAccess` (로그 전송)
    - `CloudWatchAgentServerPolicy` (선택, 메트릭 전송)
    - `AmazonSSMManagedInstanceCore` (선택, SSH 없이 접속)
4. 역할 이름: `deepple-prod-app-role`
5. 생성 완료

---

## 3. 네트워크 구성 (VPC)

**VPC란?**
Virtual Private Cloud는 AWS 클라우드에서 논리적으로 격리된 네트워크 공간입니다. 여러분만의 데이터 센터를 클라우드에 만드는 것과 같습니다.

**퍼블릭 vs 프라이빗 서브넷**

- **퍼블릭 서브넷**: 인터넷에서 직접 접근 가능 (ALB, EC2 배치)
- **프라이빗 서브넷**: 인터넷에서 직접 접근 불가 (RDS, ElastiCache 배치, 보안 강화)

### 3.1 VPC 마법사로 한 번에 생성

VPC 마법사를 사용하면 VPC, 서브넷, 인터넷 게이트웨이, 라우팅 테이블을 한 번에 생성 가능

**생성 단계**:

1. **VPC 콘솔** → VPC → **VPC 생성**
2. **생성할 리소스**: "VPC 등" 선택

**3. VPC 설정**:

```
이름 태그: deepple-prod
IPv4 CIDR: 10.0.0.0/16
IPv6 CIDR: 없음
테넌시: 기본값
```

**4. 가용 영역 설정**:

```
가용 영역(AZ) 수: 2

왜 2개 AZ를 사용하나?
- 한 AZ 장애 시에도 다른 AZ에서 서비스 계속 가능 (고가용성)
```

**5. 서브넷 설정**:

```
퍼블릭 서브넷 수: 2 (ALB, EC2용)
프라이빗 서브넷 수: 2 (RDS, ElastiCache용)

자동 설정되는 CIDR:
  퍼블릭:
    - ap-northeast-2a: 10.0.0.0/20
    - ap-northeast-2c: 10.0.16.0/20
  프라이빗:
    - ap-northeast-2a: 10.0.128.0/20
    - ap-northeast-2c: 10.0.144.0/20
```

**6. NAT 게이트웨이**:

```
권장: "1개 AZ에" 선택 (비용: ~$40/월)

왜 NAT Gateway가 필요한가?
- 프라이빗 서브넷의 RDS, ElastiCache가 외부 통신 필요
  (패치 다운로드, Docker 이미지 Pull 등)
- 외부에서 프라이빗 서브넷으로 직접 접근은 차단 (보안)

고가용성 필요 시: "AZ마다 1개" (~$80/월)
```

**7. VPC 엔드포인트**:

```
✓ S3 Gateway (무료, 비용 절감)

왜 S3 Gateway를 사용하나?
- EC2에서 S3 접근 시 인터넷을 거치지 않고 AWS 내부 네트워크 사용
- 데이터 전송 비용 절감 및 속도 향상
```

**8. DNS 옵션**:

```
✓ DNS 호스트 이름 활성화
✓ DNS 확인 활성화
```

9. **VPC 생성** 클릭

### 3.2 생성 확인

VPC 대시보드 → 리소스 맵에서 다음 확인:

- ✓ VPC: `deepple-prod-vpc`
- ✓ 서브넷 4개 (퍼블릭 2, 프라이빗 2)
- ✓ 인터넷 게이트웨이
- ✓ NAT 게이트웨이
- ✓ 라우팅 테이블 2개

---

## 4. 데이터베이스 (RDS)

**RDS란?**
AWS가 관리하는 관계형 데이터베이스 서비스입니다. 백업, 패치, 모니터링을 자동으로 처리해줍니다.

### 4.1 MySQL vs Aurora 선택

**MySQL RDS (권장 - 초기 단계)**

- 비용: Aurora 대비 30-50% 저렴
- 표준 MySQL 완벽 호환
- Aurora로 쉽게 마이그레이션 가능 (스냅샷 기반, 10-30분)

**Aurora MySQL (대규모 트래픽 시)**

- MySQL 대비 최대 5배 빠른 성능
- Read Replica 자동 확장
- 비용 높음

💡 **권장**: 초기에는 MySQL RDS로 시작 → 트래픽 증가 시 Aurora 전환

### 4.2 서브넷 그룹 생성

1. **RDS 콘솔** → 서브넷 그룹 → **DB 서브넷 그룹 생성**
2. 설정:
   ```
   이름: deepple-prod-db-subnet-group
   설명: DEEPPLE 운영 DB용 서브넷 그룹
   VPC: deepple-prod-vpc
   ```
3. 서브넷 추가:
   ```
   가용 영역: ap-northeast-2a, ap-northeast-2c
   서브넷: 프라이빗 서브넷 2개 선택 (보안)
     - deepple-prod-subnet-private1-ap-northeast-2a
     - deepple-prod-subnet-private2-ap-northeast-2c
   ```
4. 생성

### 4.3 보안 그룹 생성

1. **VPC 콘솔** → 보안 그룹 → **보안 그룹 생성**
2. 설정:
   ```
   이름: deepple-prod-db-sg
   설명: DEEPPLE 운영 RDS 보안 그룹
   VPC: deepple-prod-vpc
   ```
3. 인바운드 규칙:
   ```
   유형: MySQL/Aurora
   포트: 3306
   소스: deepple-prod-app-sg (EC2 보안 그룹만)
   설명: Allow from application servers

   ⚠️ 중요: EC2 보안 그룹만 허용! 0.0.0.0/0 절대 금지!
   ```
4. 생성

### 4.4 RDS 인스턴스 생성

1. **RDS 콘솔** → 데이터베이스 → **데이터베이스 생성**

**2. 엔진 옵션**:

```
엔진 유형: MySQL
버전: MySQL 8.0.35 (최신 8.0.x)
```

**3. 템플릿**:

```
초기 운영: "개발/테스트"
  - Single-AZ로 시작 (비용 절감)
  - 나중에 클릭 한 번으로 Multi-AZ 전환 가능 (5-10분 다운타임)
  - 비용: 프로덕션의 약 50%

대규모 운영: "프로덕션"
  - Multi-AZ 기본 활성화 (고가용성)
  - 삭제 방지 자동 활성화
  - 비용 높음

💡 Multi-AZ란?
- 다른 AZ에 자동으로 동기화되는 복제본 생성
- 메인 DB 장애 시 자동으로 복제본으로 전환 (< 2분)
```

**4. 설정**:

```
DB 인스턴스 식별자: deepple-prod-db
마스터 사용자 이름: admin
마스터 암호: (강력한 암호 설정, 저장 필수!)
```

**5. 인스턴스 구성**:

```
초기 운영: db.t3.medium (2 vCPU, 4GB RAM)
대규모: db.t3.large (2 vCPU, 8GB RAM)
```

**6. 스토리지**:

```
스토리지 유형: 범용 SSD (gp3)
할당된 스토리지: 100 GB
스토리지 자동 조정 활성화: ✓ (최대 1000 GB)
```

**7. 연결**:

```
VPC: deepple-prod-vpc
DB 서브넷 그룹: deepple-prod-db-subnet-group
퍼블릭 액세스: 아니요 (보안 필수)
VPC 보안 그룹: deepple-prod-db-sg
가용 영역: 기본 설정 (ap-northeast-2a)
```

**8. 추가 구성**:

```
초기 데이터베이스 이름: deepple
백업 보존 기간: 7일
암호화 활성화: ✓ (기본 KMS 키)
Performance Insights 활성화: ✓ (7일 무료)
모니터링 세부 수준: 60초
삭제 방지 활성화: ✓ (운영 환경 필수)
```

9. **데이터베이스 생성** (5-10분 소요)

### 4.5 연결 정보 저장

생성 완료 후 엔드포인트 확인:

```
엔드포인트: deepple-prod-db.xxxxxxxx.ap-northeast-2.rds.amazonaws.com
포트: 3306

application-prod.yml 설정:
spring:
  datasource:
    url: jdbc:mysql://[엔드포인트]:3306/deepple
    username: admin
    password: [마스터 암호]
```

---

## 5. 캐시 서버 (ElastiCache)

**ElastiCache란?**
AWS가 관리하는 Redis 서비스입니다. 백업, 패치, 복제를 자동으로 처리합니다.

### 5.1 서브넷 그룹 생성

1. **ElastiCache 콘솔** → 서브넷 그룹 → **서브넷 그룹 생성**
2. 설정:
   ```
   이름: deepple-prod-redis-subnet-group
   설명: DEEPPLE 운영 Redis용 서브넷 그룹
   VPC: deepple-prod-vpc
   ```
3. 서브넷 추가:
   ```
   가용 영역: ap-northeast-2a, ap-northeast-2c
   서브넷: 프라이빗 서브넷 2개 선택
   ```
4. 생성

### 5.2 보안 그룹 생성

1. **VPC 콘솔** → 보안 그룹 → **보안 그룹 생성**
2. 설정:
   ```
   이름: deepple-prod-redis-sg
   설명: DEEPPLE 운영 Redis 보안 그룹
   VPC: deepple-prod-vpc
   ```
3. 인바운드 규칙:
   ```
   유형: 사용자 지정 TCP
   포트: 6379
   소스: deepple-prod-app-sg (EC2 보안 그룹만)
   설명: Allow from application servers
   ```
4. 생성

### 5.3 Redis 클러스터 생성

1. **ElastiCache 콘솔** → Redis 클러스터 → **클러스터 생성**

**2. 클러스터 모드**:

```
클러스터 모드: 비활성화 (단일 복제 그룹)
```

**3. 위치**:

```
AWS 클라우드
Multi-AZ: 활성화 (고가용성)
```

**4. 클러스터 정보**:

```
이름: deepple-prod-redis
엔진 버전: Redis 7.x (최신)
포트: 6379
노드 유형: cache.t3.micro (개발), cache.t3.small (운영)
복제본 수: 1 (Multi-AZ 최소)
```

**5. 연결**:

```
서브넷 그룹: deepple-prod-redis-subnet-group
보안 그룹: deepple-prod-redis-sg
```

**6. 고급 설정**:

```
암호화: 활성화
  - 전송 중 암호화: ✓
  - 저장 시 암호화: ✓
백업: 자동 백업 활성화 (1일 보존)
```

7. **생성** (5-10분 소요)

### 5.4 연결 정보 저장

```
엔드포인트: deepple-prod-redis.xxxxxx.ng.0001.apn2.cache.amazonaws.com
포트: 6379

application-prod.yml 설정:
spring:
  data:
    redis:
      host: [엔드포인트]
      port: 6379
      ssl:
        enabled: true
```

---

## 6. 스토리지 (S3)

**S3란?**
AWS의 객체 스토리지 서비스입니다. 파일(이미지, 동영상 등)을 저장하고 제공합니다.

### 6.1 S3 버킷 생성

1. **S3 콘솔** → 버킷 → **버킷 만들기**

**2. 일반 구성**:

```
버킷 이름: deepple-prod-storage (전 세계 고유해야 함)
  또는: deepple-prod-[회사명]-storage
리전: ap-northeast-2
```

**3. 객체 소유권**:

```
ACL 비활성화됨 (권장)
```

**4. 퍼블릭 액세스 차단**:

```
✓ 모든 퍼블릭 액세스 차단 (보안 필수)

왜 퍼블릭 액세스를 차단하나?
- IAM Role로 접근하므로 퍼블릭 접근 불필요
- 실수로 민감한 파일이 공개되는 것 방지
- 필요시 CloudFront나 Presigned URL로 파일 제공
```

**5. 버킷 버전 관리**:

```
활성화 (권장, 파일 실수 삭제 방지)
```

**6. 암호화**:

```
✓ 기본 암호화 활성화 (SSE-S3)
```

7. **버킷 만들기**

### 6.2 CORS 설정 (선택, 프론트엔드 직접 업로드 시)

버킷 선택 → 권한 → CORS 편집:

```json
[
  {
    "AllowedHeaders": [
      "*"
    ],
    "AllowedMethods": [
      "GET",
      "PUT",
      "POST",
      "DELETE"
    ],
    "AllowedOrigins": [
      "https://deepple.co.kr",
      "https://www.deepple.co.kr"
    ],
    "ExposeHeaders": [
      "ETag"
    ]
  }
]
```

### 6.3 수명 주기 정책 (선택, 비용 절감)

버킷 선택 → 관리 → 수명 주기 규칙 생성:

```
규칙 이름: temp-files-cleanup
적용 범위: temp/ 접두사
작업: 30일 후 객체 삭제
```

### 6.4 애플리케이션 설정

```yaml
# application-prod.yml
cloud:
  aws:
    s3:
      bucket: deepple-prod-storage
    region:
      static: ap-northeast-2
    credentials:
      instance-profile: true  # EC2 IAM Role 사용
```

---

## 7. 컴퓨팅 리소스 (EC2)

**EC2란?**
가상 서버입니다. DEEPPLE Spring Boot 애플리케이션이 Docker 컨테이너로 실행됩니다.

**보안 그룹이란?**
EC2 인스턴스에 대한 가상 방화벽입니다. 인바운드(들어오는)와 아웃바운드(나가는) 트래픽을 제어합니다.

**Stateful Firewall (중요 개념!)**

- 나간 요청의 응답은 인바운드 규칙 없이도 자동 허용
- 예: EC2 → Docker Hub 요청 → 응답 자동 허용
- 따라서 아웃바운드만 허용하면 Docker pull, RDS 연결, API 호출 모두 가능

### 7.1 보안 그룹 생성 (순서 중요!)

#### 7.1.1 ALB 보안 그룹 먼저 생성

1. **VPC 콘솔** → 보안 그룹 → **보안 그룹 생성**
2. 설정:
   ```
   이름: deepple-prod-alb-sg
   설명: DEEPPLE 운영 ALB 보안 그룹
   VPC: deepple-prod-vpc
   ```
3. 인바운드 규칙:
   ```
   규칙 1:
     유형: HTTP
     포트: 80
     소스: 0.0.0.0/0
     설명: HTTP from anywhere

   규칙 2:
     유형: HTTPS
     포트: 443
     소스: 0.0.0.0/0
     설명: HTTPS from anywhere
   ```
4. 생성

#### 7.1.2 EC2 애플리케이션 보안 그룹 생성

1. **보안 그룹 생성**
2. 설정:
   ```
   이름: deepple-prod-app-sg
   설명: DEEPPLE 운영 애플리케이션 보안 그룹
   VPC: deepple-prod-vpc
   ```
3. 인바운드 규칙:
   ```
   규칙 1 - SSH:
     유형: SSH
     포트: 22
     소스: 관리자 IP만 (예: 123.456.789.0/32)
     설명: Admin SSH access

     ⚠️ 보안 경고: 0.0.0.0/0 절대 금지!

   규칙 2 - 애플리케이션:
     유형: 사용자 지정 TCP
     포트: 8080
     소스: deepple-prod-alb-sg (보안 그룹)
     설명: Spring Boot from ALB

   트래픽 흐름:
   인터넷 → ALB (443) → EC2 (8080)
   - ALB에서 HTTPS 처리 (SSL 종료)
   - EC2는 ALB에서만 8080 포트로 요청 받음
   ```
4. 생성

### 7.2 EC2 인스턴스 생성

1. **EC2 콘솔** → 인스턴스 → **인스턴스 시작**

**2. 이름 및 태그**:

```
이름: deepple-prod-app
Environment: production
Project: deepple
```

**3. AMI 선택**:

```
Amazon Linux 2023 AMI (권장)
  - AWS 최적화
  - 보안 패치 자동
  - yum 패키지 관리자
```

**4. 인스턴스 유형**:

```
초기 운영: t3.small (2GB RAM, ~$15/월)
대규모: t3.medium (4GB RAM, ~$30/월)

⚠️ t3.micro (1GB)는 Spring Boot 실행에 메모리 부족
```

**5. 키 페어**:

```
기존 키 페어 선택 또는 새로 생성
⚠️ 중요: .pem 파일 안전하게 보관!
```

**6. 네트워크 설정**:

```
VPC: deepple-prod-vpc
서브넷: deepple-prod-subnet-public1-ap-northeast-2a (퍼블릭)
퍼블릭 IP 자동 할당: 활성화
방화벽(보안 그룹): 기존 보안 그룹 선택
  → deepple-prod-app-sg
```

**7. 스토리지**:

```
볼륨 1 (루트):
  크기: 30 GB
  볼륨 유형: gp3
  종료 시 삭제: ✓
```

**8. 고급 세부 정보**:

```
IAM 인스턴스 프로파일: deepple-prod-app-role

사용자 데이터 (선택, 자동 Docker 설치):
#!/bin/bash
# Docker 설치
yum update -y
yum install -y docker
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# Docker Compose 설치
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

9. **인스턴스 시작**

### 7.3 Elastic IP 할당 (권장)

**Elastic IP란?**
고정 IP 주소입니다. EC2를 재시작해도 IP가 변경되지 않습니다.

**왜 필요한가?**

- EC2 재시작 시 퍼블릭 IP가 변경됨
- Route 53 도메인 연결 시 IP 변경마다 레코드 업데이트 필요
- Elastic IP 사용 시 한 번 설정으로 끝

**할당 단계**:

1. **EC2 콘솔** → Elastic IP → **Elastic IP 주소 할당**
2. 할당 → 인스턴스에 연결
3. 인스턴스 선택: `deepple-prod-app`
4. 연결

### 7.4 SSH 접속 테스트

```bash
# 키 권한 설정 (최초 1회)
chmod 400 deepple-prod.pem

# SSH 접속
ssh -i deepple-prod.pem ec2-user@[Elastic-IP]

# Docker 확인
docker --version
docker-compose --version
```

---

## 8. 로드 밸런서 (ALB)

**ALB란?**
Application Load Balancer는 트래픽을 여러 EC2 인스턴스에 분산합니다.

**왜 ALB를 사용하나?**

- SSL/TLS 인증서 관리 (EC2에서 설정 불필요)
- 헬스 체크로 비정상 인스턴스 자동 제외
- 트래픽 증가 시 EC2 추가로 쉽게 확장
- HTTP → HTTPS 리다이렉트

### 8.1 타겟 그룹 생성

**타겟 그룹이란?**
ALB가 트래픽을 전달할 EC2 인스턴스의 그룹입니다.

1. **EC2 콘솔** → 로드 밸런싱 → 대상 그룹 → **대상 그룹 생성**

**2. 기본 구성**:

```
대상 유형: 인스턴스
대상 그룹 이름: deepple-prod-tg
프로토콜: HTTP
포트: 8080 (Spring Boot 포트)
VPC: deepple-prod-vpc
프로토콜 버전: HTTP1
```

**3. 상태 검사**:

```
상태 검사 프로토콜: HTTP
상태 검사 경로: /actuator/health
고급 상태 검사:
  포트: 트래픽 포트
  정상 임계 값: 2
  비정상 임계 값: 2
  제한 시간: 5초
  간격: 30초
  성공 코드: 200
```

4. **다음** → 대상 등록 → `deepple-prod-app` 선택 → **아래에 보류 중인 것으로 포함**
5. **대상 그룹 생성**

### 8.2 Application Load Balancer 생성

1. **EC2 콘솔** → 로드 밸런서 → **Load Balancer 생성**
2. **Application Load Balancer** 선택

**3. 기본 구성**:

```
이름: deepple-prod-alb
체계: 인터넷 경계
IP 주소 유형: IPv4
```

**4. 네트워크 매핑**:

```
VPC: deepple-prod-vpc
가용 영역 및 서브넷 (2개 이상 필수):
  ✓ ap-northeast-2a → deepple-prod-subnet-public1-ap-northeast-2a
  ✓ ap-northeast-2c → deepple-prod-subnet-public2-ap-northeast-2c
```

**5. 보안 그룹**:

```
deepple-prod-alb-sg
```

**6. 리스너 및 라우팅**:

```
리스너 1:
  프로토콜: HTTP
  포트: 80
  기본 작업: deepple-prod-tg로 전달

리스너 2 (SSL 인증서 발급 후 추가):
  프로토콜: HTTPS
  포트: 443
  기본 작업: deepple-prod-tg로 전달
  보안 정책: ELBSecurityPolicy-2016-08
  SSL/TLS 인증서: ACM에서 선택
```

7. **로드 밸런서 생성** (2-3분 소요)

### 8.3 HTTP → HTTPS 리다이렉트 (SSL 설정 후)

1. ALB 선택 → 리스너 탭
2. HTTP:80 리스너 선택 → 규칙 보기
3. 기본 규칙 편집:
   ```
   작업: 리디렉션 대상
     프로토콜: HTTPS
     포트: 443
     상태 코드: 301 (영구 리디렉션)
   ```

### 8.4 연결 확인

```bash
# ALB DNS 이름 확인
# EC2 콘솔 → 로드 밸런서 → deepple-prod-alb → DNS 이름 복사

# 헬스 체크 확인
curl http://[ALB-DNS-이름]/actuator/health

# 예상 결과:
{"status":"UP"}
```

---

## 9. 도메인 및 SSL

### 9.1 도메인 구입 (Route 53 또는 외부)

**Route 53에서 구입**:

1. Route 53 콘솔 → 등록된 도메인 → **도메인 등록**
2. 도메인 검색 (예: deepple.co.kr)
3. 구매 및 등록

**외부에서 구입한 경우**:

1. 도메인 등록 대행사에서 네임서버를 Route 53으로 변경

### 9.2 호스팅 영역 생성

1. Route 53 콘솔 → 호스팅 영역 → **호스팅 영역 생성**
2. 도메인 이름: `deepple.co.kr`
3. 유형: 퍼블릭 호스팅 영역
4. 생성

### 9.3 SSL 인증서 발급 (ACM)

**ACM이란?**
AWS Certificate Manager는 무료로 SSL/TLS 인증서를 발급하고 자동 갱신합니다.

1. **Certificate Manager 콘솔** (⚠️ 리전: ap-northeast-2, ALB와 동일해야 함!)
2. **인증서 요청**

**3. 인증서 구성**:

```
인증서 유형: 퍼블릭 인증서
도메인 이름:
  - api.deepple.co.kr (운영)
  - dev-api.deepple.co.kr (개발)
  - *.deepple.co.kr (와일드카드, 선택 - 모든 서브도메인 커버)
검증 방법: DNS 검증 (권장, 자동화 가능)
```

4. **요청**
5. 인증서 선택 → **Route 53에서 레코드 생성** 클릭 (자동 DNS 검증)
6. 5-10분 후 상태가 **"발급됨"**으로 변경

### 9.4 Route 53 A 레코드 생성

1. Route 53 → 호스팅 영역 → `deepple.co.kr` 선택
2. **레코드 생성**

**운영 환경 (api.deepple.co.kr)**:

```
레코드 이름: api
레코드 유형: A
별칭: 예 (ALB 연결 시 별칭 사용)
트래픽 라우팅 대상: Application/Classic Load Balancer에 대한 별칭
리전: ap-northeast-2
로드 밸런서: deepple-prod-alb
라우팅 정책: 단순 라우팅
```

**개발 환경 (dev-api.deepple.co.kr)**:

```
레코드 이름: dev-api
레코드 유형: A
별칭: 아니요 (EC2 직접 연결)
값: [개발 EC2 Elastic IP]
TTL: 300
```

3. **레코드 생성**

### 9.5 ALB에 HTTPS 리스너 추가

1. EC2 콘솔 → 로드 밸런서 → `deepple-prod-alb` 선택
2. 리스너 탭 → **리스너 추가**
3. 설정:
   ```
   프로토콜: HTTPS
   포트: 443
   기본 작업: deepple-prod-tg로 전달
   보안 정책: ELBSecurityPolicy-2016-08
   기본 SSL/TLS 인증서: ACM에서 api.deepple.co.kr 선택
   ```
4. 추가
5. HTTP:80 리스너 → HTTPS:443 리디렉션 설정 (8.3 참고)

### 9.6 DNS 전파 확인 및 테스트

```bash
# DNS 확인 (5-10분 소요)
nslookup api.deepple.co.kr
# 예상: ALB IP 주소 반환

# HTTPS 접속 테스트
curl https://api.deepple.co.kr/actuator/health
# 예상: {"status":"UP"}

# HTTP → HTTPS 리다이렉트 확인
curl -I http://api.deepple.co.kr
# 예상: Location: https://api.deepple.co.kr
```

---
