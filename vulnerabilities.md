# BrewBond CRM security evaluation checklist

This is a defensive checklist for the local BrewBond CRM training app. The application is intentionally implemented with security controls; these checks should confirm that common vulnerability classes are not present. Use synthetic accounts and data only. Do not expose the app to a public network.

## Run locally

1. Use Java 12 and Maven.
2. If you need the admin checks, set `SHOP_ADMIN_USERNAME` and `SHOP_ADMIN_PASSWORD` before first startup. Use synthetic, local-only credentials; the password must have at least 12 characters.
3. Run `mvn package` (this also runs the unit tests), then `mvn exec:java`.
4. Open `http://localhost:8080/`. The routes used below are `/`, `/register`, `/login`, `/cart`, and `/admin`.
5. Registration creates ordinary user accounts. The admin account is created from environment variables only when the database has no admin.
6. To reset the synthetic dataset, stop the app and remove only `data/brew-bond-crm.db`, then restart it.

## Evidence notes

For each check, record the check number, route, steps, expected result, observed HTTP status, and a screenshot or a short sanitized log excerpt if available. The app has no dedicated audit-log feature, so a browser screenshot/status is often the relevant evidence. Never include passwords, session cookies, CSRF tokens, or real personal data in screenshots or logs. Keep evidence local unless it has been reviewed and redacted.

## What “PoC” means here

BrewBond CRM is a secure evaluation target, so these checks are proof-of-control demonstrations, not proof that an exploitable defect exists. A useful PoC record contains:

1. The check number and the local route.
2. The starting state (for example, signed out, ordinary account, or configured admin).
3. The UI actions and harmless synthetic input used.
4. The observed status or page result, compared with the expected result below.
5. A redacted screenshot or short log excerpt, if available.

Do not add exploit payloads, automation against other systems, real credentials, or session/CSRF values. If an app behavior differs from the expected result, document the observation here without including sensitive request data.

## Manual checks

These steps verify that the application resists common vulnerability classes; they do not trigger a real vulnerability. Use only harmless text and synthetic accounts. No exploit payload or script is needed.

1. **SQL injection / query manipulation**
   - **What it relates to:** How user-supplied values are included in database queries. It becomes a vulnerability when input is interpreted as query syntax rather than data.
   - **Why it matters:** A successful injection can expose or alter records, bypass checks, or damage data. Search fields and login forms are common places to review.
   - **BrewBond CRM context:** The catalog search is the relevant feature. Its query binds the search text as a parameter and treats wildcard characters as literal input.
   1. Open `/` and search for `blend`; note the matching result.
   2. Search for ordinary punctuation-containing text, for example `coffee's`.
   3. Confirm the page remains usable and does not display a database error.
   - Expected: the search is treated as text and only matching products are returned.

2. **Cross-site scripting (XSS)**
   - **What it relates to:** Data entered by one user being rendered in another user's browser as active HTML or script instead of plain text.
   - **Why it matters:** If untrusted content executes in a visitor's browser, it may act with that visitor's site session or display misleading content.
   - **BrewBond CRM context:** Admin-entered product names and descriptions appear in the catalog. JSP output encoding should render them as text.
   1. Sign in with the configured admin and open `/admin`.
   2. Add a synthetic product with a description such as `Coffee <Beans> & tea` (ordinary text only).
   3. Open `/` and inspect the new description.
   - Expected: angle brackets and ampersands appear as text; the browser does not interpret them as markup.

3. **Cross-site request forgery (CSRF)**
   - **What it relates to:** A browser being induced to submit an action to a site where the user is already signed in, without the user's intent.
   - **Why it matters:** Without request-intent validation, an unwanted action could be performed using the victim's existing session.
   - **BrewBond CRM context:** Adding/removing cart items, checkout, sign-out, and admin product creation change state. Their POST forms carry a session-bound token, which the server checks.
   1. Open `/` and choose **Add to cart**, or open `/admin` and use the product form.
   2. In browser developer tools, remove the form's hidden `csrf` value before submitting.
   3. Check the response and confirm the cart/catalog did not change.
   - Expected: HTTP 403; the state-changing request is rejected.

4. **Authentication bypass**
   - **What it relates to:** Accessing a feature without first proving an identity, or when the application fails to require a valid sign-in.
   - **Why it matters:** Private features or data may become available to an anonymous visitor.
   - **BrewBond CRM context:** `/admin` is the protected route used for this check. Authentication asks who the user is; check 5 separately covers what that user is allowed to do.
   1. Sign out, or open a fresh private browser window with no BrewBond CRM session.
   2. Navigate directly to `/admin`.
   - Expected: HTTP 403; no admin catalog or product form is shown.

5. **Authorization / privilege escalation**
   - **What it relates to:** Whether an authenticated identity has permission for a particular action. A user is authenticated but receives excessive privileges when role checks are missing or incorrect.
   - **Why it matters:** An ordinary account could gain administrative abilities or access another role's data.
   - **BrewBond CRM context:** Self-registration creates only `USER` accounts. The admin servlet checks the server-side session role on both page display and product creation.
   1. Open `/register`, create a synthetic ordinary account, and sign in.
   2. Navigate directly to `/admin`.
   - Expected: HTTP 403. Self-registration does not grant the `ADMIN` role.

