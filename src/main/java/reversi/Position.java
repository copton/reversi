package reversi;

public class Position implements Comparable<Position> {
    final public int x;
    final public int y;

    public Position(int _x, int _y) {
        x = _x;
        y = _y;
    }

    public Position add(int _x, int _y)
    {
        return new Position(x + _x, y + _y);
    }
    
    public Position add(Position p)
    {
        return new Position(x + p.x, y + p.y);
    }

    public boolean isValid()
    {
        return (x >= 0 && x < GameBoard.size && y >= 0 && y < GameBoard.size);
    }

    @Override
    public String toString() {
        return String.format("Position<%s, %s>", x, y);
    }

    @Override
    public int hashCode() {
        return 23 * x * y;
    }

    @Override
    public int compareTo(Position other) {
        if(x == other.x) {
            return y - other.y;
        } else {
            return x - other.x;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) {
            return false;
        }
        return compareTo((Position)obj) == 0;
    }
}
