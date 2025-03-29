import java.io.*;

public class parser {
    private static String input;

    private static int index = 0;

    public static void main(String[] args) throws IOException {
        System.out.println("Enter an expression to evaluate (or 'q' to quit):");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while ((input = reader.readLine()) != null) {
            // Reset index on each new input/line
            index = 0;

            // Error if input is empty
            if (input.length() == 0) {
                System.out.println("Syntax error: Empty input");
                continue;
            }

            // If input is "q" then exit
            if (input.equals("q")) {
                System.out.println("Exit");
                break;
            }

            // Error if input differs than '+', '-', '**' , '(', ')', numbers or whitespaces
            if (!input.matches("[0-9\\+\\-\\*\\s\\(\\)]+")) {
                System.err.println("Syntax error: Invalid characters");
                continue;
            }

            // Prevent "11 11" to "1111" after removing spaces
            if (input.matches(".*\\d\\s+\\d.*")) {
                System.err.println("Parse error: Invalid input format");
                continue;
            }

            // Remove all spaces from input
            input = input.replaceAll("\\s", "");

            // If input is empty after removing spaces
            if (input.length() == 0) {
                System.err.println("Parse error: Empty input/only spaces provided");
                continue;
            }

            try { // Evaluate the expression
                int result = parseExp();
                if (index < input.length()) {
                    throw new RuntimeException("Parse error: Not all input consumed");
                }
                System.out.println("Result: " + result);
            } catch (RuntimeException e) {
                System.err.println("Parse error: " + e.getMessage());
            } finally {
                
            }
        }
    }

    private static int parseExp() {
        int result = parseTerm();
        return parseExp2(result);
    }

    private static int parseExp2(int term) {
        if (equal('+')) {
            int parseTerm = parseTerm();

            return parseExp2(term + parseTerm);
        } else if (equal('-')) {
            int parseTerm = parseTerm();

            return parseExp2(term - parseTerm);
        } else { // ε
            return term;
        }
    }

    private static int parseTerm() {
        int result = parseFactor();
        return parseTerm2(result);
    }

    private static int parseTerm2(int factor) {
        if (equal('*')) {
            if (!equal('*')) {
                throw new RuntimeException("Parse error: Expected '**', found '*'");
            } else { // **
                int parseFactor = parseFactor();

                // Calculate a**b**c as a**(b**c)
                return (int) Math.pow(factor, parseTerm2(parseFactor));
            }
        } else { // ε
            return factor;
        }
    }

    private static int parseFactor() {
        if (equal('(')) { //( exp )
            int result = parseExp();

            if (!equal(')')) {
                throw new RuntimeException("Parse error: Expected ')'");
            }

            return result;
        } else { // num
            return parseNumber();
        }
    }

    // Parse number from input
    private static int parseNumber() {
        if (!isDigit()) {
            throw new RuntimeException("Parse error: Expected number");
        }

        int number = 0;

        while (isDigit()) {
            number = number * 10 + (consume() - '0');
        }

        return number;
    }

    private static boolean isDigit() {
        return index < input.length() && Character.isDigit(current());
    }

    // Return the current character in input without consuming it
    private static char current() {
        if (index >= input.length()) {
            throw new RuntimeException("Parse error: Unexpected end of input");
        }

        return input.charAt(index);
    }

    // Consume the current character in input and move to the next one
    private static char consume() {
        if (index >= input.length()) {
            throw new RuntimeException("Parse error: Unexpected end of input");
        }

        return input.charAt(index++);
    }

    // Check if c is equal to next char in input & not out of bounds
    private static boolean equal(char c) {
        if (index < input.length() && input.charAt(index) == c) {
            index++;
            return true;
        } else {
            return false;
        }
    }
}
