package com.CSS590.nemolibapp.Controller;

import com.CSS590.nemolibapp.Services.ComputingService;
import com.CSS590.nemolibapp.Services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * @author Yangxiao on 3/5/2019.
 */
@RestController
@RequestMapping("compute")
public class ComputingController {
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ComputingService cService;
	
	@RequestMapping(value = "networkmotif", method = RequestMethod.POST)
	public String getNetworkMotif(@RequestParam(name = "file") MultipartFile file,
	                              @RequestParam(name = "motifSize") int motifSize,
	                              @RequestParam(name = "randGraphCount") int randGraph) {
		if (file.isEmpty()) {
			
			return "Empty file";
		}
		Path filePath = storageService.storeFile(file);
		if (filePath == null) {
			return "Error, cannot upload file: " + file.getName();
		}
		String results = cService.CalculateNetworkMotif(filePath.toString(), motifSize, randGraph);
		return "Upload File: " + filePath.getFileName() + "<br />SubGraph size: " + motifSize +
				"<br />Number of random networks: " + randGraph + "<br />" + results + "\n";
	}
}
