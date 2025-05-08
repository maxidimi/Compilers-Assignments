import java.util.HashMap;
import java.util.Map;

// The Symbol Table
class SymbolTable {
    
    // Store the classes in the symbol table - their variables and methods
    Map<String, ClassDec> classes;

    SymbolTable() {
        classes = new HashMap<>();
    }
}
