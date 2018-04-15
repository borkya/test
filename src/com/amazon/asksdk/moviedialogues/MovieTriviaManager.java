package com.amazon.asksdk.moviedialogues;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.asksdk.moviedialogue.utilities.MovieTriviaUtility;
import com.amazon.asksdk.moviedialogues.dao.MovieTriviaCRUD;
import com.amazon.asksdk.moviedialogues.dao.MovieTriviaDao;
import com.amazon.asksdk.moviedialogues.dao.MovieTriviaDynamoDbClient;
import com.amazon.asksdk.moviedialogues.objects.MovieTriviaGame;
import com.amazon.asksdk.moviedialogues.objects.MovieTriviaGameDataItem;
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
	private MovieTriviaCRUD dynamoDBOperations = new MovieTriviaCRUD();
    private final MovieTriviaDao movieTriviaDao;
    public static String clipUsed = " ";
	public static String question=" ";
//	public static String answer =" ";
	public static String clip =" ";
	
    
    public MovieTriviaManager(final AmazonDynamoDBClient amazonDynamoDbClient) {
    	MovieTriviaDynamoDbClient dynamoDbClient =
                new MovieTriviaDynamoDbClient(amazonDynamoDbClient);
    	movieTriviaDao = new MovieTriviaDao(dynamoDbClient);
    }
	public SpeechletResponse handleYesIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope,
			String sessionIdSessionStart) {
		 
		 try {	 
		 log.info("In handleYesIntent START "  + requestEnvelope.getSession().getAttribute("listOfQuestionAlreadyAsked") );
		    if (!requestEnvelope.getSession().getAttributes().isEmpty()) {
		    	saveToDbAndEmptySession(requestEnvelope);
       	    }
			QuestionBean qb = new QuestionBean();
	    	log.info("Inside handleYesIntent ... START" );
	        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
	        String speechText ="Which movie this Dialogue is from ?  ";
	        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
	        Reprompt reprompt = getReprompt(speech);
	        Session session = requestEnvelope.getSession();
	        requestEnvelope.getSession().setAttribute("counter", 1);
	        //  speechOutput = "<break time=\"\1s"\>";
	        //  speechOutput = speechOutput + "<audio src='https://s3.amazonaws.com/ask-soundlibrary/animals/amzn_sfx_bear_groan_roar_01.mp3'/>";
	        // TODO  write a method to check if same session and and increment the question.  
	        log.info("Session Id @@@@@@... :" + session.getSessionId() );
//	        log.info(" Question Number @@@@@@... :" + questionNumber);
	        String speechOutput = "";
	   
	        List<Integer> ojj = getListOfAlreadyAskedQuestions(requestEnvelope, session);
	        qb = dynamoDBOperations.getFinalQuestionsForAlexa(ojj,requestEnvelope);
	        if (qb != null) {
		 	      log.info(" qb is not null");
	        	if (qb.getQuestionemptyList()) {
		 	      resetDbAndSessionIdList(requestEnvelope, session);

	        	}
	        	String answer = qb.getAnswer();
	        	requestEnvelope.getSession().setAttribute("answer",answer );
	        	clip 		 = qb.getCliphere();
	 			clipUsed 	 = MovieTriviaUtility.convertClip(clip);
	 	        log.info(" 222222222 Clip id from dynamoDB :" 	+ qb.getCliphere());
	 	        log.info(" 333333333 Clip used ---:  :" 		+ clipUsed);
	 	        log.info(" 444444444 Question from dynamoDB :" 	+ qb.getQuestion());
	 	        log.info(" 555555555 Answer from dynamoDB :" 	+ qb.getAnswer());
	 	        List<Integer> ojj1 = new ArrayList<>();
	 	        if (ojj != null){
	 	        	 log.info(" ojj is :" 	+ ojj);
	 	        	 log.info(" ojj1 is before:" 	+ ojj1);
	 	        	 ojj1 = ojj;
	 	        	 log.info(" ojj1 is after:" 	+ ojj1);
	 	        }
	 	       if (qb.getId() != null) {
	 		       ojj1.add(qb.getId());
	  	 	       }       
	 	        log.info(" added listOfQuestionAlreadyAskedid to list and new list is " + ojj1);
	 	        requestEnvelope.getSession().setAttribute("listOfQuestionAlreadyAsked", ojj1);
	 	        log.info("log after setting listOfQuestionAlreadyAsked");

		        speechOutput = clipUsed;
	 	 		speechOutput = speechOutput + qb.getQuestion() ;
	 			outputSpeech.setSsml("<speak>" + speechOutput + "</speak>");
	 	        log.info("erererererreeeeeeeeeeeeeee");
	 	        log.info("SpeechOutput    : " + speechOutput);

	 			return SpeechletResponse.newAskResponse(outputSpeech,reprompt);   // newTellResponse(outputSpeech);
	        }else {
	        	speechOutput = "The QuestionBean is null.Check logs";
	        	return SpeechletResponse.newAskResponse(outputSpeech,reprompt);
	        }
	        
		 }catch (Exception e) {
			log.info(" Exception is :" 	+  e.getMessage());
			 String outputSpeech =  "OOPS, something went wrong with URI connectivity.Do you want to start over?";//  + MovieTriviaUtility.getdynamicContinue();
			 SsmlOutputSpeech ssmloutputSpeech = new SsmlOutputSpeech(); 
			 ssmloutputSpeech.setSsml("<speak>" + outputSpeech + "</speak>");
		      PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Want to start again?");

		    Reprompt reprompt = getReprompt(speech);
 			return SpeechletResponse.newAskResponse(ssmloutputSpeech,reprompt);  
		 }
	      }
	private void resetDbAndSessionIdList(SpeechletRequestEnvelope<IntentRequest> requestEnvelope, Session session) {
		log.info(" As qb has getQuestionEmptyList is true delete the records MovieTriviaUserData for that userId");
		  resetMovieTriviaDataInDb(session);	
		  requestEnvelope.getSession().removeAttribute("listOfQuestionAlreadyAsked");
	}
	private void resetMovieTriviaDataInDb(Session session) {
		log.info("resetMovieTriviaDataInDb START");

		  MovieTriviaGame game = new MovieTriviaGame();
		  MovieTriviaUserData userData = new MovieTriviaUserData();
		  List<Integer> listofemptyQId = new ArrayList<>();
		  listofemptyQId.add(7);//TODO temporary setting to sharabi
		  userData.setquestionid(listofemptyQId);
		  game.setGameData(userData);
		  game.setSession(session);
		  movieTriviaDao.saveMovieUserData(game);
		log.info("resetMovieTriviaDataInDb END");

	}
	private void saveToDbAndEmptySession(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		log.info("Session is not the first time");
		log.info("TEST LIST@@@@: "  + requestEnvelope.getSession().getAttribute("listOfQuestionAlreadyAsked") );
		List<Integer>  saveQuestionIdList = (ArrayList<Integer>)requestEnvelope.getSession().getAttribute("listOfQuestionAlreadyAsked");
		log.info("qqObjT@@@@: "  + saveQuestionIdList );	
		saveToUserData(saveQuestionIdList, requestEnvelope.getSession());
		requestEnvelope.getSession().removeAttribute("listOfQuestionAlreadyAsked");
		requestEnvelope.getSession().removeAttribute("counter");
		requestEnvelope.getSession().removeAttribute("answer");
		requestEnvelope.getSession().removeAttribute("rightanswercount");

		
	}
	private List<Integer> getListOfAlreadyAskedQuestions(SpeechletRequestEnvelope<IntentRequest> requestEnvelope,
			Session session) {
		Object listOfQAskedToAlexa = requestEnvelope.getSession().getAttribute("listOfQuestionAlreadyAsked");
		log.info(" listOfQqAskedToAlexa frm session 111@@@@@@... :" + listOfQAskedToAlexa);
		if (listOfQAskedToAlexa != null) {
		log.info(" Type of object@@@@@@.is.. :" + listOfQAskedToAlexa.getClass());
		
		}
		
		//Convert to List<Integers>
      // LinkedHashMap<String, Object> ojj = (LinkedHashMap<String, Object>)listOfQAskedToAlexa;
		List<Integer> ojj = (List)listOfQAskedToAlexa;
		if (ojj == null || ojj.isEmpty()) {
			MovieTriviaUserData alreadyAskedData = movieTriviaDao.getMovieUserData(session);
			if (alreadyAskedData != null) {
				log.info(" alreadyAskedData frm db @@@@@@... :" + alreadyAskedData);
				List<Integer> questionIdlistFromDb = alreadyAskedData.getquestionid();
				if (questionIdlistFromDb != null) {
		    		log.info(" questionIdlistFromDb frm db @@@@@@... :" + questionIdlistFromDb);
					  ojj = questionIdlistFromDb;
				}
			}
			
		}
		return ojj;
	}
	
	public SpeechletResponse handleAnswerIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		try {
	    	log.info("In handleAnswerIntent....... START. ");
	    	int counter =  0;
	    	int rightanswercounter = 0;
	    	String speechOutput = "";
	    	String outputSpeech ="";
	    	String speechOutputCard ="";
	    	String answer="";
	    	Intent intent = requestEnvelope.getRequest().getIntent();
	    	SsmlOutputSpeech ssmloutputSpeech = new SsmlOutputSpeech();
	    	String speechText ="Which movie this Dialogue is from ?  ";
	        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
	        Reprompt reprompt = getReprompt(speech);
	        Slot slot = intent.getSlot(MovieTriviaUtility.SLOT_ANSWER);
	    	Object counterObj = requestEnvelope.getSession().getAttribute("counter");
	    	Object answerObj  = requestEnvelope.getSession().getAttribute("answer");
	    	Object rightanswercounterObj = requestEnvelope.getSession().getAttribute("rightanswercount");
		    	if (answerObj instanceof String) {
		 	         log.info("convert answerObj to int" + answerObj);
		        	 answer =  (String)answerObj;	        	
		 	         log.info(" answer value is....: " + answer);
		        }
		        if (counterObj instanceof Integer) {
		 	         log.info("convert counterObj to int" + counterObj);
		        	 counter =  (Integer)counterObj;	        	
		 	         log.info(" counter value is....: "+counter);
		        }
		        if (rightanswercounterObj instanceof Integer) {
		 	         log.info("convert rightanswercounterObj to int" + rightanswercounterObj);
		 	        rightanswercounter =  (Integer)rightanswercounterObj;	        	
		 	         log.info(" counter value is....: "+rightanswercounter);
		        }
		        
				        if(counter < MovieTriviaUtility.QB_COUNTER ) {
					        QuestionBean qb= new QuestionBean();
					        Session session = requestEnvelope.getSession();
					        List<Integer> ojj = getListOfAlreadyAskedQuestions(requestEnvelope, session);
						    qb = dynamoDBOperations.getFinalQuestionsForAlexa(ojj,requestEnvelope);	 
						    if (qb.getQuestionemptyList()) {
						 	     resetDbAndSessionIdList(requestEnvelope, session);
					        }
						    log.info(" $$$$$$$$ SLOT Value Answer is ...: "  + slot.getValue());
					    	log.info(" $$$$$$$$ Answer from dynamoDB :" + answer);
					    		
						    	if(answer.equalsIgnoreCase(slot.getValue())) {
						    			clip 		 = qb.getCliphere();
							 			clipUsed 	 = MovieTriviaUtility.convertClip(clip);
							 	        log.info(" 222222222 Clip id from dynamoDB :" 	+ qb.getCliphere());
							 	        log.info(" 333333333 Clip used ---:  " 		+ clipUsed);
							 	        log.info(" 444444444 Question from dynamoDB :" 	+ qb.getQuestion());
							 	        log.info(" 555555555 Answer from dynamoDB :" 	+ qb.getAnswer());
						    			outputSpeech =  "Your answer is Right. Here is next clip." + clipUsed  + qb.getQuestion() ;
						    			ssmloutputSpeech.setSsml("<speak>" + outputSpeech + "</speak>");
						    			counter = counter + 1;
						    			rightanswercounter = rightanswercounter + 1;
						    			log.info(" 6666666666  Counter value set in session : " + counter);
						    			setListOfQuestionAlreadyAskedSessionObject(requestEnvelope, qb.getId());					    									    			
						    			requestEnvelope.getSession().setAttribute("counter", counter);
						    			//requestEnvelope.getSession().removeAttribute("answer");
						    			requestEnvelope.getSession().setAttribute("answer",qb.getAnswer());
						    			requestEnvelope.getSession().setAttribute("rightanswercount",rightanswercounter);
							 	        log.info("111111erererererreeeeeeeeeeeeeee");
							 	       log.info("111111erererererreeeeeeeeeeeeeee "+outputSpeech);

						    			return SpeechletResponse.newAskResponse(ssmloutputSpeech,reprompt);  
							 		
						    		}else {
						    			clip 		 = qb.getCliphere();
							 			clipUsed 	 = MovieTriviaUtility.convertClip(clip);
							 	        log.info(" 222222222 Clip id from dynamoDB :" 	+ qb.getCliphere());
							 	        log.info(" 333333333 Clip used ---:  :" 		+ clipUsed);
							 	        log.info(" 444444444 Question from dynamoDB :" 	+ qb.getQuestion());
							 	        log.info(" 555555555 Answer from dynamoDB :" 	+ qb.getAnswer());
							 	        //answer = answer + "." ;
							 	        outputSpeech =  "Your answer is Wrong.The correct Answer is," +  answer + "Here is next clip." + clipUsed  + qb.getQuestion() ;
						    			ssmloutputSpeech.setSsml("<speak>" + outputSpeech + "</speak>");
						    			counter = counter + 1;
						    			log.info(" 6666666666  Counter value set in session : " + counter);
						    			setListOfQuestionAlreadyAskedSessionObject(requestEnvelope, qb.getId());					    									    			
						    			requestEnvelope.getSession().setAttribute("counter", counter);
						    			//requestEnvelope.getSession().removeAttribute("answer");
						    			requestEnvelope.getSession().setAttribute("answer",qb.getAnswer());
							 	        log.info("222222erererererreeeeeeeeeeeeeee");
								 	    log.info("222222erererererreeeeeeeeeeeeeee "+outputSpeech);

						    			return SpeechletResponse.newAskResponse(ssmloutputSpeech,reprompt);  
						    		}
				           }
						        if(counter == MovieTriviaUtility.QB_COUNTER ) {
						            log.info(" $$$$$$$$ SLOT Value Answer for  LAST QUESTION ...: "  + slot.getValue());
							    	log.info(" $$$$$$$$ Answer from dynamoDB for  LAST QUESTION...:" + answer);
								    		if(answer.equalsIgnoreCase(slot.getValue())) {
								    			rightanswercounter = rightanswercounter + 1;
								    			requestEnvelope.getSession().setAttribute("rightanswercount",rightanswercounter);
								    			
								    			outputSpeech =  "Your Answer is Right. You answered" +rightanswercounter+" out for 5. " + MovieTriviaUtility.getdynamicContinue();
								    			ssmloutputSpeech.setSsml("<speak>" + outputSpeech + "</speak>");
								    			requestEnvelope.getSession().removeAttribute("answer");
									 	        log.info("333333erererererreeeeeeeeeeeeeee");
									 	        log.info("333333erererererreeeeeeeeeeeeeee"+outputSpeech);

								    			return SpeechletResponse.newAskResponse(ssmloutputSpeech,reprompt);  
									 		
								    		}else {
								    			//answer = answer + "." ;
									 	        outputSpeech =  "Your answer is Wrong. " +" The correct Answer is " +  answer +" You answered " +rightanswercounter+" out for 5.\""+  MovieTriviaUtility.getdynamicContinue();
								    			ssmloutputSpeech.setSsml("<speak>" + outputSpeech + "</speak>");
								    			requestEnvelope.getSession().removeAttribute("answer");
									 	        log.info("444444erererererreeeeeeeeeeeeeee");
									 	        log.info("444444erererererreeeeeeeeeeeeeee"+outputSpeech);

								    			return SpeechletResponse.newAskResponse(ssmloutputSpeech,reprompt);  
								    		}
							        }
				        
				        if(answer.equalsIgnoreCase(slot.getValue())) {
			    			outputSpeech =  "Your Answer is Right but something went wrong with counter.Do you want to start over?";//  + MovieTriviaUtility.getdynamicContinue();
			    			ssmloutputSpeech.setSsml("<speak>" + outputSpeech + "</speak>");
				 	        log.info("55555erererererreeeeeeeeeeeeeee");
				 	        log.info("55555erererererreeeeeeeeeeeeeee"+outputSpeech);

			    			return SpeechletResponse.newAskResponse(ssmloutputSpeech,reprompt);  
				 		
			    		}else {
			    			answer = answer + "." ;
				 	        outputSpeech =  "Your answer is Wrong."+ "The correct Answer is " +  answer + " Something went wrong with counter.Do you want to start over ? " ; //+ MovieTriviaUtility.getdynamicContinue();
			    			ssmloutputSpeech.setSsml("<speak>" + outputSpeech + "</speak>");
				 	        log.info("666666erererererreeeeeeeeeeeeeee");
				 	        log.info("666666erererererreeeeeeeeeeeeeee"+outputSpeech);

			    			return SpeechletResponse.newAskResponse(ssmloutputSpeech,reprompt);  
			    		}
				        
		}
		catch (Exception e) {
			log.info(" Exception is :" 	+  e.getMessage());
			 String outputSpeech =  "OOPS, something went wrong with URI connectivity.Do you want to start over?";//  + MovieTriviaUtility.getdynamicContinue();
			 SsmlOutputSpeech ssmloutputSpeech = new SsmlOutputSpeech(); 
			 ssmloutputSpeech.setSsml("<speak>" + outputSpeech + "</speak>");
		      PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Want to start again?");

		        Reprompt reprompt = getReprompt(speech);

			return SpeechletResponse.newAskResponse(ssmloutputSpeech,reprompt);  
		 }
	       		 }
	private void setListOfQuestionAlreadyAskedSessionObject(SpeechletRequestEnvelope<IntentRequest> requestEnvelope,
			Integer newId) {
		Object listOfQAskedToAlexa = requestEnvelope.getSession().getAttribute("listOfQuestionAlreadyAsked");
		List<Integer> ojj = (List)listOfQAskedToAlexa;
		List<Integer> ojj1 = new ArrayList<>();
		if (ojj != null){
			 log.info(" ojj is :" 	+ ojj);
			 log.info(" ojj1 is before:" 	+ ojj1);
			ojj1 = ojj;
			log.info(" ojj1 is after:" 	+ ojj1);
		}
       if (newId != null) {    	  
		   ojj1.add(newId);
		   log.info(" ojj1 is after adding newId" 	+ ojj1);
  
       }
       log.info("set attribute for listOfQuestionsAlreadyAsked" 	+ ojj1);
       requestEnvelope.getSession().setAttribute("listOfQuestionAlreadyAsked", ojj1);
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
