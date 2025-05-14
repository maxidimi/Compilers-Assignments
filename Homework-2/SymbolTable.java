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
    public ClassDec getClass(String name) throws Exception {
        //? Check if the class exists
        if (!classes.containsKey(name)) {
            throw new Exception("Class " + name + " does not exist");
        }
        return classes.get(name);
    }

    public boolean classExists(String name) {
        return classes.containsKey(name);
    }

    // Setters
    public void setClass(ClassDec classDec) throws Exception {
        String name = classDec.getName();
        // Check if the class already exists
        if (classes.containsKey(name)) {
            throw new Exception("Class " + name + " already exists");
        }

        // Add the class to the symbol table
        classes.put(name, classDec);
    }
}
