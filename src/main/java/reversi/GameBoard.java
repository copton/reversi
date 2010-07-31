package reversi;

public interface GameBoard {
   /**
    *
    * @throws IndexOutOfBoundsException
    */
    public Occupation getOccupation(Position pos);

    public boolean checkMove(Position pos, Color color);

    public GameBoard makeMove(Position pos, Color color);

    public int countStones(Color color);

    public String dump();
}
