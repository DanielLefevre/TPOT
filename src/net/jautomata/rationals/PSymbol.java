/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.jautomata.rationals;

/**
 * Symbol à utiliser pour qu'une transition soit probabiliste.
 *
 * @author adeft
 */
public class PSymbol {
    Object label;
    double probability;

    public PSymbol(Object label, double probability) {
        this.label = label;
        this.probability = probability;
    }

    public PSymbol(Object label) {
        this.label = label;
        this.probability = 1;
    }

    public Object getLabel() {
        return label;
    }

    public void setLabel(Object label) {
        this.label = label;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return label == null;
        }
        
        if (label.getClass() == obj.getClass()) {
            // it can equal an ordinary object if it equals his label parameter
            return label.equals(obj);
        } else if (getClass() == obj.getClass()) {
            // probabilistic transitions are never equal
            // except when compared with them selfs
            return this == obj;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }
    
    @Override
    public String toString() {
        return label.toString() + " \"" + probability + "\"";
    }


}
