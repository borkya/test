package com.amazon.asksdk.moviedialogues.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.asksdk.moviedialogues.objects.MovieTriviaGameDataItem;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


/**
 * Client for DynamoDB persistance layer for the Movie Trivia skill.
 */
public class MovieTriviaDynamoDbClient {
    private final AmazonDynamoDBClient dynamoDBClient;

	 private static final Logger log = LoggerFactory.getLogger(MovieTriviaDynamoDbClient.class);
 
    public MovieTriviaDynamoDbClient(final AmazonDynamoDBClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    /**
     * Loads an item from DynamoDB by primary Hash Key. Callers of this method should pass in an
     * object which represents an item in the DynamoDB table item with the primary key populated.
     * 
     * @param tableItem
     * @return
     */
    public MovieTriviaGameDataItem loadItem(final MovieTriviaGameDataItem tableItem) {
        DynamoDBMapper mapper = createDynamoDBMapper();
        MovieTriviaGameDataItem item = mapper.load(tableItem);
   //     log.info(" " + item);

        return item;
    }

    /**
     * Stores an item to DynamoDB.
     * 
     * @param tableItem
     */
    public void saveItem(final MovieTriviaGameDataItem tableItem) {
    	log.info("saveItem START##########################");
        DynamoDBMapper mapper = createDynamoDBMapper();
    	log.info("saveItem END##########################");
        mapper.save(tableItem);
    }

    /**
     * Creates a {@link DynamoDBMapper} using the default configurations.
     * 
     * @return
     */
    private DynamoDBMapper createDynamoDBMapper() {
        return new DynamoDBMapper(dynamoDBClient);
    }
}


