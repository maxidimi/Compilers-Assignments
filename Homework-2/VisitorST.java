import syntaxtree.*;
import visitor.*;

class VisitorST extends GJDepthFirst<String, Void>{
    
    // The symbol table
    SymbolTable symbolTable = new SymbolTable();

    Boolean inVarDecleration = false;

    String currentClass = null;
    String currentMethod = null;

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
        currentClass = classname;

        // Create class & add it to the symbol table
        ClassDec classDec = new ClassDec(classname, null, 0);
        symbolTable.setClass(classname, classDec);

        n.f2.accept(this, argu);

        // Fields
        n.f3.accept(this, argu);

        // Methods
        n.f4.accept(this, argu);

        n.f5.accept(this, argu);

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
        n.f0.accept(this, argu);

        // Classname
        String classname = n.f1.accept(this, null);
        currentClass = classname;

        n.f2.accept(this, argu);

        // Parent class
        String parent = n.f3.accept(this, argu);

        // Add the class to the symbol table
        ClassDec classDec = new ClassDec(classname, parent, 0);
        symbolTable.setClass(classname, classDec);

        // Add parent's offset to the class
        ClassDec parentClass = symbolTable.getClass(parent);
        if (parentClass != null) {
            classDec.offset = parentClass.offset;
        } else { // Parent class must always be defined before the child class
            System.err.println("Error: Parent class " + parent + " not found in symbol table");
        }
        
        n.f4.accept(this, argu);

        // Fields
        n.f5.accept(this, argu);

        // Methods
        n.f6.accept(this, argu);

        n.f7.accept(this, argu);

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

        n.f2.accept(this, argu);

        if (inVarDecleration) { // Add as a variable to the current method
            MethodDec methodDec = symbolTable.getMethod(currentMethod);
            if (methodDec != null) {
                methodDec.setVariable(var, type);
            } else {
                System.err.println("Error: Method not found in symbol table");
            }
        } else if (currentMethod != null) { // Add as a field to the current class
            MethodDec methodDec = symbolTable.getMethod(currentMethod);
            if (methodDec != null) {
                methodDec.setVariable(var, type);
            } else {
                System.err.println("Error: Method not found in symbol table");
            }
        } else { // Add as a field to the current class
            ClassDec classDec = symbolTable.getClass(currentClass);
            if (classDec != null) {
                classDec.setField(var, type);
            } else {
                System.err.println("Error: Class not found in symbol table");
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

        n.f3.accept(this, argu);

        // Add the method to the current class
        MethodDec methodDec = new MethodDec(myName, myType);
        ClassDec classDec = symbolTable.classes.get(currentClass);
        if (classDec != null) {
            classDec.methods.put(myName, methodDec);
            methodDec.classInto = classDec;
        } else {
            System.err.println("Error: Class not found in symbol table");
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
                    System.err.println("Error: Invalid argument format");
                }
            }
        }

        n.f5.accept(this, argu);
        n.f6.accept(this, argu);

        // Variables
        inVarDecleration = true;
        n.f7.accept(this, argu);
        inVarDecleration = false;

        // Statements
        n.f8.accept(this, argu);

        n.f9.accept(this, argu);

        // Return expression
        n.f10.accept(this, argu);

        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

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