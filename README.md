# 🌍 E-Tour: Group Tour Booking System (Backend)

Full-stack B2C group tour booking system built with **Spring Boot 3**, **Spring Data JPA / Hibernate**, **Spring Security (JWT & OAuth2)**, and **MySQL 8**.

---

## 📡 API Overview (45 REST Endpoints across 14 Controllers)

### 1. Authentication (`/api/auth`)
- `POST /api/auth/register` - User registration with BCrypt hashing and `@Valid` validation
- `POST /api/auth/login` - User login returning Bearer JWT token
- `POST /api/auth/forgot-password` - Request password reset OTP/link
- `POST /api/auth/reset-password` - Reset account password
- `POST /api/auth/google-login` - Google OAuth2 authentication flow

### 2. Tour Bookings (`/api/Booking`)
- `GET /api/Booking` - List all bookings
- `POST /api/Booking` - Create a new booking
- `GET /api/Booking/customer/{customerId}` - Get bookings by customer ID
- `GET /api/Booking/{bookingId}` - Get booking details
- `GET /api/Booking/status/{bookingId}` - Check booking status
- `GET /api/Booking/invoice/{bookingId}` - Retrieve booking invoice metadata

### 3. Tour Catalog & Search (`/api/tours`, `/api/Search`, `/api/categories`)
- `GET /api/tours` - List all tours
- `GET /api/tours/tour-id` - Get tour by query ID
- `GET /api/tours/{identifier}` - Get tour by slug or ID
- `GET /api/tours/details/{catId}` - Get tour details with categories
- `POST /api/tours/by-category-ids` - Filter tours by categories
- `GET /api/Search/name/{query}` - Search tours by name
- `GET /api/Search/date` - Search tours by date range
- `GET /api/Search/cost/{maxCost}` - Filter tours by maximum budget
- `GET /api/categories/home` - Retrieve homepage tour categories
- `GET /api/categories/{catCode}` - Get category details

### 4. Passengers (`/api/passenger`)
- `POST /api/passenger/add` - Add passenger details to booking
- `GET /api/passenger/booking/{bookingId}` - List passengers for booking
- `GET /api/passenger/{id}` - Get passenger details

### 5. Payments & Invoices (`/api/payment`, `/payment-gateway`, `/api/invoices`, `/api/email`)
- `POST /api/payment/pay` - Process payment
- `GET /api/payment/{paymentId}` - Get payment record
- `GET /api/payment/receipt/{bookingId}` - Generate payment receipt
- `POST /payment-gateway/create-order` - Create Razorpay order
- `POST /payment-gateway/confirm-payment` - Confirm payment signature
- `POST /payment-gateway/webhook` - Webhook handler
- `GET /api/invoices/{bookingId}/download` - Download PDF invoice
- `POST /api/email/booking-confirmation` - Trigger email confirmation

### 6. AI Customer Care (`/api/ai/customer-care`)
- `POST /api/ai/customer-care/chat` - Interactive assistant chat
- `POST /api/ai/customer-care/recommend-tours` - Personalized tour recommendations
- `GET /api/ai/customer-care/faq` - Frequently asked questions
- `GET /api/ai/customer-care/health` - Assistant health status

---

## 🔐 Security Architecture

- **Password Hashing**: `BCryptPasswordEncoder` configured in `PasswordConfig.java` and utilized in `AuthService.java`.
- **Stateless Authentication**: JWT Bearer token authentication via `JwtAuthFilter.java` and `JwtUtil.java`.
- **OAuth2 Social Login**: Google OAuth2 federated login via `OAuth2SuccessHandler.java` and `spring-boot-starter-security-oauth2-client`.
- **Input Validation**: Request DTOs enforced with `@Valid`, `@NotNull`, `@NotBlank`, and `@Size` annotations.
- **CORS Configuration**: `@CrossOrigin` annotations configured on controllers for React frontend communication.

---

## 📊 Observability (Actuator)

Configured management endpoints in `application.properties`:
- `management.endpoints.web.exposure.include=health,metrics,env,info,booking-stats`
- `management.endpoint.health.show-details=always`
- Available endpoints: `/actuator/health`, `/actuator/metrics`, `/actuator/info`

---

## 🧪 Testing & Verification

Automated test execution and JaCoCo coverage reporting:
```bash
mvn clean test jacoco:report
```

### Real Measured Test Metrics (Maven Surefire & JaCoCo)
- **Total Tests Run**: **39 tests** (100% pass rate: 39 passed, 0 failed, 0 errors, 0 skipped)
- **Unit Test Suites (JUnit 5 + Mockito)**: 34 tests across all service implementations and controllers:
  - `AuthServiceTest`: 6 tests (Registration, Duplicate Email Rejection, Login, Invalid Password, Password Reset)
  - `CategoryServiceTest`: 4 tests (Home Categories, Category Hierarchy, Category IDs)
  - `PassengerServiceTest`: 5 tests (Add Passenger, Auto-pax type categorization, Booking associations)
  - `TourServiceTest`: 4 tests (Tour Retrieval by ID, Not Found Handling, Catalog Listing)
  - `PaymentServiceTest`: 5 tests (Payment Record Creation, Razorpay confirmation, Status retrieval)
  - `BookingStatusServiceTest`: 3 tests (Status lookup, All statuses retrieval)
  - `SearchServiceTest`: 3 tests (Query search, Date filter, Budget range filter)
  - `BookingServiceTest` & `BookingControllerTest`: 3 tests (Booking lifecycle and MockMvc dispatch)
  - `BackEndJavaApplicationTests`: 1 test (Spring Boot Application Context Load)
- **Integration Test Suite (`ETourIntegrationTest`)**: 5 `@SpringBootTest` + `MockMvc` integration tests running against in-memory H2 MySQL mode:
  - `IT-01`: Public user registration via `POST /api/auth/register` returns `201 Created`
  - `IT-02`: Public user login via `POST /api/auth/login` returns valid JWT Bearer token
  - `IT-03`: Protected endpoint (`GET /api/Booking`) without Bearer token returns `401 Unauthorized`
  - `IT-04`: Protected endpoint (`GET /api/Booking`) with valid JWT token succeeds (`200 OK`)
  - `IT-05`: Complete end-to-end booking flow: persists customer, category, departure, tour, status; posts new booking; adds passenger; verifies persistence via `GET /api/passenger/booking/{id}` and `GET /api/Booking/{id}`.
- **JaCoCo Line Coverage**:
  - **Overall Project Coverage**: **38.18%** (683 covered lines / 1,789 total executable lines)
  - **Services Package Coverage**: **33.03%** (219 covered lines / 663 total lines across `com.example.services*`)
  - *Previous baseline prior to audit: 14.11% overall, 10.71% in services on 4 tests.*

### Evidence & Artifacts
Raw test reports, metrics, and API collections are saved under [`docs/test-results/`](docs/test-results/):
- JaCoCo Line & Branch CSV: [`docs/test-results/jacoco.csv`](docs/test-results/jacoco.csv)
- Surefire Test Execution Output: [`docs/test-results/surefire-summary.txt`](docs/test-results/surefire-summary.txt)
- Controller Endpoints Catalog: [`docs/test-results/endpoints-list.txt`](docs/test-results/endpoints-list.txt)
- Postman Collection (21 requests): [`docs/test-results/ETour_API.postman_collection.json`](docs/test-results/ETour_API.postman_collection.json)

