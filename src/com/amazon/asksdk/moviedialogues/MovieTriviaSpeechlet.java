package com.amazon.asksdk.moviedialogues;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.asksdk.moviedialogue.utilities.MovieTriviaUtility;
import com.amazon.asksdk.moviedialogues.dao.MovieTriviaCRUD;
import com.amazon.asksdk.moviedialogues.objects.QuestionBean;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.services.DirectiveService;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class MovieTriviaSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(MovieTriviaSpeechlet.class);
    public String sessionIdSessionStart = "";
    private AmazonDynamoDBClient amazonDynamoDBClient;
    private MovieTriviaManager movieTriviaManager;
    public int questionNumber = 1;
    
    public static int counter = 0;
    

    public MovieTriviaSpeechlet(DirectiveService directiveService) {
    }
    
    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        SessionStartedRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        sessionIdSessionStart   = session.getSessionId();
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        initializeComponents();
        // any initialization logic goes here
    }
    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        LaunchRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return movieTriviaManager.getWelcomeResponse();
    }
    @Override
   public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
    	log.info("In onIntent @@@@@@@@@@@@ START. ");
    	log.info("onIntent requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    	String intentName = requestEnvelope.getRequest().getIntent().getName();
        log.info("Intent Name is ....:  " + intentName);
        if ("AMAZON.YesIntent".equals(intentName)){
        	return movieTriviaManager.handleYesIntent(requestEnvelope,sessionIdSessionStart);  
        	
        } else if ("AnswerIntent".equals(intentName)){
        	return movieTriviaManager.handleAnswerIntent(requestEnvelope);  
        }else if ("AMAZON.NoIntent".equals(intentName)) {
        	 log.info("Session Id before @@@@@@... :" + sessionIdSessionStart );
             log.info(" Question Number before @@@@@@... :" + questionNumber); 
        	 resetIds(requestEnvelope);
        	 log.info("Session Id after @@@@@@... :" + sessionIdSessionStart );
             log.info(" Question Number after@@@@@@... :" + questionNumber);
        	 PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
             outputSpeech.setText("Thanks for playing,Goodbye"); 
             // TODO Give some nice message to 
             return SpeechletResponse.newTellResponse(outputSpeech);

        }else if ("AMAZON.HelpIntent".equals(intentName)) {
            // Create the plain text output.
        	String speechOutput = "I will play a dialog from a Bollywood movie and ask you interesting Question about it." 
             + " For example, which movie is this from?,"
             + " or who was the lead Actor in this movie";
        	String repromptText = "Which country you want to know about?";
            return movieTriviaManager.newAskResponse(speechOutput, false, repromptText, false);
        } else if ("AMAZON.StopIntent".equals(intentName)) {
        	log.info("Session Id before @@@@@@... :" + sessionIdSessionStart );
            log.info(" Question Number before @@@@@@... :" + questionNumber); 
       	 	resetIds(requestEnvelope);
       	 	log.info("Session Id after @@@@@@... :" + sessionIdSessionStart );
            log.info(" Question Number after@@@@@@... :" + questionNumber);
        	PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        	outputSpeech.setText("Thanks for playing,Goodbye");
            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
        	log.info("Session Id before @@@@@@... :" + sessionIdSessionStart );
            log.info(" Question Number before @@@@@@... :" + questionNumber); 
            resetIds(requestEnvelope);
       	 log.info("Session Id after @@@@@@... :" + sessionIdSessionStart );
            log.info(" Question Number after@@@@@@... :" + questionNumber);
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Thanks for playing,Goodbye");
            return SpeechletResponse.newTellResponse(outputSpeech);
        } else {
        	resetIds(requestEnvelope);
        	String outputSpeech = "Sorry, I didn't get that.";
            String repromptText = "What country do you want facts for?";
            return movieTriviaManager.newAskResponse(outputSpeech, true, repromptText, true);
        }
      
    }
  
 	
   	@Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        SessionEndedRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();

        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        // any session cleanup logic would go here
    }
   

  
    private void resetIds(SpeechletRequestEnvelope<IntentRequest> requestEnvelope){
    	sessionIdSessionStart ="";
    	questionNumber =1;
    	log.info("Session to be saved ....: "  + requestEnvelope.getSession() );
    	if(requestEnvelope.getSession() != null){
    		log.info("TEST LIST@@@@: "  + requestEnvelope.getSession().getAttribute("listOfQuestionAlreadyAsked") );
    		List<Integer>  qqObj = (ArrayList<Integer>)requestEnvelope.getSession().getAttribute("listOfQuestionAlreadyAsked");//TODO thid will not work.Need way to comvert Object attribute to 
    		log.info("qqObjT@@@@: "  + qqObj );	
    	movieTriviaManager.saveToUserData(qqObj, requestEnvelope.getSession());
    	}
    	
    }
    
    private void initializeComponents() {
        if (amazonDynamoDBClient == null) {
            amazonDynamoDBClient = new AmazonDynamoDBClient();
            movieTriviaManager = new MovieTriviaManager(amazonDynamoDBClient);
           
        }
    }
    
}

