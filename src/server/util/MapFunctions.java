package server.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import maps.DocsInTopicMap;
import maps.TopicDistWrapper;
import maps.TopicsInDocsMap;

public class MapFunctions {

	private static int selectedTopics = 5;
	private Map<Integer, List<TopicDistWrapper>> topicsInDocsMap;
	private Map<Integer, List<Integer>> docsInTopicsMap;
	
	public MapFunctions() throws IOException {
    	this.topicsInDocsMap = TopicsInDocsMap.generateMap();
    	this.docsInTopicsMap = DocsInTopicMap.generateMap(topicsInDocsMap);
	}
	
	public List<Integer> getRandomDist(List<Integer> history) {
		int current = history.get(history.size() - 1);
		List<Integer> rd = new ArrayList<Integer>();
		List<Integer> currentList = new ArrayList<Integer>();
		
		Random random = new Random();
		double r = 0;
		int rdTopic = 0;
		int selectedId = 0;
		
		for (; rd.size() < selectedTopics;) {
			r = random.nextDouble();
			rdTopic = normalizeTopic(current, r);
			if (!rd.contains(rdTopic))
				rd.add(rdTopic);
		}
		
		for (int id: rd) {
			selectedId = getMostSimilar(id, history, currentList);
			currentList.add(selectedId);
		}
		
		return currentList;
	}
	
	public int normalizeTopic(int id, double rnd) {
		double count = 1;
		for (TopicDistWrapper topic: topicsInDocsMap.get(id)) {
			count -= topic.getDistribution();
			if (count <= rnd) {
				return topic.getTopicId();
			}
		}
		return -1;	
	}
	
	public int getSimilarID(List<Integer> history) {
    	int current = history.get(history.size() - 1);
    	int topic = findTopTopic(current);
    	int mS = getMostSimilar(topic, history);
    	return mS;
    }
    
    private int findTopTopic(int poemID) {
    	return topicsInDocsMap.get(poemID).get(0).getTopicId();
    }
    
    private int getMostSimilar(int topicID, List<Integer> history) {
    	List<Integer> topList = docsInTopicsMap.get(topicID);
    	for(int id: topList) {
    		if (!history.contains(id))
    			return id;
    	}
    	return -1;
    }
    
    private int getMostSimilar(int topicID, List<Integer> history, List<Integer> currentList) {
    	List<Integer> topList = docsInTopicsMap.get(topicID);
    	for(int id: topList) {
    		if (!history.contains(id) && !currentList.contains(id))
    			return id;
    	}
    	return -1;
    }
    
    public List<Integer> getNewID(List<Integer> history) {
    	int current = history.get(history.size() - 1);
    	List<Integer> rTopics = getRandomTopics(current);
    	List<Integer> iDs = new ArrayList<Integer>();
    	
    	for (Integer i: rTopics) {
    		iDs.add(getMostSimilar(i, history, iDs));
    	}
    	return iDs;
    }
	
	private List<Integer> getRandomTopics(int id) {
		List<TopicDistWrapper> topics = topicsInDocsMap.get(id);
		List<Integer> ret = new ArrayList<Integer>();
		List<Integer> randoms = new ArrayList<Integer>();
		int min = 2;
		int max = topics.size() - 1;
		
		Random random = new Random();
		int r = 0;
		
		for (int i = 0; randoms.size() < selectedTopics; i++) {
			r = random.nextInt(max - min + 1) + min;
			if (!randoms.contains(r))
				randoms.add(r);
		}
		
		for (Integer j: randoms) {
			ret.add(topics.get(j).getTopicId());
		}
		return ret;
	}
}
