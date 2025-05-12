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
        this.classInto = null;
        this.offset = 0;
        this.variables = new HashMap<>();
        this.arguments = new HashMap<>();
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

    public VariableDec getVariableOrArgument(String name) {
        // Check if the variable is an argument
        if (arguments.containsKey(name)) {
            return arguments.get(name);
        }

        // Check if the variable is a local variable
        if (variables.containsKey(name)) {
            return variables.get(name);
        }

        // Variable not found
        return null;
    }

    // Setters
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setArgument(String name, String type) throws Exception {
        // Check if the argument already exists
        if (arguments.containsKey(name)) {
            throw new Exception("Argument " + name + " already exists in method " + this.name);
        }

        // Add an argument to the method
        arguments.put(name, new VariableDec(name, type));
    }

    public void setVariable(String name, String type) throws Exception {
        // Check if the variable already exists
        if (variables.containsKey(name)) {
            throw new Exception("Variable " + name + " already exists in method " + this.name);
        }

        // Check if the variable is an argument
        if (arguments.containsKey(name)) {
            throw new Exception("Variable " + name + " already exists as an argument in method " + this.name);
        }

        // Add a variable to the method
        variables.put(name, new VariableDec(name, type));
    }
}
