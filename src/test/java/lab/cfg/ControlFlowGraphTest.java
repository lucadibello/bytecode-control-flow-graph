package lab.cfg;

import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public final class ControlFlowGraphTest {

  private static void assertConnected(ControlFlowGraph g,
      BasicBlock from,
      ControlFlowEdge e,
      BasicBlock to) {
    Assert.assertTrue(g.hasOutEdge(from, e));
    Assert.assertTrue(g.hasInEdge(to, e));
    Assert.assertSame(from, e.getSource());
    Assert.assertSame(to, e.getDestination());
  }

  @Test
  public void newControlFlowGraph() {
    final ControlFlowGraph g = new ControlFlowGraph();

    Assert.assertNotNull(g.getEntry());
    Assert.assertNotNull(g.getExit());
    Assert.assertNotSame(g.getEntry(), g.getExit());

    Assert.assertEquals(2, g.getNodes().size());
    Assert.assertTrue(g.getNodes().contains(g.getEntry()));
    Assert.assertTrue(g.getNodes().contains(g.getExit()));

    Assert.assertEquals(0, g.getEdges().size());
  }

  @Test
  public void addEntryEdge() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock bb = new BasicBlock(1, List.of());
    g.addNode(bb);
    final ControlFlowEdge entryEdge = g.addEntryEdge(bb);

    final Set<BasicBlock> nodes = g.getNodes();
    final Set<ControlFlowEdge> edges = g.getEdges();

    Assert.assertEquals(3, nodes.size());
    Assert.assertTrue(nodes.contains(bb));
    Assert.assertEquals(1, edges.size());
    Assert.assertTrue(edges.contains(entryEdge));

    final BasicBlock entry = g.getEntry();

    Assert.assertTrue(g.hasInEdge(bb, entryEdge));
    Assert.assertTrue(g.hasOutEdge(entry, entryEdge));
  }

  @Test(expected = IllegalStateException.class)
  public void addEntryEdgeToNonContainedBasicBlock() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock bb = new BasicBlock(1, List.of());

    g.addEntryEdge(bb);
    Assert.fail("Should have thrown");
  }

  @Test(expected = IllegalStateException.class)
  public void addTwoEntryEdges() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock bb = new BasicBlock(1, List.of());
    g.addNode(bb);
    g.addEntryEdge(bb);

    g.addEntryEdge(bb);
    Assert.fail("Should have thrown");
  }

  @Test
  public void addExitEdge() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock bb = new BasicBlock(1, List.of());
    g.addNode(bb);
    final ControlFlowEdge exitEdge = g.addExitEdge(bb);

    final Set<BasicBlock> nodes = g.getNodes();
    final Set<ControlFlowEdge> edges = g.getEdges();

    Assert.assertEquals(3, nodes.size());
    Assert.assertTrue(nodes.contains(bb));
    Assert.assertEquals(1, edges.size());
    Assert.assertTrue(edges.contains(exitEdge));

    final BasicBlock exit = g.getExit();

    Assert.assertTrue(g.hasOutEdge(bb, exitEdge));
    Assert.assertTrue(g.hasInEdge(exit, exitEdge));
  }

  @Test(expected = IllegalStateException.class)
  public void addExitEdgeFromNonContainedBasicBlock() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock bb = new BasicBlock(1, List.of());

    g.addExitEdge(bb);
    Assert.fail("Should have thrown");
  }

  @Test
  public void addFallthroughEdge() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock from = new BasicBlock(1, List.of());
    final BasicBlock to = new BasicBlock(2, List.of());
    g.addNode(from);
    g.addNode(to);
    final ControlFlowEdge e = g.addFallthroughEdge(from, to);

    final Set<BasicBlock> nodes = g.getNodes();
    final Set<ControlFlowEdge> edges = g.getEdges();

    Assert.assertEquals(4, nodes.size());
    Assert.assertTrue(nodes.contains(from));
    Assert.assertTrue(nodes.contains(to));

    Assert.assertEquals(1, edges.size());
    Assert.assertTrue(edges.contains(e));
    assertConnected(g, from, e, to);
  }

  @Test
  public void addBranchTakenEdge() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock from = new BasicBlock(1, List.of());
    final BasicBlock to = new BasicBlock(2, List.of());
    g.addNode(from);
    g.addNode(to);

    final ControlFlowEdge e = g.addBranchTakenEdge(from, to);

    Assert.assertEquals("T", e.getLabel());
    Assert.assertEquals(4, g.getNodes().size());
    Assert.assertTrue(g.getNodes().contains(from));
    Assert.assertTrue(g.getNodes().contains(to));
    Assert.assertEquals(1, g.getEdges().size());
    Assert.assertTrue(g.getEdges().contains(e));
    assertConnected(g, from, e, to);
  }

  @Test
  public void addCaseEdge() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock from = new BasicBlock(1, List.of());
    final BasicBlock to = new BasicBlock(2, List.of());
    g.addNode(from);
    g.addNode(to);

    final ControlFlowEdge e = g.addCaseEdge(from, to, 99);

    Assert.assertNotNull(e);
    Assert.assertEquals("99", e.getLabel());
    Assert.assertEquals(4, g.getNodes().size());
    Assert.assertTrue(g.getNodes().contains(from));
    Assert.assertTrue(g.getNodes().contains(to));
    Assert.assertEquals(1, g.getEdges().size());
    Assert.assertTrue(g.getEdges().contains(e));
    assertConnected(g, from, e, to);
  }

  @Test
  public void addDefaultEdge() {
    final ControlFlowGraph g = new ControlFlowGraph();
    final BasicBlock from = new BasicBlock(1, List.of());
    final BasicBlock to = new BasicBlock(2, List.of());
    g.addNode(from);
    g.addNode(to);
    final ControlFlowEdge e = g.addDefaultEdge(from, to);

    Assert.assertNotNull(e);
    Assert.assertEquals("default", e.getLabel());
    Assert.assertEquals(4, g.getNodes().size());
    Assert.assertTrue(g.getNodes().contains(from));
    Assert.assertTrue(g.getNodes().contains(to));
    Assert.assertEquals(1, g.getEdges().size());
    Assert.assertTrue(g.getEdges().contains(e));
    assertConnected(g, from, e, to);
  }
}
