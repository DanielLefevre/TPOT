package net.jautomata.rationals.converters;

import java.awt.Graphics2D;
import java.util.Map;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.converters.algorithms.LayoutAlgorithm;


/**
 * This class is used to display an Automaton in a graphic
 * component
 * 
 * @author bailly
 * @version Jul 23, 2002
 * 
 */
public class WindowDisplayer implements GraphicsDisplayer {

	private Automaton automata;
	
	private LayoutAlgorithm algorithm;
		
	/**
	 * @see net.jautomata.rationals.converters.GraphicsDisplayer#redraw(Graphics2D)
	 */
	public void draw(Graphics2D gs) {
		Map m = algorithm.getState();
	}

	/**
	 * @see net.jautomata.rationals.converters.Displayer#setAutomaton(Automaton)
	 */
	public void setAutomaton(Automaton a) throws ConverterException {
		automata = a;		
	}

	/**
	 * @see net.jautomata.rationals.converters.Displayer#display()
	 */
	public void display() throws ConverterException {
	}

	/**
	 * Returns the automata.
	 * @return Automaton
	 */
	public Automaton getAutomata() {
		return automata;
	}

	/**
	 * Sets the automata.
	 * @param automata The automata to set
	 */
	public void setAutomata(Automaton automata) {
		this.automata = automata;
	}

	/**
	 * Returns the algorithm.
	 * @return LayoutAlgorithm
	 */
	public LayoutAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets the algorithm.
	 * @param algorithm The algorithm to set
	 */
	public void setAlgorithm(LayoutAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

}
