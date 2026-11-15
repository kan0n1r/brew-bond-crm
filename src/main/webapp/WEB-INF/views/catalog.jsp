<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>BrewBond CRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/shop.css">
</head>
<body>
<header>
    <a class="brand" href="${pageContext.request.contextPath}/">
        <img src="${pageContext.request.contextPath}/shop-logo.svg" alt="" width="48" height="48">
        <span>BrewBond CRM</span>
    </a>
    <nav>
        <a href="${pageContext.request.contextPath}/cart">Cart</a>
        <c:choose>
            <c:when test="${not empty sessionScope.username}">
                <span>Signed in as <c:out value="${sessionScope.username}"/></span>
                <c:if test="${sessionScope.role eq 'ADMIN'}"><a href="${pageContext.request.contextPath}/admin">Admin</a></c:if>
                <form method="post" action="${pageContext.request.contextPath}/logout">
                    <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                    <button type="submit">Sign out</button>
                </form>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login">Sign in</a>
                <a href="${pageContext.request.contextPath}/register">Create account</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
<main>
    <c:if test="${param.checkout eq 'complete'}"><p class="notice">Checkout simulation complete. No payment was collected.</p></c:if>
    <h2>Fresh picks</h2>
    <p class="lead">Coffee and tea for a slower, better break.</p>
    <form method="get" action="${pageContext.request.contextPath}/" class="search">
        <label for="q">Search products</label>
        <input id="q" name="q" maxlength="80" value="<c:out value='${search}'/>">
        <button type="submit">Search</button>
    </form>
    <section class="products">
        <c:forEach var="product" items="${products}">
            <article class="card">
                <h3><c:out value="${product.name}"/></h3>
                <p><c:out value="${product.description}"/></p>
                <p class="price">$<c:out value="${product.priceCents / 100}"/></p>
                <form method="post" action="${pageContext.request.contextPath}/cart/add">
                    <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                    <input type="hidden" name="productId" value="<c:out value='${product.id}'/>">
                    <label>Quantity <input type="number" name="quantity" min="1" max="10" value="1"></label>
                    <button type="submit">Add to cart</button>
                </form>
            </article>
        </c:forEach>
        <c:if test="${empty products}"><p>No matching products found.</p></c:if>
    </section>
</main>
</body>
</html>
