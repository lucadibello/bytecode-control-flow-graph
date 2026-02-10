#!/bin/bash

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Compile the example Java file
echo "Compiling SimpleCalculator.java..."
javac --release 21 "$SCRIPT_DIR/SimpleCalculator.java"

if [ $? -ne 0 ]; then
  echo -e "\033[0;31mError: Compilation failed!\033[0m" 1>&2
  exit 1
fi

# Run the CFG generator
echo "Generating control flow graphs..."
cd "$PROJECT_ROOT"
./gradlew run --args "example/SimpleCalculator.class example/output" --quiet

# if dot is installed, convert the .dot files to .pdf
if command -v dot &> /dev/null; then
  echo "Converting DOT files to PDF..."
  for f in "$SCRIPT_DIR/output"/*.dot; do
    dot -T pdf -o "$SCRIPT_DIR/output/$(basename "${f}").pdf" "${f}"
  done
  echo -e "\033[0;32m✓ Done! Output files are in example/output/\033[0m"
else
  echo -e "\033[0;33mWarning: 'dot' command not found. Skipping PDF generation.\033[0m" 1>&2
  echo -e "Install Graphviz to convert .dot files to .pdf: https://graphviz.org/download/" 1>&2
  echo -e "\033[0;32m✓ Done! DOT and ASM files are in example/output/\033[0m"
fi
