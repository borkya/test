package com.amazon.asksdk.moviedialogue.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.asksdk.moviedialogues.MovieTriviaManager;
import com.amazon.speech.speechlet.services.DirectiveService;

public class MovieTriviaUtility {
	
	public static final int PAGINATION_SIZE = 2;
	private static final Logger log = LoggerFactory.getLogger(MovieTriviaManager.class);
	public static final int DELIMITER_SIZE = 2;
	public static final String SESSION_INDEX = "index";
	public static final String SESSION_TEXT = "text";
	public static final String SLOT_DAY = "day";
	public static final String SLOT_ANSWER = "Answer";
	public static final int SIZE_OF_EVENTS = 10;
	public static final String[] MONTH_NAMES = {
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };
	public DirectiveService directiveService;
    public String sessionId = "";
    public Integer questionNumber =1;
    public static List<Integer> listOfQuestionAsked = new ArrayList<>();
    public static final int DB_RECORD_COUNT = 55;
    

    public static String getdynamicContinue() {
 		List<String> listContinue = new ArrayList<String>();
 		listContinue.add("Do you want to continue ?");
 		listContinue.add("Want to play more?");
 		listContinue.add("Want to hear more ?");
 		listContinue.add("Want to listen more?");
 		listContinue.add("Can we continue?");
 		Random rand = new Random();
 		int  n = rand.nextInt(listContinue.size()  - 1) + 0; //50 is the maximum and the 1 is our minimum 
 		log.info(" Inside getdynamicContinue....: " + listContinue.get(n));
 		return listContinue.get(n);
 		
 	}
    public static String convertClip(String clip){
    	String convertedClip= "<audio src='";
    	return convertedClip = convertedClip + clip + "'/>";
    }
    // Sample Clip format "<audio src='https://s3.amazonaws.com/moviedialogs/AB%40.mp3'/>";
 
}
