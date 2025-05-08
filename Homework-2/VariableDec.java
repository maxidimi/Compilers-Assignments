// Information about a variable
class VariableDec {

    // Variable name
    String name;

    // Variable type
    String type;

    // Class that contains the variable
    ClassDec classInto;

    VariableDec(String name, String type) {
        this.classInto = null;
        this.name = name;
        this.type = type;
    }
}
