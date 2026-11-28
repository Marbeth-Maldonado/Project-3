
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import org.json.JSONObject;
import org.json.JSONArray;

public class TriviaBot {

    // Searches the Life Series Wiki
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
            System.out.println("Wiki search results:");

            if (results.length() == 0) {
                System.out.println("No results found.");
            } else {
                for (int i = 0; i < 3 && i < results.length(); i++) {
                    JSONObject page = results.getJSONObject(i);
                    System.out.println(page.getString("title"));
                }
            }

        } else {
            System.out.println("Could not connect to the Life Series Wiki.");
        }
    }

    // Runs the Trivia Bot
    public static void playTrivia(Scanner in) {

        String[] questions = {
            "Who created the Life Series?",
            "What is the name of the first season of the Life Series?",
            "Who won the first season of the Life Series?",
            "Which player is known for the Canary Curse?",
            "How many lives did players start with in 3rd Life?",
            "What color are players when they have only 1 life left?",
            "Which season introduced the Boogeyman?",
            "How many lives could players receive randomly in Last Life?",
            "What special connection did players have in Double Life?",
            "Who won Double Life?",
            "How much time did players start with in Limited Life?",
            "Who won Limited Life?",
            "What did players have to complete in Secret Life?",
            "Who won Secret Life?",
            "What special mechanic changed the gameplay in Wild Life?",
            "Who won Wild Life?",
            "Who won Past Life?",
            "What special mechanic was introduced in Past Life?",
            "How many lives did players start with in Final Life?",
            "What is the name of the place players visit after they die in Final Life?"
        };

        String[] answers = {
            "Grian",
            "3rd Life",
            "Grian",
            "Jimmy",
            "3",
            "Red",
            "Last Life",
            "2 to 6",
            "Soulmates",
            "Pearl",
            "24 hours",
            "Martyn",
            "Secret tasks",
            "Scar",
            "Wildcards",
            "Joel",
            "Martyn",
            "Secret Society",
            "6",
            "In-Between"
        };

        // 1 = easy, 2 = medium, 3 = hard
        int[] difficulties = {
            1, 1, 2, 1, 1,
            2, 1, 2, 2, 1,
            1, 2, 1, 2, 1,
            2, 2, 3, 2, 2
        };

        boolean[] asked = new boolean[questions.length];

        int score = 0;
        int difficulty = 1;
        int wrongCount = 0;

        Random random = new Random();

        System.out.println();
        System.out.println("Welcome to the Life Series Trivia Bot!");
        System.out.println("You will be asked 20 questions about the Life Series.");
        System.out.println("Let's see how much you actually know!");
        System.out.println();

        for (int round = 0; round < 20; round++) {

            // Selects a random question that hasn't been asked yet
            int[] availableQuestions = new int[questions.length];
            int possibleCount = 0;

            // Finds questions that match the current difficulty
            for (int i = 0; i < questions.length; i++) {
                if (asked[i] == false && difficulties[i] == difficulty) {
                    availableQuestions[possibleCount] = i;
                    possibleCount++;
                }
            }

            // If there are no questions at this difficulty,
            // choose from any questions that haven't been asked
            if (possibleCount == 0) {
                for (int i = 0; i < questions.length; i++) {
                    if (asked[i] == false) {
                        availableQuestions[possibleCount] = i;
                        possibleCount++;
                    }
                }
            }

            // Picks a random question
            int randomIndex = random.nextInt(possibleCount);
            int questionIndex = availableQuestions[randomIndex];

            asked[questionIndex] = true;

            System.out.println("Question " + (round + 1) + ":");
            System.out.println(questions[questionIndex]);

            System.out.print("Your answer: ");
            String answer = in.nextLine();

            // Checks if the answer is correct
            if (answer.equalsIgnoreCase(answers[questionIndex])) {

                System.out.println("Correct! Don't get too confident.");
                score++;

                // Makes the next question harder
                if (difficulty < 3) {
                    difficulty++;
                }

            } else {

                wrongCount++;

                System.out.println("Incorrect! The correct answer is: "
                        + answers[questionIndex]);

                // Makes the next question easier
                if (difficulty > 1) {
                    difficulty--;
                }

                // The bot gets sassier with each wrong answer
                if (wrongCount == 1) {
                    System.out.println("Not quite. Maybe pay attention?");
                } else if (wrongCount == 2) {
                    System.out.println("Again? This is becoming concerning.");
                } else if (wrongCount == 3) {
                    System.out.println("Are you guessing at this point?");
                } else if (wrongCount == 4) {
                    System.out.println("I think the wiki knows more than you.");
                } else {
                    System.out.println("At this point, even Jimmy is judging you.");
                }
            }

            System.out.println("Score: " + score + "/" + (round + 1));
            System.out.println();
        }

        // Displays the final score
        System.out.println("Trivia is over!");
        System.out.println("Your final score is " + score + "/20.");

        if (score == 20) {
            System.out.println("Perfect score! You really know your Life Series!");
        } else if (score >= 15) {
            System.out.println("Not bad! You know your stuff.");
        } else if (score >= 10) {
            System.out.println("You have some studying to do.");
        } else {
            System.out.println("Maybe you should rewatch the Life Series.");
        }
    }

    // Main method to run the Trivia Bot
    public static void main(String[] args)
            throws IOException, InterruptedException {

        Scanner in = new Scanner(System.in);

        System.out.println("What Life Series topic would you like to search for?");
        String topic = in.nextLine();

        searchLifeSeries(topic);

        playTrivia(in);

        in.close();
    }
}
