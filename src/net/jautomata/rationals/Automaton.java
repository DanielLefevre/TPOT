package net.jautomata.rationals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jautomata.rationals.transformations.TransformationsToolBox;
import ca.polymtl.crac.tpot.scheduler.Scheduler;


/**
 * A class defining Automaton objects This class defines the notion of
 * automaton. Following notations are used to describe this class.
 * <p>
 * An automaton is a 5-uple <em>A = (X , Q , I , T , D)</em> where
 * <ul>
 * <li><em>X</em> is a finite set of labels named alphabet ,
 * <li><em>Q</em> is a finite set of states,
 * <li><em>I</em>, included in <em>Q</em>, is the set of initial states,
 * <li><em>T</em>, included in <em>Q</em>, is the set of terminal states
 * <li>and <em>D</em> is the set of transitions, which is included in
 * <em>Q times X times Q</em> (transitions are triples <em>(q , l , q')</em>
 * where <em>q, q'</em> are states and <em>l</em> a label).
 * </ul>
 * The empty word, usually denoted by <em>epsilon</em> will be denoted here by
 * the Object <em>@</em>.
 * <p>
 * In this implementation of automaton, any object may be a label, states are
 * instance of class <tt>State</tt> and transitions are intances of class
 * <tt>Transition</tt>. Only automata should create instances of states through
 * <tt>Automaton</tt> method <tt>newState</tt>.
 * @author yroos@lifl.fr
 * @author bailly@lifl.fr
 * @version $Id: Automaton.java 1267 2006-08-14 13:24:42Z nono $
 * @see Transition State
 */
public class Automaton implements Acceptor, StateMachine, Rational, Cloneable {

    /* the identification of this automaton */

    private Object id;

    /**
     * @return Returns the id.
     */
    @Override
    public final Object getId() {
        return this.id;
    }

    /**
     * @param id
     *            The id to set.
     */
    @Override
    public final void setId(final Object id) {
        this.id = id;
    }

    /**
     * The set of all objects which are labels of transitions of this automaton.
     */
    protected Set<Object> alphabet;

    /**
     * The set of all states of this automaton.
     */
    private Set<State> states;

    /**
     * the set of initial states
     */
    private Set<State> initials;

    /**
     * the set of terminale states
     */
    private Set<State> terminals;

    /**
     * Allows acces to transitions of this automaton starting from a given state
     * and labelled by a given object. The keys of this map are instances of
     * class Key and values are sets of transitions.
     */

    private Map<Key, Set<Transition>> transitions;

    /**
     * Allows acces to transitions of this automaton arriving to a given state
     * and labelled by a given object. The keys of this map are instances of
     * class Key and values are sets of transitions.
     */
    private Map<Key, Set<Transition>> reverse;

    /**
     * bonte
     */
    private StateFactory stateFactory = new DefaultStateFactory(this);

    /**
     * @return
     */
    @Override
    public final StateFactory getStateFactory() {
        return this.stateFactory;
    }

    /**
     * @param factory
     */
    @Override
    public final void setStateFactory(final StateFactory factory) {
        this.stateFactory = factory;
        factory.setAutomaton(this);
    }

    /**
     * Returns an automaton which recognizes the regular language associated
     * with the regular expression <em>@</em>, where <em>@</em> denotes the
     * empty word.
     * @return an automaton which recognizes <em>@</em>
     */
    public static Automaton epsilonAutomaton() {
        Automaton v = new Automaton();
        v.addState(true, true);
        return v;
    }

    /**
     * Returns an automaton which recognizes the regular language associated
     * with the regular expression <em>l</em>, where <em>l</em> is a given
     * label.
     * @param label
     *            any object that will be used as a label.
     * @return an automaton which recognizes <em>label</em>
     */
    public static Automaton labelAutomaton(final Object label) {
        Automaton v = new Automaton();
        State start = v.addState(true, false);
        State end = v.addState(false, true);
        try {
            v.addTransition(new Transition(start, label, end));
        } catch (NoSuchStateException x) {
        }
        return v;
    }

