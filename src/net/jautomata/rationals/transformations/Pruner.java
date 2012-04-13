package net.jautomata.rationals.transformations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.NoSuchStateException;
import net.jautomata.rationals.State;
import net.jautomata.rationals.Transition;


/**
 * Removes states that neither accessible nor coaccessible.
 * 
 * @author nono
 * @version $Id: Pruner.java 932 2005-04-12 07:13:26Z bailly $
 */
public class Pruner implements UnaryTransformation {

  public Automaton transform(Automaton a) {
    Map conversion = new HashMap() ;
    Iterator i = a.accessibleAndCoAccessibleStates().iterator();
    Automaton b = new Automaton() ;
    while(i.hasNext()) {
      State e = (State) i.next() ;
      conversion.put(e , b.addState(e.isInitial() , e.isTerminal())) ;
    }
    i = a.delta().iterator();
    while(i.hasNext()) {
      Transition t = (Transition) i.next() ;
      State bs = (State) conversion.get(t.start()) ;
      State be = (State) conversion.get(t.end()) ;
      if(bs == null || be == null)
          continue;
      try {
        b.addTransition(new Transition(
          bs,
          t.label() ,
          be)) ;
      } catch (NoSuchStateException x) {}
    }
    return b ;
  }
}
  
