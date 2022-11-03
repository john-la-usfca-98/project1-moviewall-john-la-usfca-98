import java.util.*;
import java.io.*;

public class App {

    
    //read the csv file
    public static BufferedReader readCSV(String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return br;
    }

    //parse the csv file
    //create a 2d array to store the data
    public static String[][] parseCSV(BufferedReader br) {
        String[] data = new String[1000000];
        int i = 0;
        
        //read the csv file line by line
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                data[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //create a 2d array to store the data
        String finalArray[][] = new String[10000][10000];

        //split the data by comma
        for (int j = 1; j < data.length; j++) {
            String temp = data[j];
            if (temp == null) {
                break;
            }

            String[] Name = temp.split("name\"\": \"\"");
            String[] Movie = temp.split(",");
            String[] Role = temp.split(", \"\"character\"\": \"\"");
            
            //store the data in the 2d array
            //the row is the movie
            //the column is cast name and role
            for (int r = 1; r < 2; r++){
                if (Movie[r].contains("\"")){
                    finalArray[j-1][0] = Movie[r].substring(Movie[r].indexOf("\"") + 1);
                }
                else {
                    finalArray[j-1][0] = Movie[r];
                }
            
                for (int k = 0; k < Name.length && k < Role.length; k++) {
                    if(Name[k].contains("\"")){
                        finalArray[j-1][k] = Name[k].substring(0, Name[k].indexOf("\"\"")) 
                                             + " as " +
                                             Role[k].substring(0, Role[k].indexOf("\""));
                    }
                }
            }
        }

        //special case where a movie contains double quotes and commas
        finalArray[4800][0] = "Signed, Sealed, Delivered";

        return finalArray;
    }

    //print the 2d array
    public static void printArray(String[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] != null) {
                    System.out.print(array[i][j] + " ");
                }
            }
        }
    }

    //print movie list
    public static void printMovieList(String[][] array) {
        for (int r = 0; array[r][0] != null; r++) {
            System.out.print("Movie: ");
            for (int c = 0; array[r][c] != null; c++) {
                System.out.println(array[r][c]);
            }
        }
    }

    //print the movie name
    public static void printMovies(List<String> movies) {
        for (int i = 0; i < movies.size(); i++) {
            System.out.println(movies.get(i));
        }
    }

    //print the movie name from input cast name
    public static boolean searchForMovie(String[][] array, String name) {
        String actorName = "";
        String roleName = "";
        boolean found = false;
        List<String> movies = new ArrayList<String>();

        for (int r = 0; array[r][0] != null; r++) {
            for (int c = 0; array[r][c] != null; c++) {
                if (array[r][c].contains("as")) {
                    actorName = array[r][c].substring(0, array[r][c].indexOf(" as "));
                    roleName = array[r][c].substring(array[r][c].indexOf(" as ") - 1);
                    if (actorName.equals(name)) {
                        movies.add("Movie: " + array[r][0] + roleName);
                        found = true;
                    }
                }               
            }
        }

        if (found) {
            printMovies(movies);
        }

        return found;
    }

    //predict the cast name from wrong input cast name
    //use the Levenshtein distance algorithm
    public static String predictCast(String[][] array, String name) {
        int min = 1000000;
        String cast = "";
        for (int r = 0; array[r][0] != null; r++) {
            for (int c = 0; array[r][c] != null; c++) {
                if (array[r][c].contains("as")) {
                    String actorName = array[r][c].substring(0, array[r][c].indexOf(" as "));
                    int distance = levenshteinDistance(name, actorName);
                    if (distance < min) {
                        min = distance;
                        cast = actorName;
                    }
                }
            }
        }
        return cast;
    }

    //calculate the Levenshtein distance
    public static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int first = dp[i - 1][j] + 1;
                int second = dp[i][j - 1] + 1;
                int third = dp[i - 1][j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                    third = third + 1;
                }
                dp[i][j] = Math.min(first, Math.min(second, third));
            }
        }
        return dp[len1][len2];
    }

    public static void main (String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Movie Wall!");
        BufferedReader br = readCSV("data/tmdb_5000_credits.csv");
        String[][] movieWall = parseCSV(br);

        while(true){
            System.out.println("\nPlease enter a cast name (or enter exit to quit):");
            String name = sc.nextLine();
            if (name.equals("exit") || name.equals("quit")) {
                System.out.println("Thank you for using the Movie Wall!");
                sc.close();
                break;
            }
            
            //print raw data
            else if (name.equals("print raw data")) {
                printArray(movieWall);
            }

            //print movie list
            else if (name.equals("print")) {
                printMovieList(movieWall);
            }

            else {
                boolean found = searchForMovie(movieWall, name);
                if (!found){
                    System.out.println("\nSorry, we don't have " + name + " in our database.");
                    System.out.println("\nDid you mean " + predictCast(movieWall, name) + "?");
                    if (sc.nextLine().equals("yes") || sc.nextLine().equals("y")){
                        System.out.println("\nThe movies that " + predictCast(movieWall, name) + " has been in are:");
                        searchForMovie(movieWall, predictCast(movieWall, name));
                        continue;
                    }
                    else {
                        continue;
                    }
                }
            }
        }
    }
}

