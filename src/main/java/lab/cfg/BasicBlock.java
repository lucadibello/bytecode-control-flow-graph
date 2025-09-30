package lab.cfg;

import java.util.List;
import lab.digraph.Node;

/**
 * A basic block is a node of a {@link ControlFlowGraph}.
 *
 * <p>This class and its fields are <b>immutable</b>.
 *
 * <p>Use the {@link BasicBlockBuilder} class to create new instances
 * of this class.
 *
 * @param id           Of the Basic Block
 * @param instructions The instructions contained in this basic block.
 * @see lab.cfg.BasicBlockBuilder
 */
public record BasicBlock(
    int id,
    List<String> instructions
) implements Node {

  /**
   * @throws IllegalArgumentException If the given instructions list is mutable.
   */
  public BasicBlock {
    try {
      // Editing the instructions list should not be allowed, otherwise
      // the integrity of the di-graph may not be guaranteed.
      instructions.add(null);
      throw new IllegalArgumentException(
          "The instructions list must be immutable (forgot to use BasicBlockBuilder?)");
    } catch (UnsupportedOperationException expected) {
      // The list seems to be immutable.
    }
  }

  /**
   * Get the number of instructions contained in this basic block.
   */
  public int getInstructionsCount() {
    return instructions.size();
  }

  /**
   * Get the instruction corresponding to the given index.
   */
  public String getInstruction(int i) {
    return instructions.get(i);
  }
}
