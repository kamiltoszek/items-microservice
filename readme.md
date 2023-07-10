# For playing with the app using postman / swagger

Security header name is: "X-Api-Key". Key value is: "LetMeIn". Build-in swagger console is aware of this requirement.
Click on authorize button and set key value to LetMeIn and it will work.

# What is this?

This is toy project with imaginary items catalog microservice. It has simple functionality like
creating/updating/deleting items as well as fetching items. Its functionality is simple yet showcases some interesting
aspects like:

- Use of cache (Coffeine) through Spring Cache abstraction layer.
    - Coffeine is in memory cache, simple and lightweight, but it's not distributed cache so in case you scale
      microservice to many instances, each instance will have its own cache if this implementation is used. Ehcache has
      support for distributed caching.
- Use of Liquibase for database model migration, JPA will only validate generated db model with declared Entities to
  make sure they match.
- Use of QueryDSL for JPA and JpaSpecificationExecutor: It allows to build dynamic queries, used here in fetch items
  filtered by name, description, and price (eq, less, greater then). In addition hibernate-jpamodelgen is used which
  genreates static model for us to use while defining criteria so we don't deal with field names defined as strings...
- Spring Modulith is used to enforce modular approach, items package contains "modules": catalog (with main app logic)
  and infrastructure (with spring configuration mostly). There is also showcase how to tell Modulith that given package
  is "public" (see catalog/command/package-info.java for example). It also generates code documentation using puml.
- There is Spring Security with api key approach (as this is imaginary microservice working in some cluster of other
  services).
- Swagger/OpenApi documentation (under /swagger-ui.html)
- Tests: Unit test with mocking of database layer and integration tests using rest-assured library
- JPA Auditing: When we insert / update item entity, JPA will automatically update creation date and last modified date
  for us.
- Mapstruct and lombok working together: BTW mapstruct is used to create new dts, to update existing entity from command
  objects. It is configured that missing target field is treated as error, so we can catch early forgotten fields in
  commands.
- Exception handling: There is a global exception handler with configuration for proper response on errors in endpoint
  parameters, as well as annotation on individual exception to tell what response should be send to the client after
  throwing given exception.
- With approach of modules having public and internal, private api, I did here ItemCatalogApi as facade to module's
  functionality. Implementation of this api is also using jakarta.validation annotations, thanks to that,
  not only endpoints are guarded for argument errors, but also calls of module's interface from other modules (in code)
  are validated.
- Use of dynamic projection: ItemRepository.findItemByUuid(UUID uuid, Class<T> type) is used to return ItemDto
  directly instead of mapping via mapstruct mapper after loading Item from the db.

# My note on tests in this project

Unit tests here are proper unit tests, they work without Spring container running. They show how to use Mockito and
fluent assertions. How to verify if function was called and how many times. How to inject response to mock and how to
return updated request object (think update).
But in general, in this application, normally I would not write such tests. There is not much logic which makes any
decisions. It's just tiny layer on top of the database. Thus normally I would not write them, as they are don't give
much value and knowing too much about internals (mocking of repository layer)
they will require more maintenance time if code would be refactored in the future. For this project, integration tests
are more relevant. Normally I would make scenarios how user can interact with endpoint or group of endpoints, and test
happy and unhappy paths, then think of security like accessing protected
endpoints with different users or tinkering with is and check if I got response.

# What libraries are used here

- Spring Boot 3.1 and Spring 6 (jakarta)
- Spring Security 6 with API Key security on /api/** endpoints
- H2 for database
- Java 17 (current LTS)
- Liquibase
- Coffeine for cache
- Lombok
- Mapstruct
- QueryDSL and hibernate-jpamodelgen (for findAll with specification)
- OpenApi documentation with swagger web interface

# Building and running docker image

Project is dockerized and contains also docker-compose.yml for easier running and cleaning of docker container.

Run below commands to compile application and build docker image

- mvn clean package
- docker build -t items-ms:latest .

Now you can start it up using:

- docker-compose up

To clean after running, stop the app (ctr-c in terminal) and run:

- docker-compose down
