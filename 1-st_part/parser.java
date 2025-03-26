import java.io.*;

public class parser {
    private static String input;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while ((input = reader.readLine()) != null) {
            System.out.println(input);
        }
    }
}
