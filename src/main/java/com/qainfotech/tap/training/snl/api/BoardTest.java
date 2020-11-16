package com.qainfotech.tap.training.snl.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.testng.annotations.Test;

public class BoardTest {
	
	 @Test
	    public void intializeboard() throws FileNotFoundException, UnsupportedEncodingException, 
	    IOException {
	       
	        Board board = new Board();
	        assertTrue(Files.exists(Paths.get(board.getUUID() + ".board")));}
	
	 @Test
	    public void add_new_player() throws FileNotFoundException, UnsupportedEncodingException,
	    IOException, PlayerExistsException,
	    GameInProgressException,
	    MaxPlayersReachedExeption {
	        
	        Board board = new Board();
	        UUID rishabhUUID = board.registerPlayer("rishabh");
	        String name = board.getData().getJSONArray("players").getJSONObject(0).getString("name");
	        assertEquals(name, "rishabh");
	        System.out.println("Player added successfully");}
	 
	 @Test
     public void roll_a_dice() throws FileNotFoundException,
     UnsupportedEncodingException,
     IOException, PlayerExistsException,
     GameInProgressException, 
     MaxPlayersReachedExeption, InvalidTurnException {
         
         Board board = new Board();
         
         //Adding a Player
         UUID rishabhUUID = board.registerPlayer("rishabh");
         
         int currentPos = board.getData().getJSONArray("players").getJSONObject(0).getInt("position");
         board.rollDice(rishabhUUID);
         
         int newPos = board.getData().getJSONArray("players").getJSONObject(0).getInt("position");
         assertNotEquals(currentPos,newPos);}
	 
	 @Test
     public void player_climb_a_ladder() throws FileNotFoundException, 
     UnsupportedEncodingException,
     IOException, PlayerExistsException,
     GameInProgressException,
     MaxPlayersReachedExeption, InvalidTurnException {
         
         Board board = new Board();
         
         //Adding a Player
         UUID rishabhUUID = board.registerPlayer("rishabh");
         
         board.rollDice(rishabhUUID);
         
         int pos = board.getData().getJSONArray("players").getJSONObject(0).getInt("position");
         int step = board.getData().getJSONArray("steps").getJSONObject(pos).getInt("type");
         if(step==2)
             System.out.println("player has climbed a ladder");
         else
             System.out.println("player has not climbed a ladder");
         }
         
	 @Test
     public void delete_a_player() throws FileNotFoundException,
     UnsupportedEncodingException,
     IOException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption, NoUserWithSuchUUIDException {
         
         Board board = new Board();
         
         //Adding a Player
         UUID rishabhUUID = board.registerPlayer("rishabh");
         
         board.deletePlayer(rishabhUUID);
         
         System.out.println("Player deleted successfully");
         }
         
     
	 

}