    /**
     * Returns an automaton which recognizes the regular language associated
     * with the regular expression <em>u</em>, where <em>u</em> is a given word.
     * @param word
     *            a List of Object interpreted as a word
     * @return an automaton which recognizes <em>label</em>
     */
    public static Automaton labelAutomaton(final List<Object> word) {
        Automaton v = new Automaton();
        State start = null;
        if (word.isEmpty()) {
            v.addState(true, true);
            return v;
        } else {
            start = v.addState(true, false);
        }
        State end = null;
        try {
            for (Iterator<Object> i = word.iterator(); i.hasNext();) {
                Object o = i.next();
                end = v.addState(false, !i.hasNext());
                v.addTransition(new Transition(start, o, end));
                start = end;
            }
        } catch (NoSuchStateException x) {
        }
        return v;
    }

    /**
     * Creates a new empty automaton which contains no state and no transition.
     * An empty automaton recognizes the empty language.
     */
    public Automaton() {
        this(null);
    }

    /**
     * Create a new empty automaton with given state factory.
     * @param sf
     *            the StateFactory object to use for creating new states. May be
     *            null.
     */
    public Automaton(final StateFactory sf) {
        this.stateFactory = sf == null ? new DefaultStateFactory(this) : sf;
        this.alphabet = new HashSet<Object>();
        this.states = this.stateFactory.stateSet();
        this.initials = this.stateFactory.stateSet();
        this.terminals = this.stateFactory.stateSet();
        this.transitions = new HashMap<Key, Set<Transition>>();
        this.reverse = new HashMap<Key, Set<Transition>>();
    }

    /**
     * Returns a new instance of state which will be initial and terminal or not
     * depending of parameters.
     * @param initial
     *            if true, the new state will be initial; otherwise this state
     *            will be non initial.
     * @param terminal
     *            if true, the new state will be terminal; otherwise this state
     *            will be non terminal.
     * @return a new state, associated with this automaton. This new state
     *         should be used only with this automaton in order to create a new
     *         transition for this automaton.
     * @see Transition
     */
    @Override
    public State addState(final boolean initial, final boolean terminal) {

        State state = this.stateFactory.create(initial, terminal);
        if (initial) {
            this.initials.add(state);
        }
        if (terminal) {
            this.terminals.add(state);
        }
        this.states.add(state);
        return state;
    }

    public final void addState(final State s) {
        if (s.isInitial()) {
            this.initials.add(s);
        }
        if (s.isTerminal()) {
            this.terminals.add(s);
        }
        this.states.add(s);
    }

    /**
     * Returns the alphabet <em>X</em> associated with this automaton.
     * @return the alphabet <em>X</em> associated with this automaton.
     */
    @Override
    public final Set<Object> alphabet() {
        return this.alphabet;
    }

    /**
     * Returns the set of states <em>Q</em> associated with this automaton.
     * @return the set of states <em>Q</em> associated with this automaton.
     *         Objects which are contained in this set are instances of class
     *         <tt>State</tt>.
     * @see State
     */
    @Override
    public final Set<State> states() {
        return this.states;
    }

    /**
     * Returns the set of initial states <em>I</em> associated with this
     * automaton.
     * @return the set of initial states <em>I</em> associated with this
     *         automaton. Objects which are contained in this set are instances
     *         of class <tt>State</tt>.
     * @see State
     */
    @Override
    public final Set<State> initials() {
        return this.initials;
    }

    /**
     * Returns the set of terminal states <em>T</em> associated with this
     * automaton.
     * @return set of terminal states <em>T</em> associated with this automaton.
     *         Objects which are contained in this set are instances of class
     *         <tt>State</tt>.
     * @see State
     */
    @Override
    public final Set<State> terminals() {
        return this.terminals;
    }

    // Computes and return the set of all accessible states, starting
    // from a given set of states and using transitions
    // contained in a given Map
    protected final Set<State> access(final Set<State> start,
            final Map<Key, Set<Transition>> map) {
        Set<State> current = start;
        Set<State> old;
        do {
            old = current;
            current = this.stateFactory.stateSet();
            Iterator<State> i = old.iterator();
            while (i.hasNext()) {
                State e = i.next();
                current.add(e);
                Iterator<Object> j = this.alphabet.iterator();
                while (j.hasNext()) {
                    Iterator<Transition> k = find(map, e, j.next()).iterator();
                    while (k.hasNext()) {
                        current.add((k.next()).end());
                    }
                }
            }
        } while (current.size() != old.size());
        return current;
    }

