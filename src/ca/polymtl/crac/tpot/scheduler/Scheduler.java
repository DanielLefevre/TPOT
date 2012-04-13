package ca.polymtl.crac.tpot.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    List<EquivalenceClass> eqClasses;

    public Scheduler() {
        this.eqClasses = new ArrayList<>();
    }

    public final void addEqClass(final EquivalenceClass eqClass) {
        this.eqClasses.add(eqClass);

    }

    public final List<EquivalenceClass> getEqClasses() {
        return this.eqClasses;
    }

    public final void setEqClasses(final List<EquivalenceClass> eqClassesIn) {
        this.eqClasses = eqClassesIn;
    }
}
