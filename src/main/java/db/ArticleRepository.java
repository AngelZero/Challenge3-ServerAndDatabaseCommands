package db;

import model.Article;
import model.ArticleRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleRepository {

    private static final String INSERT_SQL =
            "INSERT INTO articles (title, authors, publication_date, abstract, link, keywords, cited_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public void save(Connection cn, Article a, String researcherTag) throws Exception {
        try (PreparedStatement ps = cn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, nz(a.getTitle()));
            ps.setString(2, nz(a.getAuthors()));
            ps.setString(3, nz(a.getYear()));
            ps.setString(4, nz(a.getAbstractText()));
            ps.setString(5, nz(a.getLink()));
            String keywords = (a.getKeywords() == null || a.getKeywords().isBlank())
                    ? researcherTag : a.getKeywords() + "," + researcherTag;
            ps.setString(6, keywords);
            if (a.getCitedBy() == null) ps.setNull(7, Types.INTEGER); else ps.setInt(7, a.getCitedBy());
            ps.executeUpdate();
        }
    }

    public List<ArticleRecord> findAll(Connection cn) throws Exception {
        String sql = "SELECT id, title, authors, publication_date, abstract, link, keywords, cited_by FROM articles ORDER BY id DESC";
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapList(rs);
        }
    }

    public List<ArticleRecord> findByAuthorTag(Connection cn, String authorId) throws Exception {
        String sql =
                "SELECT id, title, authors, publication_date, abstract, link, keywords, cited_by " +
                        "FROM articles " +
                        "WHERE FIND_IN_SET(?, REPLACE(keywords, ' ', '')) " +
                        "ORDER BY id DESC";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "author:" + authorId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        }
    }

    private static List<ArticleRecord> mapList(ResultSet rs) throws Exception {
        List<ArticleRecord> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new ArticleRecord(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("authors"),
                    rs.getString("publication_date"),
                    rs.getString("abstract"),
                    rs.getString("link"),
                    rs.getString("keywords"),
                    (Integer) rs.getObject("cited_by")
            ));
        }
        return list;
    }

    private static String nz(String s) { return (s == null) ? "" : s; }
}