    /**
     * Returns the set of all accessible states in this automaton.
     * @return the set of all accessible states in this automaton. A state
     *         <em>s</em> is accessible if there exists a path from an initial
     *         state to <em>s</em>. Objects which are contained in this set are
     *         instances of class <tt>State</tt>.
     * @see State
     */
    @Override
    public Set<State> accessibleStates() {
        return access(this.initials, this.transitions);
    }

    /**
     * Returns the set of states that can be accessed in this automaton starting
     * from given set of states
     * @param states
     *            a non null set of starting states
     * @return a - possibly empty - set of accessible states
     */
    @Override
    public Set<State> accessibleStates(final Set<State> states) {
        return access(states, this.transitions);
    }

    /*
     * (non-Javadoc)
     * @see rationals.Rational#accessibleStates(rationals.State)
     */
    @Override
    public Set<State> accessibleStates(final State state) {
        Set s = this.stateFactory.stateSet();
        s.add(state);
        return access(s, this.transitions);
    }

    /**
     * Returns the set of co-accesible states for a given set of states, that is
     * the set of states from this automaton from which there exists a path to a
     * state in <code>states</code>.
     * @param states
     *            a non null set of ending states
     * @return a - possibly empty - set of coaccessible states
     */
    @Override
    public final Set<State> coAccessibleStates(final Set<State> states) {
        return access(states, this.reverse);
    }

    /**
     * Returns the set of all co-accessible states in this automaton.
     * @return the set of all co-accessible states in this automaton. A state
     *         <em>s</em> is co-accessible if there exists a path from this
     *         state <em>s</em> to a terminal state. Objects which are contained
     *         in this set are instances of class <tt>State</tt>.
     * @see State
     */
    @Override
    public final Set<State> coAccessibleStates() {
        return access(this.terminals, this.reverse);
    }

    /**
     * Returns the set of all states which are co-accessible and accessible in
     * this automaton.
     * @return the set of all states which are co-accessible and accessible in
     *         this automaton. A state <em>s</em> is accessible if there exists
     *         a path from an initial state to <em>s</em>. A state <em>s</em> is
     *         co-accessible if there exists a path from this state <em>s</em>
     *         to a terminal state. Objects which are contained in this set are
     *         instances of class <tt>State</tt>.
     * @see State
     */
    @Override
    public Set<State> accessibleAndCoAccessibleStates() {
        Set<State> ac = accessibleStates();
        ac.retainAll(coAccessibleStates());
        return ac;
    }

    // Computes and return the set of all transitions, starting
    // from a given state and labelled by a given label
    // contained in a given Map
    protected final Set<Transition> find(final Map<Key, Set<Transition>> m,
            final State e, final Object l) {
        Key n = new Key(e, l);
        if (!m.containsKey(n)) {
            return new HashSet<Transition>();
        }
        return m.get(n);
    }

    // add a given transition in a given Map
    protected final void add(final Map<Key, Set<Transition>> m,
            final Transition t) {
        Key n = new Key(t.start(), t.label());
        Set<Transition> s;
        if (!m.containsKey(n)) {
            s = new HashSet<Transition>();
            m.put(n, s);
        } else {
            s = m.get(n);
        }
        s.add(t);
    }

    /**
     * Returns the set of all transitions of this automaton
     * @return the set of all transitions of this automaton Objects which are
     *         contained in this set are instances of class <tt>Transition</tt>.
     * @see Transition
     */
    @Override
    public Set<Transition> delta() {
        Set<Transition> s = new HashSet<Transition>();
        Iterator<Set<Transition>> i = this.transitions.values().iterator();
        while (i.hasNext()) {
            s.addAll(i.next());
        }
        return s;
    }

    /**
     * Returns the set of all transitions of this automaton starting from a
     * given state and labelled b a given label.
     * @param state
     *            a state of this automaton.
     * @param label
     *            a label used in this automaton.
     * @return the set of all transitions of this automaton starting from state
     *         <tt>state</tt> and labelled by <tt>label</tt>. Objects which are
     *         contained in this set are instances of class <tt>Transition</tt>.
     * @see Transition
     */
    @Override
    public Set<Transition> delta(final State state, final Object label) {
        return find(this.transitions, state, label);
    }

