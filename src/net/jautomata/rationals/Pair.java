package net.jautomata.rationals;

/**
 *
 * @author adeft
 */
public class Pair<S,T> {

    public S first;
    public T second;

    public Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }
    
    /**
     * Get the value of first
     *
     * @return the value of first
     */
    public S getFirst() {
        return first;
    }

    /**
     * Set the value of first
     *
     * @param first new value of first
     */
    public void setFirst(S first) {
        this.first = first;
    }
    /**
     * Get the value of second
     *
     * @return the value of second
     */
    public T getSecond() {
        return second;
    }

    /**
     * Set the value of second
     *
     * @param second new value of second
     */
    public void setSecond(T second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<S, T> other = (Pair<S, T>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 67 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }

}
