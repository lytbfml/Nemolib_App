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
	
	private static final double P_THRESH = 0.1;
	
	public ComputingService() {}
	
	public boolean CalculateNetworkMotif(String fileName, int motifSize, int randGraphCount,
	                                    List<Double> prob, ResponseBean responseBean) {
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
			targetGraph = GraphParser.parse(fileName);
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
		// todo: switch to randESU
		SubgraphProfile subgraphProfile = new SubgraphProfile();
		SubgraphEnumerator esu = new ESU(prob);
		esu.enumerate(targetGraph, motifSize, subgraphProfile);
		subgraphProfile.label();
		Map<String, Double> targetLabelRelFreqMap = subgraphProfile.getRelativeFrequencies();
		
		RandomGraphAnalyzer rga = new RandomGraphAnalyzer(new RandESU(probs), randGraphCount);
		Map<String, List<Double>> randLabelRelFreqsMap = rga.analyze(targetGraph, motifSize);
		
		RelativeFrequencyAnalyzer rfa =
				new RelativeFrequencyAnalyzer(randLabelRelFreqsMap, targetLabelRelFreqMap);
		
		SubgraphProfile np = NemoProfileBuilder.build(subgraphProfile, rfa, P_THRESH);
		
		responseBean.setResults("Running time = " + (System.currentTimeMillis() - time) +  "ms\n" + rfa.toString());
		responseBean.setOptional(np.toString());
		return true;
	}
	
}
