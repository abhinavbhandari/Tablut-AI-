
package student_player;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import boardgame.Move;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutBoardState.Piece;
import tablut.TablutMove;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    
    int alpha = 0;
    int beta = 0;
    int opponentPlayer = 1 - this.player_id;
    ArrayList<Move> stackWinMove = new ArrayList<Move>();
    
    public StudentPlayer() {
        super("260629891");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        MyTools.getSomething();
        
        if(player_id == TablutBoardState.MUSCOVITE)
        {
            return playMuscovite(boardState);
        }
        else
        {
            return playSwede(boardState);
        }
    }
    
    public Move playSwede(TablutBoardState boardState) {
        List<TablutMove> options = boardState.getAllLegalMoves();
        
        Move myMove = boardState.getRandomMove();
          
        TablutBoardState clone;
        
        if (!stackWinMove.isEmpty()) {
            return stackWinMove.remove(stackWinMove.size() -1);
        }
          
        float[] alphabeta = {Integer.MIN_VALUE, Integer.MAX_VALUE};
          
        float currentBest = Integer.MAX_VALUE;
          
        for (TablutMove maxMove: options) {
            //create a clone of the boardState.
            clone = (TablutBoardState) boardState.clone();
            
            clone.processMove(maxMove);
            if (clone.getWinner() == this.player_id) {
//              System.out.println("Check inside");
                return maxMove;
            }
            int depth = 2;
            int child = 3;
            alphabeta = getMax(clone, Integer.MIN_VALUE, alphabeta[1], depth, false, boardState.getNumberPlayerPieces(this.player_id), boardState.getNumberPlayerPieces(opponentPlayer), child);
            if (alphabeta[0] < currentBest) {
                currentBest = alphabeta[0];
                alphabeta[1] = currentBest;
                myMove = maxMove;
                
                //The reason for having the and condition is because if there is a maximizing move that is guaranteed to win found, then the stack should be full.
                if (alphabeta[1] == Integer.MIN_VALUE && stackWinMove.size() == depth) {
                    System.out.println("Making winning Move");
                    //stackWinMove.add(myMove);
                    return myMove;
                }
            }
//          System.out.println("Move: " + myMove.toPrettyString());
//          System.out.println("alpha value: " + alphabeta[0]);
          
        }
        return myMove;
    }
    
    public Move playMuscovite(TablutBoardState boardState) {
        
//      if (!stackWinMove.isEmpty()) {
//          System.out.println("Stack size: " + stackWinMove.size());
//          return stackWinMove.remove(stackWinMove.size() -1);
//      }
        
        List<TablutMove> options = boardState.getAllLegalMoves();
        
        
        Move myMove = boardState.getRandomMove();
        //iterate through each move.
        TablutBoardState clone;
        float[] alphabeta = {Integer.MIN_VALUE, Integer.MAX_VALUE};
        float currentBest = Integer.MIN_VALUE;
        
        
        int depth = 1;
        for (TablutMove maxMove: options) {
            clone = (TablutBoardState) boardState.clone();
            
            clone.processMove(maxMove);
            alphabeta = getMin(clone, alphabeta[0], Integer.MAX_VALUE, depth, true, boardState.getNumberPlayerPieces(opponentPlayer), boardState.getNumberPlayerPieces(this.player_id), 3);
            if (alphabeta[1] > currentBest) {
                currentBest = alphabeta[1];
                alphabeta[0] = currentBest;
                myMove = maxMove;
//              if (alphabeta[0] == Integer.MAX_VALUE && stackWinMove.size() == depth) {
//                  System.out.println("Making winning Move");
//                  return myMove;
//              }
                if (alphabeta[0] == Integer.MAX_VALUE) {
//                  System.out.println("Making winning Move");
                    return myMove;
                }
            }
        }
        
        
        return myMove;
    }
    
    public float[] getMax(TablutBoardState minBoardState, float alpha, float beta, int depth, boolean isBlack, int stealPC, int defendPC, int child) {
        
        float[] alphabeta = {alpha, beta};
        List<TablutMove> options = minBoardState.getAllLegalMoves();
        
        //State default value would choose if we win. 
        if (minBoardState.getWinner() == this.player_id) {
//          System.out.println("Depth: " + depth);
//          System.out.println("Turn Number: " + minBoardState.getTurnNumber());
            alpha = Integer.MIN_VALUE;
            alphabeta[0] = alpha;
            return alphabeta;
        }
        
        //if the opponent wins.
        if (minBoardState.getWinner() == this.opponentPlayer) {
//          System.out.println("Opponent Depth: " + depth);
//          System.out.println("Turn Number: " + minBoardState.getTurnNumber());
            alpha = Integer.MAX_VALUE;
            alphabeta[0] = alpha;
            return alphabeta;
        }
        
        if(depth == 0) {
            //TODO: Improve evaluation function

            float points = this.getEvaluationFunction(minBoardState, stealPC, defendPC);
            alphabeta[0] = points;
            //System.out.println("alpha points: " + points);
            return alphabeta;
        }
        
        //this will store the min's return value. 
        float[] minVal;
        boolean moveAdded = false;
        
        for (TablutMove newMove : options) {
            TablutBoardState newClone = (TablutBoardState) minBoardState.clone();
            newClone.processMove(newMove);
            
            //Gets the value from min's search.
            minVal = getMin(newClone, alpha, beta, depth-1, isBlack, stealPC, defendPC, child);
            //System.out.println("before alpha: " + alpha);
            //if this is greater than the current best
            if (minVal[1] > alpha) {
                alpha = minVal[1];
                alphabeta[0] = alpha;
//              if (alpha == Integer.MAX_VALUE && !moveAdded && isBlack) {
//                  System.out.println("Heree and stack size and: " + stackWinMove.size() + " beta: " + beta);
//                  stackWinMove.add(newMove);
//                  moveAdded = true;
//              } else 
                    if (!stackWinMove.isEmpty()) {
                    stackWinMove.clear();
                    moveAdded = false;
                }
            } else if (minVal[1] == alpha && !isBlack) {
                if (minVal[1] == Integer.MIN_VALUE && !moveAdded) {
                    stackWinMove.add(newMove);
                    moveAdded = true;
                }
            }
            
            //System.out.println("After alpha: " + alpha);
            //System.out.println("Before beta: " + beta);
            
            if (alpha >= beta) {
                alpha = beta;
//              if (beta != Integer.MAX_VALUE) {
//                  stackWinMove.clear();
//              }
                /* Don't want an if condition here to check to add a stack win move because if the integer is the lowest value, then it will be added above and it can't be lower than beta.*/
//              if (alpha == Integer.MIN_VALUE) {
//                  stackWinMove.add(newMove);
//              }
                alphabeta[0] = alpha;
                return alphabeta;
            }
        }
        
        //maybe re-initialize alpha beta values.
        return alphabeta;
    }
    
    public float[] getMin(TablutBoardState maxBoardState, float alpha, float beta, int depth, boolean isBlack, int stealPC, int defendPC, int child) {
        int currentBest = Integer.MAX_VALUE;
        float[] alphabeta = {alpha, beta};
        
        if (maxBoardState.getWinner() == this.player_id) {
//          System.out.println("Depth: " + depth);
//          System.out.println("Turn Number: " + maxBoardState.getTurnNumber());
            beta = Integer.MAX_VALUE;
            alphabeta[1] = beta;
            return alphabeta;
        }
        
        List<TablutMove> options = maxBoardState.getAllLegalMoves();
        
        
        if(depth == 0) {
            //part of evaluating. make this into a separate function since both players use it.
            float points = this.getEvaluationFunction(maxBoardState, stealPC, defendPC);
            alphabeta[1] = points;
            
            //System.out.println("alpha points: " + alphabeta[1]);
            
            
            return alphabeta;
        }
        
        float[] maxVal;
        
        for (TablutMove newMove : options) {
            
            TablutBoardState newClone = (TablutBoardState) maxBoardState.clone();
            newClone.processMove(newMove);
            
            //Gets the max value from max's search.
            //System.out.println("New Max");
            maxVal = getMax(newClone, alpha, beta, depth-1, isBlack, stealPC, defendPC, child);
            
            if (maxVal[0] <= beta) {
                if (maxVal[0] <= alpha) {
                    //prune and return current alpha beta values.
                    //this will add a winning move.
                    if (alpha == Integer.MIN_VALUE && stackWinMove.size() == (depth) && !isBlack) {
                        stackWinMove.add(newMove); 
                    } 
//                  else if (alpha == Integer.MAX_VALUE)  {
//                      if ((stackWinMove.size() == (depth-1)) && isBlack) {
//                          System.out.println("Inserting moove + stacksize: " + stackWinMove.size());
//                          stackWinMove.add(newMove);
//                      }
//                  }
                    alphabeta[0] = alpha;
                    alphabeta[1] = alpha; 
 //                 System.out.println("alpha points: " + alphabeta[1]);
                    return alphabeta;
                } else {
                    beta = maxVal[0];
                }
            }
        }
        
        alphabeta[0] = alpha;
        alphabeta[1] = beta;
        //System.out.println("alpha points: " + alphabeta[1]);
        return alphabeta;
    }
    
    private float getEvaluationFunction(TablutBoardState bs, int steal, int def) {
        //part of evaluating. make this into a separate function since both players use it.
        //number of players the opponent has is a heuristic.
        
        //difference between the number of pieces remaining: Steal is Swedes and def is Muscovites
        int minOppNum = steal - bs.getNumberPlayerPieces(opponentPlayer);
        int minMyPlayer = def - bs.getNumberPlayerPieces(this.player_id);
        
        //give the number of white pieces taken a weight of 3.
        int points = 3*minOppNum - minMyPlayer;
        
        //check if we have come to a position in which the king has been captured.
        Coord kingCoord = bs.getKingPosition();
        if (kingCoord == null) {
            if (bs.getWinner() == bs.MUSCOVITE) {
                return Integer.MAX_VALUE;
            }
        }
        
        //This calculates the corner distance.
        int cornerDistance = Coordinates.distanceToClosestCorner(kingCoord);
        int centerHypotenuseDistance = Coordinates.get(0, 0).distance(Coordinates.get(4, 4));
        float netDistance = Math.abs((float) 0.5*((float) centerHypotenuseDistance - (float) cornerDistance));
        
        points -= netDistance; 
        //int centerDistance = kingCoord.distance(Coordinates.get(4, 4));
        if (Coordinates.isCenterOrNeighborCenter(kingCoord)) {
            if (Coordinates.isCenter(kingCoord)) {
                points += 0;
            }
            points += 1;
        } else {
            points += 2;
        }
        
        List<Coord> kingNeighbors = Coordinates.getNeighbors(kingCoord);
        for (Coord c : kingNeighbors) {
            if(!bs.coordIsEmpty(c) && bs.getPieceAt(c).equals(Piece.BLACK)) {
                points +=4;
            }
        }

        return points;
    }
}