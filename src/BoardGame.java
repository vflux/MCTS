import java.awt.Color;
import java.util.List;

public interface BoardGame {

	int getCount();

	List<Move> getMoves();

	boolean makeMove(int x, int y);

	Color getTurn();

	boolean gameOver();

	int getNumberOfPossibleMoves();

	void playRandomMove();

	double[] reward();

	Color[][] getBoard();

	boolean isLegal(int i, int j);

	boolean isDecisiveGame();

	Move getDecisiveMove();

}
