#!/bin/bash

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Carrega as variáveis de ambiente
set -a
source .env.dev
set +a

DOCKER_FILE="docker-compose.dev.yml"

# Encontra o container name dinamicamente
CONTAINER_NAME=$(docker compose -f $DOCKER_FILE ps -q db_service | xargs docker inspect -f '{{.Name}}' | sed 's/\///')

if [ -z "$CONTAINER_NAME" ]; then
    echo -e "${RED}✗ Erro: Container db_service não encontrado ou não está rodando${NC}"
    exit 1
fi

echo -e "${YELLOW}⏳ Gerando e inserindo 100 usuários...${NC}"

# Gera os INSERTs com verificação
SQL_COMMANDS=""
for i in {1..100}; do
  SQL_COMMANDS+="INSERT INTO usuarios (empresa_id, is_ativo, cpf, data_nascimento, email, nome, senha, telefone, uuid, criado_em, atualizado_em) 
  SELECT 4, true, '$(printf "%011d" $i)', '1990-01-01', 'user$i@example.com', 'User $i', '\$2a\$10\$hash', '11987654321', gen_random_uuid(), NOW(), NOW()
  WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE empresa_id = 4 AND email = 'user$i@example.com'
  );"$'\n'
done

# Executa no banco de dados silenciosamente
echo "$SQL_COMMANDS" | docker exec -i $CONTAINER_NAME psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -q 

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Usuários inseridos com sucesso!${NC}"
else
    echo -e "${RED}✗ Erro ao inserir usuários${NC}"
    exit 1
fi