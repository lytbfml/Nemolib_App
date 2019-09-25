package com.CSS590.nemolibapp.services;

import com.CSS590.nemolibapp.configure.FileStorageProperties;
import com.CSS590.nemolibapp.model.FileResponse;
import com.CSS590.nemolibapp.model.ResponseBean;
import edu.uwb.nemolib.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Yangxiao on 3/5/2019.
 */
@Service
public class ComputingService {
	
	private final Logger logger = LogManager.getLogger(ComputingService.class);
	private final Path dirPath;
	private final String dirPathSep;
	
	public ComputingService(FileStorageProperties fileStorageProperties) {
		this.dirPath = Paths.get(fileStorageProperties.getWorkDir()).toAbsolutePath().normalize();
		this.dirPathSep = this.dirPath.getFileName() + File.separator;
		logger.debug("Current workdir: " + this.dirPath);
	}
	
	/**
	 * Provide three output options from here.
	 * Subgraph results can be provided into three options.
	 * 1. SubgraphCount: provide frequency for each pattern
	 * 2. SubgraphProfile: Provide frequency as well as the pattern's concentration on each vertex
	 * 3. SubgraphCollection: Provide frequency as well as the instances of each pattern written in the filename given
	 * <p>
	 * The first option is "SubgraphCount", which provides the frequencies of each pattern.
	 * If the graph or motif size is big, this method is recommended.
	 * To go different option, just comment out all the method from this line until encounter
	 */
	public boolean CalculateNetworkMotif(final String fileName, final int motifSize, final int randGraphCount,
	                                     final boolean directed, final List<Double> prob, final ResponseBean responseBean) {
		final long time = System.currentTimeMillis();
		logger.info("Start CalculateNetworkMotif");
		
		if (motifSize < 3) {
			System.err.println("Motif size must be 3 or larger");
			responseBean.setResults("Motif size must be 3 or larger");
			return false;
		}
		logger.debug("Parsing target graph...");
		final Graph targetGraph;
		try {
			targetGraph = GraphParser.parse(fileName, directed);
		} catch (IOException e) {
			System.err.println("Could not process " + fileName);
			System.err.println(e.getMessage());
			responseBean.setResults("Could not process " + fileName);
			return false;
		}
		
		SubgraphCount subgraphCount = new SubgraphCount();
		SubgraphEnumerator targetGraphESU = new ESU(prob);
		
		TargetGraphAnalyzer trgtGraphAnalyzer = new TargetGraphAnalyzer(targetGraphESU, subgraphCount);
		
		Map<String, Double> tgtFreqMap = trgtGraphAnalyzer.analyze(targetGraph, motifSize);
		
		logger.debug("Target label to relative frequency: " + tgtFreqMap);
		logger.debug("Step 2: Generating " + randGraphCount + " random graph...");
		SubgraphEnumerator randESU = new RandESU(prob);
		logger.debug("Random graph analyze...");
		RandomGraphAnalyzer randGraphAnalyzer = new RandomGraphAnalyzer(randESU, randGraphCount);
		Map<String, List<Double>> randFreqMap = randGraphAnalyzer.analyze(targetGraph, motifSize);
		
		logger.debug("Step 3: Determine network motifs through statistical analysis...");
		RelativeFrequencyAnalyzer relativeFreqAnalyzer = new RelativeFrequencyAnalyzer(randFreqMap, tgtFreqMap);
		// setRes(responseBean, time, relativeFreqAnalyzer.toString());
		responseBean.setRes(time, relativeFreqAnalyzer.toString());
		return true;
	}
	
