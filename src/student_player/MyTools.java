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
    public static final int[][] hiddenPosintmid = {{3*(originPos+7)+1,3*(originPos-2)+1},{3*(originPos+7)+1,3*(originPos)+1},{3*(originPos+7)+1,3*(originPos+2)+1}};

    public static boolean[] hiddenRevealedhist={false,false,false};
    public static boolean[] hiddenRevealed = {false,false,false};
    public static int nuggetpos=-1;

    //Ensure that if a hidden tile is revealed via a path, that it stays revealed.
    public static SBoardstateC updateRevealHistory(SBoardstateC board){
        if(board.turnPlayer == 1) {
            for (int i  =0; i < board.player1hiddenRevealed.length;i++){
                if(hiddenRevealedhist[i]==true && board.player1hiddenRevealed[i]==false){
                    board.player1hiddenRevealed[i]=true;
                }
                if(board.player1hiddenRevealed[i]==true){
                    hiddenRevealedhist[i]=true;
                }
            }
        }
        else{
            for (int i  =0; i < board.player2hiddenRevealed.length;i++){
                if(hiddenRevealedhist[i]==true && board.player1hiddenRevealed[i]==false){
                    board.player2hiddenRevealed[i]=true;
                }
                if(board.player1hiddenRevealed[i]==true){
                    hiddenRevealedhist[i]=true;
                }
            }
        }
        return board;

    }
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




    //count the number of hiddens revealed by a move
    public static int countHiddenStrategy(SBoardstateC board){
        int cc = 0;
        for (int i = 0; i < 3; i++) {
            if (    pathToMeplaced(board.intBoard, originint, new int[]{hiddenPos[i][0] * 3 + 1, hiddenPos[i][1] * 3 + 1})
                //pathToMeplaced(board.intBoard, new int[]{board.lastplayedpos[0]*3+1,board.lastplayedpos[1]*3+1}, new int[]{hiddenPos[i][0] * 3 + 1, hiddenPos[i][1] * 3 + 1})
            ) cc++;
        }
        //System.out.println("count is: "+cc);
        return cc;
    }

// PROGRESS:
// - does really well fighting for the first hidden tile.

// MAIN CONCERN:
// - has trouble going for other hidden / goal tile(s) after connecting to the first one
//      - the move that allows connection to a new hidden OR goal tile is not detected
// - need to code bonus and malus card

