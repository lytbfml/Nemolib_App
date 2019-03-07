package com.CSS590.nemolibapp.Util;

/**
 * @author Yangxiao on 3/6/2019.
 */
public class FileStorageException extends RuntimeException {
	
	public FileStorageException(String message) {
		super(message);
	}
	
	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
