package ca.polymtl.crac.tpot.scheduler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jautomata.rationals.Automaton;
import net.jautomata.rationals.State;

import ca.polymtl.crac.tpot.model.Opacity;

public class NonDetScheduler {

    List<Opacity> opacities;
    double minLpo = -1;
    double minRpo = -1;
    double minVpo = -1;
    double maxLpo = -1;
    double maxRpo = -1;
    double maxVpo = -1;

    public NonDetScheduler() {
        this.opacities = new ArrayList<>();
    }

    public final void computeOpacities(final Automaton auto,
            final String regFile) throws IOException {
        // List<Automaton> subAutos = auto.scheduleNonDet();
        // System.out.println("nbre d'auto: " + subAutos.size());
        //
        // for (Automaton automaton : subAutos) {
        // System.out.println("A = " + automaton.delta().toString());
        // System.out.println("Q = " + automaton.states().toString());
        // System.out.println("I = " + automaton.initials().toString());
        // System.out.println("T = " + automaton.terminals().toString()
        // + "\n\n");
        //
        // for (State state : automaton.states()) {
        // System.out.println("trans from: " + automaton.delta(state));
        // }
        //
        // // Acquisition du fichier contenant les observations et le prédicat
        // InputStream is1 = null;
        // try {
        // is1 = new FileInputStream("input/" + regFile + ".reg");
        // } catch (FileNotFoundException e) {
        // System.err.println("Cannot open " + regFile + " for reading");
        // System.exit(1);
        // }
        //
        // // Construit les observations et le prédicat
        // // Opacity op = new Opacity(automaton);
        // // op.readObsAndPredicate(is1);
        //
        // // TODO : change that thing !
        // try {
        // op.validateData();
        //
        // System.out.println("Obs: \n" + op.getObs().toString());
        // System.out.println("Phi: \nb" + op.getPhi().toString());
        // System.out.println("Auto: \nb" + automaton.toString());
        //
        // System.out.println("The Lpo of the system is : "
        // + op.computeLpo());
        // System.out.println("The Rpo of the system is : "
        // + op.computeRpo());
        // System.out.println("Initial entropy :    "
        // + op.getInitialEntropy());
        // System.out.println("Remaining entropy :  "
        // + op.getRemainingEntropy());
        // System.out.println("Mutual information : "
        // + op.getMutualInformation());
        // System.out.println("The Vpo of the system is : "
        // + op.computeVpo());
        //
        // this.opacities.add(op);
        // } catch (Exception ex) {
        // Logger.getLogger(NonDetScheduler.class.getName()).log(
        // Level.SEVERE, null, ex);
        // }
        // }
    }

    public final double getMaxLpo() {
        return this.maxLpo;
    }

    public final double getMaxRpo() {
        return this.maxRpo;
    }

    public final double getMaxVpo() {
        return this.maxVpo;
    }

    public final double getMinLpo() {
        return this.minLpo;
    }

    public final void getMinMaxOpacities() {
        if (this.opacities.size() == 0) {
            return;
        }

        this.minLpo = this.opacities.get(0).getLpo();
        this.maxLpo = this.opacities.get(0).getLpo();
        this.minRpo = this.opacities.get(0).getRpo();
        this.maxRpo = this.opacities.get(0).getRpo();
        this.minVpo = this.opacities.get(0).getVpo();
        this.maxVpo = this.opacities.get(0).getVpo();

        for (int i = 1; i < this.opacities.size(); i++) {
            if (this.opacities.get(i).getLpo() > this.maxLpo) {
                this.maxLpo = this.opacities.get(i).getLpo();
            }
            if (this.opacities.get(i).getLpo() < this.minLpo) {
                this.minLpo = this.opacities.get(i).getLpo();
            }
            if (this.opacities.get(i).getRpo() > this.maxRpo) {
                this.maxRpo = this.opacities.get(i).getRpo();
            }
            if (this.opacities.get(i).getRpo() < this.minRpo) {
                this.minRpo = this.opacities.get(i).getRpo();
            }
            if (this.opacities.get(i).getVpo() > this.maxVpo) {
                this.maxVpo = this.opacities.get(i).getVpo();
            }
            if (this.opacities.get(i).getVpo() < this.minVpo) {
                this.minVpo = this.opacities.get(i).getVpo();
            }
        }

    }

    public final double getMinRpo() {
        return this.minRpo;
    }

    public final double getMinVpo() {
        return this.minVpo;
    }

    public final List<Opacity> getOpacities() {
        return this.opacities;
    }

    public final void setMaxLpo(final double maxLpoIn) {
        this.maxLpo = maxLpoIn;
    }

    public final void setMaxRpo(final double maxRpoIn) {
        this.maxRpo = maxRpoIn;
    }

    public final void setMaxVpo(final double maxVpoIn) {
        this.maxVpo = maxVpoIn;
    }

    public final void setMinLpo(final double minLpoIn) {
        this.minLpo = minLpoIn;
    }

    public final void setMinRpo(final double minRpoIn) {
        this.minRpo = minRpoIn;
    }

    public final void setMinVpo(final double minVpoIn) {
        this.minVpo = minVpoIn;
    }

}
