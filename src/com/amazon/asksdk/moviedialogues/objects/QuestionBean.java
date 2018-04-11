package com.amazon.asksdk.moviedialogues.objects;

public class QuestionBean {
	
	private Integer id;
	private String cliphere;
	private String question;
	private String answer;
	private Boolean questionemptyList; 
	
	public Boolean getQuestionemptyList() {
		return questionemptyList;
	}
	public void setQuestionemptyList(Boolean questionemptyList) {
		this.questionemptyList = questionemptyList;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCliphere() {
		return cliphere;
	}
	public void setCliphere(String cliphere) {
		this.cliphere = cliphere;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
