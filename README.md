
# API Automation Framework

Java-based API test automation framework using Rest Assured and JUnit 5. Tests run across three layers: WireMock-stubbed regression coverage, live contract/smoke tests against a real endpoint, and standalone H2 database validation.

---

## Tech stack

| Component | Version |
|---|---|
| Java | 17 |
| Maven | 3.x |
| JUnit 5 (Jupiter) | 5.10.2 |
| Rest Assured | 5.4.0 |
| WireMock | 3.3.1 |
| Allure JUnit 5 | 2.25.0 |
| Jackson | 2.15.3 |
| AssertJ | 3.25.3 |
| H2 (in-memory DB) | 2.2.224 |
 
---

## Test layers

| Layer | Classes | What it validates |
|---|---|---|
| Stubbed (mock) | `UserApiTest`, `FilmApiTest`, `AuthApiTest` | Status codes, response shape, JSON schema, auth flows — deterministic, no external service |
| Live contract | `LiveSmokeTest` | Contract shape against `jsonplaceholder.typicode.com` — catches field renames, wrong status codes, removed properties |
| Database | `DatabaseTest` | Row counts, field values, payment integrity, timestamp window |

Stubbed tests run against WireMock on a dynamic port. Live tests are excluded from the default Maven run and must be explicitly opted in.
 
---

## Folder structure

```
api-automation-framework/
├── Jenkinsfile
├── pom.xml
└── src/test/
    ├── java/com/adriyashaji/automation/
    │   ├── api/
    │   │   ├── UserApiTest.java
    │   │   ├── FilmApiTest.java
    │   │   ├── AuthApiTest.java
    │   │   ├── LiveSmokeTest.java
    │   │   └── DatabaseTest.java
    │   ├── base/
    │   │   └── BaseTest.java
    │   ├── models/
    │   │   ├── User.java
    │   │   └── Film.java
    │   ├── stubs/
    │   │   ├── UserStubs.java
    │   │   ├── FilmStubs.java
    │   │   └── AuthStubs.java
    │   └── utils/
    │       ├── AuthManager.java
    │       ├── ConfigReader.java
    │       └── DatabaseHelper.java
    └── resources/
        ├── config/
        │   ├── local.properties.example   # copy to local.properties and fill in values
        │   ├── live.properties
        │   ├── staging.properties
        │   └── prod.properties
        └── schemas/
            ├── user-schema.json
            └── film-schema.json
```
 
---

## Running tests locally

**Prerequisites**

- Java 17
- Maven 3.x
- Allure CLI — optional, only needed to serve reports locally (`brew install allure` on Mac, or see [Allure docs](https://allurereport.org/docs/install/))
  **Setup**

Copy the example config before running anything:

```bash
cp src/test/resources/config/local.properties.example \
   src/test/resources/config/local.properties
```

Default values in the example are sufficient for mock-mode runs. Do not commit `local.properties`.

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

**Switch environment**

The `-Denv` flag selects the matching properties file from `src/test/resources/config/`. In mock mode, `base.url` is ignored but credentials and DB config are still loaded from the selected file.

```bash
mvn clean test -Denv=staging
```

**Run live contract tests**

`-DexcludedGroups=` clears the default `live` exclusion set in `pom.xml`, which is required to allow the live-tagged tests to run.

```bash
mvn test -Dmode=live -Denv=live -Dgroups=live -DexcludedGroups=
```

**Generate Allure report**

```bash
# Generate static HTML from a prior test run
mvn allure:report
 
# Serve in browser (requires Allure CLI)
allure serve target/allure-results
```

---
## CI

Jenkins pipeline defined in `Jenkinsfile`. Exposes `ENV` as a choice parameter (`local`, `staging`, `prod`) and `RUN_LIVE_TESTS` as a boolean flag. Allure results are published as a post-stage artifact and JUnit XML is archived on every run.
 
