# Projeto Integrador 4 Semestre

Este projeto é relacionado com as disciplinas da FATEC ARARAS ANTÔNIO BRAMBRILLA do 4° semestre de 2025. As disciplinas base são:
- Laboratório de Desenvolvimento Web III
- Qualidade e Teste de Software
- Integração e Entrega Contínua

O Projeto Consiste em um sistema para a Empresa VIAÇÃO UNIÃO, onde cumprirá os requisitos conforme apontados abaixo:
- link requisitos

Algumas funcionalidades e condições especiais para a apresentação e conclusão deste semestre são:
- Consumo de UMA API de pagamento;
- Disponibilização do projeto como um site;
- Uso de docker para Integração e Entrega Contínua;
- Testes para o funcionamento adequado do sistema.

O Projeto Consiste das seguintes ferramentas como principais:
- Java 17
- Docker
- Spring Boot
- Postgres

outros como apontados aqui na documentação oficial.

### Executando o projeto

* **Ambiente de desenvolvimento**

```bash
cd projeto_integrador_4_semestre
./bash/build.sh
```

* **Ambiente de produção**

```bash
cd projeto_integrador_4_semestre
./bash/build.sh prod
```

* **Estrutura de pastas**
.
```md
├── bash
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── projetoanderson
│   │   │           └── app
│   │   │               ├── config
│   │   │               ├── controller
│   │   │               ├── exception
│   │   │               ├── model
│   │   │               │   ├── dto
│   │   │               │   └── entity
│   │   │               │       └── enums
│   │   │               ├── repository
│   │   │               ├── service
│   │   │               └── validation
│   │   └── resources
│   └── test
│       └── java
│           └── com
│               └── projetoanderson
│                   └── app
│                       └── model
│                           ├── dto
│                           └── entity
├── target
│   ├── classes
│   │   └── com
│   │       └── projetoanderson
│   │           └── app
│   │               ├── config
│   │               ├── controller
│   │               ├── exception
│   │               ├── model
│   │               │   ├── dto
│   │               │   └── entity
│   │               │       └── enums
│   │               ├── repository
│   │               ├── service
│   │               └── validation
│   ├── generated-sources
│   │   └── annotations
│   ├── generated-test-sources
│   │   └── test-annotations
│   ├── maven-status
│   │   └── maven-compiler-plugin
│   │       ├── compile
│   │       │   └── default-compile
│   │       └── testCompile
│   │           └── default-testCompile
│   └── test-classes
│       └── com
│           └── projetoanderson
│               └── app
│                   └── model
│                       ├── dto
│                       └── entity
└── wrapper
```
