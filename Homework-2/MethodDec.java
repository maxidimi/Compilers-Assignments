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

    // Store their name and type
    Map<String, VariableDec> arguments;

    MethodDec(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
        arguments = new HashMap<>();
    }

    public void addArgument(String name, String type) {
        arguments.put(name, new VariableDec(name, type));
    }
}
