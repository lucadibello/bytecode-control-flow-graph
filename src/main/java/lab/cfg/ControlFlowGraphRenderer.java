package lab.cfg;

public final class ControlFlowGraphRenderer {

  private static final String HEADER = """
    digraph CFG {
      label=%s;
      rankdir=TB;
      ordering=out;
      graph [loop="b",fontsize=12];
      node [shape=record,fontname=monospace];
      edge [fontname=Verdana]
    """;

  private static String renderHeader(String label) {
    return String.format(HEADER, label);
  }

  private static String escapeLabel(String s) {
    if (s == null) return "";
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '"':
          out.append("\\\"");
          break;
        case '\\':
          out.append("\\\\");
          break;
        case '\n':
          out.append("\\n");
          break;
        case '\r':
          out.append("\\r");
          break;
        case '\t':
          out.append("\\t");
          break;
        default:
          if (c < 32) {
            out.append(String.format("\\%03o", (int) c));
          } else {
            out.append(c);
          }
      }
    }
    return out.toString();
  }

  private static String renderBasicBlock(BasicBlock bb) {
    if (bb.getInstructionsCount() == 0) return "";

    StringBuilder builder = new StringBuilder("\s\s");
    builder.append(bb.id());
    builder.append("\s");
    builder.append("[shape=record, label=\"{<id> ");
    builder.append(bb.id());
    builder.append("}|{");

    // Handle single instruction case
    if (bb.getInstructionsCount() == 1) {
      builder.append("<top_bottom> ");
      builder.append(bb.getInstruction(0).strip());
    } else {
      // Handle block with multiple instructions
      builder.append("<top> ");
      for (int i = 0; i < bb.getInstructionsCount(); i++) {
        if (i == bb.getInstructionsCount() - 1) builder.append("<bottom> ");
        String repr = bb.getInstruction(i);
        builder.append(repr.strip());
        if (i < bb.getInstructionsCount() - 1) {
          builder.append("|");
        }
      }
    }

    builder.append("}\"];\n");
    return builder.toString();
  }

  private static String renderEntryNode(ControlFlowGraph cfg) {
    return String.format("\s\s%d [shape=circle, label=S];\n", cfg.getEntry().id());
  }

  private static String renderExitNode(ControlFlowGraph cfg) {
    return String.format("\s\s%d [shape=circle, label=X];\n", cfg.getExit().id());
  }

  private static String renderEdge(
    ControlFlowEdge edge,
    boolean use_source_tag,
    boolean use_dest_tag
  ) {
    StringBuilder builder = new StringBuilder("\s\s");
    BasicBlock bb_s = edge.getSource(),
      bb_e = edge.getDestination();

    String source_tag = use_source_tag
      ? (bb_s.getInstructionsCount() == 1 ? ":top_bottom" : ":bottom")
      : "";
    String dest_tag = use_dest_tag
      ? (bb_e.getInstructionsCount() == 1 ? ":top_bottom" : ":top")
      : "";

    // handle different types of control flow edge! If-cases
    builder.append(bb_s.id());
    builder.append(source_tag);
    builder.append("->");
    builder.append(bb_e.id());
    builder.append(dest_tag);
    String rawLabel = edge.getLabel();
    if (rawLabel == null || rawLabel.isEmpty()) {
      builder.append(" [label=\"\"];");
    } else {
      builder.append(" [label=\"").append(escapeLabel(rawLabel)).append("\"];");
    }
    builder.append("\n");

    // output rendered edge
    return builder.toString();
  }

  public static String renderControlFlowGraph(String label, ControlFlowGraph cfg) {
    StringBuilder builder = new StringBuilder();

    // print each basic block in the cfg
    builder.append(ControlFlowGraphRenderer.renderHeader(label));
    builder.append(ControlFlowGraphRenderer.renderEntryNode(cfg));
    builder.append(ControlFlowGraphRenderer.renderExitNode(cfg));

    for (BasicBlock bb : cfg.getNodes()) {
      builder.append(ControlFlowGraphRenderer.renderBasicBlock(bb));
    }

    // step 2: define edges
    // 1) from entry node
    for (ControlFlowEdge edge : cfg.getEdges()) {
      // handle different types of control flow edge! If-cases
      boolean use_source_tag = !edge.getSource().equals(cfg.getEntry());
      boolean use_destination_tag = !edge.getDestination().equals(cfg.getExit());
      builder.append(
        ControlFlowGraphRenderer.renderEdge(edge, use_source_tag, use_destination_tag)
      );
    }
    // close graph definition
    builder.append("}\n");
    System.out.println(builder.toString());
    // return control-flow graph
    return builder.toString();
  }
}
