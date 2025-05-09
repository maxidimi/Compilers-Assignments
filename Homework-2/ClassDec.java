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

    // Total offset
    int offset;

    ClassDec(String name, String parent) {
        this.name = name;
        this.parent = parent;
        this.offset = 0;
        // LinkedHashMap to keep insertion order
        this.fields = new LinkedHashMap<>();
        this.methods = new LinkedHashMap<>();
    }

    // Getters
    public String getName() {
        return name;
    }
    public String getParent() {
        return parent;
    }
    public int getOffset() {
        return offset;
    }
    public Map<String, VariableDec> getFields() {
        return fields;
    }
    public Map<String, MethodDec> getMethods() {
        return methods;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public void setField(String name, String type) {
        // Check if the field already exists
        if (fields.containsKey(name)) {
            System.err.println("Error: Field " + name + " already exists in class " + this.name);
            return;
        }
        
        // Add a field to the class
        fields.put(name, new VariableDec(name, type));

        // Update the offset
        if (type.equals("int")) {
            offset += 4;
        } else if (type.equals("boolean")) {
            offset += 1;
        } else { // Array
            offset += 8; 
        }
    }
    public void setMethod(String name, String type) {
        // Check if the method already exists
        if (methods.containsKey(name)) {
            System.err.println("Error: Method " + name + " already exists in class " + this.name);
            return;
        }
        // Add a method to the class
        methods.put(name, new MethodDec(name, type));

        // Update the offset
        offset += 8;
    }
}
