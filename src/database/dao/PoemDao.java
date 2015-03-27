package database.dao;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import wrappers.DocDistWrapper;
import database.domain.Poem;
import database.domain.PoemLikeWrapper;

public interface PoemDao {

	public void setDataSource(DataSource ds);
	   
	/**
	 * Retrieve all poems from database
	 * 
	 * @return list of all poems
	 */
	public List<Poem> getAllPoems();
	 
	/**
	 * Query for a poem by its ID
	 * 
	 * @param the poem ID
	 * @return poem
	 */
	public Poem getPoemByID(int id);
	
	/**
	 * Returns a list of poems based on list of IDs
	 * 
	 * @param list of poem IDs
	 * @return list of poems
	 */
	@Deprecated
	public List<Poem> getPoemsByID(List<Integer> nId);
	
	/**
	 * Insert poem to database
	 * Gives the poem a unique ID in the process
	 * 
	 * @param poem to save
	 * @return number of affected rows
	 */
	public int insertPoem(Poem poem);
	
	/**
	 * Retrieve poems based on a list of Document Distribution wrapper
	 * 
	 * @param wrappers
	 * @return list of poems
	 */
	public List<Poem> getPoemsFromWrappers(List<DocDistWrapper> wrappers);

	/**
	 * Method used to update certain set of badly parsed author names
	 * 
	 * @throws SQLException
	 */
	@Deprecated
	void updateAuthorNames() throws SQLException;

	/**
	 * Method to remove set of badly parsed poems from text files
	 * 
	 */
	@Deprecated
	void removeBrokenPoems();
	
	/**
	 * Method for saving a poem like within the database
	 * 
	 * @param poem to like
	 */
	void likePoem(PoemLikeWrapper poem);

	/**
	 * Method for finding and fixing double titles within the database.
	 * Double means that the title repeats twice inside a single 
	 * poem title text field
	 * 
	 * @throws SQLException
	 */
	@Deprecated
	void fixDoubleTitles() throws SQLException;
}
