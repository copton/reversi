package reversi;

/**
 * Report the decision of the player to the framework
 */
public interface GameControler {
    /**
     * Update the decision.
     *
     * Call this function as often as needed to update the decision on the next
     * move.
     *
     * If this method is never called the player passes.
     */
    void update(Position position);


    /**
     * Log some information
     */
    void logger(String text);
}
