package com.amazon.asksdk.moviedialogues.objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName="MovieTrivia2")
public class MovieTableMapper {
	
	private Integer Id;
	private String clip;
    private String Answer1;
    private String Answer2;
    private String Question1;
    private String Question2;
	
	@DynamoDBHashKey(attributeName="Id")  
    public  Integer getId() {
		return Id;
	}
	public void setId( Integer Id) {
		this.Id = Id;
	}
	
	 @DynamoDBAttribute(attributeName="clip")  
	public String getClip() {
		return clip;
	}	
	public void setClip(String clip) {
		this.clip = clip;
	}
	
	@DynamoDBAttribute(attributeName="Answer1") 
	public String getAnswer1() {
		return Answer1;
	}
	public void setAnswer1(String Answer1) {
		this.Answer1 = Answer1;
	}
	
	@DynamoDBAttribute(attributeName="Answer2") 
	public String getAnswer2() {
		return Answer2;
	}
	public void setAnswer2(String Answer2) {
		this.Answer2 = Answer2;
	}
	
	@DynamoDBAttribute(attributeName="Question1") 
	public String getQuestion1() {
		return Question1;
	}
	public void setQuestion1(String Question1) {
		this.Question1 = Question1;
	}
	
	@DynamoDBAttribute(attributeName="Question2") 
	public String getQuestion2() {
		return Question2;
	}
	public void setQuestion2(String Question2) {
		this.Question2 = Question2;
	}
}
