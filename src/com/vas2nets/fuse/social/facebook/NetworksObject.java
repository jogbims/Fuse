package com.vas2nets.fuse.social.facebook;

public class NetworksObject {
	private String title;
	private int icons;
	private String status;
	
	public NetworksObject(String title, int icons, String status){
		this.title = title;
		this.icons = icons;
		this.status = status;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIcons() {
		return icons;
	}
	public void setIcons(int icons) {
		this.icons = icons;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
