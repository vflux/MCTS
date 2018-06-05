import java.awt.Color;

public class UCT {

	// parameters
	private int budget = 1000;
	double Cp = 1 / Math.sqrt(2);
	private boolean decisiveOn = false;
	private int maxDepth = 1000;
	private double eeratio = 0.5;

	BoardGame state;
	private Color color;
	
	public UCT(){
		
	}

	public UCT(BoardGame o) {
		Move m = UCTSearch(o);
		System.out.println(m);
	}

	public Move UCTSearch(BoardGame s0) {

		// clone s0
		if (s0 instanceof Othello)
			this.state = new Othello();
		else
			this.state = new Gomoku();
		for (Move m : s0.getMoves())
			state.makeMove(m.getX(), m.getY());

		// make node which contains clone
		Node v0 = new Node(state);

		// try to play decisive move if need be
		if (state.isDecisiveGame() && decisiveOn) {
			Move decisiveMove = state.getDecisiveMove();
			if (decisiveMove != null)
				return decisiveMove;
		}

		for (int i = 0; i < budget; i++) {
			Node v1 = treePolicy(v0);
			double[] delta = defaultPolicy(v1.getState());
			backup(v1, delta);

			// reset v0's state
			if (s0 instanceof Othello)
				this.state = new Othello();
			else
				this.state = new Gomoku();
			for (Move m : s0.getMoves())
				state.makeMove(m.getX(), m.getY());
			v0.setState(state);
		}
		return bestChild(v0, 0).getMove();
	}

	public Node treePolicy(Node v) {
		if (v == null)
			System.out.println("why is node null?");
		else if (v.getState() == null)
			System.out.println("why is v's state null?");

		int depth = 0;
		while (!v.getState().gameOver() && depth < maxDepth) {
			if (v.getState().getNumberOfPossibleMoves() != v.getChildren()
					.size()) {
				return expand(v);
			} else {
				v = bestChild(v, Cp);
				depth++;
			}
		}
		return v;
	}

	public double[] defaultPolicy(BoardGame game) {
		BoardGame s;
		if (game instanceof Othello)
			s = new Othello();
		else
			s = new Gomoku();
		for (Move m : game.getMoves())
			s.makeMove(m.getX(), m.getY());

		while (!s.gameOver()) {
			s.playRandomMove();
		}
		return s.reward();
	}

	public Node expand(Node v) {
		BoardGame o;
		if (v.getState() instanceof Othello)
			o = new Othello();
		else
			o = new Gomoku();
		for (Move m : v.getState().getMoves())
			o.makeMove(m.getX(), m.getY());
		for (int i = 0; i < o.getBoard().length; i++) {
			for (int j = 0; j < o.getBoard()[0].length; j++) {
				if (o.isLegal(i, j)
						&& v.getMoveToChild().get(new Move(i, j)) == null) {
					o.makeMove(i, j);
					Node vprime = new Node(o);
					vprime.setMove(new Move(i, j));
					v.addChild(vprime);
					v.addChild(new Move(i, j), vprime);
					vprime.setParent(v);
					return vprime;
				}
			}
		}
		System.out.println("no good");
		return null; // shouldn't get here
	}

	public Node bestChild(Node v, double c) {
		double maxReward = Double.NEGATIVE_INFINITY;
		Node bestChild = null;

		if (v.getChildren().isEmpty())
			System.out.println("has no children for some reason");

		for (Node v1 : v.getChildren()) {
			double reward = UCB1(v1, c);
			if (reward >= maxReward) {
				bestChild = v1;
				maxReward = reward;
			}

		}
		if (bestChild == null)
			System.out.println("watch out");
		return bestChild;
	}

	public double UCB1(Node v1, double c) {
		int visited = v1.getVisited();
		double exploitation = v1.getReward() / visited;
		double exploration;
		if (visited == 0) {
			exploration = 100;
		} else {
			exploration = c
					* Math.sqrt(2 * Math.log(v1.getParent().getVisited())
							/ visited);
		}
		return (eeratio * exploration + (1 - eeratio) * exploitation);
	}

	/** Was trying to make the worst bot possible */
	public Node worstChild(Node v, double c) {
		double maxReward = Double.POSITIVE_INFINITY;
		Node worstChild = null;

		if (v.getChildren().isEmpty())
			System.out.println("has no children for some reason");

		for (Node v1 : v.getChildren()) {
			System.out.println(v1.getReward());
			System.out.println(v1.getVisited());
			double exploitation = v1.getReward() / v1.getVisited();
			double exploration;
			if (v1.getVisited() == 0) {
				exploration = 100;
			} else {
				exploration = c
						* Math.sqrt(2 * Math.log(v.getVisited())
								/ v1.getVisited());
			}
			if (exploitation + exploration <= maxReward) {
				worstChild = v1;
				maxReward = exploitation + exploration;
			}

		}
		if (worstChild == null)
			System.out.println("watch out");
		return worstChild;
	}

	public void backup(Node v, double[] delta) {
		while (v != null) {
			v.setVisited(v.getVisited() + 1);
			double reward = v.getReward();
			Color turn = v.getState().getTurn();
			if (turn.equals(Color.BLACK)) // incoming move is w
				reward += delta[1];
			else
				reward += delta[0];
			v.setReward(reward);
			v = v.getParent();
		}
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public void setEE(double ee) {
		this.eeratio = ee;
	}

	public void setMaxDepth(int depth) {
		this.maxDepth = depth;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getEE() {
		return eeratio;
	}

	public int getDepth() {
		return maxDepth;
	}

}
