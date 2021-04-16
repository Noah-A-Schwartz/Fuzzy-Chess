package models;

import java.util.ArrayList;

public class ChessPiece {
	private BoardPosition position;
	private boolean hasMoved;
	private char pieceID;
	private int direction;
	
	/*							king	queen	knight	bishop	rook	pawn */
	private static int[][][] rolls = {{{4,5,6},{4,5,6},{4,5,6},{4,5,6},{5,6},{1,2,3,4,5,6}}, 
							   {{4,5,6},{4,5,6},{4,5,6},{4,5,6},{5,6},{2,3,4,5,6}},
							   {{6},{6},{4,5,6},{4,5,6},{5,6},{2,3,4,5,6}},
							   {{5,6},{5,6},{5,6},{4,5,6},{5,6},{3,4,5,6}},
							   {{4,5,6},{4,5,6},{5,6},{5,6},{6},{5,6}},
							   {{6},{6},{6},{5,6},{6},{4,5,6}}};
	public final static int DOWN = 1;
	public final static int UP = -1;
	
	
	public ChessPiece(BoardPosition pos, char id, int dir) {
		position = pos;
		pieceID = id;
		direction = dir;
		hasMoved = false;
	}
	
	private ChessPiece(BoardPosition pos, char id, int dir, boolean hasMoved) {
		position = pos;
		pieceID = id;
		direction = dir;
		this.hasMoved = hasMoved;
	}
	
	private String getName() {
		switch(pieceID) {
		case 'p':
			return "Black Pawn";
		case 'P':
			return "White Pawn";
		case 'r':
			return "Black Rook";
		case 'R':
			return "White Rook";
		case 'n':
			return "Black Knight";
		case 'N':
			return "White Knight";
		case 'b':
			return "Black Bishop";
		case 'B':
			return "White Bishop";
		case 'q':
			return "Black Queen";
		case 'Q':
			return "White Queen";
		case 'k':
			return "Black King";
		case 'K':
			return "White King";
		default:
			return null;
		}
	}
	
	private static int convertIDtoArrayPosition(char id) {
		switch(id) {
		case 'k':
		case 'K':
			return 0;
		case 'q':
		case 'Q':
			return 1;
		case 'n':
		case 'N':
			return 2;
		case 'b':
		case 'B':
			return 3;
		case 'r':
		case 'R':
			return 4;
		case 'p':
		case 'P':
			return 5;
		default:
			return 0;	
		}
			
	}
	
	public char getid() {
		return pieceID;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public BoardPosition getPosition() {
		return position;
	}
	
	public void setPosition(BoardPosition pos) {
		position = pos;
	}

	public int[] getRolls(ChessPiece other) {
		return rolls[convertIDtoArrayPosition(pieceID)][convertIDtoArrayPosition(other.getid())];
	}
	
	public static double getChanceOfCapture(char id1, char id2) {
		double result = rolls[convertIDtoArrayPosition(id1)][convertIDtoArrayPosition(id2)].length/6.0;
		return result;
	}
	
	public static boolean isWhite(char pieceID) {
		return pieceID == 'P' || pieceID == 'N' || pieceID == 'R' || pieceID == 'B' || pieceID == 'Q' || pieceID == 'K';
	}
	
	public static boolean isBlack(char pieceID) {
		return pieceID == 'p' || pieceID == 'n' || pieceID == 'r' || pieceID == 'b' || pieceID == 'q' || pieceID == 'k';
	}
	
	
	//returns possible locations a piece may step to
	//disregards if there is a piece in that location
	//or if its out of bounds - just the possibilities
	//the rest is checked later
	public ArrayList<BoardPosition> getActions() {
		int startX = position.getX();
		int startY = position.getY();
		ArrayList<BoardPosition> possibleActions = new ArrayList<BoardPosition>();

		//pawn movements - only able to move forward and forward diagonal
		possibleActions.add(new BoardPosition(startX, startY + direction));
		possibleActions.add(new BoardPosition(startX + 1, startY + direction));
		possibleActions.add(new BoardPosition(startX - 1, startY + direction));
		
		//if its a pawn - end here
		if(pieceID == 'p' || pieceID == 'P') return possibleActions;

		//rest of piece movements
		//backward and back diagonal
		possibleActions.add(new BoardPosition(startX, startY - direction));
		possibleActions.add(new BoardPosition(startX + 1, startY - direction));
		possibleActions.add(new BoardPosition(startX - 1, startY - direction));
		
		//left and right
		possibleActions.add(new BoardPosition(startX + 1, startY));
		possibleActions.add(new BoardPosition(startX - 1, startY));
		
		return possibleActions;
	}
	
	//returns number of steps a piece can move
	public int getMoveCount() {
		switch(pieceID) {
			case 'p':				
			case 'P':
			case 'r':
			case 'R':
			case 'b':
			case 'B':
				return 1;
			case 'q':
			case 'Q':
			case 'k':
			case 'K':
				return 3;
			case 'n':
			case 'N':
				return 5;
			default:
				return 0;
		}
	}
	
	public void setHasMoved(boolean flag) {
		hasMoved = flag;
	}
	
	public boolean getHasMoved() {
		return hasMoved;
	}
	
	public ChessPiece copy() {
		return new ChessPiece(new BoardPosition(position.getX(), position.getY()), pieceID, direction, hasMoved);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ChessPiece))
			return false;
		ChessPiece other = (ChessPiece)o;
		return (this.direction == other.direction && this.pieceID == other.pieceID && this.position.equals(other.getPosition()));
	}
	
	@Override
	public int hashCode() {
		int result = position.hashCode();
		result = 31 * result + Character.hashCode(pieceID);
		result = 31 * result + Integer.hashCode(direction);
		return result;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
