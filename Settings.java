import java.io.*;

public class Settings {

    //creates a text file called settings
    private static final String FILE_NAME = "settings.txt";

    //method used to print the last city into the text file
    public static void saveLastCity(String city) {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            writer.println(city);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method used to load last city from text file
    public static String loadLastCity() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            return reader.readLine();
        } catch (
                IOException e) {
            return "";
        }
    }
}

