import java.util.HashMap;
import java.util.Map;

// The Symbol Table
class SymbolTable {
    
    // Store the classes in the symbol table - their variables and methods
    Map<String, ClassDec> classes;
    Map<String, MethodDec> methods;

    String currentClass;

    String currentMethod;

    SymbolTable() {
        classes = new HashMap<>();
        methods = new HashMap<>();
        currentClass = null;
        currentMethod = null;
    }
}
