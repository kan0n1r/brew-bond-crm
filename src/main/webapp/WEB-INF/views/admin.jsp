<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Admin - CoffeShop</title><link rel="stylesheet" href="${pageContext.request.contextPath}/shop.css"></head>
<body>
<main>
    <a class="brand" href="${pageContext.request.contextPath}/">
        <img src="${pageContext.request.contextPath}/shop-logo.svg" alt="" width="48" height="48">
        <span>CoffeShop</span>
    </a>
    <h2>Product administration</h2>
    <p><a href="${pageContext.request.contextPath}/">Return to shop</a></p>
    <h3>Add a product</h3>
    <form method="post" action="${pageContext.request.contextPath}/admin">
        <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
        <label>Name <input name="name" required maxlength="80"></label>
        <label>Description <textarea name="description" required maxlength="500"></textarea></label>
        <label>Price in cents <input type="number" name="priceCents" required min="1" max="1000000"></label>
        <button type="submit">Add product</button>
    </form>
    <h3>Catalog</h3>
    <ul>
        <c:forEach var="product" items="${products}">
            <li><c:out value="${product.name}"/> — $<c:out value="${product.priceCents / 100}"/></li>
        </c:forEach>
    </ul>
</main>
</body>
</html>
