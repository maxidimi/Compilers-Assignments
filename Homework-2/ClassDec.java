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
    int varOffset;
    int methodOffset;

    ClassDec(String name, ClassDec parent) {
        this.name = name;
        this.parent = parent != null ? parent.name : null;
        this.varOffset = parent != null ? parent.varOffset : 0;
        this.methodOffset = parent != null ? parent.methodOffset : 0;
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

    public int getVarOffset() {
        return varOffset;
    }

    public int getMethodOffset() {
        return methodOffset;
    }

    public VariableDec getField(String name) {
        return fields.get(name);
    }

    public MethodDec getMethod(String name) {
        return methods.get(name);
    }

    public Map<String, VariableDec> getFields() {
        return fields;
    }

    public Map<String, MethodDec> getMethods() {
        return methods;
    }

    // Setters
    public void setVarOffset(int varOffset) {
        this.varOffset = varOffset;
    }

    public void setMethodOffset(int methodOffset) {
        this.methodOffset = methodOffset;
    }

    public void setField(String name, String type) throws Exception {
        // Check if the field already exists manually
        if (fields.containsKey(name)) {
            throw new Exception("Field " + name + " already exists in class " + this.name);
        }
        
        // Add a field to the class
        VariableDec field = new VariableDec(name, type);
        fields.put(name, field);

        // Update the offset
        field.setOffset(varOffset);
        if (type.equals("int")) {
            varOffset += 4;
        } else if (type.equals("boolean")) {
            varOffset += 1;
        } else { // Array or object
            varOffset += 8; 
        }
    }
    
    public void setMethod(MethodDec method) throws Exception {
        String methodName = method.getName();

        // Check if the method already exists
        if (methods.containsKey(methodName)) {
            throw new Exception("Method " + methodName + " already exists in class " + this.name);
        }
        // Add a method to the class
        methods.put(methodName, method);
        method.classInto = this; // Set the class that contains the method

        // Update the offset
        method.setOffset(methodOffset);
        methodOffset += 8;
    }
}
