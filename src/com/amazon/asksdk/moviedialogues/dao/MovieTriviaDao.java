package com.amazon.asksdk.moviedialogues.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.asksdk.moviedialogues.objects.MovieTriviaGame;
import com.amazon.asksdk.moviedialogues.objects.MovieTriviaGameDataItem;
import com.amazon.asksdk.moviedialogues.objects.MovieTriviaUserData;
import com.amazon.speech.speechlet.Session;

/**
 * Contains the methods to interact with the persistence layer for MovieTrivia in DynamoDB.
 */
public class MovieTriviaDao {
	private final MovieTriviaDynamoDbClient dynamoDbClient;
	 private static final Logger log = LoggerFactory.getLogger(MovieTriviaDao.class);
	
	public MovieTriviaDao(MovieTriviaDynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }
	
	 /**
     * Reads and returns the {@link ScoreKeeperGame} using user information from the session.
     * <p>
     * Returns null if the item could not be found in the database.
     * 
     * @param session
     * @return
     */
    public MovieTriviaUserData getMovieUserData(Session session) {
    	log.info("getMovieUserData START##########################");
    	MovieTriviaGameDataItem item = new MovieTriviaGameDataItem();
        item.setUserid(session.getUser().getUserId());

        item = dynamoDbClient.loadItem(item);

        if (item == null) {
            return null;
        }
        log.info("getMovieUserData END return##########################");
        return item.getUserData(); 
    }

    /**
     * Saves the {@link ScoreKeeperGame} into the database.
     * 
     * @param game
     */
    public void saveMovieUserData(MovieTriviaGame game) {
    	log.info("saveMovieUserData START##########################");
    	MovieTriviaGameDataItem item = new MovieTriviaGameDataItem();
    	log.info("USER ID FROM SESSION PRINTING");
    	log.info("game.getSession().getUser().getUserId() "+game.getSession().getUser().getUserId());    	
        item.setUserid(game.getSession().getUser().getUserId());
        item.setUserData(game.getGameData());
        log.info("saveMovieUserData end##########################");
        dynamoDbClient.saveItem(item);
        
    }
    
    
    
   
}
