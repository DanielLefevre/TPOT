package net.jautomata.rationals.transformations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.NoSuchStateException;
import net.jautomata.rationals.PSymbol;
import net.jautomata.rationals.State;
import net.jautomata.rationals.Transition;

/**
 * This transformation takes an automaton that contains probabilistic symbols
 * and eliminates the probabilities. The result is a classic automaton.
 * @author adeft
 */
public class UnProb implements UnaryTransformation {

    public Automaton transform(Automaton a) {
        if (!(a.delta().iterator().next().label() instanceof PSymbol)) {
            return a;
        }

        Automaton auto = new Automaton();
        Map<State, State> states = new HashMap<State, State>();

        Iterator<Transition> it = a.delta().iterator();
        while (it.hasNext()) {
            // get next transition
            Transition t = it.next();
            Object label;
            if(t.label() instanceof PSymbol)
            	label = ((PSymbol) t.label()).getLabel();
            else
            	label = t.label();
            
            State start = states.get(t.start());
            // add start state if needed
            if (start == null) {
                start = auto.addState(t.start().isInitial(), t.start().isTerminal());
                states.put(t.start(), start);
            }
            State end = states.get(t.end());
            // add end state if needed
            if (end == null) {
                end = auto.addState(t.end().isInitial(), t.end().isTerminal());
                states.put(t.end(), end);
            }
            // finally add transition
            try {
                auto.addTransition(new Transition(start, label, end));
            } catch (NoSuchStateException ex) {
                Logger.getLogger(UnProb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return auto;
    }
}
