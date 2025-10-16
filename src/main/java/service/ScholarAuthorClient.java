package service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/** Performs GET requests to SerpApi Google Scholar Author API. */
public class ScholarAuthorClient {
    private final HttpClient http = HttpClient.newHttpClient();
    private final String apiKey;
    private final String baseUrl = "https://serpapi.com/search";

    public ScholarAuthorClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String fetchAuthorJson(String authorId, Integer start, Integer num, String hl, String sort, boolean noCache) throws Exception {
        StringBuilder sb = new StringBuilder(baseUrl)
                .append("?engine=google_scholar_author")
                .append("&author_id=").append(enc(authorId))
                .append("&api_key=").append(enc(apiKey));
        if (start != null) sb.append("&start=").append(start);
        if (num != null) sb.append("&num=").append(num);
        if (hl != null) sb.append("&hl=").append(enc(hl));
        if (sort != null) sb.append("&sort=").append(enc(sort)); // e.g., "pubdate", "title"
        if (noCache) sb.append("&no_cache=true");

        HttpRequest req = HttpRequest.newBuilder(URI.create(sb.toString())).GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + ": " + res.body());
        }
        return res.body(); // JSON
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
