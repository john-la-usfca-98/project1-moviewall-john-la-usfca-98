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
            }
            for (int k = 0; k < Name.length && k < Role.length; k++) {
                if(Name[k].contains("\"")){
                    finalArray[j-1][k] = Name[k].substring(0, Name[k].indexOf("\"\"")) 
                                        + " as " +
                                        Role[k].substring(0, Role[k].indexOf("\""));
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
            System.out.println();
        }
    }

    //print the movie name from input cast name
    public static void printMovie(String[][] array, String name) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] != null && array[i][j].contains(name)) {
                    System.out.println(array[i][0]);
                }
            }
        }
    }

    //predict the cast name from wrong input cast name
    //use the Levenshtein distance algorithm
    public static String predictCast(String[][] array, String name) {
        int min = 1000000;
        String cast = "";
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] != null) {
                    int distance = levenshteinDistance(name, array[i][j]);
                    if (distance < min) {
                        min = distance;
                        cast = array[i][j];
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
            else if (name.equals("print")) {
                printArray(movieWall);
            }
            else {
                boolean flag = false;
                for (int i = 0; i < movieWall.length; i++) {
                    for (int j = 0; j < movieWall[i].length; j++) {
                        if (movieWall[i][j] != null && movieWall[i][j].contains(name)) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    System.out.println("\nThe movies that " + name + " has been in are:");
                    printMovie(movieWall, name);
                    continue;
                }
                else {
                    System.out.println("\nSorry, we don't have " + name + " in our database.");
                    System.out.println("\nDid you mean " + predictCast(movieWall, name) + "?");
                    if(sc.nextLine().equals("yes") || sc.nextLine().equals("y")){
                        System.out.println("\nThe movies that " + predictCast(movieWall, name) + " has been in are:");
                        printMovie(movieWall, predictCast(movieWall, name));
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
