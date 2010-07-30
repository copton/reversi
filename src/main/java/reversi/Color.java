package reversi;

public class Color extends Occupation {
    protected Color() {}
    public final static Color RED = new Color();
    public final static Color GREEN = new Color();

    public static Color other(Color color) {
        if (color == RED) return GREEN;
        else return RED;
    }

    public Color other() {
        return Color.other(this);
    }
}
