package ca.polymtl.crac.tpot.mtbdd;

import java.util.LinkedList;
import java.util.List;

import net.jautomata.rationals.Transition;
import net.jautomata.rationals.DefaultStateFactory.DefaultState;


public class MtbddTransition {

    MtbddNode transition;
    Object label;
    double probability;

    public MtbddTransition(final Object l) {
        this.label = l;
        this.transition = new MtbddNode(0);
    }

    public MtbddTransition(final Object l, final double p) {
        this.label = l;
        this.probability = p;
        this.transition = new MtbddNode(0);
    }

    /**
     * Build a boolean transition from an explicit transition.
     * @param t
     * @param xVars
     * @param yVars
     * @param prob
     */
    public final void buildMtbddTransition(final Transition t,
            final MtbddNodesArray xVars, final MtbddNodesArray yVars,
            final boolean prob) {

        int nbrNodes = xVars.getNodes().size();
        DefaultState st = (DefaultState) t.start();
        DefaultState en = (DefaultState) t.end();

        // Complete the tab with binary zeros if the binary representation is to
        // short
        st.completeBinaryTab(nbrNodes);
        // Get back the binary representation the start state
        LinkedList<Boolean> start = new LinkedList<>(st.getBinaryTab());

        // Complete the tab with binary zeros if the binary representation is to
        // short
        en.completeBinaryTab(nbrNodes);
        // Get back the binary coding of the end state
        List<Boolean> end = new LinkedList<>(en.getBinaryTab());

        // Add the start state to the transition node
        // For example, if the binary representation of the start state is 101,
        // alias [true,false,true] in @start variable
        // and nodes in startNodes are [x1,x2,x3]
        // the cudd representation of our transition node will be : transition =
        // x1.Not(x2).x3
        if (prob) {
            this.transition = Mtbdd.addConst(this.probability);
        } else {
            this.transition = Mtbdd.addConst(1);
        }

        for (int i = 0; i < start.size(); i++) {
            if (start.get(i)) {
                // if the boolean value is true, we send the node
                this.transition = Mtbdd.mtbddAnd(this.transition, xVars
                        .getNodes().get(i));
            } else {
                // else we send the complement of the node
                this.transition = Mtbdd.mtbddAnd(this.transition,
                        Mtbdd.mtbddNot(xVars.getNodes().get(i)));
            }
        }

        for (int i = 0; i < end.size(); i++) {
            if (end.get(i)) {
                // if the boolean value is true, we send the node
                this.transition = Mtbdd.mtbddAnd(this.transition, yVars
                        .getNodes().get(i));
            } else {
                // else we send the complement of the node
                this.transition = Mtbdd.mtbddAnd(this.transition,
                        Mtbdd.mtbddNot(yVars.getNodes().get(i)));
            }
        }

    }

    public final Object getLabel() {
        return this.label;
    }

    public final double getProbability() {
        return this.probability;
    }

    public final MtbddNode getTransition() {
        return this.transition;
    }

    public final void setLabel(final Object labelIn) {
        this.label = labelIn;
    }

    public final void setProbability(final double probabilityIn) {
        this.probability = probabilityIn;
    }

    public final void setTransition(final MtbddNode transitionIn) {
        this.transition = transitionIn;
    }

}
