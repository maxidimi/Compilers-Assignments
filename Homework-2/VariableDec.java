// Information about a variable
class VariableDec {

    // Variable name
    String name;

    // Variable type
    String type;

    // Offset in the stack
    int offset;

    VariableDec(String name, String type) {
        this.name = name; // Checks to ensure that name is not a primitive type or "this" are made in parsing
        this.type = type;
        this.offset = 0;
    }

    // Getters
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    // Setters
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
