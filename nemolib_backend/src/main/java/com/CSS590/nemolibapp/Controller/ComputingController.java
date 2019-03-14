package com.CSS590.nemolibapp.Controller;

import com.CSS590.nemolibapp.Model.NetworkMotifBean;
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
	public NetworkMotifBean getNetworkMotif(@RequestBody NetworkMotifBean networkMotifBean) {
		// if (networkMotifBean.getFile().isEmpty()) {
		// 	return "Empty file";
		// }
		// Path filePath = storageService.storeFile(networkMotifBean.getFile());
		// if (filePath == null) {
		// 	return "Error, cannot upload file: " + networkMotifBean.getFile().getName();
		// }
		
		// String results = cService.CalculateNetworkMotif(filePath.toString(), motifSize, randGraph);
		System.out.println("MoSize: " + networkMotifBean.getMotifSize() + " random networks: ");
		return networkMotifBean;
	}
}
