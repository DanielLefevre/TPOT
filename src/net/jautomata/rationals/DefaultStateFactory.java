package net.jautomata.rationals;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class is used by Automaton objects to create new states on A user can
 * implement its own version of StateFactory by providing an implementation for
 * createState.
 * @author Arnaud.Bailly - bailly@lifl.fr
 * @version Thu Apr 25 2002
 */
public class DefaultStateFactory implements StateFactory, Cloneable {

    public class DefaultState implements State {

        public final int i;

        boolean initial;

        boolean terminal;

        Automaton a;

        /** @author Olivier */
        // -------------------------------------

        protected final String binaryValue;

        protected LinkedList<Boolean> binaryTab;

        // -------------------------------------

        public DefaultState(final int iIn, final boolean initialIn,
                final boolean terminalIn) {

            this.i = iIn;

            /** @author Olivier */
            // -------------------------------------

            this.binaryValue = Integer.toBinaryString(this.i);
            this.binaryTab = new LinkedList<>();

            for (int j = 0; j < this.binaryValue.length(); j++) {
                if (String.valueOf(this.binaryValue.charAt(j)).equals("1")) {
                    this.binaryTab.add(true);
                } else if (String.valueOf(this.binaryValue.charAt(j)).equals(
                        "0")) {
                    this.binaryTab.add(false);
                }
            }

            // ------------------------------------------------------

            this.a = DefaultStateFactory.this.getAutomaton();
            this.initial = initialIn;
            this.terminal = terminalIn;

        }

        /** @author Olivier */
        // -------------------------------------
        public String getBinaryValue() {
            return this.binaryValue;
        }

        public final LinkedList<Boolean> getBinaryTab() {
            return this.binaryTab;
        }

        public final void setBinaryTab(final LinkedList<Boolean> binaryTab) {
            this.binaryTab = binaryTab;
        }

        public final String printBinaryTab() {
            String out = "[";
            for (int i = 0; i < this.binaryTab.size(); i++) {
                if (i != this.binaryTab.size() - 1) {
                    out += this.binaryTab.get(i) + ",";
                } else {
                    out += this.binaryTab.get(i);
                }
            }
            out += "]";
            return out;
        }

        public final void completeBinaryTab(final int size) {
            while (this.binaryTab.size() < size) {
                for (int i = 0; i < size - this.binaryTab.size(); i++) {
                    this.binaryTab.addFirst(false);
                }
            }
        }

        // -------------------------------------

        /*
         * (non-Javadoc)
         * @see salvo.jesus.graph.Vertex#getObject()
         */
        public final Object getObject() {
            return new Integer(this.i);
        }

        /*
         * (non-Javadoc)
         * @see salvo.jesus.graph.Vertex#setObject(java.lang.Object)
         */
        public void setObject(final Object object) {
            /* NOOP */
        }

        /*
         * (non-Javadoc)
         * @see rationals.State#setInitial(boolean)
         */
        @Override
        public final void setInitial(final boolean initialIn) {
            this.initial = initialIn;
        }

        /*
         * (non-Javadoc)
         * @see rationals.State#setTerminal(boolean)
         */
        @Override
        public final void setTerminal(final boolean terminalIn) {
            this.terminal = terminalIn;
        }

        /*
         * (non-Javadoc)
         * @see rationals.State#isInitial()
         */
        @Override
        public final boolean isInitial() {
            return this.initial;
        }

        /*
         * (non-Javadoc)
         * @see rationals.State#isTerminal()
         */
        @Override
        public final boolean isTerminal() {
            return this.terminal;
        }

        @Override
        public final String toString() {
            return Integer.toString(this.i);
        }

