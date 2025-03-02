# Spring6Microservices

**--- UNDER CONSTRUCTION ---**

- [Why was this project created?](#why-was-this-project-created)
- [Elements included in this project](#elements-included-in-this-project)
    - [registry-server](#registry-server)
  - [config-server](#config-server)
  - [gateway-server](#gateway-server)
  - [security-custom-service](#security-custom-service)
  - [common-core](#common-core)
  - [common-spring](#common-spring)
  - [sql](#sql)
- [Previous steps](#previous-steps)
- [Security services](#security-services)
  - [security-custom-service endpoints](#security-custom-service-endpoints) 
- [Rest API documentation](#rest-api-documentation)



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

sending the information encrypted and delegating in every microservice the labour of decrypt it. That is the reason to include in their *pom.xml*
files, the dependency:

```
<dependency>
   <groupId>org.springframework.security</groupId>
   <artifactId>spring-security-rsa</artifactId>
   <version>${spring-security-rsa.version}</version>
</dependency>
```
<br><br>


### [gateway-server](https://github.com/doctore/Spring6Microservices/tree/main/gateway-server)

Using [Spring Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html), this is the gateway implementation used by the other
microservices included in this proof of concept. This module contains a filter to registry every web service invoked, helping to debug each request.
<br><br>


### [security-custom-service](https://github.com/doctore/Spring6Microservices/tree/main/security-custom-service)

Based on JWT token, this module was created to centralize the management of authentication/authorization functionalities. Its main purpose is provided
a completely multi-application platform to generate/manage their own access and refresh tokens (including additional information), choosing from the options
defined in [TokenType](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/java/com/security/custom/enums/token/TokenType.java):

* JWS
* JWE
* ENCRYPTED_JWS
* ENCRYPTED_JWE

The provided algorithms to sign the JWT tokens, that is, generates **JWS** or the internal one inside **ENCRYPTED_JWS** are located in [TokenSignatureAlgorithm](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/java/com/security/custom/enums/token/TokenSignatureAlgorithm.java).
On the other hand, the available algorithms selecting **JWE** or **ENCRYPTED_JWE** are defined in [TokenEncryptionAlgorithm](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/java/com/security/custom/enums/token/TokenEncryptionAlgorithm.java) 

Every application will be able to manage its own token configuration/generation adding a new row in the database table: **security.application_client_details**
and including/developing a new constants in [SecurityHandler](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/java/com/security/custom/enums/SecurityHandler.java).

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
* **controller** REST Api using Webflux.

On the other hand, there are other important folders:

* **configuration** with several classes used to manage several areas such: security, exception handlers, cache, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util** to manage the JWS/JWE functionality.

Regarding **[Flyway](https://www.red-gate.com/products/flyway/)**, using [application.yml](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/resources/application.yml) the project has been configured to avoid invoking it
when this microservice is launched or packaged:

```
spring:
  flyway:
    enabled: false
```

So, if you want to manage it manually, you can create a new [maven](https://maven.apache.org/) configuration. The next picture displays how to do it
using [IntelliJ IDEA](https://www.jetbrains.com/idea/):

![Alt text](/documentation/ConfigureFlywayInSecurityCustomService.png?raw=true "Configure Flyway")
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


A common use of Either is as an alternative to scala.Option for dealing with possibly missing values.


### [common-spring](https://github.com/doctore/Spring6Microservices/tree/main/common-spring)

Maven project that includes common code specifically related with Spring framework, used in several microservices. It contains different useful helper classes like:

* [HttpUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/util/HttpUtil.java)
* [JsonUtil](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/util/JsonUtil.java)

Improvements to the functionality provided by default for managing the database:

* [ExtendedJpaRepository](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/repository/ExtendedJpaRepository.java)
* [ExtendedQueryDslJpaRepository](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/repository/ExtendedQueryDslJpaRepository.java)

Wrapper to manage a cache, regardless of the selected implementation: 

* [CacheService](https://github.com/doctore/Spring6Microservices/blob/main/common-spring/src/main/java/com/spring6microservices/common/spring/service/CacheService.java)

New validator using **Enums** to map database values in Hibernate POJOs, allowing to verify internal properties:

* [Enum validator](https://github.com/doctore/Spring6Microservices/tree/main/common-spring/src/main/java/com/spring6microservices/common/spring/validator/enums)

Common DTOs to send/receive authentication, authorization data and/or handle errors invoking endpoints:

* [Common DTOs](https://github.com/doctore/Spring6Microservices/tree/main/common-spring/src/main/java/com/spring6microservices/common/spring/dto)
<br><br>


### [sql](https://github.com/doctore/Spring6Microservices/tree/main/sql)

With SQL files included in the database, just to expose the initial steps/changes needed to start working with these microservices.
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

If you receive some errors related with encryption like:

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

And something similar in the database table `security.application_client_details`, in the column `signature_secret`.

To do it:

- Run [registry-server](#registry-server) and [config-server](#config-server)

- Encrypt required values using the provided endpoint for that purpose, as follows:

![Alt text](/documentation/Encryption.png?raw=true "Encryption endpoint")

- Overwrite current values by the provided ones.
<br><br>



## Security services

As you read previously, there are two different microservices you can use to manage the authentication/authorization functionality: [security-oauth-service](#security-oauth-service) (*PENDING TO DEVELOP*)
and [security-custom-service](#security-custom-service).

Regarding every microservice, in this section I will explain the web services provided by every one and how to use them, starting by [security-custom-service](#security-custom-service).
<br><br>


### security-custom-service endpoints

Before enter in details about this security service, it is important to know that, for every request we have to include the *Basic Auth* credentials, based in
the application configured in the database table: **security.application_client_details**.

In the next pictures I will use the predefined one:
[Spring6Microservices](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/resources/db/changelog/V3__security_schema_data.sql#L4)

![Alt text](/documentation/SecurityCustomService_Credentials.png?raw=true "Basic Auth credentials")

This microservice provides 2 different authentication flows:

* **Traditional:** the request contains the user's credentials and returns the full authentication response.
* **[PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/)** with 2 requests:
  - The first one to send *challenge* data and receive the *authorization code*.
  - The second one to send *user's credentials*, *authorization code* and *verifier* and returns the full authentication response.

So, the list of web services is the following one: 

**1.** Get the authentication information using *traditional* approach:

![Alt text](/documentation/SecurityCustomService_DirectLogin.png?raw=true "Direct Login")

In the previous image, I have used for this example `admin/admin`, there is another option: `user/user`, included in the SQL file
[security.spring6microservice_user](https://github.com/doctore/Spring6Microservices/blob/main/security-custom-service/src/main/resources/db/changelog/V3__security_schema_data.sql#L35).
<br>

**2.** Get the authorization token using [PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/) approach (<ins>1st request</ins>):

![Alt text](/documentation/SecurityCustomService_LoginAuthorized.png?raw=true "Direct Login")
<br>

**3.** Get the authentication information using [PKCE (Proof of Key Code Exchange)](https://oauth.net/2/pkce/) approach (<ins>2nd request</ins>):

![Alt text](/documentation/SecurityCustomService_LoginToken.png?raw=true "Get authentication information")
<br><br>

**4.** Once the *access* token has expired, return new authentication information using *refresh* token:

![Alt text](/documentation/SecurityCustomService_RefreshToken.png?raw=true "Refresh token")
<br>

**5.** Get authorization information using *access* token:

![Alt text](/documentation/SecurityCustomService_CheckToken.png?raw=true "Authorization information")
<br><br>



## Rest API documentation

The following microservices have a well documented Rest API:

* [security-custom-service](#security-custom-service)

[Swagger](https://swagger.io) has been used in all cases, however for a better an easier integration with Spring Framework, the library used is:

* **[Springdoc-OpenApi](https://springdoc.org)** both in every documented microservice and the [gateway-server](#gateway-server).

To facilitate access to this documentation, we can use the [gateway-server](#gateway-server) URL. On that way, using the upper selector: *Select a definition*,
we will be able to choose between all existing microservices.

In local (**dev** profile), the url to access them is `http://localhost:5555/swagger-ui/index.html`

![Alt text](/documentation/Swagger.png?raw=true "Swagger documentation")
<br><br>