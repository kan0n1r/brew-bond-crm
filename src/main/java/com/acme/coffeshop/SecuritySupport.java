package com.acme.coffeshop;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

final class SecuritySupport {
    private static final SecureRandom RANDOM = new SecureRandom();

    private SecuritySupport() {
    }

    static String csrfToken(HttpSession session) {
        String token = (String) session.getAttribute("csrfToken");
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute("csrfToken", token);
        }
        return token;
    }

    static boolean validCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String expected = (String) session.getAttribute("csrfToken");
        String actual = request.getParameter("csrf");
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    static Integer userId(HttpServletRequest request) {
        Object value = request.getSession(false) == null
                ? null : request.getSession(false).getAttribute("userId");
        return value instanceof Integer ? (Integer) value : null;
    }

    static boolean isAdmin(HttpServletRequest request) {
        return request.getSession(false) != null
                && "ADMIN".equals(request.getSession(false).getAttribute("role"));
    }
}
