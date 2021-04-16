package gui;

//acts as somewhat of a logic testing suite
public class FuzzyChessConsoleDisplay {
	public static void displayTurn(int turn, int subturn, String board) {
		System.out.print(String.format("Player %d's Turn:", turn+1));
		System.out.println(String.format("\t\tMove %d: ", subturn+1));		
		System.out.println(board);
	}
	
	public static void displayRules() {
		//todo if you want.... kind of annoying tbh
	}
	
	public static void displayAttack(String p1, String p2, int roll, boolean offset, String result, int[] rollsNeeded) {
		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(String.format("%s attempts to attack %s", p1, p2));
		String rolls = "[";
		for(int i = 0; i < rollsNeeded.length; i++) {
			rolls += rollsNeeded[i];
			if(i < rollsNeeded.length -1) {
				rolls += ", ";
			}
		}
		rolls += "]";
		System.out.println("Rolls Needed: " + rolls);
		if(offset) {
			System.out.println("Knight is offbalance from charge - subtracting 1 from roll");
		}
		System.out.println(String.format("Rolled %d\nResult: %s", roll, result));
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
	}
	
	public static void displayCapturedPieces(String p1_captures, String p2_captures) {
		System.out.println("Player 1 Captures:");
		System.out.println(p1_captures);
		System.out.println("Player 2 Captures:");
		System.out.println(p2_captures);
	}
	
	public static void displayOptions() {
		System.out.println("Options:\ne: endturn\nq: quit game\nc: show captured peices\nr: rules\nx, y: select tile");
	}
}
