package database.dao;

import java.util.List;

import javax.sql.DataSource;

import database.domain.Poem;

public interface PoemDao {

	public void setDataSource(DataSource ds);
	   
	public List<Poem> getAllPoems();
	
	public List<Poem> getNewestPoems();
	 
	public Poem getPoemByID(int id);
	public List<Poem> getPoemsByID(List<Integer> nId);
	
	public int insertPoem(Poem poem);
}
