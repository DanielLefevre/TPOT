package ca.polymtl.crac.tpot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.PSymbol;
import net.jautomata.rationals.State;
import net.jautomata.rationals.Transition;
import net.jautomata.rationals.properties.binarytests.BinaryTest;
import net.jautomata.rationals.properties.binarytests.Inclusion;
import net.jautomata.rationals.properties.unarytests.IsEmpty;
import net.jautomata.rationals.transformations.Complement;
import net.jautomata.rationals.transformations.Intersection;
import net.jautomata.rationals.transformations.SynchronizationProduct;
import net.jautomata.rationals.transformations.UnProb;
import net.jautomata.rationals.transformations.Union;
import Jama.Matrix;

/**
 * @author Olivier Bachard, Daniel Lefevre
 */
public class Opacity {

    /**
     * The automaton.
     */
    private Automaton automaton;
    /**
     * The list of observations.
     */
    private List<Automaton> observations = new ArrayList<>();
    /**
     * The predicate.
     */
    private Automaton phi;
    /**
     * The value of the initial entropy.
     */
    private double initialEntropy = -1;
    /**
     * The value of the remaining entropy.
     */
    private double remainingEntropy = -1;
    /**
     * The value of the mutual information.
     */
    private double mutualInformation = -1;
    /**
     * The value of the LPO.
     */
    private double lpo = -1;
    /**
     * The value of the RPO.
     */
    private double rpo = -1;
    /**
     * The value of the VPO.
     */
    private double vpo = -1;

    /**
     * Creates the opacity, with an automaton, a list of observations, and the
     * predicate.
     * @param automatonIn
     *            the automaton
     * @param observationsIn
     *            the list of observations
     * @param phiIn
     *            the predicate
     */
    public Opacity(final Automaton automatonIn,
            final List<Automaton> observationsIn, final Automaton phiIn) {
        this.automaton = automatonIn;
        this.observations = observationsIn;
        this.phi = phiIn;
    }

    /**
     * Computes the probability for a automaton defined by transitions (whithin
     * this automaton). We meen the probability for the automaton to reach a
     * final state.
     * @param index
     *            the index of the equivalanceClase in the equivalanceClasses
     *            vector
     * @return the coresponding probability between 0 and 1
     */
    private static double computeProbability(final Automaton auto) {
        Set<Transition> transitions = auto.delta();
        Set<State> states = auto.states();
        Set<State> initials = auto.initials();
        Set<State> terminals = auto.terminals();
        Map<State, Integer> map = new HashMap<>();
        Iterator<State> it = states.iterator();
        int index = 0;
        while (it.hasNext()) {
            State s = it.next();
            map.put(s, index);
            ++index;
        }
        int n = index; // the number of states

        // this will be used to solve the system
        Matrix a = new Matrix(n, n, 0);

        // we initialise the matrix
        // given that the equation are implicit we need to explicitate them
        // so a -1 apears in the matrix for each state
        for (int i = 0; i < n; ++i) {
            a.set(i, i, -1);
        }

        // we add all the probabilities from transitions to the matrix
        Iterator<Transition> transitionIt = transitions.iterator();
        while (transitionIt.hasNext()) {
            Transition t = transitionIt.next();
            int i = map.get(t.start());
            int j = map.get(t.end());
            double probability = 1;
            if (t.label() instanceof PSymbol) {
                probability = ((PSymbol) t.label()).getProbability();
            }
            a.set(i, j, a.get(i, j) + probability);
        }

        // we define the matrix of free terms (1-column matrix)
        // this is the probability for a state to take an ending transition
        // so it's 1 for every final state and 0 for the others
        Matrix b = new Matrix(n, 1, 0);
        it = terminals.iterator();
        while (it.hasNext()) {
            b.set(map.get(it.next()), 0, -1);
        }

        // let's find the solution
        Matrix x = a.solve(b);

        // the total probability it's sum of probabilities from each node
        double probability = 0;
        it = initials.iterator();
        while (it.hasNext()) {
            probability += x.get(map.get(it.next()), 0);
        }

        return probability;
    }

    /**
     * Computes harmonic probabilistic opacity. It provides a mesure of how
     * vulnerable the system is. If Hpo = 1 if anly \phi is allways false
     * @return a value between 0 and 1 mesuring the opacity
     */
    public final double computeHpo() {
        SynchronizationProduct synchronisation = new SynchronizationProduct();

        // some general probabilities
        // double pPhi = computeProbability(synchronisation.transform(this.auto,
        // this.phi));
        // we want to compute the opacity
        double hpo = 0;

        // first, we compute the denominator (the sum)
        // sum ( P(O=o) / P(1_\phi = 0 | O = o) )
        for (int i = 0; i < this.observations.size(); ++i) {
            Automaton observable = this.observations.get(i);

            double pObs = computeProbability(synchronisation.transform(
                    this.automaton, observable));

            if (pObs == 0) { // I think this should never happen
                continue;
            }

            // for phi : P(1_\phi = 1 and O = o)
            double pPhiAndObs = computeProbability(synchronisation.transform(
                    synchronisation.transform(this.automaton, this.phi),
                    observable));
            // for phi complement : P(1_\phi = 0 and O = o)
            double pPhiCompAndObs = pObs - pPhiAndObs;

            hpo += pObs / (pPhiCompAndObs / pObs);
        }

        hpo = 1 / hpo;

        return hpo;
    }

