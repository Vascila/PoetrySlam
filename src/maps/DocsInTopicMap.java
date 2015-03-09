package maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import wrappers.DocDistWrapper;
import wrappers.TopicDistWrapper;

public class DocsInTopicMap {

	private static final String collectionDir = "output/output_csv/DocsInTopics.csv";
	
	public static Map<Integer, List<DocDistWrapper>> generateMap() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(collectionDir));
        scanner.useDelimiter(",");
        
        Map<Integer, List<DocDistWrapper>> map = new HashMap<Integer, List<DocDistWrapper>>();
        String line = "";
        List<Integer> tokens = new ArrayList<Integer>();
        
        // Skip Header line
        scanner.nextLine();
        
        while(scanner.hasNext()){
        	line = scanner.next();
        	for (String str: line.split(";")) {
        		if (str.contains("\n")) {
        			String[] temp = str.split("\\n");
        			for (String s: temp)
        				tokens.add(Integer.parseInt(s));
        		}
        		else
        			tokens.add(Integer.parseInt(str));
        	}
        }
        scanner.close();
        
        int current = 0;
        List<DocDistWrapper> list = new ArrayList<DocDistWrapper>();
        
        
        for(int i = 0; i < tokens.size(); i += 4) {
        	if (current == tokens.get(i))
        		list.add(new DocDistWrapper(tokens.get(i + 3), 0));
        	else {
        		map.put(current, list);
        		current = tokens.get(i);
        		list = new ArrayList<DocDistWrapper>();
        		list.add(new DocDistWrapper(tokens.get(i + 3), 0));
        	}
        }
        map.put(current, list);
        return map;
    }
	
	public static Map<Integer, List<DocDistWrapper>> generateMap(Map<Integer, List<TopicDistWrapper>> topicsInDocsMap) throws FileNotFoundException {
		Map<Integer, List<DocDistWrapper>> map = new HashMap<Integer, List<DocDistWrapper>>();
		List<DocDistWrapper> current = new ArrayList<DocDistWrapper>();
		map = generateMap();
		
		for(int docName: topicsInDocsMap.keySet()) {
			List<TopicDistWrapper> tempList = topicsInDocsMap.get(docName);
			for(TopicDistWrapper wrapper: tempList) {
				current = map.get(wrapper.getTopicId());
				for(DocDistWrapper docDistWrapper: current) {
					if (docDistWrapper.getDocName() == docName) {
						docDistWrapper.setDistribution(wrapper.getDistribution());
						break;
					}
						
				}
				map.put(wrapper.getTopicId(), current);
			}
		}
		return map;
	}
}
