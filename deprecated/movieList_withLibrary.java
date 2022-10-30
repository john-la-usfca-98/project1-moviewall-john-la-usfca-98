import java.util.*;
import java.io.*;

import com.opencsv.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class App {

    //tmdb_5000_credits.csv have 4 columns: movie_id, title, cast, crew
    //but we only need title and cast columns
    //so we create a class to store the data
    public static class Movie {
        private String title;
        private String cast;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCast() {
            return cast;
        }

        public void setCast(String cast) {
            this.cast = cast;
        }
    }

    //this method will read the csv file and return a list of Movie objects
    public static List<Movie> readCSV(String filename) throws Exception {
        List<Movie> movies = new ArrayList<Movie>();

        CSVReader reader = new CSVReader(new FileReader(filename));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            Movie movie = new Movie();
            movie.setTitle(nextLine[1]);
            movie.setCast(nextLine[2]);
            movies.add(movie);
        }

        return movies;
    }

    //the cast column is a json string
    //so we need to parse the json string and get the cast name
    public static String getCastName(String cast) throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray castArray = (JSONArray) parser.parse(cast);

        String castName = "";
        for (int i = 0; i < castArray.size(); i++) {
            JSONObject castObject = (JSONObject) castArray.get(i);
            castName += castObject.get("name") + ", ";
        }

        return castName;
    }


    //print the movie titles from the user input cast name
    public static void printMovieTitles(String castName, List<Movie> movies) {
        for (Movie movie : movies) {
            if (movie.getCast().contains(castName)) {
                System.out.println(movie.getTitle());
            }
        }
    }

    //predict the cast name if the user input is not correct
    public static String predictCastName(String castName, List<Movie> movies) {
        String[] castNameArray = castName.split(" ");
        String predictedCastName = "";

        for (String name : castNameArray) {
            for (Movie movie : movies) {
                if (movie.getCast().contains(name)) {
                    predictedCastName += name + " ";
                    break;
                }
            }
        }

        return predictedCastName;
    }

    public static void main(String[] args) throws Exception {
        List<Movie> movies = readCSV("data/tmdb_5000_credits.csv");

        System.out.println("Welcome to the Movie Wall!");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of an actor (or EXIT to quit): ");
        String castName = scanner.nextLine();

        if (castName.isEmpty() || castName.equals("EXIT")) {
            System.out.println("Thank you for using the Movie Wall!");
        } else {
            if (castName.contains(" ")) {
                castName = predictCastName(castName, movies);
            }

            if (castName.isEmpty()) {
                System.out.println("No movie found");
            } else {
                printMovieTitles(castName, movies);
            }
        }

        scanner.close();
    }
}
