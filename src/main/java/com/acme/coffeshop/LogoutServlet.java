package com.acme.coffeshop;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/");
    }
}
