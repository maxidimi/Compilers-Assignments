// Information about a variable
class VariableDec {

    // Variable name
    String name;

    // Variable type
    String type;

    // Offset in the stack
    int offset;

    VariableDec(String name, String type) {
        this.name = name;
        this.type = type;
        this.offset = -1; // Default value
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
