# ===== Variáveis =====
IMAGE  := trackpassDocker/api
TAG    := dev
PORT   := 8080

BRANCH := $(shell git rev-parse --abbrev-ref HEAD)

.PHONY: help docker git merge clean \
        build up restart down dev run rebuild logs-up \
        pac check ch nb ch-% nb-% branch \
        merge-main

## ==================== MENU ====================
help:
	@echo "== Escolha um grupo de comandos =="
	@echo "  make docker-p   - Ver comandos de Docker com fluxos prontos"
	@echo "  make docker-a   - Ver comandos de Docker individuais avançados"
	@echo "  make git-p      - Ver comandos de Git com fluxos prontos"
	@echo "  make git-a      - Ver comandos de Git individuais avançados"
	@echo "  make clean      - Limpa imagens e containers não utilizados"

## ==================== COMANDOS DOCKER ====================
docker-p:
	@echo "========================= Comandos DOCKER PRONTOS ========================="
	@echo "== Fluxos prontos =="
	@echo "  make dev         - Build + Up subir rapidamente"
	@echo "  make rebuild     - Down + Build + Up atualizar tudo de uma vez"
	@echo "  make logs-up     - Sobe container e já mostra logs"

docker-a:
	@echo "========================= Comandos DOCKER Individuais =========================="
	@echo "== Individuais avançados =="
	@echo "  make build       - Build da imagem Docker"
	@echo "  make up          - Sobe container sem rebuild"
	@echo "  make restart     - Reinicia container em execução"
	@echo "  make down        - Derruba container"
	@echo "  make run         - Roda imagem direto, sem Compose"

# ======== DOCKER: individuais ========
build:
	docker build --build-arg BUILD_VERSION=$(TAG) -t $(IMAGE):$(TAG) .

up:
	docker compose -f docker-compose.dev.yml up -d

restart:
	docker compose -f docker-compose.dev.yml restart api

down:
	docker compose -f docker-compose.dev.yml down

run:
	docker run --rm -p $(PORT):8080 -e SPRING_PROFILES_ACTIVE=$(TAG) $(IMAGE):$(TAG)

clean:
	@echo "Limpando containers e imagens não utilizados..."
	docker system prune --volumes --force
	@echo "Limpeza concluída."
# ======== DOCKER combos ========
dev: build up

rebuild: down build up

logs-up:
	docker compose -f docker-compose.dev.yml up -d
	docker compose -f docker-compose.dev.yml logs -f api

## ==================== COMANDOS GIT ====================
git-p:
	@echo "========================= Comandos GIT ========================="
	@echo "== Combos fluxos prontos =="
	@echo "  make pac M=\"msg\"   - Pull + Add + Commit + Push (salvar e subir tudo)"
	@echo "  make check          - Status e últimos commits (resumo rápido)"
	@echo "  make merge-main     - Traz as alterações da 'main' para sua branch atual"
	@echo "  make merge-dev      - Leva as alterações da 'dev' para a 'main' (precisa estar na 'main')"

git-a:
	@echo "== Individuais -- avançados =="
	@echo "  make ch-nome           - Atalho (ex.: make ch-dev)"
	@echo "  make nb-nome           - Atalho (ex.: make nb-feature/login)"
	@echo "  make branch            - Mostrar a branch atual"

pac:
	$(if $(strip $(M)),,$(error Use: make pac M="sua mensagem de commit"))
	git pull origin $(BRANCH)
	git add .
	git commit -m "$(M)"
	git push origin $(BRANCH)

check:
	git status
	git log --oneline -n 5

ch-%:
	git checkout $*

nb-%:
	git checkout -b $*

branch:
	@echo "Branch atual: $(BRANCH)"

# COMANDOS GIT PARA MERGE 
merge-main:
	@echo "===== Trazendo main para a sua branch atual ====="
	@echo "Verificando alterações não commitadas..."
	@git diff-index --quiet HEAD -- || (echo ERRO: Você tem alterações não commitadas. Use 'make pac M=\"msg\"' ou 'git stash' antes de continuar. && exit 1)
	@echo "Atualizando a branch 'main'..."
	git checkout main
	git pull origin main
	@echo "Voltando para '$(BRANCH)' e fazendo o merge..."
	git checkout $(BRANCH)
	git merge main
	@echo "Pronto! 'main' foi mergeada em '$(BRANCH)'."

merge-dev:
	@echo "===== Levando alterações da 'dev' para 'main' ====="
	@echo "Verificando alterações não commitadas..."
	@git diff-index --quiet HEAD -- || (echo ERRO: Você tem alterações não commitadas. Use 'make pac M=\"msg\"' ou 'git stash' antes de continuar. && exit 1)
	@echo "Trocando para 'main' e atualizando..."
	git checkout main
	git pull origin main
	@echo "Fazendo merge da 'dev' em 'main'..."
	git merge dev
	@echo "Enviando 'main' para o remoto..."
	git push origin main
	@echo "Pronto! 'dev' foi mergeada em 'main'."

