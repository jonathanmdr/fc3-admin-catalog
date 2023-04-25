# Video Management Catalog
Microservice for managing the video catalog with Clean Architecture and Domain Driven Design.

### Microservice Domains
 - Category
 - Genre
 - Cast Member
 - Video

## How to run the project in DEV mode
> :warning: This project needs of docker runtime available on the environment
### Up
```shell
# This command provides the following containers: MySQL Database
docker-compose up -d
```
### Down
```shell
# This command kills all containers, volumes and networks
docker-compose down --remove-orphans --volumes
```

### Project entry point

```
# The project entry point is a class named Main
org.fullcycle.admin.catalog.infrastructure.Main.java
```

### Profile
```shell
# Use it on VM args:
# - dev  -> Development environment
# - prod -> Production environment
-Dspring.profiles.active=dev
```

