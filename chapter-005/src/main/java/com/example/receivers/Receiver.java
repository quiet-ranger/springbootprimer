package com.example.receivers;

import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.payroll.Employee;

public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private AtomicInteger counter = new AtomicInteger();

    /**
     * The message consumed from Redis will be first dumped to screen in its raw
     * form. If it can be parsed into an instance of Employee then it will be
     * reconverted into a pretty version of the original message, otherwise an
     * error is reported.
     * 
     * @param message
     */
    public void consume(String message) {
        LOGGER.info("Received: \n" + message );
        counter.incrementAndGet();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Employee emp = mapper.readValue(message, Employee.class);
            System.out.println("Pretty JSON"); // If you see this output the entiry was rehydrated correctly
            String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(emp);
            System.out.println(prettyJson);
        } catch (Exception e) {
            System.err.println("Failed to parse JSON into an Employee object");
        }
    }

    public int getCount() {
        return counter.get();
    }
}