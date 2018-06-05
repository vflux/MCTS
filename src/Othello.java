
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Othello implements BoardGame {

	private Color[][] board = new Color[8][8];

	private Color turn = Color.BLACK;
	private boolean gameOver = false;
	private Color winner = null;
	private int winMargin = 0;

	private int count = 0; // number of moves played
	private int blackScore = 2;
	private int whiteScore = 2;

	private List<Move> moves = new ArrayList<Move>();

	public Othello() {
		board[3][3] = Color.BLACK;
		board[3][4] = Color.WHITE;
		board[4][4] = Color.BLACK;
		board[4][3] = Color.WHITE;
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

		// check for stones to flip
		flipStones(x, y);

		// update score
		updateScore();

		// check if game is over
		setCount(getCount() + 1);
		if (getCount() == 60) {
			gameOver = true;
			computeWinner();
			return true;
		}

		// update color
		turn = (turn.equals(Color.BLACK)) ? Color.WHITE : Color.BLACK;

		// check if moves available, else update color
		boolean canPlay = false;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (isLegal(i, j))
					canPlay = true;
			}
		}
		if (!canPlay) {
			turn = (turn.equals(Color.BLACK)) ? Color.WHITE : Color.BLACK;
			if (!canPlay()) {
				gameOver = true;
				computeWinner();
				return true;
			}
		}

		// print board
		// printBoard();

		return true;
	}

	public void updateScore() {
		blackScore = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == null)
					continue;
				if (board[i][j].equals(Color.BLACK))
					blackScore++;
			}
		}
		whiteScore = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == null)
					continue;
				if (board[i][j].equals(Color.WHITE))
					whiteScore++;
			}
		}
	}

	public void computeWinner() {
		winner = (blackScore > 32) ? Color.BLACK : Color.WHITE;
		winMargin = Math.max(blackScore, 64 - blackScore)
				- Math.min(blackScore, 64 - blackScore);
	//	String w = (winner.equals(Color.BLACK)) ? "black" : "white";
		// System.out.println("winner: " + w);
	}

	public void playRandomMove() {
		Random r = new Random();
		boolean hasPlayed = false;
		while (!hasPlayed) {
			if (!canPlay())
				turn = (turn.equals(Color.BLACK)) ? Color.WHITE : Color.BLACK;
			int x = r.nextInt(8);
			int y = r.nextInt(8);
			if (isLegal(x, y)) {
				makeMove(x, y);
				hasPlayed = true;
			}
		}
	}

	public void lastMove() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (isLegal(i, j))
					makeMove(i, j);
			}
		}
	}

	public boolean isLegal(int x, int y) {
		// check no stone already there
		if (board[x][y] != null)
			return false;
		// check stone flips some other stones
		if (!canFlipStones(x, y))
			return false;

		return true;
	}

	public boolean canFlipStones(int x, int y) {
		// check up
		if (isOppositeColor(x, y + 1)) {
			for (int i = 2; !isOutOfBounds(x, y + i); i++)
				if (board[x][y + i] == null)
					break;
				else if (isSameColor(x, y + i)) {
					return true;
				}
		}

		// check right
		if (isOppositeColor(x + 1, y)) {
			for (int i = 2; !isOutOfBounds(x + i, y); i++)
				if (board[x + i][y] == null)
					break;
				else if (isSameColor(x + i, y)) {
					return true;
				}
		}

		// check down
		if (isOppositeColor(x, y - 1)) {
			for (int i = 2; !isOutOfBounds(x, y - i); i++)
				if (board[x][y - i] == null)
					break;
				else if (isSameColor(x, y - i)) {
					return true;
				}
		}

		// check left
		if (isOppositeColor(x - 1, y)) {
			for (int i = 2; !isOutOfBounds(x - i, y); i++)
				if (board[x - i][y] == null)
					break;
				else if (isSameColor(x - i, y)) {
					return true;
				}
		}

		// check top left
		if (isOppositeColor(x - 1, y + 1)) {
			for (int i = 2; !isOutOfBounds(x - i, y + i); i++)
				if (board[x - i][y + i] == null)
					break;
				else if (isSameColor(x - i, y + i))
					return true;
		}

		// check top right
		if (isOppositeColor(x + 1, y + 1)) {
			for (int i = 2; !isOutOfBounds(x + i, y + i); i++)
				if (board[x + i][y + i] == null)
					break;
				else if (isSameColor(x + i, y + i))
					return true;
		}

		// check bottom right
		if (isOppositeColor(x + 1, y - 1)) {
			for (int i = 2; !isOutOfBounds(x + i, y - i); i++)
				if (board[x + i][y - i] == null)
					break;
				else if (isSameColor(x + i, y - i))
					return true;
		}

		// check bottom left
		if (isOppositeColor(x - 1, y - 1)) {
			for (int i = 2; !isOutOfBounds(x - i, y - i); i++)
				if (board[x - i][y - i] == null)
					break;
				else if (isSameColor(x - i, y - i)) {
					return true;
				}
		}

		return false;
	}

	public void flipStones(int x, int y) {

		// check up
		if (isOppositeColor(x, y + 1)) {
			for (int i = 2; !isOutOfBounds(x, y + i); i++)
				if (board[x][y + i] == null)
					break;
				else if (isSameColor(x, y + i)) {
					// capture stones between the two
					for (int j = 1; j < i; j++)
						board[x][y + j] = turn.equals(Color.WHITE) ? Color.WHITE
								: Color.BLACK;
					break;
				}
		}

		// check right
		if (isOppositeColor(x + 1, y)) {
			for (int i = 2; !isOutOfBounds(x + i, y); i++)
				if (board[x + i][y] == null)
					break;
				else if (isSameColor(x + i, y)) {
					// capture stones between the two
					for (int j = 1; j < i; j++)
						board[x + j][y] = turn.equals(Color.WHITE) ? Color.WHITE
								: Color.BLACK;
					break;
				}
		}

		// check down
		if (isOppositeColor(x, y - 1)) {
			for (int i = 2; !isOutOfBounds(x, y - i); i++)
				if (board[x][y - i] == null)
					break;
				else if (isSameColor(x, y - i)) {
					// capture stones between the two
					for (int j = 1; j < i; j++)
						board[x][y - j] = turn.equals(Color.WHITE) ? Color.WHITE
								: Color.BLACK;
					break;
				}
		}

		// check left
		if (isOppositeColor(x - 1, y)) {
			for (int i = 2; !isOutOfBounds(x - i, y); i++)
				if (board[x - i][y] == null)
					break;
				else if (isSameColor(x - i, y)) {
					// capture stones between the two
					for (int j = 1; j < i; j++)
						board[x - j][y] = turn.equals(Color.WHITE) ? Color.WHITE
								: Color.BLACK;
					break;
				}
		}

		// check top left
		if (isOppositeColor(x - 1, y + 1)) {
			for (int i = 2; !isOutOfBounds(x - i, y + i); i++)
				if (board[x - i][y + i] == null)
					break;
				else if (isSameColor(x - i, y + i)) {
					// capture stones between the two
					for (int j = 1; j < i; j++)
						board[x - j][y + j] = turn.equals(Color.WHITE) ? Color.WHITE
								: Color.BLACK;
					break;
				}
		}

		// check top right
		if (isOppositeColor(x + 1, y + 1)) {
			for (int i = 2; !isOutOfBounds(x + i, y + i); i++)
				if (board[x + i][y + i] == null)
					break;
				else if (isSameColor(x + i, y + i)) {
					// capture stones between the two
					for (int j = 1; j < i; j++)
						board[x + j][y + j] = turn.equals(Color.WHITE) ? Color.WHITE
								: Color.BLACK;
					break;
				}
		}

		// check bottom right
		if (isOppositeColor(x + 1, y - 1)) {
			for (int i = 2; !isOutOfBounds(x + i, y - i); i++)
				if (board[x + i][y - i] == null)
					break;
				else if (isSameColor(x + i, y - i)) {
					// capture stones between the two
					for (int j = 1; j < i; j++)
						board[x + j][y - j] = turn.equals(Color.WHITE) ? Color.WHITE
								: Color.BLACK;
					break;
				}
		}

		// check bottom left
		if (isOppositeColor(x - 1, y - 1)) {
			for (int i = 2; !isOutOfBounds(x - i, y - i); i++)
				if (board[x - i][y - i] == null)
					break;
				else if (isSameColor(x - i, y - i)) {
					// capture stones between the two
					for (int j = 1; j < i; j++)
						board[x - j][y - j] = turn.equals(Color.WHITE) ? Color.WHITE
								: Color.BLACK;
					break;
				}
		}
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
		if (x > 7 || y > 7 || x < 0 || y < 0)
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
		if (!gameOver) {
			return new double[] { 0, 0 };
		}
		if (winner.equals(Color.BLACK)) {
			return new double[] { 1, -1 };
		}
		return new double[] { -1, 1 };
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

	public String getWinnerString() {
		String w = (winner.equals(Color.BLACK)) ? "Black" : "White";
		return w;
	}

	public int getBlackScore() {
		return blackScore;
	}

	public void setBlackScore(int blackScore) {
		this.blackScore = blackScore;
	}

	public int getWhiteScore() {
		return whiteScore;
	}

	public void setWhiteScore(int whiteScore) {
		this.whiteScore = whiteScore;
	}

	public void setWinner(Color winner) {
		this.winner = winner;
	}

	public int getWinMargin() {
		return this.winMargin;
	}

	public Object clone() {
		Othello o = new Othello();
		o.board = board.clone();
		o.setCount(count);
		o.gameOver = gameOver;
		o.turn = turn;
		return o;
	}

	public static void main(String[] args) {
		Othello o = new Othello();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (!o.gameOver) {
			try {
				int x = Integer.parseInt(br.readLine());
				int y = Integer.parseInt(br.readLine());
				o.makeMove(8 - y, x);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isDecisiveGame() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Move getDecisiveMove() {
		// TODO Auto-generated method stub
		return null;
	}

}
