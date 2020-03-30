package student_player;

import Saboteur.*;
import Saboteur.cardClasses.*;
import boardgame.Move;
//import student_player.MyTools.Path;

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
    public static final int originPos = 5;
    public static final int[] origin = new int[]{originPos,originPos};
    public static final int[][] hiddenPos = {{originPos+7,originPos-2},{originPos+7,originPos},{originPos+7,originPos+2}};
    public static boolean[] hiddenRevealed = {false,false,false};
    public static int nuggetpos=-1;

    public static double getSomething() {
        return Math.random();
    }

    public static SaboteurMove generateAllMoves(SaboteurTile[][] boardtiles, SaboteurMove[] moves){
        //ArrayList<SaboteurCard> deck =SaboteurCard.getDeck();
        return null;
    }
    public static void printBoard(SaboteurTile[][] board){
        System.out.println("Printing BOARD!");
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
    /*
    public static Map<String,Integer> cloneDeck(Map<String,Integer> deck){
        Map<String,Integer> new_map = new HashMap<String,Integer>();

        // using putAll method
        new_map.putAll(deck);
        return(new_map);

    }*/
    public static Map<String,Integer> cloneDeck(Map<String,Integer> deck){
        HashMap<String,Integer> copy = new HashMap<String,Integer>();
        for (Map.Entry<String,Integer> entry : deck.entrySet())
        {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;

    }
    public static ArrayList<SaboteurCard> cloneHand(ArrayList<SaboteurCard> hand){
        ArrayList<SaboteurCard> hand2 = new ArrayList<SaboteurCard>();
        for ( SaboteurCard card: hand){
            hand2.add(card);
        }
        return(hand2);
    }


    public static Map<String,Integer> updateDeck(Map<String,Integer> compoog, SaboteurTile[][] boardtiles){
        Map<String,Integer> compo = cloneDeck(compoog);

        for (int i = 0; i < boardtiles.length;i++){
            for (int j = 0; j < boardtiles[i].length;j++){
                if(boardtiles[i][j] != null ){
                    String id=boardtiles[i][j].getIdx();

                    //Check if a non card or a hidden card
                    if(!id.equals("entrance")&&!id.equals("hidden1")&&!id.equals("nugget")&&!id.equals("hidden2")) {
                        if(!(i == 12 && (j == hiddenPos[0][1] || j == hiddenPos[1][1]|| j == hiddenPos[2][1]))){

                            id = id.split("_")[0];
                            System.out.println("UPDATING: " + id);
                            compo.put(id, compoog.get(id) - 1);
                            if (compo.get(id) < 0) {
                                System.out.println("HUGE ERROR! " + id + " :" + compo.get(id));

                            }
                            System.out.println("UPDATED! " + id);
                        }
                        //update revealed if not a goal card
                        else{
                            int update;
                            switch(j){
                                case 3:
                                    update = 0;
                                case 5:
                                    update = 1;
                                case 7:
                                    update = 2;
                                default:
                                    update = -1;
                            }
                            if(update > 0 && update < 3 ){
                                if(!hiddenRevealed[update]) {
                                    hiddenRevealed[update] = true;
                                    System.out.println("Revealed Card.");
                                }

                            }

                        }

                    }else{
                        //Deal with nuggets.
                        if(id.equals("nugget")){
                            switch (j) {
                                case 3:
                                    nuggetpos=0;
                                case 5:
                                    nuggetpos=1;
                                case 7:
                                    nuggetpos=2;
                                default:
                                   nuggetpos=-1;
                            }
                            if(nuggetpos >= 0 && nuggetpos <= 2) {
                                hiddenRevealed[nuggetpos] = true;
                            }


                        }


                    }
                }
            }
        }
        //Return remaining possible moves. Feed to getalllegal.
        return compo;
    }

    public static SaboteurTile[][] copyTiles(SaboteurBoardState boardState){

        SaboteurTile[][] hiddenBoard = boardState.getHiddenBoard();
        SaboteurTile[][] copy = new SaboteurTile[hiddenBoard.length][hiddenBoard.length];

        for (int i = 0; i < hiddenBoard.length;i++){
            for(int j = 0; j < hiddenBoard[i].length;j++){
                if(hiddenBoard[i][j] == null){
                    copy[i][j]= null;
                }else {
                    copy[i][j] = new SaboteurTile(hiddenBoard[i][j].getIdx());
                }
            }
        }
        return(copy);
    }
    public static SaboteurTile[][] copyTiles(SBoardstateC boardState){

        SaboteurTile[][] hiddenBoard = boardState.getHiddenBoard();
        SaboteurTile[][] copy = new SaboteurTile[hiddenBoard.length][hiddenBoard.length];

        for (int i = 0; i < hiddenBoard.length;i++){
            for(int j = 0; j < hiddenBoard[i].length;j++){
                if(hiddenBoard[i][j] == null){
                    copy[i][j]= null;
                }else {
                    copy[i][j] = new SaboteurTile(hiddenBoard[i][j].getIdx());
                }
            }
        }
        return(copy);
    }
    public static SaboteurTile[][] copyTiles(SaboteurTile[][] hiddenBoard){


        SaboteurTile[][] copy = new SaboteurTile[hiddenBoard.length][hiddenBoard.length];

        for (int i = 0; i < hiddenBoard.length;i++){
            for(int j = 0; j < hiddenBoard[i].length;j++){
                if(hiddenBoard[i][j] == null){
                    copy[i][j]= null;
                }else {
                    copy[i][j] = new SaboteurTile(hiddenBoard[i][j].getIdx());
                }
            }
        }
        return(copy);
    }
    public static int[][] getHiddenIntBoard(SaboteurBoardState boardState) {
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
    public static int[][] copyint(SaboteurBoardState boardState){

        int[][] hiddenBoard = boardState.getHiddenIntBoard();
        int[][] copy = new int[hiddenBoard.length][hiddenBoard.length];

        for (int i = 0; i < hiddenBoard.length;i++){
            for(int j = 0; j < hiddenBoard[j].length;j++){
                copy[i][j] = hiddenBoard[i][j];
            }
        }
        return(copy);
    }
    public static SaboteurTile[][] placeBoard(SaboteurMove move,SaboteurBoardState boardState){
        SaboteurTile[][] board = copyTiles(boardState);
        SaboteurCard card = move.getCardPlayed();
        int[] pos = move.getPosPlayed();
        String cardName = card.getName();
        //Play card if not a special card
        if(!cardName.equals("Drop")&& !cardName.equals("Bonus")
                && !cardName.equals("Malus") && !cardName.equals("Destroy") &&!cardName.equals("Map")) {

            board[pos[0]][pos[1]] = new SaboteurTile(((SaboteurTile) card).getIdx());
        }else{
            switch(cardName){
                case "Destroy":
                    board[pos[0]][pos[1]] = null;
            }
        }
        return board;

    }
    public static SaboteurTile[][] placeBoard(SaboteurMove move,SBoardstateC boardState){
        SaboteurTile[][] board = copyTiles(boardState);
        SaboteurCard card = move.getCardPlayed();
        int[] pos = move.getPosPlayed();
        String cardName = card.getName();
        //Play card if not a special card
        if(!cardName.equals("Drop")&& !cardName.equals("Bonus")
                && !cardName.equals("Malus") && !cardName.equals("Destroy") &&!cardName.equals("Map")) {

            board[pos[0]][pos[1]] = new SaboteurTile(((SaboteurTile) card).getIdx());
        }else{
            switch(cardName){
                case "Destroy":
                    board[pos[0]][pos[1]] = null;
            }
        }
        return board;

    }

    public static double euclideanDistance(int[] source, int[] destination){
        return Math.sqrt((source[1] - destination[1]) * (source[1] - destination[1]) + (source[0] - destination[0]) * (source[0] - destination[0]));
    }

    public double mean(double[] values){
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum / values.length;
    }


    public static int[][] getHiddenIntBoardTile(SaboteurTile[][] board) {
        //update the int board, and provide it to the player with the hidden objectives set at EMPTY.
        //Note that this function is available to the player.
        int[][] intBoard = new int[BOARD_SIZE][BOARD_SIZE];

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
    public static int evaluate(SBoardstateC board, Map<String,Integer> deck, ArrayList<SaboteurCard> hand){
        int score= 0;
        int[][] intboard = cloneArray(board.getHiddenIntBoard());
        //first evaluate considering the nugget if exists
        //Next evaluate by deduction if there is 2 revealed,
        //Next evaluate your hand, the deck and the board. If favorable make aggressive moves. Else sabotage.
        //Next make sure the move you aren't playing will make the other win.

        //This is a winning move.
        if(nuggetpos != -1){
            if(pathToMe(intboard,origin,hiddenPos[nuggetpos])){
                return 100;
            }
        }


        return score;
    }

    public static int minimax(int currentdepth, int maxdepth, SBoardstateC boardState, Map<String,Integer> deck, ArrayList<SaboteurCard> hand,boolean maxer){
        System.out.println("Current Depth: " + currentdepth);

        SaboteurTile[][] currentState = copyTiles(boardState);
        SaboteurTile[][] newState;
        ArrayList<SaboteurCard> newHand = cloneHand(hand);
        Map<String,Integer> newDeck = cloneDeck(deck);

        //int[][] currentintState = getHiddenIntBoard(boardState);
/*
        if isMaximizingPlayer :
        bestVal = -INFINITY
        for each move in board :
        value = minimax(board, depth+1, false)
        bestVal = max( bestVal, value)
        return bestVal

*/      int score = evaluate(boardState,newDeck,newHand);

        if(currentdepth == maxdepth)
            return score;

        if (score == 100)
            return score;
        if (score == -100)
            return score;


        if(maxer == true) {
            ArrayList<SaboteurMove> possible_actions = boardState.getAllLegalMoves();

            if (possible_actions.size()== 0)
                return 0;

            int best = -1000;
            int best_value = Integer.MIN_VALUE;//Assuming maximizer
            for (int i = 0; i < possible_actions.size(); i++) {

                //make the move
                //update board
                SaboteurMove played = possible_actions.get(i);
                newState = placeBoard(played, boardState);
                //update hand and deck (making sure to clone within functions)
                newHand = cloneHand(hand);
                newHand.remove(played.getCardPlayed());

                newDeck = updateDeck(deck, newState);
                printBoard(newState);

                SBoardstateC newboard = new SBoardstateC();
                newboard.turnPlayer = boardState.getTurnPlayer();
                newboard.player1Cards = cloneHand(newHand);
                newboard.board = copyTiles(newState);
                //newboard.processMove(played);

                best = Math.max(best, minimax((currentdepth + 1), maxdepth, newboard, newDeck, newHand,false));

                //oldBState.processMove(possible_actions.get(i));
                //System.out.println(oldBoardState.getBoardForDisplay());
            }
            return best;
        }
        else{
            int best = 1000;
            ArrayList<SaboteurMove> possible_actions = new ArrayList<>();

            for (int i = 0; i < possible_actions.size(); i++) {

                //make the move
                //update board
                SaboteurMove played = possible_actions.get(i);
                newState = placeBoard(played, boardState);
                //update hand and deck (making sure to clone within functions)
                //newHand = cloneHand(decklist);
                newHand.remove(played.getCardPlayed());
                newDeck = updateDeck(deck, newState);
                printBoard(newState);

                SBoardstateC newboard = new SBoardstateC();
                newboard.turnPlayer = boardState.getTurnPlayer();
                newboard.player1Cards = cloneHand(newHand);

                newboard.board = copyTiles(newState);


                best = Math.max(best, minimax((currentdepth + 1), maxdepth, newboard, newDeck, newHand,true));

                //oldBState.processMove(possible_actions.get(i));
                //System.out.println(oldBoardState.getBoardForDisplay());
            }
            return best;
        }

    }

    static SaboteurMove findBestMove(int maxdepth, SBoardstateC boardState, Map<String,Integer> deck, ArrayList<SaboteurCard> hand)
    {
        int bestVal = -1000;



        ArrayList<SaboteurMove> possible_actions = boardState.getAllLegalMoves();
        SaboteurMove bestMove = possible_actions.get(0);
        SaboteurTile[][] currentState = copyTiles(boardState);
        SaboteurTile[][] newState;

        Map<String,Integer> newDeck = cloneDeck(deck);
        ArrayList<SaboteurCard> newHand = cloneHand(hand);

        for (int i = 0; i < possible_actions.size(); i++) {
            // compute evaluation function for this
            // move.
            // Make the move
            SaboteurMove played = possible_actions.get(i);
            newState = placeBoard(played, boardState);
            //update hand and deck (making sure to clone within functions)
            newHand = cloneHand(hand);
            newHand.remove(played.getCardPlayed());
            newDeck = updateDeck(deck, newState);
            printBoard(newState);

            SBoardstateC newboard = new SBoardstateC();
            newboard.turnPlayer = boardState.getTurnPlayer();
            newboard.player1Cards = cloneHand(newHand);
            newboard.board = copyTiles(newState);

            int moveVal = minimax(0,maxdepth,newboard,newDeck,newHand,true);
            // If the value of the current move is
            // more than the best value, then update
            // best/
            if (moveVal > bestVal)
            {
                bestMove=played;
            }
        }


        return bestMove;
    }

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
    public static boolean pathToMe(int[][] boardog, int[] originPos, int[] targetPos){
        int[][] board =cloneArray(boardog);
        for (int i =0 ;i < board.length;i++){
            for (int j =0;j <board[i].length;j++){
                if(board[i][j] == -1 || board[i][j]==0){
                    board[i][j] = 0;
                }
                if(board[i][j]==1){
                    board[i][j]=3;
                }
            }
        }

        board[originPos[0]][originPos[1]] = 1;

        int endX = targetPos[0];
        int endY = targetPos[1];


        board[endX][endY]=2;
        board[(endX+1)][endY]=2;
        board[(endX+2)][endY]=2;

        board[endX][endY+1]=2;
        board[endX+1][endY+1]=2;
        board[endX+2][endY+1]=2;
        board[endX][endY+2]=2;
        board[endX+1][endY+2]=2;
        board[endX+2][endY+2]=2;

        //print2d2(board);
        //System.out.println("");
        boolean existPath = Path.isPath(board, board.length);

        return existPath;

    }

    static class Path {

        // method for finding and printing
        // whether the path exists or not

        public static boolean isPath(int matrix[][], int n)
        {
            // defining visited array to keep
            // track of already visited indexes
            boolean visited[][] = new boolean[n][n];

            // flag to indicate whether the path exists or not
            boolean flag=false;
            for(int i=0;i<n;i++)
            {
                for(int j=0;j<n;j++)
                {
                    // if matrix[i][j] is source
                    // and it is not visited
                    if(matrix[i][j]==1 && !visited[i][j])

                        // starting from i, j and then finding the path
                        if(isPath(matrix, i, j, visited))
                        {
                            flag=true; // if path exists
                            break;
                        }
                }
            }

            if(flag)
                return true;
            else
                return false;
        }


        // method for checking boundries
        public static boolean isSafe(int i, int j, int matrix[][])
        {

            if(i>=0 && i<matrix.length && j>=0
                    && j<matrix[0].length)
                return true;
            return false;
        }

        // Returns true if there is a path from a source (a
        // cell with value 1) to a destination (a cell with
        // value 2)
        public static boolean isPath(int matrix[][],
                                     int i, int j, boolean visited[][]){

            // checking the boundries, walls and
            // whether the cell is unvisited
            if(isSafe(i, j, matrix) && matrix[i][j]!=0
                    && !visited[i][j])
            {
                // make the cell visited
                visited[i][j]=true;

                // if the cell is the required
                // destination then return true
                if(matrix[i][j]==2)
                    return true;

                // traverse up
                boolean up = isPath(matrix, i-1, j, visited);

                // if path is found in up direction return true
                if(up)
                    return true;

                // traverse left
                boolean left = isPath(matrix, i, j-1, visited);

                // if path is found in left direction return true
                if(left)
                    return true;

                //traverse down
                boolean down = isPath(matrix, i+1, j, visited);

                // if path is found in down direction return true
                if(down)
                    return true;

                // traverse right
                boolean right = isPath(matrix, i, j+1, visited);

                // if path is found in right direction return true
                if(right)
                    return true;
            }
            return false; // no path has been found
        }

        // driver program to check above function


    }

}

