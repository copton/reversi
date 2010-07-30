package reversi;

public class Position {
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
}
