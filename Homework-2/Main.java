import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import syntaxtree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            System.err.println("Usage: java Main <inputFiles>");
            System.exit(1);
        }

        for (int i = 0; i < args.length; i++) {
            FileInputStream fis = null;
            try{
                fis = new FileInputStream(args[i]); // Read the input file
                MiniJavaParser parser = new MiniJavaParser(fis); // Parse the input file

                Goal root = parser.Goal();

                VisitorST eval = new VisitorST(); // Create a visitor to build Symbol Table
                root.accept(eval, null);

                SymbolTable symbolTable = eval.symbolTable; // Get the Symbol Table

                // Call another visitor to do the type checking
                VisitorCheck check = new VisitorCheck(symbolTable);
                root.accept(check, null);

                // For each class in the symbol table print its name
                symbolTable.classes.forEach((k, v) -> {
                    if (k == symbolTable.mainClassName) {
                        return; // Skip the main class
                    }

                    System.out.println("-----------Class " + k + "-----------");

                    System.out.println("--Variables---");
                    for (String field : v.fields.keySet()) {
                        System.out.println(k + "." + field + " : " + v.fields.get(field).offset);
                    }

                    System.out.println("---Methods---");
                    for (String method : v.methods.keySet()) {
                        // Print only the methods that are not overriding others previously defined
                        if (!v.methods.get(method).isOverriding) {
                            System.out.println(k + "." + method + " : " + v.methods.get(method).offset);
                        }
                    }
                    System.out.println();
                });
            }
            catch(NullPointerException ex){
                System.err.println("Null pointer exception: " + ex.getMessage() + " for file " + args[i]); ex.printStackTrace();
            }
            catch(FileNotFoundException ex){
                System.err.println("File not found: " + ex.getMessage() + " for file " + args[i]); ex.printStackTrace();
            }
            catch(IOException ex){
                System.err.println("IO error: " + ex.getMessage() + " for file " + args[i]); ex.printStackTrace();
            }
            catch(ParseException ex){
                System.err.println("Parse error: " + ex.getMessage() + " for file " + args[i]); ex.printStackTrace();
            }
            catch(Exception ex){
                System.err.println("Error: " + ex.getMessage() + " for file " + args[i]); ex.printStackTrace();
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
