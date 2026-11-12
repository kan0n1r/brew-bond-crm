package com.acme.coffeshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("csrf", SecuritySupport.csrfToken(request.getSession(true)));
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (!InputRules.validUsername(username) || !InputRules.validPassword(password)) {
            request.setAttribute("error", "Use a 3-32 character username and a 12-128 character password.");
            doGet(request, response);
            return;
        }

        try {
            PasswordUtil.Credential credential = PasswordUtil.create(password);
            try (Connection connection = ShopDatabase.connect();
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO users(username, password_hash, password_salt, role) VALUES (?, ?, ?, 'USER')")) {
                statement.setString(1, InputRules.normalizeUsername(username));
                statement.setBytes(2, credential.getHash());
                statement.setBytes(3, credential.getSalt());
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            if (exception.getMessage() != null && exception.getMessage().contains("UNIQUE constraint failed")) {
                request.setAttribute("error", "That username is unavailable.");
                doGet(request, response);
                return;
            }
            throw new ServletException("Unable to create the account.", exception);
        } catch (Exception exception) {
            throw new ServletException("Unable to protect the account password.", exception);
        }
        response.sendRedirect(request.getContextPath() + "/login?registered=1");
    }
}
