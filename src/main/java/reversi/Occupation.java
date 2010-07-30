package reversi;

public enum Occupation {
    FREE, RED, GREEN;

    public Occupation other() {
        if (this == RED) return GREEN;
        else return RED;
    }

    public static Occupation other(final Occupation c) {
        return c.other();
    }
}
