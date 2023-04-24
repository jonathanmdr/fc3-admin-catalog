# Administrador do Catálogo de Vídeos
Microsserviço Administrador do Catálogo de Vídeos do projeto CodeFlix utilizando Clean Architecture com DDD.

### Domains
 - Category
 - Genre
 - Cast Member
 - Video

## How to run project on DEV mode
> :warning: This project needs of docker runtime available on environment
### Up
```shell
# This command provides containers of: MySQL Database
docker-compose up -d
```
### Down
```shell
# This command kill all containers, volumes and networks
docker-compose down --remove-orphans --volumes
```

