package com.acme.coffeshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class CartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Map<Integer, Integer> cart = cart(session);
        List<CartLine> lines = new ArrayList<>();
        long total = 0;
        try (Connection connection = ShopDatabase.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, price_cents FROM products WHERE id = ?")) {
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                statement.setInt(1, entry.getKey());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        CartLine line = new CartLine(new Product(result.getInt("id"), result.getString("name"),
                                "", result.getInt("price_cents")), entry.getValue());
                        lines.add(line);
                        total += (long) line.getProduct().getPriceCents() * line.getQuantity();
                    }
                }
            }
        } catch (Exception exception) {
            throw new ServletException("Unable to load the shopping cart.", exception);
        }
        request.setAttribute("lines", lines);
        request.setAttribute("total", total);
        request.setAttribute("csrf", SecuritySupport.csrfToken(session));
        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }

    @SuppressWarnings("unchecked")
    static Map<Integer, Integer> cart(HttpSession session) {
        Object stored = session.getAttribute("cart");
        if (stored instanceof Map) {
            return (Map<Integer, Integer>) stored;
        }
        Map<Integer, Integer> created = new HashMap<>();
        session.setAttribute("cart", created);
        return created;
    }

    public static final class CartLine {
        private final Product product;
        private final int quantity;

        CartLine(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public long getSubtotal() {
            return (long) product.getPriceCents() * quantity;
        }
    }
}
