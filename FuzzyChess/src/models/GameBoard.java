package models;

import java.util.ArrayList;

public class GameBoard{
	/*positions of all pieces in game - lowercase is black, uppercase is white
	 * r - rook
	 * n - knight
	 * b - bishop
	 * q - queen
	 * k - king
	 * p - pawn */
	private char[][] boardState = {{'r','n','b','q','k','b','n','r'},
								   {'p','p','p','p','p','p','p','p'},
								   {'-','-','-','-','-','-','-','-'},
								   {'-','-','-','-','-','-','-','-'},
								   {'-','-','-','-','-','-','-','-'},
								   {'-','-','-','-','-','-','-','-'},
								   {'P','P','P','P','P','P','P','P'},
								   {'R','N','B','Q','K','B','N','R'}};
	
	/*colors of all board tiles
	 * w - white
	 * b - black
	 * m - possible movement space
	 * c - possible capture space  */
	private char[][] boardColors = {{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'}};
	
	//i = y, j = x
	public void updateBoardState(BoardPosition oldPosition, BoardPosition newPosition) {
		boardState[newPosition.getY()][newPosition.getX()] = boardState[oldPosition.getY()][oldPosition.getX()];
		boardState[oldPosition.getY()][oldPosition.getX()] = '-';
	}
	
	//list movements
	//list chesspieces
	//list captures
	//selected location
	//updates the boards colors for display
	//displays what peices are active
	//what piece is selected
	//what available moves and captures are
	//available to the piece
	public void updateBoardColors(ArrayList<BoardPosition> activeCorps, ArrayList<BoardPosition> movements, ArrayList<BoardPosition> captures,
			BoardPosition selectedPosition, BoardPosition selectedEnemy) {
		resetBoardColors();
		if(activeCorps != null) {
			for(BoardPosition p : activeCorps) {
				boardColors[p.getY()][p.getX()] = 'a';
			}
		}
		
		if(movements != null) {
			for(BoardPosition p : movements) {
				boardColors[p.getY()][p.getX()] = 'm';
			}
		}
		
		if(captures != null) {
			for(BoardPosition p : captures) {
				boardColors[p.getY()][p.getX()] = 'c';
			}
		}
		if(selectedPosition != null) {
			boardColors[selectedPosition.getY()][selectedPosition.getX()] = 'a';
		}
		if(selectedEnemy != null) {
			boardColors[selectedEnemy.getY()][selectedEnemy.getX()] = 'c';
		}
	}
	
	public void setBoardState(char[][] newBoardState) {
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState.length; j++) {
				boardState[i][j] = newBoardState[i][j];
			}
		}
	}
	
	public char[][] getBoardState() {
		return boardState;
	}
	
	public char[][] getBoardColors() {
		return boardColors;
	}
	
	public boolean isOccupied(BoardPosition p) {
		return boardState[p.getY()][p.getX()] != '-';
	}
	
	public boolean isInBounds(BoardPosition p) {
		return (p.getX() >= 0 && p.getX() < 8) && (p.getY() >= 0 && p.getY() < 8);
	}
	
	public void resetBoardState(){
		 char[][] initialState =   {{'r','n','b','q','k','b','n','r'},
								    {'p','p','p','p','p','p','p','p'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'P','P','P','P','P','P','P','P'},
								    {'R','N','B','Q','K','B','N','R'}};


		 for(int i = 0; i < boardState.length; i++) {
			 for(int j = 0; j < boardState.length; j++) {
				 boardState[i][j] = initialState[i][j];
			 }
		 }
	}
	
	
	public void resetBoardColors() {
		char[][] initialColors =	{{'#','.','#','.','#','.','#','.'},
									 {'.','#','.','#','.','#','.','#'},
									 {'#','.','#','.','#','.','#','.'},
									 {'.','#','.','#','.','#','.','#'},
									 {'#','.','#','.','#','.','#','.'},
									 {'.','#','.','#','.','#','.','#'},
									 {'#','.','#','.','#','.','#','.'},
									 {'.','#','.','#','.','#','.','#'}};
		
		for(int i = 0; i < boardColors.length; i++) {
			 for(int j = 0; j < boardColors.length; j++) {
				 boardColors[i][j] = initialColors[i][j];
			 }
		 }
	}

	public GameBoard copy() {
		GameBoard copy = new GameBoard();
		copy.setBoardState(boardState);
		return copy;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof GameBoard))
			return false;
		GameBoard other = (GameBoard)o;
		//just check board state - boardcolors don't matter for equality
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState.length; j++) {
				if(this.boardState[i][j] != other.getBoardState()[i][j])
					return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState.length; j++) {
				result = 31 * result + Character.hashCode(boardState[i][j]);
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		for(int i = 0; i < boardState.length; i++) {
			sb.append(i + "\t");
			for(int j = 0; j < boardState.length; j++) {
				sb.append(boardState[i][j] + " ");
			}		
			sb.append("\t");			
			for(int k = 0; k < boardColors.length; k++) {
				sb.append(boardColors[i][k] + " ");
			}
			sb.append("\n");
		}
		
		sb.append("\n \t");
		for(int i = 0; i < boardState.length; i++) {
			sb.append(i + " ");
		}
		sb.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		return sb.toString();
	}	
}
