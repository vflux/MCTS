/** Plays two bots against each other */

public class BotsCaller {

	static int iterations = 20;
	static int defaultBudget = 200;
	static int defaultDepth = 100;
	static double defaultEE = 0.5;

	static String fileName;

	public static void main(String[] args) {
		if (args.length > 0) {
			String s = args[0];
			fileName = args[3];
			if (args[4] != null)
				iterations = Integer.parseInt(args[4]);
			if (s.equals("budget")) {
				int b1 = Integer.parseInt(args[1]);
				int b2 = Integer.parseInt(args[2]);
				testBudget(b1, b2);
			} else if (s.equals("depth")) {
				int d1 = Integer.parseInt(args[1]);
				int d2 = Integer.parseInt(args[2]);
				testDepth(d1, d2);
			} else if (s.equals("ee")) {
				double ee1 = Double.parseDouble(args[1]);
				double ee2 = Double.parseDouble(args[2]);
				testEE(ee1, ee2);
			}

		}

	}

	private static void testBudget(int b1, int b2) {
		// for (int bot2budget = 1; bot2budget <= 1000; bot2budget *= 2) {
		// for (int bot1budget = bot2budget; bot1budget <= 1000; bot1budget *=
		// 2) {
		String[] args = new String[10];
		args[0] = Integer.toString(iterations);
		args[1] = Integer.toString(b1);
		args[2] = Integer.toString(b2);
		args[3] = Integer.toString(defaultDepth);
		args[4] = Integer.toString(defaultDepth);
		args[5] = Double.toString(defaultEE);
		args[6] = Double.toString(defaultEE);
		args[7] = "budget" + fileName;
		OthelloBots.main(args);
		// }
		// }

	}

	private static void testDepth(int d1, int d2) {
		// for (int bot2depth = 1; bot2depth <= 9; bot2depth += 1) {
		// for (int bot1depth = bot2depth; bot1depth <= 10; bot1depth += 1) {
		String[] args = new String[10];
		args[0] = Integer.toString(iterations);
		args[1] = Integer.toString(defaultBudget);
		args[2] = Integer.toString(defaultBudget);
		args[3] = Integer.toString(d1);
		args[4] = Integer.toString(d2);
		args[5] = Double.toString(defaultEE);
		args[6] = Double.toString(defaultEE);
		args[7] = "depth" + fileName;
		OthelloBots.main(args);
		// }
		// }

	}

	private static void testEE(double ee1, double ee2) {
		// for (int bot2ee = 0; bot2ee <= 1; bot2ee += 0.1) {
		// for (int bot1ee = bot2ee; bot1ee <= 1; bot1ee += 0.1) {
		String[] args = new String[10];
		args[0] = Integer.toString(iterations);
		args[1] = Integer.toString(defaultBudget);
		args[2] = Integer.toString(defaultBudget);
		args[3] = Integer.toString(defaultDepth);
		args[4] = Integer.toString(defaultDepth);
		args[5] = Double.toString(ee1);
		args[6] = Double.toString(ee2);
		args[7] = "ee" + fileName;
		OthelloBots.main(args);
		// }
		// }

	}

}
