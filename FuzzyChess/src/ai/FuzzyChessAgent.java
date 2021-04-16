package ai;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import engine.FuzzyChessEngine;
import models.*;

public class FuzzyChessAgent implements Runnable {
	public static final int VERY_EASY_DIFFICULTY = 0;
	public static final int EASY_DIFFICULTY = 1;
	public static final int MED_DIFFICULTY = 2;
	public static final int HARD_DIFFICULTY = 3;
	private int difficulty;
	
	//actual game to manipulate
	private FuzzyChessEngine engine;
	//state of game to lookahead
	private FuzzyChess currentState;
	
	//moves created by evaluating the current state of the game
	private ArrayList<BoardPosition> moves;
	private int currentMoveIndex;
	
	private Thread aiWorker;
	
	//pawns really just need to move 1 space up so the back pieces can move
	private double[][] blkPawnBoardPositionValues = {{0,0,0,0,0,0,0,0},
												    {0,-2,-1,2,2,-1,-2,0},
												    {3,3,1,1,1,1,3,3},
												    {.1,.1,.1,.1,.1,.1,.1,.1},
												    {.1,.1,.1,.1,.1,.1,.1,.1},
												    {.1,.1,.1,.1,.1,.1,.1,.1},
												    {.1,.1,.1,.1,.1,.1,.1,.1},
												    {.1,.1,.1,.1,.1,.1,.1,.1}};
	//bishops need to stay back to block king and protect themselves
	private double[][] blkBishopBoardPositionValues = {{0,0,1,0,0,1,0,0},
			                                          {0,1,2,2,2,2,1,0},
												      {1,1,1,1,1,1,1,1},
												      {1,1,1,1,1,1,1,1},
												      {1,1,1,1,1,1,1,1},
												      {1,1,1,1,1,1,1,1},
												      {1,1,1,1,1,1,1,1},
												      {1,1,1,1,1,1,1,1}};
	
	//knights are stronger in the middle - they can reach every position then
	private double[][] blkKnightBoardPositionValues = {{1,1,1,1,1,1,1,1},
												      {0,1,1,1,1,1,1,0},
												      {0,1,2,2,2,2,1,0},
												      {0,1,2,2,2,2,1,0},
												      {0,1,2,2,2,2,1,0},
												      {0,1,2,2,2,2,1,0},
												      {0,1,1,1,1,1,1,0},
											   	      {1,1,1,1,1,1,1,1}};
	//rooks are very strong in middle because 6x6 range and can shoot over enemies
	private double[][] blkRookBoardPositionValues = {{0,0,0,0,0,0,0,0},
											        {1,2,2,2,2,2,2,1},
											        {0,1,3,3,3,3,1,0},
											        {0,1,4,4,4,4,1,0},
											        {0,1,4,4,4,4,1,0},
											        {0,1,2,2,2,2,1,0},
										   	        {0,0,0,0,0,0,0,0},
											        {0,0,0,0,0,0,0,0}};
	//queens are strong in the middle - they can move almost anywhere - similar to knights
	//also scores changed to try and get them behind enemy lines- where they wreak havoc
	private double[][] blkQueenBoardPositionValues = {{1,1,1,1,1,1,1,1},
													 {1,1,1,1,1,1,1,1},
											         {1,1,3,3,3,3,1,1},
											         {1,1,1,2,2,1,1,1},
											         {1,1,1,2,2,1,1,1},
											         {1,1,1,1,1,1,1,1},
										   	         {1,1,1,1,1,1,1,1},
										   	         {1,1,1,1,1,1,1,1}};
	//kings need to stay back and get to corner
	private double[][] blkKingBoardPositionValues = {{2,2,2,1,1,2,2,2},
													{1,1,1,1,1,1,1,1},
													{0,0,0,0,0,0,0,0},
													{0,0,0,0,0,0,0,0},
													{0,0,0,0,0,0,0,0},
													{0,0,0,0,0,0,0,0},
													{0,0,0,0,0,0,0,0},
													{0,0,0,0,0,0,0,0}};
	
