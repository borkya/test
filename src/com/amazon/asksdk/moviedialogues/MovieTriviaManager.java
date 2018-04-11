package com.amazon.asksdk.moviedialogues;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.asksdk.moviedialogue.utilities.MovieTriviaUtility;
import com.amazon.asksdk.moviedialogues.dao.MovieTriviaCRUD;
import com.amazon.asksdk.moviedialogues.dao.MovieTriviaDao;
import com.amazon.asksdk.moviedialogues.dao.MovieTriviaDynamoDbClient;
import com.amazon.asksdk.moviedialogues.objects.MovieTriviaGame;
import com.amazon.asksdk.moviedialogues.objects.MovieTriviaUserData;
import com.amazon.asksdk.moviedialogues.objects.QuestionBean;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class MovieTriviaManager {
	private static final Logger log = LoggerFactory.getLogger(MovieTriviaManager.class);
//	public QuestionBean qb = new QuestionBean();
	private MovieTriviaCRUD dynamoDBOperations = new MovieTriviaCRUD();
    private final MovieTriviaDao movieTriviaDao;
    public static String clipUsed = " ";
	public static String question=" ";
	public static String answer =" ";
	public static String clip =" ";
	
    
    public MovieTriviaManager(final AmazonDynamoDBClient amazonDynamoDbClient) {
    	MovieTriviaDynamoDbClient dynamoDbClient =
                new MovieTriviaDynamoDbClient(amazonDynamoDbClient);
    	movieTriviaDao = new MovieTriviaDao(dynamoDbClient);
    }
	 public SpeechletResponse handleYesIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope,
			List<Integer> listOfQuestionAlreadyAsked ,String sessionIdSessionStart) {
			QuestionBean qb = new QuestionBean();
	    	log.info("Inside handleYesIntent ... START" );
	        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
	        String speechText ="Which movie this Dialogue is from ?  ";
	        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
	        Reprompt reprompt = getReprompt(speech);
	        Session session = requestEnvelope.getSession();
	        //  speechOutput = "<break time=\"\1s"\>";
	        //  speechOutput = speechOutput + "<audio src='https://s3.amazonaws.com/ask-soundlibrary/animals/amzn_sfx_bear_groan_roar_01.mp3'/>";
	        // TODO  write a method to check if same session and and increment the question.  
	        log.info("Session Id @@@@@@... :" + session.getSessionId() );
//	        log.info(" Question Number @@@@@@... :" + questionNumber);
	        String speechOutput = "";
	        Object listOfQqAskedToAlexa = requestEnvelope.getSession().getAttribute("listOfQuestionAlreadyAsked");
	        //Convert to List<Integers>
	        qb = dynamoDBOperations.getFinalQuestionsForAlexa(listOfQuestionAlreadyAsked);
	        answer = qb.getAnswer();
	        clip 		 = qb.getCliphere();
			clipUsed 	 = MovieTriviaUtility.convertClip(clip);
	        log.info(" 222222222 Clip id from dynamoDB :" 	+ qb.getCliphere());
	        log.info(" 333333333 Clip used ---:  :" 		+ clipUsed);
	        log.info(" 444444444 Question from dynamoDB :" 	+ qb.getQuestion());
	        log.info(" 555555555 Answer from dynamoDB :" 	+ qb.getAnswer());
	 		
	        if(sessionIdSessionStart.equalsIgnoreCase(session.getSessionId())){
	        		listOfQuestionAlreadyAsked.add(qb.getId());
//	        		questionNumber++;
	        		log.info("Questions Asked so far QQQQQQQQ... :" + listOfQuestionAlreadyAsked);
	        		log.info("Id of Question Asked   IIIIIIII... :" + qb.getId());
	        	}
//		   Sample format 
//		   String speechOutput = "<audio src='https://s3.amazonaws.com/moviedialogs/Boman_Munnabhai.mp3'/>"; speechOutput = speechOutput+ "<break time= \"\2s\" />"; 
           
	 		speechOutput = clipUsed;
	 		speechOutput = speechOutput + qb.getQuestion() ;
			outputSpeech.setSsml("<speak>" + speechOutput + "</speak>");
			requestEnvelope.getSession().setAttribute("listOfQuestionAlreadyAsked", listOfQuestionAlreadyAsked);
		
			return SpeechletResponse.newAskResponse(outputSpeech,reprompt);   // newTellResponse(outputSpeech);
	    }
	    public SpeechletResponse handleAnswerIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
	    	log.info("In handleAnswerIntent....... START. ");
	    	
	    	IntentRequest request = requestEnvelope.getRequest();
//	    	Session session = requestEnvelope.getSession();
	    	Intent intent = request.getIntent();
