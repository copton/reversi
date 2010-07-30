package reversi;

public class Color extends Occupation {
    protected Color() { }
    final static Color RED = new Color();
    final static Color GREEN = new Color();

    public static Color other(Color c)
    {
        if (c == RED) return GREEN;
        else return RED;
    }
}
