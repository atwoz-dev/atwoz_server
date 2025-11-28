#!/bin/bash
set -e

echo "==================================================="
echo "Starting Development Deployment"
echo "==================================================="

# Environment variables (will be injected by workflow)
# ENV_CONTENT_B64, AWS_REGION, ECR_REGISTRY, ECR_REPOSITORY, IMAGE_TAG
# CONTAINER_NAME, BLUE_PORT, GREEN_PORT, HEALTH_CHECK_MAX_RETRIES, HEALTH_CHECK_INTERVAL

echo
echo "Step 0: Updating .env file..."
echo "${ENV_CONTENT_B64}" | base64 -d > /home/ubuntu/.env
echo "Environment variables updated"

echo
echo "Step 1: Logging in to Amazon ECR..."
aws ecr get-login-password --region ${AWS_REGION} | \
  docker login --username AWS --password-stdin ${ECR_REGISTRY}
echo "ECR login successful"

echo
echo "Step 2: Pulling Docker image..."
echo "Image: ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
docker pull ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
echo "Image pulled successfully"

echo
echo "Step 3: Determining deployment slots..."

# Determine current and target slots
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}-blue$"; then
  CURRENT_SLOT=blue
  CURRENT_PORT=${BLUE_PORT}
  TARGET_SLOT=green
  TARGET_PORT=${GREEN_PORT}
  echo "Current: Blue (port ${BLUE_PORT}) -> Deploying to: Green (port ${GREEN_PORT})"
else
  CURRENT_SLOT=green
  CURRENT_PORT=${GREEN_PORT}
  TARGET_SLOT=blue
  TARGET_PORT=${BLUE_PORT}
  echo "Current: Green (port ${GREEN_PORT}) -> Deploying to: Blue (port ${BLUE_PORT})"
fi

echo
echo "Step 4: Starting new container (${TARGET_SLOT}) on port ${TARGET_PORT}..."

# Remove old target slot container if exists
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}-${TARGET_SLOT}$"; then
  docker stop ${CONTAINER_NAME}-${TARGET_SLOT} || true
  docker rm ${CONTAINER_NAME}-${TARGET_SLOT} || true
  echo "Old ${TARGET_SLOT} container removed"
fi

docker run -d \
  --name ${CONTAINER_NAME}-${TARGET_SLOT} \
  -p ${TARGET_PORT}:8080 \
  -v /home/ubuntu/secrets:/etc/credentials:ro \
  -v /home/ubuntu/certs:/etc/certs:ro \
  --env-file /home/ubuntu/.env \
  --restart unless-stopped \
  ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}

echo "${TARGET_SLOT} container started on port ${TARGET_PORT}"

echo
echo "Step 5: Health checking ${TARGET_SLOT} container..."
MAX_RETRIES=${HEALTH_CHECK_MAX_RETRIES}
RETRY_INTERVAL=${HEALTH_CHECK_INTERVAL}
RETRY_COUNT=0
HEALTH_OK=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
  echo "Health check attempt $((RETRY_COUNT + 1))/${MAX_RETRIES}"

  if curl -f -s http://localhost:${TARGET_PORT}/actuator/health > /dev/null 2>&1; then
    echo "${TARGET_SLOT} container is healthy!"
    HEALTH_OK=true
    break
  fi

  RETRY_COUNT=$((RETRY_COUNT + 1))
  if [ $RETRY_COUNT -lt $MAX_RETRIES ]; then
    sleep $RETRY_INTERVAL
  fi
done

if [ "$HEALTH_OK" = false ]; then
  echo
  echo "ERROR: ${TARGET_SLOT} container health check failed after ${MAX_RETRIES} attempts"
  echo "${TARGET_SLOT} container logs:"
  docker logs ${CONTAINER_NAME}-${TARGET_SLOT} --tail 100

  echo
  echo "Removing failed ${TARGET_SLOT} container..."
  docker stop ${CONTAINER_NAME}-${TARGET_SLOT}
  docker rm ${CONTAINER_NAME}-${TARGET_SLOT}
  echo "${CURRENT_SLOT} container remains running - ZERO downtime maintained"

  exit 1
fi

echo
echo "Step 6: Switching nginx proxy_pass..."

# Update nginx proxy_pass to point to new target
NGINX_SITE="/etc/nginx/sites-available/dev-api.deepple.co.kr"

echo "Before change:"
sudo grep "proxy_pass" $NGINX_SITE

# Replace proxy_pass port (8081 or 8082)
sudo sed -i "s|proxy_pass http://127.0.0.1:[0-9]\+;|proxy_pass http://127.0.0.1:${TARGET_PORT};|g" $NGINX_SITE

echo "After change:"
sudo grep "proxy_pass" $NGINX_SITE

# Test nginx configuration
if sudo nginx -t 2>&1; then
  echo "Nginx configuration is valid"
  sudo nginx -s reload
  echo "Nginx reloaded - traffic switched to ${TARGET_SLOT} on port ${TARGET_PORT}"
else
  echo "ERROR: Nginx configuration test failed"
  sudo cat $NGINX_SITE | grep -A 5 -B 5 proxy_pass
  exit 1
fi

echo "Verifying traffic through nginx..."
sleep 2
if curl -f https://dev-api.deepple.co.kr/actuator/health 2>&1; then
  echo "Traffic is flowing through nginx to new container!"
else
  echo "WARNING: HTTPS health check failed, trying HTTP..."
  if curl -f http://localhost/actuator/health 2>&1; then
    echo "HTTP health check passed"
  else
    echo "WARNING: Health check failed, but deployment completed"
  fi
fi

echo
echo "Step 7: Cleaning up old ${CURRENT_SLOT} container..."
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}-${CURRENT_SLOT}$"; then
  docker stop ${CONTAINER_NAME}-${CURRENT_SLOT}
  docker rm ${CONTAINER_NAME}-${CURRENT_SLOT}
  echo "Old ${CURRENT_SLOT} container removed"
else
  echo "No current container found (possibly first deployment)"
fi

echo
echo "Step 8: Cleaning up old images..."
docker image prune -af --filter 'until=24h' || true
echo "Cleanup completed"

echo
echo "==================================================="
echo "Development Deployment Completed Successfully!"
echo "==================================================="
