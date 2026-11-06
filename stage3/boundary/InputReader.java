package boundary;

import java.util.Scanner;

/**
 * Boundary component responsible strictly for console input reading and token validation (SRP).
 * Encapsulates Scanner lifecycle, EOF detection, and non-numeric input recovery.
 */
public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null");
        }
        this.scanner = scanner;
    }

    public InputReader() {
        this(new Scanner(System.in));
    }

    /**
     * Reads the next integer from the console, re-prompting on non-numeric tokens.
     *
     * @return The parsed integer, or null if the input stream has reached EOF.
     */
    public Integer readInt() {
        while (true) {
            if (!scanner.hasNext()) {
                return null;
            }
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }
            String badToken = scanner.next();
            System.out.print("[INVALID] '" + badToken + "' is not a number. Please enter a valid number: ");
        }
    }

    /**
     * Prints a prompt and reads the next integer from the console.
     *
     * @param prompt Prompt text to display before reading.
     * @return The parsed integer, or null if the input stream has reached EOF.
     */
    public Integer promptInt(String prompt) {
        System.out.print(prompt);
        return readInt();
    }

    public void close() {
        scanner.close();
    }
}
