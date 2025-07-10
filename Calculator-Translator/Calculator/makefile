JAVAC = javac
JAVA = java

# Java source files
SRCS = Main.java Evaluator.java ParseError.java

# Compiled class files
CLASSES = $(SRCS:.java=.class)

all: $(CLASSES)

# Compile Java files
%.class: %.java
	$(JAVAC) $<

# Run the program
run: all
	$(JAVA) Main

# Clean up
clean:
	rm -f *.class
