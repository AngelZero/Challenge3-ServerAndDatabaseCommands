# Google Scholar API (via SerpApi) — Technical Report

**Scope.** Practical notes for using SerpApi’s Google Scholar engine to retrieve research results for automation and integration.
**API base.** All requests go through SerpApi’s unified search endpoint with `engine=google_scholar` via HTTP **GET** (e.g., `https://serpapi.com/search?engine=google_scholar`). ([SerpApi][1])

---

## 1) Endpoints (URLs used to access different API functions)

SerpApi exposes Google Scholar through a single search endpoint; the behavior is controlled by parameters:

| Endpoint                        | Method | Purpose (high-level)                                         | Notes                                                                                |
| ------------------------------- | ------ | ------------------------------------------------------------ | ------------------------------------------------------------------------------------ |
| `/search?engine=google_scholar` | GET    | Runs a Google Scholar search and returns structured results. | Public entry point documented by SerpApi; try it in their Playground. ([SerpApi][1]) |

> Tip: Related “sub-APIs” like Author/Cite/Profiles are linked from the same Google Scholar docs page’s sidebar, but the generic Scholar search uses the `/search` endpoint with `engine=google_scholar`. ([SerpApi][1])

---

## 2) Authentication methods (how to obtain and use keys/tokens)

* **API Key required.** Pass your SerpApi **`api_key`** with each request. ([SerpApi][1])
* **Where it comes from.** After registering, your key is available in the account dashboard (menu includes “Api Key”/“Billing Information”). (Location visible on the same docs site’s navbar.) ([SerpApi][1])
* **How to send it.** As a query parameter, e.g. `&api_key=YOUR_KEY`. (This is the documented required parameter.) ([SerpApi][1])
* **Security.** Don’t commit real keys; use environment variables. (Best practice; not provider-specific.)

---

## 3) Query parameters (options to filter and customize searches)

**Core parameters**

* `q` (required): search query string. Supports helpers like `author:` and `source:`. Constraints with `cites` and `cluster` are noted below. ([SerpApi][1])
* `engine` (required): set to `google_scholar`. ([SerpApi][1])
* `api_key` (required): your SerpApi key. ([SerpApi][1])

**Advanced Google Scholar parameters**

* `cites` (optional): article ID to trigger **Cited By** searches. Using `cites` makes `q` optional; using `cites` together with `q` searches within citing articles. ([SerpApi][1])
* `cluster` (optional): article ID to trigger **All Versions** searches. Must not be used together with `q` and `cites`. ([SerpApi][1])
* `as_ylo` / `as_yhi` (optional): include results **from year** / **until year** (range filter). ([SerpApi][1])
* `scisbd` (optional): “added in last year, sorted by date”; `0` relevance (default), `1` abstracts, `2` everything. ([SerpApi][1])

**Localization**

* `hl` (optional): interface language (two-letter code, e.g., `en`, `es`, `fr`). ([SerpApi][1])
* `lr` (optional): limit by one or more languages using `lang_{code}` with `|` delimiter. ([SerpApi][1])

**Pagination**

* `start` (optional): result offset (0, 10, 20, …). ([SerpApi][1])
* `num` (optional): results per page, `1`–`20` (default `10`). ([SerpApi][1])

**Search type / filters**

* `as_sdt` (optional): as **filter** for patents (exclude `0` default / include `7`) or as **search type** for **case law** (`4`, optionally with court codes). ([SerpApi][1])
* `safe` (optional): adult content filter (`active` / `off`). ([SerpApi][1])
* `filter` (optional): “Similar/Omitted results” filters on (`1`) or off (`0`). ([SerpApi][1])
* `as_vis` (optional): exclude citations (`1`) or include (`0` default). ([SerpApi][1])
* `as_rr` (optional): show only **review articles** (`1`) or all (`0` default). ([SerpApi][1])

**SerpApi behavior**

* `no_cache` (optional): force fresh fetch; otherwise SerpApi may serve a cached result (cache expires after ~1h; cached searches are **free** and **don’t count** toward your monthly searches). ([SerpApi][1])
* `async`, `output`, `json_restrictor`, `zero_trace`: execution/output controls (see docs section). ([SerpApi][1])

---

## 4) Response formats (how returned data is structured)

* **Default output:** `json` (or `html` if requested via `output`). ([SerpApi][1])
* **Top-level status:** `search_metadata.status` goes `Processing → Success || Error`; failures include an `error` message; `search_metadata.id` holds the SerpApi search ID. ([SerpApi][1])
* **Results:** JSON includes structured **organic results** for Scholar. (See SerpApi’s “API Results → JSON Results”.) ([SerpApi][1])

*Minimal illustrative shape (keys vary by result type):*

```json
{
  "search_metadata": { "status": "Success", "id": "..." },
  "organic_results": [
    {
      "title": "Paper title ...",
      "link": "https://...",
      "publication_info": { "authors": "...", "year": 2024 },
      "cited_by": { "value": 12 }
    }
  ]
}
```

---

## 5) Usage limits (restrictions on number of requests)

* **Plan-based quotas.** Your monthly search quota depends on your SerpApi plan (see your account/billing pages for exact numbers). ([SerpApi][1])
* **Caching does not count.** Cached searches (same query + same params) are served for up to ~1 hour and **do not** count against your monthly searches; you can force fresh results with `no_cache=true`. ([SerpApi][1])
* **Practical guidance.** Pace requests during development; prefer cached responses while iterating; monitor status/error fields and back off on failures.

---

## 6) Code examples (demonstrations)

> Replace placeholders before running. Keep your real `api_key` outside source control.

### A) `curl`

```bash
curl -G "https://serpapi.com/search" \
  --data-urlencode "engine=google_scholar" \
  --data-urlencode "q=quantum entanglement" \
  --data-urlencode "as_ylo=2020" \
  --data-urlencode "num=10" \
  --data-urlencode "api_key=${SERPAPI_KEY}"
```

### B) Java (HttpClient)

```java
import java.net.URI;
import java.net.http.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ScholarSearch {
  private static String enc(String s){ return URLEncoder.encode(s, StandardCharsets.UTF_8); }

  public static void main(String[] args) throws Exception {
    String apiKey = System.getenv("SERPAPI_KEY");
    String url = "https://serpapi.com/search"
      + "?engine=google_scholar"
      + "&q=" + enc("quantum entanglement")
      + "&num=10"
      + "&api_key=" + enc(apiKey);

    HttpClient http = HttpClient.newHttpClient();
    HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
    HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

    if (res.statusCode() / 100 == 2) {
      System.out.println(res.body()); // parse JSON with your preferred library
    } else {
      System.err.println("Error: " + res.statusCode() + " -> " + res.body());
    }
  }
}
```

### C) Python (requests)

```python
import os, requests

params = {
  "engine": "google_scholar",
  "q": "quantum entanglement",
  "num": 10,
  "api_key": os.environ["SERPAPI_KEY"]
}
r = requests.get("https://serpapi.com/search", params=params, timeout=20)
r.raise_for_status()
data = r.json()  # contains 'search_metadata', 'organic_results', etc.
print(data.get("search_metadata", {}))
```

---

## References

* **SerpApi – Google Scholar API** (endpoint, parameters, status/JSON, caching & required `api_key`). ([SerpApi][1])
* **SerpApi docs site navbar (Account/Api Key/Billing/Manage Plan)** — location of key/plan settings. ([SerpApi][1])

