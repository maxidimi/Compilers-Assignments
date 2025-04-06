public class ParseError extends Exception {
    public ParseError(String message) {
        super(message);
        Exception e = new Exception(message);
        e.printStackTrace();
    }
}