    /**
     * Returns the set of all transitions of this automaton starting from a
     * given set of state and labelled b a given label.
     * @param state
     *            a set of states of this automaton.
     * @param label
     *            a label used in this automaton.
     * @return the set of all transitions of this automaton starting from state
     *         <tt>state</tt> and labelled by <tt>label</tt>. Objects which are
     *         contained in this set are instances of class <tt>Transition</tt>.
     * @see Transition
     */
    public final Set<Transition>
            delta(final Set<State> set, final Object label) {
        Set<Transition> ret = new HashSet<Transition>();
        Iterator<State> i = set.iterator();
        while (i.hasNext()) {
            ret.addAll(delta(i.next(), label));
        }
        return ret;
    }

    /**
     * Returns the set of all transitions from state <code>from</code> to state
     * <code>to</code>.
     * @param from
     *            starting state
     * @param to
     *            ending state
     * @return a Set of Transition objects
     */
    @Override
    public Set<Transition> deltaFrom(final State from, final State to) {
        Set<Transition> t = delta(from);
        for (Iterator<Transition> i = t.iterator(); i.hasNext();) {
            Transition tr = i.next();
            if (!to.equals(tr.end())) {
                i.remove();
            }
        }
        return t;
    }

    /**
     * Return all transitions from a State
     * @param state
     *            start state
     * @return a new Set of transitions (maybe empty)
     */
    @Override
    public Set<Transition> delta(final State state) {
        Set<Transition> s = new HashSet<Transition>();
        Iterator<Object> alphit = alphabet().iterator();
        while (alphit.hasNext()) {
            s.addAll(delta(state, alphit.next()));
        }
        return s;
    }

    /**
     * Returns all transitions from a given set of states.
     * @param s
     *            a Set of State objects
     * @return a Set of Transition objects
     */
    @Override
    public Set<Transition> delta(final Set<State> s) {
        Set<Transition> ds = new HashSet<Transition>();
        Iterator<State> i = s.iterator();
        while (i.hasNext()) {
            ds.addAll(delta(i.next()));
        }
        return ds;
    }

    /**
     * Return a mapping from couples (q,q') of states to all (q,l,q')
     * transitions from q to q'
     * @return a Map
     */
    public final Map<Couple, Set<Transition>> couples() {
        // loop on transition map keys
        Iterator<Map.Entry<Key, Set<Transition>>> it = this.transitions
                .entrySet().iterator();
        Map<Couple, Set<Transition>> ret = new HashMap<Couple, Set<Transition>>();
        while (it.hasNext()) {
            Map.Entry<Key, Set<Transition>> e = it.next();
            // get start and end state
            State st = e.getKey().s;
            Iterator<Transition> trans = e.getValue().iterator();
            while (trans.hasNext()) {
                Transition tr = trans.next();
                State nd = tr.end();
                Couple cpl = new Couple(st, nd);
                Set<Transition> s = ret.get(cpl);
                if (s == null) {
                    s = new HashSet<Transition>();
                }
                s.add(tr);
                ret.put(cpl, s);
            }
        }
        return ret;
    }

    /**
     * Returns the set of all transitions of the reverse of this automaton
     * @return the set of all transitions of the reverse of this automaton. A
     *         reverse of an automaton <em>A = (X , Q , I , T , D)</em> is the
     *         automaton <em>A' = (X , Q , T , I , D')</em> where <em>D'</em> is
     *         the set <em>{ (q , l , q') | (q' , l , q) in D}</em>. Objects
     *         which are contained in this set are instances of class
     *         <tt>Transition</tt>.
     * @see Transition
     */
    @Override
    public Set<Transition> deltaMinusOne(final State state, final Object label) {
        return find(this.reverse, state, label);
    }

    /**
     * Adds a new transition in this automaton if it is a new transition for
     * this automaton. The parameter is considered as a new transition if there
     * is no transition in this automaton which is equal to the parameter in the
     * sense of method <tt>equals</tt> of class <tt>Transition</tt>.
     * @param transition
     *            the transition to add.
     * @throws NoSuchStateException
     *             if <tt>transition</tt> is <tt>null</<tt>
     * or if <tt>transition</tt> = <em>(q , l , q')</em> and <em>q</em> or
     *             <em>q'</em> does not belong to <em>Q</em> the set of the
     *             states of this automaton.
     */
    @Override
    public void addTransition(final Transition transition)
            throws NoSuchStateException {
        if (!this.states.contains(transition.start())
                || !this.states.contains(transition.end())) {
            throw new NoSuchStateException();
        }
        if (!this.alphabet.contains(transition.label())) {
            this.alphabet.add(transition.label());
        }
        add(this.transitions, transition);
        add(this.reverse, transition.getReversed());
    }

