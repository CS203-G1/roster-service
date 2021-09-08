# Roster-Service
## Dependencies
* PostgreSQL
## Setting up the service

### Dependencies
1. Git clone this repository into your preferred directory.
2. Open [IntelliJ](https://www.jetbrains.com/idea/)
   1. Go to Files > New > Project From Existing Sources...
   2. Choose the roster-service folder that you just cloned. 
   3. Select ```Import project from External Model``` option and select ```Maven```
3. IntelliJ should start setting up the dependencies for this service.

Note: Open ```pom.xml``` in the root folder and check that all dependencies are not throwing error.

If you see a red highlighted version number under this dependency, restart your IntelliJ and open this project again.
```sh
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.4</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```
### Application Properties
```sh
server.port = 8080
spring.jpa.database=POSTGRESQL
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=<POSTGRES_USERNAME>
spring.datasource.password=<POSTGRES_PASSWORD>
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation= true
```
**Variables:**
- ```DATABASE_CONNECTION_URL```:
  - This would be your localhost url, for MacOS users you can use ```host.docker.internal```.
- ```POSTGRES_USERNAME```:
  - Your PostgreSQL username
- ```POSTGRES_PASSWORD```: 
  - Your PostgreSQL password

## Deploying Roster Service on EC2
1. Under root directory ```roster-service```, create a ```docker-compose.yml``` file with the following commands:
```sh
version: '2'

services:
  app:
    image: 'roster-service:latest'
    build:
      context: .
    container_name: roster-service
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://<DATABASE_CONNECTION_URL>:5432/<DATABASE_NAME>
      - SPRING_DATASOURCE_USERNAME=<POSTGRES_USERNAME>
      - SPRING_DATASOURCE_PASSWORD=<POSTGRES_PASSWORD>
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
```
**Variables:** 
- ```DATABASE_CONNECTION_URL```: 
  - This would be your localhost url, for MacOS users you can use ```host.docker.internal```.
- ```POSTGRES_USERNAME```: 
  - Your PostgreSQL username
- ```POSTGRES_PASSWORD```: 
  - Your PostgreSQL password

2. In terminal, execute the following command to run the docker:
```sh
docker-compose up
```
3. To stop the service, ```press Ctrl+C``` and run the following command:
```sh
docker-compose down
```
