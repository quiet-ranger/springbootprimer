These are the steps to arrive at the working example included in chapter-002. It is a subset adaptation of [this tutorial](https://spring.io/guides/tutorials/rest/) as available on March 2022.

In a nutshell, we implement a minimal set of RESTful services (without HATEOAS) whose entities are persisted using JPA using an in memory database.


# Prerequisites:
1. JDK 11 or later
2. Maven 3.2+
3. Visual Studio Code (or equivalent IDE)
4. Step 9 from chapter 001 was followed to generate and install server.p12
5. The following versions of Spring and H2:

        com.h2database:h2:jar:2.1.210
        org.springframework:spring-core:jar:5.3.16
        org.springframework:spring-test:jar:5.3.16
        org.springframework.data:spring-data-jpa:jar:2.7.0-M3
        org.springframework.boot:spring-boot-starter-data-jpa:jar:2.7.0-M2
        org.springframework.boot:spring-boot-starter-web:jar:2.7.0-M2
        org.springframework.boot:spring-boot-starter-test:jar:2.7.0-M2

Beware that there have been many changes to the API and, in fact, the main reason I decided to put together this tutorial was because the original did not mention versions and nothing worked.  I made all necessary adjustments to make everything work with the specific versions I list above.  They may or may not work with different versions.

# Steps
1. Head to https://start.spring.io/ and make the following selections:

        Project Type: Maven
        Language: Java
        Spring Boot: 2.7.0 (M2)
        Group: com.example
        Artifact: chapter-002
        Name: chapter-002
        Package name: com.example.hello
        Packaging: war
        Java: 11
        Dependencies: Spring Web, Spring Data JPA, H2 Database

2. Click on generate and save the file locally (will be named chapter-002.zip)

3. Unzip to your project directory, say ~/springbootprimer

4. Enter Visual Studio Code

        $ cd ~/springbootprimer
        $ code .

5. Select File / Save Workspace As

This will result on a new file being created `springbootprimer.code-workspace.json`, which needs to be edited to contain the following:

        {
            "folders": [
                {
                    "path": "./chapter-001"
                },
                {
                    "path": "./chapter-002"
                }
            ],
            "settings": {}
        }

This creates a multi-root workspace, effectively allowing independent projects to co-exist and, potentially, establish a hierarchy of dependencies amongst them        

6. Create the following additional classes which are not generated:

+ __Employee__ class is the entity to be persisted, using the same approach as in chapter-001. It will be automatically converted to JSON on the response by the Jackson JSON library
+ __EmployeeRepository__ is the magic glue that causes __Employee__ to be persisted
+ __PayrollApplication__ is marked with @SpringBootApplication and it becomes the entrypoint for SpringBoot to introspect and wire up the application
+ __LoadDatabase__ is a convenience class to pre-load some data to the database but it also demonstrates how initialisation tasks in general can be handled.  In a nutshell, Spring will execute every CommandLineRuner inside a @Bean once the application context is loaded
+ __EmployeeController__ is the class that will directly handle HTTP requests just as in chapter-001
+ __EmployeeNotFoundException__ is used to customise the error message we would like to emit under a specific scenario. It will be further wrapped a processed by __EmployeeNotFoundAdvice__
+ __EmployeeNotFoundAdvice__ is responsible for converting a specific exception into an HTTP error code of our choice.  Note that it also gives us an opportunity to further process the message passed back

Contrary to what is instructed here https://spring.io/guides/gs/rest-service/, it is important that these new classes are created on the directory where the main SpringBootApplication, __PayrollApplication__, exists or a subdirectory of it.  If you don't do this, at runtime the controller cannot be found.

7. Open a terminal and run:

        ./mvnw clean package
        ./mvnw spring-boot:run

8. Once the server is ready (you should see "Completed Initialization" on the console) you can try accessing from a browser:

https://www.example.com:8080/employees
https://www.example.com:8080/employees/2

        {
                "id": 2,
                "name": "Mr Lighthand",
                "role": "thief"
        }

For more complex interactions, consider using [Insomnia](https://insomnia.rest/download).  If you use Insomnia, make sure you untick the checkbox "Validate Certificates" in Settings.

The original tutorial does not consider these services RESTful because the responses do not include hyperlinks to interact further with the entities.  That might well be the case if you stick to a very strict definition. 

My personal opinion on this one is that implementing HATEOAS, see chapter-003, is an interesting and powerful technique but is is also incomplete.  Note how every link returned is for a specific action that neither has a body nor further query parameters.  Unless an incredibly small subset of such combinations can be specified upfront, there is no practical way we could construct endless lists of possible actions for an entity.