	//white piece position multipliers
	private double[][] whtPawnBoardPositionValues;
	private double[][] whtBishopBoardPositionValues;
	private double[][] whtKnightBoardPositionValues;
	private double[][] whtRookBoardPositionValues;
	private double[][] whtQueenBoardPositionValues;
	private double[][] whtKingBoardPositionValues;
	
	public FuzzyChessAgent(FuzzyChessEngine engine, int difficulty) {
		whtPawnBoardPositionValues = reverse(blkPawnBoardPositionValues);
		whtBishopBoardPositionValues = reverse(blkPawnBoardPositionValues);
		whtKnightBoardPositionValues = reverse(blkKnightBoardPositionValues);
		whtRookBoardPositionValues = reverse(blkRookBoardPositionValues);
		whtQueenBoardPositionValues = reverse(blkQueenBoardPositionValues);
		whtKingBoardPositionValues = reverse(blkKingBoardPositionValues);
		
		this.engine = engine;
		moves = new ArrayList<BoardPosition>();
		currentMoveIndex = -1;
		this.difficulty = difficulty;
	}

	//actuators to manipulate the state of the game and the display
	public void makeMove() {
		++currentMoveIndex;
		if(engine.getGame().getSubTurn() >= engine.getGame().getMaxSubTurns()) {
			engine.endTurn();
		}
		else {
			//System.out.println("Clicking location" + moves.get(currentMoveIndex));
			if(currentMoveIndex % 2 == 0) {
				engine.getGame().selectPiece(moves.get(currentMoveIndex));
			}
			else {
				engine.getGame().makeMove(moves.get(currentMoveIndex));
				if(engine.getGame().getSelectedEnemyPiece() != null) {
					engine.startRollAnimation();
					return;
				}
				engine.getGame().endSubturn();
				engine.updateDisplay();
				engine.getGame().resetSelectedPieces();
				return;
			}
			engine.updateDisplay();
		}
	}
	
	//evaluates the current state of the game and figures out
	//a list of moves it will make in the turn - afterwards is called
	//multiple times to iterate through its movements
	public void evaluateTurn(FuzzyChess game) {
		this.currentState = game;
		currentMoveIndex = -1;
		if(aiWorker == null) {
			aiWorker = new Thread(this);
			aiWorker.start();
		}
	}
	
	//looks at available pieces per subturn and picks one randomly - if they aren't able to move 
	//or capture anything then it picks another randomly
	//then it moves to a random space or captures a random enemy
	private ArrayList<BoardPosition> makeLevel0Moves(FuzzyChess environment){
		ArrayList<BoardPosition> randomMoves = new ArrayList<BoardPosition>();
		ArrayList<BoardPosition> availablePieces;
		ArrayList<BoardPosition> availableMoves;
		BoardPosition selectedPosition;
		BoardPosition moveToPosition;
		while(environment.getSubTurn() < environment.getMaxSubTurns()) {
			availablePieces = environment.getCurrentCorp().getActiveMemberPositions();
			while(true) {
				selectedPosition = availablePieces.get((int)(Math.random() * 100) % availablePieces.size());
				environment.selectPiece(selectedPosition);
				//need to make sure it can actually move/capture
				//System.out.println("Selected Piece " + environment.getSelectedPiece());
				availableMoves = environment.getAllMoves();
				//bug here perchance
				if(availableMoves.size() > 0) { //make a move
					//System.out.println(availableMoves);
					moveToPosition = availableMoves.get((int)(Math.random() * 100) % availableMoves.size());
					if(environment.makeMove(moveToPosition)) {
						environment.endSubturn();
						environment.resetSelectedPieces();
						break;
					};
				}
				environment.resetSelectedPieces();
			}
			randomMoves.add(selectedPosition);
			randomMoves.add(moveToPosition);
		}
		//System.out.println("Moves to be made...");
		//System.out.println(randomMoves);
		return randomMoves;
	}
	
	//just picks best moves possible based on heuristics - no lookahead
	private ArrayList<BoardPosition> makeLevel1Moves(FuzzyChess state) {
		return generateTopMoves(state, 1).get(0);
	}
	
