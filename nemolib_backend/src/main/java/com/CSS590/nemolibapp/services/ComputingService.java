package com.CSS590.nemolibapp.services;

import com.CSS590.nemolibapp.model.ResponseBean;
import edu.uwb.nemolib.*;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Yangxiao on 3/5/2019.
 */
@Service
public class ComputingService {
	
	final Logger logger = LogManager.getLogger(ComputingService.class);
	
	public ComputingService() {}
	
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
		
		long time = System.currentTimeMillis();
		logger.info("Start CalculateNetworkMotif");
		
		if (motifSize < 3) {
			System.err.println("Motif size must be 3 or larger");
			responseBean.setResults("Motif size must be 3 or larger");
			return false;
		}
		// parse input graph
		logger.debug("Parsing target graph...");
		final Graph targetGraph;
		try {
			targetGraph = GraphParser.parse(fileName, directed);
		} catch (IOException e) {
			System.err.println("Could not process " + fileName);
			System.err.println(e);
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
		
		responseBean.setResults("Running time = " + (System.currentTimeMillis() - time) + "ms\n" +
				relativeFreqAnalyzer.toString());
		return true;
	}
	
	/**
	 * The second option is "SubgraphProfile", which maps each vertex concentration to each pattern.
	 * The frequency of each pattern is also provided
	 * If the graph or motif size is big, this method is recommended.
	 * To go different option, just comment out all the method from this line until encounter
	 */
	public boolean CalculateNemoProfile(String fileName, int motifSize, int randGraphCount, boolean directed,
	                                    List<Double> prob, ResponseBean responseBean) {
		logger.info("Start CalculateNemoProfile");
		
		long time = System.currentTimeMillis();
		
		if (motifSize < 3) {
			System.err.println("Motif size must be 3 or larger");
			responseBean.setResults("Motif size must be 3 or larger");
			return false;
		}
		
		logger.debug("Parsing target graph...");
		Graph targetGraph = null;
		try {
			targetGraph = GraphParser.parse(fileName, directed);
		} catch (IOException e) {
			System.err.println("Could not process " + fileName);
			System.err.println(e);
			responseBean.setResults("Could not process " + fileName);
			return false;
		}
		
		// If want to save the name to index map to the file
		// targetGraph.write_nametoIndex("Name_Index.txt");
		
		//If want to provide with Profile
		SubgraphProfile subgraphCount = new SubgraphProfile();
		
		// Create a class that will enuerate all subgraphs.
		// If not want do full enumeration, provide probabilities for each tree level
		SubgraphEnumerator targetGraphESU = new ESU(prob);
		
		// Will enumerate all subgraphs and results will be written in subgraphCount
		TargetGraphAnalyzer targetGraphAnalyzer = new TargetGraphAnalyzer(targetGraphESU, subgraphCount);
		
		// The frequency will be represented as percentage (relative frequency)
		Map<String, Double> targetLabelToRelativeFrequency = targetGraphAnalyzer.analyze(targetGraph, motifSize);
		
		// System.out.println("targetLabelToRelativeFrequency=" + targetLabelToRelativeFrequency);
		
		// Step 2: generate random graphs
		// System.out.println("Generating " + randGraphCount + " random graph...");
		
		// Create enumeration class, and start sampling
		SubgraphEnumerator randESU = new RandESU(prob);
		
		RandomGraphAnalyzer randomGraphAnalyzer =
				new RandomGraphAnalyzer(randESU, randGraphCount);
		
		// The results are saved to randomLabelToRelativeFrequencies
		Map<String, List<Double>> randomLabelToRelativeFrequencies = randomGraphAnalyzer.analyze(targetGraph, motifSize);
		
		// System.out.println("randomLabelToRelativeFrequencies=" + randomLabelToRelativeFrequencies);
		
		// STEP 3: Determine network motifs through statistical analysis
		RelativeFrequencyAnalyzer relativeFrequencyAnalyzer =
				new RelativeFrequencyAnalyzer(randomLabelToRelativeFrequencies, targetLabelToRelativeFrequency);
		
		// System.out.println(relativeFrequencyAnalyzer);
		
		// Display the nemoprofile result based on pvalue < 0.05.
		// If the file name is null, "NemoProfile.txt" is default.
		// If the nametoindex map is not given (given as null), then the nemoprofile provide as index instead of original vertex name
		SubgraphProfile built = NemoProfileBuilder.buildwithPvalue(subgraphCount, relativeFrequencyAnalyzer,
				0.05, "NemoProfile.txt", targetGraph.getNameToIndexMap());
		
		// Print the result in screen
		// System.out.println("NemoProfile=\n" + built + "\n");
		//
		// System.out.println("SubgraphProfile Compete");
		
		responseBean.setResults("Running time = " + (System.currentTimeMillis() - time) + "ms\n" +
				relativeFrequencyAnalyzer.toString());
		return true;
	}
	
