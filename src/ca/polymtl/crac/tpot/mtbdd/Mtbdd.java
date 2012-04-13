package ca.polymtl.crac.tpot.mtbdd;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.PSymbol;
import net.jautomata.rationals.Transition;
import net.jautomata.rationals.transformations.Complement;

import ca.polymtl.crac.tpot.model.Opacity;

/**
 * @author Olivier Bachard
 */
public class Mtbdd {

    private static int a = 0;

    // Load the dynamic library that manipulate Mtbdds
    static {
        try {
            System.loadLibrary("MtbddBis");
        } catch (UnsatisfiedLinkError e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    /**
     * Code the boolean variables xi.
     */
    private MtbddNodesArray xVars;

    /**
     * Code the boolean variables yi.
     */
    private MtbddNodesArray yVars;

    /**
     * The mtbdd representing the automaton.
     */
    private MtbddNode mtbdd;

    /**
     * Store the initial state in a list of MtbddNodes.
     */
    private MtbddNodesArray initialStates;

    /**
     * Store the terminal state in a list of MtbddNodes.
     */
    private MtbddNodesArray terminalStates;

    /**
     * The mtbdd representing the initial states.
     */
    private MtbddNode initialMtbdd;

    /**
     * The mtbdd representing the terminal states.
     */
    private MtbddNode terminalMtbdd;

    /**
     * Store all transitions of the automaton with MtbddTransition.
     */
    private List<MtbddTransition> transitions;

    /**
     * Labels of the automaton.
     */
    private List<Object> labels;

    public Mtbdd() {
        this.xVars = new MtbddNodesArray();
        this.yVars = new MtbddNodesArray();
        this.initialStates = new MtbddNodesArray();
        this.terminalStates = new MtbddNodesArray();
        this.transitions = new LinkedList<>();
        this.labels = new LinkedList<>();
    }

    public Mtbdd(final Automaton aIn, final boolean prob) {
        this.xVars = new MtbddNodesArray();
        this.yVars = new MtbddNodesArray();
        this.initialStates = new MtbddNodesArray();
        this.terminalStates = new MtbddNodesArray();
        this.labels = getLabels(aIn.delta());

        // Compute the number of nodes needed
        int nbrVars = (int) Math.ceil(Math.log(aIn.states().size())
                / Math.log(2));

        // Create the boolean variables
        createBooleanVariables(this.xVars, this.yVars, nbrVars);

        // Build every transitions of Pi automata
        if (prob) {
            this.transitions = Mtbdd.buildTransitions(aIn.delta(), this.xVars,
                    this.yVars);
        } else {
            this.transitions = Mtbdd.builTransitionsUnprob(aIn.delta(),
                    this.xVars, this.yVars);
        }

        // Build the mtbdd representing Pi
        if (prob) {
            this.mtbdd = Mtbdd.buildMtbddFromTransitions(this.transitions);
        } else {
            this.mtbdd = Mtbdd
                    .buildMtbddFromTransitionsUnprob(this.transitions);
            // System.out.println("\n (Drawing Pi)");
        }

        // Get the initials states
        this.initialStates.buildStates(aIn.initials(), this.xVars);

        // Get the finals states
        this.terminalStates.buildStates(aIn.terminals(), this.yVars);

        this.initialMtbdd = new MtbddNode(this.initialStates.getNodes().get(0)
                .getPointer());
        for (int i = 1; i < this.initialStates.getNodes().size(); i++) {
            this.initialMtbdd = Mtbdd.mtbddOr(this.initialMtbdd,
                    this.initialStates.getNodes().get(i));
        }

        this.terminalMtbdd = new MtbddNode(this.terminalStates.getNodes()
                .get(0).getPointer());
        for (int i = 1; i < this.terminalStates.getNodes().size(); i++) {
            this.terminalMtbdd = Mtbdd.mtbddOr(this.terminalMtbdd,
                    this.terminalStates.getNodes().get(i));
        }
    }

    public static MtbddNode addComputeCube(final long[] dd) {
        return new MtbddNode(Nat_addComputeCube(dd));
    }

    public static MtbddNode addConst(final double value) {
        return new MtbddNode(Nat_addConst(value));
    }

    public static MtbddNode addExistAbstract(final MtbddNode aIn,
            final MtbddNode cube) {
        return new MtbddNode(Nat_addExistAbstract(aIn.getPointer(),
                cube.getPointer()));
    }

    public static MtbddNode addIthVar(final int index) {
        return new MtbddNode(Nat_addIthVar(index));
    }

    public static MtbddNode addOrAbstract(final MtbddNode aIn,
            final MtbddNode cube) {
        return new MtbddNode(Nat_addOrAbstract(aIn.getPointer(),
                cube.getPointer()));
    }

    /**
     * Build a cube considering three Mtbdds.
     * @param piVars
     * @param obsVars
     * @param phiVars
     * @return a tab containing the variables of the cube
     */
    public static long[] builCube(final MtbddNodesArray piVars,
            final MtbddNodesArray obsVars, final MtbddNodesArray phiVars) {
        int size = piVars.getNodes().size() + obsVars.getNodes().size()
                + phiVars.getNodes().size();
        long[] cube = new long[size];
        for (int i = 0; i < piVars.getNodes().size(); i++) {
            cube[i] = piVars.getNodes().get(i).getPointer();
        }
        for (int i = piVars.getNodes().size(); i < piVars.getNodes().size()
                + obsVars.getNodes().size(); i++) {
            cube[i] = obsVars.getNodes().get(i - piVars.getNodes().size())
                    .getPointer();
        }
        for (int i = piVars.getNodes().size() + obsVars.getNodes().size(); i < size; i++) {
            cube[i] = phiVars
                    .getNodes()
                    .get(i
                            - (piVars.getNodes().size() + obsVars.getNodes()
                                    .size())).getPointer();
        }

        return cube;
    }

    /**
     * Build a cube considering two MTbdds.
     * @param piVars
     * @param obsVars
     * @return a tab containing the variables of the cube
     */
    public static long[] buildCube(final MtbddNodesArray piVars,
            final MtbddNodesArray obsVars) {
        int size = piVars.getNodes().size() + obsVars.getNodes().size();
        long[] cube = new long[size];
        for (int i = 0; i < piVars.getNodes().size(); i++) {
            cube[i] = piVars.getNodes().get(i).getPointer();
        }
        for (int i = piVars.getNodes().size(); i < piVars.getNodes().size()
                + obsVars.getNodes().size(); i++) {
            cube[i] = obsVars.getNodes().get(i - piVars.getNodes().size())
                    .getPointer();
        }

        return cube;
    }

    /**
     * Build a mtbdd from a list of MtbddTransition in MTBDD considering
     * probabilities.
     * @param transitions
     *            the transitions of the mtbdd
     * @return a mtbdd wich is a boolean "or" of every transitions
     */
    public static MtbddNode buildMtbddFromTransitions(
            final List<MtbddTransition> transitions) {

        try {
            MtbddNode mtbdd = new MtbddNode(transitions.get(0).getTransition()
                    .getPointer());
            for (int i = 1; i < transitions.size(); i++) {
                mtbdd = mtbddPlus(mtbdd, transitions.get(i).getTransition());
            }
            return mtbdd;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            System.out
                    .println("--> Size of LinkedList<MtbddTransition> is zero");
            return null;
        }
    }

    /**
     * Build a mtbdd object from a list of MtbddTransition without considering
     * probabilities.
     * @param transitions
     *            the transitions of the mtbdd
     * @return a mtbdd wich is a boolean "or" of every transitions
     */
    public static MtbddNode buildMtbddFromTransitionsUnprob(
            final List<MtbddTransition> transitions) {
        try {
            MtbddNode mtbdd = new MtbddNode(transitions.get(0).getTransition()
                    .getPointer());
            for (int i = 1; i < transitions.size(); i++) {
                mtbdd = mtbddOr(mtbdd, transitions.get(i).getTransition());
            }
            return mtbdd;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            System.out
                    .println("--> Size of LinkedList<MtbddTransition> is zero");
            return null;
        }
    }

    /**
     * Build boolean transitions from transitions of the automaton in MTBDD case
     * (with probability).
     * @param delta
     *            the set of transitions from the automaton
     * @param xVars
     *            Boolean variables coding starting states
     * @param yVars
     *            Boolean variables coding ending states
     * @return The list of transitions under the guise of a combination of
     *         boolean variables
     */
    public static List<MtbddTransition> buildTransitions(
            final Set<Transition> delta, final MtbddNodesArray xVars,
            final MtbddNodesArray yVars) {
        LinkedList<MtbddTransition> piTransitions = new LinkedList<>();
        Iterator<Transition> it = delta.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            // Create a new mtbdd transition
            if (t.label() instanceof PSymbol) {
                piTransitions.add(new MtbddTransition(((PSymbol) t.label())
                        .getLabel(), ((PSymbol) t.label()).getProbability()));
            } else {
                piTransitions.add(new MtbddTransition(t.label()));
            }

            // implement the newly created transition
            piTransitions.getLast().buildMtbddTransition(t, xVars, yVars, true);
        }
        return piTransitions;
    }

    /**
     * Build boolean transitions from transitions of the automaton in BDD case
     * (no probability).
     * @param delta
     *            the set of transitions from the automaton
     * @param xVars
     *            Boolean variables coding starting states
     * @param yVars
     *            Boolean variables coding ending states
     * @return The list of transitions under the guise of a combination of
     *         boolean variables
     */
    public static LinkedList<MtbddTransition> builTransitionsUnprob(
            final Set<Transition> delta, final MtbddNodesArray xVars,
            final MtbddNodesArray yVars) {
        LinkedList<MtbddTransition> piTransitions = new LinkedList<>();
        Iterator<Transition> it = delta.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            // Create a new mtbdd transition
            piTransitions.add(new MtbddTransition(t.label()));

            // implement the newly created transition
            piTransitions.getLast()
                    .buildMtbddTransition(t, xVars, yVars, false);
        }

        return piTransitions;
    }

