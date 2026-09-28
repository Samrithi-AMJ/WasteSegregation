# Smart Municipal Solid Waste Segregation and Collection Tracker

A full-stack municipal waste management system that helps municipalities monitor
household waste collection, waste segregation, collection workers, garbage
vehicles, missed/overdue collections, and citizen complaints.

Built as a college semester project using **Java 17, Spring Boot, MySQL, Spring
Data JPA/Hibernate**, and a **plain HTML5 + CSS3 + JavaScript** frontend
communicating with the backend over a REST API.

---

## 1. Features

- Citizen, worker, and vehicle management (full CRUD)
- Waste collection record tracking (wet / dry / plastic / paper / other)
- Waste segregation tracking with an automatically-derived segregation status
  (PROPER / PARTIAL / IMPROPER) based on how many categories were separated
- **Missed / overdue collection detection** — any collection still `PENDING`
  more than 7 days after its scheduled date is automatically surfaced as
  overdue on the dashboard, with no manual flagging required
- **Segregation percentage** — `properly segregated waste ÷ total waste × 100`,
  calculated live from the database
- Area/ward-wise statistics: total waste, total collections, missed
  collections, segregation rate
- Citizen complaint submission and status tracking
  (PENDING → ASSIGNED → IN_PROGRESS → RESOLVED / REJECTED)
- Role-based views for **ADMIN** (full access) and **CITIZEN**
  (own records + complaints only)
- Professional dashboard with Chart.js visualizations, all values computed
  from live API data — nothing is hard-coded
- Reports page with daily / weekly / monthly waste trends and filters by
  date, area, and waste type
- Search, filter, add, edit, delete on every management screen
- Centralized REST exception handling with structured JSON error responses
- Sample data (10 citizens, 5 workers, 5 vehicles, 25 collections, complaints,
  and demo logins) is seeded automatically on first run

---

## 2. Technology Stack

| Layer          | Technology                                   |
|-----------------|-----------------------------------------------|
| Backend         | Java 17, Spring Boot 3.3                      |
| Data Access     | Spring Data JPA / Hibernate                   |
| Database        | MySQL 8                                       |
| Build Tool      | Maven                                         |
| Frontend        | HTML5, CSS3, vanilla JavaScript (fetch API)   |
| Charts          | Chart.js (via CDN)                            |
| Password Hashing| BCrypt (spring-security-crypto)               |
| API Testing     | Postman                                       |

No React/Angular/Vue and no non-Java backend framework is used anywhere.

---

## 3. Architecture

```
Frontend (HTML/CSS/JS)
        │  fetch()
        ▼
   REST Controller
        │
        ▼
   Service Layer  (business logic: statistics, segregation %, overdue detection)
        │
        ▼
   Repository Layer (Spring Data JPA)
        │
        ▼
   Hibernate / JPA
        │
        ▼
      MySQL
```

### Package structure

```
com.example.wastemanagement
├── WasteManagementApplication.java
├── controller/   REST controllers (one per resource)
├── service/      Business logic
├── repository/   Spring Data JPA repositories
├── entity/       JPA entities + enums
├── dto/          Request/response DTOs (flat, no circular references)
├── exception/    Custom exceptions + @RestControllerAdvice handler
└── config/       CORS config, BCrypt bean, sample DataSeeder
```

---

## 4. Database Design

Database name: `smart_waste_management` (created automatically if it doesn't
exist, thanks to `createDatabaseIfNotExist=true` in the JDBC URL).

### Tables

| Table               | Key columns                                                                 |
|----------------------|------------------------------------------------------------------------------|
| `users`              | user_id, username, password (BCrypt hash), role, citizen_id                 |
| `citizens`           | citizen_id, name, phone, email, address, area, ward_number                  |
| `workers`            | worker_id, name, phone, assigned_area, status                               |
| `vehicles`           | vehicle_id, vehicle_number, vehicle_type, driver_name, capacity, status     |
| `waste_collections`  | collection_id, citizen_id (FK), worker_id (FK), vehicle_id (FK), collection_date, area, waste_type, quantity, status |
| `segregations`       | segregation_id, collection_id (FK, unique), wet/dry/plastic/paper/other_quantity, segregation_status |
| `complaints`          | complaint_id, citizen_id (FK), location, description, complaint_date, status |

