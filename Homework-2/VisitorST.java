import syntaxtree.*;
import visitor.*;

class VisitorST extends GJDepthFirst<String, Void>{
    
    // The symbol table
    SymbolTable symbolTable;
    Boolean inVarDecleration;
    String currentClass;
    String currentMethod;
    String mainClassName;

    // Constructor
    VisitorST() {
        symbolTable = new SymbolTable();
        inVarDecleration = false;
        currentClass = null;
        currentMethod = null;
        mainClassName = null;
    }

    /**
     * f1 -> Identifier() = main class name
     * f11 -> Identifier() = arguments array name
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        n.f1.accept(this, null);

        // Main class name
        String mainClassName = n.f1.accept(this, null);
        symbolTable.mainClassName = currentClass = this.mainClassName = mainClassName;

        // Create main class & add it to the symbol table
        ClassDec mainClass = new ClassDec(mainClassName, null);
        symbolTable.setClass(mainClass);

        // Command line arguments
        String args = n.f11.accept(this, null);
        MethodDec mainMethod = new MethodDec("main", "void");
        mainMethod.setArgument(args, "String[]");

        // Add the main method to the main class
        mainClass.setMethod(mainMethod);
        currentMethod = "main";

        // Variable declarations
        inVarDecleration = true;
        n.f14.accept(this, argu);
        inVarDecleration = false;

        // Statements
        n.f15.accept(this, argu);

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

        // Check if the class has same name as the main class
        if (classname.equals(mainClassName)) {
            throw new Exception("ClassDeclaration: Class name can't be the same as the main class");
        }

        // Create class & add it to the symbol table
        symbolTable.setClass(new ClassDec(classname, null));

        // Field declarations
        n.f3.accept(this, argu);

        // Methods
        n.f4.accept(this, argu);

        // Reset current class
        currentClass = null;

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

        // Check if the class has same name as the main class
        if (classname.equals(mainClassName)) {
            throw new Exception("ClassExtendsDeclaration: Class can't be the same as the main class");
        }

        // Parent class name
        String parent = n.f3.accept(this, argu);

        // Check that parent class has already be defined
        ClassDec parentClass = symbolTable.getClass(parent);

        // Create class & add it to the symbol table
        symbolTable.setClass(new ClassDec(classname, parentClass));

        // Field declarations
        n.f5.accept(this, argu);

        // Methods
        n.f6.accept(this, argu);

        // Reset current class
        currentClass = null;

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
        String myType = n.f1.accept(this, null);

        // Name of the method
        String myName = currentMethod = n.f2.accept(this, null);

        // Check for overriding & then add the method to the current class
        // In this phase, we don't need to check if the signatures are same, just that a method with the same name exists in the parent(s) class
        MethodDec methodDec = new MethodDec(myName, myType);
        ClassDec classDec = symbolTable.classes.get(currentClass);
        classDec.setMethod(methodDec);
        if (classDec.hasParent()) {
            ClassDec tmpClass = classDec;
            while (tmpClass.hasParent()) {
                tmpClass = symbolTable.getClass(tmpClass.getParent());
                if (tmpClass.hasMethod(myName)) {
                    methodDec.setOverriding(true); // Set flag for overriding
                    break;
                }
            }
        }
        
        // Arguments list
        String argumentList = n.f4.accept(this, argu);
        if (argumentList != null && !argumentList.isEmpty()) {
            String[] args = argumentList.split(","); // Split by comma - "Type Identifier"
            for (String arg : args) {
                String[] parts = arg.trim().split(" "); // Split by space - "Type" "Identifier"
                String argType = parts[0];
                String argName = parts[1];
                methodDec.setArgument(argName, argType);
            }
        }

        // Variable declarations
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
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Void argu) throws Exception {
        // Type of the variable
        String type = n.f0.accept(this, argu);

        // Name of the variable
        String var = n.f1.accept(this, argu);

        // Add the variable to the symbol table
        ClassDec classDec = symbolTable.getClass(currentClass);
        if (inVarDecleration) { // Add as a variable to the current method
            MethodDec methodDec = classDec.getMethod(currentMethod);
            if (methodDec != null) {
                methodDec.setVariable(var, type);
            } else {
                throw new Exception("VarDeclaration: Method " + currentMethod + " not found in symbol table, class " + currentClass);
            }

        } else { // Add as a field to the current class
            classDec.setField(var, type);
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
     * f0 -> ,
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception{
        // Type + Name
        return n.f0.accept(this, null) + " " + n.f1.accept(this, null);
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
