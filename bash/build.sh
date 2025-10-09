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


# Verifica se o arquivo .env existe
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}Erro: Arquivo $ENV_FILE não encontrado.${NC}"
    exit 1
fi

echo -e "${BLUE}===============================${NC}"
echo -e "${BLUE}Iniciando script do Docker ($ENV)...${NC}"
echo -e "${BLUE}===============================${NC}"

# Remove containers antigos
$DOCKER_BIN -f "$COMPOSE_FILE" --env-file "$ENV_FILE" down -v

# Inicializa os containers
$DOCKER_BIN -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up $UP_FLAGS

# Verificar saúde dos containers
SERVICES=$($DOCKER_BIN -f "$COMPOSE_FILE" config --services --no-interpolate)

echo -e "${YELLOW}[INFO] Verificando status dos containers...${NC}"

for SERVICE in $SERVICES; do
    echo -e "${BLUE}[INFO] Verificando serviço: $SERVICE${NC}"
    
    while true; do
        # Obter status do container do serviço
        CONTAINER_STATUS=$($DOCKER_BIN -f "$COMPOSE_FILE" ps -q "$SERVICE" 2>/dev/null | xargs docker inspect -f '{{.State.Status}}' 2>/dev/null)
        
        if [ $? -ne 0 ] || [ -z "$CONTAINER_STATUS" ]; then
            echo -e "${RED}[ERROR] Erro ao verificar status do serviço $SERVICE${NC}"
            break
        fi
        
        case "$CONTAINER_STATUS" in
            "running")
                # Verificar se tem health check
                HEALTH_STATUS=$($DOCKER_BIN -f "$COMPOSE_FILE" ps -q "$SERVICE" 2>/dev/null | xargs docker inspect -f '{{.State.Health.Status}}' 2>/dev/null)
                
                if [ "$HEALTH_STATUS" = "healthy" ] || [ "$HEALTH_STATUS" = "<no value>" ]; then
                    echo -e "${GREEN}[INFO] ✓ $SERVICE está rodando${NC}"
                    break
                elif [ "$HEALTH_STATUS" = "unhealthy" ]; then
                    echo -e "${RED}[ERROR] ✗ $SERVICE está rodando mas não está saudável${NC}"
                    break
                else
                    echo -e "${YELLOW}[INFO] ⏳ $SERVICE aguardando health check...${NC}"
                    sleep 2
                fi
                ;;
            "restarting"|"starting")
                echo -e "${YELLOW}[INFO] ⏳ $SERVICE está iniciando...${NC}"
                sleep 2
                ;;
            "exited")
                echo -e "${RED}[ERROR] ✗ $SERVICE parou inesperadamente${NC}"
                break
                ;;
            *)
                echo -e "${RED}[ERROR] ✗ $SERVICE em status desconhecido: $CONTAINER_STATUS${NC}"
                break
                ;;
        esac
    done
done

echo -e "${GREEN}[INFO] Verificação concluída!${NC}"

echo -e "${BLUE}===============================${NC}"
echo -e "${BLUE}Script do Docker finalizado.${NC}"
echo -e "${BLUE}===============================${NC}"