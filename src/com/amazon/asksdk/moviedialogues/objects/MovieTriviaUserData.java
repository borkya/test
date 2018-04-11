package com.amazon.asksdk.moviedialogues.objects;

import java.util.List;

public class MovieTriviaUserData {
	
	private List<Integer> questionid;
	public List<Integer> getquestionid() {
		return questionid;
	}

	public void setquestionid(List<Integer> questionid) {
		this.questionid = questionid;
	}
	
	@Override
    public String toString() {
        return "[MovieTriviaUserData id: " + questionid + "]";
    }

	

}
