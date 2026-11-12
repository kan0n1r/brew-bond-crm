package com.acme.coffeshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("csrf", SecuritySupport.csrfToken(request.getSession(true)));
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (!InputRules.validUsername(username) || !InputRules.validPassword(password)) {
            request.setAttribute("error", "Invalid username or password.");
            doGet(request, response);
            return;
        }
        try (Connection connection = ShopDatabase.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, username, password_hash, password_salt, role FROM users WHERE username = ?")) {
            statement.setString(1, InputRules.normalizeUsername(username));
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !PasswordUtil.verify(password, result.getBytes("password_hash"),
                        result.getBytes("password_salt"))) {
                    request.setAttribute("error", "Invalid username or password.");
                    doGet(request, response);
                    return;
                }
                HttpSession session = request.getSession(true);
                request.changeSessionId();
                session.setAttribute("userId", result.getInt("id"));
                session.setAttribute("username", result.getString("username"));
                session.setAttribute("role", result.getString("role"));
                session.removeAttribute("csrfToken");
                SecuritySupport.csrfToken(session);
            }
        } catch (Exception exception) {
            if (exception instanceof ServletException) {
                throw (ServletException) exception;
            }
            throw new ServletException("Unable to sign in.", exception);
        }
        response.sendRedirect(request.getContextPath() + "/");
    }
}
