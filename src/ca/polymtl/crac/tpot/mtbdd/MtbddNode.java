package ca.polymtl.crac.tpot.mtbdd;

/**
 * @author Olivier Bachard
 */
public class MtbddNode {

    /**
     * A pointer to the C DdNode structure.
     */
    private long pointer;

    public MtbddNode() {
        this.pointer = Nat_CreateNode();
    }

    public MtbddNode(long n) {
        this.pointer = n;
    }

    public MtbddNode(MtbddNode n) {
        this.pointer = n.getPointer();
    }

    /**
     * @return a mtbdd where all yi variables have been replaced by the xi
     *         related variables, considering probability. It means that boolean
     *         "Or" have been replaced by "Plus" operation.
     */
    public MtbddNode convertTree() {
        if (!this.isConstant()) {
            // If node is yi, it will return xi; because index are moved back a
            // row.
            MtbddNode n = Mtbdd.addIthVar(this.getIndex() - 1);
            return Mtbdd.mtbddPlus(Mtbdd.mtbddAnd(n, this.getThenChild()
                    .convertTree()), Mtbdd.mtbddAnd(Mtbdd.mtbddNot(n), this
                    .getElseChild().convertTree()));
        }
        return this;
    }

    /**
     * @return a mtbdd where all yi variables have been replaced by the xi
     *         related variables, without considering probability.
     */
    public MtbddNode convertTreeUnprob() {
        if (!this.isConstant()) {
            // If node is yi, it will return xi; because index are moved back a
            // row.
            MtbddNode n = Mtbdd.addIthVar(this.getIndex() - 1);
            return Mtbdd.mtbddOr(Mtbdd.mtbddAnd(n, this.getThenChild()
                    .convertTreeUnprob()), Mtbdd.mtbddAnd(Mtbdd.mtbddNot(n),
                    this.getElseChild().convertTreeUnprob()));
        }
        return this;
    }

    public double getConstValue() {
        return Nat_GetConstValue(this.pointer);
    }

    public MtbddNode getElseChild() {
        return new MtbddNode(Nat_GetElseChild(this.pointer));
    }

    public int getIndex() {
        return Nat_GetIndex(this.pointer);
    }

    public long getPointer() {
        return this.pointer;
    }

    public MtbddNode getThenChild() {
        return new MtbddNode(Nat_GetThenChild(this.pointer));
    }

    public boolean isComplemented() {
        return Nat_IsComplemented(this.pointer);
    }

    public boolean isConstant() {
        return Nat_IsConstant(this.pointer);
    }

    /**
     * Check if the MtbddNode is a final node. If the node "this" is a final
     * node, the result of boolean "and" between "this" and the finals nodes
     * will be not null.
     * @param finals
     *            the MtbddNode to check
     * @return true if the variable finals contains the node, false otherwise.
     */
    public boolean isFinal(MtbddNode finals) {
        MtbddNode n1 = Mtbdd.mtbddAnd(this, finals);
        Mtbdd.Nat_RecursiveDeref(n1.getPointer());
        return !n1.isConstant();
    }

    private native long Nat_CreateNode();

    private native double Nat_GetConstValue(long node);

    public native long Nat_GetElseChild(long node);

    public native int Nat_GetIndex(long node);

    public native long Nat_GetThenChild(long node);

    public native boolean Nat_IsComplemented(long n);

    public native boolean Nat_IsConstant(long node);

    public native long Nat_setConstValue(double c, long node);

    public void setConstValue(double constValue) {
        this.pointer = Nat_setConstValue(constValue, this.pointer);
    }

    public void setPointer(long pointerIn) {
        this.pointer = pointerIn;
    }
}
