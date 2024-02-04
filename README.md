# Viver bem API

<!-- Shields Exemplo, existem N diferentes shield em https://shields.io/ -->
![GitHub last commit](https://img.shields.io/github/last-commit/DevDuoTech/viver-bem-api)
![GitHub language count](https://img.shields.io/github/languages/count/DevDuoTech/viver-bem-api)
![Github repo size](https://img.shields.io/github/repo-size/DevDuoTech/viver-bem-api)
[![Docker](https://github.com/DevDuoTech/viver-bem-api/actions/workflows/docker-image.yml/badge.svg)](https://github.com/DevDuoTech/viver-bem-api/actions/workflows/docker-image.yml)

> A manager of tenants, payments and contracts for Viver Bem's apartments.

## Pre-requisites

Before running the project, make sure you have the following dependencies installed:

- `Java 17`
- `Apache Maven 3.9.2`
- `Docker`

## How to run

Follow the steps below to run the project on your local machine:

Run the following commands from the root folder of the project:

### Clone this repository

```bash
git clone https://github.com/DevDuoTech/viver-bem-api
```

### Create a ``.env``
Create a ``.env`` in root folder with following variables

````bash
DB_USERNAME=example
DB_PASSWORD=example

PGADMIN_USERNAME=example@example.com
PGADMIN_PASSWORD=example
````

### Run the Project

Enter the root folder of the project and run the command:

```bash
docker compose up
```

**Alternatively, you can run the project with:**

```bash
mvn spring-boot:run
```

## Folder Structure

The project folder structure is organized as follows:

```text
|-- viver-bem-api/
|   |-- Collections/
|   |   |-- Viver bem API.postman_collection.json
|   |-- src/
|   |   |-- main/
|   |   |   |-- br/com/devduo/viverbemapi
|   |   |   |   |-- config
|   |   |   |   |-- controller
|   |   |   |   |-- dtos
|   |   |   |   |-- enums
|   |   |   |   |-- exceptions
|   |   |   |   |-- handler
|   |   |   |   |-- models
|   |   |   |   |-- repository
|   |   |   |   |-- service
|   |-- resources
|   |   |-- db/migration
|   |   |-- application.yaml
|   |   |-- messages.properties
|   |-- test/
|-- ...
```

## Documentation
Follow the link to access the documentation: http://localhost:8080/swagger-ui/index.html

## Back to the top

[⬆ Back to the top](#viver-bem-api)
