
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * For running on the ecs grid
 */

public class OthelloBots {

    private static UCT bot1 = new UCT();
    private static UCT bot2 = new UCT();

    private Othello othello = new Othello();

    private static int iterations = 100;

    private static File file;

    public OthelloBots() {

        FileWriter fw;
        try {
            fw = new FileWriter(file, true);
            BufferedWriter out = new BufferedWriter(fw);
            out.write("iterations: " + iterations + " budget1: "
                    + bot1.getBudget() + " budget2: " + bot2.getBudget()
                    + " depth1: " + bot1.getDepth() + " depth2: "
                    + bot2.getDepth() + " ee1: " + bot1.getEE() + " ee2: "
                    + bot2.getEE() + "\n");

            UCT black;
            UCT white;
            UCT winner;
            int wins = 0;
            int sumWinMargins = 0;
            for (int i = 0; i < iterations; i++) {
                othello = new Othello();

                if (i % 2 == 0) {
                    black = bot1;
                    white = bot2;
                    bot1.setColor(Color.BLACK);
                    bot2.setColor(Color.WHITE);
                } else {
                    black = bot2;
                    white = bot1;
                    bot1.setColor(Color.WHITE);
                    bot2.setColor(Color.BLACK);
                }
                playOthello(black, white);
                if (othello.getWinner().equals(bot1.getColor())) {
                    winner = bot1;
                    wins++;
                    sumWinMargins += othello.getWinMargin();
                } else
                    winner = bot2;
                // System.out.println("winner: " + winner.getColor()
                // + "winMargin: " + othello.getWinMargin());
                out.write("winner: " + winner.getColor() + "winMargin: "
                        + othello.getWinMargin() + "\n");

            }
            // System.out.println("win%: " + (wins * 100.0 / iterations)
            // + " average win margin: " + sumWinMargins * 1.0 / wins
            // + "\n");
            out.write("\nwin%: " + (wins * 100.0 / iterations)
                    + " average win margin: " + sumWinMargins * 1.0 / wins
                    + "\n" + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playOthello(UCT black, UCT white) {

        while (!othello.gameOver()) {
            Othello o = new Othello();
            for (Move m : othello.getMoves()) {
                o.makeMove(m.getX(), m.getY());
            }
            Move m;
            if (othello.getTurn().equals(Color.BLACK))
                m = black.UCTSearch(o);
            else
                m = white.UCTSearch(o);
            othello.makeMove(m.getX(), m.getY());
        }

    }

    public static void main(String[] args) {
        if (args.length > 0) {
            int maxIterations = Integer.parseInt(args[0]);
            int budget1 = Integer.parseInt(args[1]);
            int budget2 = Integer.parseInt(args[2]);
            int depth1 = Integer.parseInt(args[3]);
            int depth2 = Integer.parseInt(args[4]);
            double ee1 = Double.parseDouble(args[5]);
            double ee2 = Double.parseDouble(args[6]);
            String title = args[7];
            file = new File(title + ".txt");

            iterations = maxIterations;
            bot1.setBudget(budget1);
            bot2.setBudget(budget2);
            bot1.setMaxDepth(depth1);
            bot2.setMaxDepth(depth2);
            bot1.setEE(ee1);
            bot2.setEE(ee2);
        }
        new OthelloBots();
    }
}
