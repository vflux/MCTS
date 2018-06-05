
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

	private BoardGame state;

	// incoming action
	private Move move;

	private int visited;
	private double reward;
	private List<Node> children = new ArrayList<Node>();
	private Map<Move, Node> moveToChild = new HashMap<Move, Node>();
	private Node parent;

	public Node(BoardGame o) {
		this.state = o;
	}

	public BoardGame getState() {
		return state;
	}

	public void setState(BoardGame othello) {
		this.state = othello;
	}

	public int getVisited() {
		return visited;
	}

	public void setVisited(int visited) {
		this.visited = visited;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public void addChild(Node child) {
		this.children.add(child);
	}

	public void addChild(Move m, Node child) {
		this.moveToChild.put(m, child);
	}

	public Map<Move, Node> getMoveToChild() {
		return this.moveToChild;
	}

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

}