//	    	log.info("Session Id @@@@@@... :" + session.getSessionId() );
//	        log.info(" Question Number QQQQQQQQ... :" + questionNumber);
//	    	 	if(sessionId.equalsIgnoreCase(session.getSessionId())){
//	    	 		questionNumber++;
//	    	 		qb = dynamoDBOperations.getFinalQuestionsForAlexa(listOfQuestionAlreadyAsked);
//	    	 		log.info(" @@@@@@@ Clip id from dynamoDB :" + qb.getCliphere());
//	    	 		listOfQuestionAlreadyAsked.add(qb.getId());
//	    	 		log.info("Questions Asked so far QQQQQQQQ... :" + listOfQuestionAlreadyAsked);
//	    	 		log.info("Id of Question Asked  IIIIIIIII... :" + qb.getId());
//	    	 		 question	 = qb.getQuestion();
//	    	 		 answer 	 = qb.getAnswer();
//	    	 		 clip 		 = qb.getCliphere();
//	    	 		 clipUsed 	 = MovieTriviaUtility.convertClip(clip);
//	    	 			 		}
	    	Slot slot = intent.getSlot(MovieTriviaUtility.SLOT_ANSWER);
	    	log.info(" $$$$$$$$ SLOT Value Answer is ...: "  + slot.getValue());
	    	log.info(" 6666666 Answer from dynamoDB :" + answer);
//	    	log.info(" $$$$$$$$ Answer from dynamoDB ...:" + qb.getAnswer()); 
	    	String speechOutput = "";
	    	String speechOutputCard ="";
	    	 if(answer.equalsIgnoreCase(slot.getValue())){
	    		 speechOutput = "Your answer is Right ." + MovieTriviaUtility.getdynamicContinue();    
	    		 speechOutputCard = "Your answer is Right. " + MovieTriviaUtility.getdynamicContinue();
	    		 return newAskResponse(speechOutputCard,speechOutput );    		
	     		}
	    	 else{
	    		 answer = answer + "." ;
	    		 speechOutputCard= " your answer is wrong. The correct answer is," + answer + " " +  MovieTriviaUtility.getdynamicContinue();
	    		  speechOutput = "Your answer is Wrong. The correct answer is,"+  answer + " " +  MovieTriviaUtility.getdynamicContinue();
	    		 return newAskResponse(speechOutputCard,speechOutput );
	    	 }
	    		 }
	 
	 private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText(speechText);

	        return speech;
	    }
	    private Reprompt getReprompt(OutputSpeech outputSpeech) {
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(outputSpeech);

	        return reprompt;
	    }
	    private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
	        return newAskResponse(stringOutput, false, repromptText, false);
	    }
	    
	    
	    SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml,
	            String repromptText, boolean isRepromptSsml) {
	        OutputSpeech outputSpeech, repromptOutputSpeech;
	        if (isOutputSsml) {
	            outputSpeech = new SsmlOutputSpeech();
	            ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
	        } else {
	            outputSpeech = new PlainTextOutputSpeech();
	            ((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
	        }

	        if (isRepromptSsml) {
	            repromptOutputSpeech = new SsmlOutputSpeech();
	            ((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);
	        } else {
	            repromptOutputSpeech = new PlainTextOutputSpeech();
	            ((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
	        }
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(repromptOutputSpeech);
	        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
	    }
	    public SpeechletResponse getWelcomeResponse() {
	        String speechText = "Welcome to  Bollywood Masti,"
	        					+ "I will play a dialog from a Bollywood movie and ask you an interesting Question about it. " 
	        					+ "For example, which movie is this from ? Or, who was the lead Actor in this movie?" 
	        					+ "Are you ready to play the game?";
	        // If the user either does not reply to the welcome message or says something that is not
	        // understood, they will be prompted again with this text.
	        String repromptText =
	                "Welcome to  Bollywood Masti,"
	                            + " I will play a dialog from a Bollywood movie and ask you an interesting Question about it. "
	                            + " For example, which movie is this from ? Or, who was the lead Actor in this movie?"
	                            + "Are you ready to play the game?";
	        return getAskResponse("Bollywood Masti", speechText,repromptText);
	  //    return newAskResponse(speechOutput, false, repromptText, false);
	    }
	    private SpeechletResponse getAskResponse(String cardTitle, String speechText,String repromptText) {
	        SimpleCard card = getSimpleCard(cardTitle, speechText);
	        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
	        PlainTextOutputSpeech repromptSpeech = getPlainTextOutputSpeech(repromptText);
	        Reprompt reprompt = getReprompt(repromptSpeech);
	        return SpeechletResponse.newAskResponse(speech, reprompt, card);
	    }  
	    private SimpleCard getSimpleCard(String title, String content) {
	        SimpleCard card = new SimpleCard();
	        card.setTitle(title);
	        card.setContent(content);

	        return card;
	    }
	    
	    public void saveToUserData(List<Integer> listOfQuestionAlreadyAsked, Session session){
	    	log.info(" $$$$$$$$ listOfQuestionAlreadyAsked is ...: "  + listOfQuestionAlreadyAsked);
	    	MovieTriviaGame gamedata = new MovieTriviaGame();
	    	gamedata.setSession(session);
	    	MovieTriviaUserData data = new MovieTriviaUserData();
	    	data.setquestionid(listOfQuestionAlreadyAsked);
	    	gamedata.setGameData(data);
	    	movieTriviaDao.saveMovieUserData(gamedata);
	    	
	    }
	    
}
