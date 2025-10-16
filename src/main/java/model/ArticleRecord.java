package model;

public class ArticleRecord {
    private Long id;
    private String title;
    private String authors;
    private String publicationDate;
    private String abstractText;
    private String link;
    private String keywords;
    private Integer citedBy;

    public ArticleRecord(Long id, String title, String authors, String publicationDate,
                         String abstractText, String link, String keywords, Integer citedBy) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publicationDate = publicationDate;
        this.abstractText = abstractText;
        this.link = link;
        this.keywords = keywords;
        this.citedBy = citedBy;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public String getPublicationDate() { return publicationDate; }
    public String getAbstractText() { return abstractText; }
    public String getLink() { return link; }
    public String getKeywords() { return keywords; }
    public Integer getCitedBy() { return citedBy; }
}
