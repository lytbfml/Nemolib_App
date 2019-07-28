package com.CSS590.nemolibapp;

import com.CSS590.nemolibapp.configure.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Yangxiao on 3/5/2019.
 */
@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class NemolibAppMain {
	public static void main(String[] args) {
		SpringApplication.run(NemolibAppMain.class, args);
	}
}