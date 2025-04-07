import java_cup.runtime.*;
import java.io.*;

class Main {
    public static void main(String[] argv) throws Exception{
        // Redirect IR parser output to Translated.ir
        PrintStream out1 = new PrintStream(new FileOutputStream("Translated.ir"));
        System.setOut(out1);

        // Parse first through Parser1 for IR
        Parser1 p1 = new Parser1(new Scanner(new InputStreamReader(System.in)));
        p1.parse();
        out1.close();

        // Redirect output to Translated.java
        PrintStream out2 = new PrintStream(new FileOutputStream("Translated.java"));
        System.setOut(out2);

        // Read the generated IR file and parse it to java code
        Parser p = new Parser(new Scanner(new FileReader("Translated.ir")));
        p.parse();
        
        out2.close();
    }
}
