package maps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wrappers.TopicDistWrapper;

public class TopicsInDocsMap {
	
	private static final String collectionDir = "output/output_csv/TopicsInDocs.csv";
	
	public static Map<Integer, List<TopicDistWrapper>> generateMap() throws IOException {
        
        Map<Integer, List<TopicDistWrapper>> map = new HashMap<Integer, List<TopicDistWrapper>>();
        List<TopicDistWrapper> tokens = new ArrayList<TopicDistWrapper>();
        
        
        try(BufferedReader br = new BufferedReader(new FileReader(collectionDir))) {
        	
        	// Skip Header line
        	br.readLine();
        	
            for(String line; (line = br.readLine()) != null; ) {
            	List<String> items = Arrays.asList(line.split("\\s*;\\s*"));
            	TopicDistWrapper tD = new TopicDistWrapper();
            	for(int i = 2; i < items.size(); i += 2) {
            		tD.setTopicId(Integer.parseInt(items.get(i)));
            		tD.setDistribution(Double.parseDouble(items.get(i + 1)));
            		tokens.add(tD);
            		tD = new TopicDistWrapper();
            	}
            	map.put(Integer.parseInt(items.get(1)), tokens);
            	tokens = new ArrayList<TopicDistWrapper>();
            }
        }
        return map;
    }
}
