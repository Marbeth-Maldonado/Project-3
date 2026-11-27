
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;

public class TriviaBot {

    public static void getPokemonGeneration(int generation)
            throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        String url = "https://pokeapi.co/api/v2/generation/" + generation;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {

            JSONObject data = new JSONObject(response.body());

            String name = data.getString("name");
            JSONArray pokemon = data.getJSONArray("pokemon_species");

            System.out.println("Generation: " + name);
            System.out.println("Number of Pokemon: " + pokemon.length());

            System.out.println("Some Pokemon from this generation:");

            for (int i = 0; i < 5 && i < pokemon.length(); i++) {
                JSONObject pokemonData = pokemon.getJSONObject(i);
                System.out.println(pokemonData.getString("name"));
            }

        } else {
            System.out.println("Could not retrieve Pokemon information.");
        }
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {

        Scanner in = new Scanner(System.in);

        System.out.println("Welcome to Pokemon Trivia Bot!");
        System.out.print("Choose a generation (1-9): ");

        int generation = in.nextInt();

        getPokemonGeneration(generation);

        in.close();
    }
}