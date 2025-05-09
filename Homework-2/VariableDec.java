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

    public String getOffset() {
        return String.valueOf(offset);
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
