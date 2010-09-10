package reversi;

public class Occupation implements java.io.Serializable, Comparable<Occupation> {

    public final static Occupation FREE = new Occupation(0, " ");

    private final int index;
    public final String representation;

    protected Occupation(final int index, final String representation) {
        this.index = index;
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }

    @Override
    public int hashCode() {
        return 23 * index;
    }

    @Override
    public int compareTo(final Occupation other) {
        return index - other.index;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Occupation)) {
            return false;
        }
        return compareTo((Occupation)obj) == 0;
    }
}
