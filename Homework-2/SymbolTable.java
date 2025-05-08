import java.util.HashMap;

// The Symbol Table
class SymbolTable {

    // Store the current scope with its variables and methods
    Scope currentScope;

    // Store the classes in the symbol table - their variables and methods
    Map<String, ClassDec> classes;

    SymbolTable() {
        classes = new HashMap<>();
    }
}