	private ArrayList<BoardPosition> makeLevel2Moves(FuzzyChess state, int depth, boolean isMax){
		ArrayList<BoardPosition> level2Moves = new ArrayList<BoardPosition>();
		int topX = 3;
		ArrayList<ArrayList<BoardPosition>> moves = generateTopMoves(state, topX);

		double bestScore = 99999;
		for(int i = 0; i < moves.size(); i++) {
			FuzzyChess nextState = doTurn(state.copy(), moves.get(i));
			double score = minimax(nextState, depth-1, -99999, 99999, !isMax);
			if(score < bestScore) {
				bestScore = score;
				level2Moves = moves.get(i);
			}
		}
		return level2Moves;
	}
	
	/*simulates entire turn and returns resulting state*/
	private FuzzyChess doTurn(FuzzyChess state, ArrayList<BoardPosition> moves) {
		FuzzyChess current = state;
		for(int i = 0; i < moves.size(); i+=2) {
			BoardPosition[] actions = {moves.get(i), moves.get(i+1)};
			current = doMove(current, actions);
		}
		current.endTurn();
		return current;
	}
	
	/*simulates subturn and assumes captures are 100% likely*/
	private FuzzyChess doMove(FuzzyChess state, BoardPosition[] parentMove) {
		if(state.getSubTurn() == state.getMaxSubTurns())
			return state;
		state.selectPiece(parentMove[0]);
		state.makeNaiveMove(parentMove[1]);
		state.endSubturn();
		return state;
	}
	