### ER Diagram (textual)

```
USER (1) ───login-links-to──── (1) CITIZEN  [only for role = CITIZEN]

CITIZEN (1) ──────< (many) WASTE_COLLECTION
CITIZEN (1) ──────< (many) COMPLAINT

WORKER  (1) ──────< (many) WASTE_COLLECTION
VEHICLE (1) ──────< (many) WASTE_COLLECTION

WASTE_COLLECTION (1) ────── (1) SEGREGATION
```

Cardinalities:
- One **Citizen** has many **WasteCollection** records and many **Complaint** records.
- One **Worker** can handle many **WasteCollection** records.
- One **Vehicle** can be used for many **WasteCollection** records.
- One **WasteCollection** has at most one **Segregation** record (`@OneToOne`).
- `@JsonIgnore` is used on the "many" side back-references so API responses
  never produce circular JSON; all cross-entity references in responses use
  flat DTOs with plain ids and names instead of nested entity graphs.

---

## 5. Setup Instructions

### Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL 8 running locally (or reachable) 

### Step 1 — Configure the database
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_waste_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

You do **not** need to manually create the database or tables — Hibernate
(`spring.jpa.hibernate.ddl-auto=update`) and the JDBC URL flag create them
automatically on first run.

### Step 2 — Build and run the backend

```bash
cd smart-waste-management
mvn clean install
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

On first run only, `DataSeeder` populates the database with sample citizens,
workers, vehicles, collections, segregation records, complaints, and login
users. You'll see a confirmation banner with demo credentials printed in the
console.

### Step 3 — Access the frontend
Because the HTML/CSS/JS files live in `src/main/resources/static`, Spring
Boot serves them directly — no separate frontend server is required:

```
http://localhost:8080/login.html
```

---

## 6. Login Credentials (seeded automatically)

| Role    | Username  | Password    |
|---------|-----------|-------------|
| Admin   | admin     | admin123    |
| Citizen | citizen1  | citizen123  |
| Citizen | citizen2  | citizen123  |
| Citizen | citizen3  | citizen123  |

Passwords are hashed with **BCrypt** before being stored — nothing is kept
as plain text.

---

## 7. REST API Endpoints

Base URL: `http://localhost:8080/api`

### Auth
| Method | Endpoint         | Description         |
|--------|------------------|----------------------|
| POST   | `/auth/login`    | Login (username/password) |

### Citizens
| Method | Endpoint              |
|--------|------------------------|
| GET    | `/citizens`            |
| GET    | `/citizens?search=name`|
| GET    | `/citizens/{id}`       |
| POST   | `/citizens`            |
| PUT    | `/citizens/{id}`       |
| DELETE | `/citizens/{id}`       |

### Workers / Vehicles
Same CRUD pattern under `/workers` and `/vehicles`.

### Waste Collections
| Method | Endpoint                         |
|--------|-----------------------------------|
| GET    | `/collections`                    |
| GET    | `/collections?area=Ward 1`        |
| GET    | `/collections?status=MISSED`      |
| GET    | `/collections?wasteType=WET`      |
| GET    | `/collections?citizenId=1`        |
| GET    | `/collections/{id}`               |
| POST   | `/collections`                    |
| PUT    | `/collections/{id}`               |
| DELETE | `/collections/{id}`               |

### Segregation
Standard CRUD under `/segregations`.

### Complaints
| Method | Endpoint                          |
|--------|-------------------------------------|
| GET    | `/complaints`                       |
| GET    | `/complaints?status=PENDING`        |
| GET    | `/complaints?citizenId=1`           |
| POST   | `/complaints`                       |
| PUT    | `/complaints/{id}`                  |
| PATCH  | `/complaints/{id}/status`           |
| DELETE | `/complaints/{id}`                  |

### Dashboard (smart features)
| Method | Endpoint                              | Returns                                  |
|--------|-----------------------------------------|--------------------------------------------|
| GET    | `/dashboard/statistics`                | Totals, segregation rate, complaint counts |
| GET    | `/dashboard/waste-by-type`             | kg per waste type                          |
| GET    | `/dashboard/waste-by-area`             | Per-area totals + segregation rate         |
| GET    | `/dashboard/collection-status`         | Counts per collection status               |
| GET    | `/dashboard/complaint-status`          | Counts per complaint status                |
| GET    | `/dashboard/missed-collections`        | Overdue collections with days overdue      |

