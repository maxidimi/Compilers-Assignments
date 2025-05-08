import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import syntaxtree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }

        for (int i = 0; i < args.length; i++) {
            FileInputStream fis = null;
            try{
                fis = new FileInputStream(args[0]); // Read the input file
                MiniJavaParser parser = new MiniJavaParser(fis); // Parse the input file

                Goal root = parser.Goal();

                System.err.println("Program parsed successfully.");

                VisitorST eval = new VisitorST(); // Create a visitor to build Symbol Table
                root.accept(eval, null);

                SymbolTable symbolTable = eval.symbolTable; // Get the Symbol Table

                // For each class in the symbol table print its name
                System.out.println("Classes in the symbol table:");
                symbolTable.classes.forEach((k, v) -> {
                    System.out.println("\tClass: " + k);
                    if(v.parent != null) System.out.println("\t\tParent: " + v.parent);
                });

                //? Call another visitor to do the type checking
                //VisitorCheck check = new VisitorCheck(symbolTable);
            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            finally{
                try{
                    if(fis != null) fis.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}
