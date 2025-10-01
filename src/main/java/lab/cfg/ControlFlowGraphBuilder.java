package lab.cfg;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lab.bytecode.Disassembler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

public final class ControlFlowGraphBuilder {

  /**
   * Build the control flow graph for a given method.
   */
  public static ControlFlowGraph createControlFlowGraph(MethodNode method) {
    ControlFlowGraph cfg = new ControlFlowGraph();
    InsnList insns = method.instructions;
    int n = insns.size();

    // Set of leaders (first instruction of a basic block)
    Set<Integer> leaders = new HashSet<>();
    leaders.add(0); // first instruction is always a leader in order to capture method entry (link to entry node)

    // NOTE: Step 1 - identify block leaders
    // Collect jump/switch letters
    for (int i = 0; i < n; i++) {
      AbstractInsnNode insn = insns.get(i);
      int opcode = insn.getOpcode();
      if (opcode == -1) continue;

      // handle jump instructions
      if (insn instanceof JumpInsnNode jIns) {
        // set jump target as leader
        int tgt = insns.indexOf(jIns.label);
        leaders.add(tgt);
        // if conditional jump, set also fall-through instruction as leader
        boolean conditional = opcode != Opcodes.GOTO && opcode != Opcodes.JSR;
        if (conditional && i + 1 < n) leaders.add(i + 1);
      }
      // handle switch instructions
      else if (insn instanceof LookupSwitchInsnNode swInsn) {
        // add default target as leader
        int defIndex = insns.indexOf(swInsn.dflt);
        leaders.add(defIndex);
        // add each case target as leader
        for (LabelNode label : swInsn.labels) {
          leaders.add(insns.indexOf(label));
        }
        if (i + 1 < n) leaders.add(i + 1);
      } else if (insn instanceof TableSwitchInsnNode tsInsn) {
        int defIdx = insns.indexOf(tsInsn.dflt);
        leaders.add(defIdx);
        for (LabelNode label : tsInsn.labels) {
          leaders.add(insns.indexOf(label));
        }
      }
    }

    // Order leaders based on instruction index
    List<Integer> orderedLeaders = new LinkedList<>(leaders);
    Collections.sort(orderedLeaders);

    // keep track of leaders -> basic block id
    Map<Integer, BasicBlockBuilder> blockAt = new HashMap<>();
    // keep track of last valid instruction in the each block (avoid recomputing it later)
    Map<BasicBlockBuilder, Integer> lastInstructionAt = new HashMap<>();

    // NOTE: Step 2 - identify block bounds and add instructions to each block
    // start from leader, proceed until next leader (or end of method)
    for (int i = 0; i < orderedLeaders.size(); i++) {
      // NOTE: i = id of basic block
      // compute start and end index of basic block
      int startIdx = orderedLeaders.get(i);
      int endIdx = (i + 1 < orderedLeaders.size()) ? orderedLeaders.get(i + 1) : n; // NOTE: wrap to n if last block

      // build basic block by decoding instructions inside it
      BasicBlockBuilder bb = new BasicBlockBuilder(i);
      int lastValidIdx = -1;
      for (int pc = startIdx; pc < endIdx; pc++) {
        AbstractInsnNode insn = insns.get(pc);
        if (insn.getOpcode() != -1) {
          String decoded = Disassembler.disassembleInstruction(insn, pc, insns);
          bb.appendInstruction(decoded);
          lastValidIdx = pc;
        }
      }
      // we link the start leader to the basic block we just created (why? useful to link target and source of jumps)
      blockAt.put(startIdx, bb);
      // record last valid instruction in this block
      lastInstructionAt.put(bb, lastValidIdx);
    }

    // NOTE: Step 3 - create basic blocks and add to the cfg
    Map<BasicBlockBuilder, BasicBlock> finalizedBlocks = new HashMap<>();
    for (Map.Entry<Integer, BasicBlockBuilder> blocks : blockAt.entrySet()) {
      BasicBlockBuilder bb = blocks.getValue();
      BasicBlock finalized = bb.build();
      cfg.addNode(finalized);
      // link to finalized version of the block
      finalizedBlocks.put(bb, finalized);
    }

    // NOTE: Step 4 - add edge from entry node to the first basic block entered
    BasicBlock first_bb = finalizedBlocks.get(blockAt.get(orderedLeaders.get(0)));
    cfg.addEntryEdge(first_bb);

    // NOTE: Step 5 - connect basic blocks with edges based on control flow (jumps, switches, fall-through as last instruction of a block)
    // loop through all leaders, find the last instruction of each block and create edges
    for (int i = 0; i < orderedLeaders.size(); i++) {
      // get basic block of the current leader
      int startIdx = orderedLeaders.get(i);
      BasicBlockBuilder from_builder = blockAt.get(startIdx);
      BasicBlock from = finalizedBlocks.get(from_builder);

      // find the last valid instruction (opcode != -1) in this block
      int lastIdx = lastInstructionAt.getOrDefault(from_builder, -1);
      // NOTE: if no valid instruction, skip to next block
      if (lastIdx == -1) continue;

      // get last instruction node object
      AbstractInsnNode lastInsn = insns.get(lastIdx);
      int lastOpcode = lastInsn.getOpcode();

      // now, depending on the type of last instruction, we connect the respective leaders's basic block to the target(s)

      // handle jump instructions (link to target, and fall-through if conditional)
      if (lastInsn instanceof JumpInsnNode jIns) {
        // add edge to jump target
        int tgtIdx = insns.indexOf(jIns.label);
        BasicBlock to = finalizedBlocks.get(blockAt.get(tgtIdx));

        // if unconditional jump, treat as fall-through
        boolean unconditional = lastOpcode == Opcodes.GOTO && lastOpcode != Opcodes.JSR;
        if (unconditional) {
          cfg.addFallthroughEdge(from, to);
        } else {
          // otherwise, handle both cases
          cfg.addBranchTakenEdge(from, to);
          // compute fall
          int fallThruIdx = i + 1;
          if (fallThruIdx < orderedLeaders.size()) {
            BasicBlock fallThruBlock = finalizedBlocks.get(
              blockAt.get(orderedLeaders.get(fallThruIdx))
            );
            cfg.addFallthroughEdge(from, fallThruBlock);
          }
        }
      }
      // handle switch instructions (link to each case and default)
      else if (lastInsn instanceof LookupSwitchInsnNode sw) {
        // cycle through each case and add edge
        for (int k = 0; k < sw.keys.size(); k++) {
          int key = sw.keys.get(k);
          int tgt = insns.indexOf(sw.labels.get(k));
          cfg.addCaseEdge(from, finalizedBlocks.get(blockAt.get(tgt)), key);
        }
        // record default case
        int def = insns.indexOf(sw.dflt);
        cfg.addDefaultEdge(from, finalizedBlocks.get(blockAt.get(def)));
      }
      // handle table switch instructions (link to each case and default)
      else if (lastInsn instanceof TableSwitchInsnNode ts) {
        for (int k = 0; k < ts.labels.size(); k++) {
          int key = ts.min + k;
          int tgt = insns.indexOf(ts.labels.get(k));
          cfg.addCaseEdge(from, finalizedBlocks.get(blockAt.get(tgt)), key);
        }
        int def = insns.indexOf(ts.dflt);
        cfg.addDefaultEdge(from, finalizedBlocks.get(blockAt.get(def)));
      }
      // last instruction is a return or throw, so we add an exit edge
      else if (
        (lastOpcode >= Opcodes.IRETURN && lastOpcode <= Opcodes.RETURN) ||
        lastOpcode == Opcodes.ATHROW
      ) {
        cfg.addExitEdge(from);
      }
      // otherwise, we have a normal instruction, so we add a fallthrough edge to the next block (if any)
      // NOTE: this can happen if the last instruction is not a jump/switch/return/throw but another leader follows (hence, a new block starts)
      //       e.g., another leader was added because it is a jump target of another block
      else {
        // normal fallthrough if there is a next block
        if (i + 1 < orderedLeaders.size()) {
          BasicBlock to = finalizedBlocks.get(blockAt.get(orderedLeaders.get(i + 1)));
          cfg.addFallthroughEdge(from, to);
        } else {
          // method ends implicitly? treat as exit
          cfg.addExitEdge(from);
        }
      }
    }
    // return control-flow graph
    return cfg;
  }
}
