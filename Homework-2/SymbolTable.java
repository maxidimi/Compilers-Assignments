import java.util.HashMap;
import java.util.Map;

// The Symbol Table
class SymbolTable {
    
    // Store the classes in the symbol table - their variables and methods
    Map<String, ClassDec> classes;

    SymbolTable() {
        classes = new HashMap<>();
    }

    // Getters
    public ClassDec getClass(String name) {
        return classes.get(name);
    }

    public boolean classExists(String name) {
        return classes.containsKey(name);
    }

    // Setters
    public void setClass(ClassDec classDec) {
        String name = classDec.getName();
        // Check if the class already exists
        if (classes.containsKey(name)) {
            new Exception("Class " + name + " already exists");
            return;
        }

        // Add the class to the symbol table
        classes.put(name, classDec);
    }
}
