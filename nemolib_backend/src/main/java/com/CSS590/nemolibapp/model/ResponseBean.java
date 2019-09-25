package com.CSS590.nemolibapp.model;

/**
 * @author Yangxiao on 3/14/2019.
 */

public interface ResponseBean {
	
	void setMessage(String message);
	
	String getMessage();
	
	void setResults(String results);
	
	default void setRes(long time, String relaFreqAna) {
		setResults("Running time = " + (System.currentTimeMillis() - time) + "ms\n" + relaFreqAna);
	}
	
	String getResults();
	
	void setOptional(String optional);
	
	String getOptional();
}
