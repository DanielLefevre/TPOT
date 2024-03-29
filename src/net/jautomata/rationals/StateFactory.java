package net.jautomata.rationals;

import java.util.Set;

/**
* This class is used by Automaton objects to create new states on 
* A user can implement its own version of StateFactory by providing
* an implementation for createState 
*
* @author Arnaud.Bailly - bailly@lifl.fr
* @version $Id: StateFactory.java 1147 2005-11-22 18:43:10Z nono $
*/
public interface StateFactory {

    /**
     * 
     * @param initial
     * @param terminal
     * @return
     */
    public abstract State create(boolean initial, boolean terminal);
	
	/**
	 * Return a new empty set that can contains State instances 
	 * created by this factory.
	 * This method is provided for optimisation purposes so that 
	 * more efficient implementations than plain sets can be used
	 * for handling sets of states.
	 * 
	 * @return an - opaque - implementation of Set.
	 */
	public Set stateSet();
	
	/**
	 * Returns a new Set which is a copy of <code>s</code>. The given
	 * set s must have been created through <strong>this</strong> StateFactory object
	 * to ensure consistent behavior.
	 * 
	 * @param s a Set 
	 * @return a shallow copy of <code>s</code>
	 */
	public Set stateSet(Set s);
	
	/**
	 * Returns a new StateFactory object which is the same as this 
	 * StateFactory.
	 * 
	 * @return an initialized StateFactory.
	 */
	public Object clone();

    /**
     * @param automaton
     */
    public abstract void setAutomaton(Automaton automaton);
	
}
///*
// * $Log: StateFactory.java,v $
// * Revision 1.3  2004/07/20 13:21:25  bonte
// * *** empty log message ***
// *
// */=======
///*
// * $Log: StateFactory.java,v $
// * Revision 1.3  2004/07/20 13:21:25  bonte
// * *** empty log message ***
// *
// * Revision 1.2  2004/07/19 06:39:02  bailly
// * made Automaton, State and Transition subclasses of Graph API
// * modified StateFactory API
// *
