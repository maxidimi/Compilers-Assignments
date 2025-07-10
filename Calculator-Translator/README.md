# 1-Calculator-Parser-and-LL1-Parser

## About
In the second part of this homework you will implement a parser and translator for a language supporting string operations. The language supports the concatenation (+) and reverse operators over strings, function definitions and calls, conditionals (if-else i.e, every "if" must be followed by an "else"), and the following logical expressions:

    string equality (string1 = string2): Whether string1 is equal to string2.
    is-prefix-of (string1 prefix string2): Whether string1 is a prefix of string2.
    is-suffix-of (string1 suffix string2): Whether string1 is a suffix of string2.

All values in the language are strings.

Function declarations must precede all top-level expressions. The precedence of the operator expressions is defined as: precedence(if) < precedence(concat) < precedence(reverse).

Your translation will take place in two stages, each implemented via a different parser based on a context-free grammar:

    The first stage/parser will translate the input language to an intermediate representation (IR) that is a subset of the input language excluding the string equality and suffix operations. You will have to implement their functionality via the remaining operators.
    The second stage/parser will translate the intermediate representation into Java.

You will use JavaCUP for the generation of the parser combined either with a hand-written lexer or a generated-one (e.g., using JFlex, which is encouraged).

You will infer the desired syntax of the input, intermediate, and output languages from the examples below. The output language is a subset of Java so it can be compiled using javac and executed using Java or online Java compilers like this, if you want to test your output.

There is no need to perform type checking for the argument types or a check for the number of function arguments. You can assume that the program input will always be semantically correct.

You should accept input programs from stdin and print the IR to file Translated.ir, and output Java programs to file Translated.java. Note that each file of Java source code you produce must have the same name as the public Java class in it. In order to compile a file named Translated.java you need to execute the command: javac Translated.java. In order to execute the produced Translated.class file you need to execute: java Translated.

To execute the program successfully, the "main" class of your Java program must have a method with the following signature: public static void main(String[] args), which will be the main method of your program, containing all the translated statements of the input program. Moreover, for each function declaration of the input program, the translated Java program must contain an equivalent static method of the same name.
Example #1

Input:
```Java
name()  {
  "John"
}
    
surname() {
  "Doe"
}
    
fullname(first_name, sep, last_name) {
  first_name + sep + last_name
}

name()
surname()
fullname(name(), " ", surname())
```
IR:
```Java
name()  {
  "John"
}
    
surname() {
  "Doe"
}
    
fullname(first_name, sep, last_name) {
  first_name + sep + last_name
}

name()
surname()
fullname(name(), " ", surname())
```
Output (Java):
```Java
public class Translated {
  public static void main(String[] args) {
    System.out.println(name());
    System.out.println(surname());
    System.out.println(fullname(name(), " ", surname()));
  }

  public static String name() {
    return "John";
  }

  public static String surname() {
    return "Doe";
  }

  public static String fullname(String first_name, String sep, String last_name) {
    return first_name + sep + last_name;
  }
}
```
Example #2

Input:
```Java
name() {
  "John"
}

repeat(x) {
  x + x
}

cond_repeat(c, x) {
  if ("?" suffix c)
    repeat(x)
  else
    x
}

cond_repeat("yes", name())
cond_repeat("no?", "Jane")
```
IR:
```Java
name(){
  "John"
}

repeat(x){
  x + x
}

cond_repeat(c, x){
  if (reverse "?" prefix reverse c)
    repeat(x)
  else
    x
}

cond_repeat("yes", name())
cond_repeat("no?", "Jane")
```
Example #3

Input:
```Java
findLangType(langName) {
  if ("Java" prefix langName)
    if(langName prefix "Java")
      "Static"
    else
      if("script" suffix langName)
        "Dynamic"
      else
        "Unknown"
      else
        if ("script" suffix langName)
          "Probably Dynamic"
        else
          "Unknown"
}

findLangType("Java")
findLangType("Javascript")
findLangType("Typescript")
```

## Calculator

### Compilation
- All needed files are in the current folder, so just run `make` (be sure to run it within the correct dir).

### Run
  - Run `make run` and give input from keyboard, or
  - provide file input through `make run < test`.

### Grammar
- In `grammar_table.txt` there is the LL1 grammar used to parse the input, as long as, FIRST, FIRST+ and FOLLOW sets of the grammar. Also, Lookahead Table can be found, that is actually implemented in the calculator.


## LL1 Parser

### Compilation
All needed files are in the current folder, so just run make (be sure to run it within the correct dir).

### Run
- `make execute < test`, with test be the path to the input file.

### Tests - script
  - In folders /tests and /more-tests there are plenty of tests to try on. Those in the first directory are used in the following script, along with their expected result.
  - `./script.sh`, to run a script that check the parser on many cases (may need chmod on your machine),

## Grading
This assignment was graded as 10/10.
