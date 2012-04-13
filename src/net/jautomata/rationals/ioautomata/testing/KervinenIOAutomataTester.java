/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 18 avr. 2005
 *
 */
package rationals.ioautomata.testing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOStateMachine;
import rationals.ioautomata.IOTransition;

/**
 * Base class for various implementations of player algorithms from 
 * Kervinen and Virolainen.
 * 
 * @author nono
 * @version $Id$
 */
public abstract class KervinenIOAutomataTester extends AbstractIOTester {

    /* fired transitions */
    private Set fired = new HashSet();
    
    /* the specification */
    private IOAutomaton spec;
    
    /* the implementation */
    private IOStateMachine machine;
    
    /* store currently evaluated best actions for each state */
    private Map /* < Set < State >, Move > */ action = new HashMap();
    
    /* (non-Javadoc)
     * @see rationals.ioautomata.testing.IOAutomataTester#test(rationals.ioautomata.IOStateMachine, rationals.ioautomata.IOAutomaton)
     */
    public void test(IOStateMachine impl, IOAutomaton spec)
            throws IOTestFailure {
        this.spec = spec;
        this.machine = impl;
    }

    
    /**
     * Returns evaluation for given state at given depth.
     * 
     * @param state the state to analyze. Assumes this is epsilon closed.
     * @param depth the depth limiting recursive search.
     * @return a value denoting interest of state.
     */
    public double playerEval(Set state,int depth,Set covered){
        Set /* < Transition > */ cov = new HashSet(covered);
        /* split transitions into input/output sets */
        Set itrs = spec.delta(state);
        Set otrs = new HashSet(itrs);
        filterInputs(itrs);
        filterOutputs(otrs);
        /* basic tests */
        if(depth == 0 || (itrs.size() == 0 && otrs.size() == 0))
            return 0;
        Map /* < Transition, Double > */ desirability = new HashMap();
        /* inputs evaluation */
        double maxi = Double.MIN_VALUE;
        IOTransition maxt = null;
        for(Iterator i = itrs.iterator();i.hasNext();) {
            IOTransition tr = (IOTransition)i.next();
            Set end = spec.step(state,tr.label());
            double e;
            if(cov.contains(tr)) 
                e = eval(state,covered) + playerEval(end,depth-1,cov);
            else {
                /* update cov */
                cov.add(tr);
                e = eval(state,covered) + playerEval(end,depth-1,cov);
                cov.remove(tr);
            }
            if(e > maxi) {
                maxi = e;
                maxt = tr;
            }
        }
        /* outputs evaluation */
        for(Iterator i = itrs.iterator();i.hasNext();) {
            IOTransition tr = (IOTransition)i.next();
            Set end = spec.step(state,tr.label());
            double e;
            if(cov.contains(tr)) 
                e = eval(state,covered) + playerEval(end,depth-1,cov);
            else {
                /* update cov */
                cov.add(tr);
                e = eval(state,covered) + playerEval(end,depth-1,cov);
                cov.remove(tr);
            }
            desirability.put(tr,new Double(e));
        }
        /* probability of outputs */
        Map probs = probOutput(otrs,cov,desirability);
        double ovalue = 0;
        for(Iterator i = probs.entrySet().iterator();i.hasNext();) {
            Map.Entry en = (Map.Entry)i.next();
            IOTransition tr = (IOTransition)en.getKey();
            Double pr = (Double) en.getValue();
            Double des = (Double) desirability.get(tr);
            ovalue += pr.doubleValue() * des.doubleValue();
        }
        /* select best choice */
        if(otrs.isEmpty() || (!itrs.isEmpty() && maxi > ovalue)) {
            action.put(state,new Move(((IOTransition.IOLetter)maxt.label()).label));
            return maxi;
        } else {
            action.put(state, Move.out);
            return ovalue;
        }
    }


    /**
     * @param state current Set of State objects.
     * @param covered current Set of - covered - Transition objects.
     * @return
     */
    protected abstract double eval(Set state, Set covered) ;


    /**
     * @param otrs
     * @param cov
     * @param desirability
     * @return
     */
    protected abstract Map probOutput(Set otrs, Set cov, Map desirability) ;
    
    
}
