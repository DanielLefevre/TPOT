package net.jautomata.rationals;

/**
 * Defines a Transition (an edge from a state to a state) in an Automaton This
 * class defines the notion of transition of an automaton. a transition is a
 * triple <em>(q , l , q')</em> where <em>q, q'</em> are states and <em>l</em> a
 * label. States <em>q</em> and <em>q'</em> must belong to the same automaton
 * <em>A</em> and the transition may only be used with this automaton <em>A</em>
 * .
 * @author yroos@lifl.fr
 * @version 1.0
 * @see Automaton
 */
public class Transition {

    private int hash = Integer.MIN_VALUE;

    private State start;

    private Object label;

    private State end;

    /**
     * Creates a new transition <em>(q , l , q')</em>.
     * @param start
     *            the state <em>q</em> for this transition <em>(q , l , q')</em>
     *            .
     * @param label
     *            the label <em>l</em>
     * @param end
     *            the state <em>q'</em> for this transition
     *            <em>(q , l , q')</em>.
     */
    public Transition(final State start, final Object label, final State end) {
        this.start = start;
        this.label = label;
        this.end = end;
    }

    /**
     * Returns the starting state of this transition.
     * @return the starting state of this transition, that is the state
     *         <em>q</em> for this transition <em>(q , l , q')</em>.
     */
    public final State start() {
        return this.start;
    }

    /**
     * Returns the label this transition.
     * @return the label state of this transition, that is the object <em>l</em>
     *         for this transition <em>(q , l , q')</em>.
     */
    public final Object label() {
        return this.label;
    }

    /**
     * Returns the ending state of this transition.
     * @return the ending state of this transition, that is the state
     *         <em>q'</em> for this transition <em>(q , l , q')</em>.
     */
    public final State end() {
        return this.end;
    }

    /**
     * returns a textual representation of this transition.
     * @return a textual representation of this transition based
     */
    @Override
    public final String toString() {
        if (this.label == null) {
            return "(" + this.start + " , 1 , " + this.end + ")";
        } else {
            return "(" + this.start + " , " + this.label + " , " + this.end
                    + ")";
        }
    }

    /**
     * Determines if this transition is equal to the parameter.
     * @param o
     *            any object.
     * @return true iff this transition is equal to the parameter. That is if
     *         <tt>o</tt> is a transition which is composed same states and
     *         label (in the sense of method <tt>equals</tt>).
     */
    @Override
    public final boolean equals(final Object o) {
        // check for self-comparison
        if (this == o) {
            return true;
        }

        if (!(o instanceof Transition)) {
            return false;
        }

        // cast to native object is now safe
        Transition that = (Transition) o;

        // now a proper field-by-field evaluation can be made
        return that.label.equals(this.label) && that.start.equals(this.start)
                && that.end.equals(this.end);
    }

    public final boolean equals(final Transition t) {
        if (!t.start().equals(this.start)) {
            return false;
        }
        if (!t.end().equals(this.end)) {
            return false;
        }
        if (!((PSymbol) t.label()).getLabel().equals(
                ((PSymbol) this.label).getLabel())) {
            return false;
        }
        if (((PSymbol) t.label()).getProbability() != ((PSymbol) this.label)
                .getProbability()) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hashcode value for this transition.
     * @return a hashcode value for this transition.
     */
    @Override
    public final int hashCode() {
        /* store computed value */
        if (this.hash != Integer.MIN_VALUE) {
            return this.hash;
        }
        int x, y, z;
        if (this.start == null) {
            x = 0;
        } else {
            x = this.start.hashCode();
        }
        if (this.end == null) {
            y = 0;
        } else {
            y = this.end.hashCode();
        }
        if (this.label == null) {
            z = 0;
        } else {
            z = this.label.hashCode();
        }
        int t = new java.awt.Point(x, y).hashCode();
        return this.hash = new java.awt.Point(t, z).hashCode();
    }

    /**
     * Replaces the label for this transition
     * <p>
     * WARNING: this method is extremely dangerous as it does not update the
     * alphabet of the automaton this transition is part of. Be sure you know
     * what you are doing or else everything could break down
     * @param msg
     */
    public final void setLabel(final Object obj) {
        this.label = obj;
    }

    /**
     * Creates a reversed transition. So the start state is at the end and
     * viceversa.
     * @return a new reversed transition
     */
    final Transition getReversed() {
        return new Transition(this.end, this.label, this.start);
    }

}
