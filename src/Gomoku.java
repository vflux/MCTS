
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Gomoku implements BoardGame {

	public final int boardSize = 15;
	public final int inARow = 5;
	private Color[][] board = new Color[boardSize][boardSize];

	private Color turn = Color.BLACK;
	private boolean gameOver = false;
	private Color winner = null;
	private int count = 0; // number of moves played
	private List<Move> moves = new ArrayList<Move>();

	private int winValue = 1;

	Random r = new Random();

	public Gomoku() {
	}

	public boolean makeMove(int x, int y) {
		// check if legal move
		if (!isLegal(x, y)) {
			System.out.println("illegal move");
			return false;
		}

		// place move
		board[x][y] = turn;
		moves.add(new Move(x, y));
		count++;

		// check if this is a winning move
		if (isWinningMove(x, y)) {
			gameOver = true;
			winner = turn;
			// String w = (winner.equals(Color.BLACK)) ? "black" : "white";
			// System.out.println("winner: " + w);
			return true;
		}

		// update color
		turn = (turn.equals(Color.BLACK)) ? Color.WHITE : Color.BLACK;

		// check if moves are still available
		if (!canPlay())
			gameOver = true;

		// print board
		// printBoard();

		return true;
	}

	public void playRandomMove() {
		r = new Random();
		boolean hasPlayed = false;
		while (!hasPlayed) {
			if (!canPlay())
				turn = (turn.equals(Color.BLACK)) ? Color.WHITE : Color.BLACK;
			int x = r.nextInt(boardSize);
			int y = r.nextInt(boardSize);
			if (isLegal(x, y)) {
				makeMove(x, y);
				hasPlayed = true;
			}
		}
	}

	public void pass() {
		turn = (turn.equals(Color.BLACK)) ? Color.WHITE : Color.BLACK;
	}

	public boolean isLegal(int x, int y) {
		// check no stone already there
		if (board[x][y] != null)
			return false;

		return true;
	}

	public boolean isWinningMove(int x, int y) {
		// check horizontal
		for (int i = 0; i < boardSize - (inARow - 1); i++) {
			boolean ok = true;
			for (int j = i; j < i + inARow; j++) {
				if (board[j][y] == null || !board[j][y].equals(turn)) {
					ok = false;
					break;
				}
			}
			if (ok)
				return true;
		}

		// check vertical
		for (int i = 0; i < boardSize - (inARow - 1); i++) {
			boolean ok = true;
			for (int j = i; j < i + inARow; j++) {
				if (board[x][j] == null || !board[x][j].equals(turn)) {
					ok = false;
					break;
				}
			}
			if (ok)
				return true;
		}

		// check TLBR diagonal

		// first find starting point
		int a = x;
		int b = y;
		while (a >= 0 && b < boardSize) {
			a--;
			b++;
		}
		// then as before
		for (int i = 0; i < boardSize - 1; i++) {
			boolean ok = true;
			for (int j = 0; j < inARow; j++) {
				if (isOutOfBounds(a + i + j, b - i - j) || board[a + i + j][b - i - j] == null
						|| !board[a + i + j][b - i - j].equals(turn)) {
					ok = false;
					break;
				}
			}
			if (ok)
				return true;
		}

		// check BLTR diagonal
		a = x;
		b = y;
		while (a < boardSize && b >= 0) {
			a++;
			b++;
		}

		for (int i = 0; i < boardSize - 1; i++) {
			boolean ok = true;
			for (int j = 0; j < inARow; j++) {
				if (isOutOfBounds(a - i - j, b - i - j) || board[a - i - j][b - i - j] == null
						|| !board[a - i - j][b - i - j].equals(turn)) {
					ok = false;
					break;
				}
			}
			if (ok)
				return true;
		}

		return false;
	}

	public boolean isBlack(int x, int y) {
		if (isOutOfBounds(x, y))
			return false;
		if (board[x][y] == null || board[x][y].equals(Color.WHITE))
			return false;
		return true;
	}

	public boolean isWhite(int x, int y) {
		if (isOutOfBounds(x, y))
			return false;
		if (board[x][y] == null || board[x][y].equals(Color.BLACK))
			return false;
		return true;
	}

	public boolean isOppositeColor(int x, int y) {
		if (turn.equals(Color.BLACK))
			return isWhite(x, y);
		return isBlack(x, y);
	}

	public boolean isSameColor(int x, int y) {
		if (turn.equals(Color.WHITE))
			return isWhite(x, y);
		return isBlack(x, y);
	}

	public boolean isOutOfBounds(int x, int y) {
		if (x > boardSize - 1 || y > boardSize - 1 || x < 0 || y < 0)
			return true;
		return false;
	}

	public boolean canPlay() {
		boolean canPlay = false;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (isLegal(i, j))
					canPlay = true;
			}
		}
		return canPlay;
	}

	public int getNumberOfPossibleMoves() {
		int count = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (isLegal(i, j))
					count++;
			}
		}
		return count;
	}

	/** Returns a move that leads to a win for either side, if one exists */
	public Move getDecisiveMove() {
		// if the game is already over calling this method doesn't make sense
		if (gameOver) {
			System.out.println("The game is already over");
			return null;
		}

		// first check for decisive moves
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				// clone this state
				Gomoku g = new Gomoku();
				for (Move m : this.moves)
					g.makeMove(m.getX(), m.getY());

				// make move and check if it wins the game
				if (!g.isLegal(i, j))
					continue;
				g.makeMove(i, j);
				if (g.getWinner() != null)
					return new Move(i, j);
			}
		}

		// if there are none then check for antidecisive moves
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				// clone this state
				Gomoku g = new Gomoku();
				for (Move m : this.moves)
					g.makeMove(m.getX(), m.getY());
				g.pass();

				// make move and check if it wins the game
				if (!g.isLegal(i, j))
					continue;
				g.makeMove(i, j);
				if (g.getWinner() != null)
					return new Move(i, j);
			}
		}

		return null;
	}

	public void printBoard() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == null)
					System.out.print("  ");
				else if (board[i][j].equals(Color.BLACK))
					System.out.print("B ");
				else if (board[i][j].equals(Color.WHITE))
					System.out.print("W ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public void printWinner() {
		String w = (winner.equals(Color.BLACK)) ? "black" : "white";
		System.out.println("winner: " + w);
	}

	public Color[][] getBoard() {
		return this.board;
	}

	public List<Move> getMoves() {
		return this.moves;
	}

	public boolean gameOver() {
		return gameOver;
	}

	public double[] reward() {
		// TODO: fix this method
		if (!gameOver || winner == null) {
			return new double[] { 0, 0 };
		}

		if (winner.equals(Color.BLACK)) {
			return new double[] { winValue, -winValue };
		}
		return new double[] { -winValue, winValue };
	}

	public Color getTurn() {
		return this.turn;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Color getWinner() {
		return winner;
	}

	public void setWinner(Color winner) {
		this.winner = winner;
	}

	public void randomise() {
		r = new Random();
	}

	public Object clone() {
		Gomoku o = new Gomoku();
		o.board = board.clone();
		o.setCount(count);
		o.gameOver = gameOver;
		o.turn = turn;
		return o;
	}

	public static void main(String[] args) {
		// Gomoku o = new Gomoku();
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(System.in));
		// while (!o.gameOver) {
		// try {
		// int x = Integer.parseInt(br.readLine());
		// int y = Integer.parseInt(br.readLine());
		// o.makeMove(boardSize - y, x);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	public boolean isDecisiveGame() {
		return true;
	}

}
