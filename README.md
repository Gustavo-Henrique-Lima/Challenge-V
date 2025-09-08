# Users Reader API

API desenvolvida em **Java 21** utilizando **Spring Boot**, com suporte a múltiplos ambientes (desenvolvimento e produção via Docker Compose), documentação em **Swagger** e cobertura de testes unitários, de repositório, de serviço e de integração.

---

## Como Rodar o Projeto

### Pré-requisitos

- [Java JDK](https://www.oracle.com/java/technologies/downloads/) **21** ou superior  
- [Maven](https://maven.apache.org/download.cgi) **3.1+**  
- [Docker](https://docs.docker.com/get-docker/) (para rodar em "produção")  

---

### Passo a Passo

#### 1. **Clonar o repositório**

- Com HTTPS:
  ```bash
  git clone https://github.com/Gustavo-Henrique-Lima/Challenge-V.git
  ```

- Com SSH:
  ```bash
  git clone git@github.com:Gustavo-Henrique-Lima/Challenge-V.git
  ```

---

#### 2. **Entrar no diretório principal**
```bash
cd Challenge-V\usersreader\
```

---

#### 3. **Executar em modo Desenvolvimento (perfil `dev`)**

Esse perfil roda **localmente** com banco em memória (**H2 Database**) e variáveis já configuradas.

```bash
mvn spring-boot:run
```

- A API estará disponível em:  
   [http://localhost:8080](http://localhost:8080)  
- Documentação Swagger:  
   [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

#### 4. **Executar em modo Produção (perfil `prod`)**

Esse perfil roda com **PostgreSQL** via Docker Compose.  

1. Edite o arquivo `.env` (ou exporte variáveis no terminal) caso queira personalizar:
   ```env
   APP_NAME=usersreader
   APP_PORT=8080
   SPRING_PROFILES_ACTIVE=prod
   CORS_ORIGINS=http://127.0.0.1:5500

   DB_SERVER=db
   DB_NAME=usersdb
   DB_USERNAME=usersapp
   DB_PASSWORD=supersecret
   DB_PORT=5432

   POSTGRES_VERSION=16-alpine
   ```

   *(se não alterar nada, os valores padrão já funcionarão).*

2. Suba os containers:
   ```bash
   docker compose up --build -d
   ```

- A API ficará disponível em:  
   [http://localhost:8080](http://localhost:8080)  
- Documentação Swagger:  
   [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Perfis de Configuração

- **`dev` (padrão para rodar via Maven/IDE)**  
  - Banco em memória (**H2**)  
  - Variáveis pré-configuradas  

- **`prod` (para rodar via Docker Compose)**  
  - Banco **PostgreSQL** persistente  
  - Variáveis definidas em `.env`  

---

## Variáveis de Ambiente

| Variável              | Descrição                                     | Padrão                 |
|-----------------------|-----------------------------------------------|------------------------|
| `APP_NAME`            | Nome da aplicação                             | usersreader            |
| `APP_PORT`            | Porta exposta da API                          | 8080                   |
| `SPRING_PROFILES_ACTIVE` | Define o perfil (`dev` ou `prod`)          | dev                    |
| `CORS_ORIGINS`        | Origens permitidas para CORS                  | http://127.0.0.1:5500  |
| `DB_SERVER`           | Host do banco (prod)                          | db                     |
| `DB_NAME`             | Nome do banco                                 | usersdb                |
| `DB_USERNAME`         | Usuário do banco                              | usersapp               |
| `DB_PASSWORD`         | Senha do banco                                | supersecret            |
| `DB_PORT`             | Porta do banco                                | 5432                   |
| `POSTGRES_VERSION`    | Imagem do PostgreSQL                          | 16-alpine              |

---

## Documentação

A API é documentada com **Swagger**, permitindo explorar e testar os endpoints de forma interativa.

- URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Testes

O projeto possui cobertura de testes:  

- **Unitários** (`UserTests`)  
- **Repositório** (`UserRepositoryTests`)  
- **Serviço** (`UserServiceTests`)  
- **Integração** (`UserControllerIT`)  

Para rodar os testes:  
```bash
mvn test
```
