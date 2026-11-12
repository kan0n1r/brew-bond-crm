# CoffeShop

A small Java 12 Servlet/JSP shop for local evaluation. It includes a searchable catalog, account registration and sign-in, a session cart, simulated checkout, and a role-protected product admin page.

## Requirements

- Java 12
- Maven 3.8+

## Run

```sh
mvn package
mvn exec:java
```

Open [http://localhost:8080](http://localhost:8080). The SQLite database is created at `data/coffeshop.db`; override the path with `SHOP_DB_PATH`.

To configure an admin account on first startup, provide both `SHOP_ADMIN_USERNAME` and `SHOP_ADMIN_PASSWORD`. Usernames must be 3-32 letters, numbers, dots, dashes, or underscores; passwords must be 12-128 characters. Registration only creates ordinary users.

## Security behavior

- SQL statements use prepared parameters and server-side validation.
- Passwords use salted PBKDF2-HMAC-SHA-256 hashes.
- State-changing forms require a session-bound CSRF token.
- Login rotates the session identifier; session IDs are cookie-only and cookies are HttpOnly.
- Admin routes verify the authenticated role server-side.
- JSP output uses JSTL escaping.
- Responses set a restrictive Content Security Policy and common browser security headers.

See [vulnerabilities.md](vulnerabilities.md) for a step-by-step, non-destructive local security checklist. Do not expose this training app to a public network or use real personal data.
