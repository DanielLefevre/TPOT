package ca.polymtl.crac.tpot.model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ca.polymtl.crac.tpot.mtbdd.Mtbdd;

public abstract class Model implements ModelObservable {

    /**
     * Nom du fichier du modèle.
     */
    private String file;
    /**
     * The linked opacity.
     */
    private Opacity opacity;
    /**
     * The observers of this model.
     */
    private List<ModelObserver> observers = new ArrayList<>();

    /**
     * Logger.
     */
    public static final Logger LOGGER = Logger.getLogger(Model.class.getName());

    /**
     * Private constructor.
     */
    public Model() {
    }

    /*
     * (non-Javadoc)
     * @see gui.models.ModelObservable#addObserver(gui.models.ModelObserver)
     */
    @Override
    public final void addObserver(final ModelObserver obs) {
        this.observers.add(obs);
    }

    public abstract void buildModel(String fileIn) throws FileNotFoundException;

    /**
     * Computes the LPO.
     */
    public final void computeLpo() {
        this.opacity.computeLpo();
        this.updateObserverLpo();
    }

    /**
     * Computes the LPO symbolically.
     */
    public final void computeLpoSymb() {
        this.opacity.setLpo(Mtbdd.computeLpo(this.opacity));
        this.updateObserverLpo();
    }

    /**
     * Computes all opacities (LPO, RPO, initial entropy, remaining entropy,
     * mutual information, VPO).
     */
    public final void computeOpacity() {
        this.opacity.computeLpo();
        this.opacity.computeRpo();
        this.opacity.getInitialEntropy();
        this.opacity.getRemainingEntropy();
        this.opacity.getMutualInformation();
        this.opacity.computeVpo();

        this.updateObserver();
    }

    /**
     * Computes RPO.
     */
    public final void computeRpo() {
        this.opacity.computeRpo();
        this.updateObserverRpo();
    }

    /**
     * Computes VPO.
     */
    public final void computeVpo() {
        this.opacity.computeVpo();
        this.updateObserverVpo();
    }

    @Override
    public final void deleteObserver() {
        this.observers = new ArrayList<>();
    }

    /**
     * Getter.
     * @return the file
     */
    public final String getFile() {
        return this.file;
    }

    /**
     * Getter.
     * @return the opacity
     */
    public final Opacity getOpacity() {
        return this.opacity;
    }

    // public final void internScheduleModel() {
    // Model.LOGGER.log(Level.INFO, "Ordonnancement interne du modele "
    // + this.file + "...");
    // new InternScheduleModel().start();
    // }

    // /**
    // * TODO : in english. Effectue les mesures d'opacités pour tous les
    // chemins
    // * pour lesquels le choix est non déterministe. Attention : l'automate
    // doit
    // * alors etre non déterministe uniquement au début jusqu'à une certaine
    // * profondeur et sans boucles dans la partie non déterministe.
    // */
    // public final void nonDetScheduleModel() {
    //
    // NonDetScheduler nds = new NonDetScheduler();
    // try {
    // nds.computeOpacities(this.automaton, this.file);
    // nds.getMinMaxOpacities();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // double[] opacities = new double[6];
    // opacities[0] = nds.getMinLpo();
    // opacities[1] = nds.getMaxLpo();
    // opacities[2] = nds.getMinRpo();
    // opacities[3] = nds.getMaxRpo();
    // opacities[4] = nds.getMinVpo();
    // opacities[5] = nds.getMaxVpo();
    //
    // updateMinMaxOpacities(opacities);
    // }

    /**
     * Setter.
     * @param fileIn
     *            the new file
     */
    public final void setFile(final String fileIn) {
        this.file = fileIn;
    }

    /**
     * Setter.
     * @param opacityIn
     *            the new opacity
     */
    public final void setOpacity(final Opacity opacityIn) {
        this.opacity = opacityIn;
    }

    /*
     * (non-Javadoc)
     * @see computingopacity.ModelObservable#updateMinMaxOpacities(double[])
     */
    @Override
    public final void updateMinMaxOpacities(final double[] opacities) {
        for (ModelObserver o : this.observers) {
            o.displayMinMaxOpacities(opacities);
        }
    }

    /*
     * (non-Javadoc)
     * @see computingopacity.ModelObservable#updateObserver()
     */
    @Override
    public final void updateObserver() {
        for (ModelObserver o : this.observers) {
            o.displayResults(this.opacity);
        }
    }

    /*
     * (non-Javadoc)
     * @see computingopacity.ModelObservable#updateObserverLpo()
     */
    @Override
    public final void updateObserverLpo() {
        for (ModelObserver o : this.observers) {
            o.displayLpo(this.opacity.getLpo(), this.opacity.getLpo());
        }
    }

    /*
     * (non-Javadoc)
     * @see computingopacity.ModelObservable#updateObserverRpo()
     */
    @Override
    public final void updateObserverRpo() {
        for (ModelObserver o : this.observers) {
            o.displayRpo(this.opacity.getRpo(), this.opacity.getRpo());
        }
    }

    /*
     * (non-Javadoc)
     * @see computingopacity.ModelObservable#updateObserverVpo()
     */
    @Override
    public final void updateObserverVpo() {
        for (ModelObserver o : this.observers) {
            o.displayVpo(this.opacity.getVpo(), this.opacity.getVpo());
        }
    }

    // /**
    // * Effectue l'ordonnancement interne du modèle dans un nouveau thread.
    // * @author Olivier Bachard, Daniel Lefevre
    // */
    // private class InternScheduleModel extends Thread {
    //
    // /**
    // * Default constructor.
    // */
    // public InternScheduleModel() {
    // // Nothing.
    // }
    //
    // @Override
    // public void run() {
    // Scheduler sched = new Scheduler();
    //
    // // Acquisition de l'ordonnanceur interne
    // try {
    // InputStream ips = new FileInputStream("input/"
    // + Model.this.getFile() + ".sched");
    // InputStreamReader ipsr = new InputStreamReader(ips);
    // BufferedReader br = new BufferedReader(ipsr);
    // String out = "";
    // String ligne;
    // while ((ligne = br.readLine()) != null) {
    // out += ligne + "\n";
    // }
    // br.close();
    // System.out.print("Parsing de l'ordonnanceur ......");
    // sched = InternSchedulerGramm.parseEquivalenceClasses(out);
    // Model.LOGGER.log(Level.INFO, "...... ok");
    //
    // } catch (
    // IOException | ParseException e) {
    // Model.LOGGER.log(Level.INFO, "Error : " + e);
    // }
    //
    // Model.LOGGER.log(Level.INFO, "Ordonnancement ......");
    // Model.this.getAutomaton().schedule(sched);
    // Model.LOGGER.log(Level.INFO, "...... ok");
    // Model.LOGGER.log(Level.INFO, "Ordonnancement termine avec succes");
    // }
    // }
}
