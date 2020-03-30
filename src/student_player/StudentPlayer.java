
package student_player;

import Saboteur.*;
import boardgame.Move;
import Saboteur.cardClasses.SaboteurCard;
import java.util.HashMap;
import java.util.Map;
//import student_player.MyTools.Path;

import Saboteur.SaboteurBoardState;
import Saboteur.cardClasses.SaboteurTile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    private ArrayList<SaboteurCard> Deck;
    private Map<String,Integer> compo;
    private ArrayList<SaboteurCard> hand;
    private SaboteurTile[][] board;
    private SaboteurBoardState cboardState;
    public StudentPlayer() {
        super("260729805");
        this.Deck = SaboteurCard.getDeck();
        this.compo = SaboteurCard.getDeckcomposition();
        //this.cboardState = new SaboteurBoardState();
    }
    public void printDeck(){
        for (int i =0 ; i < this.Deck.size();i++){
            System.out.println(this.Deck.get(i).getName());
        }
    }
    public void printBoard(){
        for (int i =0 ;i < board.length;i++){
            for (int j =0;j<board[i].length;j++) {
                if(board[i][j]!=null) {
                    System.out.print(board[i][j].getIdx());
                }else{
                    System.out.print("-");
                }
            }
            System.out.println("");
        }
    }
    public Map<String,Integer> cloneCompo(){
        Map<String,Integer> new_map = new HashMap<String,Integer>();

        // using putAll method
        new_map.putAll(this.compo);
        return(new_map);

    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        // MyTools.getSomething();
        SaboteurTile[][] boardtiles = boardState.getHiddenBoard();
        int[][] board = MyTools.cloneArray(boardState.getHiddenIntBoard());
        this.hand = boardState.getCurrentPlayerCards();
        this.board = boardtiles;

        //Update deck.
        this.compo= SaboteurCard.getDeckcomposition();
        this.compo = MyTools.updateDeck(compo,boardtiles);

        //Init clone
        SBoardstateC clone = new SBoardstateC();
        clone.turnPlayer= boardState.getTurnPlayer();
        clone.player1Cards=MyTools.cloneHand(this.hand);
        clone.board = MyTools.copyTiles(boardState);



        printBoard();
        MyTools.findBestMove(4,clone,compo,hand);
        //Check all possibilities
        // MyTools.minimax(0,0,clone,this.compo,this.hand);
        // Is random the best you can do?
        Move myMove = boardState.getRandomMove();

        printDeck();

        // Return your move to be processed by the server.
        return myMove;
    }
}