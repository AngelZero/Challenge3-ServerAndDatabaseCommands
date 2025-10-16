# Challenge3-ServerAndDatabaseCommands

**Overview:** Repository for Sprint 1–3 deliverables of the “Server and Database Commands” challenge.
- **Sprint 1:** Technical report on Google Scholar API (via SerpApi).
- **Sprint 2:** Minimal Java **MVC** app that performs **GET** requests to the **Google Scholar Author API** and prints results.
- **Sprint 3:** **Database integration (MySQL)** that stores **3 articles per researcher** (for **2** researchers total), with basic error handling.
---

## Project Purpose
Automate the retrieval of researcher and article information from Google Scholar (via an API provider) to support integration with the university’s research database.

## Key Functionalities (High-Level)
- HTTP GET to Google Scholar (Author API) through SerpApi.
- MVC separation: Service (HTTP), Controller (orchestration), Model (Author/Article), View (console).
- Database integration: persist articles with required fields and basic error handling.

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

## Sprint 3 — Database Integration (MySQL)

Schema
```
CREATE DATABASE IF NOT EXISTS scholardb
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE scholardb;

CREATE TABLE IF NOT EXISTS articles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(1024) NOT NULL,
  authors TEXT,
  publication_date VARCHAR(32),
  abstract TEXT,
  link TEXT,
  keywords TEXT,
  cited_by INT
);


```

Local Config
```
# SerpApi
SERPAPI_KEY=real_key

# MySQL (Workbench)
DB_URL=jdbc:mysql://localhost:3306/scholardb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=root
DB_PASSWORD=password

```

## What the app does (persistence)

Controller fetches Author JSON → maps to Author + Article list.

Saves the first 3 articles for the given author_id in a single transaction.

Inserts a tag author:<author_id> into the keywords column so it can distinguish researchers without altering the schema.

The sprint only requires 2 researchers × 3 articles each.
Run the app twice with two different author_ids to meet this.

## Verify (Workbench) 
USE scholardb;

-- Total rows should be 6 after two runs
SELECT COUNT(*) AS total FROM articles;

-- Rows for a specific researcher
SELECT *
FROM articles
WHERE FIND_IN_SET('author:LSsXyncAAAAJ', REPLACE(keywords, ' ', ''));


## Repository Structure (current)

```
/
├─ README.md
├─ docs/
│  ├─ GoogleScholar_API_Technical_Report.md
│  └─ screenshots/
├─ src/
│  ├─ main/java/
│  │  ├─ app/Main.java
│  │  ├─ controller/AuthorController.java
│  │  ├─ db/DbConfig.java
│  │  ├─ db/ArticleRepository.java
│  │  ├─ model/Author.java
│  │  ├─ model/Article.java
│  │  ├─ service/ScholarAuthorClient.java
│  │  ├─ util/SimpleJson.java
│  │  └─ view/ConsoleView.java
│  └─ test/java/
│     └─ it/AuthorFlowIT.java
├─ .env               # local only (gitignored)
├─ .gitignore
└─ pom.xml

```



