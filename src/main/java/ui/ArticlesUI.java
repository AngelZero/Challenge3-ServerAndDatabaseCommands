package ui;

import db.ArticleRepository;
import db.DbConfig;
import service.ScholarAuthorClient;
import util.SimpleJson;

import model.Article;
import model.ArticleRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ArticlesUI extends JFrame {

    private final JTextField authorIdInput = new JTextField(24);
    private final JButton btnFetchAndSave = new JButton("Fetch & Save (3)");
    private final JButton btnLoadByAuthor = new JButton("Load by author_id");
    private final JButton btnLoadAll = new JButton("Load ALL");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"id", "title", "authors", "publication_date", "cited_by", "link", "keywords"}, 0
    );
    private final JTable table = new JTable(tableModel);

    public ArticlesUI() {
        super("Scholar Articles — Sprint 4");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("author_id:"));
        top.add(authorIdInput);
        top.add(btnFetchAndSave);
        top.add(btnLoadByAuthor);
        top.add(btnLoadAll);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Wire actions
        btnFetchAndSave.addActionListener(e -> onFetchAndSave());
        btnLoadByAuthor.addActionListener(e -> onLoadByAuthor());
        btnLoadAll.addActionListener(e -> onLoadAll());

        setSize(1100, 520);
        setLocationRelativeTo(null);
    }

    private void onFetchAndSave() {
        String authorId = authorIdInput.getText().trim();
        if (authorId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Google Scholar author_id.", "Input required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apiKey = System.getenv("SERPAPI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            JOptionPane.showMessageDialog(this, "SERPAPI_KEY not set (use EnvFile or env var).", "Missing key", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1) Fetch JSON from SerpApi (google_scholar_author)
            ScholarAuthorClient client = new ScholarAuthorClient(apiKey);
            String json = client.fetchAuthorJson(authorId, 0, 10, "en", "pubdate", false);

            // 2) Check API error
            String apiError = SimpleJson.extractTopLevelString(json, "error");
            if (apiError != null && !apiError.isBlank()) {
                JOptionPane.showMessageDialog(this, "API error: " + apiError, "API Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3) Parse first 3 articles
            List<String> blocks = SimpleJson.extractArrayObjects(json, "articles");
            List<Article> top3 = new ArrayList<>();
            for (String block : blocks) {
                String title = SimpleJson.extractFieldString(block, "title");
                if (title == null || title.isBlank()) continue;
                String year = SimpleJson.extractFieldString(block, "year");
                Integer cited = SimpleJson.extractNestedInt(block, "cited_by", "value");
                String link = SimpleJson.extractFieldString(block, "link");
                String abstractText = SimpleJson.extractFieldString(block, "snippet");
                String authorsCsv = SimpleJson.extractFieldString(block, "authors");

                Article a = new Article(title, year, cited);
                a.setLink(link);
                a.setAbstractText(abstractText);
                a.setAuthors(authorsCsv);
                top3.add(a);
                if (top3.size() >= 3) break;
            }

            if (top3.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No articles parsed from API response.", "No Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4) Persist
            try (Connection cn = DbConfig.get()) {
                cn.setAutoCommit(false);
                ArticleRepository repo = new ArticleRepository();
                String tag = "author:" + authorId;
                for (Article a : top3) {
                    repo.save(cn, a, tag);
                }
                cn.commit();
            }

            // 5) Reload table for this author_id
            loadByAuthor(authorId);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLoadByAuthor() {
        String authorId = authorIdInput.getText().trim();
        if (authorId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an author_id to filter.", "Input required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        loadByAuthor(authorId);
    }

    private void loadByAuthor(String authorId) {
        try (Connection cn = DbConfig.get()) {
            ArticleRepository repo = new ArticleRepository();
            List<ArticleRecord> rows = repo.findByAuthorTag(cn, authorId);
            fillTable(rows);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLoadAll() {
        try (Connection cn = DbConfig.get()) {
            ArticleRepository repo = new ArticleRepository();
            List<ArticleRecord> rows = repo.findAll(cn);
            fillTable(rows);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillTable(List<ArticleRecord> rows) {
        tableModel.setRowCount(0);
        for (ArticleRecord r : rows) {
            tableModel.addRow(new Object[] {
                    r.getId(),
                    safe(r.getTitle()),
                    safe(r.getAuthors()),
                    safe(r.getPublicationDate()),
                    r.getCitedBy(),
                    safe(r.getLink()),
                    safe(r.getKeywords())
            });
        }
    }

    private static String safe(String s) { return (s == null) ? "" : s; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArticlesUI().setVisible(true));
    }
}
