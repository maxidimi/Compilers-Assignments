import syntaxtree.*;
import visitor.*;

class VisitorCheck extends GJDepthFirst<String, Void>{
    SymbolTable symbolTable; // Symbol table built in the previous traversal
    String currentClass; // Name of the current class that is being visited
    String currentMethod; // Name of the current method that is being visited
    ClassDec currentClassDec; // ClassDec object of the current class
    MethodDec currentMethodDec; // MethodDec object of the current method
    boolean fromPE; // Flag to check if the current id is from a PrimaryExpression
    boolean fromMessageSend; // Flag to check if the caller is MessageSend

    VisitorCheck(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.currentClass = null;
        this.currentMethod = null;
        this.currentClassDec = null;
        this.currentMethodDec = null;
        this.fromPE = false;
        this.fromMessageSend = false;
    }

    // Check if the type is valid - int, boolean, int[], boolean[] or a defined class
    public boolean isValidType(String type) {    
        return !(type == null || !(type.equals("int") || type.equals("boolean") || type.equals("int[]") || type.equals("boolean[]") || symbolTable.hasClass(type)));
    }

    /**
     * f1 -> Identifier() = main class name
     * f11 -> Identifier() = command line arguments String[]
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        // Main class name
        String mainClassName = currentClass = n.f1.accept(this, null);

        // Get main class & method
        currentMethod = "main";
        currentClassDec = symbolTable.getClass(mainClassName);
        currentMethodDec = symbolTable.getClass(mainClassName).getMethod("main");

        // Variable declarations
        n.f14.accept(this, null);

        // Statements
        n.f15.accept(this, null);

        // Reset current class and method
        currentClass = null;
        currentClassDec = null;

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
        
        // Field declarations
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

        // Field declarations
        n.f5.accept(this, argu);

        // Methods
        n.f6.accept(this, argu);

        // Reset current class
        currentClass = null;
        currentClassDec = null;

        return null;
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
        String myType = currentMethod = n.f1.accept(this, null);

        // Name of the method
        String myName = n.f2.accept(this, null);
        currentMethodDec = symbolTable.getClass(currentClass).getMethod(myName);
        
        // Arguments list
        String argumentList = n.f4.accept(this, argu);

        // Variable declarations
        n.f7.accept(this, argu);

        // Statements
        n.f8.accept(this, argu);

        // Return expression's type
        String returnExpType = checkForId(n.f10.accept(this, argu));

        // Check if the return type is same as in the signature or if it is a subclass
        if (!returnExpType.equals(myType) && !isSubtype(myType, returnExpType)) {
            throw new Exception("MethodDeclaration: Invalid return type for method " + myName + ": " + returnExpType + (" instead of " + myType) + " in class " + currentClass);
        }

        // Check for overriding - if exists, methods must have same argument types and return type
        if (currentMethodDec.isOverriding()) {
            ClassDec parentClass = symbolTable.getClass(currentClassDec.getParent());
            MethodDec parentMethod = parentClass.getMethod(myName);

            // Check if the return type is same as in the overriden's signature
            if (!parentMethod.getReturnType().equals(myType)) {
                throw new Exception("MethodDeclaration: Invalid return type for overriding method " + myName + ": " + myType + (" instead of " + parentMethod.getReturnType()) + " in class " + currentClass);
            }

            // Get expected types
            String[] expectedTypes = new String[parentMethod.getArguments().size()];
            int i = 0;
            for (VariableDec arg : parentMethod.getArguments().values()) {
                expectedTypes[i++] = arg.getType();
            }

            // Get input types
            String[] inputTypes = argumentList != null ? argumentList.split(", ") : new String[0];

            // Check if the number of arguments is the same
            if (inputTypes.length != parentMethod.getArguments().size()) {
                throw new Exception("MethodDeclaration: Invalid number of arguments for overriding method " + myName + ": " + inputTypes.length + (" instead of " + parentMethod.getArguments().size()) + " in class " + currentClass);
            }

            // Check that the given types are same as the expected ones
            for (i = 0; i < inputTypes.length; i++) {
                String argType = inputTypes[i].trim();
                argType = argType.split(" ")[0];
                if (!argType.equals(expectedTypes[i])) {
                    throw new Exception("MethodDeclaration: Invalid type for argument " + (i + 1) + " for overriding method " + myName + ": " + argType + (" instead of " + expectedTypes[i]) + " in class " + currentClass);
                }
            }
        }

        // Reset current method
        currentMethod = null;
        currentMethodDec = null;
        
        return null;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, Void argu) throws Exception {
        // Type & name of the variable
        String type = n.f0.accept(this, argu);
        String var = n.f1.accept(this, argu);

        // Check if the type is valid
        if (!isValidType(type)) {
            throw new Exception("VarDeclaration: Invalid type " + type + " for variable " + var);
        }
        
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
        // Type & name of argument
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);

        // Check if the type is valid
        if (!isValidType(type)) {
            throw new Exception("FormalParameter: Invalid type " + type + " for argument " + name);
        }

        return type + " " + name;
    }

    /**
     * f0 -> (
     * f1 -> Expression()
     * f2 -> )
     */
    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        return checkForId(n.f1.accept(this, argu)); // Return the type of the expression
    }

    /**
     * f0 -> "if" 
     * f1 -> "(" 
     * f2 -> Expression() 
     * f3 -> ")" 
     * f4 -> Statement() 
     * f5 -> "else" 
     * f6 -> Statement()
     */
    @Override
    public String visit(IfStatement n, Void argu) throws Exception {
        // Condition type
        String conditionType = checkForId(n.f2.accept(this, argu));

        // Check if the type is valid
        if (!conditionType.equals("boolean")) {
            throw new Exception("IfStatement: Invalid type " + conditionType + " for if statement");
        }

        // Then statement
        n.f4.accept(this, argu);

        // Else statement
        n.f6.accept(this, argu);

        return null;
    }
    /**
     * f0 -> "while" 
     * f1 -> "(" 
     * f2 -> Expression() 
     * f3 -> ")" 
     * f4 -> Statement()
     */
    @Override
    public String visit(WhileStatement n, Void argu) throws Exception {
        // Condition type
        String conditionType = checkForId(n.f2.accept(this, argu));

        // Check if the type is valid
        if (!conditionType.equals("boolean")) {
            throw new Exception("WhileStatement: Invalid type " + conditionType + " for while statement");
        }

        // Statement
        n.f4.accept(this, argu);

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
        // Expression type to print
        String expressionType = checkForId(n.f2.accept(this, argu));

        // Check if the type is valid
        if (!expressionType.equals("int")) {
            throw new Exception("PrintStatement: Invalid type " + expressionType + " for print statement");
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
        // Array type
        String arrayType = checkForId(n.f0.accept(this, argu));

        // Index type
        String index = checkForId(n.f2.accept(this, argu));

        // Expression type
        String expr = checkForId(n.f5.accept(this, argu));

        // Check if the types are valid
        if ((arrayType == null) || (index == null)) {
            throw new Exception("ArrayAssignmentStatement: Invalid null type for array assignment");
        } else if (!index.equals("int")) { // Check if the index is an integer
            throw new Exception("ArrayAssignmentStatement: Invalid type for index in array assignment: " + index);
        } else if ((arrayType.equals("int[]") && !expr.equals("int")) || (arrayType.equals("boolean[]") && !expr.equals("boolean"))) { // Check if the expression is of the same type as the array
            throw new Exception("ArrayAssignmentStatement: Invalid types for array assignment: " + arrayType + " and " + index);
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
        // Variable type
        String var = checkForId(n.f0.accept(this, argu));

        // Expression type
        String expr = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid - or if the variable is a subclass of the expression
        if ((var == null) || (expr == null) || (!var.equals(expr) && !isSubtype(var, expr))) {
            throw new Exception("AssignmentStatement: Invalid types for assignment: " + var + " and " + expr);
        }

        return null;
    }

    /**
     * f0 -> AndExpression() | CompareExpression() | PlusExpression() | MinusExpression() | 
     *       TimesExpression() | ArrayLookup() | ArrayLength() | MessageSend() | Clause()
     */
    @Override
    public String visit(Expression n, Void argu) throws Exception {
        // Visit the expression and return its type
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> IntegerLiteral() | TrueLiteral() | FalseLiteral() | Identifier() | 
     *       ThisExpression() | ArrayAllocationExpression() | AllocationExpression() | BracketExpression()
     */
    @Override
    public String visit(PrimaryExpression n, Void argu) throws Exception {
        // Set flag to check if the current case is an Identifier - used later
        if (n.f0.choice instanceof Identifier) {
            fromPE = true;
        }

        // Visit the primary expression and return the type
        String type = n.f0.accept(this, argu);

        // Check if the type is valid
        if (type == null) {
            throw new Exception("PrimaryExpression: Invalid null type for primary expression");
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
            throw new Exception("AllocationExpression: Invalid type " + type + " for allocation expression");
        }

        return type;
    }

    /**
     * f0 -> "this"
     */
    @Override
    public String visit(ThisExpression n, Void argu) throws Exception {
        if (currentClass == null) {
            throw new Exception("ThisExpression: Invalid this expression: " + n.f0.toString());
        }
        // "this" has the same type as the current class
        return currentClass;
    }

    /**
     * f0 -> "!" 
     * f1 -> Clause()
     */
    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        String clause = n.f1.accept(this, argu);

        // Check if the type is boolean
        if ((clause == null) || !clause.equals("boolean")) {
            throw new Exception("NotExpression: Invalid type for not expression: " + clause);
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
        // Left expression type
        String left = n.f0.accept(this, argu);

        // Right expression type
        String right = n.f2.accept(this, argu);

        // Check if the types are valid
        if ((left == null) || (right == null) || !left.equals("boolean") || !right.equals("boolean")) {
            throw new Exception("AndExpression: Invalid types for and expression: " + left + " and " + right);
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
        // Left expression type
        String left = checkForId(n.f0.accept(this, argu));

        // Right expression type
        String right = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
            throw new Exception("CompareExpression: Invalid types for comparison: " + left + " and " + right);
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
        // Left expression type
        String left = checkForId(n.f0.accept(this, argu));

        // Right expression type
        String right = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
            throw new Exception("PlusExpression: Invalid types for addition: " + left + " and " + right);
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
        // Left expression type
        String left = checkForId(n.f0.accept(this, argu));

        // Right expression type
        String right = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
            throw new Exception("MinusExpression: Invalid types for subtraction: " + left + " and " + right);
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
        // Left expression type
        String left = checkForId(n.f0.accept(this, argu));

        // Right expression type
        String right = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid
        if ((left == null) || (right == null) || !left.equals("int") || !right.equals("int")) {
            throw new Exception("TimesExpression: Invalid types for multiplication: " + left + " and " + right);
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
        // Array name type
        String arrayType = checkForId(n.f0.accept(this, argu));

        // Index type type
        String index = checkForId(n.f2.accept(this, argu));

        // Check if the types are valid
        if ((!arrayType.equals("int[]") && !arrayType.equals("boolean[]")) || !index.equals("int")) {
            throw new Exception("ArrayLookup: Invalid types for array lookup: " + arrayType + " and " + index + " (instead of int[]/boolean[] and int)");
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
        // Array type
        String array = checkForId(n.f0.accept(this, argu));

        // Check if the type is valid
        if ((array == null) || !array.equals("int[]") && !array.equals("boolean[]")) {
            throw new Exception("ArrayLength: Invalid type for array length: " + array);
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
            throw new Exception("ExpressionList: Invalid null type for expression list");
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
        // Object type
        fromMessageSend = true; fromPE = false;
        String objectType = checkForIdAndClass(n.f0.accept(this, argu));
        fromMessageSend = fromPE = false; // Reset flags

        // Method name
        String method = n.f2.accept(this, argu);

        // Arguments list - their types (if not null)
        String inputArgsTypes = n.f4.accept(this, argu);

        // Check if the object (it's type) has the method
        MethodDec methodDec = lookForMethod(method, objectType);
        String type = methodDec.getReturnType();

        // Check if the method has the same number of arguments
        if ((methodDec.getArguments().size() == 0) && (inputArgsTypes != null)) {
            throw new Exception("MessageSend: Method " + method + " in class " + objectType + " has no arguments");
        } else if ((methodDec.getArguments().size() > 0) && (inputArgsTypes == null)) {
            throw new Exception("MessageSend: Method " + method + " in class " + objectType + " has arguments");
        // Check that the types are correct
        } else if ((methodDec.getArguments().size() != 0) && (inputArgsTypes != null)) {
            // Split the call's argument types
            String[] argTypes = inputArgsTypes.split(", ");
            if (argTypes.length != methodDec.getArguments().size()) { // Check that call has the correct number of arguments
                throw new Exception("MessageSend: Invalid number of arguments for method " + method + " in class " + objectType);
            }

            // Get the expected types
            String[] correctArgsTypes = new String[methodDec.getArguments().size()];
            int i = 0;
            for (VariableDec arg : methodDec.getArguments().values()) {
                correctArgsTypes[i++] = arg.getType();
            }

            // Check that the types are correct
            for (i = 0; i < argTypes.length; i++) {
                String inpArgType = argTypes[i].trim();
                if (!inpArgType.equals(correctArgsTypes[i]) && !isSubtype(correctArgsTypes[i], inpArgType)) { // Check for subtyping on argument
                    throw new Exception("MessageSend: Message send: Invalid type for argument " + (i + 1) + " of method " + method + " in class " + objectType + ": " + inpArgType + (" instead of " + correctArgsTypes[i]));
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
        String type = checkForId(n.f3.accept(this, argu));

        if (!type.equals("int")) {
            throw new Exception("BooleanArrayAllocationExpression: Invalid type for array size: " + type);
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
        String type = checkForId(n.f3.accept(this, argu));

        if (!type.equals("int")) {
            throw new Exception("IntegerArrayAllocationExpression: Invalid type for array size: " + type);
        }

        return "int[]";
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
    public String visit(Identifier n, Void argu) throws Exception {
        // Identifier name
        return n.f0.toString();
    }

    // Check for a variable in the symbol table and return its type
    public String lookForId(String name, Boolean checkClassesLater) throws Exception {
        // Check if the variable is a local variable of the current method
        if (currentMethodDec != null) {
            VariableDec var = currentMethodDec.getVariableOrArgument(name);
            if (var != null) {
                return var.getType();
            }
        } else {
            throw new Exception("lookForId: Current method is null");
        }

        // Check if the variable is a field of the current class
        if (currentClassDec != null) {
            VariableDec var = currentClassDec.getField(name);
            if (var != null) {
                return var.getType();
            }
        } else {
            throw new Exception("lookForId: Current class is null");
        }

        // Check recursively in the super class(es)
        if (currentClassDec.hasParent()) {
            String temp = currentClass;
            ClassDec classTemp = symbolTable.getClass(temp);
            
            currentClass = classTemp.getParent();
            currentClassDec = symbolTable.getClass(currentClass);
            String type = lookForId(name, checkClassesLater);

            currentClass = temp;
            currentClassDec = classTemp;

            return type;
        } else if (checkClassesLater) { // Don't throw an exception, we have further checks later
            return null;
        } else {
            throw new Exception("lookForId: Can't find symbol " + name);
        }
    }

    // Find if a method exists in the class or its super class(es)
    public MethodDec lookForMethod(String method, String className) throws Exception {
        // Check if the method is in the input class
        ClassDec classTemp = symbolTable.getClass(className);
        MethodDec methodDec = symbolTable.getClass(className).getMethod(method);
        if (methodDec != null) {
            return methodDec;
        }
        // Check recursively in the super class(es)
        if (symbolTable.getClass(className).hasParent()) {
            className = classTemp.getParent();
            methodDec = lookForMethod(method, className);
            return methodDec;
        } else {
            throw new Exception("lookForMethod: Can't find method " + method + " in class " + className);
        }
    }

    // Check if the type is valid - int, boolean, int[], boolean[] or a defined class and then check if it is a variable or a class
    public String checkForId(String name) throws Exception {
        if (!isValidType(name)) {
            String temp = lookForId(name, false);
            if (temp != null) {
                name = temp;
            } else {
                throw new Exception("checkForId: Invalid type for binding: " + name);
            }
        }

        return name;
    }

    public boolean isPrimitiveType(String type) {
        return type.equals("int") || type.equals("boolean") || type.equals("int[]") || type.equals("boolean[]");
    }

    // Check first if it is a primitive type, then check if it is a class - used in method calls
    public String checkForIdAndClass(String name) throws Exception {
        // Check that type in not a primitive type
        if (name == null || isPrimitiveType(name)) {
            throw new Exception("checkForId: Invalid object type for message send: " + name);
        } else { // Look for the binding in the symbol table
            String temp = lookForId(name, true);

            if (fromMessageSend && fromPE) {
                // If the identifier is the object from a message send, we need to check that name is not just a class name
                if (temp == null) {
                    throw new Exception("checkForId: Invalid type for message send: " + name);
                } else {
                    return temp;
                }
            } else {
                if (temp == null && symbolTable.hasClass(name)) {
                    return name;
                } else if (!isPrimitiveType(temp)) {
                    return temp;
                } else {
                    throw new Exception("checkForId: Invalid type for message send: " + temp);
                }
            }
        }
    }

    // A a = new B(); where B extends A
    public boolean isSubtype(String A, String B) throws Exception {
        if (isPrimitiveType(A) || isPrimitiveType(B)) { // Primitive types are not subtypes of each other
            return false;
        }
        
        // Check recursively in the super class(es)
        ClassDec classTemp = symbolTable.getClass(B);
        while (classTemp.hasParent()) {
            if (classTemp.getParent().equals(A)) {
                return true;
            }
            classTemp = symbolTable.getClass(classTemp.getParent());
        }
        return false;
    }
}
