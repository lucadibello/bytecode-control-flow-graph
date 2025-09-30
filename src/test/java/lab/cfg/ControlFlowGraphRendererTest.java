package lab.cfg;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests ControlFlowGraphRenderer by asking it to render simple, artificially generated CFGs. The
 * test is VERY WEAK. That is, there are many bugs it will not discover.
 */
public final class ControlFlowGraphRendererTest {

  @Test
  public void renderEmptyControlFlowGraph() {
    final ControlFlowGraph cfg = new ControlFlowGraph();
    final String result = ControlFlowGraphRenderer.renderControlFlowGraph("THELABEL", cfg);

    Assert.assertTrue("must include the given label", result.contains("THELABEL"));
    Assert.assertTrue(result.startsWith("digraph "));
    Assert.assertTrue(result.contains("{"));
    Assert.assertTrue(result.contains("}"));
    Assert.assertTrue("must set properties of entry/exit nodes", result.contains("["));
    Assert.assertTrue("must set properties of entry/exit nodes", result.contains("]"));
    Assert.assertTrue("must set label for graph, and for nodes", result.contains("label"));
    Assert.assertTrue("must contain entry node's ID: -1", result.contains("-1"));
    Assert.assertTrue("must contain exit node's ID: -2", result.contains("-2"));
    Assert.assertTrue("must set shape for entry/exit nodes", result.contains("shape"));
    Assert.assertTrue("must use circle for entry/exit nodes", result.contains("circle"));

  }

  @Test
  public void renderSingleBlockControlFlowGraph() {
    final ControlFlowGraph cfg = new ControlFlowGraph();
    final BasicBlock bb = new BasicBlock(99, List.of("THEINSTRUCTION"));
    cfg.addNode(bb);
    cfg.addEntryEdge(bb);
    cfg.addExitEdge(bb);
    final String result = ControlFlowGraphRenderer.renderControlFlowGraph("THELABEL", cfg);

    Assert.assertTrue("must include the given label", result.contains("THELABEL"));
    Assert.assertTrue(result.startsWith("digraph "));
    Assert.assertTrue(result.contains("{"));
    Assert.assertTrue(result.contains("}"));
    Assert.assertTrue("must set properties of entry/exit nodes", result.contains("["));
    Assert.assertTrue("must set properties of entry/exit nodes", result.contains("]"));
    Assert.assertTrue("must set label for graph, and for nodes", result.contains("label"));
    Assert.assertTrue("must contain entry node's ID: -1", result.contains("-1"));
    Assert.assertTrue("must contain exit node's ID: -2", result.contains("-2"));
    Assert.assertTrue("must set shape for entry/exit nodes", result.contains("shape"));
    Assert.assertTrue("must use circle for entry/exit nodes", result.contains("circle"));

    Assert.assertTrue("must contain an edge (->)", result.contains("->"));
    Assert.assertTrue("must include the basic block's ID (99)", result.contains("99"));
    Assert.assertTrue("must contain THEINSTRUCTION in the label of the basic block",
        result.contains("THEINSTRUCTION"));
    Assert.assertTrue("must use a record shape for the basic block",
        result.contains("record"));
  }


  @Test
  public void renderTwoBlockControlFlowGraph() {
    final ControlFlowGraph cfg = new ControlFlowGraph();
    final BasicBlock bb1 = new BasicBlock(11, List.of("I11.1", "I11.2"));
    cfg.addNode(bb1);

    final BasicBlock bb2 = new BasicBlock(22, List.of("I22.1", "I22.2"));
    cfg.addNode(bb2);
    cfg.addEntryEdge(bb1);
    cfg.addFallthroughEdge(bb1, bb2);
    cfg.addExitEdge(bb2);

    final String result = ControlFlowGraphRenderer.renderControlFlowGraph("THELABEL", cfg);

    Assert.assertTrue("must include the given label", result.contains("THELABEL"));
    Assert.assertTrue(result.startsWith("digraph "));
    Assert.assertTrue(result.contains("{"));
    Assert.assertTrue(result.contains("}"));
    Assert.assertTrue("must set properties of entry/exit nodes", result.contains("["));
    Assert.assertTrue("must set properties of entry/exit nodes", result.contains("]"));
    Assert.assertTrue("must set label for graph, and for nodes", result.contains("label"));
    Assert.assertTrue("must contain entry node's ID: -1", result.contains("-1"));
    Assert.assertTrue("must contain exit node's ID: -2", result.contains("-2"));
    Assert.assertTrue("must set shape for entry/exit nodes", result.contains("shape"));
    Assert.assertTrue("must use circle for entry/exit nodes", result.contains("circle"));

    Assert.assertTrue("must contain an edge (->)", result.contains("->"));
    Assert.assertTrue("must include the first basic block's ID (11)", result.contains("11"));
    Assert.assertTrue("must include the second basic block's ID (22)", result.contains("22"));
    Assert.assertTrue(
        "must contain the sequence of instructions I11.1|I11.2 in the label of the first basic block",
        result.contains("I11.1|I11.2"));
    Assert.assertTrue(
        "must contain the sequence of instructions I22.1|I22.2 in the label of the first basic block",
        result.contains("I22.1|I22.2"));
    Assert.assertTrue("must use a record shape for the basic block",
        result.contains("record"));
  }
}
