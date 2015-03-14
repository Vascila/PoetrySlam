package maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import wrappers.DocDistWrapper;

public class TopicsTotalDistCalculator {

	public static List<Double> calculateTotalDistribution(Map<Integer, List<DocDistWrapper>> docsInTopicsMap) {
		List<Double> totalList = new ArrayList<Double>(Collections.nCopies(docsInTopicsMap.size(), 0.0));
		Double total = 0.0;
		
		for(Integer key: docsInTopicsMap.keySet()) {
			for(DocDistWrapper wrapper: docsInTopicsMap.get(key)) {
				total += wrapper.getDistribution();
			}
			totalList.set(key, total);
			total = 0.0;
		}
		return totalList;
	}
	
}
