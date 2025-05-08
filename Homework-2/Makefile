all: compile

compile:
	java -jar jtb132di.jar -te minijava.jj
	java -jar javacc5.jar minijava-jtb.jj
	javac VisitorCheck.java
	javac Main.java
	javac ClassDec.java
	javac MethodDec.java
	javac VariableDec.java
	javac SymbolTable.java

clean:
	rm -rf *.class syntaxtree visitor minijava-jtb.jj *~

