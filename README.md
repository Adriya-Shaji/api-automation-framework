# API Automation Framework

Java-based API test automation framework built with **Rest Assured** and **JUnit 5** for stubbed regression, live contract testing, and database validation.



---

## Test Layers

- **Stubbed:** deterministic validation of status codes, schema, and auth flows using WireMock
- **Live:** contract-level validation against `jsonplaceholder.typicode.com`
- **Database:** H2 in-memory query validation - row counts, field lookups, payment integrity checks, timestamp window assertions

---

## Tech Stack

| Component         | Version |
|-------------------|---|
| Java              | 17 |
| Maven             | 3.x |
| JUnit 5 (Jupiter) | 5.10.2 |
| Rest Assured      | 5.4.0 |
| WireMock          | 3.3.1 |
| Allure JUnit 5    | 2.25.0 |
| Jackson           | 2.15.3 |
| AssertJ           | 3.25.3 |
| H2 (in-memory DB) | 2.2.224 |

---

## Project Structure

```
api-automation-framework/
├── Jenkinsfile
├── pom.xml
└── src/test/
    ├── java/com/adriyashaji/automation/
    │   ├── api/                                  # Test classes (stubbed, live, DB)
    │   │   ├── UserApiTest.java
    │   │   ├── FilmApiTest.java
    │   │   ├── AuthApiTest.java
    │   │   ├── LiveSmokeTest.java
    │   │   └── DatabaseTest.java
    │   ├── base/                                 # Shared setup
    │   │   └── BaseTest.java
    │   ├── models/                               # POJOs
    │   │   ├── User.java
    │   │   └── Film.java
    │   ├── stubs/                                # WireMock stubs
    │   │   ├── UserStubs.java
    │   │   ├── FilmStubs.java
    │   │   └── AuthStubs.java
    │   └── utils/                                # Auth, config, DB helpers
    │       ├── AuthManager.java
    │       ├── ConfigReader.java
    │       └── DatabaseHelper.java
    └── resources/
        ├── config/                               # Environment configs
        │   ├── local.properties.example          # copy to local.properties and fill in values
        │   ├── live.properties
        │   ├── staging.properties
        │   └── prod.properties
        └── schemas/                              # JSON schemas
            ├── user-schema.json
            └── film-schema.json
```
 
---

## Running Tests

**Prerequisites:**
- Java 17
- Maven 3.x
- Allure CLI (optional, for viewing reports locally)

**Setup:**
```bash
cp src/test/resources/config/local.properties.example \
   src/test/resources/config/local.properties
```

Default values are sufficient for mock-mode runs. Do not commit `local.properties`.

**Run all stubbed tests (default)**

```bash
mvn clean test
```

**Run by tag**

```bash
mvn clean test -Dgroups=smoke
mvn clean test -Dgroups=regression
mvn clean test -Dgroups=database
```

**Run with environment**

In mock mode, `base.url` is ignored. Credentials and DB settings are still loaded from the selected config.

```bash
mvn clean test -Denv=staging
```

**Run live tests:**

Live tests are tagged with `live` and excluded by default.
```bash
mvn test -Dmode=live -Denv=live -Dgroups=live -DexcludedGroups=
```

**Reporting**

```bash
mvn allure:report
allure serve target/allure-results
```

---
## CI

The Jenkins pipeline supports:

- `ENV` – choice parameter: `local`, `staging`, or `prod`
- `RUN_LIVE_TESTS` – boolean parameter to enable or skip live tests

Each run publishes: JUnit XML reports, Allure results

---

## Design decisions

**Stubs separated from live tests.** WireMock tests run in mock mode against a dynamic local port. Live tests run separately against a real endpoint. Mixing them in one layer makes failure diagnosis harder — a flaky live response looks the same as a broken assertion.

**Stub tests assert exact values, live tests assert structure only.** Stubs return fixed data so assertions can be strict (`equalTo("Inception")`). Live responses vary by environment so assertions check field presence and type only, not values.

**Stubs split by domain.** `UserStubs`, `FilmStubs`, `AuthStubs` each own their stub registration. Earlier design had everything in one `setupStubs()` method in `BaseTest` — adding a new domain meant editing a shared base class. Extracting per-domain classes keeps ownership clear.

**Auth credentials resolved via environment variables first.** `AuthManager` checks `AUTH_USERNAME` / `AUTH_PASSWORD` before falling back to config file values. This lets CI inject credentials without committing them to source.

**`-Denv` selects config at runtime.** Switching environments requires no code change — only the properties file changes. `base.url`, credentials, and DB connection all come from the selected config.
