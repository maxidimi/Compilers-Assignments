import java.io.*;

public class parser {
    private static String input;

    private static int index = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while ((input = reader.readLine()) != null) {
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

            try {
                // Evaluate the expression
                int result = expr(input);
                System.out.println("Result: " + result);
            } catch (RuntimeException e) {
                System.err.println("Parse error: " + e.getMessage());
            } finally {
                
            }
        }
    }

    private static int expr(String input) {
        int result = 0;
        while (index < input.length()) {
            char current = input.charAt(index);
            if (Character.isDigit(current)) {
                result = parseNumber();
            } else if (current == '+') {
                index++;
                result += parseNumber();
            } else if (current == '-') {
                index++;
                result -= parseNumber();
            } else if (current == '*') {
                index++;
                result *= parseNumber();
            } else if (current == '(') {
                index++;
                result = expr(input);
            } else if (current == ')') {
                index++;
                return result;
            } else {
                throw new RuntimeException("Invalid operator: " + current);
            }
        }

        return result;
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
