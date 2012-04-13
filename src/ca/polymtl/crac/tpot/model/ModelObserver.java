package ca.polymtl.crac.tpot.model;

import ca.polymtl.crac.tpot.model.Opacity;

public interface ModelObserver {

    public void displayLpo(double lpoMin, double lpoMax);

    public void displayMinMaxOpacities(double[] opacities);

    public void displayResults(Opacity opacity);

    public void displayRpo(double rpoMin, double RpoMax);

    public void displayVpo(double vpoMin, double VpoMax);
}
