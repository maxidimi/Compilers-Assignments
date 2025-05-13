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
        } else if (type.equals("int") || type.equals("boolean") || type.equals("int[]") || type.equals("boolean[]") || symbolTable.classExists(type)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * f1 -> Identifier()
     * f11 -> Identifier()
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        // Main class name
        String mainClassName = n.f1.accept(this, null);
        currentClass = mainClassName;
        currentClassDec = symbolTable.getClass(mainClassName);
        if (currentClassDec == null) {
            throw new Exception("Class " + mainClassName + " not found in symbol table");
        }

        // Declerations
        n.f14.accept(this, null);
        currentMethod = "main";
        currentMethodDec = symbolTable.getClass(mainClassName).getMethod("main");
        if (currentMethodDec == null) {
            throw new Exception("Method main not found in symbol table, class " + mainClassName);
        }

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
        String returnType = n.f10.accept(this, argu);
        // Check if the return type is valid
        if (returnType != null && !isValidType(returnType)) {
            returnType = lookForId(returnType);
        }

        // Check if the return type is same as in the signature
        if (!returnType.equals(myType)) {
            throw new Exception("Invalid return type for method " + myName + ": " + returnType + (" instead of " + myType) + " in class " + currentClass);
        }

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
     * f0 -> ,
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ( FormalParameterTerm() )
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

    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        // Expression
        String type = checkForId(n.f1.accept(this, argu));

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
        String expr = checkForId(n.f2.accept(this, argu));

        // Check if the type is valid
        if (!expr.equals("int")) {
            throw new Exception("Invalid type " + expr + " for print statement");
        }

        return null;
    }

    /**
     * f0 -> Identifier() 
     * f1 -> "[" 
     * f2 -> Expression() 
     * f3 -> "]" 
     * f4 -> "=" 
     * f5 -> Expression() 
     * f6 -> ";"
     */
    @Override
    public String visit(ArrayAssignmentStatement n, Void argu) throws Exception {
        // Array name
        String arrayType = checkForId(n.f0.accept(this, argu));

        // Index type
        String index = checkForId(n.f2.accept(this, argu));

        // Expression
        String expr = checkForId(n.f5.accept(this, argu));

        // Check if the types are valid
        if ((arrayType == null) || (index == null)) {
            throw new Exception("Invalid null type for array assignment");
        } else if (!index.equals("int")) { // Check if the index is an integer
            throw new Exception("Invalid type for index in array assignment: " + index);
        } else if ((arrayType.equals("int[]") && !expr.equals("int")) || (arrayType.equals("boolean[]") && !expr.equals("boolean"))) { // Check if the expression is of the same type as the array
            throw new Exception("Invalid types for array assignment: " + arrayType + " and " + index);
        }

        return null;
    }

    /**
     * f0 -> Identifier() 
     * f1 -> "=" 
     * f2 -> Expression() 
     * f3 -> ";"
     */
    @Override
    public String visit(AssignmentStatement n, Void argu) throws Exception {
        // Variable name
        String var = checkForId(n.f0.accept(this, argu));

        // Expression
        String expr = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid
        if ((var == null) || (expr == null) || !var.equals(expr)) {
            throw new Exception("Invalid types for assignment: " + var + " and " + expr);
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

        // Check if the type is valid
        if (type == null) {
            throw new Exception("Invalid null type for primary expression");
        }

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
        String left = checkForId(n.f0.accept(this, argu));

        // Right expression
        String right = checkForId(n.f2.accept(this, argu));

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
        String left = checkForId(n.f0.accept(this, argu));

        // Right expression
        String right = checkForId(n.f2.accept(this, argu));

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
        String left = checkForId(n.f0.accept(this, argu));

        // Right expression
        String right = checkForId(n.f2.accept(this, argu));

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
        String left = checkForId(n.f0.accept(this, argu));

        // Right expression
        String right = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
            throw new Exception("Invalid types for multiplication: " + left + " and " + right);
        }

        return "int";
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
        String arrayType = checkForId(n.f0.accept(this, argu));

        // Index type
        String index = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid
        if ((arrayType == null) || (index == null) || !arrayType.equals("int[]") || !index.equals("int")) {
            throw new Exception("Invalid types for array lookup: " + arrayType + " and " + index);
        }

        return arrayType.substring(0, arrayType.length() - 2); // Remove the brackets
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "." 
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        // Array
        String array = checkForId(n.f0.accept(this, argu));

        // Check if the type is valid
        if ((array == null) || !array.equals("int[]") && !array.equals("boolean[]")) {
            throw new Exception("Invalid type for array length: " + array);
        }

        return "int";
    }

    /**
     * f0 -> Expression() 
     * f1 -> ExpressionTail()
     */
    @Override
    public String visit(ExpressionList n, Void argu) throws Exception {
        String ret = "";

        // Expression
        String expr = n.f0.accept(this, argu);
        if (expr == null) {
            throw new Exception("Invalid null type for expression list");
        }
        
        ret += expr;
        // Expression tail
        String tail = n.f1.accept(this, argu);
        if (tail != null) {
            ret += ", " + tail;
        }

        return ret;
    }

    /**
     * f0 -> ,
     * f1 -> Expression()
     */
    @Override
    public String visit(ExpressionTerm n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ( ExpressionTerm() )
     */
    @Override
    public String visit(ExpressionTail n, Void argu) throws Exception {
        String ret = "";

        // Check if the expression tail is empty
        if (n.f0.present()) {
            for (Node node : n.f0.nodes) {
                ret += node.accept(this, argu) + ", ";
            }
            ret = ret.substring(0, ret.length() - 2); // Remove the last comma
        }

        return ret;
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "." 
     * f2 -> Identifier() 
     * f3 -> "(" 
     * f4 -> ( ExpressionList() )? 
     * f5 -> ")"
     */
    @Override
    public String visit(MessageSend n, Void argu) throws Exception {
        String type = null;

        // Object name
        String objectType = checkForId(n.f0.accept(this, argu));

        // Method name
        String method = n.f2.accept(this, argu);

        // Arguments list
        String args = n.f4.accept(this, argu);

        // Check if the object (it's type) has the method
        MethodDec methodDec = symbolTable.getClass(objectType).getMethod(method);
        if (methodDec == null) {
            throw new Exception("Method " + method + " not found in class " + objectType);
        }
        type = methodDec.getReturnType();
        if (type == null) {
            throw new Exception("Invalid type for method " + method + " in class " + objectType);
        }

        // Check if the method has the same number of arguments
        if (methodDec.getArguments().size() == 0 && args != null) {
            throw new Exception("Method " + method + " in class " + objectType + " has no arguments");

        } else if (methodDec.getArguments().size() > 0 && args == null) {
            throw new Exception("Method " + method + " in class " + objectType + " has arguments");

        } else if (methodDec.getArguments().size() != 0 && args != null) { // Check that the types are correct
            // Split the call arguments
            String[] argTypes = args.split(", ");
            if (argTypes.length != methodDec.getArguments().size()) { // Check that call has the correct number of arguments
                throw new Exception("Invalid number of arguments for method " + method + " in class " + objectType);
            }

            // Get the proper types
            String[] argTypesList = new String[methodDec.getArguments().size()];
            int i = 0;
            for (VariableDec arg : methodDec.getArguments().values()) {
                argTypesList[i] = arg.getType();
                i++;
            }

            // Check that the types are correct
            for (i = 0; i < argTypes.length; i++) {
                String argType = argTypes[i].trim();
                if (!argType.equals(argTypesList[i])) {
                    throw new Exception("Invalid type for argument " + (i + 1) + " of method " + method + " in class " + objectType);
                }
            }
        }

        return type;
    }

    /**
     * f0 -> NotExpression() | PrimaryExpression()
     */
    @Override
    public String visit(Clause n, Void argu) throws Exception {
        return checkForId(n.f0.accept(this, argu));
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
    @Override
    public String visit(Identifier n, Void argu) throws Exception {
        // Identifier name
        return n.f0.toString();
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

        // Check recursively in the super class
        if (currentClassDec.getParent() != null) {
            String temp = currentClass;
            ClassDec classTemp = symbolTable.getClass(temp);
            
            currentClass = classTemp.getParent();
            currentClassDec = symbolTable.getClass(currentClass);
            if (currentClassDec == null) {
                throw new Exception("Class " + currentClass + " not found in symbol table");
            }
            String type = lookForId(name);

            currentClass = temp;
            currentClassDec = classTemp;

            return type;
        } else {
            throw new Exception("Class " + currentClass + " does not have a parent");
        }
    }

    public String checkForId(String name) throws Exception {
        if (!isValidType(name)) {
            String temp = lookForId(name);
            if (temp != null) {
                name = temp;
            } else {
                throw new Exception("Invalid type for array: " + name);
            }
        }

        return name;
    }
}
