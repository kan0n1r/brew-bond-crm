# CoffeShop security evaluation checklist

This is a defensive checklist for the local CoffeShop training app. The application is intentionally implemented with security controls; these checks should confirm that common vulnerability classes are not present. Use synthetic accounts and data only. Do not expose the app to a public network.

## Run locally

1. Use Java 12 and Maven.
2. If you need the admin checks, set `SHOP_ADMIN_USERNAME` and `SHOP_ADMIN_PASSWORD` before first startup. Use synthetic, local-only credentials; the password must have at least 12 characters.
3. Run `mvn package` (this also runs the unit tests), then `mvn exec:java`.
4. Open `http://localhost:8080/`. The routes used below are `/`, `/register`, `/login`, `/cart`, and `/admin`.
5. Registration creates ordinary user accounts. The admin account is created from environment variables only when the database has no admin.
6. To reset the synthetic dataset, stop the app and remove only `data/coffeshop.db`, then restart it.

## Evidence notes

For each check, record the check number, route, steps, expected result, observed HTTP status, and a screenshot or a short sanitized log excerpt if available. The app has no dedicated audit-log feature, so a browser screenshot/status is often the relevant evidence. Never include passwords, session cookies, CSRF tokens, or real personal data in screenshots or logs. Keep evidence local unless it has been reviewed and redacted.

## What “PoC” means here

CoffeShop is a secure evaluation target, so these checks are proof-of-control demonstrations, not proof that an exploitable defect exists. A useful PoC record contains:

1. The check number and the local route.
2. The starting state (for example, signed out, ordinary account, or configured admin).
3. The UI actions and harmless synthetic input used.
4. The observed status or page result, compared with the expected result below.
5. A redacted screenshot or short log excerpt, if available.

Do not add exploit payloads, automation against other systems, real credentials, or session/CSRF values. If an app behavior differs from the expected result, document the observation here without including sensitive request data.

## Manual checks

These steps verify that the application resists common vulnerability classes; they do not trigger a real vulnerability. Use only harmless text and synthetic accounts. No exploit payload or script is needed.

1. **SQL injection / query manipulation**
   1. Open `/` and search for `blend`; note the matching result.
   2. Search for ordinary punctuation-containing text, for example `coffee's`.
   3. Confirm the page remains usable and does not display a database error.
   - Expected: the search is treated as text and only matching products are returned.

2. **Cross-site scripting (XSS)**
   1. Sign in with the configured admin and open `/admin`.
   2. Add a synthetic product with a description such as `Coffee <Beans> & tea` (ordinary text only).
   3. Open `/` and inspect the new description.
   - Expected: angle brackets and ampersands appear as text; the browser does not interpret them as markup.

3. **Cross-site request forgery (CSRF)**
   1. Open `/` and choose **Add to cart**, or open `/admin` and use the product form.
   2. In browser developer tools, remove the form's hidden `csrf` value before submitting.
   3. Check the response and confirm the cart/catalog did not change.
   - Expected: HTTP 403; the state-changing request is rejected.

4. **Authentication bypass**
   1. Sign out, or open a fresh private browser window with no CoffeShop session.
   2. Navigate directly to `/admin`.
   - Expected: HTTP 403; no admin catalog or product form is shown.

5. **Authorization / privilege escalation**
   1. Open `/register`, create a synthetic ordinary account, and sign in.
   2. Navigate directly to `/admin`.
   - Expected: HTTP 403. Self-registration does not grant the `ADMIN` role.

6. **Session fixation**
   1. Open `/login` and inspect the `JSESSIONID` in the browser's cookie storage.
   2. Sign in successfully and inspect the cookie again.
   - Expected: the session identifier changes at login. The cookie is HttpOnly, so inspect it in developer tools rather than page JavaScript.

7. **Weak or plaintext password storage**
   1. Register two synthetic accounts using the same test password.
   2. Stop the app and inspect the local SQLite `users` table with a local database viewer.
   3. Review only the password-related columns; do not copy credentials or database contents into screenshots.
   - Expected: there is no plaintext password column/value; each account has a salted password hash, and the salts differ.

8. **Cross-user cart access / IDOR**
   1. Open two independent browser profiles or one normal and one private window.
   2. In profile A, add a product to `/cart`.
   3. Open `/cart` in profile B without adding anything there.
   - Expected: profile B's cart is empty and profile A's cart is unchanged.

9. **Unvalidated redirect**
   1. Open `/login` and inspect the form fields.
   2. Sign in with a synthetic account and note the destination.
   3. Confirm the form does not provide a return-destination field and the app returns to the shop.
   - Expected: navigation uses a fixed local path; there is no user-controlled redirect target.

10. **Clickjacking and response security headers**
    1. Open developer tools and select the document request for `/`.
    2. Inspect its response headers.
    - Expected: `Content-Security-Policy` includes `frame-ancestors 'none'`; `X-Frame-Options` is `DENY`; `X-Content-Type-Options` is `nosniff`; and `Referrer-Policy` is present.

## Scope note

Checkout is only a cart-clearing simulation. It does not collect payment details, create real orders, or contact external services. This sample is for local evaluation, not production deployment.
