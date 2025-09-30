package lab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lab.bytecode.Disassembler;
import lab.cfg.ControlFlowGraph;
import lab.cfg.ControlFlowGraphBuilder;
import lab.cfg.ControlFlowGraphRenderer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;


public final class App {

  /**
   * Invoke like this...
   * <code>
   * java App test-input/java10/ExampleClass.class test-output
   * </code>
   *
   * <p>...to produce one disassembly and one CFG for each method in ExampleClass.
   * Afterward, go to the test-output folder, and call...
   * <code>
   * dot -Tpdf -oall.pdf *.dot
   * </code>
   * <p>...to produce a file all.pdf containing one page for each CFG.
   */
  public static void main(String[] args) throws IOException {
    final Path classFile = Path.of(args[0]);
    final Path outputDirectory = Path.of(args[1]);
    final App app = new App(classFile, outputDirectory);
    app.execute();
  }

  private final Path classFile;

  private final Path outputDirectory;

  public App(Path classFile, Path outputDirectory) {
    this.classFile = classFile;
    this.outputDirectory = outputDirectory;
  }

  /**
   * Save the given contents into a file with the given fileName in the outputDirectory.
   */
  private void save(String fileName, String contents) throws IOException {
    if (!Files.exists(outputDirectory)) {
      Files.createDirectories(outputDirectory);
    }
    final Path file = outputDirectory.resolve(fileName);
    Files.writeString(file, contents);
  }

  public void execute() throws IOException {
    final ClassReader cr = new ClassReader(Files.newInputStream(classFile));
    // create an empty ClassNode (in-memory representation of a class)
    final ClassNode cn = new ClassNode();
    // have the ClassReader read the class file and populate the ClassNode with the corresponding information
    cr.accept(cn, 0);
    // disassemble and perform control-flow analysis
    processClass(cn);
  }

  private void processClass(ClassNode cn) throws IOException {
    System.out.println("Class: " + cn.name);
    // get the list of all methods in that class
    final List<MethodNode> methods = cn.methods;
    for (final MethodNode method : methods) {
      processMethod(method);
    }
  }

  private void processMethod(MethodNode method) throws IOException {
    System.out.println("  Method: " + method.name + method.desc);
    save(method.name + ".asm.txt", Disassembler.disassembleMethod(method));
    final ControlFlowGraph cfg = ControlFlowGraphBuilder.createControlFlowGraph(method);
    final String content = ControlFlowGraphRenderer.renderControlFlowGraph(method.name, cfg);
    save(method.name + ".dot", content);
  }
}
