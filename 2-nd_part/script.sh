#!/bin/bash

# Expected output for each test
expected_outputs=(
    "John
Doe
John Doe"
    "John
JaneJane"
    "Static
Dynamic
Probably Dynamic"
    "Tralalero Tralala Bombardino Crocodilo"
    "call tt()
Correct result
Sure you are here
abcdefihg
cbadefghi
Doe"
    "Jay
JohnJohn"
    "No definition is made, only expressions"
    ""
    "Exception in thread \"main\" java.lang.Exception: Can't recover from previous error(s)
        at java_cup.runtime.lr_parser.report_fatal_error(lr_parser.java:392)
        at java_cup.runtime.lr_parser.unrecovered_syntax_error(lr_parser.java:539)
        at java_cup.runtime.lr_parser.parse(lr_parser.java:731)
        at Main.main(Main.java:12)
make: *** [makefile:11: execute] Error 1"
    "Tralalero Tralala Bombardino Crocodilo"
    ""
)

make

for i in {1..11}; do
    test_file="tests/test$i.txt"

    echo "Running test $i..."

    output=$(make execute < "$test_file" 2>&1)

    # Remove the first three lines of the output (make logs)
    result=$(echo "$output" | tail -n +4)

    expected="${expected_outputs[$((i-1))]}"

    # Remove leading and trailing whitespaces
    expected=$(echo "$expected" | sed 's/^[ \t]*//;s/[ \t]*$//')
    result=$(echo "$result" | sed 's/^[ \t]*//;s/[ \t]*$//')

    # Check the results
    if [ "$result" == "$expected" ]; then
        echo "Test $i passed!"
        ((passed++))
    else
        echo "Test $i failed:"
        echo "Expected: '$expected'"
        echo "Got:      '$result'"
    fi

    echo "-------------------------"
done

echo "Tests completed."
echo "Total passed tests: $passed/$((i-1))"

echo "Cleaning up..."
make clean
