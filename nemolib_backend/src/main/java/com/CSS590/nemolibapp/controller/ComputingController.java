package com.CSS590.nemolibapp.controller;

import com.CSS590.nemolibapp.model.NetworkMotifResponse;
import com.CSS590.nemolibapp.model.ResponseBean;
import com.CSS590.nemolibapp.services.ComputingService;
import com.CSS590.nemolibapp.services.StorageService;
import com.CSS590.nemolibapp.support.ResourceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
	
	final Logger logger = LogManager.getLogger(ComputingController.class);
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ComputingService cService;
	
	@CrossOrigin()
	@ExceptionHandler(ResourceException.class)
	@RequestMapping(value = "networkmotif", method = RequestMethod.POST)
	public ResponseBean getNetworkMotif(@RequestParam(name = "motifSize") int motifSize,
	                                    @RequestParam(name = "randSize") int randGraph,
	                                    @RequestParam(name = "directed") int directed,
	                                    @RequestParam(name = "file") MultipartFile file,
	                                    @RequestParam(value = "prob[]") Double[] prob) {
		logger.trace("Start getNetworkMotif, file name " + file.getOriginalFilename());
		
		if (file == null || file.isEmpty()) {
			throw new ResourceException(HttpStatus.BAD_REQUEST, "Error! File is empty");
		}
		ResponseBean responseBean;
		responseBean = new NetworkMotifResponse(motifSize, randGraph, directed == 1, file.getOriginalFilename());
		Path filePath = storageService.storeFile(file);
		
		if (filePath == null) {
			responseBean.setResults("Error, cannot upload file: " + file.getName());
			throw new ResourceException(HttpStatus.BAD_REQUEST, "Error, cannot upload file" + file.getOriginalFilename());
		}
		
		String x = Paths.get(".").toAbsolutePath().normalize().toString();
		List<Double> probs = Arrays.asList(prob);
		boolean success = cService.CalculateNetworkMotif(filePath.toString(), motifSize, randGraph,
				directed == 1, probs, responseBean);
		logger.trace("Return result");
		return responseBean;
	}
	
	@CrossOrigin()
	@RequestMapping(value = "nemoprofile", method = RequestMethod.POST)
	public ResponseBean getNemoProfile(@RequestParam(name = "uuid") String uuid,
	                                   @RequestParam(name = "motifSize") int motifSize,
	                                   @RequestParam(name = "randSize") int randGraph,
	                                   @RequestParam(name = "directed") int directed,
	                                   @RequestParam(name = "file") MultipartFile file,
	                                   @RequestParam(value = "prob[]") Double[] prob) {
		logger.trace("getNemoProfile");
		logger.debug("user with id: " + uuid);
		
		if (file == null || file.isEmpty()) {
			throw new ResourceException(HttpStatus.BAD_REQUEST, "Error! File is empty");
		}
		ResponseBean responseBean;
		responseBean = new NetworkMotifResponse(motifSize, randGraph, directed == 1, file.getOriginalFilename());
		Path filePath = storageService.storeFile(file);
		
		if (filePath == null) {
			responseBean.setResults("Error, cannot upload file: " + file.getName());
			return responseBean;
		}
		
		if (prob == null) {
			responseBean.setResults("Error, please enter probabilities!");
			return responseBean;
		}
		
		String x = Paths.get(".").toAbsolutePath().normalize().toString();
		List<Double> probs = Arrays.asList(prob);
		boolean success = cService.CalculateNemoProfile(filePath.toString(), motifSize, randGraph,
				directed == 1, probs, responseBean);
		logger.trace("Return result");
		return responseBean;
	}
	
	@CrossOrigin()
	@RequestMapping(value = "nemocollect", method = RequestMethod.POST)
	public ResponseBean getNemoCollection(@RequestParam(name = "uuid") String uuid,
	                                      @RequestParam(name = "motifSize") int motifSize,
	                                      @RequestParam(name = "randSize") int randGraph,
	                                      @RequestParam(name = "directed") int directed,
	                                      @RequestParam(name = "file") MultipartFile file,
	                                      @RequestParam(value = "prob[]") Double[] prob) {
		logger.trace("getNemoCollection");
		logger.debug("user with id: " + uuid);
		
		if (file == null || file.isEmpty()) {
			NetworkMotifResponse.initWithMessage("File is empty!");
			return NetworkMotifResponse.initWithMessage("Error! File is empty!");
		}
		ResponseBean responseBean;
		responseBean = new NetworkMotifResponse(motifSize, randGraph, directed == 1, file.getOriginalFilename());
		Path filePath = storageService.storeFile(file);
		
		if (filePath == null) {
			responseBean.setResults("Error, cannot upload file: " + file.getName());
			return responseBean;
		}
		
		if (prob == null) {
			responseBean.setResults("Error, please enter probabilities!");
			return responseBean;
		}
		
		String x = Paths.get(".").toAbsolutePath().normalize().toString();
		List<Double> probs = Arrays.asList(prob);
		boolean success = cService.CalculateNemoCollection(filePath.toString(), motifSize, randGraph,
				directed == 1, probs, responseBean);
		logger.trace("Return result");
		return responseBean;
	}
	
	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = storageService.loadAsResource(fileName);
		
		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}
		
		// Fallback to the default content type if type could not be determined
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
