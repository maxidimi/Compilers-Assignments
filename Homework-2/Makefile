all: compile

compile:
	java -jar jtb132di.jar -te minijava.jj
	java -jar javacc5.jar minijava-jtb.jj
	javac Main.java

clean:
	rm -rf *.class syntaxtree visitor minijava-jtb.jj *~
	rm -f JavaCharStream.java
	rm -f MiniJavaParser.java
	rm -f MiniJavaParserConstants.java
	rm -f MiniJavaParserTokenManager.java
	rm -f ParseException.java
	rm -f Token.java
	rm -f TokenMgrError.java
