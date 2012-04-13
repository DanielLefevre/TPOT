package net.jautomata.rationals.transformations;


import net.jautomata.rationals.Automaton;

public class ToCanonicalRFSA implements UnaryTransformation {

  public Automaton transform(Automaton a) {
    Reverser r = new Reverser() ;
    ToC c = new ToC() ;
    return c.transform(r.transform(c.transform(r.transform(a)))) ;
  }
}
