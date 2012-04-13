package ca.polymtl.crac.tpot.model;

public interface ModelObservable {

    public void addObserver(ModelObserver obs);

    public void deleteObserver();

    public void updateMinMaxOpacities(double[] opacities);

    public void updateObserver();

    public void updateObserverLpo();

    public void updateObserverRpo();

    public void updateObserverVpo();
}
