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

    public void addArgument(String name, String type) {
        arguments.put(name, new VariableDec(name, type));
    }
}
