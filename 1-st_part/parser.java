import java.io.*;

public class parser {
    private static String input;

    private static int index = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while ((input = reader.readLine()) != null) {
            System.out.println("\n");
            // Reset index on each new input/line
            index = 0;
            
            // If input is "q" then exit
            if (input.equals("q")) {
                System.out.println("Exit");
                break;
            }

            // Error if input is empty
            if (input.length() == 0) {
                System.out.println("Syntax error: length");
                continue;
            }

            // Error if input differs than '+', '-', '**' , '(', ')', numbers or whitespaces
            if (!input.matches("[0-9\\+\\-\\*\\s\\(\\)]+")) {
                System.out.println("Syntax error: invalid characters");
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
                System.out.println("Syntax error: empty");
                continue;
            }

            try { // Evaluate the expression
                int result = parseExp();
                if (index < input.length()) {
                    throw new RuntimeException("Parse error");
                }
                System.out.println("Result: " + result);
            } catch (RuntimeException e) {
                System.err.println("Parse error: " + e.getMessage());
            } finally {
                
            }
        }
    }

    private static void outOfBounds() {
        if (index >= input.length()) {
            throw new RuntimeException("Out of bounds");
        }
    }

    private static int parseExp() {
        int result = parseTerm();
        return parseExp2(result);
    }
    private static int parseExp2(int term) {
        if (input.charAt(index) == '+') {
            index++;

            int parseTerm = parseTerm();

            return term + parseExp2(parseTerm);
        } else if (input.charAt(index) == '-') {
            index++;

            int parseTerm = parseTerm();

            return term - parseExp2(parseTerm);
        } else {
            return term;
        }
    }

    private static int parseTerm() {
        int result = parseFactor();
        return parseTerm2(result);
    }

    private static int parseTerm2(int factor) {
        if (input.charAt(index) == '*' && input.charAt(index + 1) == '*') {
            index += 2;

            int parseFactor = parseFactor();

            return (int) Math.pow(factor, parseFactor);
        } else {
            return factor;
        }
    }

    private static int parseFactor() {
        if (input.charAt(index) == '(') {
            index++;

            int result = parseExp();

            if (input.charAt(index) != ')') {
                throw new RuntimeException("Expected ')'");
            }

            index++;

            return result;
        } else {
            return parseNumber();
        }
    }

    // Parse number from input
    private static int parseNumber() {
        int number = 0;
        
        while (index < input.length() && Character.isDigit(input.charAt(index))) {
            number = number * 10 + (input.charAt(index) - '0');
            index++;
        }

        return number;
    }

}
