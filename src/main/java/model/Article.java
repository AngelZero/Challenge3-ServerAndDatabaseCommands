package model;

public class Article {
    private final String title;
    private final String year;
    private final Integer citedBy;

    private String authors;        // comma-separated (optional)
    private String abstractText;   // optional
    private String link;           // optional
    private String keywords;       // optional

    public Article(String title, String year, Integer citedBy) {
        this.title = title;
        this.year = year;
        this.citedBy = citedBy;
    }

    public String getTitle() { return title; }
    public String getYear() { return year; }
    public Integer getCitedBy() { return citedBy; }

    public String getAuthors() { return authors; }
    public String getAbstractText() { return abstractText; }
    public String getLink() { return link; }
    public String getKeywords() { return keywords; }

    public void setAuthors(String authors) { this.authors = authors; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    public void setLink(String link) { this.link = link; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
}
