package com.vas2nets.fuse.user;

public class FuseUser {
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String photo;
	private String authKey;
	private String deviceId;
	private String sipId;
	
	
	public FuseUser(String firstName, String lastName, String email, String phoneNumber, String photo, String authKey, String deviceId, String sipId){
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.photo = photo;
		this.authKey = authKey;
		this.deviceId = deviceId;
		this.sipId = sipId;
	}
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getAuthKey() {
		return authKey;
	}
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getSipId() {
		return sipId;
	}
	public void setSipId(String sipId) {
		this.sipId = sipId;
	}
	
	
	
	


}
