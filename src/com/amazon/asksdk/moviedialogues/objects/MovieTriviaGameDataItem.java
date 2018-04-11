package com.amazon.asksdk.moviedialogues.objects;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Model representing an item of the MovieTriviaGameData table in DynamoDB for the Movie Trivia
 * skill.
 */
@DynamoDBTable(tableName = "MovieTriviaUserData")
public class MovieTriviaGameDataItem {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private String userid;
	private MovieTriviaUserData userData;

	@DynamoDBHashKey(attributeName = "UserId")
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	@DynamoDBAttribute(attributeName = "Data")
    @DynamoDBMarshalling(marshallerClass = MovieTriviaGameDataItemMarshaller.class)

	public MovieTriviaUserData getUserData() {
		return userData;
	}

	public void setUserData(MovieTriviaUserData userData) {
		this.userData = userData;
	}
	
	 /**
     * A {@link DynamoDBMarshaller} that provides marshalling and unmarshalling logic for
     * {@link MovieTriviaUserData} values so that they can be persisted in the database as String.
     */
    public static class MovieTriviaGameDataItemMarshaller implements
            DynamoDBMarshaller<MovieTriviaUserData> {

        @Override
        public String marshall(MovieTriviaUserData gameData) {
            try {
                return OBJECT_MAPPER.writeValueAsString(gameData);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Unable to marshall game data", e);
            }
        }

        @Override
        public MovieTriviaUserData unmarshall(Class<MovieTriviaUserData> clazz, String value) {
            try {
                return OBJECT_MAPPER.readValue(value, new TypeReference<MovieTriviaUserData>() {
                });
            } catch (Exception e) {
                throw new IllegalStateException("Unable to unmarshall game data value", e);
            }
        }
    }

}