    /**
     * Compute the liberal opacity symbolically.
     * @param opacity
     *            the opacity object representing the problem to solve
     * @return TODO
     */
    public static double computeLpo(final Opacity opacity) {
        // intialisation of the manager
        Mtbdd.Nat_manager_init();

        // Build the Mtbdd representing Pi<
        Mtbdd piUnprob = new Mtbdd(opacity.getAutomaton(), false);

        // ------------------------------------------------------//
        // BEGINNING OF THE MAIN ALGORITHM //
        // ------------------------------------------------------//

        // Variables needed for the observation classes

        List<MtbddTransition> piAoTransitions = new LinkedList<>();         // Pi||Ao
        // transitions
        List<MtbddTransition> piAoPhiTransitions = new LinkedList<>();      // (Pi||Ao)||Phi
        // transitions
        List<MtbddTransition> piAoPhiCompTransitions = new LinkedList<>();    // (Pi||Ao)||PhiComp
        // transitions

        MtbddNode cubeWithPhi;     // the mtbddnode representing the cube for phi
        MtbddNode cubeWithPhiComp;     // the mtbddnode representing the cube for
        // phi complement

        // The value of liberal opacity of the automaton
        // =============================================
        double lpsoBis = 0;

        Iterator<Automaton> itObs = opacity.getObs().iterator();

        // For each observation
        while (itObs.hasNext()) {
            // System.out.println("\nObservation n°" + m + " :");
            // System.out.println("----------------------");
            Automaton observation = itObs.next();

            // ==========================================================================//
            // Build the Mtbdd representing Phi and Phi complement //
            // ==========================================================================//

            Complement complement = new Complement();
            Automaton phiComplementTemp = complement
                    .transform(opacity.getPhi());

            Complement complementBis = new Complement();
            complementBis.setAlphabet(observation.alphabet());
            Automaton phiAutomaton = complementBis.transform(phiComplementTemp);

            Complement complementTer = new Complement();
            complementTer.setAlphabet(observation.alphabet());
            Automaton phiComp = complementTer.transform(opacity.getPhi());
            Mtbdd phi = new Mtbdd(phiAutomaton, false);
            Mtbdd phiComplement = new Mtbdd(phiComp, false);

            // Build the Mtbdd of the observation
            Mtbdd obs = new Mtbdd(observation, false);

            // The boolean function representing Pi||Ao
            // ------------------------------------------------------------------------------------
            piAoTransitions.clear();
            piAoTransitions = Mtbdd.SyncProductUnprob(
                    piUnprob.getTransitions(), obs.getTransitions(),
                    obs.getLabels());

            // Language inclusion with phi
            // =====================================================================================================================

            // The boolean function representing (Pi||Ao)||phi
            // ------------------------------------------------------------------------------------
            piAoPhiTransitions.clear();
            piAoPhiTransitions = Mtbdd.SyncProductUnprob(piAoTransitions,
                    phi.getTransitions(), phi.getLabels());
            // piAoPhiTransitions =
            // Mtbdd.SyncProductUnprob(obs.getTransitions(),
            // phi.getTransitions(), phi.getLabels());
            MtbddNode piAoPhi = Mtbdd
                    .buildMtbddFromTransitionsUnprob(piAoPhiTransitions);

            // Get the initials states of the whole automaton (Pi||Ao)||phi
            // -------------------------------------------------------------------------------------
            MtbddNode initials = Mtbdd.mtbddAnd(
                    obs.getInitialMtbdd(),
                    Mtbdd.mtbddAnd(phi.getInitialMtbdd(),
                            piUnprob.getInitialMtbdd()));
            MtbddNode finals = Mtbdd.mtbddAnd(
                    obs.getTerminalMtbdd(),
                    Mtbdd.mtbddAnd(phi.getTerminalMtbdd(),
                            piUnprob.getTerminalMtbdd()));
            // MtbddNode initials = Mtbdd.mtbddAnd(obs.getInitialMtbdd(),
            // phi.getInitialMtbdd());
            // MtbddNode finals =
            // Mtbdd.mtbddAnd(obs.getTerminalMtbdd(),phi.getTerminalMtbdd());

            // Get the variables we need for the cube with phi
            // -------------------------------------------------------------------------------------
            long[] cubeVarsWithPhi = Mtbdd.builCube(piUnprob.getxVars(),
                    obs.getxVars(), phi.getxVars());
            // long cubeVarsWithPhi[] = Mtbdd.buildCube(obs.getxVars(),
            // phi.getxVars());
            cubeWithPhi = Mtbdd.addComputeCube(cubeVarsWithPhi); 	// Compute the
                                                                 // cube

            // Compute o in Phi
            // -------------------------------------------------------------------------------------
            try {
                if (piAoPhi == null
                        || Mtbdd.isLanguageEmpty(piAoPhi, initials, finals,
                                cubeWithPhi, null)) {

                    Mtbdd piProb = new Mtbdd(opacity.getAutomaton(), true);

                    List<MtbddTransition> oPiProbT = new LinkedList<>();
                    oPiProbT = Mtbdd.SyncProduct(piProb.getTransitions(),
                            obs.getTransitions(), obs.getLabels());

                    MtbddNode oPiProb = Mtbdd
                            .buildMtbddFromTransitions(oPiProbT);

                    MtbddNode oPiProbInitials = Mtbdd.mtbddAnd(
                            piProb.getInitialMtbdd(), obs.getInitialMtbdd());
                    MtbddNode oPiProbFinals = Mtbdd.mtbddAnd(
                            piProb.getTerminalMtbdd(), obs.getTerminalMtbdd());

                    long[] oPiProbCubeVars = Mtbdd.buildCube(piProb.getxVars(),
                            obs.getxVars());
                    MtbddNode oPiProbCube = Mtbdd
                            .addComputeCube(oPiProbCubeVars);

                    lpsoBis = Mtbdd.computeProbability(oPiProb,
                            oPiProbInitials, oPiProbFinals, oPiProbCube,
                            lpsoBis);

                }
            } catch (Exception e) {
                System.out.println("Error -- " + e);
            }

            // Language inclusion with phi complement
            // =====================================================================================================================

            // The boolean function representing (Pi||Ao)||phiComplement
            // ------------------------------------------------------------------------------------
            piAoPhiCompTransitions.clear();
            piAoPhiCompTransitions = Mtbdd.SyncProductUnprob(piAoTransitions,
                    phiComplement.getTransitions(), phiComplement.getLabels());
            // piAoPhiCompTransitions =
            // Mtbdd.SyncProductUnprob(obs.getTransitions(),
            // phiComplement.getTransitions(), phiComplement.getLabels());
            MtbddNode piAoPhiComp = Mtbdd
                    .buildMtbddFromTransitionsUnprob(piAoPhiCompTransitions);

            // Get the initials states of the whole automaton
            // (Pi||Ao)||phiComplement
            // -------------------------------------------------------------------------------------
            MtbddNode initialsWithPhiComp = Mtbdd.mtbddAnd(
                    obs.getInitialMtbdd(),
                    Mtbdd.mtbddAnd(phiComplement.getInitialMtbdd(),
                            piUnprob.getInitialMtbdd()));
            MtbddNode finalsWithPhiComp = Mtbdd.mtbddAnd(
                    obs.getTerminalMtbdd(),
                    Mtbdd.mtbddAnd(phiComplement.getTerminalMtbdd(),
                            piUnprob.getTerminalMtbdd()));
            // MtbddNode initialsWithPhiComp =
            // Mtbdd.mtbddAnd(obs.getInitialMtbdd(),
            // phiComplement.getInitialMtbdd());
            // MtbddNode finalsWithPhiComp =
            // Mtbdd.mtbddAnd(obs.getTerminalMtbdd(),phiComplement.getTerminalMtbdd());

            // Get the variables we need for the cube with phi complement
            // -------------------------------------------------------------------------------------
            long[] cubeVarsWithPhiComp = Mtbdd.builCube(piUnprob.getxVars(),
                    obs.getxVars(), phiComplement.getxVars());
            // long cubeVarsWithPhiComp[] = Mtbdd.buildCube(obs.getxVars(),
            // phiComplement.getxVars());
            cubeWithPhiComp = Mtbdd.addComputeCube(cubeVarsWithPhiComp);	// Compute
                                                                         // the
                                                                         // cube

            // Compute o in Phi complement
            // -------------------------------------------------------------------------------------
            try {
                if (piAoPhiComp == null
                        || Mtbdd.isLanguageEmpty(piAoPhiComp,
                                initialsWithPhiComp, finalsWithPhiComp,
                                cubeWithPhiComp, null)) {

                    Mtbdd piProb = new Mtbdd(opacity.getAutomaton(), true);

                    List<MtbddTransition> oPiProbT = new LinkedList<>();
                    oPiProbT = Mtbdd.SyncProduct(piProb.getTransitions(),
                            obs.getTransitions(), obs.getLabels());

                    MtbddNode oPiProb = Mtbdd
                            .buildMtbddFromTransitions(oPiProbT);

                    MtbddNode oPiProbInitials = Mtbdd.mtbddAnd(
                            obs.getInitialMtbdd(), piProb.getInitialMtbdd());
                    MtbddNode oPiProbFinals = Mtbdd.mtbddAnd(
                            obs.getTerminalMtbdd(), piProb.getTerminalMtbdd());

                    long[] oPiProbCubeVars = Mtbdd.buildCube(piProb.getxVars(),
                            obs.getxVars());
                    MtbddNode oPiProbCube = Mtbdd
                            .addComputeCube(oPiProbCubeVars);

                    lpsoBis = Mtbdd.computeProbability(oPiProb,
                            oPiProbInitials, oPiProbFinals, oPiProbCube,
                            lpsoBis);
                }
            } catch (Exception e) {
                System.out.println("Error -- " + e);
            }

            // Deref Cudd variables
            try {
                Mtbdd.Nat_RecursiveDeref(piAoPhi.getPointer());
                Mtbdd.Nat_RecursiveDeref(piAoPhiComp.getPointer());
            } catch (Exception e) {
                System.out.println("Error -- " + e);
            }

            Mtbdd.Nat_RecursiveDeref(initials.getPointer());
            Mtbdd.Nat_RecursiveDeref(finals.getPointer());
            Mtbdd.Nat_RecursiveDeref(initialsWithPhiComp.getPointer());
            Mtbdd.Nat_RecursiveDeref(finalsWithPhiComp.getPointer());

        }
        // End while

        Mtbdd.Nat_manager_quit();
        System.out.println("L'opacité du système lspoBis est: " + lpsoBis);
        return lpsoBis;

    }

