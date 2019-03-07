package com.CSS590.nemolibapp.Util;

/**
 * @author Yangxiao on 3/6/2019.
 */
public class NoSuchFileFoundException extends RuntimeException {
	
	public NoSuchFileFoundException(String message) {
		super(message);
	}
	
	public NoSuchFileFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
