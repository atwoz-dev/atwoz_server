#!/bin/bash

################################################################################
# ATWOZ 배포 스크립트
#
# 이 스크립트는 Docker 컨테이너를 사용하여 Spring Boot 애플리케이션을 배포합니다.
# 헬스 체크, 자동 롤백, 로깅 기능을 포함합니다.
#
# 사용법: sudo ./deploy_script.sh
################################################################################

set -e  # 에러 발생 시 즉시 종료

# 설정
DOCKER_IMAGE="ggongtae/atwoz"
IMAGE_TAG="latest"
CONTAINER_NAME="spring-app"
ENV_FILE="/home/ec2-user/.env"
LOG_FILE="/home/ec2-user/deploy.log"
HEALTH_CHECK_URL="http://localhost:8080/actuator/health"
MAX_HEALTH_CHECK_ATTEMPTS=30  # 최대 5분 대기 (30 * 10초)
HEALTH_CHECK_INTERVAL=10      # 헬스 체크 간격 (초)

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

################################################################################
# 함수: 로그 출력
################################################################################
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

log_success() {
    echo -e "${GREEN}$(date '+%Y-%m-%d %H:%M:%S') - ✓ $1${NC}" | tee -a "$LOG_FILE"
}

log_error() {
    echo -e "${RED}$(date '+%Y-%m-%d %H:%M:%S') - ✗ $1${NC}" | tee -a "$LOG_FILE"
}

log_warning() {
    echo -e "${YELLOW}$(date '+%Y-%m-%d %H:%M:%S') - ⚠ $1${NC}" | tee -a "$LOG_FILE"
}

################################################################################
# 함수: 환경 변수 파일 확인
################################################################################
check_env_file() {
    if [ ! -f "$ENV_FILE" ]; then
        log_error "환경 변수 파일이 없습니다: $ENV_FILE"
        exit 1
    fi
    log_success "환경 변수 파일 확인 완료"
}

################################################################################
# 함수: 필수 파일 확인 (ATWOZ 특화)
################################################################################
check_required_files() {
    log "필수 파일 확인 중..."

    # Firebase 인증서 확인
    if [ ! -f "/home/ec2-user/secrets/firebase-adminsdk.json" ]; then
        log_error "Firebase 인증서 파일이 없습니다: /home/ec2-user/secrets/firebase-adminsdk.json"
        exit 1
    fi

    # App Store 인증서 확인
    if [ ! -f "/home/ec2-user/certs/appstore/AppleRootCA-G2.pem" ]; then
        log_error "App Store 인증서 파일이 없습니다: /home/ec2-user/certs/appstore/AppleRootCA-G2.pem"
        exit 1
    fi

    if [ ! -f "/home/ec2-user/certs/appstore/AppleRootCA-G3.pem" ]; then
        log_error "App Store 인증서 파일이 없습니다: /home/ec2-user/certs/appstore/AppleRootCA-G3.pem"
        exit 1
    fi

    log_success "필수 파일 확인 완료"
}

################################################################################
# 함수: 백업 이미지 생성
################################################################################
create_backup() {
    if docker ps -q -f name="$CONTAINER_NAME" > /dev/null 2>&1; then
        log "기존 컨테이너 백업 중..."
        docker commit "$CONTAINER_NAME" "${CONTAINER_NAME}-backup" 2>/dev/null || true
        log_success "백업 이미지 생성 완료: ${CONTAINER_NAME}-backup"
    else
        log_warning "실행 중인 컨테이너가 없어 백업을 건너뜁니다"
    fi
}

################################################################################
# 함수: 기존 컨테이너 중지 및 제거
################################################################################
stop_old_container() {
    log "기존 컨테이너 중지 및 제거 중..."

    if docker ps -q -f name="$CONTAINER_NAME" > /dev/null 2>&1; then
        docker stop "$CONTAINER_NAME" || true
        log "컨테이너 중지 완료"
    fi

    if docker ps -aq -f name="$CONTAINER_NAME" > /dev/null 2>&1; then
        docker rm "$CONTAINER_NAME" || true
        log "컨테이너 제거 완료"
    fi
}

################################################################################
# 함수: 최신 이미지 Pull
################################################################################
pull_latest_image() {
    log "최신 Docker 이미지 다운로드 중..."
    if docker pull "${DOCKER_IMAGE}:${IMAGE_TAG}"; then
        log_success "이미지 다운로드 완료: ${DOCKER_IMAGE}:${IMAGE_TAG}"
    else
        log_error "이미지 다운로드 실패"
        exit 1
    fi
}

################################################################################
# 함수: 새 컨테이너 실행
################################################################################
start_new_container() {
    log "새 컨테이너 시작 중..."

    docker run -d \
      --name "$CONTAINER_NAME" \
      --env-file "$ENV_FILE" \
      -p 8080:8080 \
      -v /home/ec2-user/secrets:/etc/credentials:ro \
      -v /home/ec2-user/certs:/etc/certs:ro \
      --log-driver=json-file \
      --log-opt max-size=10m \
      --log-opt max-file=3 \
      --restart unless-stopped \
      "${DOCKER_IMAGE}:${IMAGE_TAG}"

    if [ $? -eq 0 ]; then
        log_success "컨테이너 시작 완료"
    else
        log_error "컨테이너 시작 실패"
        exit 1
    fi
}

