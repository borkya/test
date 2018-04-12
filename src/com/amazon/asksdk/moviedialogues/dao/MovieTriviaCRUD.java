package com.amazon.asksdk.moviedialogues.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.asksdk.moviedialogue.utilities.MovieTriviaUtility;
import com.amazon.asksdk.moviedialogues.objects.MovieTableMapper;
import com.amazon.asksdk.moviedialogues.objects.QuestionBean;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class MovieTriviaCRUD {
	 private static final Logger log = LoggerFactory.getLogger(MovieTriviaCRUD.class);
	    
	    String tableName = "movietrivia2";
	    
	    AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard().build();
	    static AmazonDynamoDB dynamoDBscan = AmazonDynamoDBClientBuilder.standard().build();
	    DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
	    static DynamoDB scanDynamoDB = new DynamoDB(dynamoDBscan);
	    static String scantableName = "MovieTrivia2";
	    
	    

	    public QuestionBean getFinalQuestionsForAlexa(List<Integer> listOfQuestionAsked) {
	    	
		         List<MovieTableMapper> scanResult = mapper.scan(MovieTableMapper.class, new DynamoDBScanExpression());
		         System.out.println("Size of scanResult 99999999.. :" + scanResult.size());
		         for (MovieTableMapper movieTrivia : scanResult) {
		        	   System.out.println("Clip from Db... :" +movieTrivia.getClip());
		               System.out.println("Question1 @@@@@@@@@@@@@@" + movieTrivia.getQuestion1());
		               System.out.println("Answer1 #############" + movieTrivia.getAnswer1());
		        	   System.out.println(" @@@@@@@@@@ Record number @@@@ :" + movieTrivia.getId() );
		        	
		         }
		        //TODO ###################
		       //TODO commented temporary List<MovieTableMapper> scanResult = mapper.scan(MovieTableMapper.class, scanExpression);
	            log.info("scanResult@@@@@@@@@"+scanResult);
	            log.info("scanResult111111111"+scanResult.size());
	          
	           MovieTableMapper scanResultfiltered = new MovieTableMapper();
	 	       QuestionBean alexQuestion = new QuestionBean();
		       	int randomNo = randInt( 1,MovieTriviaUtility.DB_RECORD_COUNT, listOfQuestionAsked);
		       	if(randomNo == 0){
		       		log.info("As random no is 0 reset the questionList");
		       		alexQuestion.setQuestionemptyList(true);//TODO
		       		alexQuestion.setQuestion(scanResult.get(0).getQuestion1());
		       		alexQuestion.setAnswer(scanResult.get(0).getAnswer1());
		       		alexQuestion.setCliphere(scanResult.get(0).getClip());
		       		alexQuestion.setId(scanResult.get(0).getId());
		       		return alexQuestion;
		       	   }

		        for (MovieTableMapper movieTrivia : scanResult) {
		        log.info("Id is " +movieTrivia.getId());
		       	log.info("randomNo is " +randomNo);	       	   
		       	   if (movieTrivia.getId().equals(randomNo)) {
		       		log.info("Got the number from db list " +movieTrivia.getId());
		       		   scanResultfiltered.setId(movieTrivia.getId());   
		       		   scanResultfiltered.setAnswer1(movieTrivia.getAnswer1());
		           	   scanResultfiltered.setAnswer2(movieTrivia.getAnswer2());
		           	   scanResultfiltered.setClip(movieTrivia.getClip());
		           	   scanResultfiltered.setQuestion1(movieTrivia.getQuestion1());
		           	   scanResultfiltered.setQuestion2(movieTrivia.getQuestion2());
		           	log.info("breaking from for loop");
		           	break;
		       	   }
		       	
		        }
		        log.info("getFinalQuestionsForAlexa end" );
			    return getQuestionForAlexaUser(scanResultfiltered, alexQuestion);
	    	
	    }


		private DynamoDBScanExpression getMovieTriviaFromDb() {
			log.info("In getMovieTriviaFromDb START");
//	    	MovieTableMapper partitionKey  = new MovieTableMapper();
//	        partitionKey.setId(1); 
//	        DynamoDBQueryExpression<MovieTableMapper> queryExpression = new DynamoDBQueryExpression<MovieTableMapper>() 
//	        	    .withHashKeyValues(partitionKey );
	        
//	        List<MovieTableMapper> itemList = mapper.query(MovieTableMapper.class, queryExpression);
	        
			Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
	         eav.put(":val1", new AttributeValue().withN("100"));
	         eav.put(":val2", new AttributeValue().withS("Id"));
	       
	         DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
	             .withFilterExpression("Id < :val1 and Id = :val2").withExpressionAttributeValues(eav);
	         log.info("In getMovieTriviaFromDb END");
			return scanExpression;
		}
	    
	    
	    public static QuestionBean getQuestionForAlexaUser(MovieTableMapper scanResultfiltered,
	    		QuestionBean alexQuestion) {
	    	log.info("In getQuestionForAlexaUser START");
	    	if (scanResultfiltered != null){
	    		alexQuestion.setQuestion(scanResultfiltered.getQuestion1());
	             alexQuestion.setId(scanResultfiltered.getId());
	             alexQuestion.setAnswer(scanResultfiltered.getAnswer1());
	             alexQuestion.setCliphere(scanResultfiltered.getClip());
	             log.info("In getQuestionForAlexaUser END");
	             return alexQuestion;
	    	}else{
	    		log.info("In getQuestionForAlexaUser END...returning null");
	             return alexQuestion;
	    	}
             
             
    	}
        
        public static int randInt(int min, int max, List<Integer> sessionList) {
        	log.info("Min is "+min+ " and max is "+max);
     		if(sessionList!= null && sessionList.size() <= MovieTriviaUtility.DB_RECORD_COUNT) {
     			Random rn = new Random();
     	 	    int randomNum = rn.nextInt((max - min) + 1) + min;
     	 	    System.out.print("Random No is "+randomNum);
     	 	    
     	 	    if (sessionList.contains(randomNum)) {
     	 	    	log.info("Recall randInt ");
     	 	    	return randInt( min, max, sessionList);
     	 	    }else {
     	 	    	log.info("returning random number "+randomNum);
     	 	    	return randomNum;
     	 	    }
     		}else {
     			log.info("returning 0");
     			return 0;//reset the counter to 1.in cases where the list is >100 njki0]\
     		}
     		
        }     
}
