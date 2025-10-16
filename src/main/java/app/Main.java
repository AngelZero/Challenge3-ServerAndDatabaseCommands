package app;

import controller.AuthorController;
import service.ScholarAuthorClient;
import view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("SERPAPI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Set SERPAPI_KEY environment variable.");
            System.exit(1);
        }

        // Pass an author_id as a program argument, or use a test id.
        String authorId = (args.length > 0) ? args[0] : "_xwYD2sAAAAJ"; // example id

        ScholarAuthorClient client = new ScholarAuthorClient(apiKey);
        ConsoleView view = new ConsoleView();
        AuthorController controller = new AuthorController(client, view);

        controller.showAuthorById(authorId);
    }
}
