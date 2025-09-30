package lab.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class that allows to build {@link BasicBlock} instances.
 */
public final class BasicBlockBuilder {

  private final int id;
  private final List<String> instructions;

  public BasicBlockBuilder(int id) {
    this.id = id;
    this.instructions = new ArrayList<>();
  }

  public void appendInstruction(String instruction) {
    instructions.add(instruction);
  }

  public BasicBlock build() {
    return new BasicBlock(id, Collections.unmodifiableList(instructions));
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object other) {
    return (this == other)
        || (other instanceof BasicBlockBuilder that
        && id == that.id
        && instructions == that.instructions);
  }
}
