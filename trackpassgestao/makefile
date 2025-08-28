# ===== Variáveis =====
IMAGE := trackpassDocker/api
TAG   := dev
PORT  := 8080

.PHONY: help dev up logs down build nocache-build restart run

## comandos disponíveis
help:
	@echo "Comandos disponiveis:"
	@echo "  make dev            - Build e sobe o container (atalho mais usado)"
	@echo "  make up             - Sobe os containers existentes (rapido)"
	@echo "  make down           - Para e remove os containers"
	@echo "  make logs           - Logs em tempo real da api"
	@echo "  make restart        - Reinicia o container da api"
	@echo "  make build          - Apenas builda a imagem Docker"
	@echo "  make nocache-build  - Forca um build sem usar o cache"
	@echo "  make run            - Roda a imagem localmente (sem Docker Compose)"

# Build normal (com cache)
build:
	docker build \
		--build-arg BUILD_VERSION=$(TAG) \
		-t $(IMAGE):$(TAG) .

# Build do zero (sem cache)
nocache-build:
	docker build --no-cache \
		--build-arg BUILD_VERSION=$(TAG) \
		-t $(IMAGE):$(TAG) .

# Sobe os containers (sem build)
up:
	docker compose -f docker-compose.dev.yml up -d

# Logs do serviço em tempo real
logs:
	docker compose -f docker-compose.dev.yml logs -f api

# Para e remove os containers
down:
	docker compose -f docker-compose.dev.yml down

# Reinicia o serviço
restart:
	docker compose -f docker-compose.dev.yml restart api

# Build + Up sendo executados juntos
dev: build up

# Roda a imagem direto (sem Docker Compose)
run:
	docker run --rm -p $(PORT):8080 \
		-e SPRING_PROFILES_ACTIVE=$(TAG) \
		$(IMAGE):$(TAG)