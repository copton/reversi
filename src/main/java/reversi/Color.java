package reversi;

public class Color extends Occupation {

    public final static Color RED = new Color(1, "X");
    public final static Color GREEN = new Color(2, "O");

    protected Color(final int index, final String name) {
        super(index, name);
    }

    public static Color other(Color color) {
        if (color.equals(RED)) {
            return GREEN;
        } else {
            return RED;
        }
    }

    public Color other() {
        return Color.other(this);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Color)) {
            return false;
        }
        return compareTo((Color)obj) == 0;
    }

}