    /**
     * Private function for computing initial entropy of the system. This is
     * H(1_\phi).
     */
    public final void computeInitialEntropy() {
        this.initialEntropy = 0;
        // H(Phi) = - Sum (P(i) log P(i))
    }

    /**
     * Computes liberal probabilistic opacity (symmetrical). It provides a
     * mesure of how insecure the system is.
     * @return a value between 0 and 1 mesuring the opacity
     */
    public final double computeLpo() {
        Complement complement = new Complement();
        BinaryTest inclusion = new Inclusion();
        SynchronizationProduct synchronisation = new SynchronizationProduct();

        Automaton phiComplement = complement.transform(this.phi);

        double opacity = 0;
        for (Automaton o : this.observations) {
            if (inclusion.test(o, this.phi) || inclusion.test(o, phiComplement)) {
                opacity += computeProbability(synchronisation.transform(
                        this.automaton, o));
            }
        }

        this.lpo = opacity;
        return opacity;
    }

    /**
     * Computes asymmetrical liberal probabilistic opacity. It provides a mesure
     * of how insecure the system is.
     * @return a value between 0 and 1 measuring the opacity
     */
    public final double computeLpoAsym() {
        BinaryTest inclusion = new Inclusion();
        SynchronizationProduct synchronisation = new SynchronizationProduct();

        double opacity = 0;
        for (Automaton o : this.observations) {
            if (inclusion.test(o, this.phi)) {
                opacity += computeProbability(synchronisation.transform(
                        this.automaton, o));
            }
        }

        return opacity;
    }

    public final void computeLpoForMtbddCase(final Automaton autoIn,
            final Automaton obsIn) {
        SynchronizationProduct synchronisation = new SynchronizationProduct();
        this.lpo = computeProbability(synchronisation.transform(autoIn, obsIn));
    }

    /**
     * Computes restrictive probabilistic opacity. It provides a mesure of how
     * insecure the system is. If Rpo = 0 then the system is not opaque
     * @return a value between 0 and 1 mesuring the opacity
     */
    public final double computeRpo() {
        SynchronizationProduct synchronisation = new SynchronizationProduct();

        // some general probabilities
        double pPhi = computeProbability(synchronisation.transform(
                this.automaton, this.phi));
        double pPhiComplement = computeProbability(this.automaton) - pPhi;

        // we want to compute the opacity

        // first we need the initial entropy = H(1_\phi)
        this.initialEntropy = 0;
        // H(Phi) = - Sum (P(i) log P(i))
        if ((pPhi != 1) && (pPhi != 0)) {
            this.initialEntropy -= pPhi * Math.log(pPhi) / Math.log(2);
            this.initialEntropy -= pPhiComplement * Math.log(pPhiComplement)
                    / Math.log(2);
        }

        // second we need the remaining entropy = H(1_\phi | O)
        this.remainingEntropy = 0;
        // H(Phi|O) = - Sum ( P ( i , o) * log ( P (i , o) )
        for (int i = 0; i < this.observations.size(); ++i) {
            Automaton observable = this.observations.get(i);

            double pObs = computeProbability(synchronisation.transform(
                    this.automaton, observable));

            if (pObs == 0) {
                continue;
            }

            // for phi
            double pPhiAndObs = computeProbability(synchronisation.transform(
                    synchronisation.transform(this.automaton, this.phi),
                    observable));
            // for phi complement
            double pPhiCompAndObs = pObs - pPhiAndObs;

            if (pPhiAndObs != 0) {
                this.remainingEntropy -= pPhiAndObs
                        * Math.log(pPhiAndObs / pObs) / Math.log(2);
            }

            if (pPhiCompAndObs != 0) {
                this.remainingEntropy -= pPhiCompAndObs
                        * Math.log(pPhiCompAndObs / pObs) / Math.log(2);
            }
        }

        // third, we find the mutual information
        // I(1_\phi ; O) = H(1_\phi) - H(1_\phi | O)
        this.mutualInformation = this.initialEntropy - this.remainingEntropy;

        // finnally, we have the rational probabilistic opacity
        // Rpo = 1 - I(1_\phi ; O) = 1 - H(1_\phi) + H(1_\phi | O)
        double opacity = 1 - this.mutualInformation;

        this.rpo = opacity;
        return opacity;
    }

