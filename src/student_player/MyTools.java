package student_player;

import Saboteur.*;
import Saboteur.cardClasses.*;
import boardgame.Move;
//import student_player.MyTools.Path;

import java.lang.reflect.Array;
import java.util.*;


public class MyTools {
    public static final int BOARD_SIZE = 14;
    public static final int EMPTY = -1;
    public static final int TUNNEL = 1;
    public static final int WALL = 0;
    public static final int originPos = 5;
    public static final int[] origin = new int[]{originPos,originPos};
    public static final int[] originint = new int[] {(originPos*3)+1 ,(originPos*3)+1};
    public static final int[][] hiddenPos = {{originPos+7,originPos-2},{originPos+7,originPos},{originPos+7,originPos+2}};
    public static final int[][] hiddenPosint = {{3*(originPos+7),3*(originPos-2)},{3*(originPos+7),3*(originPos)},{3*(originPos+7),3*(originPos+2)}};
    public static boolean[] hiddenRevealed = {false,false,false};
    public static int nuggetpos=-1;

    public static ArrayList<SaboteurCard> getDeckfromcompo(Map<String,Integer> compo){
        //returns an unshuffled deck
        //Map<String,Integer> compo = SaboteurCard.getDeckcomposition();
        ArrayList<SaboteurCard> deck =new ArrayList<SaboteurCard>();
        String[] tiles ={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
        for(int i=0;i<tiles.length;i++){
            for(int j=0;j<compo.get(tiles[i]);j++){
                deck.add(new SaboteurTile(tiles[i]));
            }
        }
        for(int i = 0; i < compo.get("destroy");i++){
            deck.add(new SaboteurDestroy());
        }
        for(int i = 0; i < compo.get("malus");i++){
            deck.add(new SaboteurMalus());
        }
        for(int i = 0; i < compo.get("bonus");i++){
            deck.add(new SaboteurBonus());
        }
        for(int i = 0; i < compo.get("map");i++){
            deck.add(new SaboteurMap());
        }
        Collections.shuffle(deck);
        return deck;
    }
    public static Map<String,Integer> cloneDeck(Map<String,Integer> deck){
        HashMap<String,Integer> copy = new HashMap<String,Integer>();
        for (Map.Entry<String,Integer> entry : deck.entrySet())
        {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;

    }
    public static double euclideanDistance(int[] source, int[] destination){
        return Math.sqrt((source[1] - destination[1]) * (source[1] - destination[1]) + (source[0] - destination[0]) * (source[0] - destination[0]));
    }

    public static double mean(double[] values){
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum / values.length;
    }
    public static int[][] cloneArray(int[][] src) {
        int length = src.length;
        int[][] target = new int[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
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
    public static boolean pathToMeplaced(int[][] boardog, int[] originPos, int[] targetPos){
        int[][] board =cloneArray(boardog);
        for (int i =0 ;i < board.length;i++){
            for (int j =0;j <board[i].length;j++){
                if(i == targetPos[0] && j == targetPos[1]){
                    if(board[i][j]==1) {
                        board[i][j] = 2;
                    }
                }
                if(board[i][j] == -1 || board[i][j]==0){
                    board[i][j] = 0;
                }
                if(board[i][j]==1){
                    board[i][j]=3;
                }

            }
        }

        board[originPos[0]][originPos[1]] = 1;

        //print2d2(board);
        //System.out.println("");
        boolean existPath = Path.isPath(board, board.length);

        return existPath;

    }


    public static int evaluate(SBoardstateC board){
        int score= 0;

        int[][] intboard = cloneArray(board.getHiddenIntBoard());
        System.out.println("ORGIN:" + intboard[originint[0]][originint[1]]);
        //first evaluate considering the nugget if exists
        //Next evaluate by deduction if there is 2 revealed,
        //Next evaluate your hand, the deck and the board. If favorable make aggressive moves. Else sabotage.
        //Next make sure the move you aren't playing will make the other win.


        if(nuggetpos != -1){
            int[] goalpos = hiddenPosint[nuggetpos];

            //Check if this is a winning move.
            if(pathToMe(intboard,origin,goalpos)){
                return 100;
            }else{
                double maxscore = 1000;
                for (int i = 12; i < (intboard.length-2);i++) {
                    for (int j = 0; j < (intboard[i].length - 2); j++) {
                        //System.out.println("COORDS:" + i +", " + j);
                        int[] destination = new int[]{i, j};
                        if (intboard[i][j] == 1) {
                            //print2d2(intboard);
                            if (pathToMe(intboard, originint, destination)) {
                                System.out.println("There's a path here");
                                destination[0] = destination[0]/3;
                                destination[1] = destination[1]/3;
                                maxscore = Math.min(maxscore, euclideanDistance(destination, goalpos));
                                //System.out.println("Distance to nugget "+euclideanDistance(destination, goalpos));
                            }
                        }
                    }
                }
                return ((int)maxscore);
            }
        }else{
            double maxscore = 1000;

            for (int i = 14; i < (intboard.length-2);i++){
                for(int j = 0; j < (intboard[i].length-2);j++){
                    //System.out.println("COORDS:" + i +", " + j);
                    int[] destination = new int[]{i,j};
                    //If a walkable path
                    if(intboard[i][j]==1) {
                        //If there is a path to origin from destination
                        if (pathToMeplaced(intboard, originint, destination)) {

                            //check for connection points
                            double[] distances = new double[]{0, 0, 0};
                            destination[0] = destination[0]/3;
                            destination[1] = destination[1]/3;
                            for (int k = 0; k < 3; k++) {
                                distances[k] = euclideanDistance(destination, hiddenPos[k]);
                                //System.out.println("Distance to goal " + k + ": " + (10/ distances[k]));
                            }
                            maxscore = Math.min(maxscore, mean(distances));

                        }
                    }
                }
            }
            if(maxscore >0) {
                System.out.println("State of the Board Score:  " +  (10/maxscore));
            }
            return((int)(10/maxscore));
            //Drop, Map play first, Destroy -> Destroy.
        }
    }

    public static double evaluate2(SBoardstateC board){
        int[][] intboard= board.intBoard;
        double result = 20.0;
        if(nuggetpos!=-1){
            System.out.println("We know where the nugget is");
            result= result/euclideanDistance(board.lastplayedpos,hiddenPos[nuggetpos]);
        }else{
            double truecount = 0;
            int index =0;
            double mean = 0;
            //Get the mean distance
            for(boolean rev: hiddenRevealed){

                if(rev==false){
                    truecount+=1;
                    mean+= euclideanDistance(board.lastplayedpos,hiddenPos[index]);
                }
                index+=1;
            }
            //Calcualte average distance to non-revealed tiles since we don't know where the nugget is.
            mean = mean/truecount;
            System.out.println("MEAN: " + mean);
            result= result/mean;
        }

        if(pathToMeplaced(intboard,originint,new int[]{board.lastplayedpos[0]*3+1,board.lastplayedpos[1]*3+1})){
            System.out.println("Path exists");
            result+=6.0;
        }

        //Check if winning move or connects to hidden
        int index = 0;
        for(int[] pos : hiddenPos) {
            if (pathToMeplaced(intboard, originint, pos)) {
                //If nugget.
                if(index==nuggetpos){
                    result+=1000;
                }

                System.out.println("Path exists");
                result += 100;
            }
            index+=1;
        }
        //Destroy tiles that disconnect

        return(result);
    }
    public static void checkHidden(SBoardstateC boardState){
        SaboteurTile[][] boardtiles=boardState.board;

        int index =0;
        int nonhiddencount = 0;
        int falseindex = -1;
        for(int[] pos : hiddenPos){
            if(boardtiles[pos[0]][pos[1]].getIdx().contains("hidden")){
                System.out.println("Updating Hidden");
                hiddenRevealed[index]=true;
                nonhiddencount+=1;
            }
            if(!boardtiles[pos[0]][pos[1]].getIdx().contains("hidden") && !boardtiles[pos[0]][pos[1]].getIdx().contains("nugget")){
                falseindex=index;
            }
            if(boardtiles[pos[0]][pos[1]].getIdx().contains("nugget")){
                nuggetpos=index;
                hiddenRevealed[index]=true;
                break;
            }
            index+=1;
        }
        if(nonhiddencount == 2 && falseindex!=-1){
            hiddenRevealed[falseindex]=true;
            nuggetpos= falseindex;
        }
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
                            //System.out.println("UPDATING: " + id);
                            compo.put(id, compoog.get(id) - 1);
                            if (compo.get(id) < 0) {
                                System.out.println("HUGE ERROR! " + id + " :" + compo.get(id));

                            }
                            //System.out.println("UPDATED! " + id);
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
        int count = 0;
        int index = 0;
        int falseindex = 0;
        for(boolean rev: hiddenRevealed){
            if(rev == true){
                count +=1;
            }else{
                falseindex=index;
            }
        }
        if(count ==2 ){
            System.out.println("We know where the nugget is");
            nuggetpos=falseindex;
        }

        //Return remaining possible moves. Feed to getalllegal.
        return compo;
    }
    public static double minimax(int currentdepth, int maxdepth, SBoardstateC boardState, boolean maxer, double maxerscore){
        System.out.println("Current Depth: " + currentdepth);
/*
        if isMaximizingPlayer :
        bestVal = -INFINITY
        for each move in board :
        value = minimax(board, depth+1, false)
        bestVal = max( bestVal, value)
        return bestVal

*/      double score = evaluate2(boardState);
        if(score > 1000){
            score = 20;
        }

        if(currentdepth == maxdepth)
            return score+maxerscore;

        if (score == 100)
            return score;

        if (score == -100)
            return score;

//Ensure that if we look into the future we don ot help the other win
        if(maxer == true) {
            ArrayList<SaboteurMove> possible_actions = boardState.getAllLegalMoves();
            System.out.println("MOVES:");
            for (int i =0 ; i < possible_actions.size();i++){
                System.out.println(possible_actions.get(i).toPrettyString());
            }
            if (possible_actions.size()== 0)
                return 0;

            double best = -1000;
            SaboteurMove bestmove = possible_actions.get(0);
            SBoardstateC board = new SBoardstateC(boardState);
            for (int i = 0; i < possible_actions.size(); i++) {


                SaboteurMove played = possible_actions.get(i);

                if(!played.getCardPlayed().getName().equals("Drop")) {


                    //Create a new copy board to run simulations on
                    SBoardstateC newboard = makeMove(played,boardState,false);/*new SBoardstateC(boardState);
                    //make the move
                    if (boardState.isLegal(played)) {
                        if (!played.getCardPlayed().getName().equals("Map") || !played.getCardPlayed().getName().equals("Destroy")) {

                            newboard.processMove(played, false);
                        }
                    } else {
                        System.out.println("Tried playing an illegal move!");
                    }*/
                    //printBoard(newboard.board);
                    //System.out.println("The resulting evaluation: " + evaluate2(newboard));

                    //Update the composition of the Deck.
                    newboard.compo = updateDeck(SaboteurCard.getDeckcomposition(), newboard.board);


                    double res = 0;
                    res = minimax((currentdepth + 1), maxdepth, newboard, false, maxerscore + score);
                    //System.out.println("Res:" + res);
                    if (best < res) {
                        bestmove = played;
                        board = newboard;
                    }
                    best = Math.max(best, res);

                }
                //oldBState.processMove(possible_actions.get(i));
                //System.out.println(oldBoardState.getBoardForDisplay());
            }

            System.out.println("Best Move: " +bestmove.toPrettyString());
            System.out.println("The resulting evaluation: " + best);
            printBoard(board.board);
            return best;
        }
        else{


            double best = -1000;
            //Get All legalmoves from player2
            ArrayList<SaboteurMove> possible_actions = boardState.getAllLegalMovesDeck();
            System.out.println("MOVES:");
            SaboteurMove bestmove = possible_actions.get(0);
            SBoardstateC board = new SBoardstateC(boardState);
            for (int i =0 ; i < possible_actions.size();i++){
                System.out.println(possible_actions.get(i).toPrettyString());
            }
            for (int i = 0; i < possible_actions.size(); i++) {

                //make the move
                //update board
                SaboteurMove played = possible_actions.get(i);
                if(!played.getCardPlayed().getName().equals("Drop")) {
                    SBoardstateC newboard = makeMove(played,boardState,false); /*new SBoardstateC(boardState);
                    //Copy player 1 and 2 hands into new board as well as the board and deck. Process the move and update composition
                    if (boardState.isLegal(played)) {
                        if (!played.getCardPlayed().getName().equals("Map") || !played.getCardPlayed().getName().equals("Destroy")) {
                            newboard.processMove(played, false);
                        }
                    } else {
                        System.out.println("Tried playing an illegal move!");
                    }
                    newboard.compo = updateDeck(SaboteurCard.getDeckcomposition(), newboard.board);
*/

                    double res = 0;
                    res = minimax((currentdepth + 1), maxdepth, newboard, true, maxerscore);
                    if (best < res) {
                        bestmove = played;
                        board = newboard;
                    }
                    best = Math.max(best, res);
                }

            }
            System.out.println("Best Move: " +bestmove.toPrettyString());
            System.out.println("The resulting evaluation: " + evaluate2(board));
            printBoard(board.board);
            return best;
        }

    }
    static SaboteurMove findBestMove(int maxdepth, SBoardstateC boardState)
    {
        double bestVal = -1000;

        ArrayList<SaboteurMove> possible_actions = boardState.getAllLegalMoves();
        System.out.println("MOVES:");
        for (int i =0 ; i < possible_actions.size();i++){
            System.out.println(possible_actions.get(i).toPrettyString());
        }
        SaboteurMove bestMove = boardState.getRandomMove();

        SBoardstateC pboard = new SBoardstateC(boardState);
        for (int i = 0; i < possible_actions.size(); i++) {

            SaboteurMove played = possible_actions.get(i);
            SBoardstateC newboard = makeMove(played,boardState,false);
            if(boardState.isLegal(played)) {
                double moveVal = minimax(0, maxdepth, newboard, true, 0);
                // If the value of the current move is
                // more than the best value, then update
                // best
                if (moveVal > bestVal) {
                    bestMove = played;
                    bestVal = moveVal;
                    pboard = newboard;

                }
            }
        }
        System.out.println("Playing simulation from first move: " +bestMove.toPrettyString());
        System.out.println("The resulting evaluation: " + evaluate2(pboard));
        printBoard(pboard.board);

        return bestMove;
    }
    public static SBoardstateC makeMove(SaboteurMove played,SBoardstateC boardState,boolean draw){
        SBoardstateC newboard = new SBoardstateC(boardState);
        boolean play = true;
        if(boardState.isLegal(played) && !played.getCardPlayed().getName().equals("Destroy")) {

            checkHidden(boardState);
            //If we have map card don't play cuz we know where the nugget is
            if(played.getCardPlayed().getName().equals("Map")){
                if(nuggetpos!=-1){
                    play = false;
                    System.out.println("We know where the nugget is");
                }
            }
            if(play==true) {
                newboard.processMove(played, draw);
            }else{
                System.out.println("Not playing a map card because we already know where the nugget is.");
            }
        }else if(!boardState.isLegal(played)){
            System.out.println("Tried playing an illegal move or Destroy");
        }

        newboard.compo=updateDeck(SaboteurCard.getDeckcomposition(),newboard.board);
        return newboard;
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

