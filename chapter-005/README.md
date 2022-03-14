# Chapter 5

Inspired and adapted from [this tutorial](https://spring.io/guides/gs/messaging-redis/) as it was published in March 2022.

Asynchronous communication is a common pattern used to deal with consumers that are
or can be slower than the producers.  There are plenty of so called message oriented
middleware, MOM, to facilitate implementations.  Redis, at first, does not appear to 
be an obvious choice as it was originally designed as an memory cache. However, it 
does include a pub/sub capability and this example demonstrates it.

Another common pattern used in modern systems is to use JSON to represent entities.  
We take the opportunity here to demonstrate how to use the jackson library to convert 
from objects to JSON and back, optionally using the pretty printing functionatily to 
make JSON more readable on the console

# Prerequisites:
1. JDK 11 or later
2. Maven 3.2+
3. Redis 5.5.0 (which includes redis-cli 5.0.7)
4. Your favourite IDE
5. The following versions of top level components

        org.springframework.boot:spring-boot-starter-data-redis:jar:2.7.0-M2
        org.springframework.boot:spring-boot-starter-test:jar:2.7.0-M2
        com.fasterxml.jackson.core:jackson-databind:jar:2.13.1

Beware that there have been many changes to the APIs and, in fact, the main reason I decided to put together this 
tutorial was because the original did not mention versions and nothing worked.  I made all necessary adjustments 
to make everything work with the specific versions I list above.  They may or may not work with different versions.

If at any point you need to know which versions of each component are used by the Maven build type the following:

        $ ./mvnw dependency:tree


# Steps
1. Head to https://start.spring.io/ and make the following selections:

        Project Type: Maven
        Language: Java
        Spring Boot: 2.7.0 (M2)
        Group: com.example
        Artifact: chapter-005
        Name: redis
        Package name: com.example.chapter-005
        Packaging: jar
        Java: 11
        Dependencies: Spring Data Redis

2. Create of adapt the following classes

+ com.example.payroll.Employee - our entity or POJO that will be "transmitted" as a message via pub/sub. Notice that, because it lives in a different package, the class and its constructor must be public
+ com.example.receivers.Receiver - this is the consumer, the object that will be registered as a handler for received message.  Note that it accepts a generic __String__ object as this is what Redis can handle.  In our case, this string is expected to represent the serialized version of an instance of __Employee__ where the on-the-wire format chose is JSON.  In the single __receive__ message we output to the console both the raw string received as well as a pretty version of the JSON that represent the entity if it can be parsed into a valid instance
+ com.example.chapter-005.RedisApplication is where most of the action happens. Remember that it is acting as both the producer and consumer therefore the code is nearly twice as long as it needs to be

Four beans are created in __RedisApplication__ by the following methods:

+ __container__ is what will contain the listening loop, the consumer, and therefore it will have the listener injected by Spring
+ __listenerAdapter__ is used to specify which class contains the logic to consume as well as the method within such class that will handle incoming messages. In our case the class is __Receiver__ and we named the method __consume__
+ __receiver__ responsible for creating the instance that is injected via __listenerAdapter__
+ __template__ is responsible for creating an instance of __StringRedisTemplate__ which is our gateway to publish messages in string form

3. We have implemented the main method is such a way that up to 5 messages will be consumed before the application exits.  Furthermore, it will create an instance of __Employee__, convert it to a __String__ and publish it via the template object created earlier.  Notice that the __container__ method explicitly hardcoded the name of the channel to be used to "chat".  The loop exists only to hold the application running until the 5 messages have been received.

4. Open a terminal and run:

        $ redis-cli
        SUBSCRIBE chat

5. On a second terminal run:

        ./mvnw clean package
        ./mvnw spring-boot:run

Notice how the sample entity/message created in __main__ appears on the first terminal

6. On a third terminal run:

        $ redis-cli
        PUBLISH chat "{\"id\":2,\"name\":\"Jimmy Hendrix\",\"role\":\"Guitarrist\"}"

Notice how the message appears on the console both on a single line and then formatted prettily over a few lines.

Now, try publishing an incomplete JSON on the third terminal:

        PUBLISH chat "{\"id\":99,\"name\":\"Joe Cocker\"}"

Once again, the entity was rehydrated correctly but one attribute has null value.

Finally, try something outrageous:

        PUBLISH chat "I don't know what JSON is"

This time around, on the console you can see the same raw message but trying to parse this into an entity fails and you see the error reported on the console
        