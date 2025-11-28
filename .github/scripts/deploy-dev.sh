#!/bin/bash
set -euo pipefail

echo "==================================================="
echo "Starting Development Deployment"
echo "==================================================="

# 인자로 환경변수 받기
ENV_CONTENT_B64="${1}"
AWS_REGION="${2}"
ECR_REGISTRY="${3}"
ECR_REPOSITORY="${4}"
IMAGE_TAG="${5}"
CONTAINER_NAME="${6}"
BLUE_PORT="${7}"
GREEN_PORT="${8}"
HEALTH_CHECK_MAX_RETRIES="${9}"
HEALTH_CHECK_INTERVAL="${10}"

IMAGE="${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
NGINX_SITE="/etc/nginx/sites-available/dev-api.deepple.co.kr"

echo ""
echo "Configuration:"
echo "  - Image: ${IMAGE}"
echo "  - Container: ${CONTAINER_NAME}"
echo "  - Blue Port: ${BLUE_PORT}"
echo "  - Green Port: ${GREEN_PORT}"

# ===========================================
# Step 0: Update .env file
# ===========================================
echo ""
echo "Step 0: Updating .env file..."
echo "${ENV_CONTENT_B64}" | base64 -d > /home/ubuntu/.env
echo "Environment variables updated"

# ===========================================
# Step 1: ECR Login
# ===========================================
echo ""
echo "Step 1: Logging in to Amazon ECR..."
aws ecr get-login-password --region "${AWS_REGION}" | \
  docker login --username AWS --password-stdin "${ECR_REGISTRY}"
echo "ECR login successful"

# ===========================================
# Step 2: Pull Docker Image
# ===========================================
echo ""
echo "Step 2: Pulling Docker image..."
docker pull "${IMAGE}"
echo "Image pulled successfully"

# ===========================================
# Step 3: Determine Deployment Slots
# ===========================================
echo ""
echo "Step 3: Determining deployment slots..."

if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}-blue$"; then
  CURRENT_SLOT="blue"
  CURRENT_PORT="${BLUE_PORT}"
  TARGET_SLOT="green"
  TARGET_PORT="${GREEN_PORT}"
else
  CURRENT_SLOT="green"
  CURRENT_PORT="${GREEN_PORT}"
  TARGET_SLOT="blue"
  TARGET_PORT="${BLUE_PORT}"
fi

echo "  Current: ${CURRENT_SLOT} (port ${CURRENT_PORT})"
echo "  Target:  ${TARGET_SLOT} (port ${TARGET_PORT})"

# ===========================================
# Step 4: Start New Container
# ===========================================
echo ""
echo "Step 4: Starting new container (${TARGET_SLOT})..."

# 기존 타겟 컨테이너 제거
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}-${TARGET_SLOT}$"; then
  echo "  Removing old ${TARGET_SLOT} container..."
  docker stop "${CONTAINER_NAME}-${TARGET_SLOT}" 2>/dev/null || true
  docker rm "${CONTAINER_NAME}-${TARGET_SLOT}" 2>/dev/null || true
fi

docker run -d \
  --name "${CONTAINER_NAME}-${TARGET_SLOT}" \
  -p "${TARGET_PORT}:8080" \
  -v /home/ubuntu/secrets:/etc/credentials:ro \
  -v /home/ubuntu/certs:/etc/certs:ro \
  --env-file /home/ubuntu/.env \
  --restart unless-stopped \
  "${IMAGE}"

echo "${TARGET_SLOT} container started on port ${TARGET_PORT}"

# ===========================================
# Step 5: Health Check
# ===========================================
echo ""
echo "Step 5: Health checking ${TARGET_SLOT} container..."

RETRY_COUNT=0
HEALTH_OK=false

while [ "${RETRY_COUNT}" -lt "${HEALTH_CHECK_MAX_RETRIES}" ]; do
  RETRY_COUNT=$((RETRY_COUNT + 1))

  if curl -sf "http://localhost:${TARGET_PORT}/actuator/health" > /dev/null 2>&1; then
    echo "Health check passed (attempt ${RETRY_COUNT})"
    HEALTH_OK=true
    break
  fi

  if [ $((RETRY_COUNT % 10)) -eq 0 ]; then
    echo "  Health check attempt ${RETRY_COUNT}/${HEALTH_CHECK_MAX_RETRIES}..."
  fi

  sleep "${HEALTH_CHECK_INTERVAL}"
done

if [ "${HEALTH_OK}" = false ]; then
  echo ""
  echo "ERROR: Health check failed after ${HEALTH_CHECK_MAX_RETRIES} attempts"
  echo ""
  echo "Container logs (last 100 lines):"
  docker logs "${CONTAINER_NAME}-${TARGET_SLOT}" --tail 100
  echo ""
  echo "Cleaning up failed container..."
  docker stop "${CONTAINER_NAME}-${TARGET_SLOT}" 2>/dev/null || true
  docker rm "${CONTAINER_NAME}-${TARGET_SLOT}" 2>/dev/null || true
  echo "Current ${CURRENT_SLOT} container remains running - ZERO downtime maintained"
  exit 1
fi

# ===========================================
# Step 6: Switch Nginx
# ===========================================
echo ""
echo "Step 6: Switching nginx proxy_pass..."

echo "  Before: $(grep 'proxy_pass' ${NGINX_SITE} | head -1 | xargs)"

sudo sed -i "s|proxy_pass http://127.0.0.1:[0-9]\+;|proxy_pass http://127.0.0.1:${TARGET_PORT};|g" "${NGINX_SITE}"

echo "  After:  $(grep 'proxy_pass' ${NGINX_SITE} | head -1 | xargs)"

# Nginx 설정 테스트 및 리로드
if sudo nginx -t 2>&1; then
  sudo nginx -s reload
  echo "Nginx reloaded - traffic switched to ${TARGET_SLOT}"
else
  echo "ERROR: Nginx configuration test failed"
  exit 1
fi

# 트래픽 확인
sleep 2
if curl -sf "http://localhost/actuator/health" > /dev/null 2>&1; then
  echo "Traffic is flowing through nginx"
else
  echo "Warning: Local health check failed, but deployment may still be successful"
fi

# ===========================================
# Step 7: Cleanup Old Container
# ===========================================
echo ""
echo "Step 7: Cleaning up old ${CURRENT_SLOT} container..."

if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}-${CURRENT_SLOT}$"; then
  docker stop "${CONTAINER_NAME}-${CURRENT_SLOT}" 2>/dev/null || true
  docker rm "${CONTAINER_NAME}-${CURRENT_SLOT}" 2>/dev/null || true
  echo "Old ${CURRENT_SLOT} container removed"
else
  echo "  No previous container found (first deployment)"
fi

# ===========================================
# Step 8: Cleanup Old Images
# ===========================================
echo ""
echo "Step 8: Cleaning up old images..."
docker image prune -af --filter 'until=24h' 2>/dev/null || true
echo "Cleanup completed"

# ===========================================
# Done
# ===========================================
echo ""
echo "==================================================="
echo "Development Deployment Completed Successfully!"
echo "==================================================="
echo ""
echo "Summary:"
echo "  - Image: ${IMAGE}"
echo "  - Active: ${TARGET_SLOT} (port ${TARGET_PORT})"
echo "  - Endpoint: https://dev-api.deepple.co.kr"
echo "==================================================="