package models;

//need to copy reference of game for ai
//need to fix corps to transfer members to king correctly *******
//need to fix delegation property and make sure pieces know they can only move
//one time per subturn


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


/* FuzzyChess
 * Author: Marcus Kruzel
 * Version: 1.0
 * Last Updated Date: 10/17/20
 * Contains main logic for game
 */
public class FuzzyChess {
	//Commanding forces for each player
	//0 == king, 1 == l_bishop, 2 == r_bishop
	private ArrayList<Corp> p1_corps;
	private ArrayList<Corp> p2_corps;
	private Corp currentEnemyCorp;

	private ArrayList<ChessPiece> p1_captures;
	private ArrayList<ChessPiece> p2_captures;
	private GameBoard board;
	private int turn;
	private int subturn;
	private boolean gameOver;
	private int maxSubTurns;

	private ChessPiece selectedPiece;
	private ChessPiece selectedEnemy;
	private ArrayList<BoardPosition> possibleMoves;
	private ArrayList<BoardPosition> possibleCaptures;

	//use for capture panel display
	private Random dice;
	private boolean diceOffset;
	private int lastRoll;
	private String captureResult;
	
	//if enabled - all rolls = 6
	private boolean devMode;

	public FuzzyChess() {
		gameOver = false;
		board = new GameBoard();
		initCorps();
		currentEnemyCorp = null;
		p1_captures = new ArrayList<ChessPiece>();
		p2_captures = new ArrayList<ChessPiece>();
		turn = 0;
		subturn = 0;
		maxSubTurns = 3;
		selectedPiece = null;
		selectedEnemy = null;
		dice = new Random();
		dice.setSeed((long)Math.random() * 100000);
		captureResult = "";
		devMode = false;
		board.updateBoardColors(getCurrentCorp().getActiveMemberPositions(), null, null, null, null);
	}
	
	//for copying
	private FuzzyChess(ChessPiece selCopy, ChessPiece selEnemy,
			ArrayList<Corp> copiedP1Corps, ArrayList<Corp> copiedP2Corps, GameBoard copiedBoard,
			ArrayList<BoardPosition> pm, ArrayList<BoardPosition> pc, int t, int st) {
		gameOver = false;
		board = copiedBoard;
		p1_corps = copiedP1Corps;
		p2_corps = copiedP2Corps;
		currentEnemyCorp = null;
		p1_captures = new ArrayList<ChessPiece>();
		p2_captures = new ArrayList<ChessPiece>();
		possibleMoves = pm;
		possibleCaptures = pc;
		turn = t;
		subturn = st;
		maxSubTurns = 3;
		selectedPiece = selCopy;
		selectedEnemy = selEnemy;
		dice = new Random();
		dice.setSeed((long)Math.random() * 100000);
		captureResult = "";
		devMode = false;
		board.updateBoardColors(getCurrentCorp().getActiveMemberPositions(), null, null, null, null);
	}

