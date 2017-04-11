package com.bristlecone.pojoobjects;


public class Person {
	
	private String name;
	private String lastname;
	private String address;
	private String password;
	
	public void setName(String personName){
		name = personName;
	}

	public void setLastName(String personLastName){
		lastname = personLastName;
	}

	public void setAddress(String personAddress){
		address = personAddress;
	}
	
	public void setPassword(String personPassword){
		password = personPassword;
	}
	
	public String getName(){
		return name;
	}
	
	public String getLastName(){
		return lastname;
	}
	
	public String getAddress(){
		return address;
	}
	
	public String getPassword(){
		return password;
	}
}
