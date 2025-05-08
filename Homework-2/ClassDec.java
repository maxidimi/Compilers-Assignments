import java.util.LinkedHashMap;
import java.util.Map;

// Information about a class
class ClassDec {
    
    // Class name
    String name;

    // Parent class name (from extends), if no parent class, this is null
    String parent;

    // Fields of the class - name and type
    Map<String, VariableDec> fields;

    // Methods of the class - pointer to their MethodDec
    Map<String, MethodDec> methods;

    // Offsets
    int offset;

    ClassDec(String name, String parent) {
        this.name = name;
        this.parent = parent;
        // LinkedHashMap to keep insertion order
        this.fields = new LinkedHashMap<>();
        this.methods = new LinkedHashMap<>();
        this.offset = -1; // Default value
    }

    public void addField(String name, String type) {
        //? Add a field to the class
    }

    public void addMethod(String name, String type) {
        //? Check if the method already exists
        //? Add a method to the class
    }
}
