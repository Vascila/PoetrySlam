package server.web;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import database.domain.Poem;
import database.domain.PoemLikeWrapper;

public interface WebServiceInterface {

	/**
	 * Method for retrieving all poems from the database
	 * 
	 * @return A list containing all poems
	 */
    @RequestMapping(value = "/getAllPoems", method = RequestMethod.GET)
    @ResponseBody
    public List<Poem> getAllPoems();
    
    /**
     * Returns a poems from the database based on its ID
     * 
     * @param the poem ID
     * @return specified poem
     */
    @RequestMapping(value = "/getPoemByID", method = RequestMethod.POST)
    @ResponseBody
    public Poem getPoemByID(@RequestBody int id);

    /**
     * Saves a poem to the database
     * 
     * @param the poem to save
     * @return boolean indicating success
     */
    @RequestMapping(value = "/savePoem_json", method = RequestMethod.POST)
    @ResponseBody
    public boolean savePoem_JSON( @RequestBody Poem poem);
    
    /**
     * Method to find the most similar poem for a given poem
     * 
     * @param current navigation history
     * @return the most similar poem
     * @throws IOException
     */
    @RequestMapping(value = "/findSimilar", method = RequestMethod.POST)
    @ResponseBody
    public Poem findSimilar( @RequestBody List<Integer> history) throws IOException;
    
    /**
     * Method for retrieving a random distribution of topics based on current viewed poem
     * 
     * @param current navigation history
     * @return List of five poems
     * @throws IOException
     */
    @RequestMapping(value = "/getRandDistTopics", method = RequestMethod.POST)
    @ResponseBody
    public List<Poem> getRandDistTopics( @RequestBody List<Integer> history) throws IOException;
    
    /**
     * Method for retrieving a random distribution of poems based on current viewed poem
     * 
     * @param current navigation history
     * @return List of five poems
     * @throws IOException
     */
    @RequestMapping(value = "/getRandDistPoems", method = RequestMethod.POST)
    @ResponseBody
    @Deprecated
    public List<Poem> getRandDistPoems( @RequestBody List<Integer> history) throws IOException;
    
    /**
     * Method for liking a poem, saving like status inside the database
     * 
     * @param poem to like
     * @throws IOException
     */
    @RequestMapping(value = "/likePoem", method = RequestMethod.POST)
    @ResponseBody
    public void likePoem( @RequestBody PoemLikeWrapper poem) throws IOException;
}
