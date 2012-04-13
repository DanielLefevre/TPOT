package net.jautomata.rationals.transformations ;

import net.jautomata.rationals.Automaton;

public interface UnaryTransformation {
  public Automaton transform(Automaton a) ;
}