################################################################################
# 함수: 헬스 체크
################################################################################
health_check() {
    log "애플리케이션 시작 대기 중... (최대 ${MAX_HEALTH_CHECK_ATTEMPTS}회 시도)"

    local attempt=0

    while [ $attempt -lt $MAX_HEALTH_CHECK_ATTEMPTS ]; do
        attempt=$((attempt+1))
        log "헬스 체크 시도 $attempt/$MAX_HEALTH_CHECK_ATTEMPTS..."

        # HTTP 상태 코드 확인
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_CHECK_URL" 2>/dev/null || echo "000")

        if [ "$HTTP_CODE" = "200" ]; then
            log_success "헬스 체크 성공! 애플리케이션이 정상 작동 중입니다."
            return 0
        elif [ "$HTTP_CODE" = "000" ]; then
            log "연결 실패... 애플리케이션이 아직 시작되지 않았습니다."
        else
            log_warning "비정상 응답 코드: $HTTP_CODE"
        fi

        # 컨테이너가 실행 중인지 확인
        if ! docker ps | grep -q "$CONTAINER_NAME"; then
            log_error "컨테이너가 실행되지 않습니다!"
            docker logs --tail 50 "$CONTAINER_NAME" | tee -a "$LOG_FILE"
            return 1
        fi

        sleep $HEALTH_CHECK_INTERVAL
    done

    log_error "헬스 체크 실패: $MAX_HEALTH_CHECK_ATTEMPTS회 시도 후 타임아웃"
    return 1
}

################################################################################
# 함수: 컨테이너 로그 확인
################################################################################
check_container_logs() {
    log "컨테이너 로그 확인 중..."

    # 최근 로그에서 에러 개수 확인
    ERROR_COUNT=$(docker logs "$CONTAINER_NAME" 2>&1 | grep -i "ERROR" | wc -l)

    log "에러 로그 개수: $ERROR_COUNT"

    if [ "$ERROR_COUNT" -gt 10 ]; then
        log_warning "에러 로그가 많습니다 ($ERROR_COUNT개). 로그를 확인하세요."
        docker logs --tail 20 "$CONTAINER_NAME" 2>&1 | grep -i "ERROR" | tee -a "$LOG_FILE"
    fi
}

################################################################################
# 함수: 롤백
################################################################################
rollback() {
    log_error "배포 실패! 롤백 시작..."

    # 현재 컨테이너 중지 및 제거
    stop_old_container

    # 백업 이미지가 있는지 확인
    if docker images | grep -q "${CONTAINER_NAME}-backup"; then
        log "백업 이미지로 복원 중..."

        docker run -d \
          --name "$CONTAINER_NAME" \
          --env-file "$ENV_FILE" \
          -p 8080:8080 \
          -v /home/ec2-user/secrets:/etc/credentials:ro \
          -v /home/ec2-user/certs:/etc/certs:ro \
          --restart unless-stopped \
          "${CONTAINER_NAME}-backup"

        if [ $? -eq 0 ]; then
            log_success "롤백 완료: 이전 버전으로 복원되었습니다"

            # 롤백 후 헬스 체크
            sleep 10
            if curl -f "$HEALTH_CHECK_URL" > /dev/null 2>&1; then
                log_success "롤백된 애플리케이션이 정상 작동 중입니다"
            else
                log_error "롤백된 애플리케이션도 시작하지 못했습니다. 수동 복구가 필요합니다!"
            fi
        else
            log_error "롤백 실패! 수동 복구가 필요합니다."
        fi
    else
        log_error "백업 이미지가 없습니다. 수동 복구가 필요합니다!"
    fi
}

################################################################################
# 함수: 오래된 이미지 정리
################################################################################
cleanup_old_images() {
    log "오래된 Docker 이미지 정리 중..."

    # dangling 이미지 제거
    docker image prune -f

    # 백업 이미지 제거 (성공 시에만)
    if docker images | grep -q "${CONTAINER_NAME}-backup"; then
        docker rmi "${CONTAINER_NAME}-backup" 2>/dev/null || true
        log "백업 이미지 제거 완료"
    fi

    log_success "이미지 정리 완료"
}

################################################################################
# 메인 실행 흐름
################################################################################
main() {
    log "===== 배포 시작 $(date) ====="

    # 1. 사전 체크
    check_env_file
    check_required_files

    # 2. 백업 생성
    create_backup

    # 3. 기존 컨테이너 중지
    stop_old_container

    # 4. 최신 이미지 다운로드
    pull_latest_image

    # 5. 새 컨테이너 시작
    start_new_container

    # 6. 헬스 체크
    if health_check; then
        # 성공
        log_success "===== 배포 성공 $(date) ====="

        # 7. 로그 확인
        check_container_logs

        # 8. 정리
        cleanup_old_images

        log_success "배포가 완료되었습니다!"
        log "애플리케이션 URL: http://localhost:8080"
        log "헬스 체크 URL: $HEALTH_CHECK_URL"

        exit 0
    else
        # 실패 - 롤백
        log_error "===== 배포 실패 $(date) ====="

        # 컨테이너 로그 출력
        log "실패한 컨테이너 로그:"
        docker logs --tail 50 "$CONTAINER_NAME" | tee -a "$LOG_FILE"

        # 롤백 실행
        rollback

        exit 1
    fi
}

# 스크립트 실행
main