all: compile

compile:
	rm -f Translated.java
	jflex scanner.flex
	java -jar java-cup-11b.jar -interface -parser Parser1 parser1.cup
	java -jar java-cup-11b.jar -interface -parser Parser parser.cup
	javac -cp java-cup-11b-runtime.jar *.java

execute:
	java -cp java-cup-11b-runtime.jar:. Main
	javac Translated.java
	java Translated

clean:
	rm -f *.class *~
	rm -f Parser.java
	rm -f Parser1.java
	rm -f sym.java
	rm -f Scanner.java
	rm -f Translated.java
