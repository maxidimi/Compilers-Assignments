# Compilers-Project-2

## Compilation
- All needed files are in the current folder, so just run `make` (be sure to run it within the dir).

## Run
- The driver implements the wanted usage, so -after compilation- run `java Main [file1] [file2] ... [fileN]`.<br/>
- Driver checks all files, even if one (or more) of them fail on check.<br/> At the end, if at least one file has failed, program exits with error code 1, else 0.<br/>
- Print pattern is the same as in the tutorial's test files.

## Tests - Script
- In the /tests directory, you can find tests (all of the ones in the course's site and some more) splitted in two directories:<br/> `/errors` and `/normal`.<br/>
- The normals -let's say Input.java- have also their expected result for the offset table in file Input.txt.<br/>
- There is also a script that run each file separately and check if the program has the expected behavior.<br/>
- The only case that I found that my checker behaves wrong, is the following:
```
public static void main(String[] args) {
    System.out.println(A.foo(1, 2));
}

class A {
    public int foo(int i, int j) {
        return 1; 
    }
}
```
Java fails in compilation of the above program: "Cannot make a static reference to the non-static method foo(int, int) from the type A".<br/> 
However, my checker accept it, beacuse it finds binding "A" in symbol table defined as a class. This happens due to the checker's logic, as it traverses upwards the tree by sending binding types, not identifiers' name.
