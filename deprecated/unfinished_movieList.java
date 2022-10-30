import java.util.*;
import java.io.*;

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
    public static List<String> readTitle(String fileName) {
        List<String> titles = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                //if the title has double quotes, we find the title by seperating everything between 2 commas
                int endIndex = line.indexOf(",\"[");

                //if the title doesn't have double quotes, we find the title by seperating everything between 1 comma
                if (endIndex == -1){
                    endIndex = line.indexOf(",[");
                }

                String title = line.substring(line.indexOf(",") + 1, endIndex);
                //removing double quotes
                title = title.replaceAll("\"", "");
                
                titles.add(title);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return titles;
    }

    //print the titles
    public static void printTitles(List<String> titles) {
        for (String title : titles) {
            System.out.println(title);
        }
    }

    //read only the cast column from the csv file
    //the cast column is a string of json objects
    //find the character name and actor name by using regex
    //store them in an actor list of Movie objects
    public static List<Movie> readCast(String fileName) {
        List<Movie> actors = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                //find the cast column by seperating everything after the 3rd comma
                String cast = line.substring(line.indexOf(",", line.indexOf(",") + 1) + 1);
                //remove the last 2 characters
                cast = cast.substring(0, cast.length() - 2);
                //split the cast column into an array of json objects
                String[] castArray = cast.split("\\},\\{");
                //create a Movie object for each json object
                for (String castMember : castArray) {
                    Movie actor = new Movie();
                    //find the character name by finding the string between "character" and "credit_id"
                    String character = castMember.substring(castMember.indexOf("\"character\":\"") + 1, castMember.indexOf("\",\"credit_id\""));
                    System.out.println(character);
                    //find the actor name by finding the string between "name" and "order"
                    String name = castMember.substring(castMember.indexOf("\"name\":\"") + 1, castMember.indexOf("\",\"order\""));
                    System.out.println(name);
                    //set the character name and actor name to the Movie object
                    actor.setCast(character + " - " + name);
                    actors.add(actor);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actors;
    }

    //create a list of Movie objects
    //combine the title list and cast list into the Movie list
    //make sure the title and cast are in the same order as the csv file
    public static List<Movie> combineLists(List<String> titles, List<Movie> actors) {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            Movie movie = new Movie();
            movie.setTitle(titles.get(i));
            movie.setCast(actors.get(i).getCast());
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

    //predict the correct cast name from the wrong cast input
    //by finding the distance between the wrong cast input and the correct cast name
    //the cast name with the smallest distance is the correct cast name
    //the distance is calculated by the Levenshtein distance algorithm
    //the algorithm is explained in the link below
    //https://en.wikipedia.org/wiki/Levenshtein_distance
    public static void predictCast(List<Movie> movies, String cast) {
        List<String> casts = new ArrayList<>();
        for (Movie movie : movies) {
            casts.add(movie.getCast());
        }
        int min = Integer.MAX_VALUE;
        String correctCast = "";
        for (String c : casts) {
            int distance = editDistance(cast, c);
            if (distance < min) {
                min = distance;
                correctCast = c;
            }
        }
        System.out.println("Did you mean " + correctCast + "?");
    }

    //calculate the edit distance between two strings
    public static int editDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)), Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    //calculate the cost of substitution
    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {

        List<Movie> readCast = readCast("data/tmdb_5000_credits.csv");
        /*
        List<String> films = readTitle("data/tmdb_5000_credits.csv");
        List<String> casts = readCast("data/tmdb_5000_credits.csv");
        List<Movie> movies = createMovieList(films, casts);
        while(true) {
            System.out.println("Enter a cast name: ");
            Scanner scanner = new Scanner(System.in);
            String cast = scanner.nextLine();
            if (cast.equals("exit") || cast.equals("quit") || cast.equals("q") || cast.equals("e") || cast.length() == 0) {
                System.out.println("Thank you for using the program!");
                scanner.close();
                break;
            }
            if (casts.contains(cast)) {
                printMovies(movies, cast);
                //return to the beginning of the loop
                continue;
            } else {
                System.out.println("No cast found. Did you mean: ");
                predictCast(movies, cast);
                if (scanner.nextLine().equals("yes")) {
                    printMovies(movies, cast);
                    //return to the beginning of the loop
                    continue;
                } else {
                    //return to the beginning of the loop
                    continue;
                }
            }
        }

    }
    */
            
    }
}