    /**
     * Compute the probability for a certain observation class. It takes the
     * mtbdd representing Pi||Ao and look if there exists a way from the initial
     * state to a terminal state. If it is the case, the probability is added.
     * @param tree
     * @param initials
     * @param finals
     * @param cube
     * @param prob
     * @return the value of the probability
     */
    public static double computeProbability(final MtbddNode tree,
            final MtbddNode initials, final MtbddNode finals,
            final MtbddNode cube, double prob) {
        MtbddNode txy = Mtbdd.mtbddAnd(tree, initials);
        MtbddNode extract = Mtbdd.addExistAbstract(txy, cube);

        // if the state is a final state
        if (extract.isConstant()) {
            return prob;
        } else if (extract.isFinal(finals)) {
            MtbddNode temp = Mtbdd.mtbddAnd(extract, finals);
            LinkedList<Double> probs = new LinkedList<>();
            probs = getConstValue(temp);
            for (int i = 0; i < probs.size(); i++) {
                prob += probs.get(i);
            }

            MtbddNode convExtract = extract.convertTree();
            Mtbdd.Nat_RecursiveDeref(extract.getPointer());
            return computeProbability(tree, convExtract, finals, cube, prob);
        } else {
            MtbddNode convExtract = extract.convertTree();
            Mtbdd.Nat_RecursiveDeref(extract.getPointer());
            return computeProbability(tree, convExtract, finals, cube, prob);
        }
    }