6. **Session fixation**
   - **What it relates to:** Keeping a pre-authentication session identifier after login, allowing an attacker who already knows that identifier to benefit from the later authenticated session.
   - **Why it matters:** Authentication should establish a fresh session identity so an earlier session ID cannot simply become privileged.
   - **BrewBond CRM context:** Successful login asks the servlet container to rotate the session ID while keeping the signed-in session data.
   1. Open `/login` and inspect the `JSESSIONID` in the browser's cookie storage.
   2. Sign in successfully and inspect the cookie again.
   - Expected: the session identifier changes at login. The cookie is HttpOnly, so inspect it in developer tools rather than page JavaScript.

7. **Weak or plaintext password storage**
   - **What it relates to:** How credentials are stored in the database. Plaintext or fast, unsalted hashes make stolen database contents much more useful to someone trying to recover passwords.
   - **Why it matters:** Users often reuse passwords; exposing recoverable credentials can affect accounts beyond this demo.
   - **BrewBond CRM context:** Passwords are processed with PBKDF2-HMAC-SHA-256 and a unique random salt. The application stores the derived hash and salt, not the original password.
   1. Register two synthetic accounts using the same test password.
   2. Stop the app and inspect the local SQLite `users` table with a local database viewer.
   3. Review only the password-related columns; do not copy credentials or database contents into screenshots.
   - Expected: there is no plaintext password column/value; each account has a salted password hash, and the salts differ.

8. **Cross-user cart access / IDOR**
   - **What it relates to:** Insecure Direct Object Reference (IDOR) is a broken-access-control case where a request can select another user's object by changing or supplying an identifier without a proper ownership check.
   - **Why it matters:** Private carts, orders, or profile data may be exposed or changed across accounts.
   - **BrewBond CRM context:** This demo stores carts in server-side sessions and does not accept a cart-owner ID from the form. Separate browser sessions should therefore have separate carts.
   1. Open two independent browser profiles or one normal and one private window.
   2. In profile A, add a product to `/cart`.
   3. Open `/cart` in profile B without adding anything there.
   - Expected: profile B's cart is empty and profile A's cart is unchanged.

9. **Unvalidated redirect**
   - **What it relates to:** Redirecting a user to a destination supplied by request input without validating that destination.
   - **Why it matters:** A trusted site's login or navigation link can be abused to send a user to a misleading external site.
   - **BrewBond CRM context:** The login handler redirects to the fixed shop path and does not read a user-provided return URL.
   1. Open `/login` and inspect the form fields.
   2. Sign in with a synthetic account and note the destination.
   3. Confirm the form does not provide a return-destination field and the app returns to the shop.
   - Expected: navigation uses a fixed local path; there is no user-controlled redirect target.

10. **Clickjacking and response security headers**
    - **What it relates to:** Clickjacking presents a real site inside a deceptive frame so a visitor can be tricked into interacting with controls. Browser response headers can restrict framing and strengthen other browser-side protections.
    - **Why it matters:** A user may unknowingly activate an action on a page they trust.
    - **BrewBond CRM context:** The Content Security Policy disallows framing; `X-Frame-Options` provides a second framing restriction. The other listed headers address content-type sniffing and referrer handling.
    1. Open developer tools and select the document request for `/`.
    2. Inspect its response headers.
    - Expected: `Content-Security-Policy` includes `frame-ancestors 'none'`; `X-Frame-Options` is `DENY`; `X-Content-Type-Options` is `nosniff`; and `Referrer-Policy` is present.

## Local curl proof-of-control checks

Start BrewBond CRM on an available local port, for example with `PORT=18080 mvn exec:java`, then use these requests against that same instance. They demonstrate that input is handled as data and unauthenticated/state-changing requests are rejected; they do not exploit a defect.

1. Search with harmless punctuation:

   ```sh
   curl -i --get --data-urlencode "q=coffee's" http://127.0.0.1:18080/
   ```

   Expected: HTTP 200, a normal catalog page, and no database error.

2. Request the admin page without an authenticated session:

   ```sh
   curl -i http://127.0.0.1:18080/admin
   ```

   Expected: HTTP 403.

3. Submit a state-changing request without a session-bound form token:

   ```sh
   curl -i -X POST http://127.0.0.1:18080/checkout
   ```

   Expected: HTTP 403 and no cart change.

4. Inspect the response headers:

   ```sh
   curl -sS -D - -o /dev/null http://127.0.0.1:18080/
   ```

   Expected: the response includes the browser security headers listed in check 10.

Do not substitute exploit payloads or run these requests against a public or third-party host. If one of these local checks returns an unexpected result, record the status and a redacted response excerpt in this file.

## Scope note

Checkout is only a cart-clearing simulation. It does not collect payment details, create real orders, or contact external services. This sample is for local evaluation, not production deployment.
