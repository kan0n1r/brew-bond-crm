<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Your cart - BrewBond CRM</title><link rel="stylesheet" href="${pageContext.request.contextPath}/shop.css"></head>
<body>
<main>
    <a class="brand" href="${pageContext.request.contextPath}/">
        <img src="${pageContext.request.contextPath}/shop-logo.svg" alt="" width="48" height="48">
        <span>BrewBond CRM</span>
    </a>
    <h2>Your cart</h2>
    <c:choose>
        <c:when test="${empty lines}"><p>Your cart is empty. <a href="${pageContext.request.contextPath}/">Browse products</a>.</p></c:when>
        <c:otherwise>
            <table>
                <thead><tr><th>Product</th><th>Quantity</th><th>Subtotal</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="line" items="${lines}">
                    <tr>
                        <td><c:out value="${line.product.name}"/></td>
                        <td><c:out value="${line.quantity}"/></td>
                        <td>$<c:out value="${line.subtotal / 100}"/></td>
                        <td><form method="post" action="${pageContext.request.contextPath}/cart/remove">
                            <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                            <input type="hidden" name="productId" value="<c:out value='${line.product.id}'/>">
                            <button type="submit">Remove</button>
                        </form></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <p class="price">Total: $<c:out value="${total / 100}"/></p>
            <form method="post" action="${pageContext.request.contextPath}/checkout">
                <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
                <button type="submit">Simulate checkout</button>
            </form>
            <p><small>This is a demo. No payment or delivery is involved.</small></p>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