	//corps grab pieces from the board
	//this is ugly code too - but w/e
	private void initCorps() {
		char[][] gameBoard = board.getBoardState();
		Corp p1_king_corp = new Corp(null);
		Corp p2_king_corp = new Corp(null);
		Corp p1_lbishop_corp = new Corp(p1_king_corp);
		Corp p2_lbishop_corp = new Corp(p2_king_corp);
		Corp p1_rbishop_corp = new Corp(p1_king_corp);
		Corp p2_rbishop_corp = new Corp(p2_king_corp);
		p1_corps = new ArrayList<Corp>();
		p2_corps = new ArrayList<Corp>();

		for (int i = 0; i < gameBoard.length; i++) {
			for (int j = 0; j < gameBoard[0].length; j++) {
				char id = gameBoard[i][j];
				BoardPosition curPosition = new BoardPosition(j, i);
				
				// kings corp
				if (j >= 3 && j < 5) {
					if (id == 'p') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.DOWN);
						p2_king_corp.addMember(piece);
					} else if (id == 'P') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.UP);
						p1_king_corp.addMember(piece);
					}
				}
				if (id == 'k' || id == 'q' || id == 'r') {
					ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.DOWN);
					p2_king_corp.addMember(piece);
				}
				if (id == 'K' || id == 'Q' || id == 'R') {
					ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.UP);
					p1_king_corp.addMember(piece);
				}
				
				// left bishop corp
				if (j < 3) {
					if (id == 'p' || id == 'b' || id == 'n') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.DOWN);
						p2_lbishop_corp.addMember(piece);
					}
					if (id == 'P' || id == 'B' || id == 'N') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.UP);
						p1_lbishop_corp.addMember(piece);
					}
				}

				// right bishop corp
				if (j >= 5) {
					if (id == 'p' || id == 'b' || id == 'n') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.DOWN);
						p2_rbishop_corp.addMember(piece);
					} else if (id == 'P' || id == 'B' || id == 'N') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.UP);
						p1_rbishop_corp.addMember(piece);
					}
				}
			}
		}
		p1_corps.add(p1_king_corp);
		p1_corps.add(p1_lbishop_corp);
		p1_corps.add(p1_rbishop_corp);
		p2_corps.add(p2_king_corp);
		p2_corps.add(p2_lbishop_corp);
		p2_corps.add(p2_rbishop_corp);
	}
	
	/* as of now - corps go in order
	 * logic bug - supposed to lose turn if corp leader is captured
	 * king - lbishop - rbishop */
	public Corp getCurrentCorp() {
		ArrayList<Corp> corps = turn == 0 ? p1_corps : p2_corps;
		for(int i = subturn; i < 3; i++) {
			if(corps.get(i).isActive())
				return corps.get(i);
		}
		return null;
	}
	
	public ArrayList<ChessPiece> getLeaders(boolean isWhite) {
		ArrayList<ChessPiece> leaders = new ArrayList<ChessPiece>();
		if(isWhite) {
			for(int i = 0; i < p1_corps.size(); i++) {
				if(p1_corps.get(i).isActive()) {
					leaders.add(p1_corps.get(i).getLeader());
				}
			}
		}
		else {
			for(int i = 0; i < p2_corps.size(); i++) {
				if(p2_corps.get(i).isActive()) {
					leaders.add(p2_corps.get(i).getLeader());
				}
			}
		}
		
		return leaders;
	}

	/*select for movement/capturing - update board colors when selected
	* when a piece is selected its possible moves and possible captures
	* are obtained for later when making a move*/
	public boolean selectPiece(BoardPosition selectedPosition) {
		if(board.isInBounds(selectedPosition)) {
			selectedPiece = getCurrentCorp().getActiveMemberAt(selectedPosition);			
			if(selectedPiece != null) {
				possibleMoves = getMovementPositions();
				possibleCaptures = getCapturePositions();
				board.updateBoardColors(null, possibleMoves, possibleCaptures, selectedPiece.getPosition(), null);
				//board.updateBoardColors(getCurrentCorp().getActiveMemberPositions(), possibleMoves, possibleCaptures);
				return true;
			}
		}
		return false;
	}
	
	
	/*gain reference to selectedEnemy and its corp so we can remove it if
	 * successful roll for capture -- maybe get rid of --*/
	private boolean selectEnemyPiece(BoardPosition selectedPosition) {
		ArrayList<Corp> enemyCorps = turn == 0 ? p2_corps : p1_corps;		
		for(Corp enemyCorp : enemyCorps) {
			currentEnemyCorp = enemyCorp;
			selectedEnemy = enemyCorp.getMemberAt(selectedPosition);
			if(selectedEnemy != null)
				return true;
		}
		return false;
	}
	
	//used by ai - assumes capture will always work
	public void makeNaiveMove(BoardPosition newPosition) {
		if(selectedPiece != null) {
			BoardPosition oldPosition = new BoardPosition(selectedPiece.getPosition().getX(), selectedPiece.getPosition().getY());
			if(possibleMoves.contains(newPosition)) {
				movePiece(oldPosition, newPosition);
			}
			if(possibleCaptures.contains(newPosition)) {
				selectEnemyPiece(newPosition);
				movePiece(oldPosition, newPosition);
			}
		}
		//board.updateBoardColors(getCurrentCorp().getActiveMemberPositions(), null, null);
		selectedPiece.setHasMoved(true);
	}
	
	public boolean makeMove(BoardPosition newPosition) {
		// check to make sure there is a selected piece
		boolean moveMade = false;
		if(selectedPiece != null) {
			BoardPosition oldPosition = new BoardPosition(selectedPiece.getPosition().getX(), selectedPiece.getPosition().getY());
			if(possibleMoves.contains(newPosition)) {
				movePiece(oldPosition, newPosition);
				moveMade = true;
			}
			if(possibleCaptures.contains(newPosition)) {
				selectEnemyPiece(newPosition);
				if(capturePiece()) {
					movePiece(oldPosition, newPosition);
				}
				moveMade = true;
			}
		}
		if(moveMade == false)
			board.updateBoardColors(getCurrentCorp().getActiveMemberPositions(), null, null, null, null);
		else
			board.updateBoardColors(null, null, null, selectedPiece.getPosition(), selectedEnemy == null ? null : selectedEnemy.getPosition());
		selectedPiece.setHasMoved(moveMade);
		return moveMade;
	}
	
	//resets the selected pieces in the game
	//call if makeMove returns false
	public void resetSelectedPieces() {
		captureResult = "";
		selectedPiece = null;
		selectedEnemy = null;
	}

	//for pathfinding use
	private class GameNode{
		public GameBoard state;
		public GameNode parent;
		public ChessPiece currentPiece;
		public int depth;
		
		public GameNode(GameBoard state, ChessPiece currentPiece, GameNode parent) {
			this.state = state;
			this.currentPiece = currentPiece;
			this.parent = parent;
			depth = parent == null ? 0 : parent.depth + 1;
		}
		
		@Override
		public int hashCode() {
			int result = state.hashCode();
			result = 31 * result + currentPiece.hashCode();
			result = 31 * result + Integer.hashCode(depth);
			return result;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof GameNode)) 
				return false;
			GameNode other = (GameNode)o;
			return (state.equals(other.state) && currentPiece.equals(other.currentPiece)); 
		}
	}
	
	//creates a new gamenode for pathfinding algorithm
	private GameNode getNode(GameNode parent, BoardPosition action) {
		if(parent == null || action == null) {
			return new GameNode(board.copy(), selectedPiece.copy(), null);
		}
		
		GameBoard newState = parent.state.copy();
		ChessPiece piece = parent.currentPiece.copy();
		
		if(newState.isInBounds(action) && !newState.isOccupied(action)){
			newState.updateBoardState(piece.getPosition(), action);
			piece.setPosition(action);
			return new GameNode(newState, piece, parent);
		}
		
		return null;
	}

	//todo - add meaningful comments
	private ArrayList<BoardPosition> getMovementPositions() {
		GameNode root = new GameNode(board.copy(), selectedPiece.copy(), null);
		Queue<GameNode> frontier = new LinkedList<GameNode>();
		frontier.add(root);
		HashSet<BoardPosition> explored = new HashSet<BoardPosition>();
		int maxDepth = root.currentPiece.getMoveCount();

		while(!frontier.isEmpty()) {
			GameNode curNode = frontier.remove();
			if(curNode.depth > maxDepth) break;   
			if(!curNode.state.equals(root.state)) //dont want to count start position
				explored.add(curNode.currentPiece.getPosition());
			for(int i = 0; i < curNode.currentPiece.getActions().size(); i++) {
				BoardPosition curAction = curNode.currentPiece.getActions().get(i);
				GameNode child = getNode(curNode, curAction);
				if((child != null) && !(frontier.contains(child)) && !(explored.contains(curAction))) {
					frontier.add(child);
				}
			}
		}
		return new ArrayList<BoardPosition>(explored);
	}
	
	/* todo/ add meaningful comments
	 * 
	 */
	private ArrayList<BoardPosition> getCapturePositions(){
		ArrayList<BoardPosition> capturePositions = new ArrayList<BoardPosition>();
		switch(selectedPiece.getid()) {
		case 'n':
		case 'N': //knight attack - melee/charge
			capturePositions.addAll(getSurroundingEnemyPositions(selectedPiece, 1));
			for(BoardPosition move : possibleMoves) {
				ChessPiece current = new ChessPiece(move, selectedPiece.getid(), selectedPiece.getDirection());
				capturePositions.addAll(getSurroundingEnemyPositions(current, 1));				
			}
			break;
		case 'r':
		case 'R': //rook attack - ranged radius of 3
			capturePositions.addAll(getSurroundingEnemyPositions(selectedPiece, 3));
			break;
		case 'p':
		case 'P':
		case 'b':
		case 'B': //pawn and bishop attack - melee front and diagonal
			for(BoardPosition p : selectedPiece.getActions()) {
				for(Corp enemyCorp : turn == 0 ? p2_corps : p1_corps) {
					if(enemyCorp.getMemberAt(p) != null) {
						capturePositions.add(p);
					}
				}
			}
			break;
		default: //king/queen attack - melee adjacent enemies
			capturePositions.addAll(getSurroundingEnemyPositions(selectedPiece, 1));
		}
		return capturePositions;
	}
	
	//check attack radius of current ChessPiece for possible enemies
	private ArrayList<BoardPosition> getSurroundingEnemyPositions(ChessPiece current, int radius){
		ArrayList<BoardPosition> surroundingEnemyPositions = new ArrayList<BoardPosition>();
		ArrayList<Corp> enemyCorps = turn == 0 ? p2_corps : p1_corps;
		int min_x = (current.getPosition().getX() - radius) < 0 ? 0 : current.getPosition().getX() - radius;
		int max_x = (current.getPosition().getX() + radius) > 7 ? 7 : current.getPosition().getX() + radius;
		int min_y = (current.getPosition().getY() - radius) < 0 ? 0 : current.getPosition().getY() - radius;
		int max_y = (current.getPosition().getY() + radius) > 7 ? 7 : current.getPosition().getY() + radius;
		
		for(int y = min_y; y <= max_y; y++) {
			for(int x = min_x; x <= max_x; x++) {
				BoardPosition p = new BoardPosition(x,y);
				if(!current.getPosition().equals(p)) {
					for(Corp enemyCorp : enemyCorps) {
						if(enemyCorp.getMemberAt(p) != null) {
							surroundingEnemyPositions.add(p);
						}
					}
				}
			}
		}
		return surroundingEnemyPositions;
	}

	private void movePiece(BoardPosition oldPosition, BoardPosition newPosition) {
		selectedPiece.setPosition(newPosition);
		board.updateBoardState(oldPosition, newPosition);
	}	
	
	//update to add roll offset for knight
	private boolean capturePiece() {
		int[] neededRolls = selectedPiece.getRolls(selectedEnemy);
		lastRoll = (int)((Math.random() * 100) % 6) + 1;
		
		//if its a knight - and the enemy position is not adjacent - subtract 1 from dice roll
		if(selectedPiece.getid() == 'n' || selectedPiece.getid() == 'N') {
			if(!selectedPiece.getActions().contains(selectedEnemy.getPosition())){
				System.out.println("Subtracting 1 from Knight Attack");
				diceOffset = true;
				if(lastRoll != 1) //cant get less than 1
					lastRoll -= 1;
			}
		}
		
		if(devMode)
			lastRoll = 6;
		
		for(int x = 0; x < neededRolls.length; x++) {
			if(neededRolls[x] == lastRoll) {
				captureResult = "Capture Success!";
				System.out.println("before capture" + currentEnemyCorp.toString());
				currentEnemyCorp.removeMember(selectedEnemy);
				System.out.println("after capture" + currentEnemyCorp.toString()); //should be empty
				if(turn == 0)
					p1_captures.add(selectedEnemy);
				else 
					p2_captures.add(selectedEnemy);
				if(selectedEnemy.getid() == 'k' || selectedEnemy.getid() == 'K') {
					gameOver = true;
				}
				return true;
			}				
		}
		captureResult = "Capture Failed...";
		return false;
	}
	
	public void quitGame() {
		gameOver = true;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void endTurn() {
		turn = ++turn % 2;
		subturn = 0;
		//reset current corps pieces so they can move again
		ArrayList<Corp> currentCorps = turn == 0 ? p1_corps : p2_corps;
		for(Corp c : currentCorps) {
			c.unfreezePieces();
		}
		board.updateBoardColors(getCurrentCorp().getActiveMemberPositions(), null, null, null, null);
	}

	public void endSubturn() {
		maxSubTurns = 3;
		ArrayList<Corp> currentCorps = turn == 0 ? p1_corps : p2_corps;
		for(Corp c : currentCorps) {
			if(!c.isActive())
				maxSubTurns--;
		}
		if(subturn++ < maxSubTurns-1) {
			board.updateBoardColors(getCurrentCorp().getActiveMemberPositions(), null, null, null, null);
		}
		else{
			board.updateBoardColors(null, null, null, null, null);
		}
		
	}
	
	public int getMaxSubTurns() {
		return maxSubTurns;
	}
	
	public ChessPiece getSelectedPiece() {
		return selectedPiece;
	}
	
	public ChessPiece getSelectedEnemyPiece() {
		return selectedEnemy;
	}
	
	public ArrayList<BoardPosition> getPossibleMoves(){
		return possibleMoves;
	}
	
	public ArrayList<BoardPosition> getPossibleCaptures(){
		return possibleCaptures;
	}
	
	public ArrayList<BoardPosition> getAllMoves(){
		ArrayList<BoardPosition> allMoves = new ArrayList<BoardPosition>();
		if(selectedPiece != null) {
			allMoves.addAll(possibleMoves);
			allMoves.addAll(possibleCaptures);
		}
		return allMoves;
	}

	public GameBoard getBoard() {
		return board;
	}
	
	public int getLastRoll() {
		return lastRoll;
	}

	public int getTurn() {
		return turn;
	}

	public int getSubTurn() {
		return subturn;
	}
	
	public boolean isDiceOffset() {
		return diceOffset;
	}
	
	public void toggleDevMode() {
		devMode = !devMode;
	}
	
	public boolean isDevMode() {
		return devMode;
	}
	
	public String getCaptureResult() {
		return captureResult;
	}

	public ArrayList<ChessPiece> getPlayer1Captures() {
		return p1_captures;
	}

	public ArrayList<ChessPiece> getPlayer2Captures() {
		return p2_captures;
	}
	
	//for ai environment visualization
	//really just need the corps which keeps track
	//of commanding forces, the gameboard, and the current turn
	public FuzzyChess copy() {
		ArrayList<Corp> copied_P1Corps = new ArrayList<Corp>();
		ArrayList<Corp> copied_P2Corps = new ArrayList<Corp>();
		ChessPiece selCopy = selectedPiece == null ? null : selectedPiece.copy();
		ChessPiece selEnemy = selectedEnemy == null ? null : selectedEnemy.copy();
		Corp copiedP1KingsCorp = p1_corps.get(0).copy();
		Corp copiedP2KingsCorp = p2_corps.get(0).copy();
		for(int i = 0; i < p1_corps.size(); i++) {
			if(i == 0) {
				copied_P1Corps.add(copiedP1KingsCorp);
				copied_P2Corps.add(copiedP2KingsCorp);
			}
			else {
				Corp copiedP1Corp = p1_corps.get(i).copy();
				Corp copiedP2Corp = p2_corps.get(i).copy();
				copiedP1Corp.setKingsCorp(copiedP1KingsCorp);
				copiedP2Corp.setKingsCorp(copiedP2KingsCorp);
				copied_P1Corps.add(copiedP1Corp);
				copied_P2Corps.add(copiedP2Corp);
			}
		}
		GameBoard copiedBoard = board.copy();
		return new FuzzyChess(selCopy, selEnemy, copied_P1Corps, copied_P2Corps, copiedBoard, possibleMoves, possibleCaptures, turn, subturn);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n\n");
		sb.append(String.format("Turn: %d Subturn: %d", turn, subturn));
		sb.append("Board\n"+board.toString());
		sb.append("P1 CORPS\n");
		for(int i = 0; i < p1_corps.size(); i++) {
			sb.append(p1_corps.get(i).toString() + "\n");
		}
		
		sb.append("P2 CORPS\n");
		for(int i = 0; i < p2_corps.size(); i++) {
			sb.append(p2_corps.get(i).toString() + "\n");
		}
		
		sb.append("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n\n");
		return sb.toString();
	}
}
