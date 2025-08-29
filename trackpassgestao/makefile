# ===== Variáveis =====
IMAGE := trackpassDocker/api
TAG   := dev
PORT  := 8080

.PHONY: help docker git

## comandos 
help:
	@echo "== Escolha um grupo de comandos =="
	@echo "  make docker   - Ver comandos de Docker"
	@echo "  make git      - Ver comandos de Git"

docker:
	@echo =========================  Comandos DOCKER  =========================== 
	@echo ""
	@echo  Ordem recomendada de uso: 
	@echo   1. make build         - Cria a imagem Docker do backend - primeira vez ou após alterações no código
	@echo   2. make up            - Sobe o container com base na imagem já criada
	@echo   3. make restart       - Reinicia o container da API - para pequenas alterações
	@echo   4. make down          - Para e remove o container quando não for mais usar
	@echo ""
	@echo == Atalhos e extras: =="
	@echo   make dev              - Faz build + up de uma vez 
	@echo   make run              - Roda a imagem direto, sem Compose - apenas para testes rápidos

git:
	@echo =========================  Comandos GIT  =========================== 
	@echo   make v      			 - Ver o que mudou
	@echo   make commit 			 - Adicionar e commitar mudanças 
	@echo   make at 			 - Baixar ultimas mudancas do repositorio
	@echo   make env   		 	 - Enviar commits para o repositorio
	@echo   make t		 	 - Trocar para outra branch 

#======== COMANDOS DOCKER ========
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

#======== COMANDOS GIT ========

v:
	git status

at:
	git pull origin $$(git branch --show-current)

env:
	git push origin $(shell git branch --show-current)

t:
	git checkout $(BR)

commit:
	git add .
	git commit -m "$(M)"
