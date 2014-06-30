package com.vas2nets.fuse.contact;

public class PhoneContact {
	private String id;
	private String name;
	private String photo;
	
	public PhoneContact(String id, String name, String photo){
		this.setId(id);
		this.setName(name);
		this.setPhoto(photo);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	
	

}
