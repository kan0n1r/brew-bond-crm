<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Sign in - BrewBond CRM</title><link rel="stylesheet" href="${pageContext.request.contextPath}/shop.css"></head>
<body>
<main class="narrow">
    <a class="brand" href="${pageContext.request.contextPath}/">
        <img src="${pageContext.request.contextPath}/shop-logo.svg" alt="" width="48" height="48">
        <span>BrewBond CRM</span>
    </a>
    <h2>Sign in</h2>
    <c:if test="${param.registered eq '1'}"><p class="notice">Account created. You can sign in now.</p></c:if>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>
    <form method="post" action="${pageContext.request.contextPath}/login">
        <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
        <label>Username <input name="username" required maxlength="32" autocomplete="username"></label>
        <label>Password <input type="password" name="password" required maxlength="128" autocomplete="current-password"></label>
        <button type="submit">Sign in</button>
    </form>
    <p><a href="${pageContext.request.contextPath}/register">Create an account</a></p>
</main>
</body>
</html>
