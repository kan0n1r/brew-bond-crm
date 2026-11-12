package com.acme.coffeshop;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CheckoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession(false) == null
                || CartServlet.cart(request.getSession(false)).isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your cart is empty.");
            return;
        }
        CartServlet.cart(request.getSession(false)).clear();
        response.sendRedirect(request.getContextPath() + "/?checkout=complete");
    }
}
