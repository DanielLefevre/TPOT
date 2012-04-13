package net.jautomata.rationals.transformations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.NoSuchStateException;
import net.jautomata.rationals.State;
import net.jautomata.rationals.Transition;


/**
 * Determinization of an automaton.
 * 
 * @author yroos
 * @version $Id: ToDFA.java 932 2005-04-12 07:13:26Z bailly $
 */
public class ToDFA implements UnaryTransformation {

    public Automaton transform(Automaton a) {
        a = new EpsilonTransitionRemover().transform(a);
        Automaton b = new Automaton();
        Map map = new HashMap();
        LinkedList l = new LinkedList();
        Set done = new HashSet();
        Set e = a.initials();
        boolean t = TransformationsToolBox.containsATerminalState(e);
        map.put(e, b.addState(true, t));
        l.add(e);
        while (!l.isEmpty()) {
            Set e1 = (Set) l.removeFirst();
            done.add(e1);
            State ep1 = (State) map.get(e1);
            Iterator j = a.alphabet().iterator();
            Object label = null;
            while (j.hasNext()) {
                label = j.next();
                Iterator i = e1.iterator();
                Set e2 = a.getStateFactory().stateSet();
                while (i.hasNext()) {
                    Iterator k = a.delta((State) i.next(), label).iterator();
                    while (k.hasNext()) {
                        e2.add(((Transition) k.next()).end());
                    }
                }
                State ep2;
                if (!e2.isEmpty()) {
                    if (!map.containsKey(e2)) {
                        t = TransformationsToolBox.containsATerminalState(e2);
                        map.put(e2, b.addState(false, t));
                    }
                    ep2 = (State) map.get(e2);
                    try {
                        b.addTransition(new Transition(ep1, label, ep2));
                    } catch (NoSuchStateException x) {
                    }
                    if (!done.contains(e2))
                        l.add(e2);
                }
            }
        }
        return b;
    }
}
