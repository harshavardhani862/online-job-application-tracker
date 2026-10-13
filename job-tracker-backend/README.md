# Online Job Application Tracker - Backend

## Stack
Java 17, Spring Boot, Spring Security, JWT, PostgreSQL, Razorpay.

## 1. Database
Create a PostgreSQL database:
job_tracker_db

## 2. Configuration
Configure environment variables using `.env.example` as a reference before
starting the backend (Spring Boot does not load `.env.example` automatically).
The PostgreSQL connection defaults to `job_tracker_db` on localhost; set
`DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` to match the server/database shown
in pgAdmin. Application create, update, and delete requests are committed to
that same PostgreSQL database (`job_applications` table).

Set `JWT_SECRET` and Razorpay test credentials in the environment as well.
Never put database, mail, JWT, or Razorpay secrets in source control or in the
React frontend.

## 3. Run
mvn spring-boot:run

Backend:
http://localhost:8080

Swagger:
http://localhost:8080/swagger-ui.html

## 4. Authentication
POST /api/auth/signup
POST /api/auth/signin

Use:
Authorization: Bearer <JWT>

## 5. Main APIs
Applications:
GET/POST /api/applications
GET/PUT/DELETE /api/applications/{id}

Interviews:
GET/POST /api/interviews
GET/PUT/DELETE /api/interviews/{id}

Reminders:
GET/POST /api/reminders
GET/PUT/DELETE /api/reminders/{id}

Dashboard:
GET /api/dashboard/statistics

Users:
GET /api/users/profile
PUT /api/users/profile

Payments:
POST /api/payments/create-order
POST /api/payments/verify
GET /api/payments/my-payments

Razorpay amount is stored in paise. 49900 = Rs. 499.


## Email Notification Feature

This project now includes Gmail SMTP email notifications for job follow-up reminders.

See `EMAIL_NOTIFICATION_SETUP.md` for complete setup and testing instructions.

Main backend components:
- `EmailNotificationService`
- `ReminderNotificationScheduler`
- `NotificationController`

The reminder table records `notification_sent` and `notification_sent_at` to prevent duplicate emails.

## Automatic application email tracking

The backend can poll one Gmail inbox over IMAP and add application confirmations
or update matching applications when it detects interview, selection, offer, or
rejection messages. Create a Gmail App Password, then set:

- `MAIL_TRACKING_ENABLED=true`
- `MAIL_TRACKING_USERNAME` and `MAIL_TRACKING_PASSWORD` to the Gmail mailbox and
  its App Password
- `MAIL_TRACKING_OWNER_EMAIL` to the email address used to sign in to this
  Job Tracker account

Leave tracking disabled until these values are configured. Tracking runs against
the same PostgreSQL database as the web application and never marks scanned
messages as read. This configuration tracks one Gmail inbox for one Job Tracker
account per backend instance.

For example, in PowerShell set the database and tracking values in the terminal
where the backend will run, then start Spring Boot:

```powershell
$env:DB_PASSWORD = "<your PostgreSQL password>"
$env:MAIL_TRACKING_ENABLED = "true"
$env:MAIL_TRACKING_USERNAME = "your Gmail address"
$env:MAIL_TRACKING_PASSWORD = "<your Gmail App Password>"
$env:MAIL_TRACKING_OWNER_EMAIL = "<the same email used to sign in to Job Tracker>"
.\mvnw.cmd spring-boot:run
```
