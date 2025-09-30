package lab.digraph;

import org.junit.Assert;
import org.junit.Test;

public class EdgeTest {

  @Test
  public void nullEdge() {
    final Edge<TestNode> e = new Edge<>(null, null);
    Assert.assertNull(e.getSource());
    Assert.assertNull(e.getDestination());
  }

  @Test
  public void normalEdge() {
    final TestNode n1 = new TestNode(1);
    final TestNode n2 = new TestNode(2);
    final Edge<TestNode> e = new Edge<>(n1, n2);
    Assert.assertEquals(n1, e.getSource());
    Assert.assertEquals(n2, e.getDestination());
  }

  @Test
  public void cycleEdge() {
    final TestNode n = new TestNode(1);
    final Edge<TestNode> e = new Edge<>(n, n);
    Assert.assertEquals(n, e.getSource());
    Assert.assertEquals(e.getSource(), e.getDestination());
  }
}