### Example response — `GET /api/collections`
```json
[
  {
    "collectionId": 1,
    "citizenId": 1,
    "citizenName": "Arun Kumar",
    "workerId": 2,
    "workerName": "Karthik Raja",
    "vehicleId": 3,
    "vehicleNumber": "TN38AB9012",
    "collectionDate": "2026-09-24",
    "area": "Ward 1",
    "wasteType": "WET",
    "quantity": 12.5,
    "status": "COLLECTED",
    "segregated": true
  }
]
```

### Example error response
```json
{
  "timestamp": "2026-09-24T10:15:30",
  "status": 404,
  "message": "Worker with ID 10 not found"
}
```

---

## 8. Postman Testing

A ready-to-import collection is provided at
`postman/SmartWasteManagement.postman_collection.json`.

1. Open Postman → **Import** → select the JSON file.
2. The collection variable `baseUrl` defaults to `http://localhost:8080/api`.
3. Run requests top-to-bottom per folder (Citizens, Workers, Vehicles,
   Collections, Segregation, Complaints, Dashboard).
4. **POST** requests return `201 Created` with the created resource.
5. **GET/PUT** requests return `200 OK`.
6. **DELETE** requests return `204 No Content`.
7. Requesting a non-existent id (e.g. `GET /citizens/9999`) returns
   `404 Not Found` with a structured JSON error body.

---

## 9. Running Tests

```bash
mvn test
```

Included tests cover:
- Citizen create / retrieve / update / delete
- Resource-not-found exception handling
- Complaint creation (defaults to `PENDING`) and status update
- Dashboard statistics calculation (total waste aggregation)

---

## 10. How the Smart Features Work

1. **Total waste** — `DashboardService` sums `quantity` across all
   `WasteCollection` rows via a JPQL `SUM()` query, per waste type and overall.
2. **Segregation percentage** — for every `Segregation` record marked
   `PROPER`, its total quantity is summed and divided by the total waste
   collected system-wide (or per-area for the area report).
3. **Segregation status auto-detection** — when a segregation record is
   submitted without an explicit status, `SegregationService` derives it:
   3+ waste categories used → `PROPER`, 2 → `PARTIAL`, 0–1 → `IMPROPER`.
4. **Missed/overdue detection** — any collection already marked `MISSED`,
   or still `PENDING` more than 7 days after its `collectionDate`, is
   surfaced by `/dashboard/missed-collections` with the computed
   `daysOverdue`. This is plain Java date arithmetic (`ChronoUnit.DAYS`) —
   no external services or ML involved.
5. **Area-wise statistics** — collections are grouped by their `area`
   field and aggregated on the fly (no denormalized area table needed).

---

## 11. Final Verification Checklist

- [x] All entities, DTOs, repositories, services, and controllers compile
      against consistent field/method names (cross-checked by static analysis)
- [x] Every frontend `fetch()` call targets an endpoint that exists on the
      matching controller
- [x] No circular JSON: back-references (`Citizen.wasteCollections`,
      `Worker.wasteCollections`, etc.) are `@JsonIgnore`d; all API responses
      use flat DTOs
- [x] Validation annotations on entities/DTOs + a global exception handler
      return structured `400`/`404`/`500` JSON errors
- [x] Passwords are BCrypt-hashed, never stored in plain text
- [x] Dashboard numbers are 100% computed from the database — nothing is
      hard-coded in the frontend
- [x] CORS is configured for local development
- [x] Sample data seeds automatically and is idempotent (only seeds once)

---

## 12. Future Enhancements

- GPS tracking and live route optimization for collection vehicles
- IoT smart bin fill-level sensors
- Native mobile application
- SMS/email notifications for missed collections and complaint updates
- AI-based waste classification from photos
- Predictive collection planning based on historical patterns
- Real-time vehicle location tracking on a map

---

## 13. Screenshots

_(Add screenshots of the login page, dashboard, and management screens here
after running the application.)_