	//this is really ugly but maybe it works....
	private ArrayList<ArrayList<BoardPosition>> generateTopMoves(FuzzyChess state, int factor){
		int topX = factor;
		//System.out.println(factor);
		ArrayList<ArrayList<BoardPosition>> topMoves = new ArrayList<ArrayList<BoardPosition>>();
		ArrayList<BoardPosition[]> topSubTurn1Moves = topXMoves(state, topX);
		
		for(int i = 0; i < topSubTurn1Moves.size(); i++) {
			if(state.getSubTurn() < state.getMaxSubTurns()-1) {
				FuzzyChess subTurn2State = doMove(state.copy(), topSubTurn1Moves.get(i));
				ArrayList<BoardPosition[]> topSubTurn2Moves = topXMoves(subTurn2State, topX);
				for(int j = 0; j < topSubTurn2Moves.size(); j++) {
					if(subTurn2State.getSubTurn() < subTurn2State.getMaxSubTurns()-1) {
						FuzzyChess subTurn3State = doMove(subTurn2State.copy(), topSubTurn2Moves.get(j));
						ArrayList<BoardPosition[]> topSubTurn3Moves = topXMoves(subTurn3State, topX);
						for(int k = 0; k < topSubTurn3Moves.size(); k++) {
							ArrayList<BoardPosition> topTurnMoves = new ArrayList<BoardPosition>();
							topTurnMoves.add(topSubTurn1Moves.get(i)[0]);
							topTurnMoves.add(topSubTurn1Moves.get(i)[1]);
							topTurnMoves.add(topSubTurn2Moves.get(j)[0]);
							topTurnMoves.add(topSubTurn2Moves.get(j)[1]);
							topTurnMoves.add(topSubTurn3Moves.get(k)[0]);
							topTurnMoves.add(topSubTurn3Moves.get(k)[1]);
							topMoves.add(topTurnMoves);
						}
					}
					else {
						ArrayList<BoardPosition> topTurnMoves = new ArrayList<BoardPosition>();
						topTurnMoves.add(topSubTurn1Moves.get(i)[0]);
						topTurnMoves.add(topSubTurn1Moves.get(i)[1]);
						topTurnMoves.add(topSubTurn2Moves.get(j)[0]);
						topTurnMoves.add(topSubTurn2Moves.get(j)[1]);
						topMoves.add(topTurnMoves);
					}
				}
			}
			else {
				ArrayList<BoardPosition> topTurnMoves = new ArrayList<BoardPosition>();
				topTurnMoves.add(topSubTurn1Moves.get(i)[0]);
				topTurnMoves.add(topSubTurn1Moves.get(i)[1]);
				topMoves.add(topTurnMoves);
			}
		}
		return topMoves;
	}
	
	
	private ArrayList<BoardPosition[]> topXMoves(FuzzyChess state, int x){
		Queue<BoardPosition[]> topXMoves = new LinkedList<BoardPosition[]>();
		Queue<Double> scores = new LinkedList<Double>();
		ArrayList<BoardPosition> availablePieces = state.getCurrentCorp().getActiveMemberPositions();
		boolean isMax = state.getTurn() == 0;
		
		if(isMax)
			scores.add(-99999.0);
		else 
			scores.add(99999.0);
		
		for(int i = 0; i < availablePieces.size(); i++) {
			BoardPosition[] bestMove = new BoardPosition[2];
			BoardPosition selectedPosition = availablePieces.get(i);
			state.selectPiece(selectedPosition);
			for(int j = 0; j < state.getAllMoves().size(); j++) {
				FuzzyChess nextState = state.copy();
				BoardPosition selectedAction = state.getAllMoves().get(j);
				nextState.makeNaiveMove(selectedAction);
				double score = evaluateGameState(nextState);
				if(isMax) {
					if((score) > scores.peek()) {
						bestMove[1] = selectedAction;
						bestMove[0] = selectedPosition;
						if(topXMoves.size() == x) {
							topXMoves.remove();
							scores.remove();
						}
						scores.add(score);
						topXMoves.add(bestMove);
					}
				}
				else {
					if((score) < scores.peek()) {
						bestMove[1] = selectedAction;
						bestMove[0] = selectedPosition;
						if(topXMoves.size() == x) {
							topXMoves.remove();
							scores.remove();
						}
						scores.add(score);
						topXMoves.add(bestMove);
					}
				}
			}
		}
		return new ArrayList<BoardPosition[]>(topXMoves);
	}
	
	

	
	private double minimax(FuzzyChess state, int depth, double alpha, double beta, boolean isMax) {
		int topX = 3;
		if(depth == 0) {
			return evaluateGameState(state);
		}
		ArrayList<ArrayList<BoardPosition>> moves = generateTopMoves(state, topX);
		if(isMax) {
			double bestScore = -99999;
			for(int i = 0; i < moves.size(); i++) {
				FuzzyChess nextState = doTurn(state.copy(), moves.get(i));
				bestScore = Math.max(bestScore, minimax(nextState, depth-1, alpha, beta, !isMax));
				alpha = Math.max(alpha, bestScore);
				if(alpha >= beta) 
					return bestScore;
			}
			return bestScore;
		}
		else {
			double bestScore = 99999;
			for(int i = 0; i < moves.size(); i++) {
				FuzzyChess nextState = doTurn(state.copy(), moves.get(i));
				bestScore = Math.min(bestScore, minimax(nextState, depth-1, alpha, beta, !isMax));
				beta = Math.min(beta, bestScore);
				if(beta <= alpha)
					return bestScore;
			}
			return bestScore;
		}
	}
	
	
	public double evaluateGameState(FuzzyChess state) {
		double boardPositions = evaluateBoardPositions(state);
		double chance = evaluateChanceOfCapture(state);
		double leaderPositions = evaluateLeaderPositions(state);
		double centerControl = evaluateCenterControl(state);
		double safety = evaluateMovedPieceSafety(state);
		//System.out.println("leader positions" + leaderPositions);
		double evaluation = (boardPositions + leaderPositions + centerControl + safety) * chance;
		//System.out.println("Total evaluatation: " + evaluation);
		return evaluation;
	}
	