    public static double countPath(final MtbddNode node) {
        return Nat_CountPath(node.getPointer());
    }

    public static double countPathsToNonZero(final MtbddNode node) {
        return Nat_CountPathsToNonZero(node.getPointer());
    }

    public static double findMax(final MtbddNode node) {
        return Nat_findMax(node.getPointer());
    }

    /**
     * @param node
     *            a mtbdd
     * @return look through the mtbdd and get all the values of the leafs
     */
    public static LinkedList<Double> getConstValue(final MtbddNode node) {
        LinkedList<Double> list = new LinkedList<>();
        if (!node.isConstant()) {
            for (Double d : getConstValue(node.getThenChild())) {
                list.add(d);
            }
            for (Double d : getConstValue(node.getElseChild())) {
                list.add(d);
            }
            return list;
        }
        list.add(node.getConstValue());
        return list;
    }

    /**
     * Compute the inclusion of languages. It takes the mtbdd representing
     * (pi||Ao)Phi and look if there exists a way from the initial state bis a
     * terminal state. If it is the case, the language is not empty.
     * @param tree
     * @param initials
     * @param finals
     * @param cube
     * @param prev
     * @return true if the language is empty, false otherwise
     */
    public static boolean isLanguageEmpty(final MtbddNode tree,
            final MtbddNode initials, final MtbddNode finals,
            final MtbddNode cube, MtbddNode prev) {
        MtbddNode txy = Mtbdd.mtbddAnd(tree, initials);
        MtbddNode extract = Mtbdd.addOrAbstract(txy, cube);

        if (prev != null) {
            // If the boolean And return a constant null, it means that extract
            // and prev are the same
            MtbddNode fixPoint = mtbddAnd(extract, mtbddNot(prev));
            if (fixPoint.isConstant() && fixPoint.getConstValue() == 0) {
                // Fix point is reached
                return prev.isFinal(finals);
            }
        }

        // if the state is a final state
        if (extract.isFinal(finals)) {
            return false;
        }
        prev = new MtbddNode(extract.getPointer());
        MtbddNode convExtract = extract.convertTreeUnprob();
        Mtbdd.Nat_RecursiveDeref(txy.getPointer());
        return isLanguageEmpty(tree, convExtract, finals, cube, prev);
    }

