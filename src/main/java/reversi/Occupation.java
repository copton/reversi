package reversi;

public enum Occupation {
    FREE, RED, GREEN;

    public static Occupation other(final Occupation c) {
        if (c == RED) return GREEN;
        else return RED;
    }
}
