package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;
import boardgame.Move;
//import student_player.MyTools.Path;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

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
    public static Map<String,Integer> cloneDeck(Map<String,Integer> deck){
        Map<String,Integer> new_map = new HashMap<String,Integer>();

        // using putAll method
        new_map.putAll(deck);
        return(new_map);

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
                            compo.put(id, compo.get(id) - 1);
                            if (compo.get(id) < 0) {
                                System.out.println("HUGE ERROR!");
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
    public static int evaluate(SaboteurTile[][] board, int[] placedPos, Map<String,Integer> deck, ArrayList<SaboteurCard> hand){
        int score= 0;
        int[][] intboard = getHiddenIntBoardTile(board);
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

    public static SaboteurMove minimax(int currentdepth, int maxdepth, SaboteurBoardState boardState, Map<String,Integer> deck, ArrayList<SaboteurCard> hand){

        ArrayList<SaboteurMove> possible_actions = boardState.getAllLegalMoves();
        SaboteurTile[][] currentState = copyTiles(boardState);
        SaboteurTile[][] newState;

        Map<String,Integer> newDeck = cloneDeck(deck);
        ArrayList<SaboteurCard> newHand = cloneHand(hand);
        //int[][] currentintState = getHiddenIntBoard(boardState);
/*
        if isMaximizingPlayer :
        bestVal = -INFINITY
        for each move in board :
        value = minimax(board, depth+1, false)
        bestVal = max( bestVal, value)
        return bestVal
*/

        int best_value = Integer.MIN_VALUE;//Assuming maximizer
        for (int i = 0; i <possible_actions.size();i++){
            //update board
            SaboteurMove played = possible_actions.get(i);
            newState = placeBoard(played,boardState);

            //update hand and deck (making sure to clone within functions)
            newHand = cloneHand(hand);
            newHand.remove(played.getCardPlayed());
            newDeck = updateDeck(deck,newState);
            printBoard(newState);

            //int value = minimax((currentdepth+1), maxdepth, boardState, newDeck,newHand);


            //oldBState.processMove(possible_actions.get(i));
            //System.out.println(oldBoardState.getBoardForDisplay());
        }
        return null;
    }
    /*
    public static ArrayList<SaboteurMove> getAllPossibleMoves(int depth){

        for (int i = 0; i < depth; i++){

        }
    }*/
    /*
    public boolean isLegal(SaboteurMove m,ArrayList<SaboteurCard> hand,int player1nbMalus,SaboteurTile[][] board) {
        // For a move to be legal, the player must have the card in its hand
        // and then the game rules apply.
        // Note that we do not test the flipped version. To test it: use the flipped card in the SaboteurMove object.

        SaboteurCard testCard = m.getCardPlayed();
        int[] pos = m.getPosPlayed();
        int currentPlayer = m.getPlayerID();


        boolean isBlocked;

        isBlocked= player1nbMalus > 0;

        if(testCard instanceof SaboteurDrop){
            if(hand.size()>=pos[0]){
                return true;
            }
        }
        boolean legal = false;
        for(SaboteurCard card : hand){
            if (card instanceof SaboteurTile && testCard instanceof SaboteurTile && !isBlocked) {
                if(((SaboteurTile) card).getIdx().equals(((SaboteurTile) testCard).getIdx())){
                    return verifyLegit(((SaboteurTile) card).getPath(),pos,board);
                }
                else if(((SaboteurTile) card).getFlipped().getIdx().equals(((SaboteurTile) testCard).getIdx())){
                    return verifyLegit(((SaboteurTile) card).getFlipped().getPath(),pos,board);
                }
            }
            else if (card instanceof SaboteurBonus && testCard instanceof SaboteurBonus) {

                if (player1nbMalus > 0) return true;

                return false;
            }
            else if (card instanceof SaboteurMalus && testCard instanceof SaboteurMalus ) {
                return true;
            }
            else if (card instanceof SaboteurMap && testCard instanceof SaboteurMap) {
                int ph = 0;
                for(int j=0;j<3;j++) {
                    if (pos[0] == hiddenPos[j][0] && pos[1] == hiddenPos[j][1]) ph=j;
                }
                if (!this.hiddenRevealed[ph])
                    return true;
            }
            else if (card instanceof SaboteurDestroy && testCard instanceof SaboteurDestroy) {
                int i = pos[0];
                int j = pos[1];
                if (board[i][j] != null && (i != originPos || j != originPos) && (i != hiddenPos[0][0] || j != hiddenPos[0][1])
                        && (i != hiddenPos[1][0] || j != hiddenPos[1][1]) && (i != hiddenPos[2][0] || j != hiddenPos[2][1])) {
                    return true;
                }
            }
        }
        return legal;
    }

    public ArrayList<SaboteurMove> getAllLegalMoves(ArrayList<SaboteurCard> hand,int player1nbMalus) {
        // Given the current player hand, gives back all legal moves he can play.
        boolean isBlocked=player1nbMalus > 0;

        ArrayList<SaboteurMove> legalMoves = new ArrayList<>();

        for(SaboteurCard card : hand){
            if( card instanceof SaboteurTile && !isBlocked) {
                ArrayList<int[]> allowedPositions = possiblePositions((SaboteurTile)card);
                for(int[] pos:allowedPositions){
                    legalMoves.add(new SaboteurMove(card,pos[0],pos[1],turnPlayer));
                }
                //if the card can be flipped, we also had legal moves where the card is flipped;
                if(SaboteurTile.canBeFlipped(((SaboteurTile)card).getIdx())){
                    SaboteurTile flippedCard = ((SaboteurTile)card).getFlipped();
                    ArrayList<int[]> allowedPositionsflipped = possiblePositions(flippedCard);
                    for(int[] pos:allowedPositionsflipped){
                        legalMoves.add(new SaboteurMove(flippedCard,pos[0],pos[1],turnPlayer));
                    }
                }
            }
            else if(card instanceof SaboteurBonus){
                if(turnPlayer ==1){
                    if(player1nbMalus > 0) legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
                }
                else if(player2nbMalus>0) legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
            }
            else if(card instanceof SaboteurMalus){
                legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
            }
            else if(card instanceof SaboteurMap){
                for(int i =0;i<3;i++){ //for each hidden card that has not be revealed, we can still take a look at it.
                    if(! this.hiddenRevealed[i]) legalMoves.add(new SaboteurMove(card,hiddenPos[i][0],hiddenPos[i][1],turnPlayer));
                }
            }
            else if(card instanceof SaboteurDestroy){
                for (int i = 0; i < BOARD_SIZE; i++) {
                    for (int j = 0; j < BOARD_SIZE; j++) { //we can't destroy an empty tile, the starting, or final tiles.
                        if(this.board[i][j] != null && (i!=originPos || j!= originPos) && (i != hiddenPos[0][0] || j!=hiddenPos[0][1] )
                                && (i != hiddenPos[1][0] || j!=hiddenPos[1][1] ) && (i != hiddenPos[2][0] || j!=hiddenPos[2][1] ) ){
                            legalMoves.add(new SaboteurMove(card,i,j,turnPlayer));
                        }
                    }
                }
            }
        }
        // we can also drop any of the card in our hand
        for(int i=0;i<hand.size();i++) {
            legalMoves.add(new SaboteurMove(new SaboteurDrop(), i, 0, turnPlayer));
        }
        return legalMoves;
    }
    public ArrayList<int[]> possiblePositions(SaboteurTile card,SaboteurTile[][] board) {
        // Given a card, returns all the possiblePositions at which the card could be positioned in an ArrayList of int[];
        // Note that the card will not be flipped in this test, a test for the flipped card should be made by giving to the function the flipped card.
        ArrayList<int[]> possiblePos = new ArrayList<int[]>();
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}}; //to make the test faster, we simply verify around all already placed tiles.
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != null) {
                    for (int m = 0; m < 4; m++) {
                        if (0 <= i+moves[m][0] && i+moves[m][0] < BOARD_SIZE && 0 <= j+moves[m][1] && j+moves[m][1] < BOARD_SIZE) {
                            if (verifyLegit(card.getPath(), new int[]{i + moves[m][0], j + moves[m][1]},board )){
                                possiblePos.add(new int[]{i + moves[m][0], j +moves[m][1]});
                            }
                        }
                    }
                }
            }
        }
        return possiblePos;
    }
    public boolean verifyLegit(int[][] path,int[] pos,SaboteurTile[][] board){
        // Given a tile's path, and a position to put this path, verify that it respects the rule of positionning;
        if (!(0 <= pos[0] && pos[0] < BOARD_SIZE && 0 <= pos[1] && pos[1] < BOARD_SIZE)) {
            return false;
        }
        if(board[pos[0]][pos[1]] != null) return false;

        //the following integer are used to make sure that at least one path exists between the possible new tile to be added and existing tiles.
        // There are 2 cases:  a tile can't be placed near an hidden objective and a tile can't be connected only by a wall to another tile.
        int requiredEmptyAround=4;
        int numberOfEmptyAround=0;

        ArrayList<SaboteurTile> objHiddenList=new ArrayList<>();
        for(int i=0;i<3;i++) {
            if (!hiddenRevealed[i]){
                objHiddenList.add(board[hiddenPos[i][0]][hiddenPos[i][1]]);
            }
        }
        //verify left side:
        if(pos[1]>0) {
            SaboteurTile neighborCard = board[pos[0]][pos[1] - 1];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                if (path[0][0] != neighborPath[2][0] || path[0][1] != neighborPath[2][1] || path[0][2] != neighborPath[2][2] ) return false;
                else if(path[0][0] == 0 && path[0][1]== 0 && path[0][2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify right side
        if(pos[1]<BOARD_SIZE-1) {
            SaboteurTile neighborCard = board[pos[0]][pos[1] + 1];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                if (path[2][0] != neighborPath[0][0] || path[2][1] != neighborPath[0][1] || path[2][2] != neighborPath[0][2]) return false;
                else if(path[2][0] == 0 && path[2][1]== 0 && path[2][2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify upper side
        if(pos[0]>0) {
            SaboteurTile neighborCard = board[pos[0]-1][pos[1]];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                int[] p={path[0][2],path[1][2],path[2][2]};
                int[] np={neighborPath[0][0],neighborPath[1][0],neighborPath[2][0]};
                if (p[0] != np[0] || p[1] != np[1] || p[2] != np[2]) return false;
                else if(p[0] == 0 && p[1]== 0 && p[2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify bottom side:
        if(pos[0]<BOARD_SIZE-1) {
            SaboteurTile neighborCard = board[pos[0]+1][pos[1]];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                int[] p={path[0][0],path[1][0],path[2][0]};
                int[] np={neighborPath[0][2],neighborPath[1][2],neighborPath[2][2]};
                if (p[0] != np[0] || p[1] != np[1] || p[2] != np[2]) return false;
                else if(p[0] == 0 && p[1]== 0 && p[2] ==0 ) numberOfEmptyAround +=1; //we are touching by a wall
            }
        }
        else numberOfEmptyAround+=1;

        if(numberOfEmptyAround==requiredEmptyAround)  return false;

        return true;
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