	/**
	 * The second option is "SubgraphProfile", which maps each vertex concentration to each pattern.
	 * The frequency of each pattern is also provided
	 * If the graph or motif size is big, this method is recommended.
	 * To go different option, just comment out all the method from this line until encounter
	 */
	public List<String> CalculateNemoProfile(String uuid, String fileName, int motifSize, int randGraphCount,
	                                         boolean directed, List<Double> prob, FileResponse responseBean) {
		logger.info("Start CalculateNemoProfile");
		final long time = System.currentTimeMillis();
		
		if (motifSize < 3) {
			System.err.println("Motif size must be 3 or larger");
			responseBean.setResults("Motif size must be 3 or larger");
			return null;
		}
		
		logger.debug("Parsing target graph...");
		Graph targetGraph;
		try {
			targetGraph = GraphParser.parse(fileName, directed);
		} catch (IOException e) {
			System.err.println("Could not process " + fileName);
			System.err.println(e.getMessage());
			responseBean.setResults("Could not process " + fileName);
			return null;
		}
		
		SubgraphProfile subgraphCount = new SubgraphProfile();
		SubgraphEnumerator targetGraphESU = new ESU(prob);
		
		TargetGraphAnalyzer trgtGraphAnalyzer = new TargetGraphAnalyzer(targetGraphESU, subgraphCount);
		Map<String, Double> tgtFreqMap = trgtGraphAnalyzer.analyze(targetGraph, motifSize);
		logger.debug("Target label to relative frequency: " + tgtFreqMap);
		logger.debug("Step 2: Generating " + randGraphCount + " random graph...");
		SubgraphEnumerator randESU = new RandESU(prob);
		logger.debug("Random graph analyze...");
		RandomGraphAnalyzer randGraphAnalyzer = new RandomGraphAnalyzer(randESU, randGraphCount);
		
		Map<String, List<Double>> randFreqMap = randGraphAnalyzer.analyze(targetGraph, motifSize);
		// logger.debug("random Label To Relative Frequencies=" + randFreqMap);
		logger.debug("Step 3: Determine network motifs through statistical analysis...");
		RelativeFrequencyAnalyzer relativeFreqAnalyzer = new RelativeFrequencyAnalyzer(randFreqMap, tgtFreqMap);
		
		String resFile1 = uuid + "_nemoProfile.txt";
		String resFile2 = uuid + "_subProfile.txt";
		logger.debug("Building results based on pvalue < 0.05 " + resFile1);
		NemoProfileBuilder.buildwithPvalue(subgraphCount, relativeFreqAnalyzer,
				0.05, this.dirPathSep + resFile1, targetGraph.getNameToIndexMap());
		
		logger.debug("Building results based on pvalue = 1.0 " + resFile2);
		try {
			NemoProfileBuilder.buildwithPvalue(subgraphCount, relativeFreqAnalyzer,
					1.0, this.dirPathSep + resFile2, targetGraph.getNameToIndexMap());
		} catch (Exception ex) {
			logger.debug("Cannot create subProfile: " + ex.toString());
		}
		
		List<String> resFiles = new ArrayList<>();
		resFiles.add(resFile1);
		resFiles.add(resFile2);
		
		logger.trace("SubgraphProfile Compete");
		responseBean.setRes(time, relativeFreqAnalyzer.toString());
		// setRes(responseBean, time, relativeFreqAnalyzer.toString());
		return setFileRes(resFiles, responseBean);
	}
	
