package server.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import wrappers.DocDistWrapper;
import wrappers.TopicDistWrapper;
import maps.DocsInTopicMap;
import maps.TopicsInDocsMap;
import maps.TopicsTotalDistCalculator;

public class MapFunctions {

	private static int selectedTopics = 5;
	private Map<Integer, List<TopicDistWrapper>> topicsInDocsMap;
	private Map<Integer, List<DocDistWrapper>> docsInTopicsMap;
	private List<Double> topicsTotalDistributions;
	
	public MapFunctions() throws IOException {
    	this.topicsInDocsMap = TopicsInDocsMap.generateMap();
    	this.docsInTopicsMap = DocsInTopicMap.generateMap(topicsInDocsMap);
    	this.topicsTotalDistributions = TopicsTotalDistCalculator.calculateTotalDistribution(docsInTopicsMap);
	}
	
	public List<Integer> getRandomDistTopics(List<Integer> history) {
		int current = history.get(history.size() - 1);
		List<Integer> rd = new ArrayList<Integer>();
		List<Integer> currentList = new ArrayList<Integer>();
		
		Random random = new Random();
		double r = 0;
		int rdTopic = 0;
		int rdPoem = 0;
		
		for (; rd.size() < selectedTopics;) {
			r = random.nextDouble();
			rdTopic = normalizeTopic(current, r);
			if (!rd.contains(rdTopic))
				rd.add(rdTopic);
		}
		
		for (int id: rd) {
			rdPoem = getMostRelevant(id, history, currentList);
			currentList.add(rdPoem);
		}
		
		return currentList;
	}
	
	public List<DocDistWrapper> getRandomDistPoems(List<Integer> history) {
		int current = history.get(history.size() - 1);
		List<Integer> foundTopics = new ArrayList<Integer>();
		List<DocDistWrapper> foundPoems = new ArrayList<DocDistWrapper>();
		DocDistWrapper foundPoem = new DocDistWrapper();
		
		Random random = new Random();
		double r = 0;
		int rdTopic = 0;
		int rdPoem = 0;
		
		for (; foundTopics.size() < selectedTopics;) {
			r = random.nextDouble();
			rdTopic = normalizeTopic(current, r);
			if (!foundTopics.contains(rdTopic))
				foundTopics.add(rdTopic);
		}
		
		double total = 0.0;
		for (Integer topicID: foundTopics) {
			total = topicsTotalDistributions.get(topicID);
			do {
				r = random.nextDouble() * total;
				foundPoem = normalizePoem(topicID, r);
				rdPoem = foundPoem.getDocName();
			} while (checkList(foundPoems, rdPoem) || history.contains(rdPoem));
			System.out.println("Adding" + foundPoem.getDocName());
			foundPoems.add(foundPoem);
		}
		System.out.println();
		return foundPoems;
	}
	
	private boolean checkList(List<DocDistWrapper> list, int poemID) {
		for (DocDistWrapper wrapper: list) {
			if (wrapper.getDocName() == poemID)
				return true;
		}
		return false;
	}
	
	public DocDistWrapper normalizePoem(int topicID, double rnd) {
		double count = topicsTotalDistributions.get(topicID);
		for (DocDistWrapper poem: docsInTopicsMap.get(topicID)) {
			count -= poem.getDistribution();
			if (count <= rnd) {
				return poem;
			}
		}
		return null;
	}
	
	public int normalizeTopic(int poemID, double rnd) {
		double count = 1;
		for (TopicDistWrapper topic: topicsInDocsMap.get(poemID)) {
			count -= topic.getDistribution();
			if (count <= rnd) {
				return topic.getTopicId();
			}
		}
		return -1;	
	}
	
	public DocDistWrapper getSimilarID(List<Integer> history) {
    	int current = history.get(history.size() - 1);
    	int topic = findTopTopic(current);
    	DocDistWrapper mS = getMostSimilar(topic, history);
    	return mS;
    }
    
    private int findTopTopic(int poemID) {
    	return topicsInDocsMap.get(poemID).get(0).getTopicId();
    }
    
    private DocDistWrapper getMostSimilar(int topicID, List<Integer> history) {
    	List<DocDistWrapper> topList = docsInTopicsMap.get(topicID);
    	for(DocDistWrapper wrapper: topList) {
    		int id = wrapper.getDocName();
    		if (!history.contains(id))
    			return wrapper;
    	}
    	return null;
    }
    
    private int getMostSimilar(int topicID, List<Integer> history, List<Integer> currentList) {
    	List<DocDistWrapper> topList = docsInTopicsMap.get(topicID);
    	for(DocDistWrapper wrapper: topList) {
    		int id = wrapper.getDocName();
    		if (!history.contains(id) && !currentList.contains(id))
    			return id;
    	}
    	return -1;
    }
    
    private int getMostRelevant(int topicID, List<Integer> history, List<Integer> currentList) {
    	List<DocDistWrapper> topList = docsInTopicsMap.get(topicID);
    	for(DocDistWrapper wrapper: topList) {
    		int id = wrapper.getDocName();
    		if (!history.contains(id) && !currentList.contains(id)) {
    			return id;
    		}
    	}
    	return -1;
    }
    
    private int getRandomDistPoem(int topicID, List<Integer> history, List<Integer> currentList) {
    	List<DocDistWrapper> docList = docsInTopicsMap.get(topicID);
    	for(DocDistWrapper wrapper: docList) {
    		int id = wrapper.getDocName();
    		if (!history.contains(id) && !currentList.contains(id)) {
    			return id;
    		}
    	}
    	return -1;
    }
    
    
    /**
     * RANDOMLY selects a number of topics belonging to the last poem within the history List.
     * Then finds the the most relevant poem for each topic 
     * (discards poem if already occurred within history or new list)
     * 
     * @param List of visited poemIDs
     * @return List of most relevant poemID per topic
     */
    public List<Integer> getNewID(List<Integer> history) {
    	int current = history.get(history.size() - 1);
    	List<Integer> rTopics = getRandomTopics(current);
    	List<Integer> iDs = new ArrayList<Integer>();
    	
    	for (Integer i: rTopics) {
    		iDs.add(getMostSimilar(i, history, iDs));
    	}
    	return iDs;
    }
	
    /**
     * RANDOMLY select a number of topics belonging to a given poem
     * 
     * @param poemID
     * @return List of randomly selected topics
     */
	private List<Integer> getRandomTopics(int poemID) {
		List<TopicDistWrapper> topics = topicsInDocsMap.get(poemID);
		List<Integer> ret = new ArrayList<Integer>();
		List<Integer> randoms = new ArrayList<Integer>();
		int min = 2;
		int max = topics.size() - 1;
		
		Random random = new Random();
		int r = 0;
		
		for (; randoms.size() < selectedTopics;) {
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
