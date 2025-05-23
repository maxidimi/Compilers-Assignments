# Compilers-Project-2

## Compilation
All needed files are in the current folder, so just run `make` (be sure to run it within the dir).

## Run
The driver implements the wanted usage, so -after compilation- run `java Main [file1] [file2] ... [fileN]`.<br/>
Program checks all files, even if one of them fail on the check. At the end, if at least one file has failed, program exits with error code 1-else with code 0.<br/>
Print pattern is the same as in the example files.

## Tests - Script
In the /tests directory, you can find tests (all of the ones in the course's site and some more) splitted in two directories: `/errors` and `/normal`.<br/>
The normals -let's say Input.java- have also their expected result for the offset table in file Input.txt.
There is also a script that run each file separately and check if the program has the expected behavior.
