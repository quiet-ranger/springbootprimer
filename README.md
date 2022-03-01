These are the steps to arrive at the working example included in chapter-001

# Prerequisites:
1. JDK 11 or later
2. Maven 3.2+
3. Visual Studio Code (or equivalent IDE)

# Steps
1. Head to https://start.spring.io/ and make the following selections:

    Project Type: Maven
    Language: Java
    Spring Boot: 2.7.0 (M2)
    Group: com.example
    Artifact: hello
    Name: hello
    Package name: com.example.hello
    Packaging: war
    Java: 11
    Dependencies: Spring Web

2. Click on generate and save the file locally (will be named hello.zip)

3. Unzip to your project directory, say ~/springbootprimer

4. Enter IDE 

    $ cd ~/springbootprimer/hello
    $ code .

5. Create the following additional classes which are not generated:

  1. A resource representation class, a regular POJO, under com.example.hello.controolers.Greeting.java
  2. A controller com.example.hello.controllers.GreetingController.java

Contrary to what is instructed here https://spring.io/guides/gs/rest-service/, it is important that these two
new classes are created on the directory where the main SpringBootApplication, HelloApplication, exists or a 
subdirectory of it.  If you don't do this, at runtime the controller cannot be found.

Greeting.java will be automatically converted to JSON on the response by the Jackson JSON library.

GreetingController.java is the resource controller, the component that will
actually receive the requests because it is marked with the annocation @RestController.  Its greeting method both contains a path mapping and the specification for an optional query parameter with a default value.

6. Open a terminal and run:

    ./mvnw clean package
    ./mvnw spring-boot:run

7. Once the server is ready (you should see "Completed Initialization" on the console) you can try accessing from a browser:

https://www.example.com:8080/greeting

If you are running Google Chrome Version 98.0.4758.102 (Official Build) (64-bit) or later, the request will fail for one of two reasons:

  1. The DNS cannot resolve www.example.com to your localhost
  2. The server does not contain a valid SSL certificate

8. To fix the first of the two problems, add the following to /etc/hosts

    127.0.0.1       www.example.com

9. To fix the second problem you must create a self certificate

    $ cd ~/springbootprimer/hello/src/main/resources
    $ keytool -keystore server.p12 -storepass 123456 -alias jj -deststoretype pkcs12 -genkeypair -keyalg RSA -validity 3650 -ext san=ip:www.example.com -keysize 2048

    What is your first and last name?
      [Unknown]:  Joe Bloggs
    What is the name of your organizational unit?
      [Unknown]:  Trespassers
    What is the name of your organization?
      [Unknown]:  Global
    What is the name of your City or Locality?
      [Unknown]:  Madrid
    What is the name of your State or Province?
      [Unknown]:  Madrid
    What is the two-letter country code for this unit?
      [Unknown]:  ES
    Is CN=Joe Bloggs, OU=Trespassers, O=Global, L=Madrid, ST=Madrid, C=ES > correct?
      [no]:  yes

Check certificate is valid:

    $ keytool -list -v -storetype pkcs12 -keystore server.p12

Then add the following entries to application.properties

    server.ssl.key-store-type=PKCS12
    server.ssl.key-store=classpath:server.p12
    server.ssl.key-store-password=123456
    server.ssl.key-alias=tomcat

10. Rebuild and restart

    $ ./mvnw clean package
    $ ./mvnw spring-boot:run

This time Chrome will warn you that it cannot verify the validity of the certificate but it will offer you the Advanced button from where you can click on “Proceed to www.example.com”

Both of:
https://www.example.com:8080/greeting
https://www.example.com:8080/greeting?name=Joe

Should now work.
