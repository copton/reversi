package reversi;

import java.util.ArrayList;

public interface Player {
    /**
     * Initialize the player.
     *
     * @param color
     *   the color of the player
     */
    public void initialize(Color color);

    /**
     * Ask the player to make its next move.
     *
     * When this function returns the position which was last reported to the
     * controler is taken as the player's final decision.
     *
     * This method is called in an extra thread. After a configurable timeout
     * this thread is interrupted and the last updated position which was
     * reported to the game controler is taken as the player's decision.
     *
     * @param board
     *   the current situation on the board
     * @param lastMove
     *   the last move of the opponent. If the game just started lastMove is null.
     * @param controler
     *   the game controler which takes the decisions on the next move
     */
    public void nextMove(GameBoard board, Position lastMove, GameControler controler);
}
