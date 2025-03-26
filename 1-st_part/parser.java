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

            // First syntax error check
            if (input.length() == 0) {
                System.out.println("Syntax error: length");
                continue;
            }

            // Error if something differs than '+', '-', '**' , '(', ')', numbers or whitespaces
            if (!input.matches("[0-9\\+\\-\\*\\s\\(\\)]+")) {
                System.out.println("Syntax error: invalid characters");
                continue;
            }

            // Prevent "11 11" to "1111"
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

            // Identify the expression (number, operator or parenthesis)
            while (index < input.length()) {
                char current = input.charAt(index);
                if (Character.isDigit(current)) {
                    System.out.println("Number: " + parseNumber());
                } else {
                    System.out.println("Operator: " + current);
                    index++;
                }
            }

            System.out.println("Input: " + input);
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
