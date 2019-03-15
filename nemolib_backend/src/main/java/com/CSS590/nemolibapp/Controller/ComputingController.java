package com.CSS590.nemolibapp.Controller;

import com.CSS590.nemolibapp.Model.NetworkMotifBean;
import com.CSS590.nemolibapp.Model.NetworkMotifResponse;
import com.CSS590.nemolibapp.Model.ResponseBean;
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
	
	@CrossOrigin()
	@RequestMapping(value = "networkmotif", method = RequestMethod.POST)
	public NetworkMotifResponse getNetworkMotif(@RequestParam(name = "motifSize") int motifSize,
	                              @RequestParam(name = "randSize") int randGraph,
	                              @RequestParam(name = "file") MultipartFile file) {
		if (file == null || file.isEmpty()) {
			NetworkMotifResponse.initWithMessage("File is empty!");
			return NetworkMotifResponse.initWithMessage("Error! File is empty!");
		}
		ResponseBean responseBean;
		responseBean = new NetworkMotifResponse(motifSize, randGraph, file.getName());
		Path filePath = storageService.storeFile(file);
		
		if (filePath == null) {
			responseBean.setResults(
					"Error, cannot upload file: " + file.getName());
			return null;
		}
		
		// String results = cService.CalculateNetworkMotif(filePath.toString(), motifSize, randGraph);
		System.out.println("MoSize: " + motifSize + " random networks: ");
		return null;
	}
}
