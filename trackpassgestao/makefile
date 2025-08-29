# ===== Variáveis =====
IMAGE  := trackpassDocker/api
TAG    := dev
PORT   := 8080

BRANCH := $(shell git rev-parse --abbrev-ref HEAD)

.PHONY: help docker git \
        build up restart down dev run \
        s pull c push historico ch nb ch-% nb-% branch

## ==================== MENU  ====================
help:
	@echo "== Escolha um grupo de comandos =="
	@echo "  make docker   - Ver comandos de Docker"
	@echo "  make git      - Ver comandos de Git"

## ==================== COMANDOS DOCKER ====================
docker:
	@echo "=========================  Comandos DOCKER  =========================="
	@echo ""
	@echo "== Ordem recomendada de uso =="
	@echo "  1. make build         - Cria a imagem Docker do backend (1ª vez ou após alterações no código)"
	@echo "  2. make up            - Sobe o container com base na imagem já criada"
	@echo "  3. make restart       - Reinicia o container da API (para pequenas alterações)"
	@echo "  4. make down          - Para e remove o container quando não for mais usar"
	@echo ""
	@echo "== Atalhos e extras =="
	@echo "  make dev              - Faz build + up de uma vez"
	@echo "  make run              - Roda a imagem direto, sem Compose (testes rápidos)"

# ======== COMANDOS DOCKER ========
build:
	docker build \
		--build-arg BUILD_VERSION=$(TAG) \
		-t $(IMAGE):$(TAG) .

up:
	docker compose -f docker-compose.dev.yml up -d

restart:
	docker compose -f docker-compose.dev.yml restart api

down:
	docker compose -f docker-compose.dev.yml down

dev: build up

run:
	docker run --rm -p $(PORT):8080 \
		-e SPRING_PROFILES_ACTIVE=$(TAG) \
		$(IMAGE):$(TAG)

## ==================== COMANDOS GIT ====================
git:
	@echo "=========================  Comandos GIT  ==========================="
	@echo ""
	@echo "== Ordem recomendada de uso =="
	@echo "  1. make s                - Ver o que mudou (status dos arquivos)"
	@echo "  2. make pull             - Atualizar repositório local (git pull da branch atual)"
	@echo "  3. make c M=\"msg\"        - Adicionar e commitar alterações com mensagem"
	@echo "  4. make push             - Enviar commits para a branch atual (git push)"
	@echo "  5. make historico        - Ver últimos commits resumidos"
	@echo ""
	@echo "== Extras =="
	@echo "  make ch BR=nome          - Trocar para outra branch existente"
	@echo "  make nb BR=nome          - Criar e trocar para uma nova branch"
	@echo "  make ch-nome             - Trocar para branch pelo atalho (ex.: make ch-dev)"
	@echo "  make nb-nome             - Criar e trocar para nova branch (ex.: make nb-feature/login)"
	@echo "  make branch              - Mostrar a branch atual"

# ======== COMANDOS GIT ========
s:
	git status

pull:
	git pull origin $(BRANCH)

push:
	git push origin $(BRANCH)

c:
	git add .
	- git commit -m "$(M)" || echo "Nenhuma mudança para commitar"

historico:
	git log --oneline -n 10

ch:
	git checkout $(BR)

nb:
	git checkout -b $(BR)

ch-%:
	git checkout $*

nb-%:
	git checkout -b $*

branch:
	@echo "Branch atual: $(BRANCH)"