    /**
     * Computes vulnerable probabilistic opacity. It provides a mesure of how
     * vulnerable the system is. If Rpo = 0 then the system is not opaque
     * @return a value between 0 and 1 mesuring the opacity
     */
    public final double computeVpo() {
        SynchronizationProduct synchronisation = new SynchronizationProduct();

        // some general probabilities
        // double pPhi = computeProbability(synchronisation.transform(this.auto,
        // this.phi));
        // we want to compute the opacity
        double vpOpacity = 0;
        // first, we compute the denominator (the sum)
        // sum ( P(O=o)log(1-V(1_\phi | O = o)))
        // where V(1_\phi | O = o) = max(P(1_\phi = 0 | O = o); P(1_\phi = 0 | O
        // = o))
        for (int i = 0; i < this.observations.size(); ++i) {
            Automaton observable = this.observations.get(i);

            double pObs = computeProbability(synchronisation.transform(
                    this.automaton, observable));

            if (pObs == 0) { // I think this should never happen
                continue;
            }

            // for phi : P(1_\phi = 1 and O = o)
            double pPhiAndObs = computeProbability(synchronisation.transform(
                    synchronisation.transform(this.automaton, this.phi),
                    observable));
            // for phi complement : P(1_\phi = 0 and O = o)
            double pPhiCompAndObs = pObs - pPhiAndObs;

            double vulnerability = Math.max(pPhiAndObs, pPhiCompAndObs) / pObs;

            vpOpacity += pObs * Math.log(1 - vulnerability) / Math.log(2);
        }

        vpOpacity = -1 / vpOpacity;

        this.vpo = vpOpacity;
        return vpOpacity;
    }

    /**
     * Getter.
     * @return the automaton
     */
    public final Automaton getAutomaton() {
        return this.automaton;
    }

    /**
     * Getter.
     * @return the initial entropy
     */
    public final double getInitialEntropy() {
        return this.initialEntropy;
    }

    /**
     * Getter.
     * @return the LPO
     */
    public final double getLpo() {
        return this.lpo;
    }

    /**
     * Getter.
     * @return the mutual information
     */
    public final double getMutualInformation() {
        return this.mutualInformation;
    }

    /**
     * Getter.
     * @return the observer
     */
    public final List<Automaton> getObs() {
        return this.observations;
    }

    /**
     * Getter.
     * @return the phi
     */
    public final Automaton getPhi() {
        return this.phi;
    }

    /**
     * Getter.
     * @return the remaining entropy
     */
    public final double getRemainingEntropy() {
        return this.remainingEntropy;
    }

    /**
     * Getter.
     * @return the RPO
     */
    public final double getRpo() {
        return this.rpo;
    }

    /**
     * Getter.
     * @return the VPO
     */
    public final double getVpo() {
        return this.vpo;
    }

    /**
     * Setter.
     * @param autoIn
     *            the new automaton
     */
    public final void setAutomaton(final Automaton autoIn) {
        this.automaton = autoIn;
    }

    /**
     * Setter.
     * @param lpoIn
     *            the new LPO
     */
    public final void setLpo(final double lpoIn) {
        this.lpo = lpoIn;
    }

    /**
     * Setter.
     * @param obsIn
     *            the new observer
     */
    public final void setObs(final List<Automaton> obsIn) {
        this.observations = obsIn;
    }

    /**
     * Setter.
     * @param phiIn
     *            the new phi
     */
    public final void setPhi(final Automaton phiIn) {
        this.phi = phiIn;
    }

    public final void validateData() throws IncorrectDataException {
        // Makes sure all attributes are not null.
        if (this.automaton == null) {
            throw new IncorrectDataException("System automaton is null.");
        }
        if (this.phi == null) {
            throw new IncorrectDataException("Predicate automaton is null.");
        }
        if (this.observations == null) {
            throw new IncorrectDataException("No observable defined.");
        }
        for (int i = 0; i < this.observations.size(); ++i) {
            if (this.observations.get(i) == null) {
                throw new IncorrectDataException("Observable " + i
                        + " is null.");
            }
        }

        Inclusion inclusion = new Inclusion();
        Automaton a = new UnProb().transform(this.automaton);

        // Tests if the union of observables equals the automaton.
        Automaton allObs = new Automaton();
        Union union = new Union();
        for (Automaton o : this.observations) {
            allObs = union.transform(allObs, o);
        }

        if (!inclusion.test(a, allObs) || !inclusion.test(allObs, a)) {
            throw new IncorrectDataException(
                    "The union of observables should equal the automaton.");
        }

        // we check if the probabilistic automaton includes the predicate
        if (!inclusion.test(this.phi, a)) {
            throw new IncorrectDataException(
                    "The automaton doesn't include the predicate.");
        }

        // checking if observation classes colide
        Intersection intersection = new Intersection();
        for (int i = 0; i < this.observations.size(); ++i) {
            for (int j = i + 1; j < this.observations.size(); ++j) {
                if (!IsEmpty.test(intersection.transform(
                        this.observations.get(j), this.observations.get(i)))) {
                    throw new IncorrectDataException(
                            "Collision between observables " + i + " and " + j);
                }
            }
        }
    }

    /**
     * Implements an exception used in the validation of the data.
     * @author Daniel Lefevre
     */
    public class IncorrectDataException extends Exception {

        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = -5269556630169873750L;

        /**
         * Constructor.
         * @param s
         *            the explanation of the error
         */
        public IncorrectDataException(final String s) {
            super(s);
        }
    }
}