	private double evaluateSurroundings(FuzzyChess state, char pieceID, BoardPosition piecePosition) {
		double resultScore = 0;
		BoardPosition p = piecePosition;
		char[][] boardState = state.getBoard().getBoardState();
		
		//check range 3 for rooks - cause they scary
		int range = 3;
		int minY = (p.getY()-range) < 0 ? 0 : p.getY()-range;
		int maxY = (p.getY()+range) > boardState.length-1 ? boardState.length-1 : p.getY()+range;
		int minX = (p.getX()-range) < 0 ? 0 : p.getX()-range;
		int maxX = (p.getX()+range) > boardState.length-1 ? boardState.length-1 : p.getX()+range;
		for(int i = minY; i <= maxY; i++) {
			for(int j = minX; j <= maxX; j++) {
				if(j != p.getX() && i != p.getY()) {
					if(ChessPiece.isWhite(pieceID)) {
						if(boardState[i][j] == 'r') {
							resultScore -= getPieceScore(boardState[i][j]) * 3;
						}
					}
					if(ChessPiece.isBlack(pieceID)) {
						if(boardState[i][j] == 'R') {
							//System.out.println("ROOK IN RANGE");
							resultScore -= getPieceScore(boardState[i][j]) * 3;
						}
					}
				}
			}
		}
		//check immediate surroundings for every other piece
		range = 1;
		minY = (p.getY()-range) < 0 ? 0 : p.getY()-range;
		maxY = (p.getY()+range) > boardState.length-1 ? boardState.length-1 : p.getY()+range;
		minX = (p.getX()-range) < 0 ? 0 : p.getX()-range;
		maxX = (p.getX()+range) > boardState.length-1 ? boardState.length-1 : p.getX()+range;
		for(int i = minY; i <= maxY; i++) {
			for(int j = minX; j <= maxX; j++) {
				if(j != p.getX() && i != p.getY()) {
					if(ChessPiece.isWhite(pieceID)) {
						if(ChessPiece.isBlack(boardState[i][j]) && boardState[i][j] != 'r' && boardState[i][j] != 'k') {
							resultScore -= getPieceScore(boardState[i][j]);
						}
					}
					if(ChessPiece.isBlack(pieceID)) {
						if(ChessPiece.isWhite(boardState[i][j]) && boardState[i][j] != 'R' && boardState[i][j] != 'K') {
							//System.out.println(boardState[i][j] + " IN RANGE");
							resultScore -= getPieceScore(boardState[i][j]);
							//System.out.println(resultScore);
						}						
					}
				}
			}
		}
		return resultScore;
	}
	
	
	private double evaluateChanceOfCapture(FuzzyChess state) {
		if(state.getSelectedEnemyPiece() == null) {
			return 1.0;
		}
		int[] rolls = state.getSelectedPiece().getRolls(state.getSelectedEnemyPiece());
		double chance = (1.0/6.0) * (rolls.length);
		return chance < .25 ? 0.0 : chance;
	}
	
	private double evaluateMovedPieceSafety(FuzzyChess state) {
		char id = state.getSelectedPiece().getid();
		BoardPosition p = state.getSelectedPiece().getPosition();
		double value = evaluateSurroundings(state, id, p);
		
		if(ChessPiece.isBlack(id))
			return -value;
		return value;
	}
	
	private double evaluateCenterControl(FuzzyChess state) {
		double whtScore = 0;
		double blkScore = 0;
		
		char[][] boardState = state.getBoard().getBoardState();
		for(int i = 2; i < 6; i++) {
			for(int j = 2; j < 6; j++) {
				if(ChessPiece.isWhite(boardState[i][j]) && (boardState[i][j] != 'K')) {
					whtScore += getPieceScore(boardState[i][j]);
				}
				else if(ChessPiece.isBlack(boardState[i][j]) && (boardState[i][j] != 'k')) {
					blkScore += getPieceScore(boardState[i][j]);
				}
			}
		}
		return whtScore - blkScore;
	}
	
