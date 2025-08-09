# Spring6Microservices

- [Why was this project created?](#why-was-this-project-created)
- [Elements included in this project](#elements-included-in-this-project)
  - [registry-server](#registry-server)
  - [config-server](#config-server)
  - [gateway-server](#gateway-server)
  - [security-oauth-service](#security-oauth-service)
  - [security-custom-service](#security-custom-service) 
  - [order-service](#order-service)
  - [invoice-service](#invoice-service)  
  - [common-core](#common-core)
  - [common-spring](#common-spring)
  - [grpc-api](#grpc-api)
  - [sql](#sql)
- [Previous steps](#previous-steps)
- [Security services](#security-services)
  - [security-oauth-service endpoints](#security-oauth-service-endpoints) 
  - [security-custom-service endpoints](#security-custom-service-endpoints)
- [Rest API documentation](#rest-api-documentation)
  - [Endpoint definitions](#endpoint-definitions)
- [gRPC communication](#grpc-communication)
  - [Security in gRPC](#security-in-grpc)
  - [Request identifier in gRPC](#request-identifier-grpc)
  - [gRPC example request](#grpc-example-request) 
- [Native images](#native-images)
  - [Install and configure GraalVM JDK](#install-and-configure-graalvm-jdk) 
  - [security-custom-service native](#security-custom-service-native)



## Why was this project created?

Basically to know how to create a project using the microservices approach with 6th version of Spring framework. Due to there are several options we
can use for different features included in a microservice architecture, the main purpose of this project is explore the most widely used creating a
good base we will be able to use in a real one.

The current project is based on previous the one [Spring5Microservices](https://github.com/doctore/Spring5Microservices).
<br><br>



## Elements included in this project

Below is shown a brief introduction to the subprojects included in this one:
<br><br>


### [registry-server](https://github.com/doctore/Spring6Microservices/tree/main/registry-server)

Server used to register all microservices included in this project. In this case, using [Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
each client can simultaneously act as a server, to replicate its status to a connected peer. In other words, a client retrieves a list of all connected
peers of a service registry and makes all further requests to any other services through a load-balancing algorithm (Ribbon by default).
<br><br>


### [config-server](https://github.com/doctore/Spring6Microservices/tree/main/config-server)

[Configuration server](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_spring_cloud_config_server) used by the included microservices
to get their required initial values like database configuration, for example. Those configuration values have been added into the project:

* [Spring6Microservices_ConfigServerData](https://github.com/doctore/Spring6Microservices_ConfigServerData)

As you can see, there is a specific folder for every microservice and the important information is encoded (the next code is part of
[security-custom-service-dev.yml](https://github.com/doctore/Spring6Microservices_ConfigServerData/blob/main/security-custom-service/security-custom-service-dev.yml) file):

```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spring6
    username: spring6
    # Using environment variable ENCRYPT_KEY=ENCRYPT_KEY
    # Raw password: spring6
    password: "{cipher}c8e1f3a8e0f5d7246a0dcbe620b97de51b580a1ef16f80ffafd3989920287278"
```

To increase the security level, in the [config-server](#config-server) microservice I have deactivated the decryption in [application.yml](https://github.com/doctore/Spring6Microservices/blob/main/config-server/src/main/resources/application.yml):

```
spring:
  cloud:
    config:
      server:
        encrypt:
          # We will send encrypted properties
          enabled: false
```

Sending the information encrypted and delegating in every microservice the labour of decrypt it. That is the reason to include in their *pom.xml*
files, the dependency:

```
<dependency>
   <groupId>org.springframework.security</groupId>
   <artifactId>spring-security-rsa</artifactId>
   <version>${spring-security-rsa.version}</version>
</dependency>
```
<br>


### [gateway-server](https://github.com/doctore/Spring6Microservices/tree/main/gateway-server)

Using [Spring Gateway Server WebFlux](https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-webflux.html), this is the gateway
implementation used by the other microservices included in this proof of concept. This module contains a filter to registry every web service invoked, helping
to debug each request.
<br><br>


### [security-oauth-service](https://github.com/doctore/Spring6Microservices/tree/main/security-oauth-service)

Full integration with Oauth 2.0 + Jwt functionality provided by [Spring Authorization Server](https://spring.io/projects/spring-authorization-server),
used to be able to manage authentication/authorization functionalities through access and refresh tokens. With this microservice working as Oauth 2.0 Server,
we will be able to configure the details of every allowed application using the database table: [security.oauth2_registered_client](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V3__oauth2_registered_client_table.sql).

On the other hand, several customizations have been included to manage the creation of access JWT token and how to append additional information too.

The technologies used are the following ones:

* **[Spring Data JDBC](https://spring.io/projects/spring-data-jdbc)** to speed up data access, persistence, and management between Java objects and database queries.
* **[Flyway](https://www.red-gate.com/products/flyway/)** as version control of database changes.
* **[Lombok](https://projectlombok.org/features)** to reduce the code development in entities and DTOs.
* **[Hazelcast](https://hazelcast.com)** as cache to reduce the invocations to the database.
* **[SpringDoc-OpenApi](https://springdoc.org/)** to document the endpoints provided by the microservice using [Swagger](https://swagger.io/).

In this microservice, the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.

On the other hand, there are other important folders:

* **configuration** with several classes used to manage several areas such: security, documentation, database or cache.
* **model** to store the entities.

Regarding **[Flyway](https://www.red-gate.com/products/flyway/)**, using [application.yml](https://github.com/doctore/Spring6Microservices/blob/main/security-oauth-service/src/main/resources/application.yml) the project has been configured
to avoid invoking it when this microservice is launched or packaged:

```
spring:
  flyway:
    enabled: false
```

So, if you want to manage it manually, you can create a new [maven](https://maven.apache.org/) configuration. The next picture displays how to do it
using [IntelliJ IDEA](https://www.jetbrains.com/idea/):

![Alt text](/documentation/security-oauth-service/ConfigureFlyway.png?raw=true "Configure Flyway")

All the managed SQL files are located in the folder [changelog](https://github.com/doctore/Spring6Microservices/tree/main/sql/changelog).
<br><br>


### [security-custom-service](https://github.com/doctore/Spring6Microservices/tree/main/security-custom-service)

Based on JWT token, this module was created to centralize the management of authentication/authorization functionalities. Its main purpose is provided
a completely multi-application platform to generate/manage their own access and refresh tokens (including additional information), choosing between the
options defined in [TokenType](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/java/com/security/custom/enums/token/TokenType.java):

* JWS
* JWE
* ENCRYPTED_JWS
* ENCRYPTED_JWE

The provided algorithms to sign the JWT tokens, that is, to generate `JWS` or the internal one inside `ENCRYPTED_JWS` are located in [TokenSignatureAlgorithm](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/java/com/security/custom/enums/token/TokenSignatureAlgorithm.java).
On the other hand, the available algorithms selecting `JWE` or `ENCRYPTED_JWE` are defined in [TokenEncryptionAlgorithm](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/java/com/security/custom/enums/token/TokenEncryptionAlgorithm.java) 

Every application will be able to manage its own token configuration/generation adding a new row in the database table: [security.application_client_details](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V1__application_client_details_table.sql)
and including/developing a new value in [SecurityHandler](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/java/com/security/custom/enums/SecurityHandler.java).

The technologies used are the following ones:

* **[Hibernate](https://hibernate.org)** as ORM to deal with the PostgreSQL database.
* **[JPA](https://en.wikipedia.org/wiki/Jakarta_Persistence)** for accessing, persisting, and managing data between Java objects and database.
* **[Spring Data JDBC](https://spring.io/projects/spring-data-jdbc)** to speed up data access, persistence, and management between Java objects and database for some queries.
* **[Flyway](https://www.red-gate.com/products/flyway/)** as version control of database changes.
* **[Lombok](https://projectlombok.org/features)** to reduce the code development in entities and DTOs.
* **[Hazelcast](https://hazelcast.com)** as cache to reduce the invocations to the database.
* **[NimbusJoseJwt](https://connect2id.com/products/nimbus-jose-jwt)** to work with JWS/JWE tokens.
* **[SpringDoc-OpenApi](https://springdoc.org/)** to document the endpoints provided by the microservice using [Swagger](https://swagger.io/).
* **[Webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html)** creating a reactive REST Api to manage the authentication/authorization requests.

In this microservice, the layer's division is:

* **application** parent folder of the microservice groups whose authentication/authorization is managed by this one.
* **repository** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Spring Webflux.

On the other hand, there are other important folders:

* **configuration** with several classes used to manage several areas such: security, documentation, database, cache, exception handlers, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util** to manage the JWS/JWE functionality.

Regarding **[Flyway](https://www.red-gate.com/products/flyway/)**, using [application.yml](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/resources/application.yml) the project has been configured
to avoid invoking it when this microservice is launched or packaged:

```
spring:
  flyway:
    enabled: false
```

So, if you want to manage it manually, you can create a new [maven](https://maven.apache.org/) configuration. The next picture displays how to do it
using [IntelliJ IDEA](https://www.jetbrains.com/idea/):

![Alt text](/documentation/security-custom-service/ConfigureFlyway.png?raw=true "Configure Flyway")

All the managed SQL files are located in the folder [changelog](https://github.com/doctore/Spring6Microservices/tree/main/sql/changelog).
<br><br>


### [order-service](https://github.com/doctore/Spring6Microservices/tree/main/order-service)

One order has several order lines and one order line mainly contains: concept, amount and cost. The main purpose of this microservice is the creation
of a small one on which I am using the following technologies:

* **[MyBatis](https://mybatis.org/mybatis-3/getting-started.html)** replacing the traditional pair Hibernate/JPA by adding more code to deal with the database but improving the performance.
* **[Flyway](https://www.red-gate.com/products/flyway/)** as version control of database changes.
* **[Lombok](https://projectlombok.org/features)** to reduce the code development in models and DTOs.
* **[MapStruct](https://mapstruct.org)** used to easily convert between models and DTOs and vice versa.
* **[SpringDoc-OpenApi](https://springdoc.org/)** to document the endpoints provided by the microservice using [Swagger](https://swagger.io/).
* **[Spring OAuth 2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)** to handle the security through the microservice [security-oauth-service](#security-oauth-service).
* **[MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)** a traditional Spring MVC Rest API to manage the included requests.
* **[gRPC](https://grpc.io/docs/what-is-grpc/introduction/)** server, more information in [gRPC communication](#grpc-communication).

In this microservice, the layer's division is:

* **mapper** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Spring MVC.

On the other hand, there are other important folders:

* **configuration** with several classes used to manage several areas such: security, documentation, database, exception handlers, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from models to DTOs and vice versa.

Regarding **[Flyway](https://www.red-gate.com/products/flyway/)**, using [application.yml](https://github.com/doctore/Spring6Microservices/blob/main/order-service/src/main/resources/application.yml) the project has been configured
to avoid invoking it when this microservice is launched or packaged:

```
spring:
  flyway:
    enabled: false
```

So, if you want to manage it manually, you can create a new [maven](https://maven.apache.org/) configuration. The next picture displays how to do it
using [IntelliJ IDEA](https://www.jetbrains.com/idea/):

![Alt text](/documentation/order-service/ConfigureFlyway.png?raw=true "Configure Flyway")

All the managed SQL files are located in the folder [changelog](https://github.com/doctore/Spring6Microservices/tree/main/sql/changelog).
<br><br>


### [invoice-service](https://github.com/doctore/Spring6Microservices/tree/main/invoice-service)

Microservice used to manage invoices and related customers. The main purpose of this microservice is the creation of a small one on which I am using
the following technologies:

* **[Hibernate](https://hibernate.org)** as ORM to deal with the PostgreSQL database.
* **[JPA](https://en.wikipedia.org/wiki/Jakarta_Persistence)** for accessing, persisting, and managing data between Java objects and database.
* **[Flyway](https://www.red-gate.com/products/flyway/)** as version control of database changes.
* **[Lombok](https://projectlombok.org/features)** to reduce the code development in entities and DTOs.
* **[SpringDoc-OpenApi](https://springdoc.org/)** to document the endpoints provided by the microservice using [Swagger](https://swagger.io/).
* **[Webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html)** creating a reactive REST Api to manage the authentication/authorization requests.
* **[gRPC](https://grpc.io/docs/what-is-grpc/introduction/)** client, more information in [gRPC communication](#grpc-communication).

In this microservice, the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Spring Webflux.

On the other hand, there are other important folders:

* **configuration** with several classes used to manage several areas such: security, documentation, database, exception handlers, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from models to DTOs and vice versa.

Regarding **[Flyway](https://www.red-gate.com/products/flyway/)**, using [application.yml](https://github.com/doctore/Spring6Microservices/blob/main/invoice-service/src/main/resources/application.yml) the project has been configured
to avoid invoking it when this microservice is launched or packaged:

```
spring:
  flyway:
    enabled: false
```

So, if you want to manage it manually, you can create a new [maven](https://maven.apache.org/) configuration. The next picture displays how to do it
using [IntelliJ IDEA](https://www.jetbrains.com/idea/):

![Alt text](/documentation/invoice-service/ConfigureFlyway.png?raw=true "Configure Flyway")

All the managed SQL files are located in the folder [changelog](https://github.com/doctore/Spring6Microservices/tree/main/sql/changelog).
<br><br>


### [common-core](https://github.com/doctore/Spring6Microservices/tree/main/common-core)

Maven project that includes common code used in several microservices, with different useful helper classes like:

* [AssertUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/AssertUtil.java)
* [CollectionUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/CollectionUtil.java)
* [CollectorsUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/CollectorsUtil.java)
* [ComparatorUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/ComparatorUtil.java)
* [DateTimeUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/DateTimeUtil.java)
* [EnumUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/EnumUtil.java)
* [ExceptionUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/ExceptionUtil.java)
* [FunctionUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/FunctionUtil.java)
* [MapUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/MapUtil.java)
* [NumberUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/NumberUtil.java)
* [ObjectUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/ObjectUtil.java)
* [PredicateUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/PredicateUtil.java)
* [StringUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/util/StringUtil.java)

Generic interfaces used to provide common conversion functionality using [MapStruct](https://mapstruct.org):

* [BaseConverter](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/converter/BaseConverter.java)
* [BaseEnumConverter](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/converter/enums/BaseEnumConverter.java)

And functional programming structures and useful classes like:

* [Either](https://github.com/doctore/Spring6Microservices/tree/main/common-core/src/main/java/com/spring6microservices/common/core/functional/either) as an alternative to [Optional](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html) for dealing with possibly missing values.
* [Function](https://github.com/doctore/Spring6Microservices/tree/main/common-core/src/main/java/com/spring6microservices/common/core/function) improvements.
* [Lazy](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/functional/Lazy.java) to manage a lazy evaluated value. 
* [PartialFunction](https://github.com/doctore/Spring6Microservices/blob/main/common-core/src/main/java/com/spring6microservices/common/core/functional/PartialFunction.java) unary function where the domain does not necessarily include all values of the type used.  
* [Predicate](https://github.com/doctore/Spring6Microservices/tree/main/common-core/src/main/java/com/spring6microservices/common/core/predicate) improvements.
* [Try](https://github.com/doctore/Spring6Microservices/tree/main/common-core/src/main/java/com/spring6microservices/common/core/functional/Try) representing a computation that may either result in an exception, or return a successfully computed value. 
* [Tuple](https://github.com/doctore/Spring6Microservices/tree/main/common-core/src/main/java/com/spring6microservices/common/core/collection/tuple) immutable objects that contains a fixed number of elements (by now up to 9).
* [Validation](https://github.com/doctore/Spring6Microservices/tree/main/common-core/src/main/java/com/spring6microservices/common/core/functional/validation) to validate the given instance.
<br><br>


### [common-spring](https://github.com/doctore/Spring6Microservices/tree/main/common-spring)

Maven project that includes common code specifically related with Spring framework, used in several microservices. It contains different useful helper classes like:

* [HashUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/util/HashUtil.java)
* [HttpUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/util/HttpUtil.java)
* [JsonUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/util/JsonUtil.java)

Improvements to the functionality provided by default for managing the database:

* [ExtendedJpaRepository](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/repository/ExtendedJpaRepository.java)
* [ExtendedQueryDslJpaRepository](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/repository/ExtendedQueryDslJpaRepository.java)

Wrapper to manage a cache, regardless of the selected implementation: 

* [CacheService](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/service/CacheService.java)

New validators: 

* [EnumHasInternalStringValue](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/validator/enums/EnumHasInternalStringValue.java): using **Enums** to map database values in [Hibernate](https://hibernate.org/) POJOs, allowing to verify internal properties.
* [ContainsAnyProvidedString](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/validator/string/ContainsAnyProvidedString.java): to verify if the given string matches with one of the provided ones inside the defined array.

Common DTOs to send/receive authentication, authorization data and/or handle errors invoking endpoints:

* [Common DTOs](https://github.com/doctore/Spring6Microservices/tree/main/common-spring/src/main/java/com/spring6microservices/common/spring/dto)
<br><br>


### grpc-api

Common functionality used by developed gRPC server and client. This one contains:

* [order.proto](https://github.com/doctore/Spring6Microservices/blob/main/grpc-api/src/main/resources/proto/order.proto) with the contract
  which includes defining the gRPC service and the method request and response types using [protocol buffers](https://developers.google.com/protocol-buffers/docs/reference/java-generated)
  specification.

* [BasicCredential](https://github.com/doctore/Spring6Microservices/blob/main/grpc-api/src/main/java/com/spring6microservices/grpc/security/BasicCredential.java)
  which carries the Basic Authentication that will be propagated from gRPC client to the server in the request metadata with the `Authorization` key.

* [GrpcErrorHandlerUtil](https://github.com/doctore/Spring6Microservices/blob/main/grpc-api/src/main/java/com/spring6microservices/grpc/util/GrpcErrorHandlerUtil.java)
  helper class with several methods to manage errors both on gRPC client and server side.

More information about how gRPC server and client uses it in [gRPC communication](#grpc-communication).
<br><br>


### [sql](https://github.com/doctore/Spring6Microservices/tree/main/sql)

With SQL files included in the database, just to expose the initial steps to start working with these microservices.

* [changelog](https://github.com/doctore/Spring6Microservices/tree/main/sql/changelog) contains all the SQL archives required by the microservices that
use [Flyway](https://www.red-gate.com/products/flyway/) to manage their changes in database.
<br><br>



## Previous steps

Due to every microservice has to decrypt the information sent by [config-server](#config-server), some steps are required:
<br><br>


### Setting up an encryption key

In this project a symmetric encryption key has been used. The symmetric encryption key is nothing more than a shared secret that's used by the encrypter
to encrypt a value and the decrypter to decrypt a value. With the Spring Cloud configuration server developed in [config-server](#config-server), the
symmetric encryption key is a string of characters you select that is passed to the service via an operating system environment variable called
**ENCRYPT_KEY**. For those microservices, I have used:

```
ENCRYPT_KEY=ENCRYPT_KEY
```
<br><br>


### JDK and Oracle JCE

If you are using [Oracle JDK](https://www.oracle.com/java/technologies/downloads) instead of [OpenJDK](https://openjdk.org), you need to download and
install Oracle's Unlimited Strength Java Cryptography Extension (JCE). This isn't available through Maven and must be downloaded from Oracle Corporation.
Once you've downloaded the zip files containing the JCE jars, you must do the following:

- Locate your `$JAVA_HOME/jre/lib/security` directory

- Back up the `local_policy.jar` and `US_export_policy.jar` files in the `$JAVA_HOME/jre/lib/security` directory to a different location.

- Unzip the JCE zip file you downloaded from Oracle

- Copy the `local_policy.jar` and `US_export_policy.jar` to your `$JAVA_HOME/jre/lib/security` directory.
  <br><br>


### Problems resolution

If you receive some errors related to encryption like:

```
IllegalStateException: Cannot decrypt: ...
```

Please, take a look to the previous steps in this section, maybe one of them is missing. If you still see same error messages, the best way to solve
it is changing the *cipher values* added in the microservices configuration files included in:

* [Spring6Microservices_ConfigServerData](https://github.com/doctore/Spring6Microservices_ConfigServerData)

Like:

```
spring:
  datasource:
    # Raw password: spring6
    password: "{cipher}c8e1f3a8e0f5d7246a0dcbe620b97de51b580a1ef16f80ffafd3989920287278"
```

And something similar in the database table [security.application_client_details](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V1__application_client_details_table.sql),
in the columns: `signature_secret` and `encryption_secret`.

To do it:

- Run [registry-server](#registry-server) and [config-server](#config-server)

- Encrypt required values using the provided endpoint for that purpose, as follows:

![Alt text](/documentation/config-service/Encryption.png?raw=true "Encryption endpoint")

- Overwrite current values by the provided ones.
<br><br>



## Security services

As you read previously, there are two different microservices you can use to manage the authentication/authorization functionality: [security-oauth-service](#security-oauth-service)
and [security-custom-service](#security-custom-service).

Regarding every microservice, in this section I will explain the web services provided by everyone and how to use them, starting by [security-oauth-service](#security-oauth-service).
<br><br>


### security-oauth-service endpoints

Before entering in details about this security service, it is important to know that, for every request we have to include the *Basic Auth* Oauth 2.0 credentials,
based on the application configured in the database table: [security.oauth2_registered_client](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V3__oauth2_registered_client_table.sql).

In the next pictures I will use the predefined one:
[Spring6Microservices](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V3__oauth2_registered_client_table.sql#L25)

![Alt text](/documentation/security-oauth-service/Credentials.png?raw=true "Basic Auth credentials")
<br><br>

So, the list of web services is the following one:

**1.** Get the authorization code using [PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/) approach (<ins>1st request</ins>), pasting in a browser
a request similar to:

```
http://localhost:8181/security/oauth/authorize?response_type=code&client_id=Spring6Microservices&scope=openid&redirect_uri=http://localhost:8181/security/oauth/authorized&code_challenge=jZae727K08KaOmKSgOaGzww_XVqGr_PKEgIMkjrcbJI&code_challenge_method=S256
```

Providing:

* `response_type=code` to specify to the authorization server that the client wants to use the authorization code grant type.
* `client_id=Spring6Microservices` the client's identifier added in [security.oauth2_registered_client](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V3__oauth2_registered_client_table.sql).
* `scope=openid` indicates which scope the client wants to be granted with this authentication attempt.
* `redirect_uri=http://localhost:8181/security/oauth/authorized` specifies the URI to which the authorization server will redirect after a successful authentication. This URI must be one of those previously configured for the current client.
* `code_challenge=jZae727K08KaOmKSgOaGzww_XVqGr_PKEgIMkjrcbJI` if using the authorization code enhanced with [PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/), the hash value of the verifier that must be provided in the second request.
* `code_challenge_method=S256` hashing method has been used to create the challenge from the verifier. In this case, S256 means [SHA-256](https://en.wikipedia.org/wiki/SHA-2).

The browser will redirect you to the login page:

![Alt text](/documentation/security-oauth-service/Login.png?raw=true "Login page")

In the previous image I used `admin/admin` however there is another option: `user/user`, included in the SQL file [security.spring6microservice_user](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V2__spring6microservice_security_tables.sql#L54).

We configured a no existing page as `redirect_uri` for that reason we receive a **404** as response after the successful login however, the
[Spring Authorization Server](https://spring.io/projects/spring-authorization-server) returns a valid authorization code we will be able to use
in the second request of [PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/):

![Alt text](/documentation/security-oauth-service/AuthorizationCodeResponse.png?raw=true "Authorization code")
<br><br>

**2.** Get the authentication information using [PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/) approach (<ins>2nd request</ins>), using above authorization code:

![Alt text](/documentation/security-oauth-service/LoginToken.png?raw=true "Get authentication information")

Providing:

* `client_id=Spring6Microservices` the client's identifier added in [security.oauth2_registered_client](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V3__oauth2_registered_client_table.sql).
* `redirect_uri=http://localhost:8181/security/oauth/authorized` specifies the URI to which the authorization server will redirect after a successful authentication. This URI must be one of those previously configured for the current client.
* `grant_type=authorization_code` shows which flow the client uses to request the access token.
* `code=...` the value of the authorization code the authorization server provided to the client (returned as `code` query parameter in the previous request).
* `code_verifier=123456` the verifier based on which the challenge that the client sent at authorization was created (`code_challenge` & `code_challenge_method` parameters).
<br><br>

**3.** Once the *access* token has expired, return new authentication information using *refresh* token:

![Alt text](/documentation/security-oauth-service/RefreshToken.png?raw=true "Refresh token")
<br><br>

**4.** Get authorization information using *access* token:

![Alt text](/documentation/security-oauth-service/IntrospectToken.png?raw=true "Introspect token")
<br><br>

**5.** Revoke the token using the *access* one:

![Alt text](/documentation/security-oauth-service/RevokeToken.png?raw=true "Revoke token")

So, if we try to get again its internal information, this is the new response:

![Alt text](/documentation/security-oauth-service/IntrospectInvalidToken.png?raw=true "Introspect invalid token")
<br><br>


### security-custom-service endpoints

Before entering in details about this security service, it is important to know that, for every request we have to include the *Basic Auth* credentials,
based on the application configured in the database table: [security.application_client_details](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V1__application_client_details_table.sql).

In the next pictures I will use the predefined one:
[Spring6Microservices](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V1__application_client_details_table.sql#L24)

![Alt text](/documentation/security-custom-service/Credentials.png?raw=true "Basic Auth credentials")
<br><br>

This microservice provides 2 different authentication flows:

* **Traditional:** the request contains the *user's credentials* and returns the full authentication response.
* **[PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/)** with 2 requests:
  - The first one to send *challenge* data and receive the *authorization code*.
  - The second one to send *user's credentials*, *authorization code* and *verifier* and returns the full authentication response.

So, the list of web services is the following one: 

**1.** Get the authentication information using **traditional** approach:

![Alt text](/documentation/security-custom-service/DirectLogin.png?raw=true "Direct Login")

In the previous image I used `admin/admin` however there is another option: `user/user`, included in the SQL file [security.spring6microservice_user](https://github.com/doctore/Spring6Microservices/blob/main/sql/changelog/V2__spring6microservice_security_tables.sql#L54).
<br><br>

**2.** Get the authorization code using [PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/) approach (<ins>1st request</ins>):

![Alt text](/documentation/security-custom-service/LoginAuthorized.png?raw=true "Direct Login")
<br><br>

**3.** Get the authentication information using [PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/) approach (<ins>2nd request</ins>):

![Alt text](/documentation/security-custom-service/LoginToken.png?raw=true "Get authentication information")
<br><br>

**4.** Once the *access* token has expired, return new authentication information using *refresh* token:

![Alt text](/documentation/security-custom-service/RefreshToken.png?raw=true "Refresh token")
<br><br>

**5.** Get authorization information using *access* token:

![Alt text](/documentation/security-custom-service/CheckToken.png?raw=true "Authorization information")
<br><br>

**6.** Logs out a user related with an application:

![Alt text](/documentation/security-custom-service/Logout.png?raw=true "Log out")

So, if we try to get again its internal information, this is the new response:

![Alt text](/documentation/security-custom-service/CheckTokenOfBlacklisted.png?raw=true "Pair application-user were blacklisted")

The user must complete any of the provided login flows to resubmit new requests.
<br><br>



## Rest API documentation

The following microservices have a well-documented Rest API:

* [invoice-service](#invoice-service)
* [order-service](#order-service)
* [security-custom-service](#security-custom-service)
* [security-oauth-service](#security-oauth-service)

[Swagger](https://swagger.io) has been used in all cases, however for a better an easier integration with Spring Framework, the library used is:

* **[Springdoc-OpenApi](https://springdoc.org)** both in every documented microservice and the [gateway-server](#gateway-server).

To facilitate access to this documentation, we can use the [gateway-server](#gateway-server) URL. On that way, using the upper selector: *Select a definition*,
we will be able to choose between all existing microservices.

Using **dev** profile, the url to access them is `http://localhost:5555/swagger-ui/index.html`

![Alt text](/documentation/invoice-service/SwaggerDocumentation.png?raw=true "Invoice Service Swagger documentation")
<br><br>
![Alt text](/documentation/order-service/SwaggerDocumentation.png?raw=true "Order Service Swagger documentation")
<br><br>
![Alt text](/documentation/security-custom-service/SwaggerDocumentation.png?raw=true "Security Custom Service Swagger documentation")
<br><br>
![Alt text](/documentation/security-oauth-service/SwaggerDocumentation.png?raw=true "Security Oauth Service Swagger documentation")
<br><br>


### Endpoint definitions

Configured to use the application [Bruno](https://www.usebruno.com/), several collections were created to use the provided endpoints. You can them [here](https://github.com/doctore/Spring6Microservices/tree/main/documentation/BrunoCollections).
<br><br>



## gRPC communication

Besides the REST API developed in:

* [order-service](#order-service)
* [invoice-service](#invoice-service)

In this project has been added a [gRPC](https://grpc.io/docs/what-is-grpc/introduction/) communication channel between:

* **gRPC server:** in [order-service](https://github.com/doctore/Spring6Microservices/tree/main/order-service/src/main/java/com/order/grpc)
* **gRPC client:** in [invoice-service](https://github.com/doctore/Spring6Microservices/tree/main/invoice-service/src/main/java/com/invoice/grpc)

Both use the same approach to run server and client instances:

* The instance definition:
  - [Server instance](https://github.com/doctore/Spring6Microservices/blob/main/order-service/src/main/java/com/order/grpc/server/GrpcServer.java)
  - [Client instance](https://github.com/doctore/Spring6Microservices/blob/main/invoice-service/src/main/java/com/invoice/grpc/client/GrpcClient.java)
<br>

* The Spring functionality used to run it:
  - [Server runner](https://github.com/doctore/Spring6Microservices/blob/main/order-service/src/main/java/com/order/grpc/server/GrpcServerRunner.java)
  - [Client runner](https://github.com/doctore/Spring6Microservices/blob/main/invoice-service/src/main/java/com/invoice/grpc/client/GrpcClientRunner.java)
<br>


### Security in gRPC

The internal communication between a microservice and its related security server, that is:

* [order-service](#order-service) => [security-oauth-service](#security-oauth-service)
* [invoice-service](#invoice-service) => [security-custom-service](#security-custom-service)

Uses [Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication) to include the required credentials:

* [order-service](#order-service) in the class [CustomAuthoritiesOpaqueTokenIntrospector](https://github.com/doctore/Spring6Microservices/blob/main/order-service/src/main/java/com/order/configuration/security/oauth/CustomAuthoritiesOpaqueTokenIntrospector.java)
* [invoice-service](#invoice-service) in the class [CustomAuthenticationManager](https://github.com/doctore/Spring6Microservices/blob/main/invoice-service/src/main/java/com/invoice/configuration/security/CustomAuthenticationManager.java)

In the current gRPC development I have followed the same approach:

* In the **gRPC client** creating a new [BasicCredential](https://github.com/doctore/Spring6Microservices/blob/main/grpc-api/src/main/java/com/spring6microservices/grpc/security/BasicCredential.java)
instance and adding it in the `Authorization` header sent to the server, using the method `buildCallCredentials` of the class [GrpcClient](https://github.com/doctore/Spring6Microservices/blob/main/invoice-service/src/main/java/com/invoice/grpc/client/GrpcClient.java).

* In the **gRPC server**, the interceptor [AuthenticationInterceptor](https://github.com/doctore/Spring6Microservices/blob/main/order-service/src/main/java/com/order/grpc/interceptor/AuthenticationInterceptor.java)
manages required verifications.
<br><br>


### Request identifier in gRPC

This project uses [Spring tracing](https://docs.spring.io/spring-boot/reference/actuator/tracing.html) through [Micrometer tracing](https://docs.micrometer.io/tracing/reference/) for distributed tracing,
to simulate the same behaviour two interceptors have been defined:

* At **gRPC server** with [RequestIdInterceptor](https://github.com/doctore/Spring6Microservices/blob/main/order-service/src/main/java/com/order/grpc/interceptor/RequestIdInterceptor.java)
* At **gRPC client** with [RequestIdInterceptor](https://github.com/doctore/Spring6Microservices/blob/main/invoice-service/src/main/java/com/invoice/grpc/interceptor/RequestIdInterceptor.java)
<br><br>


### gRPC example request

Everytime [invoice-service](#invoice-service) returns an invoice details searched by its identifier or code, will send a request to [order-service](#order-service) using a [gRPC](https://grpc.io/docs/what-is-grpc/introduction/)
communication channel to get its order and order lines data. 

The communication diagram including also the invocation of [security-oauth-service](#security-oauth-service) is the following:

![Alt text](/documentation/GrpcCommunicationDiagram.png?raw=true "gRPC Communication diagram")
<br>

So, as I explained you in [security-custom-service endpoints](#security-custom-service-endpoints), once you have obtained the required JWT access token, you can use it to invoke the web service that uses the developed
gRPC channel:

![Alt text](/documentation/invoice-service/WebServiceWithGRPC.png?raw=true "Example of web service using gRPC channel")
<br><br>



## Native images

Native Image is a technology to compile Java code ahead-of-time to a binary – a native executable. A native executable includes only the code required at run time, that is the application classes,
standard-library classes, the language runtime, and statically-linked native code from the JDK.

An executable file produced by Native Image has several important advantages, in that it:

* Uses a fraction of the resources required by the Java Virtual Machine, so is cheaper to run.
* Starts in milliseconds
* Delivers peak performance immediately, with no warmup.
* Can be packaged into a lightweight container image for fast and efficient deployment.
<br><br>


### Install and configure GraalVM JDK 

Download the required GraalVM JDK from [here](https://www.graalvm.org/downloads/), in this project I use Java 21. Although there are several options to manage different JDKs such as [SDKMAN](https://sdkman.io/),
I have followed the instructions included in the [webpage](https://medium.com/@nickkalgin/installing-graalvm-jdk-on-ubuntu-ee787c19e0cf) to install and configure GraalVM JDK in Ubuntu.

In summary, the steps are:

**1.** Unpack the JDK in `/usr/lib/jvm/java-21-graalvm-jdk-amd64` and navigate to `/usr/lib/jvm/` to work on the next points. 
<br>

**2.** Create a symbolic link that points to the GraalVM JDK 21 directory:

```
ln -s java-21-graalvm-jdk-amd64 /usr/lib/jvm/java-1.21.0-graalvm-jdk-amd64
```
<br>

**3.** Prepare a descriptor file providing the information about binaries to the [update-alternative](https://man7.org/linux/man-pages/man1/update-alternatives.1.html) utilities, creating the new file
`.java-1.21.0-openjdk-amd64.jinfo` with:

```
name=java-21-graalvm-amd64
alias=java-1.21.0-graalvm-amd64
priority=2102
section=main

hl java /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/java
hl jpackage /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jpackage
hl keytool /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/keytool
hl rmiregistry /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/rmiregistry
hl jexec /usr/lib/jvm/java-21-graalvm-jdk-amd64/lib/jexec
jdkhl jar /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jar
jdkhl jarsigner /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jarsigner
jdkhl javac /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/javac
jdkhl javadoc /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/javadoc
jdkhl javap /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/javap
jdkhl jcmd /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jcmd
jdkhl jdb /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jdb
jdkhl jdeprscan /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jdeprscan
jdkhl jdeps /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jdeps
jdkhl jfr /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jfr
jdkhl jimage /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jimage
jdkhl jinfo /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jinfo
jdkhl jlink /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jlink
jdkhl jmap /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jmap
jdkhl jmod /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jmod
jdkhl jps /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jps
jdkhl jrunscript /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jrunscript
jdkhl jshell /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jshell
jdkhl jstack /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jstack
jdkhl jstat /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jstat
jdkhl jstatd /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jstatd
jdkhl jwebserver /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jwebserver
jdkhl serialver /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/serialver
jdkhl jhsdb /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jhsdb
jdk jconsole /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/jconsole
jdk native-image /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/native-image
jdk native-image-configure /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/native-image-configure
jdk native-image-inspect /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/native-image-inspect
```
<br>

**4.** Configure [update-alternative](https://man7.org/linux/man-pages/man1/update-alternatives.1.html) to use executables from the GraalVM JDK, creating the new file
`java-21-graalvm-jdk-amd64_alternatives-install` with:

```
for path in /usr/lib/jvm/java-21-graalvm-jdk-amd64/bin/*; do
    name=$(basename $path)
    update-alternatives --install /usr/bin/$name $name $path 2102 \
    --slave /usr/share/man/man1/$name.1.gz $name.1.gz /usr/lib/jvm/java-21-graalvm-jdk-amd64/man/man1/$name.1
done

update-alternatives --install /usr/bin/jexec jexec /usr/lib/jvm/java-21-graalvm-jdk-amd64/lib/jexec 2102
```
<br>

**5.** Invoke the new file:

```
./java-21-graalvm-jdk-amd64_alternatives-install
```
<br>

**6.** Switch to the brand new GraalVM JDK using the update-java-alternatives:

```
update-java-alternatives -s java-1.21.0-graalvm-jdk-amd64
```
<br>

If everything went well, then we should watch something like:

```
java --version
java 21.0.6 2025-01-21 LTS
Java(TM) SE Runtime Environment Oracle GraalVM 21.0.6+8.1 (build 21.0.6+8-LTS-jvmci-23.1-b55)
Java HotSpot(TM) 64-Bit Server VM Oracle GraalVM 21.0.6+8.1 (build 21.0.6+8-LTS-jvmci-23.1-b55, mixed mode, sharing)
```
<br>


### security-custom-service native

Once we have configured the GraalVM JDK, the required changes/considerations in [security-custom-service](#security-custom-service) are:

**1.** Create the new [application-native.yml](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/resources/application-native.yml)
file to include the specific configuration for native images.
<br><br>

**2.** Add the [configuration files](https://github.com/doctore/Spring6Microservices/tree/main/security-custom-service/src/main/resources/META-INF/native-image) based on the project's functionality.

To know how to generate all the [configuration archives](https://stackoverflow.com/questions/76747716/how-to-register-method-for-runtime-reflection-with-graalvm) in this microservice,
package the project with maven using **native** profile and invoke:

```
java -Dspring.aot.enabled=true -agentlib:native-image-agent=config-output-dir=./native-config -jar target/security-custom-service-1.0.0.jar
```

You can use all the new files located in `native-config` folder, to include in the project only the required ones.
<br><br>

**3.** Update [pom.xml](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/pom.xml) to add, at least, the **native** profile. Not all the dependencies are available
to work with native images, you can check the list [here](https://github.com/oracle/graalvm-reachability-metadata/tree/master/metadata).
<br><br>

**4.** Create a new [maven](https://maven.apache.org/) configuration to compile the project and generate the native image as executable file:

![Alt text](/documentation/security-custom-service/ConfigureNativeCompilation.png?raw=true "Native image compilation")
<br><br>

**5.** Invoke the generated executable archive:

```
./target/security-custom-service --spring.profiles.active=native
```
<br>

If everything went well, then the application will run smoothly and the endpoints will respond to the request normally. Comparing both options: **local** and **native**
you will be able to notice an important improvement in the performance:

* **local:** more than 6 seconds.

![Alt text](/documentation/security-custom-service/LocalProfile.png?raw=true "Local run")
<br><br>

* **native:** less than 3 seconds.

![Alt text](/documentation/security-custom-service/NativeProfile.png?raw=true "Native run")