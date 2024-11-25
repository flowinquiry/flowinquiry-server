package com.flexwork.platform.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Obfuscator {

    // Function to obfuscate a value (string or number)
    public static String obfuscate(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid input: value cannot be null");
        }

        // Convert the value to a string, encode to Base64, and make it URL-safe
        String base64 = Base64.getEncoder().encodeToString(value.toString().getBytes(StandardCharsets.UTF_8));
        return base64.replace("+", "-").replace("/", "_").replace("=", "");
    }

    // Function to deobfuscate the value (returns the original type: string or number)
    public static Object deobfuscate(String encodedValue) {
        if (encodedValue == null || encodedValue.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: encodedValue cannot be null or empty");
        }

        // Decode the URL-safe Base64 string
        String base64 = encodedValue.replace("-", "+").replace("_", "/");
        while (base64.length() % 4 != 0) {
            base64 += "="; // Add padding
        }

        String decodedString = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);

        // Try to parse the decoded string as a number
        try {
            return Double.parseDouble(decodedString);
        } catch (NumberFormatException e) {
            return decodedString;
        }
    }
}

