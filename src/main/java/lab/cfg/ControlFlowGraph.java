package lab.cfg;

import java.util.List;
import lab.digraph.DiGraph;

public final class ControlFlowGraph extends DiGraph<BasicBlock, ControlFlowEdge> {

  private static final int ENTRY_ID = -1;
  private static final int EXIT_ID = -2;

  private final BasicBlock entry;
  private final BasicBlock exit;

  public ControlFlowGraph() {
    super();
    entry = new BasicBlock(ENTRY_ID, List.of());
    exit = new BasicBlock(EXIT_ID, List.of());
    addNode(entry);
    addNode(exit);
  }

  private void checkContains(BasicBlock block, String name) {
    if (!hasNode(block)) {
      throw new IllegalStateException("Control flow graph does not contain the given "
          + name
          + " block.");
    }
  }

  /**
   * Connect the entry node to the given basic block.
   *
   * @return The edge that was created.
   */
  public ControlFlowEdge addEntryEdge(BasicBlock firstBlock) {
    if (hasAnyOutEdge(entry)) {
      throw new IllegalStateException(
          "Control flow graph already has an entry edge. It can only have one.");
    }
    checkContains(firstBlock, "firstBlock");
    return connect(entry, firstBlock, ControlFlowEdge::new);
  }

  /**
   * Connect the exit node to the given basic block.
   *
   * @return The edge that was created.
   */
  public ControlFlowEdge addExitEdge(BasicBlock returnBlock) {
    checkContains(returnBlock, "returnBlock");
    return connect(returnBlock, exit, ControlFlowEdge::new);
  }

  /**
   * Connect the two given blocks through a fallthrough edge.
   *
   * @return The edge that was created.
   */
  public ControlFlowEdge addFallthroughEdge(BasicBlock fromBlock, BasicBlock toBlock) {
    checkContains(fromBlock, "fromBlock");
    checkContains(toBlock, "toBlock");
    return connect(fromBlock, toBlock, ControlFlowEdge::new);
  }

  /**
   * Connect the two given blocks through a "branch taken" ("<code>true</code>") edge.
   *
   * @return The edge that was created.
   */
  public ControlFlowEdge addBranchTakenEdge(BasicBlock fromBlock, BasicBlock toBlock) {
    checkContains(fromBlock, "fromBlock");
    checkContains(toBlock, "toBlock");
    return connect(fromBlock, toBlock, (s, d) -> new ControlFlowEdge("T", s, d));
  }

  /**
   * Connect the two given blocks through a case edge for the given key (in a switch construct).
   *
   * @return The edge that was created.
   */
  public ControlFlowEdge addCaseEdge(BasicBlock fromBlock, BasicBlock toBlock, int key) {
    checkContains(fromBlock, "fromBlock");
    checkContains(toBlock, "toBlock");
    return connect(fromBlock, toBlock, (s, d) -> new ControlFlowEdge(String.valueOf(key), s, d));
  }

  /**
   * Connect the two given blocks through a default case edge (in a switch construct).
   *
   * @return The edge that was created.
   */
  public ControlFlowEdge addDefaultEdge(BasicBlock fromBlock, BasicBlock toBlock) {
    checkContains(fromBlock, "fromBlock");
    checkContains(toBlock, "toBlock");
    return connect(fromBlock, toBlock, (s, d) -> new ControlFlowEdge("default", s, d));
  }

  /**
   * Get the entry node E.
   */
  public BasicBlock getEntry() {
    return entry;
  }

  /**
   * Get the exit node X.
   */
  public BasicBlock getExit() {
    return exit;
  }
}
