package com.acme.coffeshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SecuritySupport.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Administrator access required.");
            return;
        }
        try (Connection connection = ShopDatabase.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, description, price_cents FROM products ORDER BY id");
             ResultSet result = statement.executeQuery()) {
            java.util.List<Product> products = new java.util.ArrayList<>();
            while (result.next()) {
                products.add(new Product(result.getInt("id"), result.getString("name"),
                        result.getString("description"), result.getInt("price_cents")));
            }
            request.setAttribute("products", products);
        } catch (Exception exception) {
            throw new ServletException("Unable to load the administration page.", exception);
        }
        request.setAttribute("csrf", SecuritySupport.csrfToken(request.getSession(true)));
        request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SecuritySupport.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Administrator access required.");
            return;
        }
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        int priceCents;
        try {
            priceCents = Integer.parseInt(request.getParameter("priceCents"));
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid price.");
            return;
        }
        if (name == null || name.trim().isEmpty() || name.length() > 80
                || description == null || description.trim().isEmpty() || description.length() > 500
                || priceCents < 1 || priceCents > 1_000_000) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product details are invalid.");
            return;
        }
        try (Connection connection = ShopDatabase.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO products(name, description, price_cents) VALUES (?, ?, ?)")) {
            statement.setString(1, name.trim());
            statement.setString(2, description.trim());
            statement.setInt(3, priceCents);
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new ServletException("Unable to add the product.", exception);
        }
        response.sendRedirect(request.getContextPath() + "/admin");
    }
}
