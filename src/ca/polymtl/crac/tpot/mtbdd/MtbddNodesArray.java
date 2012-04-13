package ca.polymtl.crac.tpot.mtbdd;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.jautomata.rationals.State;
import net.jautomata.rationals.DefaultStateFactory.DefaultState;


public class MtbddNodesArray {

    private List<MtbddNode> nodes;

    public MtbddNodesArray() {
        this.nodes = new LinkedList<>();
    }

    public final void addNode(final MtbddNode node) {
        this.nodes.add(node);
    }

    /**
     * Build MtbddNodes from explicit State and store them in a list.
     * @param states
     * @param arr_nodes
     */
    public final void buildStates(final Set<State> states,
            final MtbddNodesArray arr_nodes) {

        this.nodes.clear();
        int nbrNodes = arr_nodes.getNodes().size();

        // For each state
        for (State state : states) {
            DefaultState s = (DefaultState) state;
            MtbddNode n = Mtbdd.addConst(1);
            // MtbddNode n = new MtbddNode();
            Mtbdd.Nat_RecursiveDeref(n.getPointer());

            // Get the list of boolean representing the binary current state
            s.completeBinaryTab(nbrNodes);
            List<Boolean> start = new LinkedList<>(s.getBinaryTab());

            for (int i = 0; i < start.size(); i++) {
                // if the boolean value is true, we send the node
                if (start.get(i)) {
                    n = Mtbdd.mtbddAnd(n, arr_nodes.getNodes().get(i));
                }
                // else we send the complement of the node
                else {
                    n = Mtbdd.mtbddAnd(n,
                            Mtbdd.mtbddNot(arr_nodes.getNodes().get(i)));
                }
            }

            this.nodes.add(n);
        }

    }

    public final List<MtbddNode> getNodes() {
        return this.nodes;
    }

    public final void setNodes(final LinkedList<MtbddNode> nodesIn) {
        this.nodes = nodesIn;
    }
}
