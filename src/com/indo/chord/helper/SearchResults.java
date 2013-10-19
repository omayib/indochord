package com.indo.chord.helper;

public class SearchResults {
	private int id = 0;
	private String judul = "";
	private String singer = "";

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public void setJudul(String d) {
		this.judul = d;
	}

	public String getJudul() {
		return judul;
	}

	public void setSinger(String name) {
		this.singer = name;
	}

	public String getSinger() {
		return singer;
	}

}