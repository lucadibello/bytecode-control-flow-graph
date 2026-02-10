# Java Control Flow Graph (CFG) Generator

A robust Java bytecode disassembler and Control Flow Graph generator. This tool parses Java class files and generates visual representations of method logic using the ASM library and Graphviz.

## 🚀 Features

* **Bytecode Disassembly**: Transform `.class` files into human-readable bytecode.
* **Graph Generation**: Automatically identify basic blocks and control flow edges.
* **Multiple Formats**: Export CFGs to **DOT** format or **PDF**.
* **Comprehensive Support**: Handles conditionals, loops, arrays, and object allocations.

---

## 📊 Example: Factorial Method

<table>
  <thead>
    <tr>
      <th align="left" width="50%">Java Source Code</th>
      <th align="left" width="50%">Generated Control Flow Graph</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td valign="top">
<pre lang="java">
public int factorial(int n) {
  int result = 1;
  while (n > 1) {
    result *= n;
    n--;
  }
  return result;
}
</pre>
      </td>
      <td align="center" valign="top">
        <img src="assets/factorial.dot.png" alt="Factorial CFG" style="width: 100%; height: auto;"/>
      </td>
    </tr>
  </tbody>
</table>

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

Try the included example to see the tool in action:

```bash
cd example
./run-example.sh
```

This will:
1. Compile `SimpleCalculator.java`
2. Generate bytecode disassembly (`.asm.txt` files)
3. Generate control flow graphs (`.dot` files)
4. Convert to PDF (if Graphviz is installed)

The output will be in `example/output/`.

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

- **Bytecode Analysis**: Utilizes the ASM library to traverse class structures and instruction sequences.
- **Basic Block Construction**: Identifies "leaders" (targets of jumps or instructions following jumps) to partition bytecode.
- **Graph Mapping**: Maps jumps (GOTO, IF_ICMP, etc.) to edges between blocks.
- **Visualization**: Outputs the resulting data structure into the DOT language for rendering.

## Project Structure

```
.
├── example/
│   ├── SimpleCalculator.java    # Example Java program
│   ├── run-example.sh            # Script to compile and analyze the example
│   └── output/                   # Generated CFGs and bytecode (after running)
├── src/
│   ├── main/java/lab/
│   │   ├── App.java              # Main application entry point
│   │   ├── bytecode/             # Bytecode disassembler
│   │   └── cfg/                  # Control flow graph generator
│   └── test/                     # Test files
└── build.gradle                  # Gradle build configuration
```

## License

This project is open source and available for personal and educational use.
