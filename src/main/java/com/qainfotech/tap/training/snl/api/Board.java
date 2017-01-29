package com.qainfotech.tap.training.snl.api;

import java.util.UUID;
import java.util.Random;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author Ramandeep 
 */
public class Board {
    
    UUID uuid;
    JSONObject data;
    
    private JSONObject getStep(Integer number, Integer type, Integer target){
        return new JSONObject("{\"number\":"+number+",\"type\":"+type+", \"target\":"+target+"}");
    }

    private void initBoard(){
        JSONArray steps = new JSONArray();
        for(int position = 0; position <=100; position++){
            steps.put(position, getStep(position, 0, position));
        }
        // snakes
        steps.put(99, getStep(99, 1, 3));
        steps.put(93, getStep(93, 1, 67));
        steps.put(55, getStep(55, 1, 13));
        steps.put(70, getStep(70, 1, 32));
        steps.put(23, getStep(23, 1, 7));
        // ladders
        steps.put(2, getStep(2, 2, 24));
        steps.put(11, getStep(11, 2, 33));
        steps.put(25, getStep(25, 2, 85));
        steps.put(37, getStep(37, 2, 61));
        steps.put(68, getStep(68, 2, 90));
        steps.put(79, getStep(79, 2, 97));
        
        data = new JSONObject();
        data.put("players", new JSONArray());
        data.put("turn", 0);
        data.put("steps", steps);
    }
    
    private void saveBoard()
            throws FileNotFoundException, UnsupportedEncodingException{
        saveBoard(this.data);
    }
    private void saveBoard(JSONObject content)
            throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter writer = new PrintWriter(uuid.toString() + ".board", "UTF-8");
        writer.println(content.toString(2));
        writer.close();
    }
    
    /**
     * construct a new board
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public Board()
            throws FileNotFoundException, UnsupportedEncodingException{
        this.uuid = UUID.randomUUID();
        initBoard();
        saveBoard();
    }
    
    /**
     * adds new player to board
     * Conditions: 
     *   - Max players 4
     *   - Players must have unique names on a board
     *   - All players are on initial position - 0th step
     * @param name - player name
     * @return JSONArray of registered players on the board with new player added
     * @throws PlayerExistsException exception thrown when entered name 
     *        parameter matches existing players
     * @throws GameInProgressException exception thrown when we try to register 
     *        on a board where players have already started movement
     * @throws MaxPlayersReachedExeption exception thrown when we try to register more 
     *        players than allowed limit for board
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public JSONArray registerPlayer(String name) 
            throws PlayerExistsException, GameInProgressException,
                FileNotFoundException, UnsupportedEncodingException, MaxPlayersReachedExeption {
        if(data.getJSONArray("players").length()==4){
            throw new MaxPlayersReachedExeption(4);
        }
        for(Object playerObject:data.getJSONArray("players")){
            JSONObject player = (JSONObject)playerObject;
            if(player.getString("name").equals(name)){
                throw new PlayerExistsException(name);
            }
            if(player.getInt("position")!=0){
                throw new GameInProgressException();
            }
        }
        JSONObject newPlayer = new JSONObject();
        newPlayer.put("name", name);
        newPlayer.put("uuid", UUID.randomUUID());
        newPlayer.put("position", 0);
        data.getJSONArray("players").put(newPlayer);
        saveBoard();
        return data.getJSONArray("players");
    }
    
    /**
     * deletes player from list if uuid matches
     * @param playerUuid UUID of player which has to be deleted
     * @return JSONArray of all existing players on the board
     * @throws NoUserWithSuchUUIDException raised when incorrect UUID is passed
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public JSONArray deletePlayer(UUID playerUuid)
            throws NoUserWithSuchUUIDException, FileNotFoundException,
                UnsupportedEncodingException{
        Boolean response = false;
        for(int i = 0; i < data.getJSONArray("players").length(); i++){
            JSONObject player = data.getJSONArray("players").getJSONObject(i);
            
            if(player.getString("uuid").equals(playerUuid.toString())){
                data.getJSONArray("players").remove(i);
                data.put("turn", 0);
                saveBoard();
                response = true;
            }
        }
        if(!response){
            throw new NoUserWithSuchUUIDException(playerUuid.toString());
        }
        return data.getJSONArray("players");
    }
    
    /**
     * Roll the dice of the turn player and make move on the board per the outcome
     * of the dice roll
     * @param playerUuid UUID of player who has the turn
     * @return JSONObject containing the outcome of dice, name and uuid of turn 
     * player and a message specifying the move that was made by the turn player
     * @throws InvalidTurnException raised when incorrect uuid is passed
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public JSONObject rollDice(UUID playerUuid) 
            throws InvalidTurnException, FileNotFoundException,
                UnsupportedEncodingException{
        JSONObject response = new JSONObject();
        Integer turn = data.getInt("turn");
        if(playerUuid.equals(UUID.fromString(data.getJSONArray("players").getJSONObject(turn).getString("uuid")))){
            JSONObject player = data.getJSONArray("players").getJSONObject(turn);
            
            Integer dice = new Random().nextInt(6) + 1;
            Integer currentPosition = player.getInt("position");
            Integer newPosition = currentPosition + dice;
            String message = "";
            String playerName = player.getString("name");
            if(newPosition <= 100){
                JSONObject step = data.getJSONArray("steps").getJSONObject(newPosition);
                newPosition = step.getInt("target");
                if(step.getInt("type")==0){
                    message = "Player moved to " + newPosition;
                }else if(step.getInt("type")==1){
                    message = "Player was bit by a snake, moved back to " + newPosition;
                }else if(step.getInt("type")==2){
                    message = "Player climbed a ladder, moved to " + newPosition;
                }
                data.getJSONArray("players").getJSONObject(turn).put("position", newPosition);
            }else{
                message = "Incorrect roll of dice. Player did not move";
            }
            Integer newTurn = turn+1;
            if(newTurn >= data.getJSONArray("players").length()){
                newTurn = 0;
            }
            data.put("turn", newTurn);
            saveBoard();
            response.put("message", message);
            response.put("playerUuid", playerUuid);
            response.put("playerName", playerName);
            response.put("dice", dice);
            
        }else{
            throw new InvalidTurnException(playerUuid);
        }
        return response;
    }
    
    /**
     * access existing board object by uuid
     * @param uuid UUID of existing board
     * @throws IOException
     */
    public Board(UUID uuid) throws IOException {
        this.uuid = uuid;
        this.data = new JSONObject(new String(
                Files.readAllBytes(Paths.get(uuid.toString() + ".board"))));
    }
    
    /**
     * pretty print the UUID of board
     * @return UUID of board as String
     */
    @Override
    public String toString(){
        return this.uuid.toString();
    }
    
    /**
     * 
     * @return JSONObject board data containing steps, players and turn
     */
    public JSONObject getData(){
        return data;
    }
    
    /**
     * 
     * @return UUID of this board object
     */
    public UUID getUUID(){
        return uuid;
    }
}