        @Override
        public final boolean equals(final Object o) {
            try {
                DefaultState ds = (DefaultState) o;
                return (ds.i == this.i) && (this.a == ds.a);
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        public final int hashCode() {
            return this.i;
        }
    }

    private class DefaultStateSet implements Set {

        private DefaultStateFactory df;

        /**
         * Constructor.
         * @param setIn
         *            the set of states
         * @param dfIn
         *            the default state factory
         */
        public DefaultStateSet(final DefaultStateSet setIn,
                final DefaultStateFactory dfIn) {
            this.bits = (BitSet) setIn.bits.clone();
            this.df = dfIn;
        }

        /**
         * Default constructor.
         * @param dfIn
         *            the default state factory
         */
        public DefaultStateSet(final DefaultStateFactory dfIn) {
            this.df = dfIn;
        }

        @Override
        public boolean equals(final Object obj) {
            DefaultStateSet dss = (DefaultStateSet) obj;
            if (dss == null) {
                return false;
            }
            return dss.bits.equals(this.bits) && dss.df == this.df;
        }

        @Override
        public int hashCode() {
            return this.bits.hashCode();
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            String b = this.bits.toString();
            sb.append(b.substring(1, b.length() - 1));
            sb.append(']');
            return sb.toString();
        }

        private int modcount = 0;
        private int mods = 0;
        private int bit = -1;

        private BitSet bits = new BitSet();

        private Iterator it = new Iterator() {

            @Override
            public void remove() {
                if (DefaultStateSet.this.getBit() > 0) {
                    DefaultStateSet.this.getBits().clear(
                            DefaultStateSet.this.getBit());
                }
            }

            @Override
            public boolean hasNext() {
                return DefaultStateSet.this.getBits().nextSetBit(
                        DefaultStateSet.this.getBit()) > -1;
            }

            @Override
            public Object next() {
                DefaultStateSet.this.setBit(DefaultStateSet.this.getBits()
                        .nextSetBit(DefaultStateSet.this.getBit()));
                if (DefaultStateSet.this.getBit() == -1) {
                    throw new NoSuchElementException();
                }
                DefaultState ds = new DefaultState(
                        DefaultStateSet.this.getBit(), false, false);
                ds.initial = DefaultStateFactory.this.getAutomaton().initials()
                        .contains(ds);
                ds.terminal = DefaultStateFactory.this.getAutomaton()
                        .terminals().contains(ds);
                DefaultStateSet.this
                        .setMods(DefaultStateSet.this.getMods() + 1);
                DefaultStateSet.this.setModCount(DefaultStateSet.this
                        .getModCount() + 1);
                if (DefaultStateSet.this.getMods() != DefaultStateSet.this
                        .getModCount()) {
                    throw new ConcurrentModificationException();
                }
                /* advance iterator */
                DefaultStateSet.this.bit++;
                return ds;
            }
        };

        /*
         * (non-Javadoc)
         * @see java.util.Set#size()
         */
        @Override
        public int size() {
            return this.bits.cardinality();
        }

        /**
         * Setter.
         * @param modCountIn
         *            the new mod count
         */
        protected void setModCount(final int modCountIn) {
            this.modcount = modCountIn;
        }

        /**
         * Setter.
         * @param modsIn
         *            the new mods
         */
        protected void setMods(final int modsIn) {
            this.mods = modsIn;
        }

        /**
         * Getter.
         * @return the mod count
         */
        protected int getModCount() {
            return this.modcount;
        }

        /**
         * Getter.
         * @return the mods
         */
        protected int getMods() {
            return this.mods;

        }

        /**
         * Setter.
         * @param bitIn
         *            the bit
         */
        public void setBit(final int bitIn) {
            this.bit = bitIn;
        }

        /**
         * Getter.
         * @return the bit set
         */
        public BitSet getBits() {
            return this.bits;
        }

        /**
         * Getter.
         * @return the bit
         */
        public int getBit() {
            return this.bit;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#clear()
         */
        @Override
        public void clear() {
            this.modcount++;
            this.bits.clear();
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#isEmpty()
         */
        @Override
        public boolean isEmpty() {
            return this.bits.isEmpty();
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#toArray()
         */
        @Override
        public Object[] toArray() {
            Object[] ret = new Object[size()];
            Iterator it = iterator();
            int i = 0;
            while (it.hasNext()) {
                ret[i++] = it.next();
            }
            return ret;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#add(java.lang.Object)
         */
        @Override
        public boolean add(final Object o) {
            DefaultState ds = (DefaultState) o;
            if (this.bits.get(ds.i)) {
                return false;
            }
            this.bits.set(ds.i);
            this.modcount++;
            return true;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#contains(java.lang.Object)
         */
        @Override
        public boolean contains(final Object o) {
            DefaultState ds = (DefaultState) o;
            return this.bits.get(ds.i);
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#remove(java.lang.Object)
         */
        @Override
        public boolean remove(final Object o) {
            DefaultState ds = (DefaultState) o;
            if (!this.bits.get(ds.i)) {
                return false;
            }
            this.bits.clear(ds.i);
            this.modcount++;
            return true;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#addAll(java.util.Collection)
         */
        @Override
        public boolean addAll(final Collection c) {
            DefaultStateSet dss = (DefaultStateSet) c;
            this.bits.or(dss.bits);
            this.modcount++;
            return true;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#containsAll(java.util.Collection)
         */
        @Override
        public boolean containsAll(final Collection c) {
            DefaultStateSet dss = (DefaultStateSet) c;
            BitSet bs = new BitSet();
            bs.or(this.bits);
            bs.and(dss.bits);
            this.modcount++;
            return bs.equals(dss.bits);
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#removeAll(java.util.Collection)
         */
        @Override
        public boolean removeAll(final Collection c) {
            DefaultStateSet dss = (DefaultStateSet) c;
            this.bits.andNot(dss.bits);
            this.modcount++;
            return true;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#retainAll(java.util.Collection)
         */
        @Override
        public boolean retainAll(final Collection c) {
            DefaultStateSet dss = (DefaultStateSet) c;
            this.bits.and(dss.bits);
            this.modcount++;
            return true;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#iterator()
         */
        @Override
        public Iterator iterator() {
            /* reset iterator */
            this.bit = this.modcount = this.mods = 0;
            return this.it;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Set#toArray(java.lang.Object[])
         */
        @Override
        public Object[] toArray(final Object[] a) {
            Object[] ret;
            if (a.length == size()) {
                ret = a;
            } else { /* create array dynamically */
                ret = (Object[]) Array.newInstance(a.getClass()
                        .getComponentType(), size());
            }
            Iterator it = iterator();
            int i = 0;
            while (it.hasNext()) {
                DefaultState ds = (DefaultState) it.next();
                ret[ds.i] = ds;
            }
            return ret;
        }

    }

    // //////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////

    private int id = 0;

    private Automaton automaton;

    // //////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////

    DefaultStateFactory(final Automaton a) {
        this.automaton = a;
    }

    /**
     * Getter.
     * @return the automaton
     */
    public final Automaton getAutomaton() {
        return this.automaton;
    }

    /**
     * Creates a new state which is initial and terminal or not, depending on
     * the value of parameters.
     * @param initial
     *            if true, this state will be initial; otherwise this state will
     *            be non initial.
     * @param terminal
     *            if true, this state will be terminal; otherwise this state
     *            will be non terminal.
     * @return the new created state
     */
    @Override
    public final State create(final boolean initial, final boolean terminal) {
        return new DefaultState(this.id++, initial, terminal);
    }

    /*
     * (non-Javadoc)
     * @see rationals.StateFactory#stateSet()
     */
    @Override
    public final Set<State> stateSet() {
        return new DefaultStateSet(this);
    }

    /*
     * (non-Javadoc)
     * @see rationals.StateFactory#stateSet(java.util.Set)
     */
    @Override
    public final Set stateSet(final Set s) {
        return new DefaultStateSet((DefaultStateSet) s, this);
    }

    @Override
    public final Object clone() {
        DefaultStateFactory cl;
        try {
            cl = (DefaultStateFactory) super.clone();
            cl.id = 0;
        } catch (CloneNotSupportedException e) {
            cl = null;
        }
        return cl;
    }

    /*
     * (non-Javadoc)
     * @see rationals.StateFactory#setAutomaton(rationals.Automaton)
     */
    @Override
    public final void setAutomaton(final Automaton automatonIn) {
        this.automaton = automatonIn;
    }
}
