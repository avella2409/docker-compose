# docker-compose

## Goal

Use docker-compose to run a spring boot app with a mongo database.

## Step

### Step 1 : Set properties for the mongo database

```properties
spring.data.mongodb.host=HOST
spring.data.mongodb.port=27017
spring.data.mongodb.username=username
spring.data.mongodb.password=password
spring.data.mongodb.authentication-database=admin
spring.data.mongodb.database=testdb
```

### Step 2 : Create a simple pojo for mongo document

We don't need to annotate the class with `@Document` or to annotate the field id with `@Id`, if the name of the id is `id` this is ok.

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
class Person {
    String id;
    String name;
    String city;
}
```

### Step 3 : Create a spring data repository

```java
interface PersonRepository extends ReactiveMongoRepository<Person, String> {
    Flux<Person> findAllByCity(String city);
}
```

### Step 4 : Create a web endpoint to see data

```java
    @Bean
    RouterFunction<ServerResponse> route(PersonRepository personRepository) {
        return RouterFunctions.route()
                .GET("/person/city/{city}", serverRequest -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(personRepository.findAllByCity(serverRequest.pathVariable("city")), Person.class))
                .build();
    }
```

### Step 5 : Create some sample data when application start

```java
@RequiredArgsConstructor
@Component
class SampleDataInitializer {

    private final PersonRepository personRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {

        Flux<Person> saveAllPerson = personRepository.saveAll(Flux.just(
                new Person(null, "Anthony", "paris"),
                new Person(null, "Celia", "paris"),
                new Person(null, "Thomas", "lyon"),
                new Person(null, "Romain", "marseille")
        ));

        personRepository.deleteAll()
                .thenMany(saveAllPerson)
                .subscribe();
    }
}
```

### Step 6 : Create the Dockerfile for this app

- `FROM openjdk:15` Specify base image
- `ARG JAR_FILE=target/*.jar` Create variable `JAR_FILE` referencing our `jar`file
- `COPY ${JAR_FILE} app.jar` Copy our jar file to the container and name it  `app.jar`
- `EXPOSE 8080` Expose our server port (It's optional, but it's good to see what port our app will be using)
- `ENTRYPOINT ["java", "-jar", "/app.jar"]` Launch our app when we start our container

```dockerfile
FROM openjdk:15
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Step 7 : Create the `docker-compose.yml` file

- `version` Specify the version of the `docker-compose.yml` file format
- `services` List of all service we want to deploy
- `volumes` Volumes to create if not already created

```yaml
version: "3.9"
services:
  java-app:
    build: .
    ports:
      - "8080:8080"
  mongodb:
    image: mongo:4.4.2
    ports:
      - "27017:27017"
    environment:
      - MONGO_INITDB_ROOT_USERNAME=username
      - MONGO_INITDB_ROOT_PASSWORD=password
    volumes:
    - mongodb-volume:/data/db
volumes:
  mongodb-volume:
    name: mongodb-volume
```

### Step 8 : Run our docker-compose file

- `-d` Detached mode
```bash
docker-compose up -d
```

If we use the following command we should see two container started, the container that hold our spring boot app and the container that hold our mongo database

```bash
docker ps
```

### Step 9 : Test our endpoint

Go to our endpoint `/person/city/paris`, we should see our sample data with city equals to `paris`.

### Final step : Stop all container

To stop all container started with docker-compose we just need to use the following command

```bash
docker-compose down
```