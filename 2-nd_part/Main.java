import java_cup.runtime.*;
import java.io.*;

class Main {
    public static void main(String[] argv) throws Exception{
        // Redirect stdout and stderr to Translated.java
        PrintStream out = new PrintStream(new FileOutputStream("Translated.java"));
        System.setOut(out);
        System.setErr(out);

        Parser p = new Parser(new Scanner(new InputStreamReader(System.in)));
        p.parse();
        
        out.close();
    }
}
