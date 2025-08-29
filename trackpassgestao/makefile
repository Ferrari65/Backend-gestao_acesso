# ===== Variáveis =====
IMAGE  := trackpassDocker/api
TAG    := dev
PORT   := 8080

BRANCH := $(shell git rev-parse --abbrev-ref HEAD)

.PHONY: help docker git \
        build up restart down dev run \
        s pull c push historico ch nb ch-% nb-% branch \
        merge-main

## ==================== MENU  ====================
help:
	@echo "== Escolha um grupo de comandos =="
	@echo "  make docker       - Ver comandos de Docker"
	@echo "  make git          - Ver comandos de Git"
	@echo "  make merge        - exibe fluxo de merge main -> dev"

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
	@echo ""
	@echo "== Extras =="
	@echo "  make ch-nome             - Trocar via atalho (ex.: make ch-dev)"
	@echo "  make nb-nome             - Criar via atalho (ex.: make nb-feature/login)"
	@echo "  make branch              - Mostrar a branch atual"

merge:
	@echo "===== trazer main -> dev ===="
	@echo "  make merge-main          - Atualiza main e faz merge main -> dev cria dev se não existir"

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

merge-main:
	@echo ">> Verificando se há alterações locais não commitadas..."
	@git diff-index --quiet HEAD -- || echo " Há mudanças locais. Faça commit make c M=\"msg\" ou git stash antes." & exit 1
	@echo ">> Atualizando 'main'..."
	@git checkout main
	@git pull origin main
	@echo ">> Indo para 'dev' criando se necessário..."
	@git checkout dev || git checkout -b dev
	@echo ">> Fazendo merge: main -> dev..."
	@git merge main
	@echo ">> Enviando 'dev' para o remoto ..."
	@git push -u origin dev
	@echo " Fluxo concluído: 'dev' agora contém tudo que está em 'main'."
