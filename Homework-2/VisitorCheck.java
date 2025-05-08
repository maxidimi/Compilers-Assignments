import syntaxtree.*;
import visitor.*;

class VisitorCheck extends GJDepthFirst<String, Void>{
    // The symbol table
    SymbolTable symbolTable;

    VisitorCheck(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        n.f1.accept(this, null);

        super.visit(n, argu);

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        
        // Classname
        String classname = n.f1.accept(this, argu);

        n.f2.accept(this, argu);

        // Fields
        n.f3.accept(this, argu);

        // Methods
        n.f4.accept(this, argu);

        n.f5.accept(this, argu);

        // Add the class to the symbol table
        ClassDec classDec = new ClassDec(classname, null);
        symbolTable.classes.put(classname, classDec);

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        n.f0.accept(this, argu);

        // Classname
        String classname = n.f1.accept(this, null);

        n.f2.accept(this, argu);

        // Parent class
        String parent = n.f3.accept(this, argu);

        n.f4.accept(this, argu);

        // Fields
        n.f5.accept(this, argu);

        // Methods
        n.f6.accept(this, argu);

        n.f7.accept(this, argu);

        // Add the class to the symbol table
        ClassDec classDec = new ClassDec(classname, parent);
        symbolTable.classes.put(classname, classDec);

        return null;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Void argu) throws Exception {
        String _ret=null;

        // Type of the variable
        String type = n.f0.accept(this, argu);

        // Name of the variable
        String var = n.f1.accept(this, argu);
        
        super.visit(n, argu);

        // Add the variable to the current class/scope
        
        return _ret;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        // Arguments list
        String argumentList = n.f4.present() ? n.f4.accept(this, null) : "";

        // Type of the method
        String myType = n.f1.accept(this, null);

        // Name of the method
        String myName = n.f2.accept(this, null);

        // Local variables
        super.visit(n, argu);

        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
        // Arguments list
        String ret = n.f0.accept(this, null);

        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        // Arguments list
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        // Arguments list
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception{
        // Type of argument
        String type = n.f0.accept(this, null);

        // Name of argument
        String name = n.f1.accept(this, null);

        return type + " " + name;
    }

    @Override
    public String visit(BooleanArrayType n, Void argu) {
        return "boolean[]";
    }

    @Override
    public String visit(IntegerArrayType n, Void argu) {
        return "int[]";
    }

    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    public String visit(IntegerType n, Void argu) {
        return "int";
    }

    @Override
    public String visit(Identifier n, Void argu) {
        // Identifier name
        return n.f0.toString();
    }
}