// SMALLER ISSUES:
// - might need more complexity after revealing goal tile
// - need to code destroy card
    public static double destroy(SBoardstateC board){
        double result =0;
        if(board.lastplayed.getCardPlayed().getName().equals("Destroy")){
            result+=-20;

        }
        return result;
    }
    public static double evaluate2(SBoardstateC board){
        int[][] intboard= board.intBoard;
        double result = 20.0;

        if(nuggetpos!=-1){
            System.out.println("We know where the nugget is");
            result= result/euclideanDistance(board.lastplayedpos,hiddenPos[nuggetpos]);

            //check winning condition
            if (pathToMeplaced(board.intBoard,originint,
                    new int[]{(hiddenPos[nuggetpos][0] * 3 + 1), (hiddenPos[nuggetpos][1] * 3 + 1)})){
                result += 1000;
            }

        }else{
            double truecount = 0;
            int ind =0;
            double mean = 0;
            //Get the mean distance
            for(boolean rev: hiddenRevealed){

                if(rev==false){
                    truecount+=1;
                    mean+= euclideanDistance(board.lastplayedpos,hiddenPos[ind]);
                }
                ind+=1;
            }
            //Calculate average distance to non-revealed tiles since we don't know where the nugget is.
            mean = mean/truecount;
            System.out.println("MEAN: " + mean);
            result= result/mean;
        }

        // supposedly multiplies the number of hidden tiles connected to the bonus
        // but ocationally doesn't seem to choose to connect even when it does find a path???
        // could be a minor problem with this function but it seems that it works most times
        result += MyTools.countHiddenStrategy(board)*200;
        result += saveCardsStrategy(board) * 100;
        //replaced with countHiddenStrategy()
        //Check if winning move or connects to hidden
/*
    int index = 0;
    for(int[] pos : hiddenPos) {
        if (pathToMeplaced(intboard, originint, new int[]{pos[0]*3+1,pos[1]*3+1} )) {
            //If nugget.
            if(index==nuggetpos){
                result+=1000;
            }
            System.out.println("Path exists to hidden");
            result += 50;
        }
        index+=1;
    }
*/

        // give bonus for finding a path from origin
        if(pathToMeplaced(intboard,originint,new int[]{board.lastplayedpos[0]*3+1,board.lastplayedpos[1]*3+1})){
            System.out.println("Path exists");
            result+=10.0;
        }


        // safe guard. weight down the moves that may allow opponents to connect to a hidden tile
        // this seems to stop working after we connect to a hidden tile
        // I might need to add some extra logic somewhere
        // maybe:
        // play slightly different strategies based on the which tiles are revealed?

        if(     euclideanDistance(board.lastplayedpos, hiddenPos[0]) == 2.0 ||
                euclideanDistance(board.lastplayedpos, hiddenPos[1]) == 2.0 ||
                euclideanDistance(board.lastplayedpos, hiddenPos[2]) == 2.0 ||
                euclideanDistance(board.lastplayedpos, hiddenPos[0]) == Math.sqrt(2) ||
                euclideanDistance(board.lastplayedpos, hiddenPos[1]) == Math.sqrt(2) ||
                euclideanDistance(board.lastplayedpos, hiddenPos[2]) == Math.sqrt(2)) {
            result -= 60;
        }



        //Destroy tiles that disconnect

        return(result);
    }
    public static boolean checkConnected(SaboteurCard card){
        if(tileCard(card)) {
            int[][] path= ((SaboteurTile)card).getPath();
            if(path[1][1]==1) {
                if (path[0][1] == 1 && path[2][1] == 1 || path[0][1] == 1 && path[1][0] == 1 ||
                        path[0][1] == 1 && path[1][2] == 1 || path[1][0] == 1 && path[1][2] == 1) {
                    return true;
                }
            }
        }
        return false;

    }
    public static double valueConnected(SBoardstateC board){
        SaboteurCard card=board.lastplayed.getCardPlayed();
        if(tileCard(card)){
            int[][] path= ((SaboteurTile)card).getPath();
            if(path[1][1] ==1){
                if(path[0][1] == 1 && path[2][1] == 1 || path[0][1] == 1 && path[1][0] == 1 ||
                        path[0][1] == 1 && path[1][2] == 1 || path[1][0] == 1 && path[1][2] == 1){
                    return 20;
                }
            }
        }
        return 0;
    }
    public static boolean tileCard(SaboteurCard card){
        return (card.getName().contains("Tile"));
    }
    public static double valueDownwardsStrategy(SBoardstateC board){
        double result=0;
        if(board.turnNumber > 2 && board.lastplayed != null) {
            SaboteurCard cardPlayed = board.lastplayed.getCardPlayed();
            if (cardPlayed.getName().contains("Tile")) {
                if (board.lastplayedpos[0] >= 5 && board.lastplayedpos[0] < 13) {
                    int[][] path = ((SaboteurTile) cardPlayed).getPath();
                    if (path[2][1] == 1) {
                        result += 5;
                        System.out.println("HEY This card is a card that points downwards");
                    }
                }
            }
        }
        return(result);
    }
    public static int[] converttoint(int[] pos){
        pos[0]=pos[0]*3;
        pos[1]=pos[1]*3;
        return pos;
    }
    public static double valueBackwards(SBoardstateC board){
        double result = 0;
        if(nuggetpos != -1){
            SaboteurCard cardPlayed = board.lastplayed.getCardPlayed();
            if(cardPlayed.getName().contains("Tile")){
                if(pathToMeplaced(board.getHiddenIntBoard(),board.lastplayedpos,hiddenPosint[nuggetpos])){
                    result+= 5;
                    System.out.println("HEY This card connects the dots");
                }
            }
        }
        return(result);
    }
    /*
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
        }*/

    public static SBoardstateC checkHiddenupdate(SBoardstateC boardState){
        SaboteurTile[][] boardtiles=boardState.board;

        int nonhiddencount = 0;
        int falseindex = -1;
        boolean[] phiddenRevealed;
        if(boardState.getTurnNumber()==1){
            phiddenRevealed=boardState.player1hiddenRevealed;
        }else{
            phiddenRevealed=boardState.player2hiddenRevealed;
        }

        if(boardState.nuggetpos==-1) {
            for (int i = 0; i < phiddenRevealed.length; i++) {
                if (phiddenRevealed[i] == true) {
                    nonhiddencount += 1;
                } else {
                    falseindex = i;
                }
            }
            if (nonhiddencount == 2) {
                System.out.println("Great. We know where it is at turn: "+boardState.getTurnNumber());
                nuggetpos = falseindex;
                boardState.nuggetpos=falseindex;
                if(boardState.getTurnPlayer()==1) {
                    boardState.player1hiddenRevealed[falseindex] = true;
                    //boardState.board[hiddenPos[nuggetpos][0]][hiddenPos[nuggetpos][1]];
                    //boardState.hiddenRevealed[falseindex] = true;
                }else{
                    boardState.player2hiddenRevealed[falseindex] = true;
                    //boardState.hiddenRevealed[falseindex] = true;
                }
            }
        }

        return boardState;
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
                    }
                }
            }
        }

        //Return remaining possible moves. Feed to getalllegal.
        return compo;
    }
    public static double minimax(int currentdepth, int maxdepth, SBoardstateC boardState, boolean maxer, double maxerscore){
        //System.out.println("Current Depth: " + currentdepth);
/*
        if isMaximizingPlayer :
        bestVal = -INFINITY
        for each move in board :
        value = minimax(board, depth+1, false)
        bestVal = max( bestVal, value)
        return bestVal

*/      double score = evaluate2(boardState);
        if((score+maxerscore)>= (boardState.turnNumber/3) + 4){
            return(score+maxerscore);
        }

        if(currentdepth == maxdepth)
            return score+maxerscore;


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
                    SBoardstateC newboard = makeMove(played,boardState,false);
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
                    SBoardstateC newboard = makeMove(played,boardState,false);

                    double res = 0;
                    res = minimax((currentdepth + 1), maxdepth, newboard, true, maxerscore);
                    if(res>=100){
                        System.out.println("YOU HELPED HIM WIN WTF");
                        return(-1000);
                    }
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

    public static boolean isMalused(SBoardstateC board){
       return(board.getNbMalus(board.turnPlayer)>0);
    }

    public static boolean activeSabotager(SBoardstateC board){
        ArrayList<SaboteurCard> possible_actions = board.getCurrentPlayerCards();
        double sabotagingcards = 0;
        double buildingcards = 0;
        double prospectingcards = 0;
        Map<String,Integer> deckcomp = board.compo;
        for (int i =0;i < possible_actions.size();i++){
            SaboteurCard card = possible_actions.get(i);
            if(checkConnected(card)==true||card.getName().equals("Bonus")||card.getName().equals("Malus")){
                buildingcards +=1;
            }else if (card.getName().equals("Destroy")||card.getName().equals("Malus")||card.getName().equals("Tile")){
                sabotagingcards+=1;
            }else{
                sabotagingcards+=1;
            }
        }
        System.out.println("Sabotage Ratio: " + sabotagingcards +"/"+buildingcards);
        return (sabotagingcards>buildingcards);
    }
    public static double saveCardsStrategy(SBoardstateC board){
        double result = 0;            // discourages the bot to play things that connects left and right
        // early on in the game
        int turn = board.getTurnNumber();
        if(sideWayConnect(board.lastplayed.getCardPlayed())){
            result = 0.002*turn*turn-1;
            // this is a very slow  growing exponential function
            // starts from -1 when x=0 and reaches 0 when x=10
        }
        System.out.println("saving card");
        return result;
    }
    public static boolean sideWayConnect(SaboteurCard c){
        if(c.getName().contains("Tile")){
            if(checkConnected(c)) {
                SaboteurTile t = (SaboteurTile) c;
                if(t.getPath()[0][1]==1 && t.getPath()[2][1]==1) return true;
            }
        }
        return false;
    }
    public static boolean isSabotagingMove(SaboteurMove played){
        if(     played.getCardPlayed().getName().equals("Destroy") ||
                played.getCardPlayed().getName().equals("Malus")   ||
                !checkConnected(played.getCardPlayed())){
            return true;
        }
        return false;
    }
    static SaboteurMove findBestMove(int maxdepth, SBoardstateC boardState,ArrayList<SaboteurMove> possible_actions2 )
    {
        boardState=updateRevealHistory(boardState);
        double bestVal = -1000;
        double worstVal = 1000;

        if(boardState.nuggetpos==-1){
            System.out.println("NOT FOUND");
        }
        boardState=checkHiddenupdate(boardState);
        if(boardState.nuggetpos!=-1){
            System.out.println("FOUND");
        }
        System.out.println("Turn number:  "+boardState.getTurnNumber() +" hidden tiles: "+ boardState.player1hiddenRevealed[0] + boardState.player1hiddenRevealed[1] + boardState.player1hiddenRevealed[1]);

        ArrayList<SaboteurMove> possible_actions = boardState.getAllLegalMoves();
        if(possible_actions.size()==possible_actions2.size()){
            System.out.println("In Business");
        }else{
            System.out.println("Uh oh");
            System.out.println("MOVES FOR REAL:");
            /*
            for (int i =0 ; i < possible_actions2.size();i++){
                System.out.println("Actual:" + possible_actions2.get(i).toPrettyString() + " OURS:" + possible_actions.get(i).toPrettyString());
            }*/
            possible_actions=possible_actions2;
        }


        //Update the board with nugget position
        SaboteurMove bestMove = boardState.getRandomMove();
        SaboteurMove worstMove = boardState.getRandomMove();

        boolean sabotage = false;

        if(activeSabotager(boardState)){
            System.out.println("Sabotager!");
            sabotage= true;
        }

        SBoardstateC pboard = new SBoardstateC(boardState);
        for (int i = 0; i < possible_actions.size(); i++) {
            double moveVal=0;
            //Play bonus card every time malused
            SaboteurMove played = possible_actions.get(i);
            int[] pos = played.getPosPlayed();
            if(isMalused(boardState) && played.getCardPlayed().getName().equals("Bonus")){
                return played;
            }
            //Get it to always play map card if unknown
            if(played.getCardPlayed().getName().equals("Map")){
                boolean[] revealed=boardState.returnRevealed();
                if(revealed[0]!=true && pos[1] == 3||
                revealed[1]!=true && pos[1]==5 || revealed[2]!=true && pos[1]==7){
                    System.out.println("Never Played this card before");
                    if(boardState.nuggetpos==-1){
                        return(played);
                    }
                    System.out.println("Not playing since we know where it is");
                }
            }
            if(sabotage==true){
                if(isSabotagingMove(played)){
                    moveVal+=5;
                    System.out.println("Sabotaging move");
                }
            }
            SBoardstateC newboard = makeMove(played, boardState, false);
            //if (boardState.isLegal(played)) {
            moveVal+= evaluate2(newboard);//minimax(0, maxdepth, newboard, true, 0);
            System.out.println("MOVE VALUE: " +played.toPrettyString()+" " +moveVal);
            // If the value of the current move is
            // more than the best value, then update
            // best
            if (moveVal > bestVal) {
                bestMove = played;
                bestVal = moveVal;
                pboard = newboard;
            }
            if (moveVal < worstVal) {
                worstMove = played;
                worstVal = moveVal;
                pboard = newboard;
                }

            //}

        }
        System.out.println("Playing simulation from first move: " +bestMove.toPrettyString());
        System.out.println("The resulting evaluation: " + evaluate2(pboard));
        printBoard(pboard.board);
        /*
        if(bestVal < (boardState.getTurnNumber()/5)){
            bestMove=SaboteurDrop();
        }*/
        return bestMove;
    }

    public static SBoardstateC makeMove(SaboteurMove played,SBoardstateC boardState,boolean draw){
        SBoardstateC newboard = new SBoardstateC(boardState);
        boolean play = true;

        newboard=checkHiddenupdate(newboard);
        newboard.processMove(played,false);
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

