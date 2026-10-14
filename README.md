
# LifeLink - Blood Donor Finder

A free community web app that connects **blood donors** with people who need blood.
Built with **Servlets + JSP + JDBC (MVC architecture)** as a Web Technology project.

## What it does

| Feature | Who can use it |
|---|---|
| Search donors by blood group and city | Everyone |
| See open (urgent) blood requests | Everyone |
| See donor / requester **phone numbers** | Logged-in members only (privacy) |
| Register as a donor, switch "available" on/off | Members |
| Post an urgent blood request | Members |
| Mark a request fulfilled or delete it | The member who posted it |

Why it is useful: in an emergency, families lose precious time searching for donors.
LifeLink gives one simple place to search by blood group and city and to post requests.

> LifeLink only connects people. Always confirm with the hospital and take blood from a
> licensed blood bank or hospital.

## Syllabus coverage (where to show it in your viva)

**Servlets**

| Topic | Where |
|---|---|
| Server-side programming, web server | Tomcat runs all servlets |
| Servlet life cycle | `init()` in `NewRequestServlet`, `doGet/doPost` in every servlet, listener in `AppStartupListener` |
| Reading servlet parameters | `req.getParameter(...)` in `DonorServlet`, `RegisterServlet` |
| Initialization of servlet parameters | `@WebInitParam maxUnits` in `NewRequestServlet`; `<context-param>` in `web.xml` |
| HTTP request and response | all servlets (GET search forms, POST forms) |
| `javax.servlet.*`, `javax.servlet.http.*` | imports in every servlet |
| Redirection | `resp.sendRedirect(...)` after login, register, logout, add/delete |
| Session management | `LoginServlet` creates session, `AuthFilter` checks it, `LogoutServlet` ends it |
| Cookie management | "Remember my e-mail" cookie in `LoginServlet` |
| Database access using servlets | `dao/` package (JDBC + PreparedStatement) |

**JSP**

| Topic | Where |
|---|---|
| Limitations of servlet | Servlets contain logic only; all HTML lives in JSPs |
| JSP life cycle | `footer.jsp` declaration/scriptlet run per request; pages compiled to servlets by Tomcat |
| Scripting elements | `footer.jsp`: declaration `<%! %>`, scriptlet `<% %>`, expression `<%= %>`; `profile.jsp` scriptlet |
| Directive elements | `<%@ page %>`, `<%@ taglib %>`, `<%@ include %>` in every page |
| Action elements | `jsp:include`, `jsp:param`, `jsp:forward` (`index.jsp`), `jsp:useBean`, `jsp:getProperty` |
| Implicit objects | `session`, `out`, `application`, `exception` (`profile.jsp`, `footer.jsp`, `error.jsp`) plus EL `param`, `sessionScope` |
| Error handling | `<error-page>` in `web.xml` and `error.jsp` (`isErrorPage="true"`) |
| Accessing bean using JSP | `profile.jsp` uses `jsp:useBean` on `model/User` |
| Database access using JSP | JSPs show data loaded through DAOs (`donors.jsp`, `requests.jsp`) |
| MVC architecture | **Model** = `model/` + `dao/`, **View** = `WEB-INF/views/*.jsp`, **Controller** = `servlet/` |

## Project structure

```
src/main/java/com/lifelink/
  model/     User, BloodRequest            (JavaBeans)
  dao/       DBUtil, UserDAO, RequestDAO   (JDBC)
  servlet/   Home, Donor, Request, NewRequest, Profile, Login, Logout, Register,
             AuthFilter, AppStartupListener
  util/      AppConstants, PasswordUtil    (PBKDF2 salted password hashing)
src/main/webapp/
  index.jsp                 welcome page (forwards to /home)
  css/style.css
  WEB-INF/web.xml           settings, session config, error pages
  WEB-INF/views/*.jsp       all pages (hidden from direct URL access)
```

The database is **H2 (embedded)**: no MySQL or Oracle to install. Tables are created
automatically on first start.

## Run it on your computer

You need: **JDK 17+**, **Maven**, **Apache Tomcat 9** (must be 9, not 10: this project uses `javax.servlet`).

```
mvn clean package
```

Copy `target/ROOT.war` into Tomcat's `webapps` folder, start Tomcat, open
`http://localhost:8080/`.