	/**
	 * The last option is "SubgraphCollection", which write all instances of each pattern, and frequency of each
	 * pattern
	 * It is recormended to use for moderate graph size or motif size.
	 * To go different option, just comment out all the method from this line until encounter 33333333333333333333333333333333333333333
	 */
	public List<String> CalculateNemoCollection(String uuid, String fileName, int motifSize, int randGraphCount,
	                                            boolean directed, List<Double> prob, FileResponse responseBean) {
		logger.info("Start CalculateNemoCollection");
		final long time = System.currentTimeMillis();
		
		if (motifSize < 3) {
			System.err.println("Motif size must be 3 or larger");
			responseBean.setResults("Motif size must be 3 or larger");
			return null;
		}
		
		logger.debug("Parsing target graph...");
		Graph targetGraph;
		try {
			targetGraph = GraphParser.parse(fileName, directed);
		} catch (IOException e) {
			System.err.println("Could not process " + fileName);
			System.err.println(e.getMessage());
			responseBean.setResults("Could not process " + fileName);
			return null;
		}
		
		// If want to save the name to index map to the file
		// targetGraph.write_nametoIndex("Name_Index.txt");
		
		// If want to provide collections with instances written "Results.txt" file.
		// SubgraphCollection subgraphCount = new SubgraphCollection("Results.txt");
		
		// Default file name is "SubgraphCollectionResult.txt"
		
		// Create subgraphCount instance which will collect results in SubgraphCollectionG6.txt
		SubgraphCollection subgraphCount = new SubgraphCollection(this.dirPathSep + uuid + "_subG6.txt");
		
		SubgraphEnumerator targetGraphESU = new ESU(prob);
		TargetGraphAnalyzer trgtGraphAnalyzer = new TargetGraphAnalyzer(targetGraphESU, subgraphCount);
		Map<String, Double> tgtFreqMap = trgtGraphAnalyzer.analyze(targetGraph, motifSize);
		logger.debug("Target label to relative frequency: " + tgtFreqMap);
		logger.debug("Step 2: Generating " + randGraphCount + " random graph...");
		SubgraphEnumerator randESU = new RandESU(prob);
		RandomGraphAnalyzer randGraphAnalyzer = new RandomGraphAnalyzer(randESU, randGraphCount);
		Map<String, List<Double>> randFreqMap = randGraphAnalyzer.analyze(targetGraph, motifSize);
		
		// logger.debug("random Label To Relative Frequencies=" + randFreqMap);
		logger.debug("Step 3: Determine network motifs through statistical analysis...");
		RelativeFrequencyAnalyzer relativeFreqAnalyzer = new RelativeFrequencyAnalyzer(randFreqMap, tgtFreqMap);
		
		// This is optional, if the user want to collect all subgraphs with canonical label in a file
		// Write the nemocollection result based on zscore thresh (anything with >=2 is collected) .
		// logger.trace("Write the nemocollection result based on zscore thresh (anything with >=2 is collected) .");
		// NemoCollectionBuilder.buildwithZScore(subgraphCount, relativeFreqAnalyzer,
		// 		2.0, "NemoCollectionZscore.txt", targetGraph.getNameToIndexMap());
		
		
		String resFile1 = uuid + "_nemoCollection.txt";
		String resFile2 = uuid + "_subCollection.txt";
		
		logger.trace("Write the nemocollection result based on pvalue thresh (anything with <0.05 is collected).");
		NemoCollectionBuilder.buildwithPvalue(subgraphCount, relativeFreqAnalyzer,
				0.05, this.dirPathSep + resFile1, targetGraph.getNameToIndexMap());
		
		logger.trace("Write the subgraph/nemo collection to " + resFile1 + ", " + resFile2);
		NemoCollectionBuilder.buildwithPvalue(subgraphCount, relativeFreqAnalyzer,
				1.0, this.dirPathSep + resFile2, targetGraph.getNameToIndexMap());
		
		logger.trace("NemoCollection Compete");
		
		List<String> resFiles = new ArrayList<>();
		resFiles.add(resFile1);
		resFiles.add(resFile2);
		
		responseBean.setRes(time, relativeFreqAnalyzer.toString());
		// setRes(responseBean, time, relativeFreqAnalyzer.toString());
		return setFileRes(resFiles, responseBean);
	}
	
	private List<String> setFileRes(List<String> resFileNames, FileResponse responseBean) {
		
		if (resFileNames != null || resFileNames.size() > 0) {
			List<String> files = new ArrayList<>();
			for (int i = 0; i < resFileNames.size(); i++) {
				File file = new File(this.dirPathSep + resFileNames.get(i));
				if (file.exists()) {
					files.add(resFileNames.get(i));
					responseBean.addFilename(resFileNames.get(i));
					// responseBean.setSize(file.length() / 1024);
				}
			}
			
			return files;
		} else {
			return new ArrayList<>();
		}
	}
}
