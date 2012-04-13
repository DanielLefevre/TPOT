/*
 * ______________________________________________________________________________
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL Redistribution and use in source
 * and binary forms, with or without modification, are permitted provided that
 * the following conditions are met: (1) Redistributions of source code must
 * retain the above copyright notice, this list of conditions and the following
 * disclaimer. (2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. (3) The
 * name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission. THIS SOFTWARE
 * IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. Created on 29 mars 2005
 */
package net.jautomata.rationals.converters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.NoSuchStateException;
import net.jautomata.rationals.PSymbol;
import net.jautomata.rationals.State;
import net.jautomata.rationals.Transition;

/**
 * A codec for rationals that stores an automaton in a specific format similar
 * to the {@see ToString} class output. <strong>Note </strong> : the alphabet
 * may not contains the reserved characters '[', ',' , ']', '(', ')'.
 * 
 * <pre>
 *     A = [ <comma separated alphabet> ]\n
 *     Q = [ <comma separated list of statess > ] \n
 *     I = [ <comma separated list of initials > ] \n 
 *     T = [ <comma separated list of terminals > ] \n 
 *     delta = [ \n 
 *     ( to, label ,from ) \n
 *     ( to, label ,from ) \n
 *      .... \n
 *         ] \n
 * </pre>
 * @author nono
 * @version $Id$
 * @see toAscii
 */
public class JAutoCodec implements StreamEncoder, StreamDecoder {

    /*
     * (non-Javadoc)
     * @see rationals.converters.StreamEncoder#output(rationals.Automaton,
     * java.io.OutputStream)
     */
    @Override
    public final void output(final Automaton a, final OutputStream stream)
            throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(stream));
        pw.print(new toAscii().toString(a));
        pw.flush();
    }

    /*
     * (non-Javadoc)
     * @see rationals.converters.StreamDecoder#input(java.io.InputStream)
     */
    @Override
    public final Automaton input(final InputStream is) throws IOException {

        // the program has to determine if the automaton it's probabilistic
        boolean isProbabilisticAutomaton = false;

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        Automaton a = new Automaton();
        Map<String, State> smap = new HashMap<>();
        Map<String, Transition> transitions = new HashMap<>();

        /* regexes for various parts */
        Pattern set = Pattern.compile("\\[\\s*(.*)\\s*\\]");
        Pattern trans = Pattern
                .compile("\\(([^,]*)[ ]*,([^,]*),[ ]*([^,)]*)\\)");
        // patter to match probabilistic transitions -> (from, label, to,
        // probability)
        Pattern transProb = Pattern
                .compile("\\((.*)[ ]*,(.*),(.*),[ ]*([^)]*)\\)");

        /* read the alphabet - and discard it */
        String alph = rd.readLine();
        // System.out.println("alph: " + alph);
        /* read states */
        String states = rd.readLine();
        // System.out.println("states: " + states);
        // System.out.println("states.substring(4): " + states.substring(4));
        Matcher stm = set.matcher(states.substring(4));
        if (!stm.find()) {
            throw new IOException("Failed to parse states set in input stream "
                    + states);
        }
        String tmp = stm.group(0);
        states = stm.group(1);
        StringTokenizer st = new StringTokenizer(states, ",");
        Set<String> sset = new HashSet<>(); /* set of all states */
        while (st.hasMoreTokens()) {
            String s = st.nextToken().trim();
            // System.out.println("s: " + s);
            sset.add(s);
        }
        /* read initials */
        states = rd.readLine();
        stm = set.matcher(states.substring(4));
        if (!stm.find()) {
            throw new IOException(
                    "Failed to parse initial states set in input stream "
                            + states);
        }
        states = stm.group(1);
        st = new StringTokenizer(states, ",");
        Set<String> iset = new HashSet<>(); /* set of all states */
        while (st.hasMoreTokens()) {
            String s = st.nextToken().trim();
            iset.add(s);
        }
        /* read terminals */
        states = rd.readLine();
        stm = set.matcher(states.substring(4));
        if (!stm.find()) {
            throw new IOException("Failed to parse terminals set in string "
                    + states);
        }
        states = stm.group(1);
        st = new StringTokenizer(states, ",");
        Set<String> tset = new HashSet<>(); /* set of all states */
        while (st.hasMoreTokens()) {
            String s = st.nextToken().trim();
            tset.add(s);
        }
        /* create states */
        Iterator<String> it = sset.iterator();
        while (it.hasNext()) {
            String s = it.next();
            State newState = a.addState(iset.contains(s), tset.contains(s));
            smap.put(s, newState);
        }

        String what = rd.readLine();
        // System.out.println("what: " + what);
        st = new StringTokenizer(what, "=");

        String whatToRead = st.nextToken().trim();
        // System.out.println("whatToRead: " + whatToRead);

        Set<Transition> newSet = new HashSet<>();

        String trs = null;
        while (!(trs = rd.readLine()).equals("]")) {
            // check if next transtion matches
            if (isProbabilisticAutomaton) {
                stm = transProb.matcher(trs);
                if (!stm.find()) {
                    throw new IOException("Failed to parse transition in "
                            + trs);
                }
            } else {
                stm = trans.matcher(trs);
                if (!stm.find()) {
                    stm = transProb.matcher(trs);
                    if (stm.find()) {
                        isProbabilisticAutomaton = true;
                    } else {
                        throw new IOException("Failed to parse transition in "
                                + trs);
                    }
                }
            }

            // add transtion to automaton
            State from = smap.get(stm.group(1).trim());
            // System.out.println("stm.group(1).trim(): " +
            // stm.group(1).trim());
            String symbol = stm.group(2).trim();
            // System.out.println("symbol: " + symbol);
            if ("1".equals(symbol)) {
                symbol = null;
            }
            State to = smap.get(stm.group(3).trim());
            double probability = 1;
            if (isProbabilisticAutomaton) {
                probability = Double.parseDouble(stm.group(4).trim());
            }
            if (from == null || to == null) {
                throw new IOException("Failed to parse states in transition "
                        + trs);
            }

            Transition newTransition;
            Object lbl;

            if (!isProbabilisticAutomaton) {
                lbl = symbol;
            } else {
                lbl = new PSymbol(symbol, probability);
            }
            newTransition = new Transition(from, lbl, to);
            try {
                a.addTransition(newTransition);
            } catch (NoSuchStateException e) {
                e.printStackTrace();
            }
        }
        return a;
    }
}
