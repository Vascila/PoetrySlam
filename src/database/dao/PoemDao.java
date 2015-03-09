package database.dao;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import wrappers.DocDistWrapper;
import database.domain.Poem;
import database.domain.PoemLikeWrapper;

public interface PoemDao {

	public void setDataSource(DataSource ds);
	   
	public List<Poem> getAllPoems();
	
	public List<Poem> getNewestPoems();
	 
	public Poem getPoemByID(int id);
	
	public List<Poem> getPoemsByID(List<Integer> nId);
	
	public int insertPoem(Poem poem);
	
	public List<Poem> getPoemsFromWrappers(List<DocDistWrapper> wrappers);

	void updateAuthorNames() throws SQLException;

	void removeBrokenPoems();
	
	void likePoem(PoemLikeWrapper poem);

	void checkDuplicateTitle();

	void fixDoubleTitles() throws SQLException;
}
