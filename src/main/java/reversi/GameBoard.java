package reversi;

public interface GameBoard {
   /**
    * Get the occupation of a position on the board
    *
    * @param pos
    *   the position on the board
    * @return
    *   the occupation on the given position on the board
    * @throws IndexOutOfBoundsException
    */
    public Occupation getOccupation(Position pos);

    /**
     * Check if a move is possible according to the Reversi rules.
     *
     * @param pos
     *     the position on which the stone is set
     * @param color
     *     the color of the stone
     * @return
     *  true iff the given move is possible.
     */
    public boolean checkMove(Position pos, Color color);

    public class InvalidMoveException extends Exception { }

    /**
     * Make a move.
     * @param pos
     *   the position on which the stone is set
     * @param color
     *   the color of the stone
     * @return
     *   a new game board with the new situation after the move
     * @throws InvalidMoveException
     *  if the given move is not valid according to the Reversi rules. 
     *  i.e. if checkMove with the same parameters would return false
     */
    public GameBoard makeMove(Position pos, Color color) throws InvalidMoveException;
}
