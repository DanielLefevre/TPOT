package ca.polymtl.crac.tpot;

import ca.polymtl.crac.tpot.model.BasicModel;
import ca.polymtl.crac.tpot.model.Model;

/**
 * Main class.
 * @author Daniel Lefevre
 */
public final class Main {

    /**
     * Private constructor.
     */
    private Main() {
    }

    /**
     * Main.
     * @param args
     *            not used
     */
    public static void main(final String[] args) {
        Model model = new BasicModel("files/Exemple/exemple");
        try {
            model.buildModel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.computeOpacity();
        System.out.println("LPO : " + model.getOpacity().getLpo());
        System.out.println("RPO : " + model.getOpacity().getRpo());
        System.out.println("VPO : " + model.getOpacity().getVpo());
        System.out.println("Initial entropy : "
                + model.getOpacity().getInitialEntropy());
        System.out.println("Remaining entropy : "
                + model.getOpacity().getRemainingEntropy());
        System.out.println("Mutual information : "
                + model.getOpacity().getMutualInformation());
    }
}
