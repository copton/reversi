package player;

import java.util.Random;
import java.util.ArrayList;

import reversi.Color;
import reversi.Position;
import reversi.GameBoard;

public class RandomPlayer implements reversi.Player {
    private Color color;
    private Random rand;
   
    public void initialize(Color color)
    {
        this.color = color;
        this.rand = new Random();
    }

  

    public void nextMove(GameBoard board, Position lastMove, reversi.GameControler controler)
    {
      controler.logger("next move called: " + board);
      ArrayList<Position> possibleMoves = new ArrayList<Position>();
      for (int x=0; x<8; x++) {
        for (int y=0; y<8; y++) {
          Position pos = new Position(x, y);
          if (board.checkMove(pos, color)) {
            controler.logger("adding position " + pos);
            possibleMoves.add(pos);
          }
        }
      }

      if (! possibleMoves.isEmpty()) {
        int move = rand.nextInt(possibleMoves.size());
        controler.update(possibleMoves.get(move));
      }
    }
}
