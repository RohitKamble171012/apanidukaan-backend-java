# ApaniDukaan — Backend (Java / Spring Boot)

A full rewrite of [ApaniDukaan](https://github.com/RohitKamble171012/ApaniDukaan)'s backend — originally Node.js/Express — into Java and Spring Boot, keeping the same MongoDB data model and the same Next.js frontend it was built to serve.

ApaniDukaan itself is a SaaS platform that helps small local retailers get a digital storefront: a shopkeeper lists products, customers browse and place orders without needing an account, and the shopkeeper manages everything from a dashboard.

This repo is that backend, rebuilt from scratch in Spring Boot as a way to actually learn the framework — not by following a tutorial project, but by porting a real system with real business rules already defined.

## Why this exists

I know Java, but had never touched Spring Boot before starting this. Rather than learn it in the abstract, I rebuilt a production system I already understood the requirements for. Every module here was built by first understanding what the Node version needed to do, then figuring out the Spring-idiomatic way to do it — which meant hitting real framework quirks and debugging them properly instead of copy-pasting a solution.

## What's in here

| Module | What it does |
|---|---|
| **Shop** | CRUD for a shopkeeper's storefront — name, category, contact info, visibility toggle. Slug-based public lookup. |
| **Product** | CRUD for inventory, scoped to a shop. Stock quantity, availability, pricing. Bulk import/export via Excel. |
| **Order** | Public checkout (no login required) and public order tracking by order number. Protected shopkeeper view of their orders. Server computes the real total from actual product prices — never trusts client-submitted prices. Deducts stock on order creation. |
| **Payment** | Razorpay integration — creates a Razorpay order tied to an internal order, verifies payment signatures from the frontend, and handles Razorpay's server-to-server webhook for payment confirmation. |
| **QR** | Generates a scannable QR code (PNG) encoding a shop's public storefront URL. |
| **Feedback** | Public feedback and "do you stock this item" requests, tied to a shop. Protected view for the shopkeeper to review and resolve. |
| **Auth** | Syncs a Firebase-authenticated user into a local `User` record on login. Firebase handles actual authentication; this backend only verifies tokens and tracks profile data. |
| **Upload** | Local file storage for images (product photos, etc.), served back as static files. |
| **Analytics** | Computed dashboard stats for a shopkeeper — total orders, revenue, pending orders, top-selling products — derived from existing order data, no separate analytics collection. |

## Tech stack

- **Java 21**, Spring Boot 4.1
- **MongoDB Atlas** via Spring Data MongoDB
- **Firebase Admin SDK** for authentication (token verification only — login itself happens on the frontend)
- **Razorpay Java SDK** for payments
- **Apache POI** for Excel import/export
- **ZXing** for QR code generation
- **Maven**

## Architecture notes — things worth knowing if you're reading this code

**Every write operation checks ownership, not just authentication.** Being logged in isn't enough to modify a shop's products — the backend checks that the Firebase UID making the request actually matches the shop's `ownerUid`. This shows up as a repeated pattern (`assertOwnership`) across `ShopService`, `ProductService`, `OrderService`, and `FeedbackService`.

**Public vs. protected routes are deliberate, not accidental.** Customer-facing actions — browsing a shop, placing an order, tracking an order, submitting feedback — don't require login, matching how the actual product works (customers shouldn't need an account to buy groceries). Shopkeeper actions — managing inventory, viewing orders, seeing analytics — require a valid Firebase token. This split is enforced centrally in `SecurityConfig`, not scattered across individual controllers.

**The server never trusts client-submitted prices or totals.** When an order is created, `OrderService` looks up the real `Product` price from the database and recomputes the total itself — the amount the client sends is ignored. Same logic applies to stock: availability is checked and inventory is decremented server-side as part of order creation, not left to the client to report.

**MongoDB connection is manually wired, not left to Spring Boot's auto-configuration.** This project hit a real bug: Spring Boot 4.1.0's Mongo auto-configuration wasn't correctly binding `spring.data.mongodb.uri` from properties into the actual `MongoClient` bean it creates — the property resolved correctly everywhere else (confirmed via `@Value` injection), but the auto-configured client still fell back to `localhost:27017`. The fix, in `MongoConfig`, is to define the `MongoClient`, `MongoDatabaseFactory` beans explicitly, using `@Value` to inject the URI from properties. Credentials still live only in configuration, not in code — this isn't hardcoding, it's just doing manually what auto-configuration was silently failing to do.

**Payment integrity has two independent checks.** `PaymentService.verifyPaymentSignature` confirms the frontend's claim that a payment succeeded (protects against a malicious client just lying about success). `PaymentService.verifyWebhookSignature` verifies Razorpay's own server-to-server webhook call, which is the actual source of truth and doesn't depend on the customer's browser staying open or behaving honestly.

## Setup

1. Clone the repo.
2. Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties` and fill in real values:
   - MongoDB Atlas connection string
   - Razorpay test-mode API keys
   - Razorpay webhook secret (create one in the Razorpay dashboard, even a placeholder URL works for local dev)
3. Get a Firebase service account key (Firebase Console → Project Settings → Service Accounts → Generate new private key) and save it as `src/main/resources/firebase-service-account.json`.
4. Run:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Confirm it's up:
   ```bash
   curl http://localhost:8080/api/health
   ```

Neither `application.properties` nor `firebase-service-account.json` are committed — both contain real credentials and are gitignored. The `.example` file documents the required shape without exposing anything real.

## What this project is not

This is a backend rewrite, not a from-scratch redesign. The data model, business rules, and API shape follow the original Node.js backend closely — the goal was learning Spring Boot against a real, already-defined system, not inventing a new architecture. The [original Node.js/Next.js repo](https://github.com/RohitKamble171012/ApaniDukaan) is the live, production version of this product.
