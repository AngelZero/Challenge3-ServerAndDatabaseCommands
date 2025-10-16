package model;

import java.util.List;

public class Author {
    private final String name;
    private final String affiliations;
    private final List<Article> articles;

    public Author(String name, String affiliations, List<Article> articles) {
        this.name = name;
        this.affiliations = affiliations;
        this.articles = articles;
    }

    public String getName() { return name; }
    public String getAffiliations() { return affiliations; }
    public List<Article> getArticles() { return articles; }
}
