package net.jautomata.rationals.transformations;

import java.util.Iterator;
import java.util.Set;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.NoSuchStateException;
import net.jautomata.rationals.State;
import net.jautomata.rationals.Transition;


/**
 * Complete an Automaton by adding a sink state and needed transitions.
 * <p />
 * <ul>
 * <li>C = complete(A)</li>
 * <li>S(C) = S(A) U {sink}</li>
 * <li>S0(C) = S0(A)</li>
 * <li>T(C) = T(A)</li>
 * <li>D(C) = D(A) U { (s1,a,sink)) | not exists (s1,a,s2) in D(A) }</li>
 * </ul>
 * 
 * @author nono
 * @version $Id: SinkComplete.java 1181 2005-12-07 22:42:28Z nono $
 */
public class SinkComplete implements UnaryTransformation {
    
    /*
     *  (non-Javadoc)
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton transform(Automaton a) {
        Automaton b = (Automaton) a.clone();
        State hole = null;
        Set states = b.getStateFactory().stateSet();
        states.addAll(b.states());
        Iterator i = states.iterator();
        while (i.hasNext()) {
            State e = (State) i.next();
            Iterator j = b.alphabet().iterator();
            while (j.hasNext()) {
                Object label = j.next();
                if (b.delta(e, label).isEmpty()) {
                    if (hole == null)
                        hole = b.addState(false, false);
                    try {
                        b.addTransition(new Transition(e, label, hole));
                    } catch (NoSuchStateException x) {
                    }
                }
            }
        }
        if (!(hole == null)) {
            Iterator j = b.alphabet().iterator();
            while (j.hasNext()) {
                try {
                    b.addTransition(new Transition(hole, j.next(), hole));
                } catch (NoSuchStateException x) {
                }
            }
        }
        return b;
    }
}