    public static MtbddNode mtbddAnd(final MtbddNode aIn, final MtbddNode b) {
        return new MtbddNode(Nat_And(aIn.getPointer(), b.getPointer()));
    }

    public static MtbddNode mtbddNot(final MtbddNode aIn) {
        return new MtbddNode(Nat_Not(aIn.getPointer()));
    }

    public static MtbddNode mtbddOr(final MtbddNode aIn, final MtbddNode b) {
        return new MtbddNode(Nat_Or(aIn.getPointer(), b.getPointer()));
    }

    public static MtbddNode mtbddPlus(final MtbddNode aIn, final MtbddNode b) {
        return new MtbddNode(Nat_Plus(aIn.getPointer(), b.getPointer()));
    }

    public static native long Nat_addComputeCube(final long[] dd);

    public static native long Nat_addConst(final double value);

    public static native long Nat_addExistAbstract(final long dd,
            final long cube);

    public static native long Nat_addIthVar(final int index);

    public static native long Nat_addOrAbstract(final long dd, final long cube);

    public static native long Nat_And(final long dd1, final long dd2);

    public static native double Nat_CountPath(final long node);

    public static native double Nat_CountPathsToNonZero(final long node);

    public static native void Nat_drawMtbdd(final long mtbdd);

    public static native void Nat_drawMtbdd(final long mtbdd,
            final String filename);

