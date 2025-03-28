import java.io.*;

public class parser {
    private static String input;

    private static int index = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while ((input = reader.readLine()) != null) {
            // Reset index on each new input/line
            index = 0;

            // Error if input is empty
            if (input.length() == 0) {
                System.out.println("Syntax error: empty input");
                continue;
            }

            // If input is "q" then exit
            if (input.equals("q")) {
                System.out.println("Exit");
                break;
            }

            // Error if input differs than '+', '-', '**' , '(', ')', numbers or whitespaces
            if (!input.matches("[0-9\\+\\-\\*\\s\\(\\)]+")) {
                System.err.println("Syntax error: invalid characters");
                continue;
            }

            // Prevent "11 11" to "1111" after removing spaces
            if (input.matches(".*\\d\\s+\\d.*")) {
                System.err.println("Parse error: invalid input format");
                continue;
            }

            // Remove all spaces from input
            input = input.replaceAll("\\s", "");

            // If input is empty after removing spaces
            if (input.length() == 0) {
                System.err.println("Syntax error: empty input/only spaces provided");
                continue;
            }

            try { // Evaluate the expression
                int result = parseExp();
                if (index < input.length()) {
                    throw new RuntimeException("Parse error: not all input consumed");
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
        if (equal('*') && equal('*')) {
            int parseFactor = parseFactor();

            return parseTerm2((int) Math.pow(factor, parseFactor));
        } else { // ε
            return factor;
        }
    }

    private static int parseFactor() {
        if (equal('(')) {
            int result = parseExp();

            if (!equal(')')) {
                throw new RuntimeException("Parse error: Expected ')'");
            }

            return result;
        } else {
            return parseNumber();
        }
    }

    // Parse number from input
    private static int parseNumber() {
        if (!isDigit()) {
            throw new RuntimeException("Expected number");
        }

        int number = 0;

        while (isDigit()) {
            number = number * 10 + (input.charAt(index) - '0');
            index++;
        }

        return number;
    }

    private static boolean isDigit() {
        return index < input.length() && Character.isDigit(input.charAt(index));
    }

    private static char consume() {
        if (index >= input.length()) {
            throw new RuntimeException("Unexpected end of input");
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

    //? Check for * which are not followed by *
    //? Precedence in a**b**c
}
