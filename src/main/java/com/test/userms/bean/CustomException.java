package com.test.userms.bean;

import java.util.Date;

public class CustomException {
	private Date time;
	private String message;
	private String description;
	
	public CustomException() {
		super();
	}
	public CustomException(Date time, String message, String description) {
		super();
		this.time = time;
		this.message = message;
		this.description = description;
	}
	public Date getTime() {
		return time;
	}
	public String getMessage() {
		return message;
	}
	public String getDescription() {
		return description;
	}
	
	
}
