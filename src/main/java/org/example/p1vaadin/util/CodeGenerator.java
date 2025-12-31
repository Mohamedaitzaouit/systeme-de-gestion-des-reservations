package org.example.p1vaadin.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    public String generateReservationCode() {
        int number = 10000 + random.nextInt(90000);
        return "EVT-" + number;
    }
}