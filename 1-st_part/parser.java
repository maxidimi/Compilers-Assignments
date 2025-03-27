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

    private static int parseTerm() {
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

    private static int parseExp2(int term) {
        char operator = input.charAt(index);
        if (operator != '+' && operator != '-') {
            return term;
        } else {
            index++;
        }

        if (operator == '+') {
            return term + parseTerm();
        } else {
            return term - parseTerm();
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
