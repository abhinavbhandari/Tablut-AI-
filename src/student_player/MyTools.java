package student_player;

import java.util.List;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

public class MyTools {
	
	List<TablutMove> options;
	static int bestMove = -9999;
	static int bestMin = -9999;
	static int bestMax = -9999;
	static int alpha = Integer.MIN_VALUE;
	static int beta = Integer.MAX_VALUE; 
	int returnValue = 0;
	static boolean firstIter = true;
	
    public static double getSomething() {
        return Math.random();
    }
    
    public static int getTree(TablutBoardState clone, TablutMove move, int depth, boolean isMaximizing) {
    	clone.processMove(move);
    	//check if winner is yourself when you're playing as a Miscovitz.
    	if (isMaximizing && clone.getWinner() == 0) {
    		return Integer.MAX_VALUE;
    	}
    	
    	
    	if (depth == 0) {
    		//for now we use a heuristic in which we calculate the number of opponent pieces remaining.
    		int opponent = clone.getOpponent();
    		int numberOfOpponentPieces = -1*clone.getNumberPlayerPieces(opponent);
    		if (!isMaximizing) {
    			if (numberOfOpponentPieces > bestMax) {
    				bestMax = numberOfOpponentPieces;
    				return bestMax;
    			} else {
    				return bestMax;
    			}
    		} else {
    			if (numberOfOpponentPieces < bestMin) {
    				bestMin = numberOfOpponentPieces;
    			}
    			return bestMin;
    		}
    	} else {
    		//new point stores the score value for the moves that are being returned.
    		int newPoint;
    		
    		//All moves for this branch.
    		List<TablutMove> branchMoves = clone.getAllLegalMoves();
    		
    		//will store the current Best Player, as of yet (may not need it after first iter.
    		int currentBest;
    		if (isMaximizing) {
    			isMaximizing = false;
    			currentBest = Integer.MIN_VALUE;
    		} else {
    			isMaximizing = true;
    			currentBest = Integer.MAX_VALUE;
    		}
    		
    		for (TablutMove newMove : branchMoves) {
    			
    			TablutBoardState newClone = (TablutBoardState) clone.clone();
    			newPoint = getTree(newClone, newMove, depth-1, isMaximizing);
    			
    			//because maximizing is false at this point.
    			if (!isMaximizing) {
					if (newPoint > currentBest && (newPoint > alpha)) {
    					currentBest = newPoint;
    				}
					//put in a check for alpha values.
//    				else {
//    					if (newPoint > currentBest && (newPoint > alpha)) {
//    						currentBest = newPoint;
//    					} 
//    					//check for if the new point is below the maximum.
//    					else if (newPoint < alpha) {
//    						return alpha;
//    					}
//    				}
    			}
    			
    			
    			
    			
    			
    			//Minimizing is true
    			else {
    				//minimizing because the points are in negative. so -11 < -9.
    				if (firstIter) {
    					if (newPoint < currentBest) {
    						
    					}
    				}
    				if(newPoint < currentBest && (newPoint > beta)) {
    					currentBest = newPoint;
    				} else if (newPoint < beta) {
    					return beta;
    				}
    			}
    		}
    		
//    		if (!isMaximizing && currentBest < alpha) {
//    			//returns alpha
//    			return alpha;
//    		}
    		if (!isMaximizing && alpha > currentBest) {
    			return alpha;
    		} else {
    			return currentBest;
    		}
    	}
    	//return currentBest;
//    	depth = -1;
//    	List<TablutMove> branchMoves = clone.getAllLegalMoves();
    }
    
    public static void setIter(boolean bool) {
    	firstIter = bool;
    }
    
    public static boolean getIter() {
    	return firstIter;
    }
}
