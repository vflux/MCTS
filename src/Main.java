
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Main implements KeyListener {

	private Othello othello = new Othello();
	private UCT uct = new UCT();
	static int budget = 5000;
	static int maxDepth = 1000;
	static double ee = 0.5;

	private List<Move> moves = new ArrayList<Move>();

	private static JFrame frame;
	private JMenuBar menuBar;
	private JMenu fileMenu, helpMenu, rules;
	private JMenuItem menuItem;
	private JComponent board;
	private JSlider rolloutsSlider;
	private JSlider eeSlider;
	private JSlider depthSlider;
	private JTextArea score;

	private Move currentSquare;
	private Color botColor;

	// final int WIDTH = 480;
	final int WIDTH = 500*3/2;
	final int LENGTH = 820*3/2;

	public Main() {
		chooseColor();
		setupInterface();
		uct.setBudget(budget);
		uct.setMaxDepth(maxDepth);
		uct.setEE(ee);

		if (othello.getTurn().equals(botColor))
			botMove();

	}

	public void chooseColor() {
		int value = JOptionPane
				.showOptionDialog(
						null,
						"Which color do you want? (Black plays first but loses if it's a draw)",
						"Color choice", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, new String[] {
								"Black", "White" }, // this is the array
						"default");

		if (value == 0)
			botColor = Color.WHITE;
		else
			botColor = Color.black;
	}

	public void botMove() {
		if (othello.gameOver()) {
			System.out.println(othello.getWinner() + " has won");
			return;
		}
		Othello o = new Othello();
		for (Move m : moves) {
			System.out.println(m);
			o.makeMove(m.getX(), m.getY());
		}

		// if only one move is left play it
		if (o.getCount() == 59)
			if (o.canPlay()) {
				for (int i = 0; i < othello.getBoard().length; i++) {
					for (int j = 0; j < othello.getBoard()[0].length; j++) {
						if (othello.isLegal(i, j))
							if (othello.makeMove(i, j))
								moves.add(new Move(i, j));

					}
				}
			} else {
				;
			}

		else {
			Move m = uct.UCTSearch(o);
			if (othello.makeMove(m.getX(), m.getY()))
				moves.add(m);

			if (othello.getTurn().equals(botColor))
				botMove();
		}

	}

	public void setupInterface() {
		// Set up a window .
		frame = new JFrame("Othello");
		frame.setSize(LENGTH, WIDTH * 5 / 4);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				endProgram();

			}
		});

		board = new JComponent() {
			protected void paintComponent(Graphics g) {
				redraw(g);
			}
		};
		frame.add(board, BorderLayout.CENTER);

		// menu bar
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");

		menuItem = new JMenuItem("New Game(N)");
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Create a new game");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				frame.dispose();
				new Main();

			}
		});
		fileMenu.add(menuItem);
		fileMenu.addSeparator();
		menuItem = new JMenuItem("Exit (ESC) ");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				endProgram();

			}
		});
		fileMenu.add(menuItem);

		menuBar.add(fileMenu);

		helpMenu = new JMenu("Help");
		rules = new JMenu("Rules");
		rules.add(menuItem);

		helpMenu.add(rules);
		menuBar.add(helpMenu);

		frame.setJMenuBar(menuBar);

		// add mouseMotionListener to respond to the user moving the mouse
		// around on the board
		board.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				// update currentSquare
				updateCurrentSquare(e.getX(), e.getY());
				board.repaint();
			}

		});

		// add mouseListener to respond to the user clicking on a position
		board.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				move(e);
				board.repaint();

			}
		});

		// make a panel for sliders
		JPanel sliders = new JPanel();
		sliders.setLayout(new BoxLayout(sliders, BoxLayout.PAGE_AXIS));
		sliders.setBorder(new EmptyBorder(25, 5, 0, 10));
		frame.add(sliders, BorderLayout.EAST);

		ChangeListener listener = new SliderListener();

		sliders.add(new JLabel("Random Simulations"));
		rolloutsSlider = getSlider(0, 5000, budget, 1000, 200, listener);
		sliders.add(rolloutsSlider);
		sliders.add(Box.createVerticalStrut(20));

		// JTextField rolloutsField = new JTextField();
		// rolloutsField.setBounds(0, 0, 1, 2);
		// sliders.add(rolloutsField);

		sliders.add(new JLabel("Exploration Factor(%)"));
		eeSlider = getSlider(0, 100, (int) (ee * 100), 10, 5, listener);
		sliders.add(eeSlider);
		sliders.add(Box.createVerticalStrut(20));

		sliders.add(new JLabel("Maximum Search Tree Depth"));
		depthSlider = getSlider(0, 1000, maxDepth, 200, 100, listener);
		sliders.add(depthSlider);
		sliders.add(Box.createVerticalStrut(20));

		score = new JTextArea();
		// score.setBounds(0, 0, 7, 20);
		// score.setText("Black: " + othello.getBlackScore() + " White: "
		// + othello.getWhiteScore());
		sliders.add(score);

		board.setFocusable(true);
		board.addKeyListener(this);
		// frame.pack();
		frame.setVisible(true);
	}

	public void move(MouseEvent e) {
		if (othello.gameOver()) {
			System.out.println(othello.getWinner() + " has won");
			return;
		}
		int x = e.getX();
		int y = e.getY();
		int row = (int) (x / (WIDTH / 8));
		int column = (int) (y / (WIDTH / 8));
		if (row >= 0 && column >= 0 && row <= 7 && column <= 7)
			if (othello.makeMove(row, column)) {
				moves.add(new Move(row, column));
				redraw(board.getGraphics());
				// display winner if over
				if (othello.gameOver()) {
					Color winner = othello.getWinner();
					if (winner.equals(botColor))
						JOptionPane.showMessageDialog(frame, "You lost!",
								"Information", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(frame, "You won!",
								"Information", JOptionPane.INFORMATION_MESSAGE);
					return;
				} else if (othello.getTurn().equals(botColor)) {
					botMove();
					redraw(board.getGraphics());
				}
			}
		if (othello.gameOver()) {
			Color winner = othello.getWinner();
			if (winner.equals(botColor))
				JOptionPane.showMessageDialog(frame, "You lost!",
						"Information", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(frame, "You won!", "Information",
						JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void updateCurrentSquare(int x, int y) {
		int row = (int) (x / (WIDTH / 8));
		int col = (int) (y / (WIDTH / 8));
		if (row >= 0 && col >= 0 && row <= 7 && col <= 7)
			currentSquare = new Move(row, col);
	}

	public void redraw(Graphics g) {
		g = (Graphics2D) g;

		g.setColor(Color.green);
		g.setColor(new Color(0, 220, 0));
		g.fillRect(0, 0, WIDTH, WIDTH);

		g.setColor(Color.black);
		g.drawLine(0, 0, 0, WIDTH);
		g.drawLine(WIDTH, 0, WIDTH, WIDTH);
		g.drawLine(0, 0, WIDTH, 0);
		g.drawLine(0, WIDTH, WIDTH, WIDTH);

		for (int i = 1; i < 8; i++) {
			g.drawLine(WIDTH * i / 8, 0, WIDTH * i / 8, WIDTH);
			g.drawLine(0, WIDTH * i / 8, WIDTH, WIDTH * i / 8);
		}

		// highlight currentsquare, if any
		if (currentSquare != null) {
			if (othello.isLegal(currentSquare.getX(), currentSquare.getY()))
				highlightSquareGreen(currentSquare.getX(),
						currentSquare.getY(), g);
			else
				highlightSquareRed(currentSquare.getX(), currentSquare.getY(),
						g);
		}

		// draw stones
		Color[][] board = othello.getBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == null)
					continue;
				else if (board[i][j].equals(Color.black)) {
					g.setColor(Color.black);
				} else if (board[i][j].equals(Color.WHITE)) {
					g.setColor(Color.white);
				}
				g.fillOval(i * WIDTH / 8 + 10, j * WIDTH / 8 + 10, WIDTH / 12,
						WIDTH / 12);
			}
		}

		// update score
		// score = new JLabel("Black: " + othello.getBlackScore() + " White: "
		// + othello.getWhiteScore());
		String s = "Black: " + othello.getBlackScore() + " White: "
				+ othello.getWhiteScore();
		s += "\n\n" + "Random simulations: " + uct.getBudget();
		s += "\n" + "Exploration Factor: " + (int) (uct.getEE() * 100) + "%";
		s += "\n" + "Maximum Search Tree Depth: " + uct.getDepth();
		score.setText(s);

	}

	public void highlightSquareRed(int row, int col, Graphics g) {
		g = (Graphics2D) g;
		Double dim = 15.95;
		g.setColor(new Color(255, 0, 0, 75));
		((Graphics2D) g).fill(new Rectangle2D.Double(1 + WIDTH * row / 8, 1
				+ WIDTH * col / 8, WIDTH / 8, WIDTH / 8));
	}

	public void highlightSquareGreen(int row, int col, Graphics g) {
		g = (Graphics2D) g;
		Double dim = 15.95;
		g.setColor(new Color(173, 255, 92, 75));
		((Graphics2D) g).fill(new Rectangle2D.Double(1 + WIDTH * row / 8, 1
				+ WIDTH * col / 8, WIDTH / 8, WIDTH / 8));
	}

	/**
	 * Open pop-up box to confirm that the player would like to exit.
	 * 
	 * @return confirmation as an integer
	 */
	public void endProgram() {
		int exit = JOptionPane.showConfirmDialog(null,
				"Are you sure you would like to exit?", "Exit Confirmation",
				JOptionPane.YES_NO_OPTION);
		if (exit != 1) {
			System.exit(1);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();

		if (code == KeyEvent.VK_N) {
			frame.dispose();
			new Main();
		}
		board.repaint();

	}

	public JSlider getSlider(int min, int max, int init, int mjrTkSp,
			int mnrTkSp, ChangeListener listener) {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, init);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(mjrTkSp);
		slider.setMinorTickSpacing(mnrTkSp);
		slider.setPaintLabels(true);
		slider.addChangeListener(listener);
		return slider;
	}

	private class SliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider) e.getSource();

			if (slider.equals(rolloutsSlider)) {
				int value = slider.getValue();
				if (value == 0)
					value = 1;
				uct.setBudget(value);
			}

			else if (slider.equals(eeSlider)) {
				int value = slider.getValue();
				uct.setEE(value * 1.0 / 100);
			}

			else if (slider.equals(depthSlider)) {
				int value = slider.getValue();
				if (value == 0)
					value = 1;
				uct.setMaxDepth(value);
			}
			redraw(board.getGraphics());
		}
	}

	public static void main(String[] args) {
		if (args.length > 0)
			budget = Integer.parseInt(args[0]);
		if (args.length > 1)
			maxDepth = Integer.parseInt(args[1]);
		if (args.length > 2)
			ee = Double.parseDouble(args[2]);
		new Main();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
