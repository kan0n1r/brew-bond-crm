package com.acme.coffeshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CatalogServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("q");
        if (search == null) {
            search = "";
        }
        search = search.trim();
        if (search.length() > 80) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Search is too long.");
            return;
        }

        List<Product> products = new ArrayList<>();
        String query = "SELECT id, name, description, price_cents FROM products "
                + "WHERE name LIKE ? ESCAPE '\\' OR description LIKE ? ESCAPE '\\' ORDER BY id";
        String pattern = "%" + search.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        try (Connection connection = ShopDatabase.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    products.add(new Product(result.getInt("id"), result.getString("name"),
                            result.getString("description"), result.getInt("price_cents")));
                }
            }
        } catch (Exception exception) {
            throw new ServletException("Unable to load the product catalog.", exception);
        }

        request.setAttribute("products", products);
        request.setAttribute("search", search);
        request.setAttribute("csrf", SecuritySupport.csrfToken(request.getSession(true)));
        request.getRequestDispatcher("/WEB-INF/views/catalog.jsp").forward(request, response);
    }
}