    public static native void Nat_enableGarbageCollection();

    public static native double Nat_findMax(final long node);

    public static native double Nat_findMin(final long node);

    public static double Nat_findMin(final MtbddNode node) {
        return Nat_findMin(node.getPointer());
    }

    public static native void Nat_freeTree();

    public static native void Nat_manager_init();

    public static native void Nat_manager_quit();

    public static native long Nat_Not(final long dd);

    public static native long Nat_Or(final long dd1, final long dd2);

    public static native long Nat_parcoursArbre(final long n);

    public static native long Nat_Plus(final long dd1, final long dd2);

    public static native long Nat_ReadLogicZero();

    public static native long Nat_readOne();

    public static native void Nat_RecursiveDeref(final long node);

    public static native void Nat_Ref(final long node);

    public static native long Nat_Xor(final long dd1, final long dd2);

    public static MtbddNode readLogicZero() {
        return new MtbddNode(Nat_ReadLogicZero());
    }

    public static MtbddNode readOne() {
        return new MtbddNode(Nat_readOne());
    }

    /**
     * Compute the synchronised product of two Mtbdds considering probabilities.
     * @param mtbdd1
     * @param mtbdd2
     * @param labels
     * @return a list of transitions corresponding to all transitions of the
     *         synchronised product
     */
    public static List<MtbddTransition> SyncProduct(
            final List<MtbddTransition> mtbdd1,
            final List<MtbddTransition> mtbdd2, final List<Object> labels) {
        LinkedList<MtbddTransition> syncMtbdd = new LinkedList<>();

        // Compute the boolean mtbdd1||mtbdd2
        // For each label
        Iterator<Object> itLabels = labels.iterator();
        while (itLabels.hasNext()) {

            Object label = itLabels.next();
            // System.out.println("label: " + label);
            // Transitions corresponding with the current label
            LinkedList<MtbddTransition> t_Mtbdd1 = new LinkedList<>();
            LinkedList<MtbddTransition> t_Mtbdd2 = new LinkedList<>();

            // Get the mtbdd1 transitions labeled by labels[i]
            Iterator<MtbddTransition> it = mtbdd1.iterator();
            while (it.hasNext()) {

                MtbddTransition t = it.next();
                if (t.getLabel().equals(label)) {
                    // System.out.println("   pi: " +t.getLabel());
                    t_Mtbdd1.add(t);
                }

            }

            // Get the mtbdd2 transitions labeled by labels[i]
            it = mtbdd2.iterator();
            while (it.hasNext()) {
                MtbddTransition t = it.next();
                if (t.getLabel().equals(label)) {
                    // System.out.println("  obs: " + t.getLabel());
                    t_Mtbdd2.add(t);
                }
            }
            // System.out.println("sizes: " + t_Mtbdd1.size() + " - " +
            // t_Mtbdd2.size());
            // If mtbdd1.size = 0, it means there were no transition in mtbdd1
            // labelled with the current label
            if (t_Mtbdd1.size() > 0) {
                MtbddNode n_Mtbdd1 = new MtbddNode(t_Mtbdd1.getFirst()
                        .getTransition());
                MtbddNode n_Mtbdd2 = new MtbddNode(t_Mtbdd2.getFirst()
                        .getTransition());

                // Make boolean OR on mtbdd1 transitions for this label
                for (int j = 1; j < t_Mtbdd1.size(); j++) {
                    n_Mtbdd1 = mtbddPlus(n_Mtbdd1, t_Mtbdd1.get(j)
                            .getTransition());
                }

                // Make boolean OR on mtbdd2 transitions for this label
                for (int j = 1; j < t_Mtbdd2.size(); j++) {
                    n_Mtbdd2 = mtbddPlus(n_Mtbdd2, t_Mtbdd2.get(j)
                            .getTransition());
                }

                // Make boolean And on mtbdd1 and mtbdd2 for this label and add
                // the result the list of syncMtbdd transitions
                syncMtbdd.add(new MtbddTransition(label));
                syncMtbdd.getLast().setTransition(
                        new MtbddNode(Mtbdd.Nat_And(n_Mtbdd1.getPointer(),
                                n_Mtbdd2.getPointer())));
            }
        }

        return syncMtbdd;
    }

