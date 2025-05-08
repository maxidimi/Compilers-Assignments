import java.util.HashMap;
import java.util.Map;

// Information about a class
class ClassDec {
    
    // Class name
    String name;

    // Parent class name (from extends), if no parent class, this is null
    String parent;

    // Fields of the class - name and type
    Map<String, String> fields;

    // Methods of the class - pointer to their MethodDec
    Map<String, MethodDec> methods;

    ClassDec(String name, String parent) {
        this.name = name;
        this.parent = parent;
        fields = new HashMap<>();
        methods = new HashMap<>();
    }

    public void addField(String name, String type) {
        fields.put(name, type);
    }

    public void addMethod(String name, String type) {
        methods.put(name, new MethodDec(name, type));
    }
}
