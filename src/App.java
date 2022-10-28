import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

    //read only the title column from the csv file
    //some titles have commas and double quotes
    //so we need to use regex to get the title
    public static String getTitle(String line) {
        String title = "";
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            title = matcher.group(1);
        }
        return title;
    }
    
    //print the titles
    public static void printTitles(List<String> titles) {
        for (String title : titles) {
            System.out.println(title);
        }
    }

    //read only the cast column from the csv file
    //find the character token and the actor name
    //store only the actor name in the cast list
    public static List<String> readCast(String fileName) {
        List<String> casts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                for (int i = 0; i < tokens.length; i++) {
                    if (tokens[i].equals("\"character\"")) {
                        casts.add(tokens[i + 1]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return casts;
    }

    //create a movie object and store the title and cast list
    //then store the movie object in a movie list
    public static List<Movie> createMovieList(List<String> titles, List<String> casts) {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < casts.size(); i++) {
            Movie movie = new Movie();
            movie.setTitle(titles.get(i));
            movie.setCast(casts.get(i));
            movies.add(movie);
        }
        return movies;
    }

    //print the list of movies and cast
    public static void printMovies(List<Movie> movies) {
        for (Movie movie : movies) {
            System.out.println(movie.getTitle() + " " + movie.getCast());
        }
    }
    //print the title of the movies from the cast input
    public static void printMovies(List<Movie> movies, String cast) {
        for (Movie movie : movies) {
            if (movie.getCast().contains(cast)) {
                System.out.println(movie.getTitle());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<String> films = readTitle("data/tmdb_5000_credits.csv");
        printTitles(films);
        /*
        List<String> casts = readCast("data/tmdb_5000_credits.csv");
        List<Movie> movies = createMovieList(films, casts);
        printMovies(movies);
        */

    }
            
}