	private double evaluateLeaderPositions(FuzzyChess state) {
		double whtScore = 0;
		double blkScore = 0;
		ArrayList<ChessPiece> whiteLeaders = state.getLeaders(true);
		ArrayList<ChessPiece> blackLeaders = state.getLeaders(false);
		whtScore += whiteLeaders.size() * 300;
		blkScore += blackLeaders.size() * 300;
		
		
		//now check surroundings of each leader and add to score aswell
		for(int i = 0; i < whiteLeaders.size(); i++) {
			//System.out.println(whiteLeaders.get(i).getid() + " @ " + whiteLeaders.get(i).getPosition());
			double safety = evaluateSurroundings(state, whiteLeaders.get(i).getid(), whiteLeaders.get(i).getPosition());
			if(i == 0) safety *= 2; //king
			whtScore += safety;
			
		}
		for(int i = 0; i < blackLeaders.size(); i++) {
			//System.out.println(blackLeaders.get(i).getid() + " @ " + blackLeaders.get(i).getPosition());
			double safety = evaluateSurroundings(state, whiteLeaders.get(i).getid(), whiteLeaders.get(i).getPosition());
			if(i == 0) safety *= 2; //king
			blkScore += safety;
		}
		
		//System.out.println(" White score: " + whtScore);
		//System.out.println(" Black score: " + blkScore);
		return whtScore - blkScore;
	}
	
	private double evaluateBoardPositions(FuzzyChess state) {
		int whtScore = 0;
		int blkScore = 0;
		//use white values
		char[][] boardState = state.getBoard().getBoardState();
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState[0].length; j++) {
				char id = boardState[i][j];
				switch(id) {
				case 'p':
					blkScore += getPieceScore(id) * blkPawnBoardPositionValues[i][j];
					break;
				case 'P':
					whtScore += getPieceScore(id) * whtPawnBoardPositionValues[i][j];
					break;
				case 'n':
					blkScore += getPieceScore(id) + blkKnightBoardPositionValues[i][j];
					break;
				case 'N':
					whtScore += getPieceScore(id) + whtKnightBoardPositionValues[i][j];
					break;
				case 'r':
					blkScore += getPieceScore(id) * blkRookBoardPositionValues[i][j];
					break;
				case 'R':
					whtScore += getPieceScore(id) * whtRookBoardPositionValues[i][j];
					break;
				case 'b':
					blkScore += getPieceScore(id) + blkBishopBoardPositionValues[i][j];
					break;
				case 'B':
					whtScore += getPieceScore(id) + whtBishopBoardPositionValues[i][j];
					break;
				case 'q':
					blkScore += getPieceScore(id) + blkQueenBoardPositionValues[i][j];
					break;
				case 'Q':
					whtScore += getPieceScore(id) + whtQueenBoardPositionValues[i][j];
					break;
				case 'k':
					blkScore += getPieceScore(id) + blkKingBoardPositionValues[i][j];
				    break;
				case 'K':
					whtScore += getPieceScore(id) + whtKingBoardPositionValues[i][j];
					break;
				default:
						
				}
			}
		}
		return whtScore - blkScore;
	}
	
	private double[][] reverse(double[][] arr){
		double[][] reversed = new double[arr.length][arr[0].length];
		for(int i = 0; i < arr.length; i++) {
			reversed[i] = arr[arr.length-i-1];
		}		
		return reversed;
	}
	
	public int getPieceScore(char pieceID) {
		switch(pieceID) {
		case 'p':
		case 'P':
			return 10;
		case 'n':
		case 'N':
			return 50;
		case 'q':
		case 'Q':
			return 65;
		case 'r':
		case 'R':
			return 80;
		case 'b':
		case 'B':
			return 100;
		case 'k':
		case 'K':
			return 10000;
		default:
			return 0;
		}
	}
	public Thread getAiWorker(){
		return aiWorker;
	}

	@Override
	public void run() {
		switch(difficulty) {
		case 0:
			moves = makeLevel0Moves(currentState); //pick random moves
			break;
		case 1:
			moves = makeLevel1Moves(currentState); //just use eval functions
			break;
		case 2:
			moves = makeLevel2Moves(currentState, 3, false); //use minimax
			break;
		case 3:
			moves = makeLevel2Moves(currentState, 5, false); //use minimax
			break;
		default:
			moves = makeLevel0Moves(currentState);
		}
		aiWorker = null;
		engine.aiReadyCallBack();
	}
}
