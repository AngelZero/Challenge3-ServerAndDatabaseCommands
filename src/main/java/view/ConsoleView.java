package view;

import model.Author;
import model.Article;

public class ConsoleView {

    public void renderAuthor(Author author) {
        if (author == null) {
            System.out.println("No author data.");
            return;
        }
        System.out.println("=== Google Scholar Author ===");
        System.out.println("Name: " + author.getName());
        System.out.println("Affiliations: " + author.getAffiliations());
        System.out.println();
        System.out.println("Articles:");
        if (author.getArticles() == null || author.getArticles().isEmpty()) {
            System.out.println("  (no articles found)");
            return;
        }
        for (Article a : author.getArticles()) {
            String cited = (a.getCitedBy() == null) ? "0" : a.getCitedBy().toString();
            System.out.printf("  - %s (%s) | cited_by=%s%n",
                    nullToDash(a.getTitle()),
                    nullToDash(a.getYear()),
                    cited);
        }
    }

    public void renderError(String message) {
        System.err.println("[ERROR] " + message);
    }

    private static String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
