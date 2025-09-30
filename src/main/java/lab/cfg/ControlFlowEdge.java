package lab.cfg;

import java.util.Objects;
import lab.digraph.Edge;

/**
 * Edge of a control-flow-graph.
 */
public final class ControlFlowEdge extends Edge<BasicBlock> {

  private final String label;

  public ControlFlowEdge(BasicBlock source, BasicBlock destination) {
    this("", source, destination);
  }

  public ControlFlowEdge(String label, BasicBlock source, BasicBlock destination) {
    super(source, destination);
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public boolean equals(Object other) {
    return (this == other)
        || (super.equals(other)
        && other instanceof ControlFlowEdge that
        && Objects.equals(label, that.label));
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), label);
  }
}
