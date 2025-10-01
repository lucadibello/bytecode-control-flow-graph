package lab.digraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;

/**
 * Directed graph.
 *
 * @param <N> Type of nodes.
 * @param <E> Type of edges (that connect nodes of type &lt;N&gt;
 */
public class DiGraph<N extends Node, E extends Edge<N>> {

  private final Set<N> nodes;
  private final Set<E> edges;
  private final Map<N, List<E>> inEdges;
  private final Map<N, List<E>> outEdges;

  public DiGraph() {
    this.nodes = new TreeSet<>(Comparator.comparingInt(Object::hashCode));
    this.edges = new TreeSet<>(Comparator.comparingInt(Object::hashCode));
    this.inEdges = new HashMap<>();
    this.outEdges = new HashMap<>();
  }

  /**
   * Add a node to this graph.
   */
  public void addNode(N node) {
    if (!nodes.add(node)) {
      throw new IllegalArgumentException("Graph already contains the given node!");
    }
    inEdges.put(node, new ArrayList<>());
    outEdges.put(node, new ArrayList<>());
  }

  /**
   * Connect a node to another by creating an edge between them using the given edge supplier.
   *
   * @param source       Source node.
   * @param destination  Destination node.
   * @param edgeSupplier A method that produces an edge for two given nodes.
   * @return The edge that was created between the two nodes.
   * @throws IllegalStateException If the connection between the two nodes is invalid.
   */
  public E connect(N source, N destination, BiFunction<N, N, E> edgeSupplier) {
    if (!nodes.contains(source)) {
      throw new IllegalStateException("Graph does not contain the 'source' node");
    }
    if (!nodes.contains(destination)) {
      throw new IllegalStateException("Graph does not contain the 'destination' node");
    }
    final E edge = edgeSupplier.apply(source, destination);
    if (hasOutEdge(source, edge)) {
      throw new IllegalStateException("'Source node' already has this edge as an 'out edge'");
    }
    if (hasInEdge(destination, edge)) {
      throw new IllegalStateException("'Destination node' already has this edge as an 'in edge'");
    }
    if (edges.contains(edge)) {
      throw new IllegalStateException("Graph already contain the given edge");
    }
    edges.add(edge);
    outEdges.get(source).add(edge);
    inEdges.get(destination).add(edge);

    return edge;
  }

  /**
   * Get an immutable set of all edges in this graph.
   */
  public Set<E> getEdges() {
    return Collections.unmodifiableSet(edges);
  }

  /**
   * Get an immutable set of all nodes in this graph.
   */
  public Set<N> getNodes() {
    return Collections.unmodifiableSet(nodes);
  }

  /**
   * Check whether a node is contained in this graph.
   */
  public boolean hasNode(N node) {
    return nodes.contains(node);
  }

  /**
   * Check whether a given input edge already exists for a given node.
   */
  public boolean hasInEdge(N node, E edge) {
    return (
      inEdges.containsKey(node) &&
      node.equals(edge.getDestination()) &&
      inEdges.get(node).contains(edge)
    );
  }

  /**
   * Check whether a given output edge already exists for a given node.
   */
  public boolean hasOutEdge(N node, E edge) {
    return (
      outEdges.containsKey(node) &&
      node.equals(edge.getSource()) &&
      outEdges.get(node).contains(edge)
    );
  }

  /**
   * Check whether a given node has any output edge.
   */
  public boolean hasAnyOutEdge(N node) {
    return outEdges.containsKey(node) && !outEdges.get(node).isEmpty();
  }
}
