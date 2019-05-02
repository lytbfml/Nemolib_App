package com.CSS590.nemolibapp.Services;

import com.CSS590.nemolibapp.Model.ResponseBean;
import edu.uwb.nemolib.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Yangxiao on 3/5/2019.
 */
@Service
public class ComputingService {
	
	public ComputingService() {}
	
	public boolean CalculateNetworkMotif(String fileName, int motifSize, int randGraphCount, boolean directed,
	                                     int option, List<Double> prob, ResponseBean responseBean) {
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
		
		// hard-code probs for now
		List<Double> probs = new LinkedList<>();
		for (int i = 0; i < motifSize - 2; i++) {
			probs.add(1.0);
		}
		probs.add(1.0);
		probs.add(0.1);
		
		// If want to save the name to index map to the file
		targetGraph.write_nametoIndex("Name_Index.txt");
		
		String results = "";
		if (option == 1) {
			results = subGraphRe(targetGraph, motifSize, randGraphCount, prob);
		} else if (option == 2) {
			results = subGraphProF(targetGraph, motifSize, randGraphCount, prob);
		} else if (option == 3) {
			results = subGraphColl(targetGraph, motifSize, randGraphCount, prob);
		}
		
		responseBean.setResults("Running time = " + (System.currentTimeMillis() - time) + "ms\n" + results);
		return true;
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
	private String subGraphRe(Graph targetGraph, int motifSize, int randGraphCount, List<Double> prob) {
		
		SubgraphCount subgraphCount = new SubgraphCount();
		
		// Create a class that will enuerate all subgraphs.
		// If not want do full enumeration, provide probabilities for each tree level
		SubgraphEnumerator targetGraphESU = new ESU(prob);
		
		// Will enumerate all subgraphs and results will be written in subgraphCount
		TargetGraphAnalyzer targetGraphAnalyzer =
				new TargetGraphAnalyzer(targetGraphESU, subgraphCount);
		
		
		// The frequency will be represented as percentage (relative frequency)
		Map<String, Double> targetLabelToRelativeFrequency =
				targetGraphAnalyzer.analyze(targetGraph, motifSize);
		
		System.out.println("targetLabelToRelativeFrequency=" + targetLabelToRelativeFrequency);
		
		// Step 2: generate random graphs
		System.out.println("Generating " + randGraphCount + " random graph...");
		
		// Create enumeration class, and start sampling
		SubgraphEnumerator randESU = new RandESU(prob);
		
		RandomGraphAnalyzer randomGraphAnalyzer = new RandomGraphAnalyzer(randESU, randGraphCount);
		
		// The results are saved to randomLabelToRelativeFrequencies
		Map<String, List<Double>> randomLabelToRelativeFrequencies = randomGraphAnalyzer.analyze(targetGraph, motifSize);
		
		System.out.println("randomLabelToRelativeFrequencies=" + randomLabelToRelativeFrequencies);
		
		//STEP 3: Determine network motifs through statistical analysis
		RelativeFrequencyAnalyzer relativeFrequencyAnalyzer =
				new RelativeFrequencyAnalyzer(randomLabelToRelativeFrequencies, targetLabelToRelativeFrequency);
		
		System.out.println(relativeFrequencyAnalyzer);
		System.out.println("SubraphCount Compete");
		
		return relativeFrequencyAnalyzer.toString();
	}
	
	/**
	 * The second option is "SubgraphProfile", which maps each vertex concentration to each pattern.
	 * The frequency of each pattern is also provided
	 * If the graph or motif size is big, this method is recommended.
	 * To go different option, just comment out all the method from this line until encounter
	 */
	private String subGraphProF(Graph targetGraph, int motifSize, int randGraphCount, List<Double> prob) {
		//If want to provide with Profile
		SubgraphProfile subgraphCount = new SubgraphProfile();
		
		// Create a class that will enuerate all subgraphs.
		// If not want do full enumeration, provide probabilities for each tree level
		SubgraphEnumerator targetGraphESU = new ESU(prob);
		
		// Will enumerate all subgraphs and results will be written in subgraphCount
		TargetGraphAnalyzer targetGraphAnalyzer = new TargetGraphAnalyzer(targetGraphESU, subgraphCount);
		
		// The frequency will be represented as percentage (relative frequency)
		Map<String, Double> targetLabelToRelativeFrequency = targetGraphAnalyzer.analyze(targetGraph, motifSize);
		
		System.out.println("targetLabelToRelativeFrequency=" + targetLabelToRelativeFrequency);
		
		// Step 2: generate random graphs
		System.out.println("Generating " + randGraphCount + " random graph...");
		
		// Create enumeration class, and start sampling
		SubgraphEnumerator randESU = new RandESU(prob);
		
		RandomGraphAnalyzer randomGraphAnalyzer =
				new RandomGraphAnalyzer(randESU, randGraphCount);
		
		// The results are saved to randomLabelToRelativeFrequencies
		Map<String, List<Double>> randomLabelToRelativeFrequencies = randomGraphAnalyzer.analyze(targetGraph, motifSize);
		
		System.out.println("randomLabelToRelativeFrequencies=" + randomLabelToRelativeFrequencies);
		// STEP 3: Determine network motifs through statistical analysis
		RelativeFrequencyAnalyzer relativeFrequencyAnalyzer =
				new RelativeFrequencyAnalyzer(randomLabelToRelativeFrequencies,
						targetLabelToRelativeFrequency);
		System.out.println(relativeFrequencyAnalyzer);
		
		// Display the nemoprofile result based on pvalue < 0.05.
		// If the file name is null, "NemoProfile.txt" is default.
		// If the nametoindex map is not given (given as null), then the nemoprofile provide as index instead of original vertex name
		SubgraphProfile built = NemoProfileBuilder.buildwithPvalue(subgraphCount,
				relativeFrequencyAnalyzer, 0.05, "NemoProfile.txt", targetGraph.getNameToIndexMap());
		
		// Print the result in screen
		System.out.println("NemoProfile=\n" + built + "\n");
		
		System.out.println("SubgraphProfile Compete");
		
		return relativeFrequencyAnalyzer.toString();
	}
	
	/**
	 * The last option is "SubgraphCollection", which write all instances of each pattern, and frequency of each
	 * pattern
	 * It is recormended to use for moderate graph size or motif size.
	 * To go different option, just comment out all the method from this line until encounter 33333333333333333333333333333333333333333
	 */
	private String subGraphColl(Graph targetGraph, int motifSize, int randGraphCount, List<Double> prob) {
		
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
		
		System.out.println("targetLabelToRelativeFrequency=" + targetLabelToRelativeFrequency);
		
		// Step 2: generate random graphs
		System.out.println("Generating " + randGraphCount + " random graph...");
		
		// Create enumeration class, and start sampling
		SubgraphEnumerator randESU = new RandESU(prob);
		
		RandomGraphAnalyzer randomGraphAnalyzer = new RandomGraphAnalyzer(randESU, randGraphCount);
		
		// The results are saved to randomLabelToRelativeFrequencies
		Map<String, List<Double>> randomLabelToRelativeFrequencies = randomGraphAnalyzer.analyze(targetGraph, motifSize);
		
		System.out.println("randomLabelToRelativeFrequencies=" + randomLabelToRelativeFrequencies);
		
		// STEP 3: Determine network motifs through statistical analysis
		RelativeFrequencyAnalyzer relativeFrequencyAnalyzer = new RelativeFrequencyAnalyzer(randomLabelToRelativeFrequencies, targetLabelToRelativeFrequency);
		System.out.println(relativeFrequencyAnalyzer);
		
		// This is optional, if the user want to collect all subgraphs with canonical label in a file
		// Write the nemocollection result based on zscore thresh (anything with >=2 is collected) .
		System.out.println("Writing network motif instances to NemoCollection file");
		NemoCollectionBuilder.buildwithZScore(subgraphCount, relativeFrequencyAnalyzer,
				2, "NemoCollectionZscore.txt", targetGraph.getNameToIndexMap());
		
		// Write the nemocollection result based on pvalue thresh (anything with <0.05 is collected) .
		NemoCollectionBuilder.buildwithPvalue(subgraphCount, relativeFrequencyAnalyzer,
				0.05, "NemoCollectionPValue.txt", targetGraph.getNameToIndexMap());
		
		// Write the subgraph collection
		NemoCollectionBuilder.buildwithPvalue(subgraphCount, relativeFrequencyAnalyzer,
				1, "SubgraphCollection.txt", targetGraph.getNameToIndexMap());
		
		System.out.println("NemoCollection Compete");
		
		return relativeFrequencyAnalyzer.toString();
	}
}
