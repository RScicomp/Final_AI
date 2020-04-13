
package student_player;

import Saboteur.*;
import boardgame.Move;
import Saboteur.cardClasses.SaboteurCard;

import java.util.Map;
//import student_player.MyTools.Path;

import Saboteur.SaboteurBoardState;
import Saboteur.cardClasses.SaboteurTile;

import java.util.ArrayList;


/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public static SBoardstateC oldboard;
    public static boolean[] hiddenRevealedhist={false,false,false};
    public static int[] maxpath;
    public static boolean stalemate=false;

    public StudentPlayer() {
        super("260729805");
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
        int playerturn=1-boardState.getTurnPlayer();
        ArrayList<SaboteurCard> hand = boardState.getCurrentPlayerCards();
        Map<String,Integer> compo = MyTools.updateDeck(SaboteurCard.getDeckcomposition(),boardState.getHiddenBoard());
        ArrayList<SaboteurCard> deck = MyTools.getDeckfromcompo(compo);


        SBoardstateC clone = new SBoardstateC(boardState,deck);
        //Ensure up to date revealed cards.
        for(int i = 0; i < 3;i++) {
            if (MyTools.pathToMeplaced(boardState.getHiddenIntBoard(), MyTools.originint, MyTools.hiddenPosintmid[i])) {
                hiddenRevealedhist[i] = true;
            }
        }
        if(playerturn==1){
            if(boardState.getNbMalus(0) >0){
                clone.player1nbMalus=1;
            }
        }
        else{
            if(boardState.getNbMalus(1) >0){
                clone.player2nbMalus=1;
            }
        }

        setStalemate(clone);
        clone.stalemate = stalemate;

        clone.compo = compo;
        clone = MyTools.checkHiddenupdate(clone);
        clone= updateRevealHistory(clone);


        SaboteurMove myMove = MyTools.findBestMove(0, clone,boardState.getAllLegalMoves());
        System.out.println("Turn:"+boardState.getTurnPlayer());

        for(int i =0 ; i <boardState.getCurrentPlayerCards().size();i++){
            System.out.println("HAND:"+boardState.getCurrentPlayerCards().get(i).getName());
        }

        if(!boardState.isLegal(myMove)){
            System.out.println("Illegal move!");
            myMove = boardState.getRandomMove();
        }

        oldboard = clone;
        //SaboteurMove myMove = clone.getRandomMove();
        return myMove;
    }
    public static void setStalemate(SBoardstateC board){
        //in a stalemate start saving all your cards.
        SaboteurTile[][] tiles = board.getHiddenBoard();
        SaboteurTile[] threats = {tiles[11][2],tiles[11][4],tiles[11][6],tiles[11][8]};
        SaboteurTile[] threats2 = {tiles[10][2],tiles[10][4],tiles[10][6],tiles[10][8]};
        SaboteurTile[] sidethreats = {tiles[12][1],tiles[12][10]};
        boolean changed = false;

        for (int i = 0; i < threats2.length;i++) {
            if (threats[i] != null){
                if(MyTools.checkConnected(threats[i]) && tiles[9][i] instanceof SaboteurTile){
                    stalemate = true;
                    changed= true;
                    break;
                }
            }
        }
        for (int i = 0; i < sidethreats.length;i++) {
            if (sidethreats[i] != null && tiles[9][i] instanceof SaboteurTile){
                if(MyTools.checkConnected(sidethreats[i])){
                    stalemate = true;
                    changed= true;

                    break;
                }
            }
        }
        for(int i = 0; i <=8;i++){
            if(tiles[9][i] != null && tiles[9][i] instanceof SaboteurTile){
                if(MyTools.checkPointingdown(tiles[9][i])){
                    stalemate = true;
                    changed= true;
                    break;
                }
            }
        }
        if(changed==false){
            stalemate=false;
        }

    }


    public static SBoardstateC updateRevealHistory(SBoardstateC board){
        if(board.turnPlayer == 1) {
            for (int i  =0; i < board.player1hiddenRevealed.length;i++){
                if(hiddenRevealedhist[i]==true && board.player1hiddenRevealed[i]==false){
                    board.player1hiddenRevealed[i]=true;
                    //board.hiddenRevealed[i]=true;
                }
                if(board.player1hiddenRevealed[i]==true){
                    hiddenRevealedhist[i]=true;
                    //board.hiddenRevealed[i]=true;
                }
            }
        }
        else{
            for (int i  =0; i < board.player2hiddenRevealed.length;i++){
                if(hiddenRevealedhist[i]==true && board.player1hiddenRevealed[i]==false){
                    board.player2hiddenRevealed[i]=true;
                    //board.hiddenRevealed[i]=true;
                }
                if(board.player1hiddenRevealed[i]==true){
                    hiddenRevealedhist[i]=true;
                    //board.hiddenRevealed[i]=true;
                }
            }
        }
        return board;

    }

}