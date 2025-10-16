package it;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import service.ScholarAuthorClient;
import util.SimpleJson;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style test (hits the real API).
 * Validates:
 *  - SERPAPI_KEY is present (otherwise test is skipped)
 *  - HTTP call to google_scholar_author succeeds
 *  - JSON contains author.name
 *  - JSON contains at least 1 article
 */
public class AuthorFlowIT {

    private static String apiKey;

    @BeforeAll
    static void setup() {
        apiKey = System.getenv("SERPAPI_KEY");
        // Skip the test if the key isn't available (keeps CI green without secrets)
        Assumptions.assumeTrue(apiKey != null && !apiKey.isBlank(),
                "SERPAPI_KEY is not set; skipping integration test.");
    }

    @Test
    @DisplayName("Fetch author and validate name + at least one article")
    void fetchAuthor_and_validate_basic_fields() throws Exception {
        String authorId = System.getProperty("authorId", "LSsXyncAAAAJ");

        ScholarAuthorClient client = new ScholarAuthorClient(apiKey);
        String json = client.fetchAuthorJson(authorId, 0, 5, "en", "pubdate", false);

        // API-level error reported in payload?
        String apiError = SimpleJson.extractTopLevelString(json, "error");
        assertNull(apiError, "API returned error: " + apiError);

        // Must have author name
        String authorName = SimpleJson.extractNestedString(json, "author", "name");
        assertNotNull(authorName, "author.name is missing");
        assertFalse(authorName.isBlank(), "author.name is blank");

        // Must have at least one article
        List<String> articleBlocks = SimpleJson.extractArrayObjects(json, "articles");
        assertNotNull(articleBlocks, "articles array missing");
        assertFalse(articleBlocks.isEmpty(), "no articles found");
    }
}
