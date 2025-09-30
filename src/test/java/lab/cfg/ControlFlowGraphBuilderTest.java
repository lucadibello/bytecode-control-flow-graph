package lab.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import lab.digraph.Edge;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * This tests the ControlFlowGraphBuilder by running it on the compiled ExampleClass. It loads the
 * ExampleClass.class. The different test methods test the CFG on different inputs (different
 * methods in class ExampleClass). The test is VERY WEAK. That is, there are many bugs it will not
 * discover.
 */
@RunWith(Parameterized.class)
public final class ControlFlowGraphBuilderTest {

  @Parameterized.Parameters(name = "CFG for {0}{1}")
  public static Collection<Object[]> data() {
    return List.of(
        new Object[]{"emptyMethod", "()V", 3, 2},
        new Object[]{"ifMethod", "(I)I", 5, 5},
        new Object[]{"ifElseMethod", "(I)I", 6, 6},
        new Object[]{"switchMethod", "(I)I", 8, 10},
        new Object[]{"switchMethod2", "(I)I", 8, 10},
        new Object[]{"forMethod", "(I)I", 6, 6},
        new Object[]{"whileMethod", "(I)I", 6, 6},
        new Object[]{"doWhileMethod", "(I)I", 5, 5},
        new Object[]{"forWithBreakMethod", "(I)I", 8, 9},
        new Object[]{"forWithContinueMethod", "(I)I", 9, 10},
        new Object[]{"whileTrueMethod", "(I)I", 3, 2},
        new Object[]{"doWhileTrue", "(I)I", 3, 2},
        new Object[]{"forEver", "(I)I", 4, 3},
        new Object[]{"nestedFor", "(I)I", 9, 10},
        new Object[]{"staticCallMethod", "(I)I", 3, 2},
        new Object[]{"condMethod", "(II)I", 6, 6},
        new Object[]{"shortCircuitMethod", "(III)I", 6, 7},
        new Object[]{"nonShortCircuitMethod", "(III)I", 11, 13}
    );
  }

  private final MethodNode methodNode;
  private final ControlFlowGraph g;
  private final int expectedNumOfBasicBlocks;
  private final int expectedNumOfEdges;

  public ControlFlowGraphBuilderTest(String methodName,
      String methodDesc,
      int expectedNumOfBasicBlocks,
      int expectedNumOfEdges) throws IOException, URISyntaxException {
    this.methodNode = getExampleClassMethod(methodName, methodDesc);
    this.g = ControlFlowGraphBuilder.createControlFlowGraph(this.methodNode);
    this.expectedNumOfBasicBlocks = expectedNumOfBasicBlocks;
    this.expectedNumOfEdges = expectedNumOfEdges;
  }

  @Test
  public void testControlFlowGraph() {
    Assert.assertEquals(String.format(
            "CFG should contain %1$d basic blocks: entry, exit, and %2$d with code",
            expectedNumOfBasicBlocks,
            Math.max(0, expectedNumOfBasicBlocks - 2)),
        expectedNumOfBasicBlocks,
        g.getNodes().size());

    Assert.assertEquals(String.format("CFG should contain %1$d edges", expectedNumOfEdges),
        expectedNumOfEdges,
        g.getEdges().size());

    final BasicBlock entry = g.getEntry();

    final Set<BasicBlock> visited = new HashSet<>();
    visited.add(entry);
    visited.add(g.getExit());

    final Deque<BasicBlock> queue = new ArrayDeque<>();
    // Find the node the entry node is connected to
    g.getEdges().stream()
        .filter(e -> entry.equals(e.getSource()))
        .findFirst()
        .map(Edge::getDestination)
        .ifPresent(queue::add);

    long totalNonPseudoInstructions = 0;

    while (!queue.isEmpty()) {
      final BasicBlock visiting = queue.pop();
      if (!visited.add(visiting)) {
        continue;
      }

      // Enqueue connected blocks
      g.getEdges().stream()
          .filter(e -> visiting.equals(e.getSource()) && !visited.contains(e.getDestination()))
          .map(ControlFlowEdge::getDestination)
          .forEach(queue::add);

      // Count instructions
      totalNonPseudoInstructions += visiting.getInstructionsCount();
    }

    // Pseudo-instructions (those with opcode < 0) should not be included
    final InsnList asmInstructions = methodNode.instructions;
    final long expectedNonPseudoInstructionsCount = StreamSupport.stream(
            Spliterators.spliterator(asmInstructions.iterator(),
                asmInstructions.size(),
                Spliterator.SIZED | Spliterator.ORDERED),
            false)
        .filter(i -> i.getOpcode() >= 0)
        .count();

    Assert.assertEquals(
        "Basic blocks instruction count sum differs from number of instructions in method node",
        expectedNonPseudoInstructionsCount,
        totalNonPseudoInstructions);
  }

  private static MethodNode getExampleClassMethod(String name, String desc)
      throws IOException, URISyntaxException {
    // Use ASM to read a class file (from test resources) and create a ClassNode
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    final URI exampleClassUri = Objects.requireNonNull(cl.getResource("ExampleClass.class"))
        .toURI();

    final ClassNode classNode;
    try (InputStream iStream = exampleClassUri.toURL().openStream()) {
      final ClassReader cr = new ClassReader(iStream);
      // create an empty ClassNode (in-memory representation of a class)
      classNode = new ClassNode();
      // have the ClassReader read the class file and populate the ClassNode with the corresponding information
      cr.accept(classNode, 0);
    }

    for (MethodNode methodNode : classNode.methods) {
      if (methodNode.name.equals(name) && methodNode.desc.equals(desc)) {
        return methodNode;
      }
    }
    throw new IllegalArgumentException("Error in test harness: method " + name + " not found!");
  }
}
