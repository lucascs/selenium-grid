package com.thoughtworks.selenium.grid.hub;

public class Hub {
	
	private final String url;

	public Hub(String url) {
		this.url = url;
		
	}

	public String url() {
		return this.url;
	}

}
