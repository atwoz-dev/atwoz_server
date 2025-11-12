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

| 환경        | 브랜치       | 인프라            | 배포 방식             | 목적     |
|-----------|-----------|----------------|-------------------|--------|
| **local** | feature/* | Docker Compose | 수동                | 로컬 개발  |
| **dev**   | develop   | AWS            | 자동 (develop 머지 시) | QA     |
| **prod**  | main      | AWS            | 자동 (main 머지 시)    | 실제 서비스 |

---

## 2. AWS 계정 및 기본 설정

### 2.1 리전 선택

**중요**: 모든 작업 전에 반드시 리전을 확인하세요!

1. AWS 콘솔 접속
2. 우측 상단의 리전 드롭다운 클릭
3. **아시아 태평양(서울) ap-northeast-2** 선택
4. 모든 작업은 서울 리전에서 진행

### 2.2 IAM Role 생성

**개념**: IAM Role은 EC2 인스턴스가 S3, CloudWatch 등 AWS 서비스에 안전하게 접근할 수 있도록 권한을 부여합니다. 코드에 액세스 키를 넣지 않아도 됩니다.

#### 2.2.1 운영 환경용 Role 생성

1. **IAM 서비스로 이동**
    - AWS 콘솔 검색창에 "IAM" 입력
    - "IAM" 클릭

2. **역할 만들기 시작**
    - 좌측 메뉴에서 "역할" 클릭
    - "역할 만들기" 버튼 클릭

3. **신뢰할 수 있는 엔터티 유형 선택**
    - "AWS 서비스" 선택 (기본값)
    - 사용 사례: "EC2" 선택
    - "다음" 클릭

4. **권한 정책 연결**

   다음 정책들을 검색해서 체크:

   ```
   ✓ AmazonS3FullAccess
     → S3 버킷에 파일 업로드/다운로드

   ✓ CloudWatchLogsFullAccess
     → 애플리케이션 로그를 CloudWatch로 전송

   ✓ CloudWatchAgentServerPolicy (선택사항, 권장)
     → 서버 메트릭(CPU, 메모리 등)을 CloudWatch로 전송

   ✓ AmazonSSMManagedInstanceCore (선택사항, 권장)
     → SSH 없이 AWS Systems Manager로 서버 접속 가능
   ```

    - "다음" 클릭

5. **역할 이름 및 설명 입력**
    - 역할 이름: `deepple-prod-app-role`
    - 설명: `DEEPPLE 운영 서버용 EC2 역할 - S3, CloudWatch 접근`
    - "역할 만들기" 클릭

6. **생성 완료 확인**
    - 역할 목록에 `deepple-prod-app-role`이 표시되면 성공

#### 2.2.2 개발 환경용 Role 생성 (선택사항)

동일한 과정으로:

- 역할 이름: `deepple-dev-app-role`
- 동일한 권한 추가

**💡 IAM Role을 사용하는 이유**

- ✅ 코드나 .env 파일에 액세스 키를 넣지 않아도 됨
- ✅ EC2에서 자동으로 AWS 서비스 접근 (보안 강화)
- ✅ 권한이 자동으로 회전되어 관리 불필요

---

## 3. 네트워크 구성 (VPC)

### 3.1 VPC란?

**개념**: Virtual Private Cloud는 AWS 클라우드에서 논리적으로 격리된 네트워크 공간입니다. 여러분만의 데이터 센터를 클라우드에 만드는 것과 같습니다.

**VPC에 포함되는 리소스**:

- VPC (가상 네트워크)
- 서브넷 (VPC 내부의 IP 대역 분할)
- 인터넷 게이트웨이 (인터넷 연결)
- NAT 게이트웨이 (프라이빗 서브넷의 아웃바운드 인터넷 연결, 선택)
- 라우팅 테이블 (네트워크 트래픽 경로 설정)

### 3.3 VPC 마법사로 한 번에 구성하기 (권장)

AWS VPC 마법사를 사용하면 VPC, 서브넷, 인터넷 게이트웨이, 라우팅 테이블을 한 번에 생성할 수 있습니다.

#### 3.3.1 운영 환경 VPC 생성

**1단계: VPC 서비스로 이동**

1. AWS 콘솔 접속
2. 리전이 서울인지 확인
3. 검색창에 "VPC" 입력 → "VPC" 클릭
4. 좌측 메뉴에서 "VPC" 클릭
5. 우측 상단 **"VPC 생성"** 버튼 클릭

**2단계: 생성할 리소스 선택**

- **생성할 리소스**: "VPC 등" 선택

**3단계: VPC 설정**

```
이름 태그 자동 생성:
  ✓ 체크 (자동으로 리소스 이름 생성)

이름 태그:
  deepple-prod

IPv4 CIDR 블록:
  10.0.0.0/16
  (총 65,536개의 IP 주소, 운영 환경에 충분)

IPv6 CIDR 블록:
  "IPv6 CIDR 블록 없음" 선택
  (IPv6가 필요없으면 선택 해제)

테넌시:
  "기본값" 선택
  (전용 하드웨어는 매우 비쌈)
```

**4단계: 가용 영역(AZ) 설정**

```
가용 영역(AZ) 수:
  2
  (고가용성을 위해 2개 이상 필수)

  💡 설명: 2개의 가용 영역에 리소스를 분산하면,
     한 AZ에 장애가 발생해도 다른 AZ에서 서비스 계속 가능
```

**5단계: 서브넷 설정**

```
퍼블릭 서브넷 수:
  2
  (각 AZ에 1개씩, ALB와 EC2가 위치)

프라이빗 서브넷 수:
  2
  (각 AZ에 1개씩, RDS와 ElastiCache가 위치)

  💡 퍼블릭 vs 프라이빗:
     - 퍼블릭: 인터넷에서 직접 접근 가능 (EC2, ALB)
     - 프라이빗: 인터넷에서 직접 접근 불가 (RDS, ElastiCache)
```

**서브넷 CIDR 블록 자동 설정 확인**:

VPC 마법사가 자동으로 다음과 같이 설정합니다:

```
퍼블릭 서브넷:
  - ap-northeast-2a: 10.0.0.0/20 (4,096개 IP)
  - ap-northeast-2c: 10.0.16.0/20 (4,096개 IP)

프라이빗 서브넷:
  - ap-northeast-2a: 10.0.128.0/20 (4,096개 IP)
  - ap-northeast-2c: 10.0.144.0/20 (4,096개 IP)
```

**6단계: NAT 게이트웨이 설정 (선택사항, 고가용성)**

```
NAT 게이트웨이($):
  선택 옵션:

  ❌ "없음" (비용 절감, 비권장)
     - 프라이빗 서브넷이 인터넷에 접근 불가
     - RDS, ElastiCache는 업데이트 다운로드 불가
     - 비용: $0/월

  ✅ "1개 AZ에" (권장, 비용 절약)
     - 1개의 NAT 게이트웨이로 모든 프라이빗 서브넷 지원
     - NAT 게이트웨이가 있는 AZ 장애 시 인터넷 연결 끊김
     - 비용: 약 $40/월
     - 운영 초기에 권장

  ⭐ "AZ마다 1개" (고가용성, 비용 높음)
     - 각 AZ마다 NAT 게이트웨이 배치
     - 한 AZ 장애 시에도 다른 AZ는 정상 작동
     - 비용: 약 $80/월
     - 트래픽이 많고 안정성이 중요할 때 권장

  💡 권장: 운영 환경은 "1개 AZ에" 선택
     프라이빗 서브넷의 외부 통신이 필요하므로 NAT 게이트웨이 필요
     (예: RDS 패치 다운로드, Docker 이미지 Pull)
```

**7단계: VPC 엔드포인트 설정 (선택사항, 비용 절감)**

```
VPC 엔드포인트:
  "S3 Gateway" 체크

  💡 설명: S3 Gateway 엔드포인트는 무료이며,
     EC2에서 S3로 접근할 때 인터넷을 거치지 않고
     AWS 내부 네트워크를 사용하여 비용 절감

  기타 엔드포인트는 비용이 발생하므로 선택 해제
```

**8단계: DNS 옵션**

```
DNS 옵션:
  ✓ DNS 호스트 이름 활성화
  ✓ DNS 확인 활성화

  (기본값 유지, RDS 등의 엔드포인트 이름 사용에 필요)
```

**9단계: VPC 생성**

1. 우측 하단 **"VPC 생성"** 버튼 클릭
2. 생성 진행 상황 확인 (약 2-3분 소요)
3. "VPC가 성공적으로 생성되었습니다" 메시지 확인

**10단계: 생성된 리소스 확인**

생성 완료 후 다음 리소스들이 자동으로 생성됩니다:

```
✓ VPC: deepple-prod-vpc (10.0.0.0/16)

✓ 서브넷 4개:
  - deepple-prod-subnet-public1-ap-northeast-2a
  - deepple-prod-subnet-public2-ap-northeast-2c
  - deepple-prod-subnet-private1-ap-northeast-2a
  - deepple-prod-subnet-private2-ap-northeast-2c

✓ 인터넷 게이트웨이: deepple-prod-igw
  (VPC에 자동 연결됨)

✓ NAT 게이트웨이: deepple-prod-nat-public1-ap-northeast-2a
  (선택한 경우)

✓ 라우팅 테이블 2개:
  - deepple-prod-rtb-public (퍼블릭 서브넷용)
  - deepple-prod-rtb-private (프라이빗 서브넷용)

✓ S3 엔드포인트 (선택한 경우)
```

#### 3.3.2 개발 환경(Development) VPC 생성 (선택사항)

개발 환경은 비용 절감을 위해 간소화된 구성으로 생성합니다.

**동일한 과정으로 VPC 마법사 실행, 차이점만 명시**:

```
이름 태그: deepple-dev

IPv4 CIDR 블록: 10.1.0.0/16

가용 영역(AZ) 수: 1
  (고가용성 불필요, 비용 절감)

퍼블릭 서브넷 수: 1
프라이빗 서브넷 수: 1

NAT 게이트웨이: 없음
  (프라이빗 서브넷의 외부 통신이 필요없으면 비용 절감)
  (필요시 "1개 AZ에" 선택)

VPC 엔드포인트: S3 Gateway만 체크
```

### 3.4 수동으로 VPC 구성하기 (선택사항)

VPC 마법사 대신 직접 하나씩 생성하고 싶다면 다음 순서로 진행합니다:

<details>
<summary>수동 구성 단계 보기 (클릭하여 펼치기)</summary>

#### 1. VPC 생성

```
1. VPC 대시보드 → "VPC" → "VPC 생성"
2. 생성할 리소스: "VPC만"
3. 이름 태그: deepple-prod-vpc
4. IPv4 CIDR: 10.0.0.0/16
5. "VPC 생성" 클릭
```

#### 2. 서브넷 생성 (4개)

```
1. VPC 대시보드 → "서브넷" → "서브넷 생성"
2. VPC: deepple-prod-vpc 선택

퍼블릭 서브넷 1:
  - 이름: deepple-prod-public-1a
  - 가용 영역: ap-northeast-2a
  - IPv4 CIDR: 10.0.0.0/20

퍼블릭 서브넷 2:
  - 이름: deepple-prod-public-1c
  - 가용 영역: ap-northeast-2c
  - IPv4 CIDR: 10.0.16.0/20

프라이빗 서브넷 1:
  - 이름: deepple-prod-private-1a
  - 가용 영역: ap-northeast-2a
  - IPv4 CIDR: 10.0.128.0/20

프라이빗 서브넷 2:
  - 이름: deepple-prod-private-1c
  - 가용 영역: ap-northeast-2c
  - IPv4 CIDR: 10.0.144.0/20

3. "서브넷 생성" 클릭
```

#### 3. 인터넷 게이트웨이 생성 및 연결

```
1. VPC 대시보드 → "인터넷 게이트웨이" → "인터넷 게이트웨이 생성"
2. 이름 태그: deepple-prod-igw
3. "인터넷 게이트웨이 생성" 클릭
4. 생성된 IGW 선택 → "작업" → "VPC에 연결"
5. VPC: deepple-prod-vpc 선택 → "인터넷 게이트웨이 연결"
```

#### 4. NAT 게이트웨이 생성 (선택사항)

```
1. VPC 대시보드 → "NAT 게이트웨이" → "NAT 게이트웨이 생성"
2. 이름: deepple-prod-nat-1a
3. 서브넷: deepple-prod-public-1a (퍼블릭 서브넷 선택!)
4. 연결 유형: 퍼블릭
5. Elastic IP 할당: "Elastic IP 할당" 클릭
6. "NAT 게이트웨이 생성" 클릭
7. 생성 완료까지 약 5분 대기
```

#### 5. 라우팅 테이블 생성 및 설정

**퍼블릭 라우팅 테이블**:

```
1. VPC 대시보드 → "라우팅 테이블" → "라우팅 테이블 생성"
2. 이름: deepple-prod-public-rt
3. VPC: deepple-prod-vpc
4. "라우팅 테이블 생성" 클릭
5. 생성된 라우팅 테이블 선택 → "라우팅" 탭 → "라우팅 편집"
6. "라우팅 추가":
   - 대상: 0.0.0.0/0
   - 대상: 인터넷 게이트웨이 → deepple-prod-igw 선택
7. "변경 사항 저장"
8. "서브넷 연결" 탭 → "서브넷 연결 편집"
9. 퍼블릭 서브넷 2개 선택 (deepple-prod-public-1a, 1c)
10. "연결 저장"
```

**프라이빗 라우팅 테이블**:

```
1. "라우팅 테이블 생성"
2. 이름: deepple-prod-private-rt
3. VPC: deepple-prod-vpc
4. "라우팅 테이블 생성" 클릭
5. (NAT 게이트웨이 사용 시) "라우팅 편집"
   - 대상: 0.0.0.0/0
   - 대상: NAT 게이트웨이 → deepple-prod-nat-1a 선택
6. "서브넷 연결 편집"
7. 프라이빗 서브넷 2개 선택 (deepple-prod-private-1a, 1c)
8. "연결 저장"
```

</details>

### 3.5 VPC 생성 확인

VPC가 올바르게 생성되었는지 확인합니다.

1. **VPC 대시보드로 이동**
2. **"리소스 맵" 클릭**
3. **deepple-prod-vpc 선택**
4. 다음 항목들이 표시되는지 확인:
    - ✓ VPC
    - ✓ 서브넷 4개 (퍼블릭 2, 프라이빗 2)
    - ✓ 인터넷 게이트웨이
    - ✓ NAT 게이트웨이 (선택한 경우)
    - ✓ 라우팅 테이블 2개

### 3.6 서브넷 이름 기억하기

이후 RDS, ElastiCache, EC2 등을 생성할 때 서브넷을 선택해야 하므로 이름을 기억해두세요:

```
퍼블릭 서브넷 (EC2, ALB용):
  - deepple-prod-subnet-public1-ap-northeast-2a
  - deepple-prod-subnet-public2-ap-northeast-2c

프라이빗 서브넷 (RDS, ElastiCache용):
  - deepple-prod-subnet-private1-ap-northeast-2a
  - deepple-prod-subnet-private2-ap-northeast-2c
```

**💡 Tip**: 서브넷 ID도 함께 메모해두면 나중에 CLI나 Terraform 사용 시 편리합니다.

---

## 4. 데이터베이스 구성 (RDS)

### 4.1 RDS란?

**개념**: Relational Database Service는 AWS가 관리하는 관계형 데이터베이스 서비스입니다. 백업, 패치, 모니터링을 자동으로 처리해줍니다.

**RDS 사용 시 장점**:

- 자동 백업 및 복원
- 자동 소프트웨어 패치
- Multi-AZ 고가용성 지원
- 읽기 전용 복제본 (Read Replica) 지원
- 모니터링 및 알람

### 4.2 MySQL vs Aurora 선택 가이드

**개념**: RDS에서는 일반 MySQL과 Aurora MySQL 중 선택할 수 있습니다.

#### MySQL RDS (권장 - 초기 단계)

**장점**:

- 비용이 Aurora 대비 30-50% 저렴
- 표준 MySQL과 완벽히 호환
- 간단한 구성, 예측 가능한 성능

**단점**:

- Aurora보다 낮은 성능
- Read Replica 확장이 수동

**적합한 경우**:

- ✅ 초기/중소 규모 서비스
- ✅ 트래픽이 아직 크지 않을 때
- ✅ 예산이 제한적일 때

#### Aurora MySQL

**장점**:

- MySQL 대비 최대 5배 빠른 성능
- Read Replica 자동 확장
- 더 빠른 장애 조치 (< 30초)
- 스토리지 자동 확장 (10GB ~ 128TB)

**단점**:

- 비용이 MySQL 대비 높음
- 초기 설정이 복잡

**적합한 경우**:

- ✅ 대규모 트래픽 예상
- ✅ MSA 전환 계획
- ✅ 높은 가용성이 필수적인 경우

#### 마이그레이션 가능성

MySQL RDS ➔ Aurora는 **스냅샷 기반으로 쉽게 전환 가능**합니다:

- 다운타임: 10-30분 정도
- 데이터 손실: 없음
- 코드 변경: 불필요 (엔드포인트만 변경)

💡 **권장**: 초기에는 MySQL RDS로 시작하고, 트래픽 증가 시 Aurora로 전환

### 4.3 환경별 RDS 설정

| 항목                   | Development | Production                 |
|----------------------|-------------|----------------------------|
| DB 인스턴스 클래스          | db.t3.micro | db.t3.medium ~ db.t3.large |
| Multi-AZ             | 아니요 (비용 절감) | 예 (필수, 고가용성)               |
| 스토리지                 | 20GB        | 100GB (자동 확장)              |
| 백업 보존 기간             | 3일          | 7-30일                      |
| Performance Insights | 비활성화        | 활성화 (7일 무료)                |

### 4.3 운영 환경(Production) RDS 생성

#### 4.3.1 서브넷 그룹 생성 (먼저 해야 함)

**1단계: RDS 서비스로 이동**

1. AWS 콘솔 검색창에 "RDS" 입력
2. "RDS" 클릭
3. 리전이 **"서울 (ap-northeast-2)"**인지 확인

**2단계: 서브넷 그룹 생성**

1. 좌측 메뉴에서 **"서브넷 그룹"** 클릭
2. **"DB 서브넷 그룹 생성"** 버튼 클릭
3. 서브넷 그룹 세부 정보:
   ```
   이름: deepple-prod-db-subnet-group
   설명: DEEPPLE 운영 DB용 서브넷 그룹
   VPC: deepple-prod-vpc 선택
   ```
4. **서브넷 추가**:
   ```
   가용 영역 선택:
   ✓ ap-northeast-2a
   ✓ ap-northeast-2c

   서브넷 선택:
   ✓ deepple-prod-subnet-private1-ap-northeast-2a (10.0.128.0/20)
   ✓ deepple-prod-subnet-private2-ap-northeast-2c (10.0.144.0/20)

   💡 중요: 반드시 프라이빗 서브넷 선택!
   ```
5. **"생성"** 버튼 클릭

#### 4.3.2 보안 그룹 생성

**1단계: EC2 콘솔에서 보안 그룹 생성**

1. AWS 콘솔 검색창에 "VPC" 입력
2. 좌측 메뉴에서 **"보안 그룹"** 클릭
3. **"보안 그룹 생성"** 버튼 클릭

**2단계: 보안 그룹 설정**

```
기본 세부 정보:
  보안 그룹 이름: deepple-prod-db-sg
  설명: DEEPPLE 운영 RDS 보안 그룹
  VPC: deepple-prod-vpc

인바운드 규칙:
  규칙 1:
    유형: MySQL/Aurora
    프로토콜: TCP
    포트 범위: 3306
    소스: 사용자 지정
    → deepple-prod-app-sg 검색 후 선택 (EC2 보안 그룹)
    설명: Allow from application servers

  💡 중요: EC2 보안 그룹만 허용! 0.0.0.0/0 절대 금지!

아웃바운드 규칙:
  기본값 유지 (모든 트래픽 허용)
```

4. **"보안 그룹 생성"** 클릭

#### 4.3.3 RDS MySQL 인스턴스 생성

**1단계: 데이터베이스 생성 시작**

1. RDS 콘솔로 돌아가기
2. 좌측 메뉴에서 **"데이터베이스"** 클릭
3. **"데이터베이스 생성"** 버튼 클릭

**2단계: 엔진 옵션**

```
엔진 유형:
  ○ MySQL 선택

에디션:
  MySQL Community (기본값)

엔진 버전:
  MySQL 8.0.35 (또는 최신 8.0.x 버전)

  💡 주의: 8.0.x 중 가장 최신 버전 선택 권장
  DEEPPLE 프로젝트는 MySQL 8.0 기준으로 개발됨
```

**3단계: 템플릿**

```
템플릿 선택 가이드:

○ 프로덕션 (대규모 트래픽, 높은 가용성 필요 시)
  - Multi-AZ 기본 활성화
  - 고가용성 옵션 자동 설정
  - 삭제 방지 기본 활성화
  - 비용: 높음
  - 나중에 변경: 가능

○ 개발/테스트 (권장 - 초기 운영)
  - Single-AZ 기본 설정
  - 비용 절감
  - 필요시 Multi-AZ로 쉽게 전환 가능 (클릭 한 번, 5-10분 다운타임)
  - 비용: 프로덕션의 약 50%
  - 나중에 변경: 가능

○ 프리 티어 (학습용만)
  - 제한 많음 (t2.micro, 20GB, 750시간/월)
  - Multi-AZ 미지원
  - 운영 환경 부적합

💡 권장 선택:
  초기 운영 또는 트래픽 적을 때: "개발/테스트" 선택
  → 비용 절약하면서 필요시 업그레이드 가능
```

**4단계: 설정**

```
DB 인스턴스 식별자:
  deepple-prod-db

  💡 이 이름은 엔드포인트 URL에 포함됩니다
  예: deepple-prod-db.xxxxx.ap-northeast-2.rds.amazonaws.com

자격 증명 설정:
  마스터 사용자 이름: admin
  (또는 원하는 이름, 기억하기 쉬운 것)

  자격 증명 관리:
    ○ 자체 관리 선택

  마스터 암호:
    [강력한 비밀번호 입력]

    💡 비밀번호 생성 예시:
    - 16자 이상
    - 대문자, 소문자, 숫자, 특수문자 포함
    - 생성 후 안전한 곳에 저장 (AWS Secrets Manager 권장)

  마스터 암호 확인:
    [동일한 비밀번호 재입력]
```

**5단계: 인스턴스 구성**

```
DB 인스턴스 클래스:
  인스턴스 클래스 유형:
    ○ 버스터블 클래스 (t 클래스) 선택

  인스턴스 클래스:
    db.t3.medium (2 vCPU, 4GB RAM)

    💡 권장 인스턴스 선택:

    ✅ db.t3.medium (시작용, 권장)
       - 비용: ~$60/월
       - 동시 접속자 100-500명 처리 가능

    ⭐ db.t3.large (트래픽 증가 시)
       - 비용: ~$120/월
       - 동시 접속자 500-1000명 처리 가능

    🚀 db.m5.large (고성능 필요 시)
       - 비용: ~$150/월
       - 안정적인 성능, CPU 크레딧 걱정 없음
```

**6단계: 스토리지**

```
스토리지 유형:
  ○ 범용 SSD (gp3) 선택 (최신, 권장)

  💡 gp2 vs gp3:
  - gp3: 최신, 동일 가격에 20% 더 빠름
  - gp2: 이전 세대

할당된 스토리지:
  100 GiB (초기 권장)

  💡 데이터 예상량:
  - 사용자 10만명 기준: ~20-30GB
  - 여유분 고려하여 100GB 권장

스토리지 자동 조정:
  ✓ 스토리지 자동 조정 활성화

  최대 스토리지 임계값:
    500 GiB

  💡 자동 확장 트리거:
  - 여유 공간 < 10% 일 때 자동 확장
  - 다운타임 없이 확장됨
```

**7단계: 가용성 및 내구성 (중요!)**

```
Multi-AZ 배포:
  ✓ Multi-AZ DB 인스턴스 생성 (필수!)

  💡 Multi-AZ란?
  - 다른 가용 영역(AZ)에 대기 DB 자동 복제
  - 주 DB 장애 시 1-2분 내 자동 페일오버
  - 다운타임 최소화 (계획된 유지보수 시에도)
  - 비용: 약 2배 (운영 환경 필수)

  ❌ Single-AZ (개발 환경만 사용)
  - 비용 절감, 고가용성 없음
  - 장애 시 서비스 중단
```

**8단계: 연결**

```
컴퓨팅 리소스:
  ○ EC2 컴퓨팅 리소스에 연결 안 함 선택
  (수동으로 VPC와 보안 그룹 설정)

네트워크 유형:
  IPv4

Virtual Private Cloud(VPC):
  deepple-prod-vpc 선택

DB 서브넷 그룹:
  deepple-prod-db-subnet-group 선택
  (위에서 생성한 서브넷 그룹)

퍼블릭 액세스:
  ○ 아니요 선택 (필수!)

  💡 매우 중요:
  - "예" 선택 시 인터넷에서 DB 직접 접근 가능 (보안 위험!)
  - 반드시 "아니요" 선택
  - EC2에서만 프라이빗 네트워크로 접근

VPC 보안 그룹:
  ○ 기존 항목 선택
  → deepple-prod-db-sg 선택
  ("default" 보안 그룹 제거)

가용 영역:
  기본 설정 없음 (자동 선택)

  💡 Multi-AZ 사용 시 AWS가 자동으로 최적의 AZ 선택

추가 구성:
  데이터베이스 포트: 3306 (기본값)
```

**9단계: 데이터베이스 인증**

```
데이터베이스 인증 옵션:
  ✓ 암호 인증 (기본값, 권장)

  선택사항:
  ☐ IAM 데이터베이스 인증
  ☐ Kerberos 인증

  💡 암호 인증이 가장 일반적이고 Spring Boot와 호환성 좋음
```

**10단계: 모니터링 (선택사항, 권장)**

```
Enhanced Monitoring 활성화:
  ✓ Enhanced Monitoring 활성화

  세분화: 60초

  모니터링 역할:
    ○ 기본값 (자동 생성)

  💡 Enhanced Monitoring:
  - OS 레벨 메트릭 수집 (CPU, 메모리, 디스크 I/O)
  - 비용: 거의 무료 ($1~2/월)
  - 성능 문제 진단에 매우 유용

Performance Insights 활성화:
  ✓ Performance Insights 켜기

  보존 기간: 7일 (무료)

  💡 Performance Insights:
  - 쿼리 성능 분석 도구
  - 느린 쿼리 자동 감지
  - 7일 무료, 장기 보존은 유료
```

**11단계: 추가 구성**

```
데이터베이스 옵션:
  초기 데이터베이스 이름: (비워둠)

  💡 비워두는 이유:
  - RDS 생성 후 수동으로 생성 권장
  - 문자셋(utf8mb4) 등 세부 설정 필요

  DB 파라미터 그룹: default.mysql8.0 (기본값)
  옵션 그룹: default:mysql-8-0 (기본값)

백업:
  ✓ 자동 백업 활성화 (필수!)

  백업 보존 기간: 7일

  💡 백업 보존 기간 선택:
  - 개발: 3일
  - 운영(권장): 7일
  - 규정 준수 필요: 30일 (최대 35일)

  백업 기간:
    03:00 - 04:00 (UTC)
    → 한국 시간 12:00 - 13:00 (낮 시간)

    💡 권장 시간 (한국 기준):
    - 18:00 - 19:00 UTC (새벽 3시-4시)
    - 트래픽이 가장 적은 시간 선택

  ✓ AWS Backup으로 백업 복사 (선택사항)

암호화:
  ✓ 암호화 활성화

  AWS KMS 키: (default) aws/rds 선택

  💡 개인정보보호법 준수를 위해 필수

로그 내보내기:
  CloudWatch Logs로 내보낼 로그 유형:
  ✓ 오류 로그
  ✓ 느린 쿼리 로그 (선택사항, 권장)
  ☐ 일반 로그 (디버깅 시에만)
  ☐ 감사 로그 (규정 준수 필요 시)

  💡 느린 쿼리 로그:
  - 1초 이상 걸리는 쿼리 자동 기록
  - 성능 최적화에 유용

유지 관리:
  자동 마이너 버전 업그레이드 활성화:
    ✓ 활성화 (권장)

    💡 마이너 버전 업그레이드:
    - 8.0.35 → 8.0.36 같은 보안 패치
    - 자동 적용, 다운타임 최소화

  유지 관리 기간:
    월요일 18:00 - 19:00 UTC (새벽 3시-4시)

    💡 트래픽 적은 시간대 선택

삭제 방지:
  ✓ 삭제 방지 활성화 (운영 환경 필수!)

  💡 실수로 DB 삭제 방지
```

**12단계: 예상 월별 비용 확인**

생성 전 우측 상단의 **"월별 예상 요금"** 확인:

```
예상 비용 (db.t3.medium, Multi-AZ, 100GB):
- 인스턴스: ~$120/월
- 스토리지: ~$20/월
- 백업: ~$5/월 (100GB 기준)
─────────────────
총 예상: ~$145/월
```

**13단계: 데이터베이스 생성**

1. 모든 설정 확인
2. 우측 하단 **"데이터베이스 생성"** 버튼 클릭
3. 생성 진행 상황 확인 (약 10-15분 소요)
4. 상태가 "사용 가능"으로 변경될 때까지 대기

#### 4.3.4 개발 환경(Development) RDS 생성 (선택사항)

동일한 과정으로, 차이점만 명시:

```
DB 인스턴스 식별자: deepple-dev-db
템플릿: 개발/테스트
DB 인스턴스 클래스: db.t3.micro
Multi-AZ: 아니요
스토리지: 20GB, 자동 조정 최대 100GB
백업 보존 기간: 3일
Performance Insights: 비활성화 (비용 절감)
삭제 방지: 비활성화 (개발 환경은 재생성 용이)
```

### 4.4 RDS 엔드포인트 확인

**1단계: 엔드포인트 복사**

1. RDS 콘솔 → "데이터베이스"
2. `deepple-prod-db` 클릭
3. **"연결 & 보안"** 탭에서 엔드포인트 확인

```
엔드포인트:
  deepple-prod-db.xxxxxxxxxxxxx.ap-northeast-2.rds.amazonaws.com

포트:
  3306
```

**2단계: 엔드포인트 저장**

이 엔드포인트를 `.env` 파일의 `MYSQL_HOST`에 사용합니다.

```bash
MYSQL_HOST=deepple-prod-db.xxxxxxxxxxxxx.ap-northeast-2.rds.amazonaws.com
```

### 4.5 Flyway 마이그레이션 확인 (DEEPPLE 특화)

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

### 4.6 RDS 중지 및 비용 관리

**RDS 중지 시 주의사항**

RDS를 일시적으로 사용하지 않을 때 중지할 수 있지만, 몇 가지 중요한 사항이 있습니다:

#### 1. 자동 재시작 정책

```
RDS를 중지하면 7일 후 자동으로 재시작됩니다
- AWS 정책: 유지보수 및 패치 적용 위해
- 장기 중지 불가: 계속 꺼두려면 7일마다 재중지 필요
- 알림 설정 권장: 재시작 알림 받도록 CloudWatch 알람 설정
```

#### 2. 중지 중 발생하는 비용

**절약되는 비용** ✅:

- 인스턴스 시간당 비용 (가장 큰 부분)
- 예: db.t3.small ($25/월) → $0

**계속 발생하는 비용** ❌:

- 스토리지 비용: 할당한 GB만큼 계속 과금
    - 예: 30GB × $0.115/GB = ~$3.5/월
- 백업 스토리지: 무료 범위 초과 시
- Enhanced Monitoring (활성화 시): ~$1-2/월

**총 절약률**: 약 85-90% 절감 (인스턴스 비용만 중단)

#### 3. RDS 중지 방법

```
AWS 콘솔에서:
1. RDS 콘솔 → 데이터베이스 선택
2. 작업 → 중지 클릭
3. 스냅샷 생성 여부 선택 (권장: 체크)
4. 확인

중지 완료까지: 약 2-5분 소요
```

#### 4. 완전한 비용 절감 방법

**장기간 사용하지 않을 경우**:

```
1. 최종 스냅샷 생성
   - RDS 콘솔 → 작업 → 스냅샷 생성
   - 이름: deepple-prod-db-final-snapshot-YYYYMMDD

2. RDS 인스턴스 삭제
   - 최종 스냅샷 생성: 예
   - 삭제 방지 비활성화 필요

3. 스냅샷 비용
   - 스토리지 비용의 약 1/3
   - 30GB 스냅샷: ~$1/월

4. 복원 시
   - 스냅샷에서 복원 (10-20분 소요)
   - 모든 데이터 보존
```

#### 5. 비용 비교 (db.t3.small, 30GB 기준)

| 상태               | 월 비용 | 절감률 | 재시작 시간 |
|------------------|------|-----|--------|
| 실행 중 (Single-AZ) | ~$28 | 0%  | -      |
| 중지됨              | ~$4  | 85% | 2-5분   |
| 삭제 (스냅샷 보관)      | ~$1  | 96% | 10-20분 |

#### 6. 실전 운영 팁

**개발/테스트 환경**:

```bash
# 업무 종료 시 중지 (수동 또는 Lambda 자동화)
# 월요일 아침 재시작
# 월 비용: ~$15 → ~$5 (약 60% 절감)
```

**운영 환경**:

```bash
# 중지 금지 (서비스 중단)
# Multi-AZ + Auto Scaling 권장
# 비용 절감은 예약 인스턴스 활용
```

**알람 설정 (자동 재시작 방지)**:

```
CloudWatch 이벤트:
- 이벤트 패턴: RDS DB Instance 상태 변경
- 필터: "starting" → "running"
- 알림: SNS 토픽으로 이메일/SMS 발송
```

#### 7. 엔드포인트 저장 필수!

RDS 중지 전 반드시 엔드포인트 정보를 저장하세요:

```bash
# RDS 엔드포인트 복사
RDS 콘솔 → 데이터베이스 선택 → 연결 & 보안 탭

엔드포인트: deepple-prod-db.xxxxx.ap-northeast-2.rds.amazonaws.com
포트: 3306
사용자 이름: admin
암호: [안전한 곳에 보관]

# .env 파일이나 안전한 문서에 저장!
```

💡 **권장 운영 방식**:

- 개발/테스트: 사용하지 않을 때 중지 → 비용 절감
- 운영 환경: 항상 실행 + 예약 인스턴스로 비용 절감

---

## 5. 캐시 서버 구성 (ElastiCache)

### 5.1 ElastiCache란?

**개념**:

- ElastiCache는 AWS의 관리형 인메모리 캐시 서비스입니다.
- Valkey는 Redis의 오픈소스 포크로, Redis와 호환되면서 완전한 오픈소스입니다.
- 데이터베이스 부하를 줄이고 응답 속도를 높입니다.

💡 **Valkey vs Redis**: Valkey는 Redis 7.2+ 호환이며, AWS에서 공식 지원합니다.

### 5.2 서브넷 그룹 생성 (먼저 해야 함)

**AWS 콘솔 → ElastiCache → 서브넷 그룹**으로 이동

**서브넷 그룹 생성** 클릭:

```
이름: deepple-prod-cache-subnet-group
설명: DEEPPLE 운영 ElastiCache 서브넷 그룹
VPC: deepple-prod-vpc

가용 영역 및 서브넷 선택:
✓ ap-northeast-2a → deepple-prod-private-1a
✓ ap-northeast-2c → deepple-prod-private-1c
```

### 5.3 보안 그룹 생성

**EC2 → 보안 그룹 → 보안 그룹 생성**

```
보안 그룹 이름: deepple-prod-cache-sg
설명: Security group for ElastiCache cluster
VPC: deepple-prod-vpc

인바운드 규칙:
┌─────────────────┬──────────┬─────────────────────────┬────────────────────────┐
│ 유형            │ 포트     │ 소스                    │ 설명                   │
├─────────────────┼──────────┼─────────────────────────┼────────────────────────┤
│ 사용자 지정 TCP │ 6379     │ deepple-prod-app-sg     │ Allow Cache from EC2   │
└─────────────────┴──────────┴─────────────────────────┴────────────────────────┘

아웃바운드 규칙:
- 기본값 유지 (모든 트래픽 허용)
```

### 5.4 ElastiCache 클러스터 생성

**AWS 콘솔 → ElastiCache → 캐시 생성** 클릭

**1단계: 클러스터 엔진 선택**

```
엔진 옵션:
  ○ Redis OSS
  ✓ Valkey (권장, 완전 오픈소스)

생성 방법:
  ○ 클러스터 캐시 설계 (복잡)
  ✓ 손쉬운 생성 (권장) 또는 클러스터 캐시 설계
```

**2단계: 클러스터 설정**

```
이름: deepple-prod-cache
위치:
  ✓ AWS 클라우드

클러스터 모드:
  ○ 활성화 (샤딩, 수평 확장, 복잡)
  ✓ 비활성화 (권장, 단순 구성) - 대부분의 경우 충분
```

**3단계: 엔진 버전**

```
Valkey 버전: 7.2 (최신 안정 버전, Redis 7.2 호환)
포트: 6379 (기본값)
파라미터 그룹: default.valkey7 (기본값 사용)
노드 유형: 아래 참조
```

**4단계: 노드 유형 선택**

| 환경      | 노드 유형           | 메모리     | vCPU | 비용/월  | 권장 사항    |
|---------|-----------------|---------|------|-------|----------|
| 개발      | cache.t3.micro  | 512MB   | 2    | ~$12  | 개발/테스트만  |
| 운영(소규모) | cache.t3.small  | 1.5GB   | 2    | ~$25  | ✅ 권장     |
| 운영(중규모) | cache.t3.medium | 3.1GB   | 2    | ~$50  | 대용량 캐시   |
| 운영(대규모) | cache.r7g.large | 13.07GB | 2    | ~$130 | 고성능 필요 시 |

```
선택:
  개발: cache.t3.micro
  운영: ✓ cache.t3.small (권장)
```

**5단계: 복제본 개수 (고가용성)**

```
복제본 수:
  개발 환경:
    ❌ 0개 - 복제본 없음 (비용 절감, 단일 장애점 있음)

  운영 환경:
    ✅ 1개 - 고가용성 및 읽기 확장 (권장!)
    ⭐ 2개 - 더 높은 가용성 (대규모 서비스, 선택사항)

💡 복제본 1개 설정 시 효과:
- 다른 AZ에 자동 배치
- 주 노드 장애 시 자동 승격 (Automatic Failover)
- 읽기 성능 향상 (읽기 분산)
- 비용: 노드당 추가 비용 발생
```

**6단계: Multi-AZ 자동 장애 조치**

```
Multi-AZ:
  개발: ❌ 비활성화 (비용 절감)
  운영: ✅ 활성화 (필수!)

  💡 Multi-AZ 효과:
  - 복제본을 다른 가용 영역에 배치
  - 주 노드 장애 시 1-2분 내 자동 복구
  - 운영 환경 필수 설정
```

**7단계: 서브넷 그룹**

```
서브넷 그룹:
  ✓ deepple-prod-cache-subnet-group (위에서 생성)

  💡 프라이빗 서브넷에 배치되어 외부 접근 불가
```

**8단계: 보안 설정**

```
VPC 보안 그룹:
  ✓ deepple-prod-cache-sg 선택

전송 중 암호화 (TLS):
  개발: ○ 비활성화
  운영: ✓ 활성화 (권장!)

저장 데이터 암호화:
  개발: ○ 비활성화 (비용 절감)
  운영: ✓ 활성화 (필수!)

  암호화 키:
    ✓ (기본값) AWS 관리형 키 사용
    ○ 고객 관리형 키 (KMS) - 고급 보안 필요 시

AUTH 토큰 (비밀번호 인증):
  개발: ○ 비활성화
  운영: ✓ 활성화 (보안 강화!)

  토큰: [강력한 랜덤 문자열, 안전하게 보관 필수!]

  💡 AUTH 토큰 생성:
  openssl rand -base64 32
```

**9단계: 백업 설정**

```
자동 백업:
  개발: ❌ 비활성화 (비용 절감)
  운영: ✅ 활성화 (데이터 보호!)

백업 보존 기간:
  개발: 1일
  운영: 3-7일 (권장: 3일)

백업 기간 (시간대):
  - 새벽 2:00-4:00 (트래픽 적은 시간대 선택)
```

**10단계: 로그 전송 (선택사항, 권장)**

```
로그 전송:
  ✓ 느린 로그 (Slow log) → CloudWatch Logs
  ✓ 엔진 로그 (Engine log) → CloudWatch Logs

  로그 형식: JSON (권장)
  로그 그룹: /aws/elasticache/cache/deepple-prod
```

**11단계: 유지 관리**

```
유지 관리 기간:
  - 요일: 화요일 (권장)
  - 시간: 새벽 3:00-4:00 (트래픽 적은 시간)

  💡 자동 패치 및 업데이트 시간
```

**12단계: 태그 및 생성**

```
태그 (선택):
  Name: deepple-prod-cache
  Environment: Production
  Project: DEEPPLE
  Engine: valkey

→ "생성" 버튼 클릭
```

**생성 시간**: 약 10-15분 소요

### 5.5 엔드포인트 확인

클러스터 생성 완료 후:

**ElastiCache → 캐시 → deepple-prod-cache** 클릭

```
기본 엔드포인트 (Primary, 쓰기/읽기):
  deepple-prod-cache.xxxxx.apne2.cache.amazonaws.com:6379

읽기 엔드포인트 (Reader, 읽기 전용 - 복제본 있을 경우):
  deepple-prod-cache-ro.xxxxx.apne2.cache.amazonaws.com:6379
```

💡 **애플리케이션 연결 전략**:

- **쓰기 작업**: 기본 엔드포인트 사용
- **읽기 작업**: 읽기 엔드포인트 사용 (부하 분산)

### 5.6 연결 테스트

EC2에서 ElastiCache 연결 테스트:

```bash
# Redis CLI 설치 (Valkey는 Redis 호환이므로 redis-cli 사용)
sudo yum install -y redis  # Amazon Linux
sudo apt install -y redis-tools  # Ubuntu

# TLS 없이 연결 테스트 (개발 환경)
redis-cli -h deepple-prod-cache.xxxxx.apne2.cache.amazonaws.com -p 6379

# TLS + AUTH 토큰 사용 연결 (운영 환경)
redis-cli -h deepple-prod-cache.xxxxx.apne2.cache.amazonaws.com \
  -p 6379 \
  --tls \
  -a [AUTH_토큰]

# 연결 후 테스트
127.0.0.1:6379> ping
PONG
127.0.0.1:6379> set test "hello valkey"
OK
127.0.0.1:6379> get test
"hello valkey"
127.0.0.1:6379> info server
# Valkey 서버 정보 출력
```

### 5.7 애플리케이션 설정 (.env)

```bash
# ElastiCache (Redis 호환)
REDIS_HOST=deepple-prod-cache.xxxxx.apne2.cache.amazonaws.com
REDIS_PORT=6379
REDIS_PASSWORD=[AUTH 토큰]
REDIS_SSL_ENABLED=true  # 운영 환경에서 TLS 활성화 시
```

💡 **참고**: Spring Boot의 Redis 라이브러리는 Valkey와 완전 호환됩니다 (Redis 프로토콜 사용).

---

## 6. 스토리지 구성 (S3)

### 6.1 S3란?

**개념**: Simple Storage Service는 객체 스토리지 서비스로, 이미지, 동영상, 프로필 사진 등의 파일을 저장합니다.

**S3 특징**:

- 무제한 용량 (파일당 최대 5TB)
- 99.999999999% (11 9's) 내구성
- 자동 복제 (리전 내 여러 가용 영역)
- 비용 효율적 (사용한 만큼만 지불)

**DEEPPLE 프로젝트 사용 목적**:

- 사용자 프로필 사진
- 인증 사진 (본인 인증)
- 데이팅 인증 사진
- 커뮤니티 이미지

### 6.2 S3 버킷 생성

#### 6.2.1 운영 환경 버킷 생성

**1단계: S3 서비스로 이동**

1. AWS 콘솔에서 "S3" 검색
2. **"버킷 만들기"** 클릭

**2단계: 일반 구성**

```
버킷 이름:
  deepple-prod-storage

  💡 중요:
  - 전 세계에서 고유해야 함 (이미 사용 중이면 에러)
  - 소문자, 숫자, 하이픈(-)만 사용 가능
  - 변경 불가능 (버킷 생성 후)

AWS 리전:
  ✓ 아시아 태평양(서울) ap-northeast-2

  💡 EC2, RDS와 동일한 리전 선택 (전송 비용 절감)
```

**3단계: 객체 소유권**

```
객체 소유권:
  ✓ ACL 비활성화됨(권장)

  💡 설명:
  - ACL(Access Control List) 대신 버킷 정책 사용
  - 모든 객체를 버킷 소유자가 소유
  - 현대적이고 안전한 방식
```

**4단계: 퍼블릭 액세스 차단 설정 (매우 중요!)**

```
이 버킷의 퍼블릭 액세스 차단 설정:
  ✓ 모든 퍼블릭 액세스 차단 (강력 권장!)

  ☑ 새 ACL(액세스 제어 목록)을 통해 부여된 버킷 및 객체에 대한 퍼블릭 액세스 차단
  ☑ 임의의 ACL(액세스 제어 목록)을 통해 부여된 버킷 및 객체에 대한 퍼블릭 액세스 차단
  ☑ 새 퍼블릭 버킷 또는 액세스 포인트 정책을 통해 부여된 버킷 및 객체에 대한 퍼블릭 액세스 차단
  ☑ 임의의 퍼블릭 버킷 또는 액세스 포인트 정책을 통해 버킷 및 객체에 대한 퍼블릭 및 교차 계정 액세스 차단

  💡 매우 중요:
  - DEEPPLE은 Presigned URL 방식 사용
  - 직접적인 퍼블릭 액세스는 불필요
  - 보안 위험 방지
```

**5단계: 버킷 버전 관리**

```
버킷 버전 관리:
  개발: ○ 비활성화 (비용 절감)
  운영: ✓ 활성화 (권장!)

  💡 버전 관리 효과:
  - 파일 삭제/덮어쓰기 시 이전 버전 유지
  - 실수로 삭제한 파일 복구 가능
  - 비용: 모든 버전의 스토리지 비용 발생
```

**6단계: 태그 (선택사항)**

```
태그:
  Environment = production
  Project = deepple
  ManagedBy = manual (또는 terraform)
```

**7단계: 기본 암호화**

```
기본 암호화:
  암호화 유형:
    ✓ SSE-S3 (서버 측 암호화 - Amazon S3 관리형 키)

  버킷 키:
    ✓ 활성화 (권장)

  💡 버킷 키 효과:
  - 암호화 요청 비용 99% 절감
  - 성능 향상
  - 추가 비용 없음
```

**8단계: 고급 설정**

```
객체 잠금:
  ○ 비활성화 (DEEPPLE에서 불필요)

  💡 객체 잠금이란?
  - 법률/규정 준수용 기능
  - 지정된 기간 동안 객체 삭제/수정 불가
  - 금융, 의료 등 특수 목적
```

**9단계: 버킷 생성**

- 모든 설정 확인
- **"버킷 만들기"** 버튼 클릭
- 생성 완료 확인

#### 6.2.2 개발 환경 버킷 생성 (선택사항)

동일한 과정으로:

```
버킷 이름: deepple-dev-storage
버전 관리: 비활성화 (비용 절감)
나머지: 운영 환경과 동일
```

### 6.3 수명 주기 정책 설정 (비용 절감)

버킷 생성 후 수명 주기 규칙 추가:

**1단계: 버킷 선택**

1. S3 콘솔에서 `deepple-prod-storage` 클릭
2. **"관리"** 탭 클릭
3. **"수명 주기 규칙 생성"** 클릭

**2단계: 규칙 1 - 오래된 버전 삭제**

```
수명 주기 규칙 이름: delete-old-versions

규칙 범위:
  ✓ 버킷의 모든 객체에 적용

수명 주기 규칙 작업:
  ☑ 객체의 이전 버전 영구 삭제

  이전 버전 영구 삭제:
    90일 후

  💡 효과:
  - 90일 이전 버전 자동 삭제
  - 스토리지 비용 절감
```

**3단계: 규칙 2 - 미완료 멀티파트 업로드 정리**

```
수명 주기 규칙 이름: cleanup-incomplete-uploads

규칙 범위:
  ✓ 버킷의 모든 객체에 적용

수명 주기 규칙 작업:
  ☑ 불완전한 멀티파트 업로드 삭제

  불완전한 멀티파트 업로드 삭제:
    7일 후

  💡 효과:
  - 업로드 실패한 파일 조각 자동 정리
  - 불필요한 비용 방지
```

### 6.4 CORS 설정

**1단계: 권한 탭으로 이동**

1. 버킷 선택 → **"권한"** 탭
2. **"CORS(Cross-Origin Resource Sharing)"** 섹션
3. **"편집"** 클릭

**2단계: CORS 구성 입력**

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
      "https://api.deepple.com",
      "https://dev-api.deepple.com",
      "http://localhost:3000"
    ],
    "ExposeHeaders": [
      "ETag",
      "x-amz-server-side-encryption",
      "x-amz-request-id"
    ],
    "MaxAgeSeconds": 3000
  }
]
```

**3단계: 저장**

💡 **CORS가 필요한 이유**:

- 웹 브라우저에서 직접 S3 업로드 시 필요
- Presigned URL 사용 시에도 권장

### 6.5 버킷 정책 설정 (선택사항)

**CloudFront 사용 시** 또는 **특정 IP 제한 시** 버킷 정책 추가:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowCloudFrontAccess",
      "Effect": "Allow",
      "Principal": {
        "Service": "cloudfront.amazonaws.com"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::deepple-prod-storage/*",
      "Condition": {
        "StringEquals": {
          "AWS:SourceArn": "arn:aws:cloudfront::ACCOUNT-ID:distribution/DISTRIBUTION-ID"
        }
      }
    }
  ]
}
```

### 6.6 CloudFront CDN 연동 (선택사항, 권장)

**개념**: CloudFront는 AWS의 CDN 서비스로, 전 세계에 파일을 캐싱하여 빠른 전송을 제공합니다.

**장점**:

- 이미지 로딩 속도 향상 (해외 사용자도 빠름)
- S3 요청 비용 절감 (캐시 히트 시)
- DDoS 보호
- HTTPS 지원

**CloudFront 배포 생성 (간략)**:

```
1. CloudFront 콘솔로 이동
2. "배포 생성" 클릭
3. 오리진:
   - 오리진 도메인: deepple-prod-storage.s3.ap-northeast-2.amazonaws.com
   - 오리진 액세스: Origin Access Control (권장)
4. 기본 캐시 동작:
   - 뷰어 프로토콜 정책: Redirect HTTP to HTTPS
   - 허용된 HTTP 메서드: GET, HEAD, OPTIONS
   - 캐시 정책: CachingOptimized
5. 배포 생성
6. 배포 도메인 이름 사용: d111111abcdef8.cloudfront.net
```

### 6.7 IAM 정책 설정

**EC2 IAM Role에 S3 권한 추가** (섹션 2.2에서 생성한 `deepple-prod-app-role`):

**1단계: IAM 역할로 이동**

1. IAM 콘솔 → "역할"
2. `deepple-prod-app-role` 검색 후 클릭

**2단계: 인라인 정책 추가**

1. **"권한 추가"** → **"인라인 정책 생성"**
2. **JSON** 탭 클릭
3. 다음 정책 입력:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "S3AccessForDEEPPLE",
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

4. **"정책 검토"** 클릭
5. 정책 이름: `S3AccessPolicy`
6. **"정책 생성"** 클릭

### 6.8 애플리케이션 설정 (.env)

```bash
# AWS S3 (IAM Role 사용 시 Access Key 불필요)
AWS_S3_BUCKET_NAME=deepple-prod-storage
AWS_S3_REGION=ap-northeast-2

# CloudFront 사용 시 (선택사항)
CLOUDFRONT_DOMAIN=d111111abcdef8.cloudfront.net
```

💡 **Presigned URL 방식**:

- DEEPPLE은 Presigned URL 방식 사용
- EC2에서 임시 URL 생성 → 클라이언트에 전달
- 클라이언트가 직접 S3에 업로드
- 서버 부하 최소화

### 6.9 S3 사용량 모니터링

**1단계: S3 스토리지 렌즈 활성화**

1. S3 콘솔 → **"스토리지 렌즈"**
2. 기본 대시보드에서 사용량 확인

**2단계: 비용 확인**

```
예상 비용 (운영 환경):
- 스토리지: 100GB × $0.025/GB = $2.5/월
- PUT 요청: 10,000건/월 × $0.005/1000건 = $0.05/월
- GET 요청: 100,000건/월 × $0.0004/1000건 = $0.04/월
- 데이터 전송: 50GB × $0.126/GB = $6.3/월 (CloudFront 미사용 시)
───────────────────────────
총 예상: ~$9/월 (CloudFront 미사용)
         ~$3/월 (CloudFront 사용 시, 전송 비용 절감)
```

---

## 7. 컴퓨팅 리소스 (EC2)

### 7.1 EC2란?

**개념**: Elastic Compute Cloud는 가상 서버입니다. 여러분의 애플리케이션이 실행되는 컴퓨터라고 생각하면 됩니다.

### 7.2 보안 그룹 생성

💡 **보안 그룹이란?**
보안 그룹은 EC2 인스턴스에 대한 **가상 방화벽** 역할을 합니다. 인바운드(들어오는)와 아웃바운드(나가는) 트래픽을 제어합니다.

#### 애플리케이션 보안 그룹 생성 (`deepple-prod-app-sg`)

**1단계: 보안 그룹 생성 시작**

1. AWS 콘솔 → **EC2 대시보드** 접속
2. 좌측 메뉴 → **네트워크 및 보안** → **보안 그룹** 클릭
3. 우측 상단 **[보안 그룹 생성]** 버튼 클릭

**2단계: 기본 세부 정보**

```
보안 그룹 이름: deepple-prod-app-sg
설명: DEEPPLE Production Application Server Security Group
VPC: deepple-prod-vpc (앞서 생성한 VPC 선택)
```

**3단계: 인바운드 규칙 추가**

아래 **[인바운드 규칙 추가]** 버튼을 4번 클릭하여 규칙 추가:

**규칙 1 - SSH 접속 (관리자만)**

```
유형: SSH
프로토콜: TCP
포트 범위: 22
소스: 내 IP (자동 감지) 또는 사무실 IP (예: 123.456.789.0/32)
설명: Admin SSH access only
```

⚠️ **보안 경고**: SSH는 반드시 특정 IP만 허용! `0.0.0.0/0` (모든 IP) 절대 금지!

**규칙 2 - HTTP (로드 밸런서에서)**

```
유형: HTTP
프로토콜: TCP
포트 범위: 80
소스 유형: 사용자 지정
소스: deepple-prod-alb-sg (보안 그룹 선택)
설명: HTTP from ALB
```

💡 **팁**: 소스에 보안 그룹을 지정하면 해당 보안 그룹이 할당된 리소스만 접근 가능합니다.

**규칙 3 - HTTPS (로드 밸런서에서)**

```
유형: HTTPS
프로토콜: TCP
포트 범위: 443
소스 유형: 사용자 지정
소스: deepple-prod-alb-sg (보안 그룹 선택)
설명: HTTPS from ALB
```

**규칙 4 - Spring Boot 애플리케이션 포트 (로드 밸런서에서)**

```
유형: 사용자 지정 TCP
프로토콜: TCP
포트 범위: 8080
소스 유형: 사용자 지정
소스: deepple-prod-alb-sg (보안 그룹 선택)
설명: Spring Boot app port from ALB
```

💡 **왜 8080인가?**
DEEPPLE Spring Boot 애플리케이션은 8080 포트에서 실행됩니다. ALB가 이 포트로 헬스 체크 및 트래픽 전달을 수행합니다.

**4단계: 아웃바운드 규칙 확인**

기본 아웃바운드 규칙 유지 (자동으로 설정됨):

```
유형: 모든 트래픽
프로토콜: 전체
포트 범위: 전체
대상: 0.0.0.0/0
설명: Allow all outbound traffic
```

💡 **아웃바운드는 왜 모두 허용?**

- EC2가 외부 서비스(RDS, ElastiCache, S3, API 등) 호출해야 함
- 인바운드는 엄격히 제한, 아웃바운드는 일반적으로 허용

**5단계: 태그 추가**

```
키: Name, 값: deepple-prod-app-sg
키: Environment, 값: Production
키: Project, 값: DEEPPLE
```

**6단계: 생성 완료**

우측 하단 **[보안 그룹 생성]** 버튼 클릭

---

#### 인바운드 규칙 요약

| 규칙         | 포트   | 소스      | 용도            | 보안 수준    |
|------------|------|---------|---------------|----------|
| SSH        | 22   | 관리자 IP만 | 서버 관리         | ⚠️ 매우 중요 |
| HTTP       | 80   | ALB SG  | HTTP 리다이렉트    | ✅ 안전     |
| HTTPS      | 443  | ALB SG  | HTTPS 트래픽     | ✅ 안전     |
| Custom TCP | 8080 | ALB SG  | Spring Boot 앱 | ✅ 안전     |

💡 **보안 모범 사례**:

- ✅ **최소 권한 원칙**: 필요한 포트만 개방
- ✅ **소스 제한**: SSH는 관리자 IP만, 애플리케이션 포트는 ALB만
- ✅ **설명 작성**: 각 규칙의 용도를 명확히 기록
- ❌ **0.0.0.0/0 남용 금지**: SSH, DB 포트에 절대 사용 금지

### 7.3 EC2 인스턴스 생성

💡 **EC2 인스턴스란?**
애플리케이션이 실제로 실행되는 가상 서버입니다. DEEPPLE Spring Boot 애플리케이션이 Docker 컨테이너로 실행됩니다.

#### 7.3.1 EC2 인스턴스 시작

**1단계: EC2 대시보드 접속**

1. AWS 콘솔 → **EC2** 검색 후 클릭
2. 리전이 **"서울 (ap-northeast-2)"**인지 확인
3. 좌측 메뉴 → **"인스턴스"** 클릭
4. 우측 상단 **"인스턴스 시작"** 버튼 클릭

**2단계: 이름 및 태그**

```
이름: deepple-prod-app

추가 태그 (선택사항):
  Environment = production
  Project = deepple
  Role = application
```

**3단계: 애플리케이션 및 OS 이미지 (AMI) 선택**

```
빠른 시작 탭에서 선택:

  ✅ Amazon Linux 2023 AMI (권장)
     - 설명: Amazon Linux 2023 AMI
     - 아키텍처: 64비트 (x86)
     - 루트 디바이스 유형: EBS

  또는

  ⭐ Ubuntu Server 22.04 LTS
     - 설명: Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
     - 아키텍처: 64비트 (x86)

💡 권장: Amazon Linux 2023
  - AWS에 최적화됨
  - 보안 패치 자동 적용
  - yum 패키지 관리자 사용
```

**4단계: 인스턴스 유형 선택**

```
인스턴스 유형 선택:

개발 환경:
  ❌ t3.micro (1GB RAM, 비권장)
     - Spring Boot 실행에 메모리 부족

  ✅ t3.small (2GB RAM, 권장)
     - 비용: ~$15/월
     - 개발/테스트 용도 적합

운영 환경:
  ✅ t3.medium (4GB RAM, 시작용 권장)
     - 비용: ~$30/월
     - 동시 접속자 100-300명
     - 2 vCPU, 4GB RAM

  ⭐ t3.large (8GB RAM, 안정적)
     - 비용: ~$60/월
     - 동시 접속자 500-1000명
     - 2 vCPU, 8GB RAM
     - 트래픽 증가 시 권장

  🚀 t3.xlarge (16GB RAM, 대규모)
     - 비용: ~$120/월
     - 대용량 트래픽
     - 4 vCPU, 16GB RAM

💡 권장: 운영 환경은 t3.medium에서 시작, 필요시 t3.large로 업그레이드
```

**5단계: 키 페어 (로그인) 설정**

```
키 페어 이름:
  - 기존 키 페어 선택: (이미 있으면 선택)
  또는
  - "새 키 페어 생성" 클릭

새 키 페어 생성 시:
  키 페어 이름: deepple-prod-key
  키 페어 유형: RSA
  프라이빗 키 파일 형식: .pem (Mac/Linux) 또는 .ppk (Windows/PuTTY)

  → "키 페어 생성" 클릭

⚠️ 매우 중요:
- 키 파일은 자동으로 다운로드됩니다
- 이 키 파일을 안전한 곳에 보관하세요!
- 분실 시 서버 접속 불가능
- 절대 Git에 커밋하지 마세요!

다운로드 후 로컬에서 권한 설정:
chmod 400 ~/Downloads/deepple-prod-key.pem
```

**6단계: 네트워크 설정**

```
"편집" 버튼 클릭:

VPC:
  ✓ deepple-prod-vpc 선택

서브넷:
  ✓ deepple-prod-subnet-public1-ap-northeast-2a (퍼블릭 서브넷)

  💡 서브넷 선택 Trade-off 분석:

  [선택한 방식] 퍼블릭 서브넷 + 보안 그룹 강화:
  ✅ 비용 효율적: NAT Gateway 불필요 (~$40-50/월 절약)
  ✅ 인터넷 직접 연결: Docker pull, apt update 가능
  ✅ 간단한 구조: 추가 인프라 불필요
  ✅ 보안 그룹으로 충분한 보호 가능
  ⚠️ EC2가 퍼블릭 IP를 가짐 (보안 그룹으로 접근 제어)

  [대안] 프라이빗 서브넷 + NAT Gateway (Best Practice):
  ✅ 완벽한 격리: EC2가 인터넷에 직접 노출 안 됨
  ✅ 더 높은 보안 수준
  ❌ 추가 비용: NAT Gateway $40-50/월
  ❌ 복잡도 증가: Bastion Host 또는 Session Manager 필요

  → 소규모 운영 환경에서는 퍼블릭 서브넷 + 보안 그룹 방식이 합리적
  → 규모가 커지면 프라이빗 서브넷으로 전환 고려

퍼블릭 IP 자동 할당:
  ✓ 활성화

  💡 퍼블릭 IP 필요 이유:
  - 인터넷을 통한 SSH 접속 (보안 그룹으로 IP 제한)
  - Docker 이미지 Pull
  - 소프트웨어 업데이트 다운로드
  - 외부 API 호출 (결제, SMS 등)

방화벽 (보안 그룹):
  ○ 기존 보안 그룹 선택
  ✓ deepple-prod-app-sg (앞서 생성한 보안 그룹)

  "default" 보안 그룹은 제거하세요!

  ⚠️ 중요: 보안 그룹 인바운드 규칙 확인
  - SSH (22): 특정 IP만 허용 (회사/집 IP)
  - 애플리케이션 (8080): ALB 보안 그룹만 허용
  - 일반 인터넷 사용자는 EC2에 직접 접근 불가
```

**7단계: 스토리지 구성**

```
루트 볼륨 설정:

볼륨 1 (루트):
  크기(GiB): 30
  볼륨 유형: gp3 (범용 SSD, 최신)

  💡 gp3 선택 이유:
  - gp2보다 20% 빠르고 비용 동일
  - IOPS: 3000 (기본값, 충분)
  - 처리량: 125 MB/s (기본값)

  종료 시 삭제: ✓ 체크 (기본값)
  암호화: ○ 암호화 안 함 (선택사항)

💡 스토리지 크기 가이드:
- 30GB: OS + Docker + 애플리케이션 이미지
- 로그 파일은 CloudWatch로 전송되므로 큰 용량 불필요
- 필요시 나중에 EBS 볼륨 확장 가능

추가 볼륨 (선택사항):
  데이터 전용 볼륨이 필요한 경우에만 추가
  - "새 볼륨 추가" 클릭
  - 크기: 50GiB
  - 볼륨 유형: gp3
```

**8단계: 고급 세부 정보**

```
"고급 세부 정보" 펼치기:

IAM 인스턴스 프로파일:
  ✓ deepple-prod-app-role (섹션 2.2에서 생성한 역할)

  💡 IAM Role 필요 이유:
  - S3 버킷 접근 (파일 업로드/다운로드)
  - CloudWatch Logs 전송
  - 액세스 키 없이 안전하게 AWS 서비스 사용

종료 방식:
  중지 - 최대 절전 모드 동작: 중지

종료 방지 기능 활성화:
  개발: ☐ 비활성화
  운영: ✓ 활성화 (권장, 실수로 인한 삭제 방지)

세부 CloudWatch 모니터링:
  ○ 비활성화 (기본값, 비용 절감)

  💡 세부 모니터링:
  - 활성화 시: 1분 간격 메트릭 ($2.10/월 추가)
  - 비활성화 시: 5분 간격 메트릭 (무료)
  - 대부분의 경우 5분 간격으로 충분

사용자 데이터 (User Data):
  아래 스크립트를 복사하여 붙여넣기
```

**사용자 데이터 스크립트** (Amazon Linux 2023용):

```bash
#!/bin/bash
# Amazon Linux 2023

# 로그 파일 설정
exec > >(tee /var/log/user-data.log)
exec 2>&1

echo "===== Server Setup Started ====="
date

# 시스템 업데이트 
echo "===== System Update ====="
yum update -y

# 타임존 설정
echo "===== Setting timezone to Asia/Seoul ====="
timedatectl set-timezone Asia/Seoul

# Docker 설치
echo "===== Installing Docker ====="
yum install -y docker
systemctl start docker
systemctl enable docker

# ec2-user를 docker 그룹에 추가
usermod -aG docker ec2-user

# 프로젝트 디렉토리 생성
echo "===== Creating directories ====="
mkdir -p /home/ec2-user/deepple
mkdir -p /home/ec2-user/secrets           # Firebase 인증서 저장 위치
mkdir -p /home/ec2-user/certs/appstore    # App Store 인증서 저장 위치

# 보안 권한 설정
chmod 700 /home/ec2-user/secrets          # secrets는 소유자만 접근
chmod 755 /home/ec2-user/certs            # certs는 읽기 가능
chmod 755 /home/ec2-user/certs/appstore

# 소유권 설정
chown -R ec2-user:ec2-user /home/ec2-user/deepple
chown -R ec2-user:ec2-user /home/ec2-user/secrets
chown -R ec2-user:ec2-user /home/ec2-user/certs

echo "===== Server Setup Completed ====="
date
```

**💡 스크립트 구성 요소**:

**필수 항목**:

- ✅ Docker 설치 - 애플리케이션 실행에 필수
- ✅ 디렉토리 생성 - Firebase, App Store 인증서 저장용
- ✅ 타임존 설정 - 로그 시간 일관성

💡 **사용자 데이터 스크립트 설명**:

- EC2 인스턴스 최초 부팅 시 자동 실행
- Docker, Docker Compose 자동 설치
- 프로젝트에 필요한 디렉토리 생성
- 로그는 `/var/log/user-data.log`에 저장

**9단계: 인스턴스 시작**

```
우측 "요약" 패널에서 설정 확인:
  - 인스턴스 수: 1
  - AMI: Amazon Linux 2023 또는 Ubuntu 22.04
  - 인스턴스 유형: t3.medium (또는 선택한 타입)
  - 키 페어: deepple-prod-key
  - 네트워크: deepple-prod-vpc
  - 보안 그룹: deepple-prod-app-sg
  - 스토리지: 30GB gp3

→ 우측 하단 "인스턴스 시작" 버튼 클릭
```

**10단계: 인스턴스 시작 확인**

```
1. "성공적으로 인스턴스를 시작했습니다" 메시지 확인
2. "인스턴스 보기" 클릭
3. 인스턴스 상태:
   - 인스턴스 상태: 실행 중 (초록색)
   - 상태 확인: 2/2 통과 (약 5분 소요)

4. 인스턴스 ID 확인: i-0123456789abcdef0
5. 퍼블릭 IPv4 주소 확인: 13.124.xxx.xxx (메모!)
```

#### 7.3.2 환경별 인스턴스 구성 비교

| 항목           | Development        | Production           |
|--------------|--------------------|----------------------|
| 인스턴스 이름      | deepple-dev-app-01 | deepple-prod-app-01  |
| 인스턴스 유형      | t3.small           | t3.medium ~ t3.large |
| 스토리지         | 20GB gp3           | 30GB gp3             |
| 퍼블릭 IP       | 활성화                | 활성화                  |
| 종료 방지        | 비활성화               | ✅ 활성화                |
| 세부 모니터링      | 비활성화               | 선택사항                 |
| Auto Scaling | 없음                 | ⭐ 권장 (2-4대)          |

#### 7.3.3 인스턴스 시작 후 확인

**SSH 접속 테스트**:

```bash
# 로컬 터미널에서
chmod 400 ~/Downloads/deepple-prod-key.pem

# Amazon Linux 2023
ssh -i ~/Downloads/deepple-prod-key.pem ec2-user@13.124.xxx.xxx

# Ubuntu
ssh -i ~/Downloads/deepple-prod-key.pem ubuntu@13.124.xxx.xxx
```

**초기 설정 스크립트 확인**:

```bash
# EC2에 접속한 후

# 사용자 데이터 로그 확인
sudo cat /var/log/user-data.log

# Docker 설치 확인
docker --version
# Docker version 24.0.5, build ced0996

docker-compose --version
# Docker Compose version v2.23.0

# Docker 그룹 확인 (ec2-user가 docker 그룹에 있어야 함)
groups
# ec2-user wheel docker

# 디렉토리 확인
ls -la /home/ec2-user/
# drwxr-xr-x  2 ec2-user ec2-user   24 Jan  1 12:00 certs
# drwxr-xr-x  2 ec2-user ec2-user    6 Jan  1 12:00 deepple
# drwx------  2 ec2-user ec2-user    6 Jan  1 12:00 secrets

# 타임존 확인
timedatectl
# Time zone: Asia/Seoul (KST, +0900)
```

💡 **트러블슈팅**:

- Docker 명령어에 `sudo` 필요 시: 로그아웃 후 재로그인
- 사용자 데이터 실행 실패 시: `/var/log/user-data.log` 확인

### 7.4 EC2 초기 설정

SSH로 접속 후:

```bash
# 키 파일 권한 설정 (로컬)
chmod 400 deepple-prod-key.pem

# SSH 접속
ssh -i deepple-prod-key.pem ec2-user@[EC2_PUBLIC_IP]

# Docker 확인
docker --version

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

💡 **ALB 보안 그룹의 역할**
ALB는 인터넷에서 들어오는 모든 트래픽을 받아서 백엔드 EC2 인스턴스로 전달합니다. 따라서 인터넷(0.0.0.0/0)에서 HTTP/HTTPS를 허용해야 합니다.

#### ALB 보안 그룹 생성 (`deepple-prod-alb-sg`)

**1단계: 보안 그룹 생성 시작**

1. AWS 콘솔 → **VPC 대시보드** 접속
2. 좌측 메뉴 → **보안 그룹** 클릭
3. 우측 상단 **[보안 그룹 생성]** 버튼 클릭

**2단계: 기본 세부 정보**

```
보안 그룹 이름: deepple-prod-alb-sg
설명: DEEPPLE Production ALB Security Group
VPC: deepple-prod-vpc (앞서 생성한 VPC 선택)
```

**3단계: 인바운드 규칙 추가**

아래 **[인바운드 규칙 추가]** 버튼을 2번 클릭하여 규칙 추가:

**규칙 1 - HTTP (모든 인터넷에서)**

```
유형: HTTP
프로토콜: TCP
포트 범위: 80
소스: 0.0.0.0/0 (모든 곳)
설명: Allow HTTP from anywhere
```

💡 **왜 HTTP를 열어야 하나요?**

- HTTP 트래픽을 받아서 HTTPS로 리디렉션하기 위함
- 사용자가 http://api.deepple.com 으로 접속해도 자동으로 https:// 로 전환

**규칙 2 - HTTPS (모든 인터넷에서)**

```
유형: HTTPS
프로토콜: TCP
포트 범위: 443
소스: 0.0.0.0/0 (모든 곳)
설명: Allow HTTPS from anywhere
```

💡 **HTTPS 트래픽**

- 실제 애플리케이션 트래픽은 모두 HTTPS로 전달됨
- SSL 인증서는 ALB에서 종료 (SSL Termination)

**4단계: 아웃바운드 규칙 확인**

기본 아웃바운드 규칙 유지 (자동으로 설정됨):

```
유형: 모든 트래픽
프로토콜: 전체
포트 범위: 전체
대상: 0.0.0.0/0
설명: Allow all outbound traffic
```

💡 **아웃바운드 규칙**

- ALB가 백엔드 EC2 인스턴스(8080 포트)로 트래픽 전달하기 위함
- 헬스 체크 요청도 아웃바운드 규칙을 통해 전송

**5단계: 태그 추가**

```
키: Name, 값: deepple-prod-alb-sg
키: Environment, 값: Production
키: Project, 값: DEEPPLE
```

**6단계: 생성 완료**

우측 하단 **[보안 그룹 생성]** 버튼 클릭

---

#### 인바운드 규칙 요약

| 규칙    | 포트  | 소스        | 용도        | 이유          |
|-------|-----|-----------|-----------|-------------|
| HTTP  | 80  | 0.0.0.0/0 | HTTP 리디렉션 | HTTPS 강제 전환 |
| HTTPS | 443 | 0.0.0.0/0 | 실제 트래픽    | 사용자 요청 처리   |

💡 **보안 차이점**:

- **ALB**: 인터넷에서 접근 가능 (0.0.0.0/0)
- **EC2**: ALB에서만 접근 가능 (deepple-prod-alb-sg)
- 이렇게 하면 EC2는 인터넷에 직접 노출되지 않음

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

#### 의존성 추가

`build.gradle`:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    // ... 기타 의존성
}
```

#### 설정 파일

`application-prod.yml` (운영 환경):

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health  # 보안상 health만 노출 (info, metrics 제거)
      base-path: /actuator
  endpoint:
    health:
      show-details: never  # 외부 노출 방지 - 상세 정보 완전 차단
  server:
    port: 8080  # 애플리케이션과 동일 포트
```

#### 각 설정 항목 설명

1. **`management.endpoints.web.exposure.include`**
    - 외부에 노출할 엔드포인트 목록을 지정합니다.
    - **운영 환경 권장**: `health`만 노출
    - `health`: 애플리케이션 상태 정보 (ALB 헬스체크에 필수)
    - `info`: 애플리케이션 정보 (버전, 빌드 정보 등) - 보안상 제거 권장
    - `metrics`: 애플리케이션 성능 메트릭 (메모리, CPU, 요청 수 등) - 보안상 제거 권장
    - ⚠️ **중요**: `info`, `metrics`는 시스템 정보를 노출하므로 운영 환경에서 제거

2. **`management.endpoints.web.base-path`**
    - 모든 Actuator 엔드포인트의 기본 경로를 설정합니다.
    - 기본값: `/actuator`
    - 접근 URL: `http://your-domain/actuator/health`

3. **`management.endpoint.health.show-details`** ⚠️ **보안 매우 중요**
    - 헬스체크 응답에 상세 정보를 포함할지 결정합니다.
    - **운영 환경 필수 설정**: `never`

   **옵션별 차이:**
    - `never`: 상태 코드만 반환 `{"status":"UP"}` - **운영 환경 권장**
    - `when-authorized`: Spring Security 인증된 사용자에게만 상세 정보 표시
    - `always`: 항상 상세 정보 표시 (DB 정보, Redis 정보 등 노출) - **절대 금지**

   **⚠️ 보안 위험 - 외부 접근 가능**
    - 이 프로젝트는 ALB를 통해 외부에서 `/actuator/health` 접근 가능
    - 도메인: `https://api.deepple.com/actuator/health`로 누구나 접근 가능
    - `show-details: always` 또는 `when-authorized` 사용 시 노출되는 정보:
      ```json
      {
        "status": "UP",
        "components": {
          "db": {
            "status": "UP",
            "details": {
              "database": "MySQL",
              "validationQuery": "isValid()",
              "result": 1
            }
          },
          "redis": {
            "status": "UP",
            "details": {
              "version": "7.0.0"
            }
          }
        }
      }
      ```
    - 공격자가 얻을 수 있는 정보: DB 종류, 버전, Redis 버전, 연결 상태 등

   **`never` 설정 시 응답:**
   ```json
   {
     "status": "UP"
   }
   ```
    - ALB 헬스체크는 정상 작동 (status만 확인)
    - 외부에서 접근해도 민감한 정보 노출 없음

4. **`management.server.port`**
    - Actuator 엔드포인트를 제공할 포트를 지정합니다.
    - 애플리케이션 포트(8080)와 동일하게 설정하여 ALB가 동일 포트로 헬스체크를 수행할 수 있도록 합니다.
    - 별도 포트를 사용하려면 보안 그룹에서 해당 포트를 추가로 열어야 합니다.

#### 환경별 권장 설정

**개발 환경** (`application-dev.yml`):

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics  # 디버깅 편의를 위해 모두 노출
  endpoint:
    health:
      show-details: always  # 개발 환경에서는 상세 정보 확인
```

**운영 환경** (`application-prod.yml`):

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health  # health만 노출
  endpoint:
    health:
      show-details: never  # 상세 정보 완전 차단
```

#### 엔드포인트 확인

애플리케이션 실행 후 다음 URL로 접근 가능:

```bash
# 헬스체크 (ALB가 사용, 외부에서도 접근 가능)
GET http://localhost:8080/actuator/health
GET https://api.deepple.com/actuator/health

# 응답 예시 (show-details: never)
{
  "status": "UP"
}

# 존재하지 않는 엔드포인트 (보안상 제거됨)
GET http://localhost:8080/actuator/info    # 404 Not Found
GET http://localhost:8080/actuator/metrics # 404 Not Found
```

---

## 9. 도메인 및 SSL 인증서

### 9.1 도메인 구매 방법 선택

도메인을 구매하고 AWS와 연결하는 방법은 크게 2가지가 있습니다.

#### 방법 비교

| 항목         | Route 53에서 구매 | 외부 업체에서 구매 (가비아 등)    |
|------------|---------------|-----------------------|
| **편의성**    | ✅ AWS 통합 관리   | ⚠️ 2곳에서 관리 필요         |
| **비용**     | 보통 ($12-13/년) | 저렴 (₩10,000-15,000/년) |
| **설정 난이도** | ✅ 쉬움 (자동 연동)  | ⚠️ 네임서버 수동 변경 필요      |
| **갱신**     | 자동 갱신         | 수동 또는 자동              |
| **한국어 지원** | ❌ 영어만         | ✅ 한국어                 |
| **결제**     | 카드/AWS 계정     | 카드/계좌이체               |

**💡 권장:**

- **AWS 초보자 또는 통합 관리 선호**: Route 53에서 구매
- **비용 절감 우선**: 가비아 등 외부 업체 구매

---

### 9.2 방법 1: Route 53에서 도메인 직접 구매 (권장)

#### 9.2.1 도메인 검색 및 구매

**1단계: Route 53 콘솔 접속**

```
1. AWS 콘솔 → Route 53 검색
2. 좌측 메뉴 → "등록된 도메인" 클릭
3. "도메인 등록" 버튼 클릭
```

**2단계: 도메인 검색**

```
도메인 이름 입력:
  deepple.com

"확인" 클릭

결과:
  ✓ deepple.com - 사용 가능 ($12/년)
  또는
  ✗ deepple.com - 이미 등록됨

사용 가능하면 "장바구니에 추가" 클릭
```

💡 **Route 53에서 구매 가능한 도메인:**

- `.com`: 가장 신뢰받는 도메인 ($12/년)
- `.net`: 대안 ($11/년)
- `.io`: 개발자들이 선호 ($39/년, 비쌈)
- `.org`, `.info`, `.biz` 등

❌ **Route 53에서 구매 불가능한 도메인:**

- `.co.kr`: 한국 도메인 (KISA 관리, 가비아/후이즈 등에서만 구매 가능)
- → `.co.kr` 필요 시 반드시 **방법 2** 사용 (외부 업체 구매 + Route 53 연결)

**3단계: 연락처 정보 입력**

```
도메인 연락처 정보:
  - 등록자 연락처 (필수)
  - 관리자 연락처
  - 기술 담당자 연락처

개인정보 보호:
  ✓ 개인정보 보호 활성화 (권장, 무료)
  → WHOIS 조회 시 개인 정보 숨김
```

**4단계: 자동 갱신 설정**

```
자동 갱신:
  ✓ 자동 갱신 활성화 (권장)

→ 도메인 만료 방지
```

**5단계: 구매 완료**

```
1. 약관 동의 체크
2. "도메인 등록 완료" 클릭
3. 이메일로 확인 메일 수신
4. 이메일 내 링크 클릭하여 도메인 소유권 확인 (15일 내)

처리 시간: 최대 3일 (보통 몇 시간)
```

**💡 중요: 이메일 확인 필수!**

- 15일 내 이메일 확인 안 하면 도메인 정지됨
- 스팸 메일함도 확인하세요

#### 9.2.2 호스팅 영역 자동 생성 확인

Route 53에서 도메인을 구매하면 **호스팅 영역이 자동으로 생성**됩니다.

```
Route 53 → 호스팅 영역 → deepple.com 확인

자동 생성된 레코드:
  - NS (네임서버): 4개
  - SOA (도메인 권한): 1개
```

**→ 9.4 단계로 이동하여 서브도메인 레코드 추가**

---

### 9.3 방법 2: 외부 업체 도메인을 Route 53에 연결

#### 💡 이 방법이 필요한 경우

1. **이미 가비아/후이즈 등에서 도메인을 구매한 경우**
    - `deepple.co.kr`, `deepple.com` 등을 이미 보유
    - 네임서버만 Route 53으로 변경하면 됨
    - → **9.3.2 단계로 바로 이동**

2. **`.co.kr` 도메인이 필요한 경우**
    - Route 53에서 구매 불가능
    - 가비아 등에서 구매 필수
    - → **9.3.1 단계부터 시작**

---

#### 9.3.1 가비아에서 도메인 구매 (이미 구매했다면 건너뛰기)

**💡 이미 도메인을 보유하고 있다면 이 단계를 건너뛰고 9.3.2로 이동하세요.**

**1단계: 가비아 접속**

```
1. https://www.gabia.com 접속
2. 도메인 검색창에 "deepple.co.kr" 또는 "deepple.com" 입력
3. 검색 클릭
```

**2단계: 도메인 구매**

```
사용 가능 시:
  1. "도메인 등록" 클릭
  2. 1년 등록 선택
     - .com: ₩15,000 정도
     - .co.kr: ₩20,000 정도 (사업자등록증 필요할 수 있음)
  3. 장바구니 → 결제
  4. 회원가입 및 로그인
  5. 결제 완료

처리 시간: 즉시 (.co.kr은 승인까지 1-2일 소요 가능)
```

**3단계: 도메인 관리 페이지 접속**

```
가비아 → My가비아 → 서비스 관리 → 도메인 → 구매한 도메인 확인
```

#### 9.3.2 Route 53 호스팅 영역 생성

**1단계: 호스팅 영역 생성**

```
AWS 콘솔 → Route 53 → 호스팅 영역 → "호스팅 영역 생성"

도메인 이름: deepple.co.kr (또는 deepple.com 등 구매한 도메인)
유형: ○ 퍼블릭 호스팅 영역
태그 (선택):
  Name = deepple.co.kr
  Environment = production

"호스팅 영역 생성" 클릭
```

**2단계: 네임서버 확인**

호스팅 영역 생성 후 NS 레코드에 4개의 네임서버가 표시됩니다:

```
NS 레코드:
  ns-1234.awsdns-12.org
  ns-5678.awsdns-56.co.uk
  ns-9012.awsdns-90.com
  ns-3456.awsdns-34.net
```

**💡 이 4개의 네임서버를 메모하세요!**

#### 9.3.3 가비아에서 네임서버 변경

**1단계: 가비아 도메인 관리**

```
My가비아 → 도메인 → deepple.com 선택 → "관리" 클릭
```

**2단계: 네임서버 설정**

```
1. "네임서버 설정" 탭 클릭
2. "네임서버 변경" 버튼 클릭
3. "호스팅 네임서버" 선택
4. 아래 4개 입력:

   1차: ns-1234.awsdns-12.org
   2차: ns-5678.awsdns-56.co.uk
   3차: ns-9012.awsdns-90.com
   4차: ns-3456.awsdns-34.net

5. "적용" 클릭
```

**3단계: DNS 전파 대기**

```
변경 완료 시간: 최대 48시간 (보통 1-2시간)

확인 방법:
nslookup deepple.com

또는

dig deepple.com NS
```

**💡 주의사항:**

- 네임서버 변경 후 기존 가비아 DNS 설정은 모두 무효화됨
- 이후 모든 DNS 설정은 Route 53에서만 관리

---

### 9.4 Route 53 서브도메인 레코드 생성

**💡 중요**: 이미 9.3 단계에서 호스팅 영역을 생성했다면 이 단계는 건너뛰고 2단계(레코드 생성)로 바로 이동하세요.

1. **호스팅 영역 확인 또는 생성**

    ```
    - 도메인 이름: deepple.co.kr
    - 유형: 퍼블릭 호스팅 영역

    💡 이미 9.3.2 단계에서 호스팅 영역을 생성했다면 이 단계는 건너뛰세요.
    ```

2. **레코드 생성**

    ```
    Route 53 → 호스팅 영역 → deepple.co.kr 선택 → "레코드 생성" 클릭

    레코드 1 (운영):
    - 레코드 이름: api
    - 레코드 유형: A
    - 별칭: 예
    - 트래픽 라우팅 대상:
      * Application/Classic Load Balancer에 대한 별칭
      * 아시아 태평양(서울) ap-northeast-2
      * deepple-prod-alb 선택
    - 라우팅 정책: 단순 라우팅 (Simple routing)
    - 대상 상태 평가: 예 (활성화)
    - "레코드 생성" 클릭

    💡 설정 설명:
    - 단순 라우팅: 단일 ALB로 트래픽을 보내는 가장 일반적인 방식
    - 대상 상태 평가: ALB가 unhealthy 상태면 트래픽 차단 (무료, 권장)

    결과: api.deepple.co.kr → ALB로 연결

    레코드 2 (개발, 선택사항):

    **옵션 A: ALB 사용하는 경우**
    - 레코드 이름: dev-api
    - 레코드 유형: A
    - 별칭: 예
    - 트래픽 라우팅 대상: ALB (deepple-dev-alb)
    - 라우팅 정책: 단순 라우팅
    - 대상 상태 평가: 예

    결과: dev-api.deepple.co.kr → ALB → EC2

    **옵션 B: EC2 직접 연결 (ALB 없음)**
    - 레코드 이름: dev-api
    - 레코드 유형: A
    - 별칭: 아니요
    - 값: EC2 퍼블릭 IP 주소 입력 (예: 3.35.123.45)
    - TTL: 300 (5분)
    - 라우팅 정책: 단순 라우팅

    결과: dev-api.deepple.co.kr → EC2 직접 연결

    💡 **EC2 퍼블릭 IP 확인 방법**:
    ```
   EC2 콘솔 → 인스턴스 → 개발 서버 선택 → 퍼블릭 IPv4 주소 복사
    ```

    💡 **옵션 선택 가이드**:
    - ALB 사용: HTTPS, 헬스체크, 오토스케일링 필요 시
    - EC2 직접: 개발/테스트 환경, 비용 절감, 간단한 구성
    ```

3. **네임서버 설정 확인**

    ```
    💡 9.3.3 단계에서 이미 완료했다면 건너뛰세요.

    - Route 53의 NS 레코드 확인
    - 도메인 등록 업체(가비아)에서 네임서버를 Route 53으로 변경
    - DNS 전파 대기 (1-2시간)
    ```

### 9.5 여러 도메인을 하나의 서버에 연결하기

**개념**: 하나의 EC2 인스턴스에 여러 도메인/서브도메인을 연결할 수 있습니다. 개발 서버에 여러 도메인을 설정하거나, 기존 도메인을 유지하면서 새 도메인을 추가할 때 유용합니다.

#### 9.5.1 Route 53 별칭(Alias) vs 일반 A 레코드

Route 53 레코드 생성 시 "별칭" 옵션을 선택해야 합니다. 두 방식의 차이를 이해하고 올바르게 선택하세요.

**별칭(Alias) vs 일반 A 레코드 비교**

| 구분        | 별칭: 예 (Alias)               | 별칭: 아니요 (일반 A 레코드)        |
|-----------|-----------------------------|---------------------------|
| **대상**    | AWS 리소스 (ALB, CloudFront 등) | IP 주소 직접 입력               |
| **값 입력**  | 리소스 선택 (드롭다운)               | IP 주소 입력 (예: 3.35.123.45) |
| **비용**    | ✅ 무료 (쿼리 비용 없음)             | 💰 쿼리 비용 ($0.40/100만)     |
| **IP 변경** | ✅ 자동 추적 (AWS가 관리)           | ❌ 수동 업데이트 필요              |
| **TTL**   | AWS가 자동 관리                  | 수동 설정 (예: 300초)           |
| **헬스체크**  | AWS 리소스 헬스체크 통합             | 별도 설정 필요                  |

**언제 사용하나요?**

**별칭: 예 (권장)**

```
사용 대상:
✅ ALB (Application Load Balancer)
✅ CloudFront 배포
✅ S3 website endpoint
✅ API Gateway
✅ Elastic Beanstalk 환경

예시:
- 레코드 이름: api
- 별칭: 예
- 대상: deepple-prod-alb (ALB 선택)

장점:
- ALB IP가 변경되어도 자동 추적
- 쿼리 비용 무료
- 대상 상태 평가 가능
```

**별칭: 아니요 (IP 직접 입력)**

```
사용 대상:
❌ EC2 퍼블릭 IP 주소
❌ 외부 서버 IP 주소
❌ 온프레미스 서버

예시:
- 레코드 이름: dev-api
- 별칭: 아니요
- 값: 3.35.123.45 (EC2 IP)
- TTL: 300

주의:
- EC2 IP 변경 시 수동 업데이트 필요
- 쿼리 비용 발생 (미미함)
```

**❓ EC2는 왜 별칭(Alias)를 쓸 수 없나요?**

Route 53 Alias는 **특정 AWS 리소스만 지원**합니다:

- ✅ ALB, NLB (로드밸런서)
- ✅ CloudFront 배포
- ✅ S3 website endpoint
- ✅ API Gateway
- ❌ **EC2 인스턴스 (지원 안 됨)**

**EC2를 Route 53에 연결하는 방법:**

| 방법                     | 레코드 타입          | 대상                                  | 권장도      | 비고                                |
|------------------------|-----------------|-------------------------------------|----------|-----------------------------------|
| **Elastic IP + A 레코드** | A 레코드 (별칭: 아니요) | Elastic IP (예: 3.35.123.45)         | ✅ **권장** | 고정 IP, EC2 재시작해도 불변               |
| EC2 DNS + CNAME        | CNAME           | EC2 퍼블릭 DNS (예: ec2-3-35-123-45...) | ⚠️ 비권장   | EC2 재시작 시 DNS 변경 가능, 루트 도메인 사용 불가 |
| 퍼블릭 IP + A 레코드         | A 레코드 (별칭: 아니요) | EC2 퍼블릭 IP                          | ❌ 비권장    | EC2 중지/시작 시 IP 변경됨                |

**💡 Elastic IP를 사용 중이라면:**

- **현재 방법이 맞습니다!** (별칭: 아니요, 값: Elastic IP)
- EC2를 재시작해도 IP가 유지됩니다
- 가장 안정적이고 권장되는 방법입니다

**예시**:

```
Route 53 A 레코드 설정:
- 레코드 이름: dev-api
- 레코드 유형: A
- 별칭: 아니요
- 값: 3.35.123.45 (Elastic IP)
- TTL: 300 (5분)
```

**❌ EC2 퍼블릭 DNS 이름을 사용하지 않는 이유:**

```
EC2 퍼블릭 DNS: ec2-3-35-123-45.ap-northeast-2.compute.amazonaws.com

문제점:
1. EC2를 중지했다가 시작하면 DNS 이름이 변경됨 (Elastic IP 없으면)
2. Route 53 Alias 대상이 아님 (CNAME으로만 사용 가능)
3. CNAME은 루트 도메인(deepple.co.kr)에 사용 불가, 서브도메인만 가능
4. Elastic IP보다 복잡하고 불안정

→ Elastic IP를 할당받아 A 레코드로 연결하는 것이 훨씬 간단하고 안정적!
```

---

#### 9.5.2 하나의 서버에 여러 도메인 연결하기

**시나리오**: 개발 서버에 기존 `dev-api.deepple.co.kr`와 새로운 `dev.deepple.co.kr`을 동시에 연결하고 싶은 경우

**구조**:

```
Route 53 호스팅 영역 (deepple.co.kr):

레코드 1: dev-api.deepple.co.kr → 3.35.123.45 (개발 EC2)
레코드 2: dev.deepple.co.kr     → 3.35.123.45 (같은 개발 EC2)
레코드 3: test-api.deepple.co.kr → 3.35.123.45 (같은 개발 EC2)
```

**결과**:

- 모든 도메인이 같은 Spring Boot 애플리케이션으로 연결됨
- 어느 도메인으로 접속해도 동일한 서비스 제공

**1단계: EC2 퍼블릭 IP 확인**

```bash
# AWS 콘솔에서
EC2 콘솔 → 인스턴스 → 개발 서버 선택 → 퍼블릭 IPv4 주소 복사

예시: 3.35.123.45
```

**2단계: Route 53 A 레코드 추가 (기존 도메인)**

```
Route 53 → 호스팅 영역 → deepple.co.kr → "레코드 생성" 클릭

레코드 1 (기존):
- 레코드 이름: dev-api
- 레코드 유형: A
- 별칭: 아니요
- 값: 3.35.123.45 (EC2 IP)
- TTL: 300 (5분)
- 라우팅 정책: 단순 라우팅

"레코드 생성" 클릭
```

**3단계: Route 53 A 레코드 추가 (새 도메인)**

```
"레코드 생성" 다시 클릭

레코드 2 (새로 추가):
- 레코드 이름: dev
- 레코드 유형: A
- 별칭: 아니요
- 값: 3.35.123.45 (같은 EC2 IP)
- TTL: 300 (5분)
- 라우팅 정책: 단순 라우팅

"레코드 생성" 클릭
```

**4단계: 추가 도메인 계속 생성 (필요시)**

```
레코드 3 (선택사항):
- 레코드 이름: test-api
- 레코드 유형: A
- 별칭: 아니요
- 값: 3.35.123.45 (같은 EC2 IP)
- TTL: 300

💡 원하는 만큼 추가 가능!
```

**5단계: Spring Boot 설정 (변경 불필요)**

Spring Boot는 기본적으로 모든 Host 헤더를 받아들이므로 **추가 설정 불필요**합니다.

만약 특정 도메인만 허용하고 싶다면 `application-prod.yml`에 추가:

```yaml
server:
  port: 8080

# 특정 도메인만 허용하려면 (선택사항, 보안 강화)
spring:
  web:
    cors:
      allowed-origins:
        - https://dev-api.deepple.co.kr
        - https://dev.deepple.co.kr
        - https://test-api.deepple.co.kr
```

**6단계: 보안 그룹 확인**

개발 서버가 인터넷에서 접근 가능하도록 보안 그룹 설정:

```
deepple-dev-app-sg (개발 서버 보안 그룹):

인바운드 규칙:
- 유형: HTTP, 포트: 80, 소스: 0.0.0.0/0
- 유형: Custom TCP, 포트: 8080, 소스: 0.0.0.0/0
- 유형: SSH, 포트: 22, 소스: 관리자 IP만 (보안!)

💡 또는 사무실/집 IP만 허용하여 보안 강화:
- 소스: 123.456.789.0/32 (특정 IP 또는 IP 대역)
```

**7단계: DNS 전파 확인**

DNS 전파까지 5-10분 소요:

```bash
# DNS 조회 확인
nslookup dev-api.deepple.co.kr
# 예상 결과: 3.35.123.45

nslookup dev.deepple.co.kr
# 예상 결과: 3.35.123.45 (같은 IP)

nslookup test-api.deepple.co.kr
# 예상 결과: 3.35.123.45 (같은 IP)

# 또는 dig 사용
dig dev-api.deepple.co.kr
dig dev.deepple.co.kr
```

**8단계: 접속 테스트**

```bash
# 각 도메인으로 접속 테스트
curl http://dev-api.deepple.co.kr:8080/actuator/health
curl http://dev.deepple.co.kr:8080/actuator/health
curl http://test-api.deepple.co.kr:8080/actuator/health

# 모두 동일한 응답:
{"status":"UP"}
```

#### 9.5.3 비용

**추가 비용 없음!**

```
비용 구성:
- Route 53 호스팅 영역: $0.50/월 (호스팅 영역당, 1개만 필요)
- A 레코드 추가: 처음 25개 무료
- 쿼리 비용: $0.40/100만 쿼리 (개발 환경은 미미함)
- EC2: 기존과 동일 (추가 비용 없음)

예상 비용:
- 개발 환경 3개 도메인: $0.50/월 (호스팅 영역만)
- 쿼리 비용: ~$0.01/월 (개발 트래픽 기준)
```

#### 9.5.4 실제 사용 예시

**시나리오 1: 팀 내부용 + 외부 공유용**

```
dev-api.deepple.co.kr → 개발 서버 (팀 내부 사용)
demo.deepple.co.kr    → 개발 서버 (클라이언트 데모용)
```

**시나리오 2: 환경별 도메인**

```
dev.deepple.co.kr     → 개발 서버
staging.deepple.co.kr → 개발 서버 (QA 테스트용)
```

**시나리오 3: 기능별 도메인**

```
api-v1.deepple.co.kr  → 개발 서버 (API v1)
api-v2.deepple.co.kr  → 개발 서버 (API v2 테스트)
```

#### 9.5.5 주의사항

1. **EC2 IP 변경 시**
   ```
   EC2를 중지했다가 재시작하면 퍼블릭 IP가 변경됩니다!

   해결 방법:
   1. Elastic IP 할당 (권장, $3.6/월)
      - EC2 콘솔 → Elastic IP → 할당 → EC2에 연결
      - IP가 고정되어 Route 53 업데이트 불필요

   2. IP 변경 시마다 Route 53 A 레코드 업데이트
      - 모든 A 레코드의 값을 새 IP로 변경
   ```

2. **HTTPS 설정**
   ```
   EC2 직접 연결 시 HTTPS 설정 방법:

   옵션 1: Nginx 리버스 프록시 + Let's Encrypt
   옵션 2: ALB 추가 (HTTPS 자동 처리, 비용 $20/월)
   옵션 3: 개발 환경은 HTTP만 사용 (간단)
   ```

3. **보안**
   ```
   개발 서버를 인터넷에 노출할 때 주의:
   - SSH는 반드시 특정 IP만 허용
   - 민감한 데이터는 운영 DB와 분리
   - 개발 환경 .env 파일 사용
   ```

---

### 9.6 SSL 인증서 발급 (ACM)

**개념**: AWS Certificate Manager는 무료로 SSL/TLS 인증서를 발급하고 자동 갱신합니다.

1. **인증서 요청**

    ```
    AWS 콘솔 → Certificate Manager → "인증서 요청" 클릭

    - 리전: ap-northeast-2 (ALB와 동일 리전!)
    - 인증서 유형: 퍼블릭 인증서 요청
    - 도메인 이름:
      * api.deepple.co.kr (필수)
      * dev-api.deepple.co.kr (개발 환경 사용 시)
      * *.deepple.co.kr (와일드카드, 선택사항 - 모든 서브도메인 커버)
    - 검증 방법: DNS 검증 (권장)
    - "요청" 클릭
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