    /**
     * Compute the synchronised product of two Mtbdds without considering
     * probabilities
     * @param mtbdd1
     * @param mtbdd2
     * @param labels
     * @return a list of transitions corresponding to all transitions of the
     *         synchronised product
     */
    public static List<MtbddTransition> SyncProductUnprob(
            final List<MtbddTransition> mtbdd1,
            final List<MtbddTransition> mtbdd2, final List<Object> labels) {
        LinkedList<MtbddTransition> syncMtbdd = new LinkedList<>();

        // Compute the boolean mtbdd1||mtbdd2
        // For each label
        Iterator<Object> itLabels = labels.iterator();
        while (itLabels.hasNext()) {
            Object label = itLabels.next();
            // System.out.print("label: " + label + " - ");
            // Transitions corresponding with the current label
            LinkedList<MtbddTransition> t_Mtbdd1 = new LinkedList<>();
            LinkedList<MtbddTransition> t_Mtbdd2 = new LinkedList<>();

            // Get the mtbdd1 transitions labeled by labels[i]
            Iterator<MtbddTransition> it = mtbdd1.iterator();
            while (it.hasNext()) {
                MtbddTransition t = it.next();
                if (t.getLabel().equals(label)) {
                    // System.out.print(t.getLabel() + " - ");
                    t_Mtbdd1.add(t);
                }

            }

            // Get the mtbdd2 transitions labeled by labels[i]
            it = mtbdd2.iterator();
            while (it.hasNext()) {
                MtbddTransition t = it.next();
                if (t.getLabel().equals(label)) {
                    // System.out.println(t.getLabel());
                    t_Mtbdd2.add(t);
                }
            }
            // System.out.println("sizes: " + t_Mtbdd1.size() + " - " +
            // t_Mtbdd2.size());
            // If mtbdd1.size = 0, it means there were no transition in mtbdd1
            // labelled with the current label
            if (t_Mtbdd1.size() > 0) {
                MtbddNode n_Mtbdd1 = new MtbddNode(t_Mtbdd1.getFirst()
                        .getTransition());
                MtbddNode n_Mtbdd2 = new MtbddNode(t_Mtbdd2.getFirst()
                        .getTransition());

                // Make boolean OR on mtbdd1 transitions for this label
                for (int j = 1; j < t_Mtbdd1.size(); j++) {
                    n_Mtbdd1 = mtbddOr(n_Mtbdd1, t_Mtbdd1.get(j)
                            .getTransition());
                }

                // Make boolean OR on mtbdd2 transitions for this label
                for (int j = 1; j < t_Mtbdd2.size(); j++) {
                    n_Mtbdd2 = mtbddOr(n_Mtbdd2, t_Mtbdd2.get(j)
                            .getTransition());
                }

                // Make boolean And on mtbdd1 and mtbdd2 for this label and add
                // the result the list of syncMtbdd transitions
                syncMtbdd.add(new MtbddTransition(label));
                syncMtbdd.getLast().setTransition(
                        new MtbddNode(Mtbdd.Nat_And(n_Mtbdd1.getPointer(),
                                n_Mtbdd2.getPointer())));
            }
        }

        return syncMtbdd;
    }

