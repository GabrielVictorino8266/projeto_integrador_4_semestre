#!/usr/bin/env bash

echo "==============================="
echo "Starting Docker build script..."
echo "==============================="

if [ "$1" = "prod" ]; then
    echo "[INFO] Environment: PRODUCTION"
    echo "[INFO] Using docker-compose.prod.yml and .env.prod"
    docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
    echo "[INFO] Production containers are up."
else
    echo "[INFO] Environment: DEVELOPMENT (default)"
    echo "[INFO] Using docker-compose.dev.yml and .env.dev"
    docker-compose -f docker-compose.dev.yml --env-file .env.dev up -d --build
    echo "[INFO] Development containers are up."
fi

echo "==============================="
echo "Docker build script finished."
echo "==============================="
