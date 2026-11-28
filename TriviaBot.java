
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;

public class TriviaBot {

    public static void searchLifeSeries(String searchTerm)
            throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        String search = searchTerm.replace(" ", "%20");

        String url = "https://the-life-series.fandom.com/api.php?action=query&list=search&srsearch="
                + search + "&format=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {

            JSONObject data = new JSONObject(response.body());

            JSONObject query = data.getJSONObject("query");
            JSONArray results = query.getJSONArray("search");

            System.out.println();
            System.out.println("Here is what I found:");

            if (results.length() == 0) {
                System.out.println("No results found. Try another topic.");
            } else {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject page = results.getJSONObject(i);

                    String title = page.getString("title");

                    System.out.println((i + 1) + ". " + title);
                }
            }

        } else {
            System.out.println("The bot couldn't connect to the wiki.");
        }
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {

        Scanner in = new Scanner(System.in);

        System.out.println("Welcome to the Life Series Trivia Bot!");
        System.out.print("What topic should I search for? ");

        String topic = in.nextLine();

        searchLifeSeries(topic);

        in.close();
    }
}
