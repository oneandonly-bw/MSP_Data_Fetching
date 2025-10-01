#!/bin/bash

# Go to your Java source folder
cd /c/DCM/CODE/MSP_ORCHESTRATOR/SRC/MAIN/JAVA

# Output file
OUTPUT_FILE="class_dependencies.txt"
> $OUTPUT_FILE  # clear previous content

# Loop over all Java files under org
find org -name "*.java" | while read file; do
    echo "Class: $file" >> $OUTPUT_FILE
    echo "Imports:" >> $OUTPUT_FILE
    grep "^import" "$file" >> $OUTPUT_FILE
    echo "Method Calls:" >> $OUTPUT_FILE
    grep "\." "$file" | grep "(" >> $OUTPUT_FILE
    echo >> $OUTPUT_FILE
done

echo "Done! Check $OUTPUT_FILE"
