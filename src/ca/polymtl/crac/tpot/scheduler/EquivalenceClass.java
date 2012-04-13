package ca.polymtl.crac.tpot.scheduler;

import java.util.ArrayList;
import java.util.List;

public class EquivalenceClass {

    List<String> eqActions;

    public EquivalenceClass() {
        this.eqActions = new ArrayList<>();
    }

    public final void addEqAction(final String action) {
        this.eqActions.add(action);
    }

    public final List<String> getEqActions() {
        return this.eqActions;
    }

    public final void setEqActions(final List<String> eqActionsIn) {
        this.eqActions = eqActionsIn;
    }
}