	/**
	 * The last option is "SubgraphCollection", which write all instances of each pattern, and frequency of each
	 * pattern
	 * It is recormended to use for moderate graph size or motif size.
	 * To go different option, just comment out all the method from this line until encounter 33333333333333333333333333333333333333333
	 */
	public boolean CalculateNemoCollection(String fileName, int motifSize, int randGraphCount, boolean directed,
	                                       List<Double> prob, ResponseBean responseBean) {
		logger.info("Start CalculateNemoCollection");
		
		long time = System.currentTimeMillis();
		
		if (motifSize < 3) {
			System.err.println("Motif size must be 3 or larger");
			responseBean.setResults("Motif size must be 3 or larger");
			return false;
		}
		// parse input graph
		// System.out.println("Parsing target graph...");
		Graph targetGraph = null;
		try {
			targetGraph = GraphParser.parse(fileName, directed);
		} catch (IOException e) {
			System.err.println("Could not process " + fileName);
			System.err.println(e);
			responseBean.setResults("Could not process " + fileName);
			return false;
		}
		
		// If want to save the name to index map to the file
		// targetGraph.write_nametoIndex("Name_Index.txt");
		
		// If want to provide collections with instances written "Results.txt" file.
		// SubgraphCollection subgraphCount = new SubgraphCollection("Results.txt");
		
		// Default file name is "SubgraphCollectionResult.txt"
		
		// Create subgraphCount instance which will collect results in SubgraphCollectionG6.txt
		SubgraphCollection subgraphCount = new SubgraphCollection();
		
		// Create a class that will enuerate all subgraphs.
		// If not want do full enumeration, provide probabilities for each tree level
		SubgraphEnumerator targetGraphESU = new ESU(prob);
		
		// Will enumerate all subgraphs and results will be written in subgraphCount
		TargetGraphAnalyzer targetGraphAnalyzer = new TargetGraphAnalyzer(targetGraphESU, subgraphCount);
		
		// The frequency will be represented as percentage (relative frequency)
		Map<String, Double> targetLabelToRelativeFrequency = targetGraphAnalyzer.analyze(targetGraph, motifSize);
		
		// System.out.println("targetLabelToRelativeFrequency=" + targetLabelToRelativeFrequency);
		
		// Step 2: generate random graphs
		// System.out.println("Generating " + randGraphCount + " random graph...");
		
		// Create enumeration class, and start sampling
		SubgraphEnumerator randESU = new RandESU(prob);
		
		RandomGraphAnalyzer randomGraphAnalyzer = new RandomGraphAnalyzer(randESU, randGraphCount);
		
		// The results are saved to randomLabelToRelativeFrequencies
		Map<String, List<Double>> randomLabelToRelativeFrequencies = randomGraphAnalyzer.analyze(targetGraph, motifSize);
		
		// System.out.println("randomLabelToRelativeFrequencies=" + randomLabelToRelativeFrequencies);
		
		// STEP 3: Determine network motifs through statistical analysis
		RelativeFrequencyAnalyzer relativeFrequencyAnalyzer =
				new RelativeFrequencyAnalyzer(randomLabelToRelativeFrequencies, targetLabelToRelativeFrequency);
		
		// System.out.println(relativeFrequencyAnalyzer);
		
		// This is optional, if the user want to collect all subgraphs with canonical label in a file
		// Write the nemocollection result based on zscore thresh (anything with >=2 is collected) .
		// System.out.println("Writing network motif instances to NemoCollection file");
		
		NemoCollectionBuilder.buildwithZScore(subgraphCount, relativeFrequencyAnalyzer,
				2.0, "NemoCollectionZscore.txt", targetGraph.getNameToIndexMap());
		
		// Write the nemocollection result based on pvalue thresh (anything with <0.05 is collected) .
		NemoCollectionBuilder.buildwithPvalue(subgraphCount, relativeFrequencyAnalyzer,
				0.05, "NemoCollectionPValue.txt", targetGraph.getNameToIndexMap());
		
		// Write the subgraph collection
		NemoCollectionBuilder.buildwithPvalue(subgraphCount, relativeFrequencyAnalyzer,
				1.0, "SubgraphCollection.txt", targetGraph.getNameToIndexMap());
		
		// System.out.println("NemoCollection Compete");
		
		responseBean.setResults("Running time = " + (System.currentTimeMillis() - time) + "ms\n" +
				relativeFrequencyAnalyzer.toString());
		return true;
	}
	
}
