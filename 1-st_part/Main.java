import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter an expression to evaluate (or 'q' to quit):");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String input;
        while ((input = reader.readLine()) != null) {
            // If input is "q" then exit
            if (input.equals("q")) {
                System.out.println("Exit");
                break;
            }

            try {
                int result = Evaluator.evaluate(input);
                System.out.println("Result: " + result);
            } catch (ParseError e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
