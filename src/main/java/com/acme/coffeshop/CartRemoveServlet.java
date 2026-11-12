package com.acme.coffeshop;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CartRemoveServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            if (request.getSession(false) != null) {
                CartServlet.cart(request.getSession(false)).remove(productId);
            }
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product.");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
