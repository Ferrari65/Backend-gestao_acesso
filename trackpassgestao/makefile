# ===== Variáveis =====
IMAGE  := trackpassDocker/api
TAG    := dev
PORT   := 8080

BRANCH := $(shell git rev-parse --abbrev-ref HEAD)

.PHONY: help docker git merge \
        build up restart down dev run rebuild logs-up \
        pac check ch nb ch-% nb-% branch \
        merge-main

## ==================== MENU ====================
help:
	@echo == Escolha um grupo de comandos ==
	@echo   make docker-p   - Ver comandos de Docker com fluxos prontos
	@echo   make docker-a   - Ver comandos de Docker individuais avancados
	@echo   make git-p      - Ver comandos de Git com fluxos prontos
	@echo   make git-a      - Ver comandos de Git individuais avancados

## ==================== COMANDOS DOCKER ====================
docker-p:
	@echo =========================  Comandos DOCKER PRONTOS  ==========================
	@echo == fluxos prontos ==
	@echo   make dev              - Build + Up -- subir rapidamente
	@echo   make rebuild          - Down + Build + Up --atualizar tudo de uma vez
	@echo   make logs-up          - Sobe container e já mostra logs
	@echo 

docker-a:
	@echo "=========================  Comandos DOCKER Individuais  =========================="	
	@echo "== Individuais avancados =="
	@echo "  make build            - Build da imagem Docker"
	@echo "  make up               - Sobe container sem rebuild"
	@echo "  make restart          - Reinicia container em execução"
	@echo "  make down             - Derruba container"
	@echo "  make run              - Roda imagem direto, sem Compose"

# ======== DOCKER: individuais ========
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

run:
	docker run --rm -p $(PORT):8080 \
		-e SPRING_PROFILES_ACTIVE=$(TAG) \
		$(IMAGE):$(TAG)

# ======== DOCKER: combos ========
dev: build up

rebuild: down build up

logs-up:
	docker compose -f docker-compose.dev.yml up -d
	docker compose -f docker-compose.dev.yml logs -f api

## ==================== COMANDOS GIT ====================
git-p:
	@echo =========================  Comandos GIT  ===========================
	@echo ""
	@echo == Combos fluxos prontos =="
	@echo   make pac M=\"msg\"     	    - Pull + Add + Commit + Push - salvar e subir tudo
	@echo   make check             	    - Status e ultimos commits - resumo rapido
	@echo   make merge-main		    - Trazer main  dev 

git-a:
	@echo "== Individuais -- avancados =="
	@echo "  make ch BR=nome        - Trocar para branch existente"
	@echo "  make nb BR=nome        - Criar e trocar para nova branch"
	@echo "  make ch-nome           - Atalho (ex.: make ch-dev)"
	@echo "  make nb-nome           - Atalho (ex.: make nb-feature/login)"
	@echo "  make branch            - Mostrar a branch atual"

merge:
	@echo "===== Fluxo para trazer main -> dev ====="


# ======== GIT: combos ========
# Fluxo completo: pull + add + commit + push
pac:
	$(if $(strip $(M)),,$(error Use: make pac M="sua mensagem de commit"))
	git pull origin $(BRANCH)
	git add .
	git commit -m "$(M)"
	git push origin $(BRANCH)

# Checagem rápida
check:
	git status
	git log --oneline -n 5

# ======== GIT: individuais ========
ch:
	git checkout $(BR)

nb:
	git checkout -b $(BR)

# Atalhos sem BR=
ch-%:
	git checkout $*

nb-%:
	git checkout -b $*

branch:
	@echo "Branch atual: $(BRANCH)"

# ======== OPÇÃO A: Trazer main -> dev (merge) ========
merge-main:
	@echo Checking for uncommitted changes...
	@git diff-index --quiet HEAD -- || (echo ERROR: Uncommitted changes. Commit (make pac M="msg") or git stash first. & exit 1)
	@echo Updating main...
	@git checkout main
	@git pull origin main
	@echo Switching to dev (create if needed)...
	@git checkout dev || git checkout -b dev
	@echo Merging main into dev...
	@git merge main
	@echo Pushing dev...
	@git push -u origin dev
	@echo DONE: dev now contains everything from main.