**Eclipse / IntelliJ:** import as a Maven project, add a Tomcat 9 server, "Run on server".

**With Docker (no Java or Tomcat needed):**

```
docker build -t lifelink .
docker run -p 8080:8080 lifelink
```

## Publish it free on the internet (Render)

1. Create a free GitHub account and upload this folder as a new repository.
2. Create a free account at render.com, then **New > Web Service** and connect the repository.
3. Choose **Language: Docker** and the **Free** instance type.
4. Under **Environment Variables** add `PORT` = `8080`.
5. Click **Create Web Service**. After the build finishes you get a public link like
   `https://lifelink-xxxx.onrender.com`. Share that link in your society's WhatsApp group.

Good to know:
- Free instances go to sleep after some idle time, so the first visit can take about a minute.
- On the free plan the database file is wiped whenever the service restarts or redeploys.
  For real, long-term use attach a persistent disk mounted at `/usr/local/tomcat/data`
  (a paid Render feature) or move to an external database later.
- Use HTTPS (Render provides it) because the app has login passwords.

## Quick test checklist

1. Open the site. Home page shows 0 donors.
2. Register two accounts (different blood groups and cities).
3. Log out, search donors: contact shows "Login to see contact".
4. Log in, search again: phone numbers are visible.
5. Post a blood request, check it under **Blood Requests**, then mark it fulfilled in **My Profile**.
6. Open a wrong URL such as `/abc`: the friendly 404 page appears.

## Ideas to extend later

E-mail or SMS alerts for new requests, a CSRF token on forms, an admin page to remove fake
entries, a "last donated" date, and a MySQL/PostgreSQL database for permanent storage.


## Security hardening notes

This version includes a synchronizer-token CSRF filter for POST requests, POST-only logout,
no-cache headers on authenticated profile/request pages, and a 10-character minimum for newly
registered passwords. The remembered-email cookie is HttpOnly and Secure when the request is HTTPS.

Before production use, deploy behind HTTPS and configure the session cookie's Secure and SameSite
attributes at the Tomcat/reverse-proxy layer. Add login rate limiting, account recovery, donor-specific
contact consent, and a tested persistent database/backup plan before inviting real users. CSRF protection
does not replace authorization checks or privacy controls. Do not use this student project as an emergency
medical service without security review, monitoring, and an operational process for handling abuse.


## Donor phone privacy update
- Phone numbers are hidden in donor search unless the donor explicitly opts in.
- Existing accounts are migrated with phone sharing disabled by default.
- The schema migration adds `phone_visible` automatically without dropping existing user/request data.
- This is a development-stage privacy improvement, not a complete production security audit. Use HTTPS and review applicable privacy/data-protection requirements before public launch.


## Email verification setup (required for registration)

New accounts must verify their email address before they can log in. Verification links expire after 24 hours and are single-use; only a SHA-256 hash of the token is stored in the database. Existing accounts are preserved as verified during migration so current users are not locked out; new accounts are explicitly created as unverified and must follow the email link. For a public launch, consider requiring existing users to re-verify if your privacy policy and rollout plan allow it.

The application sends email through SMTP. Configure these environment variables **before starting Tomcat**:

- `SMTP_HOST` (Gmail: `smtp.gmail.com`)
- `SMTP_PORT` (Gmail: `587`)
- `SMTP_USER` (your sending Gmail address)
- `SMTP_PASSWORD` (a Google App Password, not your normal Gmail password)
- `SMTP_FROM` (the sender address, usually the same as `SMTP_USER`)

For a local PowerShell test, set the variables in the same PowerShell window used to start Tomcat. Example (replace the placeholders; do not share the password):

```powershell
$env:SMTP_HOST = "smtp.gmail.com"
$env:SMTP_PORT = "587"
$env:SMTP_USER = "your-sender@gmail.com"
$env:SMTP_PASSWORD = "your-16-character-google-app-password"
$env:SMTP_FROM = $env:SMTP_USER
& "$env:CATALINA_HOME\bin\startup.bat"
```

Use a Google App Password only when 2-Step Verification is enabled on the sending Google account. Never commit SMTP credentials to source control or put them in JSP/Java source code. For production, use your hosting provider's secret/environment-variable manager and HTTPS. If SMTP is not configured, registration is intentionally blocked instead of creating accounts that cannot verify their email.

# LifeLink

