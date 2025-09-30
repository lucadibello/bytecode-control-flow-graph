package lab.cfg;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public final class BasicBlockTest {

  @Test
  public void newBasicBlock() {
    final BasicBlock bb = new BasicBlock(1, List.of());
    Assert.assertEquals(1, bb.id());
    Assert.assertFalse(bb.instructions().iterator().hasNext());
  }

  @Test
  public void appendInstruction() {
    final BasicBlock bb = new BasicBlock(1, List.of("i1", "i2"));

    final Iterator<String> itr = bb.instructions().iterator();
    Assert.assertEquals("i1", itr.next());
    Assert.assertEquals("i2", itr.next());
  }

  @Test(expected = IllegalArgumentException.class)
  public void instructionsMustNotBeMutable() {
    new BasicBlock(1, new ArrayList<>());
    Assert.fail("Should have failed");
  }
}
