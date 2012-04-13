package net.jautomata.rationals.transformations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.NoSuchStateException;
import net.jautomata.rationals.PSymbol;
import net.jautomata.rationals.Pair;
import net.jautomata.rationals.State;
import net.jautomata.rationals.Transition;

/**
 *
 * @author adeft
 */
public class SynchronizationProduct implements BinaryTransformation {

    public Automaton transform(Automaton a, Automaton b) {
        if (!(a.delta().iterator().next().label() instanceof PSymbol))
            return null;

        // b must be deterministic
        b = new ToDFA().transform(b);

        Automaton sync = new Automaton();

        Map<Pair<State,State>, State> syncStates = new HashMap<Pair<State, State>, State>();

        Queue<Pair<State,State>> queue = new LinkedList<Pair<State,State>>();

        Set<State> aStart = TransformationsToolBox.epsilonClosure(a.initials(), a);
        State bStart = b.initials().iterator().next(); // b is deterministic

        for (Iterator<State> it = aStart.iterator(); it.hasNext();) {
            State aState = it.next();
            State newState = sync.addState(aState.isInitial() && bStart.isInitial(),
                    aState.isTerminal() && bStart.isTerminal());
            Pair<State, State> myPair = new Pair<State, State>(aState,bStart);
            syncStates.put(myPair, newState);
            queue.add(myPair);
        }

        while (queue.peek() != null) {
            Pair<State, State> pair = queue.poll();

            Set<Transition> bDelta = b.delta(pair.second);

            // create map between labels and transitions
            //  each label will apear only once because "b" is determinstic
            Map<Object, Transition> bLabels= new HashMap<Object, Transition>();
            for (Iterator<Transition> it = bDelta.iterator(); it.hasNext();) {
                Transition bTransition = it.next();
                bLabels.put(bTransition.label(), bTransition);
            }

            // Find all future transitions
            Set aStates = a.getStateFactory().stateSet(); // see N.B. below
            // N.B. the set is not defined as Set<State> because the
            // epsilonClosure doesn't accept a Set<State> as parameter
            aStates.add(pair.first);
            aStates = TransformationsToolBox.epsilonClosure(aStates, a);
            Set<Transition> aDelta = a.delta(aStates);


            // add them to the synchronized automaton if possible
            for (Iterator<Transition> it = aDelta.iterator(); it.hasNext();) {
                Transition aTransition = it.next();
                PSymbol aLabel = (PSymbol) aTransition.label();
                Object label = aLabel.getLabel();

                // we add the tranzition only if b has it too
                if (bLabels.containsKey(label)) {
                    State aState = aTransition.end();
                    State bState = bLabels.get(label).end();

                    Pair newPair = new Pair<State, State>(aState, bState);

                    // add the new found state to sync if necessary
                    if (!syncStates.containsKey(newPair)) {
                        State newState = sync.addState(aState.isInitial() && bState.isInitial(),
                                aState.isTerminal() && bState.isTerminal());
                        syncStates.put(newPair, newState);
                        queue.add(newPair);
                    }

                    // add the probabilistic transition so sync
                    try {
                        sync.addTransition(new Transition(
                                syncStates.get(pair),
                                aLabel,
                                syncStates.get(newPair)
                                ));
                    } catch (NoSuchStateException ex) {
                        Logger.getLogger(SynchronizationProduct.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }

            }

        }
        return sync;
    }
}
