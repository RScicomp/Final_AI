package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurMove;
import boardgame.Move;
import Saboteur.cardClasses.SaboteurCard;
//import student_player.MyTools.Path;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;
import Saboteur.cardClasses.SaboteurTile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class MyTools {
    public static final int BOARD_SIZE = 14;
    public static final int EMPTY = -1;
    public static final int TUNNEL = 1;
    public static final int WALL = 0;

    public static double getSomething() {
        return Math.random();
    }

    public static SaboteurMove generateAllMoves(SaboteurTile[][] boardtiles, SaboteurMove[] moves){
        //ArrayList<SaboteurCard> deck =SaboteurCard.getDeck();
        return null;
    }
    public static Map<String,Integer> updateDeck(Map<String,Integer> compo, SaboteurTile[][] boardtiles){

        for (int i = 0; i < boardtiles.length;i++){
            for (int j = 0; j < boardtiles[i].length;j++){
                if(boardtiles[i][j] != null ){
                    String id=boardtiles[i][j].getIdx();

                    if(!id.equals("entrance")&&!id.equals("hidden1")&&!id.equals("nugget")&&!id.equals("hidden2")) {
                        id = id.split("_")[0];
                        System.out.println("UPDATING: " + id);
                        compo.put(id, compo.get(id) - 1);
                        if(compo.get(id)<0){
                            System.out.println("HUGE ERROR!");
                        }
                        System.out.println("UPDATED! " + id);
                    }
                }
            }
        }
        //Return remaining possible moves. Feed to getalllegal.
        return compo;
    }

    public SaboteurTile[][] copyTiles(SaboteurBoardState boardState){

        SaboteurTile[][] hiddenBoard = boardState.getHiddenBoard();
        SaboteurTile[][] copy = new SaboteurTile[hiddenBoard.length][hiddenBoard.length];

        for (int i = 0; i < hiddenBoard.length;i++){
            for(int j = 0; j < hiddenBoard[j].length;j++){
                copy[i][j] = new SaboteurTile(hiddenBoard[i][j].getIdx());
            }
        }
        return(copy);
    }
    public int[][] getHiddenIntBoard(SaboteurBoardState boardState) {
        //update the int board, and provide it to the player with the hidden objectives set at EMPTY.
        //Note that this function is available to the player.
        int[][] intBoard = new int[BOARD_SIZE][BOARD_SIZE];
        SaboteurTile[][] board = boardState.getHiddenBoard();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(board[i][j] == null){
                    for (int k = 0; k < 3; k++) {
                        for (int h = 0; h < 3; h++) {
                            intBoard[i * 3 + k][j * 3 + h] = EMPTY;
                        }
                    }
                }
                else {

                        int[][] path = board[i][j].getPath();
                        for (int k = 0; i < 3; i++) {
                            for (int h = 0; i < 3; i++) {
                                intBoard[i * 3 + k][j * 3 + h] = path[h][2-k];
                            }
                        }

                }
            }
        }

        return intBoard;
    }
    public int[][] copyint(SaboteurBoardState boardState){

        int[][] hiddenBoard = boardState.getHiddenIntBoard();
        int[][] copy = new int[hiddenBoard.length][hiddenBoard.length];

        for (int i = 0; i < hiddenBoard.length;i++){
            for(int j = 0; j < hiddenBoard[j].length;j++){
                copy[i][j] = hiddenBoard[i][j];
            }
        }
        return(copy);
    }
    public int[][] placeBoard(SaboteurBoardState boardState){
        int[][] board = copyint(boardState);

        return board;

    }


    public static SaboteurMove minimax(int currentdepth, int maxdepth, SaboteurBoardState boardState){

        ArrayList<SaboteurMove> possible_actions = boardState.getAllLegalMoves();
        SaboteurTile[][] hiddenBoard = boardState.getHiddenBoard();
        int[][] hiddenboardint= boardState.getHiddenIntBoard();
        SaboteurTile[][] oldBoardState = new SaboteurTile[hiddenBoard.length][hiddenBoard[0].length];

        for (int i = 0; i < hiddenBoard.length;i++){
            for(int j = 0; j < hiddenBoard[j].length;j++){
                oldBoardState[i][j] = new SaboteurTile(hiddenBoard[i][j].getIdx());
            }
        }

        int best_value = Integer.MIN_VALUE;//Assuming maximizer
        for (int i = 0; i <possible_actions.size();i++){
            //oldBoardState.processMove(possible_actions.get(i));
            //System.out.println(oldBoardState.getBoardForDisplay());
        }
        return null;
    }
    /*
    public static ArrayList<SaboteurMove> getAllPossibleMoves(int depth){

        for (int i = 0; i < depth; i++){

        }
    }*/

    public static int[][] cloneArray(int[][] src) {
        int length = src.length;
        int[][] target = new int[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }

    public static void printDeck(ArrayList<SaboteurCard> Deck){
        for (int i =0 ; i < Deck.size();i++){
            System.out.println(Deck.get(i).getName());
        }
    }

}