package controller;

import model.Author;
import model.Article;
import service.ScholarAuthorClient;
import view.ConsoleView;
import util.SimpleJson;

import java.util.ArrayList;
import java.util.List;

public class AuthorController {

    private final ScholarAuthorClient client;
    private final ConsoleView view;

    public AuthorController(ScholarAuthorClient client, ConsoleView view) {
        this.client = client;
        this.view = view;
    }

    public void showAuthorById(String authorId) {
        try {
            // Fetch JSON from SerpApi (Google Scholar Author engine)
            String json = client.fetchAuthorJson(authorId, 0, 10, "en", "pubdate", false);

            // Basic check for API-level errors (SerpApi returns an "error" field on failure)
            String apiError = SimpleJson.extractTopLevelString(json, "error");
            if (apiError != null && !apiError.isBlank()) {
                view.renderError("API error: " + apiError);
                return;
            }

            // Map Author (name, affiliations)
            String authorName = SimpleJson.extractNestedString(json, "author", "name");
            String affiliations = SimpleJson.extractNestedString(json, "author", "affiliations");

            // Map Articles: title, year, cited_by.value
            List<Article> articles = new ArrayList<>();
            List<String> articleBlocks = SimpleJson.extractArrayObjects(json, "articles");
            for (String block : articleBlocks) {
                String title = SimpleJson.extractFieldString(block, "title");
                String year = SimpleJson.extractFieldString(block, "year");
                Integer cited = SimpleJson.extractNestedInt(block, "cited_by", "value");
                if (title != null) {
                    articles.add(new Article(title, year, cited));
                }
            }

            Author author = new Author(
                    authorName != null ? authorName : "(unknown)",
                    affiliations != null ? affiliations : "(unknown)",
                    articles
            );

            view.renderAuthor(author);

        } catch (Exception e) {
            view.renderError("Failed to load author: " + e.getMessage());
        }
    }
}
