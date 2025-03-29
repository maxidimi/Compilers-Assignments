public class Evaluator {
    private static String input;

    private static int index = 0;

    public static int evaluate(String expression) {

        // Error if input is empty
        if (input.isEmpty()) {
            throw new ParseError("Parse error: Empty input");
        }

        // Error if input differs than '+', '-', '**' , '(', ')', numbers or whitespaces
        if (!input.matches("[0-9\\+\\-\\*\\(\\)]+")) {
            throw new ParseError("Syntax error: Invalid characters");
        }

        // Prevent "11 11" to "1111" after removing spaces
        if (input.matches(".*\\d\\s+\\d.*")) {
            throw new ParseError("Parse error: Invalid input format");
        }

        // Remove all spaces from input
        input = input.replaceAll("\\s", "");

        // If input is empty after removing spaces
        if (input.length() == 0) {
            throw new ParseError("Parse error: Empty input/only spaces provided");
        }

        int result = parseExp();

        if (index < input.length()) {
            throw new ParseError("Parse error: Not all input consumed");
        }

        return result;
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
        if (equal('(')) { // ( exp )
            int result = parseExp();

            if (!equal(')')) {
                throw new RuntimeException("Parse error: Expected ')'");
            }

            return result;
        } else { // num
            return parseNumber();
        }
    }

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
