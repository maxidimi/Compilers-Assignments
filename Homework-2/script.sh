#!/bin/bash

JAVA_CLASS=Main
TEST_DIR=tests/normal
TMP_OUT=tmp_output.txt
passed=0
failed=0

make
for input_file in "$TEST_DIR"/*.java; do
    base_name=$(basename "$input_file" .java)
    expected_file="$TEST_DIR/$base_name.txt"

    if [[ -f "$expected_file" ]]; then
        echo "Running test: $base_name"

        # Run the program
        java "$JAVA_CLASS" "$input_file" >& "$TMP_OUT"

        # Compare outputs
        if diff -q "$TMP_OUT" "$expected_file" > /dev/null; then
            echo "Passed"
            ((passed++))
        else
            echo "Failed"
            echo "Diff:"
            diff "$TMP_OUT" "$expected_file"
            ((failed++))
        fi
    else
        echo "Output file missing: $expected_file"
    fi
done

# Clean up
echo
echo "Cleaning up..."
rm -f "$TMP_OUT"
make clean

# Print stats
echo
echo "Total tests: $((passed + failed))"
echo "  ---Tests passed: $passed/$((passed + failed))"
echo "  ---Tests failed: $failed/$((passed + failed))"
