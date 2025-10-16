package controller;

import db.ArticleRepository;
import db.DbConfig;
import model.Author;
import model.Article;
import service.ScholarAuthorClient;
import util.SimpleJson;
import view.ConsoleView;

import java.sql.Connection;
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
            // 1) Fetch JSON from SerpApi (Google Scholar Author engine)
            String json = client.fetchAuthorJson(authorId, 0, 10, "en", "pubdate", false);

            // 2) API-level error
            String apiError = SimpleJson.extractTopLevelString(json, "error");
            if (apiError != null && !apiError.isBlank()) {
                view.renderError("API error: " + apiError);
                return;
            }

            // 3) Map Author (name, affiliations)
            String authorName = SimpleJson.extractNestedString(json, "author", "name");
            String affiliations = SimpleJson.extractNestedString(json, "author", "affiliations");

            // 4) Map Articles: title, year, cited_by.value (+ optional fields)
            List<Article> articles = new ArrayList<>();
            List<String> articleBlocks = SimpleJson.extractArrayObjects(json, "articles");

            for (String block : articleBlocks) {
                String title = SimpleJson.extractFieldString(block, "title");
                String year = SimpleJson.extractFieldString(block, "year");
                Integer cited = SimpleJson.extractNestedInt(block, "cited_by", "value");

                // Optional extra fields
                String link = SimpleJson.extractFieldString(block, "link");            // often present
                String abstractText = SimpleJson.extractFieldString(block, "snippet"); // short summary/snippet
                String authorsCsv = SimpleJson.extractFieldString(block, "authors");   // provider-dependent

                if (title == null || title.isBlank()) {
                    continue; // skip incomplete rows
                }

                Article art = new Article(title, year, cited);
                art.setLink(link);
                art.setAbstractText(abstractText);
                art.setAuthors(authorsCsv);
                articles.add(art);
            }

            Author author = new Author(
                    (authorName != null && !authorName.isBlank()) ? authorName : "(unknown)",
                    (affiliations != null && !affiliations.isBlank()) ? affiliations : "(unknown)",
                    articles
            );

            // 5) Persist top 3 articles for this researcher (DB transaction)
            //    - matches Sprint 3 requirement: store 3 articles per researcher
            if (!articles.isEmpty()) {
                try (Connection cn = DbConfig.get()) {
                    cn.setAutoCommit(false);
                    ArticleRepository repo = new ArticleRepository();
                    String researcherTag = "author:" + authorId; // stored in 'keywords' column

                    int count = 0;
                    for (Article a : articles) {
                        repo.save(cn, a, researcherTag);
                        if (++count >= 3) break;
                    }
                    cn.commit();
                } catch (Exception dbEx) {
                    // Show concise DB error; still render author info to the console
                    view.renderError("DB error: " + dbEx.getMessage());
                }
            }

            // 6) Finally, render to console
            view.renderAuthor(author);

        } catch (Exception e) {
            view.renderError("Failed to load author: " + e.getMessage());
        }
    }
}
