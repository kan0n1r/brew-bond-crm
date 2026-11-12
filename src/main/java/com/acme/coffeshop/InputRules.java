package com.acme.coffeshop;

import java.util.regex.Pattern;

final class InputRules {
    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9._-]{3,32}");

    private InputRules() {
    }

    static boolean validUsername(String value) {
        return value != null && USERNAME.matcher(value.trim()).matches();
    }

    static boolean validPassword(String value) {
        return value != null && value.length() >= 12 && value.length() <= 128;
    }

    static String normalizeUsername(String value) {
        return value.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
