# Challenge3-ServerAndDatabaseCommands

**Overview:** High-level repository for Sprint 1–2 deliverables of the “Server and Database Commands” challenge.  
This repo contains the technical documentation (Sprint 1) and a minimal **Java MVC** app that performs **GET** requests to the **Google Scholar Author API** via SerpApi (Sprint 2), plus a JUnit 5 test.

---

## Project Purpose
Automate the retrieval of researcher and article information from Google Scholar (via an API provider) to support integration with the university’s research database.

## Key Functionalities (High-Level)
- Perform HTTP GET requests to Google Scholar (Author API) through the API provider.
- Document endpoints, authentication, query parameters, response formats, usage limits, and minimal code examples.
- Provide a foundation for Java MVC integration (Sprint 2) and database integration (Sprint 3).

## Project Relevance
Reduces manual data collection, standardizes academic metadata retrieval, and facilitates reliable downstream integration into the institution’s research database.

---

## Documentation (Sprint 1)
- Technical Report: [`docs/GoogleScholarAPI_TechnicalReport.md`](docs/GoogleScholarAPI_TechnicalReport.md)

> **Note:** Real API keys are **not** committed. I recommend the use of environment variables or a local `.env` (gitignored).

---

## Sprint 2 — Java MVC App


### How to Run (IntelliJ)
1. Ensure Java 11+.
2. Put SerpApi key in `.env` at the project root:
```

SERPAPI_KEY=real_key

````
3. Create a **Run Configuration** (Application):
- **Main class:** `app.Main`
- **Working directory:** project root
- Load the key:
  - Enable **EnvFile** and add the project `.env`

4. Run. The console should print the author name, affiliations, and a list of articles.

---

## Tests (JUnit 5)

This project includes a JUnit 5 integration test that calls the SerpApi Google Scholar **Author** API and validates basic fields.

### IntelliJ

* Create a **JUnit** run config for class `it.AuthorFlowIT`.
* Load the key (EnvFile with `.env`, or Environment variable `SERPAPI_KEY`).

**Expected**

* Test passes asserting:

    * `author.name` is present
    * `articles[]` has at least one item
* If `SERPAPI_KEY` is missing, the test is configured to **skip** (not fail).

---

## Repository Structure (current)

```
/
├─ README.md
├─ docs/
│  ├─ GoogleScholar_API_Technical_Report.md
│  └─ screenshots/
├─ src/
│  ├─ main/java/...(MVC source)
│  └─ test/java/...(JUnit 5 test)
├─ .env                 # local only (gitignored)
├─ .gitignore
└─ pom.xml
```


