package lab.digraph;

import java.util.Objects;

/**
 * Edge of a directed graph.
 */
public class Edge<N extends Node> {

  private final N source;

  private final N destination;

  public Edge(N source, N destination) {
    this.source = source;
    this.destination = destination;
  }

  public final N getSource() {
    return source;
  }

  public final N getDestination() {
    return destination;
  }

  @Override
  public boolean equals(Object other) {
    return (this == other)
        || (other instanceof Edge<?> that
        && Objects.equals(source, that.source)
        && Objects.equals(destination, that.destination));
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, destination);
  }
}
