package com.CSS590.nemolibapp.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Yangxiao on 3/6/2019.
 */

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
	private String uploadDir;
	
	public String getUploadDir() {
		return uploadDir;
	}
	
	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
}
