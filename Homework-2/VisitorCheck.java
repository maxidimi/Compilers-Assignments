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

    boolean zeroArraylength;

    String varId;
    String varType;

    VisitorCheck(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.inVarDecleration = false;
        this.currentClass = null;
        this.currentMethod = null;
        this.currentClassDec = null;
        this.currentMethodDec = null;
        this.zeroArraylength = false;
    }

    public boolean isValidType(String type) {
        // Check if the type is valid
        if (type == null) {
            return false;
        }
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
    @Override
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
        currentMethodDec = symbolTable.getClass(currentClass).getMethod(myName);
        if (currentMethodDec == null) {
            throw new Exception("Method " + myName + " not found in symbol table, class " + currentClass);
        }
        
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
    @Override
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

    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        // Expression
        String type = n.f1.accept(this, argu);

        // Check if the type is valid
        if (type == null) {
            throw new Exception("Invalid null type for bracket expression");
        }

        return type;
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
        String expr = n.f2.accept(this, argu);

        // Check if the type is valid
        if ((expr == null) || !expr.equals("int")) {
            //?throw new Exception("Invalid type for print statement: " + expr);
        }

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
     * f0 -> IntegerLiteral() | TrueLiteral() | FalseLiteral() | Identifier() | 
     *       ThisExpression() | ArrayAllocationExpression() | AllocationExpression() | BracketExpression()
     */
    @Override
    public String visit(PrimaryExpression n, Void argu) throws Exception {
        // Visit the primary expression and return the type
        String type = n.f0.accept(this, argu);

        return type;
    }

    /**
     * f0 -> "new" 
     * f1 -> Identifier() 
     * f2 -> "(" 
     * f3 -> ")"
     */
    @Override
    public String visit(AllocationExpression n, Void argu) throws Exception {
        String type = n.f1.accept(this, argu);
        
        // Check if the type is valid
        if (!isValidType(type)) {
            throw new Exception("Invalid type " + type + " for allocation expression");
        }

        return type;
    }

    /**
     * f0 -> "this"
     */
    @Override
    public String visit(ThisExpression n, Void argu) throws Exception {
        // Check if the current class is valid
        if (currentClass == null) {
            throw new Exception("Invalid this expression: " + n.f0.toString());
        }

        return currentClass;
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "[" 
     * f2 -> PrimaryExpression() 
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n, Void argu) throws Exception {
        // Array name
        String array = n.f0.accept(this, argu);

        // Index type
        String index = n.f2.accept(this, argu);

        // Find the array type
        String arrayType = lookForId(array);

        // Check if the types are valid
        if ((arrayType == null) || (index == null) || !arrayType.equals("int[]") || !index.equals("int")) {
            throw new Exception("Invalid types for array lookup: " + arrayType + " and " + index);
        }

        return arrayType.substring(0, arrayType.length() - 2); // Remove the brackets
    }

    /**
     * f0 -> "!" 
     * f1 -> Clause()
     */
    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        // Clause
        String clause = n.f1.accept(this, argu);

        // Check if the type is boolean
        if ((clause == null) || !clause.equals("boolean")) {
            throw new Exception("Invalid type for not expression: " + clause);
        }

        return "boolean";
    }
    /**
     * f0 -> Clause() 
     * f1 -> "&&" 
     * f2 -> Clause()
     */
    @Override
    public String visit(AndExpression n, Void argu) throws Exception {
        // Left expression
        String left = n.f0.accept(this, argu);

        // Right expression
        String right = n.f2.accept(this, argu);

        // Check if the types are valid
        if ((left == null) || (right == null) || !left.equals("boolean") || !right.equals("boolean")) {
            throw new Exception("Invalid types for and expression: " + left + " and " + right);
        }

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
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
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
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
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
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
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
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
            throw new Exception("Invalid types for multiplication: " + left + " and " + right);
        }

        return "int";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "." 
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        // Array
        String array = n.f0.accept(this, argu);

        // Check if the type is valid
        if ((array == null) || !array.equals("int[]") && !array.equals("boolean[]")) {
            throw new Exception("Invalid type for array length: " + array);
        }

        return "int";
    }
    /**
     * f0 -> "new" 
     * f1 -> "boolean" 
     * f2 -> "[" 
     * f3 -> Expression() 
     * f4 -> "]"
     */
    @Override
    public String visit(BooleanArrayAllocationExpression n, Void argu) throws Exception {
        String type = n.f3.accept(this, argu);

        if (!type.equals("int")) {
            throw new Exception("Invalid type for array size: " + type);
        }

        if (zeroArraylength) {
            throw new Exception("Array length cannot be zero");
        }

        return "boolean[]";
    }
    /**
     * f0 -> "new" 
     * f1 -> "int" 
     * f2 -> "[" 
     * f3 -> Expression() 
     * f4 -> "]"
     */
    @Override
    public String visit(IntegerArrayAllocationExpression n, Void argu) throws Exception {
        // Size of the array
        String type = n.f3.accept(this, argu);

        if (!type.equals("int")) {
            throw new Exception("Invalid type for array size: " + type);
        }

        if (zeroArraylength) {
            throw new Exception("Array length cannot be zero");
        }

        return "int[]";
    }
    /**
     * f0 -> BooleanArrayAllocationExpression() | IntegerArrayAllocationExpression()
     */
    @Override
    public String visit(ArrayAllocationExpression n, Void argu) throws Exception {
        String type = n.f0.accept(this, argu);

        if (type == null) {
            throw new Exception("Invalid type for array allocation: " + type);
        }

        return type;
    }

    @Override
    public String visit(IntegerLiteral n, Void argu) throws Exception {
        int value = Integer.parseInt(n.f0.toString());
        zeroArraylength = (value == 0); // Used to check if the array length is zero

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

    // Check for a variable in the symbol table and return its type
    public String lookForId(String name) throws Exception {
        // Check if the variable is a local variable of the current method
        if (currentMethodDec != null) {
            VariableDec var = currentMethodDec.getVariableOrArgument(name);
            if (var != null) {
                return var.getType();
            }
        } else {
            throw new Exception("Current method is null");
        }

        // Check if the variable is a field of the current class
        if (currentClassDec != null) {
            VariableDec var = currentClassDec.getField(name);
            if (var != null) {
                return var.getType();
            }
        } else {
            throw new Exception("Current class is null");
        }

        return null;
    }

    @Override
    public String visit(Identifier n, Void argu) throws Exception {
        // Identifier name
        return n.f0.toString();
    }
}