    // --------------------------------------------------//
    // STATIC METHODS //
    // --------------------------------------------------//

    /**
     * Create the boolean variables.
     * @param xVars
     * @param yVars
     * @param nbrVars
     */
    public static final void createBooleanVariables(
            final MtbddNodesArray xVars, final MtbddNodesArray yVars,
            final int nbrVars) {
        xVars.getNodes().clear();
        yVars.getNodes().clear();
        for (int i = 0; i < nbrVars; i++) {
            xVars.addNode(new MtbddNode());
            yVars.addNode(new MtbddNode());
        }
    }

    public final MtbddNode getInitialMtbdd() {
        return this.initialMtbdd;
    }

    public final MtbddNodesArray getInitialStates() {
        return this.initialStates;
    }

    public final List<Object> getLabels() {
        return this.labels;
    }

    /**
     * @param delta
     *            the list of transitions of the automaton
     * @return the list of labels for this automaton in a linkedlist
     */
    public static final List<Object> getLabels(final Set<Transition> delta) {
        List<Object> labels = new LinkedList<>();

        Iterator<Transition> it = delta.iterator();
        while (it.hasNext()) {
            Transition t = it.next();

            // If the label already is in the list containing all the labels, we
            // do nothing
            // else we add the label to the list
            boolean existLabel = false;
            for (int i = 0; i < labels.size(); i++) {
                if (labels.get(i).equals(t.label())) {
                    existLabel = true;
                    break;
                }
            }
            // Add the label to the list of labels
            if (!existLabel) {
                labels.add(t.label());
            }
        }

        return labels;
    }

    public final MtbddNode getMtbdd() {
        return this.mtbdd;
    }

    public final MtbddNode getTerminalMtbdd() {
        return this.terminalMtbdd;
    }

    public final MtbddNodesArray getTerminalStates() {
        return this.terminalStates;
    }

    public final List<MtbddTransition> getTransitions() {
        return this.transitions;
    }

    public final MtbddNodesArray getxVars() {
        return this.xVars;
    }

    public final MtbddNodesArray getyVars() {
        return this.yVars;
    }

    public final void setMtbdd(final MtbddNode mtbddIn) {
        this.mtbdd = mtbddIn;
    }
}
