package model;

public class Article {
    private final String title;
    private final String year;
    private final Integer citedBy;

    public Article(String title, String year, Integer citedBy) {
        this.title = title;
        this.year = year;
        this.citedBy = citedBy;
    }

    public String getTitle() { return title; }
    public String getYear() { return year; }
    public Integer getCitedBy() { return citedBy; }
}
