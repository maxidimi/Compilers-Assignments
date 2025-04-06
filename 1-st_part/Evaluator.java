public class Evaluator {
    private static String input;

    private static int index;

    public static int evaluate(String expression) throws ParseError {
        input = expression;
        index = 0;

        int result = parseExp();

        if (index < input.length()) {
            throw new ParseError("Parse error: Not all input consumed");
        }

        return result;
    }

    private static int parseExp() throws ParseError {
        if (current() != '(' && !isDigit()) { // error
            throw new ParseError("Parse error: Expected '(', or number");
        }
        int result = parseTerm();
        return parseExp2(result);
    }

    private static int parseExp2(int term) throws ParseError {
        if (equal('+')) {
            int parseTerm = parseTerm();

            return parseExp2(term + parseTerm);
        } else if (equal('-')) {
            int parseTerm = parseTerm();

            return parseExp2(term - parseTerm);
        } else if (isEnd() || equal(')')) {
            return term;
        } else { // error
            throw new ParseError("Parse error: Expected '+', '-', ')', or '$'");
        }
    }

    private static int parseTerm() throws ParseError {
        if (current() != '(' && !isDigit()) { // error
            throw new ParseError("Parse error: Expected '(', or number");
        }
        int result = parseFactor();
        return parseTerm2(result);
    }

    private static int parseTerm2(int factor) throws ParseError {
        if (equal('*')) {
            if (!equal('*')) {
                throw new ParseError("Parse error: Expected '**', found '*'");
            } else { // **
                int parseFactor = parseFactor();

                // Calculate a**b**c as a**(b**c)
                return (int) Math.pow(factor, parseTerm2(parseFactor));
            }
        } else if (isEnd() || current() == '+' || current() == '-' || current() == ')') {
            return factor;
        } else { // error
            throw new ParseError("Parse error: Expected '+', '-', ')', or '$'");
        }
    }

    private static int parseFactor() throws ParseError {
        if (equal('(')) { // ( exp )
            int result = parseExp();
            return result;
        } else if (isDigit()) { // num
            return parseNum();
        } else { // error
            throw new ParseError("Parse error: Expected '(', or number");
        }
    }

    private static int parseNum() throws ParseError {
        if (!isDigit()) { // error
            throw new ParseError("Parse error: Expected number");
        }

        int number = 0;
        while (isDigit()) number = number * 10 + (consume() - '0');

        skipWhitespace();
        if ((index < input.length()) && isDigit()) { // error (not approve "1 1" as "11")
            throw new ParseError("Parse error: Not approved format");
        }

        return number;
    }

    // Check if the current character is a digit
    private static boolean isDigit() throws ParseError {
        return Character.isDigit(isEnd() ? '\0' : input.charAt(index));
    }

    // Return the current character in input without consuming it
    private static char current() throws ParseError {
        skipWhitespace();
        if (isEnd()) {
            throw new ParseError("Parse error: Unexpected end of input");
        }

        return input.charAt(index);
    }

    // Consume the current character in input and move to the next one
    private static char consume() throws ParseError {
        skipWhitespace();
        if (isEnd()) {
            throw new ParseError("Parse error: Unexpected end of input");
        }

        return input.charAt(index++);
    }

    // Check if c is equal to next char in input & not out of bounds
    private static boolean equal(char c) {
        if (!isEnd() && input.charAt(index) == c) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    // Skip whitespace characters in input
    private static void skipWhitespace() throws ParseError {
        while (!isEnd() && Character.isWhitespace(input.charAt(index))) {
            index++;
        }
    }

    // Check if the input has been fully consumed
    private static boolean isEnd() {
        return index >= input.length();
    }
}
