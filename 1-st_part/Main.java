import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, ParseError {
        System.out.println("Enter an expression to evaluate:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String input = reader.readLine();
        try {
            int result = Evaluator.evaluate(input);
            System.out.println("Result: " + result);
        } catch (ParseError e) {
            System.err.println(e.getMessage());
        }
    }
}