    /**
     * the project method keeps from the Automaton only the transitions labelled
     * with the letters contained in the set alph, effectively computing a
     * projection on this alphabet.
     * @param alph
     *            the alphabet to project on
     */
    public final void projectOn(final Set<Object> alph) {
        // remove unwanted transitions from ret
        Iterator<Map.Entry<Key, Set<Transition>>> trans = this.transitions
                .entrySet().iterator();
        Set<Transition> newtrans = new HashSet<Transition>();
        while (trans.hasNext()) {
            Map.Entry<Key, Set<Transition>> entry = trans.next();
            Key k = entry.getKey();
            Iterator<Transition> tit = entry.getValue().iterator();
            while (tit.hasNext()) {
                Transition tr = tit.next();
                if (!alph.contains(k.l)) {
                    // create epsilon transition
                    newtrans.add(new Transition(k.s, null, tr.end()));
                    // remove transtion
                    tit.remove();
                }
            }
        }
        // add newly created transitions
        if (!newtrans.isEmpty()) {
            Iterator<Transition> it = newtrans.iterator();
            while (trans.hasNext()) {
                Transition tr = it.next();
                add(this.transitions, tr);
                add(this.reverse, tr.getReversed());
            }
        }
        // remove alphabet
        this.alphabet.retainAll(alph);
    }

    /**
     * returns a textual representation of this automaton.
     * @return a textual representation of this automaton based on the converter
     *         <tt>toAscii</tt>.
     * @see net.jautomata.rationals.converters.toAscii
     */
    @Override
    public final String toString() {
        return new net.jautomata.rationals.converters.toAscii().toString(this);
    }

    /**
     * returns a copy of this automaton.
     * @return a copy of this automaton with new instances of states and
     *         transitions.
     */
    @Override
    public Object clone() {
        Automaton b;
        b = new Automaton();
        Map<State, State> map = new HashMap();
        Iterator<State> it = states().iterator();
        while (it.hasNext()) {
            State e = it.next();
            map.put(e, b.addState(e.isInitial(), e.isTerminal()));
        }
        Iterator<Transition> it2 = delta().iterator();
        while (it2.hasNext()) {
            Transition t = it2.next();
            try {
                b.addTransition(new Transition(map.get(t.start()), t.label(),
                        map.get(t.end())));
            } catch (NoSuchStateException x) {
                System.err.println(x.getMessage());
                x.printStackTrace();
            }
        }
        return b;
    }

    protected class Key {

        private State s;
        private Object l;

        protected Key(final State s, final Object l) {
            this.s = s;
            this.l = l;
        }

        @Override
        public final boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            try {
                Key t = (Key) o;
                boolean ret = (this.l == null ? t.l == null : this.l
                        .equals(t.l))
                        && (this.s == null ? t.s == null : this.s.equals(t.s));
                return ret;
            } catch (ClassCastException x) {
                return false;
            }
        }

