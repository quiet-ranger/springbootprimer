# Chapter 3

This is a subset adaptation of [this tutorial](https://spring.io/guides/tutorials/rest/) as available on March 2022.

In a nutshell, we enhance/modify what was done in Chapter 2 in order to implement HATEOAS and therefore enable 
[HAL based clients] /https://stateless.group/hal_specification.html).  According to Roy Fielding, this is a mandatory 
step in order for your service to be considered RESTful although I really wonder how many people actually agree with 
this strict definition.

# Prerequisites:
1. Same as for Chapter 2
2. HATEOAS version 1.5.0-M2

In order to chech the versions of the various components being pulled by Maven if a specific one is not given you can use the following command:

        $ ./mvnw dependency:tree



# Steps
1. Head to https://start.spring.io/ and make the following selections:

        Project Type: Maven
        Language: Java
        Spring Boot: 2.7.0 (M2)
        Group: com.example
        Artifact: chapter-003
        Name: chapter-003
        Package name: com.example.hello
        Packaging: jar
        Java: 11
        Dependencies: Spring Web, Spring Data JPA, H2 Database, HATEOAS

2. Click on generate and save the file locally (will be named chapter-003.zip)

3. Unzip to your project directory, say ~/springbootprimer

4. Enter your favourite IDE and import the project

6. Create the following additional classes which are not generated:

+ Same as created for chapter-002, except for `LoadDatabase` as the functionality was moved inside `PayrollApplication` to 
demonstrate that this is also possible/acceptable
+ __EmployeeModelAssembler__ has a convenience method to generate HATEOAS links

Notice that the biggest change actually happens in __EmployeeController__ as no longer return the original entities but 
various wrappers from the HATEOAS package.


7. Open a terminal and run:

        ./mvnw clean package
        ./mvnw spring-boot:run

8. Once the server is ready (you should see "Completed Initialization" on the console) you can try accessing from a browser:

https://www.example.com:8080/employees

        {
            "_embedded": {
                "employeeList": [
                    {
                        "id": 1,
                        "name": "Joe Bloggs",
                        "role": "burglar",
                        "_links": {
                            "self": {
                                "href": "https://www.example.com:8080/employees/1"
                            },
                            "employees": {
                                "href": "https://www.example.com:8080/employees"
                            }
                        }
                    },
                    {
                        "id": 2,
                        "name": "Mr Lighthand",
                        "role": "thief",
                        "_links": {
                            "self": {
                                "href": "https://www.example.com:8080/employees/2"
                            },
                            "employees": {
                                "href": "https://www.example.com:8080/employees"
                            }
                        }
                    }
                ]
            },
            "_links": {
                "self": {
                        "href": "https://www.example.com:8080/employees"
                }
            }
        }