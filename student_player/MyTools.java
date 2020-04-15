package student_player;

import Saboteur.*;
import Saboteur.cardClasses.*;
//import student_player.MyTools.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;


public class MyTools {
    //public static final int DEFAULT_TIMEOUT = 1500;//20000
    //private static final int DEFAULT_TIMEOUT_CUSHION = 1000;
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
    public static boolean playMalus=false;
    public static ArrayList<SaboteurMove> legalmoves;
    public static boolean[] hiddenRevealedhist={false,false,false};
    public static boolean[] hiddenRevealed = {false,false,false};
    public static int playerturn;
    public static boolean connectTwohidden=false;
    public static boolean[] hidden;
    public static boolean globalsabotage =false;
    public static SBoardstateC originalboard;
    public static SaboteurMove oppwinningmove=null;
    public static boolean opponentMalused=false;
    public static int connectedcardcount=0;
    public static int unconnectedcardindex=-1;
    public static Map<String,Double> scores=new HashMap<String,Double>();

    //public static int nuggetpos=-1;

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
        Set<String> compostring = compo.keySet();
        System.out.println(compostring);
        for (int i = 0; i < tiles.length; i++) {
            if(compostring.contains(tiles[i])) {
                for (int j = 0; j < compo.get(tiles[i]); j++) {
                    deck.add(new SaboteurTile(tiles[i]));
                }
            }
        }
        if(compostring.contains("destroy")) {
            for (int i = 0; i < compo.get("destroy"); i++) {
                deck.add(new SaboteurDestroy());
            }
        }
        if(compostring.contains("malus")) {
            for (int i = 0; i < compo.get("malus"); i++) {
                deck.add(new SaboteurMalus());
            }
        }
        if(compostring.contains("bonus")) {
            for (int i = 0; i < compo.get("bonus"); i++) {
                deck.add(new SaboteurBonus());
            }
        }
        if(compostring.contains("map")) {
            for (int i = 0; i < compo.get("map"); i++) {
                deck.add(new SaboteurMap());
            }
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
        if(board.lastplayed.getCardPlayed().equals("Malus")){
            System.out.println("Hello");
        }
        if(board.lastplayed.getCardPlayed() instanceof SaboteurDrop){
            return dropStrategy(board);
        }
        if(board.lastplayed.getCardPlayed() instanceof SaboteurMap) {
            return evalmapStrategy(board);
        }

        if(board.lastplayed.getCardPlayed().getName().equals("Destroy")) {
            return destroyStrategy(board);//+maximumPathStrategy(board);
        }
        double result = 20.0;

        if(board.nuggetpos!=-1){
            System.out.println("We know where the nugget is: "+board.nuggetpos);
            board.hiddenCards[board.nuggetpos].getIdx().equals("nugget");
            result= result/euclideanDistance(board.lastplayedpos,hiddenPos[board.nuggetpos]);

            //check winning condition
            if (pathToMeplaced(board.intBoard,originint,
                    new int[]{(hiddenPos[board.nuggetpos][0] * 3 + 1), (hiddenPos[board.nuggetpos][1] * 3 + 1)})){
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

        /*
        if(board.turnNumber > 10 && board.turnNumber < 45) {
            result += connectingTwoPaths(board);

            result += stalemateStrategy(board);

            result += outlastStrategy(board);
        }
*/
        //result += stalemateStrategy(board);

        //result += outlastStrategy(board);
        SaboteurMove played = board.lastplayed;
        result += checkConnectTwoHidden(board);
        if(result <-1000){
            System.out.println("Heey");
        }
        result += devalueUpcards(board);
        if(result <-1000){
            System.out.println("Heey");
        }

        result += MyTools.valueConnected(board);
        if(result <-1000){
            System.out.println("Heey");
        }
        result += MyTools.countHiddenStrategy(board)*200;
        if(result <-1000){
            System.out.println("Heey");
        }
        result += saveCardsStrategy(board)*5;
        if(result <-1000){
            System.out.println("Heey");
        }
        result += safeGuardStrategy(board);
        if(result <-1000){
            System.out.println("Heey");
        }
        result += helpWinStrategy(board);
        if(result <-1000){
            System.out.println("Heey");
        }
        result += pathStrategy(board);

        if(result <-1000){
            System.out.println("Heey");
        }
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



        // safe guard. weight down the moves that may allow opponents to connect to a hidden tile
        // this seems to stop working after we connect to a hidden tile
        // I might need to add some extra logic somewhere
        // maybe:
        // play slightly different strategies based on the which tiles are revealed?



        if(result< 0){
            System.out.println("WHAT");
        }

        //Detroy tiles that disconnect

        return(result);
    }
    public static double checkConnectTwoHidden(SBoardstateC boardState){
        if(boardState.getTurnPlayer()==1) {
            hidden = boardState.player1hiddenRevealed;
        }
        else{
            hidden = boardState.player2hiddenRevealed;
        }
        if(boardState.nuggetpos == -1) {
            if (hidden[0] == false && hidden[1] == true || hidden[0] == true && hidden[1] == false || hidden[0] == false && hidden[1] == false) {
                if (boardState.lastplayed.getCardPlayed() instanceof SaboteurTile) {
                    int[][] path = ((SaboteurTile) boardState.lastplayed.getCardPlayed()).getPath();
                    int[] pos = boardState.lastplayed.getPosPlayed();
                    boolean sideway = sideWayConnect(boardState.lastplayed.getCardPlayed());
                    if (pos[0] == 12 && pos[1] == 4 && sideWayConnect(boardState.lastplayed.getCardPlayed())) {
                        return 600;
                    }
                }
            }
            if (hidden[1] == false && hidden[2] == true||hidden[1] == true && hidden[2] == false||hidden[1] == false && hidden[2] == false) {
                if (boardState.lastplayed.getCardPlayed() instanceof SaboteurTile) {
                    int[][] path = ((SaboteurTile) boardState.lastplayed.getCardPlayed()).getPath();
                    int[] pos = boardState.lastplayed.getPosPlayed();
                    boolean sideway = sideWayConnect(boardState.lastplayed.getCardPlayed());
                    if (pos[0] == 12 && pos[1] == 4 && sideWayConnect(boardState.lastplayed.getCardPlayed())) {
                        return 600;
                    }
                }
            }
        }
        return 0;
    }
    public static double evalmapStrategy(SBoardstateC boardState){
        SaboteurMove played = boardState.lastplayed;

        int[] pos = played.getPosPlayed();
        if(boardState.nuggetpos==-1) {
            if (played.getCardPlayed() instanceof SaboteurMap) {

                boolean[] revealed;
                if (boardState.getTurnPlayer() == 1) {
                    revealed = boardState.player2hiddenRevealed;
                } else {
                    revealed = boardState.player1hiddenRevealed;
                }
                if (revealed[0] != true && pos[1] == 3 ||
                        revealed[1] != true && pos[1] == 5 || revealed[2] != true && pos[1] == 7) {
                    System.out.println("Never Played this card before");

                    return 200;

                }
            }
        }
        return 0;
    }
    public static double safeGuardStrategy(SBoardstateC board) {
        double result = 0;
        SaboteurMove move = board.lastplayed;
        int opponent = board.getTurnPlayer();
        SaboteurMove lastplayed = board.lastplayed;

        //goal unknown
        if(board.nuggetpos==-1){

            //if opponent is not malused
            if (!(board.getNbMalus(opponent) > 0)) {
                if (euclideanDistance(board.lastplayedpos, hiddenPos[0]) == 2.0 ||
                        euclideanDistance(board.lastplayedpos, hiddenPos[1]) == 2.0 ||
                        euclideanDistance(board.lastplayedpos, hiddenPos[2]) == 2.0 ||
                        euclideanDistance(board.lastplayedpos, hiddenPos[0]) == Math.sqrt(2) ||
                        euclideanDistance(board.lastplayedpos, hiddenPos[1]) == Math.sqrt(2) ||
                        euclideanDistance(board.lastplayedpos, hiddenPos[2]) == Math.sqrt(2)) {
                    int[] pos = board.lastplayedpos;
                    boolean path = pathToMeplaced(board.getHiddenIntBoard_old(), new int[]{pos[0] * 3 + 1, pos[1] * 3 + 1}, originint);
                    if (path) {
                        if(board.turnNumber < 5){
                            System.out.println("HEY?!??!");
                        }
                        playMalus = true;
                        result -= 1000;

                    }
                }
            }
        }else{
            if(     euclideanDistance(board.lastplayedpos,hiddenPos[board.nuggetpos])==2 ||
                    euclideanDistance(board.lastplayedpos,hiddenPos[board.nuggetpos])==Math.sqrt(2)){
                if(board.turnNumber < 5){
                    System.out.println("HEY?!??!");
                }
                playMalus=true;
                if(board.getNbMalus(opponent)<1){
                    result-=1000;
                }
            }
            // we cant connect to all three tiles
            if(countHiddenStrategy(board)<3 &&countHiddenStrategy(board)>0 ){
                System.out.println("111111");
                // we cant connect to nugget
                if(!pathToMeplaced(board.intBoard, originint, hiddenPosint[board.nuggetpos])){
                    System.out.println("222222");
                    for(int i=0;i<3;i++){
                        // wer not at nugget position, opponent isnt malused
                        if(     i!=board.nuggetpos &&
                                board.getNbMalus(opponent)<1){

                            System.out.println("333333");

                            result-=1000;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static double devalueUpcards(SBoardstateC board){
        SaboteurMove played = board.lastplayed;
        int[] pos = played.getPosPlayed();
        if(!globalsabotage){
            if(played.getCardPlayed() instanceof SaboteurTile) {
                if(((SaboteurTile) played.getCardPlayed()).getIdx().equals("5_flip")) {
                    return -10;
                }
            }
        }
        return 0;
    }
    //Value cards being played near the bottom.
    public static double bottomStrategy(SBoardstateC board){
        double result =0;
        if((board.lastplayedpos[0] < 5)){
            result-=10;
            if(!checkConnected(board.lastplayed.getCardPlayed())){
                result+=10;
            }
            return result;
        }
        return 0;

    }
    public static double dropStrategy(SBoardstateC board){
        SaboteurMove played = board.lastplayed;
        SBoardstateC clone = new SBoardstateC(board);
        int[] pos = played.getPosPlayed();
        ArrayList<SaboteurCard> hand;
        ArrayList<SaboteurCard> opponentshand = board.getCurrentPlayerCards();
        if(board.turnPlayer == 1) {
            hand = board.player2Cards;
        }else{
            hand = board.player1Cards;
        }
        if(played.getCardPlayed() instanceof SaboteurDrop){
            SaboteurCard dropped = board.dropped;
            if (dropped instanceof SaboteurTile) {
                if (!checkConnected((SaboteurTile) dropped)) {
                    return 1;
                }
            }

        }
        return 0;
    }


    public static double considerOpponentStrategy(SBoardstateC board){
        SaboteurMove played = board.lastplayed;
        SBoardstateC clone = new SBoardstateC(board);
        int[] pos = played.getPosPlayed();
        ArrayList<SaboteurCard> hand;
        ArrayList<SaboteurCard> opponentshand = board.getCurrentPlayerCards();
        if(board.turnPlayer == 1) {
            hand = board.player2Cards;
        }else{
            hand = board.player1Cards;
        }
        //consider the chance of them winning

        return 0;
    }
    public static double destroyStrategy(SBoardstateC board){
        SaboteurMove played = board.lastplayed;
        SaboteurCard destroyed = board.destroyed;
        SBoardstateC clone = new SBoardstateC(board);
        ArrayList<SaboteurMove> moves = board.getAllLegalMoves(1-board.turnPlayer);
        int[] pos = played.getPosPlayed();
        int[] intpos = new int[]{pos[0]*3+1,pos[1]*3+1};
        ArrayList<SaboteurCard> hand;
        if(board.turnPlayer == 1) {
            hand = board.player2Cards;
        }else{
            hand = board.player1Cards;
        }
        double result=0;
/*
        if(!outlastStrategy(board)){
            return 0;
        }*/
        if(board.getNbMalus(1-board.getTurnPlayer())>0){
            destroyed=board.destroyed;
            return euclideanDistance(origin,pos);
        }
        if(globalsabotage){
            if(pathToMeplaced(originalboard.getHiddenIntBoard(),originint,intpos)){
                if(!pathToMeplaced(board.getHiddenIntBoard(),originint,intpos)){
                    result+=100;
                }
            }
            return(result+euclideanDistance(origin,pos));
        }
        //If destroyed a disconnect card, and we can do better.
        if(!checkConnected(destroyed)){
            for (int i = 0; i < moves.size();i++){
                int[] handpos=moves.get(i).getPosPlayed();
                if(handpos[0]==pos[0] && handpos[1]==pos[1]){
                    SBoardstateC cloneboard = new SBoardstateC(originalboard);
                    cloneboard.processMove(moves.get(i),false);
                    if(pathToMeplaced(cloneboard.getHiddenIntBoard(),originint,intpos)){
                        return 50 + euclideanDistance(origin,pos);
                    }
                }
            }
        }


            /*
            if(checkConnected(destroyed)){
                result=-20;
                result+=euclideanDistance(origin,pos);
            }else{
                result+=10;
                for (int i = 0; i < moves.size();i++){
                    ArrayList<SaboteurMove> opponent= board.getAllLegalMovesDeck2();
                    board.processMove(moves.get(i),false);
                    if(outlastStrategy(board)){
                        result+=10;
                    }
                }

                System.out.println("GOOD Destroy");
            }*/


        return result;

    }
    public static boolean checkPlayingNearnugget(SBoardstateC board){
        if (board.nuggetpos == 0) {
            if (pathToMeplaced(board.getHiddenIntBoard(), hiddenPosint[board.nuggetpos + 1], originint)) {
                return true;
            }
        }
        if (board.nuggetpos == 1) {
            if (pathToMeplaced(board.getHiddenIntBoard(), hiddenPosint[board.nuggetpos + 1], originint)) {
                return true;
            }
            if (pathToMeplaced(board.getHiddenIntBoard(), hiddenPosint[board.nuggetpos - 1], originint)) {
                return true;
            }
        }
        if (board.nuggetpos == 2) {
            if (pathToMeplaced(board.getHiddenIntBoard(), hiddenPosint[board.nuggetpos - 1], originint)) {
                return true;
            }
        }

        return false;
    }
    public static double helpWinStrategy(SBoardstateC board){
        //If we are playing near nugget and the opponent is not malused.
        if(checkPlayingNearnugget(board) && !opponentMalused){
            return -1000;
        }
        return 0;
    }
    public static boolean checkConnected(SaboteurCard card){
        if(tileCard(card)) {
            return(checkConnected((SaboteurTile)card));
        }
        return false;

    }
    public static boolean checkConnected(SaboteurTile card){
        int[][] path= ((SaboteurTile)card).getPath();
        String name = card.getIdx();
        if(path[1][1]==1) {
            //horizontal line,
            if (name.equals("0")||name.equals("5")||name.equals("5_flip")||name.equals("6")||name.equals("6_flip")||
                    name.equals("7")||name.equals("7_flip")||name.equals("8")||name.equals("9")||
                    name.equals("9_flip")||name.equals("10")
            ){
                return true;
            }
        }
        return false;

    }
    public static double valueConnected(SBoardstateC board){
        SaboteurCard card=board.lastplayed.getCardPlayed();
        if(tileCard(card)){
            int[][] path= ((SaboteurTile)card).getPath();
            if(checkConnected(card)){
                if(board.lastplayed.getPosPlayed()[0]>5) {
                    return 20;
                }
            }else{
                if(opponentMalused && board.lastplayed.getPosPlayed()[0]>8 && connectedcardcount>2){
                    return -10;
                }
            }

        }
        return 0;
    }
    public static boolean tileCard(SaboteurCard card){
        if(card instanceof SaboteurTile){
            return true;
        }
        if(card.getName() != null) {
            return (card.getName().contains("Tile"));
        }

        return false;
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
    public static double pathStrategy(SBoardstateC board){
        double result = 0;
        if(pathToMeplaced(board.intBoard,originint,new int[]{board.lastplayedpos[0]*3+1,board.lastplayedpos[1]*3+1})) {
            if (board.lastplayed.getPosPlayed()[0] > 5) {
                System.out.println("Path exists");
                result = board.lastplayed.getPosPlayed()[0]*.5;
            }
        }
        return result;
    }
    //Value cards that increase the max path.
    public static double maximumPathStrategy(SBoardstateC board){
        //if a max path wouldn't exist without this move, give it a bonus
        int[] maxpath = board.maxpath;

        double previousmax = euclideanDistance(origin,maxpath);
        board.longestpath();
        int[] newmaxpath = board.maxpath;

        if(newmaxpath[0]!= maxpath[0] || newmaxpath[1]!= maxpath[1] ) {
            double currentmax = euclideanDistance(origin, board.maxpath);
            if(currentmax>previousmax){
                return 5;
            }
        }

        return 0;
    }
    public static double valueBackwards(SBoardstateC board){
        double result = 0;
        if(board.nuggetpos != -1){
            SaboteurCard cardPlayed = board.lastplayed.getCardPlayed();
            if(cardPlayed.getName().contains("Tile")){
                if(pathToMeplaced(board.getHiddenIntBoard(),board.lastplayedpos,hiddenPosint[board.nuggetpos])){
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
                    boardState.hiddenRevealed[i]=true;
                    nonhiddencount += 1;
                } else {
                    falseindex = i;

                }
            }
            if (nonhiddencount == 2) {
                System.out.println("Great. We know where it is at turn: "+boardState.getTurnNumber());
                boardState.nuggetpos = falseindex;
                boardState.nuggetpos = falseindex;
                if(boardState.getTurnPlayer()==1) {
                    boardState.player1hiddenRevealed[falseindex] = true;
                    boardState.hiddenRevealed[falseindex]=true;
                    //boardState.board[hiddenPos[nuggetpos][0]][hiddenPos[nuggetpos][1]];
                    //boardState.hiddenRevealed[falseindex] = true;
                }else{
                    boardState.player2hiddenRevealed[falseindex] = true;
                    boardState.hiddenRevealed[falseindex]=true;
                    //boardState.hiddenRevealed[falseindex] = true;
                }
                boardState.hiddenCards[falseindex]=new SaboteurTile("nugget");
            }else{

            }
        }else{
            boardState.hiddenRevealed[boardState.nuggetpos]=true;
        }

        return boardState;
    }
    public static Map<String,Integer> updateDeck(Map<String,Integer> compoog, SaboteurTile[][] boardtiles,ArrayList<SaboteurCard> hand){
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
        for (int i = 0; i < hand.size();i++){
            String id = hand.get(i).getName();
            id = id.split("_")[0];
            //System.out.println("UPDATING: " + id);
            if(compoog.get(id)!=null) {
                compo.put(id, compoog.get(id) - 1);
            }else{
                break;
            }
        }

        //Return remaining possible moves. Feed to getalllegal.
        return compo;
    }
    public static boolean outlastStrategy(SBoardstateC boardState){
        //Sabotage and outlast the other player. Considering the remaining moves, ie. tiles
        /*
        if(boardState.lastplayed.getPosPlayed()[0]==11){
            if(boardState.lastplayed.getPosPlayed()[1]==3){
                String name =boardState.lastplayed.getCardPlayed().getName();
                ifname.equals("0")||name.equals("5")||name.equals("5_flip")||name.equals("6")||name.equals("6_flip")||
                        name.equals("7")||name.equals("7_flip")||name.equals("8")||name.equals("9")
            }
        }
        if(boardState.lastplayed.getPosPlayed()[0]==12){
            if(boardState.lastplayed.getPosPlayed()[1]==4){

            }

        }
        if(boardState.lastplayed.getPosPlayed()[0]==13){

        }*/

    // check game saving, ie if i can destroy, destroy.
        SaboteurMove lastplayed = boardState.lastplayed;
        if(boardState.turnNumber >= 10 && !opponentMalused){
            if(boardState.nuggetpos==-1) {
                if (boardState.hiddenRevealed[0] == false && boardState.hiddenRevealed[1] == false &&
                        boardState.hiddenRevealed[2] == false) {
                    boardState.hiddenCards[0]=new SaboteurTile("nugget");
                    boardState.hiddenCards[1]=new SaboteurTile("nugget");
                    boardState.hiddenCards[2]=new SaboteurTile("nugget");
                }
                if (boardState.hiddenRevealed[0] == false && boardState.hiddenRevealed[1] == false &&
                        boardState.hiddenRevealed[2] == true) {
                    boardState.hiddenCards[0]=new SaboteurTile("nugget");
                    boardState.hiddenCards[1]=new SaboteurTile("nugget");
                }
                if (boardState.hiddenRevealed[0] == false && boardState.hiddenRevealed[1] == true &&
                        boardState.hiddenRevealed[2] == false) {
                    boardState.hiddenCards[0]=new SaboteurTile("nugget");
                    boardState.hiddenCards[2]=new SaboteurTile("nugget");
                }
                if (boardState.hiddenRevealed[0] == true && boardState.hiddenRevealed[1] == false &&
                        boardState.hiddenRevealed[2] == false) {
                    boardState.hiddenCards[1]=new SaboteurTile("nugget");
                    boardState.hiddenCards[2]=new SaboteurTile("nugget");
                }
            }else{
                boardState.hiddenCards[boardState.nuggetpos]=new SaboteurTile("nugget");
            }
            ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMovesDeck2();
           for(int i = 0; i < legalMoves.size();i++){
               SBoardstateC clone =new SBoardstateC(boardState);
               //this.hiddenCards[i].getIdx().equals("nugget")
               clone.processMove(legalMoves.get(i),false);
               int winner = clone.getWinner();
               //If this move causes the opponet to win return false
               if(winner==(1-playerturn)){
                   System.out.println(winner);
                   oppwinningmove=legalMoves.get(i);
                   return false;
               }
           }

        }

        return true;
    }

    public static double stalemateStrategy(SBoardstateC boardState){
        double bonus = 0;
        double constant = 30;
        double constant2=10;
        SaboteurMove move = boardState.lastplayed;
        int[] pos = move.getPosPlayed();
        if(boardState.stalemate){
            if(checkConnected(boardState.lastplayed.getCardPlayed())){
                constant = -5*(constant/(double)boardState.turnNumber);
                bonus = bonus+constant;
            }else{
                bonus+=30;
                constant2=constant2/euclideanDistance(move.getPosPlayed(),origin);
                bonus+=constant2;
            }
            if(pos[0]==11 || pos[0]==12 && pos[1]==2||pos[0]==12 && pos[1]==8||
                    pos[0]==12 && pos[1]==4||pos[0]==12 && pos[1]==6||pos[0]==13 && pos[1]==3||
                    pos[0]==13 && pos[1]==5||pos[0]==13 && pos[1]==7){
                bonus=0;
            }

        }
        return bonus;
    }
    public static boolean checkPointingdown(SaboteurCard card){
        if(card instanceof SaboteurTile){
           int path[][] = ((SaboteurTile) card).getPath();
           if(path[1][1]==1 && path[0][1]==1 && path[2][1]==1){
               return true;
           }
        }
        return false;
    }
    public static double connectingTwoPaths(SBoardstateC boardState){
        SaboteurMove move = boardState.lastplayed;
        SaboteurTile[][] tiles = boardState.getHiddenBoard();
        double count = 0;
        int[] pos = move.getPosPlayed();
        int[] newposabove= new int[]{pos[0]-1,pos[1]};
        int[] newposbelow= new int[]{pos[0]+1,pos[1]};
        int[] newposleft= new int[]{pos[0],pos[1]-1};
        int[] newposright= new int[]{pos[0],pos[1]+1};
        if(newposabove[0]>0) {
            if (tiles[newposabove[0]][newposabove[1]] != null){
                count += 1;
            }
        }
        if(tiles[newposbelow[0]][newposbelow[1]] != null){
            count+=1;
        }
        if(tiles[newposright[0]][newposright[1]] != null){
            count+=1;
        }
        if(newposleft[1] >0){
            if(tiles[newposleft[0]][newposleft[1]] != null) {
                count += 1;
            }
        }
        return (count*5);
    }


    public static boolean isMalused(SBoardstateC board){
        opponentMalused = board.getNbMalus(board.turnPlayer)>0;
       return(board.getNbMalus(board.turnPlayer)>0);
    }

    public static boolean activeSabotager(SBoardstateC board){
        ArrayList<SaboteurCard> possible_actions = board.getCurrentPlayerCards();
        double sabotagingcards = 0;
        double buildingcards = 0;
        for (int i =0;i < possible_actions.size();i++){
            SaboteurCard card = possible_actions.get(i);

            if(!card.getName().equals("5_flip")||checkConnected(card)==true||card.getName().equals("Bonus")||card.getName().equals("Malus")){
                buildingcards +=1;

            }else if (card.getName().equals("5_flip")||card.getName().equals("Destroy")||card.getName().equals("Malus")||!checkConnected(card)){
                sabotagingcards+=1;
            }
        }
        System.out.println("Sabotage Ratio: " + sabotagingcards +"/"+buildingcards);
        return (sabotagingcards>(buildingcards+6));
    }
    public static void discconncount(ArrayList<SaboteurCard> possible_actions){
        for (int i =0;i < possible_actions.size();i++){
            SaboteurCard card = possible_actions.get(i);

            if(checkConnected(card)==true){
                connectedcardcount+=1;

            }else{

                unconnectedcardindex=i;

            }
        }
    }
    public static double saveCardsStrategy(SBoardstateC board){
        double result = 0;            // discourages the bot to play things that connects left and right
        // early on in the game
        int turn = board.getTurnNumber();
        if(sideWayConnect(board.lastplayed.getCardPlayed())&&!opponentMalused&&turn <= 20){
            result = 0.002*turn*turn-1;
            if(result <0){
                System.out.println("Why negative");
            }
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
                boolean oldsideways=t.getPath()[0][1]==1 && t.getPath()[2][1]==1;
                boolean sideways=t.getPath()[0][1]==1 && t.getPath()[1][1]==1 && t.getPath()[2][1]==1;
                if(sideways!=oldsideways){
                    sideways=oldsideways;
                }
                if(sideways) return true;
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
        scores =  new HashMap<String,Double>();
        discconncount(boardState.getCurrentPlayerCards());
        hidden = boardState.hiddenRevealed;
        originalboard = boardState;
        boardState=updateRevealHistory(boardState);
        playerturn=boardState.getTurnPlayer();
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

        ArrayList<SaboteurMove> possible_actions = possible_actions2;
        legalmoves=possible_actions2;



        //Update the board with nugget position
        SaboteurMove bestMove = boardState.getRandomMove();
        SaboteurMove worstMove = boardState.getRandomMove();

        boolean sabotage = false;
        boolean failedtoplaymap=false;
        //Testing
        /*
        if(activeSabotager(boardState)||globalsabotage==true){
            System.out.println("Sabotager!");
            sabotage= true;
            globalsabotage=true;
        }*/
        //check for winning

        if(boardState.nuggetpos!=-1) {
            for (int i = 0; i < possible_actions.size(); i++) {
                SaboteurMove played = possible_actions.get(i);
                if(tileCard(played.getCardPlayed())) {
                    SBoardstateC newboard = makeMove(played, boardState, false);
                    //scores.put(played.toPrettyString(),);
                    if (checkConnected(played.getCardPlayed())&&pathToMeplaced(newboard.intBoard, originint, hiddenPosint[boardState.nuggetpos])){
                        return played;
                    }
                }
            }
        }

        SBoardstateC pboard = new SBoardstateC(boardState);
        for (int i = 0; i < possible_actions.size(); i++) {

            double moveVal = 0;
            //Play bonus card every time malused
            SaboteurMove played = possible_actions.get(i);
            int[] pos = played.getPosPlayed();
            if (isMalused(boardState) && played.getCardPlayed().getName().equals("Bonus")) {
                return played;
            }
            //play malus card if don't have bonus
            else if(isMalused(boardState) && played.getCardPlayed().getName().equals("Malus")){
                return played;
            }
            if(playMalus) {
                if(boardState.turnNumber<5){
                    System.out.println("HEY");
                }
                for (SaboteurCard c:boardState.getCurrentPlayerCards()){
                    if(c.getName().equals("Malus")) {
                        System.out.println("playin malus");
                         return new SaboteurMove(c,0,0, boardState.turnPlayer);
                    }
                }
            }

            if (sabotage == true) {
                if (isSabotagingMove(played)) {
                    moveVal += 20;
                    System.out.println("Sabotaging move");
                }
            }
            SBoardstateC newboard = makeMove(played, boardState, false);
            //if (boardState.isLegal(played)) {
            moveVal += evaluate2(newboard);//minimax(0, maxdepth, newboard, true, 0);
            System.out.println("MOVE VALUE: " + played.toPrettyString() + " " + moveVal);
            // If the value of the current move is
            // more than the best value, then update
            // best
            scores.put(played.toPrettyString(),moveVal);

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
        if(bestMove.getCardPlayed() instanceof SaboteurDrop){
            System.out.println(scores);
        }
        if(bestMove.getCardPlayed().getName().equals("Malus")){
            System.out.println(scores);
        }
        /*
        if(!opponentMalused && bestVal < 100) {
            if (playMalus == true) {
                discconncount(boardState.getCurrentPlayerCards());
                if (!bestMove.getCardPlayed().getName().equals("Malus")) {
                    System.out.println("Can't play malus, a drop disconnect card instead");
                    if (connectedcardcount == 2 && unconnectedcardindex != -1) {
                        bestMove = new SaboteurMove(new SaboteurDrop(), unconnectedcardindex, 0, boardState.getTurnPlayer());
                    }
                }
            }
        }*/



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
        if(newboard.lastplayedpos == null){
            System.out.println("ERROR");
        }
        ArrayList<SaboteurCard> hand;
        if(boardState.getTurnPlayer()==0) {
            hand=boardState.player2Cards;
        }
        else{
            hand=boardState.player1Cards;
        }
        //newboard.compo=updateDeck(SaboteurCard.getDeckcomposition(),newboard.board,hand);
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
    public static ArrayList<SaboteurCard> getTopDeck(Map<String,Integer> compo,int no) {

        Map<String, Integer> sortedMap = sortByValue(compo);
        sortedMap = getTopCards(sortedMap,no);
        return(getDeckfromcompo(sortedMap));
    }
    public static ArrayList<SaboteurCard> getTopDeckone(Map<String,Integer> compo,int no) {

        Map<String, Integer> sortedMap = sortByValue(compo);
        sortedMap = getTopCards(sortedMap,no);
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            sortedMap.put(entry.getKey(),1);
        }
        return(getDeckfromcompo(sortedMap));
    }
    public static Map<String,Integer> getTopCards(Map<String, Integer> map,int nocards) {
        Map<String,Integer> newmap = new HashMap<String,Integer>();
        int count = 0;
        System.out.println("MAP ENTRY SET: " +map.entrySet());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if(count >= nocards){
                break;
            }

            if (!entry.getKey().equals("map")&&!entry.getKey().equals("destroy")&&!entry.getKey().equals("bonus")&&
                    (entry.getKey().equals("0")||entry.getKey().equals("5")||entry.getKey().equals("5_flip")||
                            entry.getKey().equals("6")||entry.getKey().equals("6_flip")||entry.getKey().equals("8")||
                            entry.getKey().equals("7")||entry.getKey().equals("7_flip")||entry.getKey().equals("9")||
                            entry.getKey().equals("9_flip")||entry.getKey().equals("10"))
            ) {
                System.out.println("Key : " + entry.getKey()
                        + " Value : " + entry.getValue());
                newmap.put(entry.getKey(), entry.getValue());
                count += 1;
            }

        }
        return newmap;
    }
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        Collections.reverse(list);
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
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

