public class Evaluator {
    private static String input;

    private static int index;

    public static int evaluate(String expression) {
        input = expression;
        index = 0;

        int result = parseExp();

        if (index < input.length()) {
            throw new ParseError("Parse error: Not all input consumed");
        }

        return result;
    }

    private static int parseExp() {
        if(current() != '(' && !isDigit()) { // error
            throw new RuntimeException("Parse error: Expected '(', or number");
        }
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
        } else if (current() == ')' || current() == '\0') {
            return term;
        } else { // error
            throw new RuntimeException("Parse error: Expected '+', '-', ')' or end of expression");
        }
    }

    private static int parseTerm() {
        if(current() != '(' && !isDigit()) { // error
            throw new RuntimeException("Parse error: Expected '(', or number");
        }
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
        } else if (current() == '+' || current() == '-' || current() == ')' || current() == '\0') {
            return factor;
        } else {
            throw new RuntimeException("Parse error: Expected '+', '-', ')', or end of expression");
        }
    }

    private static int parseFactor() {
        if (equal('(')) { // ( exp )
            int result = parseExp();

            if (!equal(')')) { // error
                throw new RuntimeException("Parse error: Expected ')'");
            }

            return result;
        } else if (isDigit()) { // number
            return parseNumber();
        } else { // error
            throw new RuntimeException("Parse error: Expected '(', or number");
        }
    }

    private static int parseNumber() {
        if (!isDigit()) { // error
            throw new RuntimeException("Parse error: Expected number");
        }

        int number = 0;

        while (isDigit()) {
            number = number * 10 + (consume() - '0');
        }

        return number;
    }

    private static boolean isDigit() {
        consumeWhitespace();
        return index < input.length() && Character.isDigit(current());
    }

    // Return the current character in input without consuming it
    private static char current() {
        consumeWhitespace();
        if (index >= input.length()) {
            throw new RuntimeException("Parse error: Unexpected end of input");
        }

        return input.charAt(index);
    }

    // Consume the current character in input and move to the next one
    private static char consume() {
        consumeWhitespace();
        if (index >= input.length()) {
            throw new RuntimeException("Parse error: Unexpected end of input");
        }

        return input.charAt(index++);
    }

    // Check if c is equal to next char in input & not out of bounds
    private static boolean equal(char c) {
        consumeWhitespace();
        if (index < input.length() && input.charAt(index) == c) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    // Consume whitespaces
    private static void consumeWhitespace() {
        while (index < input.length() && Character.isWhitespace(input.charAt(index))) {
            index++;
        }
    }
}
