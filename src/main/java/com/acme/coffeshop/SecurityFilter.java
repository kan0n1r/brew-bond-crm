package com.acme.coffeshop;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class SecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpRequest.setCharacterEncoding("UTF-8");
        httpResponse.setHeader("Content-Security-Policy",
                "default-src 'self'; style-src 'self'; object-src 'none'; base-uri 'self'; "
                        + "frame-ancestors 'none'; form-action 'self'");
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        httpResponse.setHeader("Cache-Control", "no-store");

        if ("POST".equalsIgnoreCase(httpRequest.getMethod()) && !SecuritySupport.validCsrf(httpRequest)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid form token.");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
