package reversi;

public interface GameBoard {
    public static final int size = 8;

   /**
    *
    * @throws IndexOutOfBoundsException
    */
    public Occupation getOccupation(Position pos);

    public boolean checkMove(Position pos, Occupation color);

    public GameBoard makeMove(Position pos, Occupation color);

    public int countStones(Occupation color);

    public String dump();
}
