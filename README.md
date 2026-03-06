# API Automation Framework
A professional REST API test automation framework built with Java, RestAssured and JUnit5.

## Tech Stack
| Tool | Purpose |
|------|---------|
| Java 17 | Core language |
| Maven | Build and dependency management |
| RestAssured | API test automation |
| JUnit5 | Test runner and Assertions |
| AssertJ | Fluent assertions |
| Jackson | JSON serialisation |
| PostgreSQL + JDBC | Database validation |
| Allure | Test reporting |
| Jenkins | CI/CD |
 
## Framework Architecture
````
src/test/java/com/adriyashaji/automation

├── base/
│   ├── BaseTest.java         # Parent class - shared RequestSpec, ResponseSpec
│   ├── ConfigReader.java     # Loads environment specific properties
│   ├── AuthManager.java      # Bearer token management
│
├── api/
│   ├── UserApiTest.java      # User endpoint tests
│   ├── FilmApiTest.java      # Film endpoint tests
│
└── db/
    ├── DatabaseHelper.java   # JDBC Utility for DB validation

src/test/resources/
├── config.properties         # Environment config (gitignored)
└── schemas/
     └──user-schema.json      # JSON schema for contract testing
````

## Running Tests
```` bash
# Run all test
mvn clean test

# Run smoke tests only
mvn test -Dgroups=smoke

#Run against QA env
mvn test -Denv=qa

#Generate Allure report
mvn allure:serve
````

## Key Features

- BaseTest architecture - shared setup across all test classes
- Environment switching - dev, QA, UAT via maven properties
- Bearer token authentication with centralised AuthManager
- JSON Schema contract testing
- DB validation - API assertions backed by JDBC queries
- Allure reporting with Epic/Feature/Story organisation
- Jenkins CI pipeline


## About

Test automation engineer with 7 years of experience across banking, financial services,
and enterprise applications. Includes contract automation engineering at Nationwide
Building Society (2021–2025), one of the UK's largest financial institutions.
This framework demonstrates API testing, database validation, and CI/CD integration
using tools standard across UK banks and fintechs.