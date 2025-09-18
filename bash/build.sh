#!/usr/bin/env bash

# Cores para saída
GREEN="\033[0;32m"
YELLOW="\033[1;33m"
RED="\033[0;31m"
BLUE="\033[0;34m"
NC="\033[0m" # Sem cor

# Detecta o comando Docker Compose
if command -v docker compose >/dev/null 2>&1; then
    DOCKER_BIN="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
    DOCKER_BIN="docker-compose"
else
    echo -e "${RED}Erro: Nenhum comando docker compose encontrado.${NC}"
    exit 1
fi

# Define ambiente com base no argumento
if [ "$1" = "prod" ]; then
    ENV="prod"
    COMPOSE_FILE="docker-compose.prod.yml"
    ENV_FILE=".env.prod"
    UP_FLAGS="--build -d"
else
    ENV="dev"
    COMPOSE_FILE="docker-compose.dev.yml"
    ENV_FILE=".env.dev"
    UP_FLAGS="--build -d"
fi

echo -e "${BLUE}===============================${NC}"
echo -e "${BLUE}Iniciando script do Docker ($ENV)...${NC}"
echo -e "${BLUE}===============================${NC}"

# Inicializa os containers
$DOCKER_BIN -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up $UP_FLAGS
# Note: This will use cache unless you combine with build --no-cache first
echo -e "${YELLOW}[INFO] Aguardando containers ficarem prontos...${NC}"

# # Debug: exibe status dos containers
# echo -e "${YELLOW}[DEBUG] Status dos containers:${NC}"
# $DOCKER_BIN -f "$COMPOSE_FILE" ps

# # Debug: exibe status de saúde
# echo -e "${YELLOW}[DEBUG] Status de saúde:${NC}"
# $DOCKER_BIN -f "$COMPOSE_FILE" ps -a --format "table {{.Name}}\t{{.Status}}\t{{.Health}}"

# Verificar saúde dos containers
SERVICES=$($DOCKER_BIN -f "$COMPOSE_FILE" config --services --no-interpolate)
TOTAL_SERVICES=$(echo "$SERVICES" | wc -l)

while true; do
    # Conta quantos serviços estão em execução
    RUNNING_COUNT=$($DOCKER_BIN -f "$COMPOSE_FILE" ps -q 2>/dev/null | xargs docker inspect -f '{{if eq .State.Health.Status "healthy"}}1{{end}}' 2>/dev/null | grep -c 1)

    if [ "$RUNNING_COUNT" -eq "$TOTAL_SERVICES" ]; then
        echo -e "${GREEN}[INFO] Todos os containers estão rodando! ($RUNNING_COUNT/$TOTAL_SERVICES)${NC}"
        break
    else
        echo -e "${YELLOW}[INFO] Aguardando containers... ($RUNNING_COUNT/$TOTAL_SERVICES)${NC}"
        sleep 2
    fi
done


echo -e "${BLUE}===============================${NC}"
echo -e "${BLUE}Script do Docker finalizado.${NC}"
echo -e "${BLUE}===============================${NC}"