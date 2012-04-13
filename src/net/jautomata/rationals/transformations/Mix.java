package net.jautomata.rationals.transformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.DefaultSynchronization;
import net.jautomata.rationals.NoSuchStateException;
import net.jautomata.rationals.State;
import net.jautomata.rationals.Synchronization;
import net.jautomata.rationals.Transition;


/**
 * This class implements the mix - ie: synchronization product - operator
 * between two automatas.
 * <ul>
 * <li>C = A mix B</li>
 * <li>S(C) = { (a,b) | a in S(A) and b in S(B) }</li>
 * <li>S0(C) = (S0(A),SO(B))</li>
 * <li>T(C) = { (a,b) | a in T(A) and b in T(B) }</li>
 * <li>D(C) = { ((s1a,s1b),a,(s2a,s2b)) | exists (s1a,a,s2a) in D(A) and exists
 * (s1b,a,s2b) in D(b) } U { ((s1a,s1b),a,(s1a,s2b)) | a not in S(A) and exists
 * (s1b,a,s2b) in D(b) } U { ((s1a,s1b),a,(s2a,s1b)) | a not in S(B) and exists
 * (s1a,a,s2a) in D(a) }</li>
 * </ul>
 * 
 * @author Arnaud Bailly
 * @version 22032002
 */
public class Mix implements BinaryTransformation {

    private Synchronization synchronization;

    /**
     * Compute mix of two automata using default synchronization scheme which is
     * the equality of labels.
     * 
     * @see net.jautomata.rationals.DefaultSynchronization
     * @see net.jautomata.rationals.Synchronization
     */
    public Mix() {
        this.synchronization = new DefaultSynchronization();
    }

    /**
     * Compute mix of two automata using given synchronization scheme.
     * 
     * @param synch
     *            a Synchronization object. Must not be null.
     */
    public Mix(Synchronization synch) {
        this.synchronization = synch;
    }

    /*
     *  (non-Javadoc)
     * @see rationals.transformations.BinaryTransformation#transform(rationals.Automaton, rationals.Automaton)
     */
    public Automaton transform(Automaton a, Automaton b) {
        Automaton ret = new Automaton();

        // we will continue normaly if no probabilistic automaton is involved

        Set alph = synchronization.synchronizable(a.alphabet(), b.alphabet());
        /* check alphabets */
        Map<StatesCouple, State> amap = new HashMap<StatesCouple, State>();
        Map<StatesCouple, State> bmap = new HashMap<StatesCouple, State>();
        List<StatesCouple> todo = new ArrayList();
        Set<StatesCouple> done = new HashSet();
        Set<State> as = TransformationsToolBox.epsilonClosure(a.initials(), a);
        Set<State> bs = TransformationsToolBox.epsilonClosure(b.initials(), b);
        State from = ret.addState(true, TransformationsToolBox
                .containsATerminalState(as)
                && TransformationsToolBox.containsATerminalState(bs));
        StatesCouple statesCouple = new StatesCouple(as, bs);
        amap.put(statesCouple, from);
        todo.add(statesCouple);
        do {
            StatesCouple couple = todo.remove(0);
            from = amap.get(couple);
            if (done.contains(couple))
                continue;
            done.add(couple);
            /* get transition sets */
            Map<Object, Set< State>> tam =
                    TransformationsToolBox.mapAlphabet(a.delta(couple.sa), a);
            Map<Object, Set< State>> tbm =
                    TransformationsToolBox.mapAlphabet(b.delta(couple.sb), b);
            /* create label map for synchronized trans */
            Map<Object, StatesCouple> tcm = new HashMap< Object, StatesCouple >();

            /* unsynchronizable transitions in A */
            for (Iterator<Map.Entry<Object,Set<State>>> i =
                    tam.entrySet().iterator(); i.hasNext();) {
                Map.Entry<Object,Set<State>> me = i.next();
                Object l = me.getKey();
                as = me.getValue();
                if (!alph.contains(l)) {
                    Set asc = TransformationsToolBox.epsilonClosure(as, a);
                    tcm.put(l, statesCouple = new StatesCouple(asc, couple.sb));
                    State to = (State) amap.get(statesCouple);
                    if (to == null) {
                        to = ret.addState(false, TransformationsToolBox
                                .containsATerminalState(statesCouple.sa)
                                && TransformationsToolBox
                                        .containsATerminalState(statesCouple.sb));
                        amap.put(statesCouple, to);
                    }
                    todo.add(statesCouple);
                    i.remove();
                }
            }
            /* unsynchronizable transition(s) in B */
            for (Iterator i = tbm.entrySet().iterator(); i.hasNext();) {
                Map.Entry me = (Map.Entry) i.next();
                Object l = me.getKey();
                bs = (Set) me.getValue();
                if (!alph.contains(l)) {
                    Set bsc = TransformationsToolBox.epsilonClosure(bs, b);
                    tcm.put(l, statesCouple = new StatesCouple(couple.sa, bsc));
                    State to = (State) amap.get(statesCouple);
                    if (to == null) {
                        to = ret.addState(false, TransformationsToolBox
                                .containsATerminalState(statesCouple.sa)
                                && TransformationsToolBox
                                        .containsATerminalState(statesCouple.sb));
                        amap.put(statesCouple, to);
                    }
                    todo.add(statesCouple);
                    i.remove();
                }
            }
            /*
             * there remains in tam and tbm only possibly synchronizable
             * transitions
             */
            for (Iterator i = tam.entrySet().iterator(); i.hasNext();) {
                Map.Entry me = (Map.Entry) i.next();
                Object l = me.getKey();
                as = (Set) me.getValue();
                for (Iterator j = tbm.entrySet().iterator(); j.hasNext();) {
                    Map.Entry mbe = (Map.Entry) j.next();
                    Object k = mbe.getKey();
                    bs = (Set) mbe.getValue();
                    Object sy = synchronization.synchronize(l, k);
                    if (sy != null) {
                        Set asc = TransformationsToolBox.epsilonClosure(as, a);
                        Set bsc = TransformationsToolBox.epsilonClosure(bs, b);
                        tcm.put(sy, statesCouple = new StatesCouple(asc, bsc));
                        State to = (State) amap.get(statesCouple);
                        if (to == null) {
                            to = ret.addState(false, TransformationsToolBox
                                    .containsATerminalState(statesCouple.sa)
                                    && TransformationsToolBox
                                            .containsATerminalState(statesCouple.sb));
                            amap.put(statesCouple, to);
                        }
                        todo.add(statesCouple);
                    }
                }
            }
            /*
             * 
             * create new transitions in return automaton, update maps
             */
            for (Iterator i = tcm.entrySet().iterator(); i.hasNext();) {
                Map.Entry me = (Map.Entry) i.next();
                Object l = me.getKey();
                statesCouple = (StatesCouple) me.getValue();
                State to = (State) amap.get(statesCouple);
                if (to == null) {
                    to = ret.addState(false, TransformationsToolBox
                            .containsATerminalState(statesCouple.sa)
                            && TransformationsToolBox
                                    .containsATerminalState(statesCouple.sb));
                    amap.put(statesCouple, to);
                }
                try {
                    ret.addTransition(new Transition(from, l, to));
                } catch (NoSuchStateException e) {
                }
            }
        } while (!todo.isEmpty());
        return ret;
    }
}