package db;

import model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ArticleRepository {

    private static final String INSERT_SQL =
            "INSERT INTO articles (title, authors, publication_date, abstract, link, keywords, cited_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public void save(Connection cn, Article a, String researcherTag) throws Exception {
        try (PreparedStatement ps = cn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, nullToEmpty(a.getTitle()));
            ps.setString(2, nullToEmpty(a.getAuthors()));
            ps.setString(3, nullToEmpty(a.getYear()));
            ps.setString(4, nullToEmpty(a.getAbstractText()));
            ps.setString(5, nullToEmpty(a.getLink()));
            // keywords: include a researcher tag to distinguish the 2 researchers
            String keywords = (a.getKeywords() == null || a.getKeywords().isBlank())
                    ? researcherTag
                    : a.getKeywords() + "," + researcherTag;
            ps.setString(6, keywords);
            ps.setObject(7, a.getCitedBy());                       // may be null
            ps.executeUpdate();
        }
    }

    private static String nullToEmpty(String s) { return (s == null) ? "" : s; }
}
