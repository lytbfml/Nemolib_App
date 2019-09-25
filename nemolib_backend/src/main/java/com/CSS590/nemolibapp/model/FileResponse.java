package com.CSS590.nemolibapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yangxiao Wang on 7/28/2019
 */
public class FileResponse extends NetworkMotifResponse {
	
	private String message;
	private String results;
	private List<String> filename;
	private List<String> url;
	// private long size;
	
	public FileResponse(int motifSize, int randSize, boolean directed, String fileName) {
		super(motifSize, randSize, directed, fileName);
		this.filename = new ArrayList<>();
	}
	
	public FileResponse() {
	}
	
	public List<String> getFilename() {
		return filename;
	}
	
	public void setFilename(List<String> filenames) {
		this.filename = filenames;
	}
	
	public void addFilename(String filename) {
		this.filename.add(filename);
	}
	
	public List<String> getUrl() {
		return url;
	}
	
	public void setUrl(List<String> url) {
		this.url = url;
	}
	
	// public long getSize() {
	// 	return size;
	// }
	//
	// public void setSize(long size) {
	// 	this.size = size;
	// }
}
