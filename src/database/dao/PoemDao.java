package database.dao;

import java.util.List;

import javax.sql.DataSource;

import database.domain.Poem;

public interface PoemDao {

	public void setDataSource(DataSource ds);
	   
	public List<Poem> getAllPoems();
	
	public int insertPoem(Poem poem);
}
