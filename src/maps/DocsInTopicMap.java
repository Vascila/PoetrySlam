package maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DocsInTopicMap {

	private static final String collectionDir = "output/output_csv/DocsInTopics.csv";
	
	public static Map<Integer, List<Integer>> generateMap() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(collectionDir));
        scanner.useDelimiter(",");
        
        Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
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
        List<Integer> list = new ArrayList<Integer>();
        
        
        for(int i = 0; i < tokens.size(); i += 4) {
        	if (current == tokens.get(i))
        		list.add(tokens.get(i + 3));
        	else {
        		map.put(current, list);
        		current = tokens.get(i);
        		list = new ArrayList<Integer>();
        		list.add(tokens.get(i + 3));
        	}
        }
        map.put(current, list);
        return map;
    }
	
	public static Map<Integer, List<Integer>> generateMap(Map<Integer, List<TopicDistWrapper>> topicsInDocsMap) throws FileNotFoundException {
		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		map = generateMap();
		
		
		
		return map;
	}
}
