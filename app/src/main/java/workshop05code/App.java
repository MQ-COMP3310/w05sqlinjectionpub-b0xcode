package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // logger.log(Level.INFO,"The 'Hello, World' program runs");
    // End code for logging exercise

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            // System.out.println("Wordle created and connected.");
            logger.log(Level.INFO, "Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            logger.log(Level.SEVERE, "Not able to connect.");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            // System.out.println("Wordle structures in place.");
            logger.log(Level.INFO, "Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            logger.log(Level.SEVERE, "Not able to launch.");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            boolean valid;
            int i = 1;
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                valid = Pattern.matches("^[a-z]{4}$", line);
                if (valid) {
                    logger.log(Level.INFO, line);
                    wordleDatabaseConnection.addValidWord(i, line);
                    i++;
                } else {
                    logger.log(Level.SEVERE, "Invalid word " + line + " discovered in data.txt");
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Not able to load.", e);
            System.out.println("Not able to load . Sorry!");
            // System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            boolean valid = false;
            String guess = "";
            while (!valid) {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();
                if (guess.equals("q"))
                    break;
                valid = Pattern.matches("^[a-z]{4}$", guess);
                if (!valid) {
                    logger.log(Level.WARNING, "Received invalid input: " + guess);
                    System.out.println("Invalid input! 4 letter word, lowercase only please.");
                }
            }
            while (!guess.equals("q")) {
                valid = false;
                System.out.println("You've guessed '" + guess + "'.");

                if (wordleDatabaseConnection.isValidWord(guess)) {
                    System.out.println("Success! It is in the the list.\n");
                } else {
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }

                while (!valid) {
                    System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                    guess = scanner.nextLine();
                    if (guess.equals("q"))
                        break;
                    valid = Pattern.matches("^[a-z]{4}$", guess);
                    if (!valid) {
                        logger.log(Level.WARNING, "Received invalid input: " + guess);
                        System.out.println("Invalid input! 4 letter word, lowercase only please.");
                    }
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Error: ", e);
            // e.printStackTrace();
        }

    }
}