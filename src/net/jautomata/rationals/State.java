package net.jautomata.rationals;


/** 
 * Interface for State objects
 * 
 * This class defines the notion of state of an automaton. 
 * @author yroos@lifl.fr
 * @version 1.0
 * @see Automaton
 * @see StateFactory
*/
public interface State {

    /** Sets this state as initial or not depending on the value of 
     *  parameter.
     *  @param initial if true, sets this state as initial; otherwise 
     *  sets this state as non initial.
     */
    public void setInitial(boolean initial);
    
    /** Sets this state as terminal or not depending on the value of 
     *  parameter.
     *  @param terminal if true, sets this state as terminal; otherwise 
     *  sets this state as non terminal.
     */  
    public void setTerminal(boolean terminal);

    /** Determines if this state is initial.
     *  @return true iff this state is initial.
     */
    public boolean isInitial();
    
    /** Determines if this state is terminal.
     *  @return true iff this state is terminal.
     */
    public boolean isTerminal();
    
    /** returns a textual representation of this state.
     *  @return a textual representation of this state.
     */
    public String toString();
}
