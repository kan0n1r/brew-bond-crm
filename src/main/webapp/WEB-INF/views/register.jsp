<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Create account - BrewBond CRM</title><link rel="stylesheet" href="${pageContext.request.contextPath}/shop.css"></head>
<body>
<main class="narrow">
    <a class="brand" href="${pageContext.request.contextPath}/">
        <img src="${pageContext.request.contextPath}/shop-logo.svg" alt="" width="48" height="48">
        <span>BrewBond CRM</span>
    </a>
    <h2>Create account</h2>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>
    <form method="post" action="${pageContext.request.contextPath}/register">
        <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
        <label>Username <input name="username" required minlength="3" maxlength="32" autocomplete="username"></label>
        <small>3-32 letters, numbers, dots, dashes, or underscores.</small>
        <label>Password <input type="password" name="password" required minlength="12" maxlength="128" autocomplete="new-password"></label>
        <small>Use at least 12 characters.</small>
        <button type="submit">Create account</button>
    </form>
</main>
</body>
</html>
