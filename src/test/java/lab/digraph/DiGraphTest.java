package lab.digraph;

import org.junit.Assert;
import org.junit.Test;

public class DiGraphTest {

  @Test
  public void newDiGraph() {
    final DiGraph<TestNode, Edge<TestNode>> g = new DiGraph<>();
    Assert.assertEquals(0, g.getNodes().size());
    Assert.assertEquals(0, g.getEdges().size());
  }

  @Test
  public void addNode() {
    final DiGraph<TestNode, Edge<TestNode>> g = new DiGraph<>();
    final TestNode n = new TestNode(1);
    g.addNode(n);
    Assert.assertEquals(1, g.getNodes().size());
    Assert.assertTrue(g.getNodes().contains(n));
    Assert.assertEquals(0, g.getEdges().size());
  }

  @Test
  public void connect() {
    final DiGraph<TestNode, Edge<TestNode>> g = new DiGraph<>();

    final TestNode n1 = new TestNode(1);
    final TestNode n2 = new TestNode(2);
    g.addNode(n1);
    g.addNode(n2);

    Assert.assertEquals(2, g.getNodes().size());
    Assert.assertEquals(0, g.getEdges().size());

    g.connect(n1, n2, Edge::new);

    final Edge<TestNode> expectedEdge = new Edge<>(n1, n2);

    Assert.assertEquals(2, g.getNodes().size());
    Assert.assertEquals(1, g.getEdges().size());
    Assert.assertTrue(g.getEdges().contains(expectedEdge));

    Assert.assertTrue(g.hasAnyOutEdge(n1));
    Assert.assertFalse(g.hasAnyOutEdge(n2));

    Assert.assertTrue(g.hasOutEdge(n1, expectedEdge));
    Assert.assertFalse(g.hasOutEdge(n2, expectedEdge));

    Assert.assertFalse(g.hasInEdge(n1, expectedEdge));
    Assert.assertTrue(g.hasInEdge(n2, expectedEdge));
  }

  @Test
  public void connectBiDirection() {
    final DiGraph<TestNode, Edge<TestNode>> g = new DiGraph<>();

    final TestNode n1 = new TestNode(1);
    final TestNode n2 = new TestNode(2);
    g.addNode(n1);
    g.addNode(n2);

    Assert.assertEquals(2, g.getNodes().size());
    Assert.assertEquals(0, g.getEdges().size());

    g.connect(n1, n2, Edge::new);
    g.connect(n2, n1, Edge::new);

    final Edge<TestNode> e1 = new Edge<>(n1, n2);
    final Edge<TestNode> e2 = new Edge<>(n2, n1);

    Assert.assertEquals(2, g.getNodes().size());
    Assert.assertEquals(2, g.getEdges().size());
    Assert.assertTrue(g.getEdges().contains(e1));
    Assert.assertTrue(g.getEdges().contains(e2));

    Assert.assertTrue(g.hasAnyOutEdge(n1));
    Assert.assertTrue(g.hasAnyOutEdge(n2));

    Assert.assertTrue(g.hasOutEdge(n1, e1));
    Assert.assertFalse(g.hasOutEdge(n1, e2));

    Assert.assertFalse(g.hasOutEdge(n2, e1));
    Assert.assertTrue(g.hasOutEdge(n2, e2));

    Assert.assertFalse(g.hasInEdge(n1, e1));
    Assert.assertTrue(g.hasInEdge(n1, e2));

    Assert.assertTrue(g.hasInEdge(n2, e1));
    Assert.assertFalse(g.hasInEdge(n2, e2));
  }

  @Test(expected = IllegalStateException.class)
  public void connectSourceNotContained() {
    final DiGraph<TestNode, Edge<TestNode>> g = new DiGraph<>();

    final TestNode n1 = new TestNode(1);
    final TestNode n2 = new TestNode(2);
    // MISSING: g.addNode(n1);
    g.addNode(n2);
    g.connect(n1, n2, Edge::new);

    Assert.fail("Should have thrown");
  }

  @Test(expected = IllegalStateException.class)
  public void connectDestinationNotContained() {
    final DiGraph<TestNode, Edge<TestNode>> g = new DiGraph<>();

    final TestNode n1 = new TestNode(1);
    final TestNode n2 = new TestNode(2);
    g.addNode(n1);
    // MISSING: g.addNode(n2);
    g.connect(n1, n2, Edge::new);

    Assert.fail("Should have thrown");
  }

  @Test(expected = IllegalStateException.class)
  public void connectSameEdgeTwice() {
    final DiGraph<TestNode, Edge<TestNode>> g = new DiGraph<>();

    final TestNode n1 = new TestNode(1);
    final TestNode n2 = new TestNode(2);
    g.addNode(n1);
    g.addNode(n2);
    g.connect(n1, n2, Edge::new);
    g.connect(n1, n2, Edge::new);

    Assert.fail("Should have thrown");
  }

  @Test
  public void connectSelfLoop() {
    final DiGraph<TestNode, Edge<TestNode>> g = new DiGraph<>();

    final TestNode n = new TestNode(1);
    g.addNode(n);

    Assert.assertEquals(1, g.getNodes().size());
    Assert.assertEquals(0, g.getEdges().size());

    g.connect(n, n, Edge::new);

    final Edge<TestNode> expectedEdge = new Edge<>(n, n);
    Assert.assertEquals(1, g.getNodes().size());
    Assert.assertTrue(g.getNodes().contains(n));
    Assert.assertEquals(1, g.getEdges().size());
    Assert.assertTrue(g.getEdges().contains(expectedEdge));
    Assert.assertTrue(g.hasOutEdge(n, expectedEdge));
    Assert.assertTrue(g.hasInEdge(n, expectedEdge));
  }
}
