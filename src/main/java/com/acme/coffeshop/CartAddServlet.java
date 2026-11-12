package com.acme.coffeshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CartAddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int productId;
        int quantity;
        try {
            productId = Integer.parseInt(request.getParameter("productId"));
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product or quantity.");
            return;
        }
        if (productId < 1 || quantity < 1 || quantity > 10) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product or quantity.");
            return;
        }

        try (Connection connection = ShopDatabase.connect();
             PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM products WHERE id = ?")) {
            statement.setInt(1, productId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                    return;
                }
            }
        } catch (Exception exception) {
            throw new ServletException("Unable to validate the product.", exception);
        }

        Map<Integer, Integer> cart = CartServlet.cart(request.getSession(true));
        int current = cart.getOrDefault(productId, 0);
        if (current + quantity > 10) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The cart limit is 10 per product.");
            return;
        }
        cart.put(productId, current + quantity);
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
