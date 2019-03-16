package com.CSS590.nemolibapp.Services;

import com.CSS590.nemolibapp.Property.FileStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * @author Yangxiao on 3/5/2019.
 */
@Service
public class StorageService {
	private final Path dirPath;
	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public StorageService(FileStorageProperties fileStorageProperties) {
		
		this.dirPath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
		
		try {
			Files.createDirectories(this.dirPath);
		} catch (Exception ex) {
			System.out.println("Could not create the upload directory : " + ex);
		}
	}
	
	public Path storeFile(MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				System.out.println("Sorry! Filename contains invalid path sequence " + fileName);
			}
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.dirPath.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return targetLocation;
		} catch (IOException ex) {
			System.out.println("Could not store file: " + fileName + ". Please try again!\n" + ex);
			return null;
		}
	}
	
	Stream<Path> loadAll() {
		return null;
	}
	
	Path loadFile(String filename) {
		return null;
	}
	
	Resource loadAsResource(String fileName) {
		// try {
		// 	Path filePath = this.dirPath.resolve(fileName).normalize();
		// 	Resource resource = new UrlResource(filePath.toUri());
		// 	if (resource.exists()) {
		// 		return resource;
		// 	} else {
		// 		System.out.println("File not found " + fileName);
		// 	}
		// } catch (MalformedURLException e) {
		// 	System.out.println("File not found " + fileName + "\n" + e);
		// }
		// return null;
		try {
			Path file = dirPath.resolve(fileName);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("FAIL! Cannot load file " + fileName +
						                           ", file does not exist or not readable");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("FAIL! Cannot load file " + fileName);
		}
	}
	
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(dirPath.toFile());
	}
	
}
