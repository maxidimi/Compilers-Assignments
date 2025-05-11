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

        // Main class name
        String mainClassName = n.f1.accept(this, null);
        currentClass = mainClassName;

        // Create main class & add it to the symbol table
        String mainClass = n.f1.accept(this, null);
        ClassDec mainClassDec = new ClassDec(mainClass, null);
        symbolTable.setClass(mainClassDec);
        this.mainClassName = mainClass;

        // Command line arguments
        String args = n.f11.accept(this, null);
        String argsType = "String[]";
        MethodDec mainMethod = new MethodDec("main", "void");
        mainMethod.setArgument(args, argsType);

        // Variables
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
            throw new Exception("Class " + classname + " cannot be the same as the main class");
        }

        // Create class & add it to the symbol table
        ClassDec classDec = new ClassDec(classname, null);
        symbolTable.setClass(classDec);

        // Fields
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
            throw new Exception("Class " + classname + " cannot be the same as the main class");
        }

        // Parent class
        String parent = n.f3.accept(this, argu);

        // Get the parent class from the symbol table
        ClassDec parentClass = symbolTable.getClass(parent);
        if (parentClass == null) { // Parent class must always be defined before the child class
            throw new Exception("Parent class " + parent + " not found in symbol table");
        }

        // Create class & add it to the symbol table
        ClassDec classDec = new ClassDec(classname, parentClass);
        symbolTable.setClass(classDec);

        // Fields
        n.f5.accept(this, argu);

        // Methods
        n.f6.accept(this, argu);

        // Reset current class
        currentClass = null;

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

        // Add the variable to the symbol table
        ClassDec classDec = symbolTable.getClass(currentClass);
        if (classDec == null) {
            throw new Exception("Class " + currentClass + " not found in symbol table");
        } else {
            if (inVarDecleration) { // Add as a variable to the current method
                MethodDec methodDec = classDec.getMethod(currentMethod);
                if (methodDec != null) {
                    methodDec.setVariable(var, type);
                } else {
                    throw new Exception("Method " + currentMethod + " not found in symbol table, class " + currentClass);
                }

            } else { // Add as a field to the current class
                classDec.setField(var, type);
            }
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

        // Add the method to the current class
        MethodDec methodDec = new MethodDec(myName, myType);
        ClassDec classDec = symbolTable.classes.get(currentClass);
        if (classDec != null) {
            classDec.setMethod(methodDec);
        } else {
            throw new Exception("Class " + currentClass + " not found in symbol table");
        }
        
        // Arguments list
        String argumentList = n.f4.accept(this, argu);
        if (argumentList != null && !argumentList.isEmpty()) {
            String[] args = argumentList.split(",");
            for (String arg : args) {
                String[] parts = arg.trim().split(" ");
                if (parts.length == 2) {
                    String argType = parts[0];
                    String argName = parts[1];
                    methodDec.setArgument(argName, argType);
                } else {
                    throw new Exception("Invalid argument format");
                }
            }
        }

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