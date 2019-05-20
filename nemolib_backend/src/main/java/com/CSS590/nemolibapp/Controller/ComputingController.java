package com.CSS590.nemolibapp.Controller;

import com.CSS590.nemolibapp.Model.NetworkMotifResponse;
import com.CSS590.nemolibapp.Model.ResponseBean;
import com.CSS590.nemolibapp.Services.ComputingService;
import com.CSS590.nemolibapp.Services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


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
	public ResponseBean getNetworkMotif(@RequestParam(name = "motifSize") int motifSize,
	                                    @RequestParam(name = "randSize") int randGraph,
	                                    @RequestParam(name = "directed") int directed,
	                                    @RequestParam(name = "file") MultipartFile file,
	                                    @RequestParam(value = "prob[]") Double[] prob) {
		if (file == null || file.isEmpty()) {
			NetworkMotifResponse.initWithMessage("File is empty!");
			return NetworkMotifResponse.initWithMessage("Error! File is empty!");
		}
		ResponseBean responseBean;
		responseBean = new NetworkMotifResponse(motifSize, randGraph, directed == 1, file.getOriginalFilename());
		Path filePath = storageService.storeFile(file);
		
		if (filePath == null) {
			responseBean.setResults(
					"Error, cannot upload file: " + file.getName());
			return responseBean;
		}
		
		if (prob == null) {
			responseBean.setResults(
					"Error, please enter probabilities!");
			return responseBean;
		}
		
		String x = Paths.get(".").toAbsolutePath().normalize().toString();
		List<Double> probs = Arrays.asList(prob);
		boolean success = cService.CalculateNetworkMotif(filePath.toString(), motifSize, randGraph,
				directed == 1, probs, responseBean);
		return responseBean;
	}
	
	@CrossOrigin()
	@RequestMapping(value = "nemoprofile", method = RequestMethod.POST)
	public ResponseBean getNemoProfile(@RequestParam(name = "motifSize") int motifSize,
	                                   @RequestParam(name = "randSize") int randGraph,
	                                   @RequestParam(name = "directed") int directed,
	                                   @RequestParam(name = "file") MultipartFile file,
	                                   @RequestParam(value = "prob[]") Double[] prob) {
		if (file == null || file.isEmpty()) {
			NetworkMotifResponse.initWithMessage("File is empty!");
			return NetworkMotifResponse.initWithMessage("Error! File is empty!");
		}
		ResponseBean responseBean;
		responseBean = new NetworkMotifResponse(motifSize, randGraph, directed == 1, file.getOriginalFilename());
		Path filePath = storageService.storeFile(file);
		
		if (filePath == null) {
			responseBean.setResults(
					"Error, cannot upload file: " + file.getName());
			return responseBean;
		}
		
		if (prob == null) {
			responseBean.setResults(
					"Error, please enter probabilities!");
			return responseBean;
		}
		
		String x = Paths.get(".").toAbsolutePath().normalize().toString();
		List<Double> probs = Arrays.asList(prob);
		boolean success = cService.CalculateNemoProfile(filePath.toString(), motifSize, randGraph,
				directed == 1, probs, responseBean);
		return responseBean;
	}
	
	@CrossOrigin()
	@RequestMapping(value = "nemocollect", method = RequestMethod.POST)
	public ResponseBean getNemoCollection(@RequestParam(name = "motifSize") int motifSize,
	                                      @RequestParam(name = "randSize") int randGraph,
	                                      @RequestParam(name = "directed") int directed,
	                                      @RequestParam(name = "file") MultipartFile file,
	                                      @RequestParam(value = "prob[]") Double[] prob) {
		if (file == null || file.isEmpty()) {
			NetworkMotifResponse.initWithMessage("File is empty!");
			return NetworkMotifResponse.initWithMessage("Error! File is empty!");
		}
		ResponseBean responseBean;
		responseBean = new NetworkMotifResponse(motifSize, randGraph, directed == 1, file.getOriginalFilename());
		Path filePath = storageService.storeFile(file);
		
		if (filePath == null) {
			responseBean.setResults(
					"Error, cannot upload file: " + file.getName());
			return responseBean;
		}
		
		if (prob == null) {
			responseBean.setResults(
					"Error, please enter probabilities!");
			return responseBean;
		}
		
		String x = Paths.get(".").toAbsolutePath().normalize().toString();
		List<Double> probs = Arrays.asList(prob);
		boolean success = cService.CalculateNemoCollection(filePath.toString(), motifSize, randGraph,
				directed == 1, probs, responseBean);
		return responseBean;
	}
}
