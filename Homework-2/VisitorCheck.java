import syntaxtree.*;
import visitor.*;

class VisitorCheck extends GJDepthFirst<String, Void>{
    
    // The symbol table
    SymbolTable symbolTable;

    Boolean inVarDecleration;

    String currentClass;
    String currentMethod;

    ClassDec currentClassDec;
    MethodDec currentMethodDec;

    VisitorCheck(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.inVarDecleration = false;
        this.currentClass = null;
        this.currentMethod = null;
        this.currentClassDec = null;
        this.currentMethodDec = null;
    }

    public boolean isValidType(String type) {
        // Check if the type is valid
        if (type.equals("int") || type.equals("boolean") || type.equals("int[]") || type.equals("boolean[]")) {
            return true;
        } else if (symbolTable.classExists(type)) {
            return true;
        } else {
            return false;
        }
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

        // Statements
        n.f15.accept(this, null);

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
        
        // Classname
        String classname = n.f1.accept(this, argu);
        currentClass = classname;
        currentClassDec = symbolTable.getClass(classname);
        if (currentClassDec == null) {
            throw new Exception("Class " + classname + " not found in symbol table");
        }
        
        // Fields
        n.f3.accept(this, argu);

        // Methods
        n.f4.accept(this, argu);

        // Reset current class
        currentClass = null;
        currentClassDec = null;

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
        // Classname
        String classname = n.f1.accept(this, null);
        currentClass = classname;
        currentClassDec = symbolTable.getClass(classname);
        if (currentClassDec == null) {
            throw new Exception("Class " + classname + " not found in symbol table");
        }

        // Parent class
        String parent = n.f3.accept(this, argu);

        // Fields
        n.f5.accept(this, argu);

        // Methods
        n.f6.accept(this, argu);

        // Reset current class
        currentClass = null;
        currentClassDec = null;

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

        // Check if the type is valid
        if (!isValidType(type)) {
            throw new Exception("Invalid type " + type + " for variable " + var);
        }
        
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
        // Type of the method
        String myType = n.f1.accept(this, null);

        // Name of the method
        String myName = n.f2.accept(this, null);
        currentMethod = myName;
        
        // Arguments list
        String argumentList = n.f4.accept(this, argu);

        // Variables
        inVarDecleration = true;
        n.f7.accept(this, argu);
        inVarDecleration = false;

        // Statements
        n.f8.accept(this, argu);

        // Return expression
        n.f10.accept(this, argu);

        // Reset current method
        currentMethod = null;
        
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
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
        //? f0
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

        // Check if the type is valid
        if (!isValidType(type)) {
            throw new Exception("Invalid type " + type + " for argument " + name);
        }

        return type + " " + name;
    }

    /**
     * f0 -> "{" 
     * f1 -> ( Statement() )* 
     * f2 -> "}"
     */
    @Override
    public String visit(Block n, Void argu) throws Exception {
        // Statements
        n.f1.accept(this, argu);

        return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public String visit(PrintStatement n, Void argu) throws Exception {
        // Expression in print
        n.f2.accept(this, argu);

        return null;
    }

    /**
     * f0 -> AndExpression() | CompareExpression() | PlusExpression() | MinusExpression() | 
     *       TimesExpression() | ArrayLookup() | ArrayLength() | MessageSend() | Clause()
     */
    @Override
    public String visit(Expression n, Void argu) throws Exception {
        // Visit the expression and return the type
        String type = n.f0.accept(this, argu);

        return type;
    }

    /**
     * f0 -> Clause() 
     * f1 -> "&&" 
     * f2 -> Clause()
     */
    @Override
    public String visit(AndExpression n, Void argu) throws Exception {
        // Left expression
        n.f0.accept(this, argu);

        // Right expression
        n.f2.accept(this, argu);

        return "boolean";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "<" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        // Left expression
        String left = n.f0.accept(this, argu);

        // Right expression
        String right = n.f2.accept(this, argu);

        // Check if the types are valid
        if (!left.equals("int") || !right.equals("int")) {
            throw new Exception("Invalid types for comparison: " + left + " and " + right);
        }

        return "boolean";
    }
    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "+" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {
        // Left expression
        String left = n.f0.accept(this, argu);

        // Right expression
        String right = n.f2.accept(this, argu);

        // Check if the types are valid
        if (!left.equals("int") || !right.equals("int")) {
            throw new Exception("Invalid types for addition: " + left + " and " + right);
        }

        return "int";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "-" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        // Left expression
        String left = n.f0.accept(this, argu);

        // Right expression
        String right = n.f2.accept(this, argu);

        // Check if the types are valid
        if (!left.equals("int") || !right.equals("int")) {
            throw new Exception("Invalid types for subtraction: " + left + " and " + right);
        }

        return "int";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "*" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
        // Left expression
        String left = n.f0.accept(this, argu);

        // Right expression
        String right = n.f2.accept(this, argu);

        // Check if the types are valid
        if (!left.equals("int") || !right.equals("int")) {
            throw new Exception("Invalid types for multiplication: " + left + " and " + right);
        }

        return "int";
    }

    @Override
    public String visit(IntegerLiteral n, Void argu) throws Exception {
        return "int";
    }

    @Override
    public String visit(TrueLiteral n, Void argu) throws Exception {
        return "boolean";
    }

    @Override
    public String visit(FalseLiteral n, Void argu) throws Exception {
        return "boolean";
    }

    @Override
    public String visit(BooleanArrayType n, Void argu) {
        return "boolean[]";
    }

    @Override
    public String visit(IntegerArrayType n, Void argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, Void argu) {
        return "int";
    }

    @Override
    public String visit(Identifier n, Void argu) {
        // Identifier name
        return n.f0.toString();
    }
}
