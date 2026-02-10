# Java Control Flow Graph (CFG) Generator

A robust Java bytecode disassembler and Control Flow Graph generator. This tool parses Java class files and generates visual representations of method logic using the ASM library and Graphviz.

## 🚀 Features

* **Bytecode Disassembly**: Transform `.class` files into human-readable bytecode.
* **Graph Generation**: Automatically identify basic blocks and control flow edges.
* **Multiple Formats**: Export CFGs to **DOT** format or **PDF**.
* **Comprehensive Support**: Handles conditionals, loops, arrays, and object allocations.

---

## 📊 Example: Factorial Method

| Java Source Code | Generated CFG (DOT) |
| :--- | :--- |
| ```java\npublic int factorial(int n) {\n  int result = 1;\n  while (n > 1) {\n    result *= n;\n    n--;\n  }\n  return result;\n}\n``` | ```dot\ndigraph CFG {\n  label=factorial;\n  node [shape=record];\n  -1 [shape=circle, label=S];\n  -2 [shape=circle, label=X];\n  0 [label="ICONST_1 | ISTORE 2"];\n  1 [label="ILOAD 1 | ICONST_1 | IF_ICMPLE 20"];\n  2 [label="ILOAD 2 | IMUL | ISTORE 2 | IINC 1 -1 | GOTO 4"];\n  3 [label="ILOAD 2 | IRETURN"];\n  -1 -> 0; 0 -> 1; 1 -> 2; 2 -> 1; 1 -> 3 [label="T"]; 3 -> -2;\n}\n``` |

**Key Components:**
* **S / X**: Start and Exit nodes.
* **Basic Blocks**: Nodes containing sequential bytecode instructions.
* **Edges**: Paths of execution (labeled "T" for conditional branches).

---

## 🛠 Usage

### Prerequisites
* **Java 21** or higher
* **Graphviz** (optional, required for PDF rendering)
* **Gradle** (included wrapper)

### Quick Start
To analyze the provided example and generate a combined PDF:
```bash
./run.sh

### Analyzing Custom Class Files

#### Option 1: Using Gradle (Recommended)

```bash
./gradlew run --args "/path/to/YourClass.class /path/to/output_dir"
```

#### Option 2: Using the JAR

First, build the project:

```bash
./gradlew build
```

Then run the JAR with your class file:

```bash
java -cp build/libs/lab-03-lucadibello.jar:lib/asm-9.7.jar:lib/asm-tree-9.7.jar:lib/asm-util-9.7.jar \
     lab.App /path/to/YourClass.class /path/to/output
```

## Output structure

The tool generates two files per method in your specified output directory:

| File Extension  | Description |
|---|---|
| .asm.txt  | Disassembled bytecode with labels and stack maps. |
| .dot  | Graphviz-compatible description of the CFG. |

## How it works

- Bytecode Analysis: Utilizes the ASM library to traverse class structures and instruction sequences.
- Basic Block Construction: Identifies "leaders" (targets of jumps or instructions following jumps) to partition bytecode.
- Graph Mapping: Maps jumps (GOTO, IF_ICMP, etc.) to edges between blocks.
- Visualization: Outputs the resulting data structure into the DOT language for rendering.
