import java.util.HashMap;
import java.util.Map;

// Information about a method
class MethodDec {

    // Method name
    String name;

    // Return type
    String returnType;

    // Class that contains the method
    ClassDec classInto;

    // Store arguments of the method
    Map<String, VariableDec> arguments;

    // Store variables used in the method
    Map<String, VariableDec> variables;

    // Offset in the stack
    int offset;

    MethodDec(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
        this.classInto = null; // Default value
        this.offset = -1; // Default value
        this.variables = new HashMap<>();
        this.arguments = new HashMap<>();
    }

    // Setters
    public void setClassInto(ClassDec classInto) {
        this.classInto = classInto;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setArgument(String name, String type) {
        // Check if the argument already exists
        if (arguments.containsKey(name)) {
            System.err.println("Error: Argument " + name + " already exists in method " + this.name);
            return;
        }

        // Add an argument to the method
        arguments.put(name, new VariableDec(name, type));
    }

    public void setVariable(String name, String type) {
        // Check if the variable already exists
        if (variables.containsKey(name)) {
            System.err.println("Error: Variable " + name + " already exists in method " + this.name);
            return;
        }

        // Add a variable to the method
        variables.put(name, new VariableDec(name, type));
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public ClassDec getClassInto() {
        return classInto;
    }

    public int getOffset() {
        return offset;
    }

    public Map<String, VariableDec> getArguments() {
        return arguments;
    }

    public Map<String, VariableDec> getVariables() {
        return variables;
    }
}
