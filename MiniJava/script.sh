#!/bin/bash

JAVA_CLASS=Main
TEST_DIR_NORMAL=tests/normal
TEST_DIR_ERRORS=tests/errors
TMP_OUT=tmp_output.txt
passed=0
failed=0
err_passed=0
err_failed=0

make
echo
echo "==== Running normal tests ===="
for input_file in "$TEST_DIR_NORMAL"/*.java; do
    base_name=$(basename "$input_file" .java)
    expected_file="$TEST_DIR_NORMAL/$base_name.txt"

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

echo
echo "==== Running error tests (should fail) ===="
for input_file in "$TEST_DIR_ERRORS"/*.java; do
    base_name=$(basename "$input_file" .java)
    echo "Running error test: $base_name"

    # Run the program expecting failure
    if java "$JAVA_CLASS" "$input_file" >& "$TMP_OUT"; then
        echo "Failed (unexpected success)"
        ((err_failed++))
    else
        echo "Passed (correctly failed)"
        ((err_passed++))
    fi
done

# Clean up
echo
echo "Cleaning up..."
rm -f "$TMP_OUT"
make clean

# Print stats
echo
echo "================ Statistics ================"
total_normal=$((passed + failed))
total_errors=$((err_passed + err_failed))
echo "Normal tests: $passed passed, $failed failed ($total_normal total)"
echo "Error tests:  $err_passed passed, $err_failed failed ($total_errors total)"
echo "Total tests:  $((total_normal + total_errors))"
