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
 * Created on 16 avr. 2005
 *
 */
package rationals.ioautomata.testing;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOStateMachine;
import rationals.ioautomata.IOSynchronization;
import rationals.ioautomata.IOTransition;
import fr.lifl.utils.MsgQueue;

/**
 * An asynchronous implementation of StateMachine backed up by an IOAutomaton.
 * <p>
 * This implementation is asynchronous in the sense that input and output
 * methods are non blocking.
 * 
 * @author nono
 * @version $Id$
 */
public class AsynchIOAutomatonSMAdapter implements IOStateMachine {

    private IOAutomaton auto;

    private MsgQueue inq = new MsgQueue();

    private MsgQueue outq = new MsgQueue();

    private Set state;

    private boolean stop;

    private IOSynchronization synch = new IOSynchronization();

    private boolean error;
    
    private Random rand = new Random();

    public AsynchIOAutomatonSMAdapter(IOAutomaton auto) {
        this.auto = auto;
        this.state = auto.initials();
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.IOStateMachine#input(java.lang.Object)
     */
    public void input(Object o) {
        inq.enqueue(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.IOStateMachine#isInputEnabled()
     */
    public boolean isInputEnabled() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.IOStateMachine#output()
     */
    public Object output() {
        return outq.dequeue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.IOStateMachine#output(java.lang.Object[], int,
     *      int)
     */
    public int output(Object[] out, int start, int len) {
        int i;
        for (i = start; i < len && i < out.length; i++) {
            Object o = outq.dequeue();
            if (o == null)
                break;
            out[i] = o;
        }
        return i - start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.IOStateMachine#availableOutput()
     */
    public int availableOutput() {
        return outq.getSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.IOStateMachine#reset()
     */
    public void reset() {
        state = auto.initials();
    }

    /*
     * (non-Javadoc)
     * 
     * @see rationals.ioautomata.IOStateMachine#stop()
     */
    public void stop() {
        this.stop = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        _run:
            while(!stop) {
            /* get all fireable transitions in current state */
            Set trs = auto.delta(state);
            /* is input available ? */
            if (inq.getSize() > 0) {
                Object in = inq.dequeue();
                /* find a transition with this label */
                IOTransition tr = null;
                IOTransition.IOLetter lt = new IOTransition.IOLetter(in,IOAlphabetType.OUTPUT);
                for (Iterator it = trs.iterator(); it.hasNext();) {
                    tr = (IOTransition) it.next();
                    if (synch.synchronize(tr.label(), lt) != null) {
                        /* do transition */
                        doTransition(tr.label());
                        continue _run;
                    }
                }
            } else {
                /* else select an output or internal transition */
                removeInputs(trs);
                if(trs.size() == 0)
                    continue;
                int r = rand.nextInt(trs.size());
                IOTransition tr = null;
                for (Iterator it = trs.iterator(); r >= 0; r--)
                    tr = (IOTransition) it.next();
                if (tr == null) {
                    /* no possible transition was found, loop */
                    continue;
                }
                if (tr.getType() == IOAlphabetType.OUTPUT) {
                    outq.enqueue(((IOTransition.IOLetter)tr.label()).label);
                }
                doTransition(tr.label());
                continue;
            }
            /* if we are here, then no transition match so we stop */
            error = true;
            stop = true;

        }
    }

    /**
     * @param object
     */
    private void doTransition(Object object) {
        Set next = auto.step(state,object);
        state = next;
        System.err.println("doing transition on "+object);
    }


    /**
     * Remove all input transitions from this set of transitions
     * 
     * @param trs
     */
    private void removeInputs(Set trs) {
        for(Iterator i = trs.iterator();i.hasNext();)
            if(((IOTransition)i.next()).getType() == IOAlphabetType.INPUT)
                i.remove();
    }

    public boolean isError() {
        return error;
    }
}
