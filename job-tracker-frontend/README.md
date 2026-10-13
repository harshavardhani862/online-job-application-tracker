# Online Job Application Tracker - Frontend

React + TypeScript frontend for the Online Job Application Tracker.

## Backend

The frontend is configured for:

http://localhost:8080/api

Edit `.env` if your backend changes.

## Run

```bash
npm install
npm run dev
```

Frontend:

http://localhost:5173

Backend:

http://localhost:8080

## Main API endpoints

POST /api/auth/signup
POST /api/auth/signin

GET /api/users/profile
PUT /api/users/profile

GET /api/applications
GET /api/applications/{id}
POST /api/applications
PUT /api/applications/{id}
DELETE /api/applications/{id}

GET /api/interviews
POST /api/interviews
PUT /api/interviews/{id}
DELETE /api/interviews/{id}

GET /api/reminders
POST /api/reminders
PUT /api/reminders/{id}
DELETE /api/reminders/{id}

GET /api/dashboard/statistics

POST /api/payments/create-order
POST /api/payments/verify
GET /api/payments/my-payments
