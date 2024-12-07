package io.github.betterclient.compiler.util;

import java.util.Random;

public class StringParser {
    public static String parse(String string) {
        return string
                .replace("\\\\", "\\")
                .replace("\\\"", "\"")
                .replace("\\'", "'")
                .replace("\\t", "\t")
                .replace("\\b", "\b")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\f", "\f");
    }

    public static int find(String input) {
        boolean escaped = false;
        for (int i = 1; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '"' && !escaped) {
                return i;
            }
            escaped = (c == '\\') && !escaped;
        }
        return -1; //well well well
    }

    public static String randomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }
}