        @Override
        public final int hashCode() {
            int x, y;
            if (this.s == null) {
                x = 0;
            } else {
                x = this.s.hashCode();
            }
            if (this.l == null) {
                y = 0;
            } else {
                y = this.l.hashCode();
            }
            return y << 16 | x;
            // return new java.awt.Point(x, y).hashCode();
        }
    }

    /**
     * Returns true if this automaton accepts given word -- ie. sequence of
     * letters. Note that this method accepts words with letters not in this
     * automaton's alphabet, effectively recognizing all words from any alphabet
     * projected to this alphabet.
     * <p>
     * If you need standard recognition, use
     * @see{accept(java.util.List) .
     * @param word
     * @return
     */
    public final boolean prefixProjection(final List<Object> word) {
        Set<State> s = stepsProject(word);
        return !s.isEmpty();
    }

    /**
     * Return the set of steps this automaton will be in after reading word.
     * Note this method skips letters not in alphabet instead of rejecting them.
     * @param l
     * @return
     */
    public final Set<State> stepsProject(final List<Object> word) {
        Set<State> s = initials();
        Iterator<Object> it = word.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!this.alphabet.contains(o)) {
                continue;
            }
            s = step(s, o);
            if (s.isEmpty()) {
                return s;
            }
        }
        return s;
    }

    /*
     * (non-Javadoc)
     * @see rationals.Acceptor#accept(java.util.List)
     */
    @Override
    public final boolean accept(final List<Object> word) {
        Set<State> s = TransformationsToolBox.epsilonClosure(steps(word), this);
        s.retainAll(terminals());
        return !s.isEmpty();
    }

    /**
     * Return true if this automaton can accept the given word starting from
     * given set. <em>Note</em> The ending state(s) need not be terminal for
     * this method to return true.
     * @param state
     *            a starting state
     * @param word
     *            a List of objects in this automaton's alphabet
     * @return true if there exists a path labelled by word from s to at least
     *         one other state in this automaton.
     */
    public final boolean accept(final State state, final List<Object> word) {
        Set<State> s = this.stateFactory.stateSet();
        s.add(state);
        return !steps(s, word).isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see rationals.Acceptor#steps(java.util.List)
     */
    @Override
    public final Set<State> steps(final List<Object> word) {
        Set<State> s = TransformationsToolBox.epsilonClosure(initials(), this);
        return steps(s, word);
    }

    /**
     * Return the set of states this automaton will be in after reading the word
     * from start states s.
     * @param s
     *            the set of starting states
     * @param word
     *            the word to read.
     * @return the set of reached states.
     */
    @Override
    public final Set<State> steps(Set<State> s, final List<Object> word) {
        Iterator<Object> it = word.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            s = step(s, o);
            if (s.isEmpty()) {
                return s;
            }
        }
        return s;
    }

    /**
     * Return the set of states this automaton will be in after reading the word
     * from singler start state s.
     * @param st
     *            the starting state
     * @param word
     *            the word to read.
     * @return the set of reached states.
     */
    @Override
    public final Set<State> steps(final State st, final List<Object> word) {
        Set<State> s = this.stateFactory.stateSet();
        s.add(st);
        Iterator<Object> it = word.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            s = step(s, o);
            if (s.isEmpty()) {
                return s;
            }
        }
        return s;
    }

    /**
     * Return the list of set of states this automaton will be in after reading
     * word from start state. Is start state is null, assume reading from
     * initials().
     * @param word
     * @param start
     */
    @Override
    public final List<Set<State>> traceStates(final List<Object> word,
            final State start) {
        List<Set<State>> ret = new ArrayList<Set<State>>();
        Set<State> s = null;
        if (start != null) {
            s = this.stateFactory.stateSet();
            s.add(start);
        } else {
            s = initials();
        }
        Iterator<Object> it = word.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!this.alphabet.contains(o)) {
                continue;
            }
            s = step(s, o);
            ret.add(s);
            if (s.isEmpty()) {
                return null;
            }
        }
        return ret;
    }

    /**
     * Returns the size of the longest word recognized by this automaton where
     * letters not belonging to its alphabet are ignored.
     * @param word
     * @return
     */
    public final int longestPrefixWithProjection(final List<Object> word) {
        int lret = 0;
        Set<State> s = initials();
        Iterator<Object> it = word.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if ((o == null) || !this.alphabet.contains(o)) {
                lret++;
                continue;
            }
            s = step(s, o);
            if (s.isEmpty()) {
                break;
            }
            lret++;
        }
        return lret;
    }

    /**
     * Return the set of states accessible in one transition from given set of
     * states s and letter o.
     * @param s
     * @param o
     * @return
     */
    @Override
    public final Set<State> step(final Set<State> s, final Object o) {
        Set<State> ns = this.stateFactory.stateSet();
        Set<State> ec = TransformationsToolBox.epsilonClosure(s, this);
        Iterator<State> it = ec.iterator();
        while (it.hasNext()) {
            State st = it.next();
            Iterator<Transition> it2 = delta(st).iterator();
            while (it2.hasNext()) {
                Transition tr = it2.next();
                if (tr.label() != null && tr.label().equals(o)) {
                    ns.add(tr.end());
                }
            }
        }
        return ns;
    }

    /**
     * @param tr
     * @param msg
     */
    public final void
            updateTransitionWith(final Transition tr, final Object msg) {
        Object lbl = tr.label();
        this.alphabet.remove(lbl);
        this.alphabet.add(msg);
        /* update transition map */
        Key k = new Key(tr.start(), lbl);
        Set<Transition> s = this.transitions.remove(k);
        if (s != null) {
            this.transitions.put(new Key(tr.start(), msg), s);
        }
        /* update reverse map */
        k = new Key(tr.end(), lbl);
        s = this.reverse.remove(k);
        if (s != null) {
            this.reverse.put(new Key(tr.end(), msg), s);
        }
        tr.setLabel(msg);
    }

    /**
     * @param st
     * @return
     */
    @Override
    public Set<Transition> deltaMinusOne(final State st) {
        Set<Transition> s = new HashSet<Transition>();
        Iterator<Object> alphit = alphabet().iterator();
        while (alphit.hasNext()) {
            s.addAll(deltaMinusOne(st, alphit.next()));
        }
        return s;
    }

    /**
     * Enumerate all prefix of words of length lower or equal than i in this
     * automaton.
     * @param i
     *            maximal length of words.
     * @return a Set of List of Object
     */
    public final Set<List<Object>> enumerate(final int ln) {
        Set<List<Object>> ret = new HashSet<List<Object>>();
        class EnumState {

            /**
             * @param s
             * @param list
             */
            public EnumState(final State s, final List<Object> list) {
                this.st = s;
                this.word = new ArrayList<Object>(list);
            }

            State st;
            List word;
        }
        LinkedList<EnumState> ll = new LinkedList<EnumState>();
        List<Object> cur = new ArrayList<Object>();
        for (Iterator<State> i = this.initials.iterator(); i.hasNext();) {
            State s = i.next();
            if (s.isTerminal()) {
                ret.add(new ArrayList<Object>());
            }
            ll.add(new EnumState(s, cur));
        }

        do {
            EnumState st = ll.removeFirst();
            Set<Transition> trs = delta(st.st);
            List<Object> word = st.word;
            for (Iterator<Transition> k = trs.iterator(); k.hasNext();) {
                Transition tr = k.next();
                word.add(tr.label());
                if (word.size() <= ln) {
                    EnumState en = new EnumState(tr.end(), word);
                    ll.add(en);
                    ret.add(en.word);
                }
                word.remove(word.size() - 1);
            }
        } while (!ll.isEmpty());
        return ret;
    }

    /**
     * @param sched
     * @return
     */
    public final void schedule(final Scheduler sched) {
        Iterator<State> it = this.states.iterator();

        // Pour chaque état de l'automate
        while (it.hasNext()) {
            State s = it.next();
            // Pour chaque classe d'équivalence
            for (int i = 0; i < sched.getEqClasses().size(); i++) {
                // temp stocke les transitions dont le label est dans la classe
                // d'équivalence
                ArrayList<Transition> temp = new ArrayList<Transition>();
                Iterator<Transition> it2 = delta(s).iterator();
                // Pour chaque transition partant de l'état
                while (it2.hasNext()) {
                    Transition t = it2.next();
                    // Pour chaque action de la classe d'équivalence
                    for (int j = 0; j < sched.getEqClasses().get(i)
                            .getEqActions().size(); j++) {
                        String ac = sched.getEqClasses().get(i).getEqActions()
                                .get(j);
                        if (t.label() instanceof PSymbol) {
                            if (ac.equals(((PSymbol) t.label()).getLabel())) {
                                temp.add(t);
                            }
                        } else {
                            if (ac.equals(t.label())) {
                                temp.add(t);
                            }
                        }
                    }

                }

                // On modifie la probabilité et le label de chaque transition
                // récupérée
                String newLabel = "";
                // Création du nouveau label (concaténation de ceux de chaque
                // transition)
                for (int j = 0; j < sched.getEqClasses().get(i).getEqActions()
                        .size(); j++) {
                    newLabel += sched.getEqClasses().get(i).getEqActions()
                            .get(j);
                }
                // Ajout du nouveau label à l'alphabet de l'automate
                this.alphabet.add(newLabel);

                // Modification des transitions
                for (int j = 0; j < temp.size(); j++) {
                    if (temp.get(j).label() instanceof PSymbol) {
                        double p = ((PSymbol) temp.get(j).label())
                                .getProbability();
                        ((PSymbol) temp.get(j).label()).setLabel(newLabel);
                        ((PSymbol) temp.get(j).label())
                                .setProbability((double) p / temp.size());
                    } else {
                        temp.get(j)
                                .setLabel(
                                        new PSymbol(newLabel, (double) 1
                                                / temp.size()));
                    }
                }
            }

        }
    }

    /**
     * On considère ici que le non déterminisme se fait au début de l'automate
     * sous forme d'arbre (pas de boucles). La fonction parcours tous les
     * chemins possibles jusqu'à éliminer tout le non déterminisme et renvoit
     * une liste d'automates probabilistes correspondant à chacun de ces
     * chemins.
     * @return la liste d'automates probabilistes
     */
    public final ArrayList<Automaton> scheduleNonDet() {
        ArrayList<Automaton> autos = new ArrayList<Automaton>();
        // La liste des états racines des sous automates probabilistes
        ArrayList<State> roots = new ArrayList<State>();
        // On récupère la liste des états racines
        Iterator<State> inits = this.initials().iterator();
        while (inits.hasNext()) {
            State s = (State) inits.next();
            roots = getNonDetRoots(roots, s);
        }
        // On construit pour chaque états le sous automate partant de cet état
        for (int i = 0; i < roots.size(); i++) {
            // On construit le sous automate partant de l'état récupéré
            Automaton subAuto = getSubAutomaton(roots.get(i));
            Iterator<Object> it = this.alphabet.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (!subAuto.alphabet().contains(o) && o instanceof PSymbol) {
                    subAuto.alphabet().add(o);
                }

            }
            autos.add(subAuto);
        }
        return autos;
    }

    /**
     * Construit un sous automate partant de l'état de départ passé en paramètre
     * @param start
     * @param init
     * @param auto
     * @return
     */
    public final Automaton getSubAutomaton(final State start) {
        Automaton auto = new Automaton();
        // On récupère la liste des états du sous automates
        ArrayList<State> subStates = new ArrayList<State>();
        subStates = this.getStatesFrom(start, subStates);
        // On élimine les éventuels doublons de la liste
        Set<State> set = new HashSet<State>();
        set.addAll(subStates);
        subStates = new ArrayList<State>(set);

        // On fait un copie de l'automate en ne gardant que les transitions
        // partant des états de la liste
        Map<State, State> map = new HashMap();
        for (int j = 0; j < subStates.size(); j++) {
            if (subStates.get(j).equals(start)) {
                map.put(subStates.get(j),
                        auto.addState(true, subStates.get(j).isTerminal()));
            } else {
                map.put(subStates.get(j), auto.addState(subStates.get(j)
                        .isInitial(), subStates.get(j).isTerminal()));
            }
        }

        for (int j = 0; j < subStates.size(); j++) {

            Iterator<Transition> it2 = delta(subStates.get(j)).iterator();
            while (it2.hasNext()) {
                Transition t = it2.next();
                try {
                    auto.addTransition(new Transition(map.get(t.start()), t
                            .label(), map.get(t.end())));
                } catch (NoSuchStateException x) {
                    x.printStackTrace();
                }
            }
        }

        return auto;

    }

    public final ArrayList<State> getStatesFrom(final State start,
            ArrayList<State> out) {
        out.add(start);
        Set<Transition> setTr = this.delta(start);
        Iterator<Transition> it = setTr.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            if (!t.end().isTerminal()) {
                out = getStatesFrom(t.end(), out);
            } else {
                out.add(t.end());
            }
        }

        return out;
    }

    /**
     * Récupère tous les états de l'automate à partir desquels l'automate
     * devient déterministe. Parcours les transitions de l'automate partant de
     * l'état passé en paramètre. Si les transitions sont probabilistes alors on
     * ajoutre l'état à la liste de sortie, sinon on réapplique la fonction aux
     * états d'arrivés de chaque transition.
     * @param roots
     * @param start
     * @return
     */
    public final ArrayList<State> getNonDetRoots(ArrayList<State> roots,
            final State start) {
        Set<Transition> transitions = this.delta(start);
        Iterator<Transition> it = transitions.iterator();
        while (it.hasNext()) {
            Transition t = (Transition) it.next();
            if (t.label() instanceof PSymbol) {
                roots.add(start);
                return roots;
            } else {
                roots = getNonDetRoots(roots, t.end());
            }
        }
        return roots;
    }
}
