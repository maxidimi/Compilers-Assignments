import java.util.HashMap;
import java.util.Map;

// The Symbol Table
class SymbolTable {
    
    // Store the classes in the symbol table - their variables and methods
    Map<String, ClassDec> classes;
    Map<String, MethodDec> methods;

    SymbolTable() {
        classes = new HashMap<>();
        methods = new HashMap<>();
    }

    public void setClass(String name, ClassDec classDec) {
        // Check if the class already exists
        if (classes.containsKey(name)) {
            System.err.println("Error: Class " + name + " already exists");
            return;
        }

        // Add a class to the symbol table
        classes.put(name, classDec);
    }

    public void setMethod(String name, MethodDec methodDec) {
        // Check if the method already exists
        if (methods.containsKey(name)) {
            System.err.println("Error: Method " + name + " already exists");
            return;
        }

        // Add a method to the symbol table
        methods.put(name, methodDec);
    }
    
    public ClassDec getClass(String name) {
        return classes.get(name);
    }

    public MethodDec getMethod(String name) {
        return methods.get(name);
    }

    public boolean classExists(String name) {
        return classes.containsKey(name);
    }

    public boolean methodExists(String name) {
        return methods.containsKey(name);
    }
}
