# CoffeShop security evaluation checklist

This is a defensive checklist for the local CoffeShop training app. The application is intentionally implemented with security controls; these checks should confirm that common vulnerability classes are not present. Use synthetic accounts and data only. Do not expose the app to a public network.

## Run locally

1. Use Java 12 and Maven.
2. Set `SHOP_ADMIN_USERNAME` and `SHOP_ADMIN_PASSWORD` before the first run if you need the admin area. Use a unique local password of at least 12 characters.
3. Run `mvn package` (this also runs the unit tests), then `mvn exec:java`.
4. Open `http://localhost:8080/`.
5. Registration creates ordinary user accounts. The admin account is created from environment variables only when the database has no admin.
6. To reset synthetic data, stop the app and remove the specific `data/coffeshop.db` file.

## Manual checks

For each check, record the observed result locally. No script or payload is needed.

1. **SQL injection / query manipulation**
   - Search for ordinary text and text containing punctuation in the product catalog.
   - Confirm search results stay within matching catalog entries and the page does not reveal a database error.
   - Expected: search input is bound as a SQL parameter; it cannot change the query structure.

2. **Cross-site scripting (XSS)**
   - As the configured admin, add a product with a description containing harmless angle brackets and ordinary text.
   - Return to the catalog and inspect the description.
   - Expected: the characters are shown as text, not interpreted as markup or executable content.

3. **Cross-site request forgery (CSRF)**
   - Open a normal form such as add-to-cart or product creation.
   - In browser developer tools, remove the form's hidden `csrf` value and submit it.
   - Expected: the request is rejected with HTTP 403 and no state changes.

4. **Authentication bypass**
   - Sign out, then open `/admin` directly.
   - Expected: HTTP 403; admin data and actions are unavailable.

5. **Authorization / privilege escalation**
   - Register and sign in with an ordinary account; try `/admin`.
   - Expected: HTTP 403. Only the environment-configured admin account is assigned the `ADMIN` role.

6. **Session fixation**
   - Open the login page and note the session cookie in browser storage.
   - Sign in successfully and inspect the cookie again.
   - Expected: the session identifier changes after authentication; the old session is not the authenticated session.

7. **Weak or plaintext password storage**
   - Register two synthetic accounts with the same password.
   - Inspect the local SQLite `users` table using a local database viewer.
   - Expected: no plaintext password is stored; password hashes and per-account salts differ.

8. **Cross-user cart access / IDOR**
   - Open two independent browser profiles. Add a product to the cart in only one profile.
   - Expected: the other profile's cart remains independent; cart state is held in each session and no user-supplied cart owner ID is used.

9. **Unvalidated redirect**
   - Visit `/login` and inspect the form and subsequent navigation after a successful sign-in.
   - Expected: navigation returns to the fixed shop path; the app does not accept a user-supplied redirect destination.

10. **Clickjacking and response security headers**
    - Inspect a page response in browser developer tools.
    - Expected: `Content-Security-Policy` includes `frame-ancestors 'none'`, and the response has `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, and `Referrer-Policy`.

## Scope note

Checkout is only a cart-clearing simulation. It does not collect payment details, create real orders, or contact external services. This sample is for local evaluation, not production deployment.
