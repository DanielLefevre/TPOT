package net.jautomata.rationals.transformations;

import net.jautomata.rationals.Automaton;

/**
 * A generic interface for binary operations between two automata.
 * 
 * @author nono
 * @version $Id: BinaryTransformation.java 927 2005-04-07 20:34:30Z bailly $
 */
public interface BinaryTransformation {
  public Automaton transform(Automaton a , Automaton b) ;
}
