
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
        ArrayList<SaboteurCard> hand = boardState.getCurrentPlayerCards();
        Map<String,Integer> compo = MyTools.updateDeck(SaboteurCard.getDeckcomposition(),boardState.getHiddenBoard());
        ArrayList<SaboteurCard> deck = MyTools.getDeckfromcompo(compo);

        SBoardstateC clone = new SBoardstateC(boardState,deck);
        clone = MyTools.checkHiddenupdate(clone);

        SaboteurMove myMove = MyTools.findBestMove(0, clone,boardState.getAllLegalMoves());
        System.out.println("Turn:"+boardState.getTurnPlayer());

        for(int i =0 ; i <boardState.getCurrentPlayerCards().size();i++){
            System.out.println("HAND:"+boardState.getCurrentPlayerCards().get(i).getName());
        }

        if(!boardState.isLegal(myMove)){
            System.out.println("Illegal move!");
            myMove = boardState.getRandomMove();
        }

        //SaboteurMove myMove = clone.getRandomMove();
        return myMove;
    }
}