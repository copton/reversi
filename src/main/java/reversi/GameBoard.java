package reversi;

public interface GameBoard {
    public static final int size = 8;

   /**
    *
    * @throws IndexOutOfBoundsException
    */
    public Occupation getOccupation(Position pos);

    public boolean checkMove(Position pos, Color color);

    public GameBoard makeMove(Position pos, Color color);

    public int countStones(Color color);
}
