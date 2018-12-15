package com.dmytroverner.mobileappws.shared;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Utils {
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
