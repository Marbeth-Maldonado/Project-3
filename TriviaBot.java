
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;

public class TriviaBot {

    public static void main(String[] args) throws Exception {

        Scanner in = new Scanner(System.in);

        System.out.println("Welcome to Trivia Bot!");
        System.out.println("Let's see how much you know!");

        // ask the player how many questions they want
        System.out.print("How many questions do you want? ");
        int amount = Integer.parseInt(in.nextLine());

        // ask the player to choose a difficulty
        System.out.println("Choose a difficulty: easy, medium, or hard");
        String difficulty = in.nextLine().toLowerCase();

        // get the questions from the API
        List<Question> questions = getTriviaQuestions(amount, difficulty);

        if (questions.isEmpty()) {
            System.out.println("Trivia Bot couldn't find any questions!");
        } else {

            int score = 0;

            // go through each question and add points for correct answers
            for (Question question : questions) {
                score += askQuestion(in, question);
            }

            // show the player's final score
            System.out.println("\nGame over!");
            System.out.println("Your final score is: " + score + " out of " + (questions.size() * 2));
        }

        in.close();
    }

    // this method gets the questions from the Open Trivia Database
    public static List<Question> getTriviaQuestions(int amount, String difficulty) throws Exception {

        String url = "https://opentdb.com/api.php?amount=" + amount
                + "&category=15&difficulty=" + URLEncoder.encode(difficulty, StandardCharsets.UTF_8)
                + "&type=multiple";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        // make sure the request worked before reading the response
        if (response.statusCode() != 200) {
            System.out.println("Something went wrong getting the questions.");
            return new ArrayList<>();
        }

        // turn the JSON response into Java objects
        Gson gson = new Gson();
        TriviaResponse data = gson.fromJson(response.body(), TriviaResponse.class);

        if (data.response_code != 0) {
            System.out.println("Trivia Bot couldn't find questions for that selection.");
            return new ArrayList<>();
        }

        return data.results;
    }

    // this method asks a question and checks the player's answer
    public static int askQuestion(Scanner in, Question question) {

        System.out.println("\n" + decodeText(question.question));

        // put the correct and incorrect answers together
        List<String> answers = new ArrayList<>(question.incorrect_answers);
        answers.add(question.correct_answer);

        // mix up the answers so the correct one isn't always in the same place
        Collections.shuffle(answers);

        boolean usedHint = false;

        // show the answer choices
        showAnswers(answers);

        System.out.println("Enter the number of your answer, or H for a hint:");
        String choice = in.nextLine();

        // give the player a hint by removing one wrong answer
        if (choice.equalsIgnoreCase("H")) {
            for (int i = 0; i < answers.size(); i++) {
                if (!answers.get(i).equals(question.correct_answer)) {
                    answers.remove(i);
                    break;
                }
            }

            usedHint = true;
            System.out.println("Trivia Bot: I've removed one incorrect answer. Good luck!");
            showAnswers(answers);

            System.out.print("Your answer: ");
            choice = in.nextLine();
        }

        int answerNumber = Integer.parseInt(choice);

        // check if the answer is correct
        if (answerNumber >= 1 && answerNumber <= answers.size()
                && answers.get(answerNumber - 1).equals(question.correct_answer)) {

            System.out.println("Trivia Bot: Correct! Well done!");

            if (usedHint) {
                return 1;
            } else {
                return 2;
            }

        } else {
            System.out.println("Trivia Bot: Incorrect!");
            System.out.println("The correct answer was: " + decodeText(question.correct_answer));
            return 0;
        }
    }

    // this method displays the answer choices
    public static void showAnswers(List<String> answers) {

        for (int i = 0; i < answers.size(); i++) {
            System.out.println((i + 1) + ". " + decodeText(answers.get(i)));
        }
    }

    // this method fixes some special characters from the API response
    public static String decodeText(String text) {

        return text.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    // this class holds the information from the API response
    public static class TriviaResponse {
        int response_code;
        List<Question> results;
    }

    // this class holds each trivia question and its answers
    public static class Question {
        String category;
        String type;
        String difficulty;
        String question;
        String correct_answer;
        List<String> incorrect_answers;
    }
}
