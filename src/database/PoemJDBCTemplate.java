package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import wrappers.DocDistWrapper;
import database.dao.PoemDao;
import database.domain.Poem;
import database.domain.PoemLikeWrapper;
import database.mapper.PoemMapper;
import database.util.DBUtil;

public class PoemJDBCTemplate implements PoemDao {

	// All queries used for database access
	private static final String getAllPoems = "SELECT * FROM Poems ";
	private static final String getPoemByID = "SELECT * FROM Poems WHERE Poems.poemid = ? ";
	private static final String insertSql = "INSERT INTO Poems (Author, PoemText, Title, date) values (?, ?, ?, ?)";
	private static final String insertLikeSql = "INSERT INTO TestData(userid, poemid, choseSimilar, choseNew, weight) values (?, ?, ?, ?, ?)";
	private static final String getPoemsByID = "SELECT * FROM poems WHERE poemid IN (";
	private static final String FIX_POEM_BY_ID = "SELECT poemid FROM (SELECT poemid, row_number() over "
			+ "(partition BY lower(title) ORDER BY poemid) AS rnum FROM poems) t WHERE t.rnum > 1 order by poemid ";
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Poem> getAllPoems() {
		List<Poem> poems = jdbcTemplateObject.query(getAllPoems, new PoemMapper());
		return poems;
	}

	@Override
	public Poem getPoemByID(int id) {
		Object[] args = {id};
		Poem poem = jdbcTemplateObject.query(getPoemByID, args, new PoemMapper()).get(0);
		return poem;
	}
	

	@Override
	public List<Poem> getPoemsFromWrappers(List<DocDistWrapper> wrappers) {
		List<Integer> temp = new ArrayList<Integer>();
		for(DocDistWrapper wrapper: wrappers)
			temp.add(wrapper.getDocName());
		String sql = DBUtil.finishIN(getPoemsByID, temp);
		List<Poem> poems = jdbcTemplateObject.query(sql, new PoemMapper());
		for(int i = 0; i < poems.size(); i++)
			poems.get(i).setDistribution(wrappers.get(i).getDistribution());
		return poems;
	}
	
	@Override
	public List<Poem> getPoemsByID(List<Integer> nId) {
		String sql = DBUtil.finishIN(getPoemsByID, nId);
		List<Poem> poems = jdbcTemplateObject.query(sql, new PoemMapper());
		return poems;
	}
	
	
	@Override
	public void removeBrokenPoems() {
		
		String sql = FIX_POEM_BY_ID;
		List<Integer> ids = jdbcTemplateObject.queryForList(sql, Integer.class);
			
		  String sourceFileName = "C:\\Users\\Lukas\\git\\PoetrySlam\\OtherText\\testLine.txt";
		  String destinationFileName = "C:\\Users\\Lukas\\git\\PoetrySlam\\OtherText\\testLineFixed.txt";
		
	      BufferedReader br = null;
	      PrintWriter pw = null; 
	      int id = 0;
	      
	      try {
	          br = new BufferedReader(new FileReader( sourceFileName ));
	    	  pw =  new PrintWriter(new FileWriter( destinationFileName ));

	          String line;
	          while ((line = br.readLine()) != null) {
	        	  id = Integer.parseInt(line.substring(0, line.indexOf(" ")));
	        	  if (!ids.contains(id) && id != 0)
	        		  pw.println(line);
	          }

	          br.close();
	          pw.close();
	      }catch (Exception e) {
		  e.printStackTrace();
	      }
		
		
	}
	
	
	@Override
	public void fixDoubleTitles() throws SQLException {
		List<Poem> poems = jdbcTemplateObject.query(getAllPoems, new PoemMapper());
		List<Poem> newPoems = new ArrayList<Poem>();
		
		boolean dup = true;
		for (Poem p: poems) {
			
			String[] tokens = p.getTitle().split(" ");
			if (tokens.length % 2 == 0) {
				int half = tokens.length / 2;
				int otherHalf = tokens.length;
				while (dup && half != 0) {
					if (!tokens[half-1].equals(tokens[otherHalf-1]))
						dup = false;
					half--;
					otherHalf--;
					if (half == 0 && dup) {
						p.setTitle(halfString(p.getTitle()));
						newPoems.add(p);
					}
				}
				dup = true;
			}
		}
		String updateSql = "UPDATE poems SET title = ? WHERE poemid = ?";
		try (
		        Connection connection = dataSource.getConnection();
		        PreparedStatement statement = connection.prepareStatement(updateSql);
		    ) {
			for (Poem p: newPoems) {
				statement.setString(1, p.getTitle());
				statement.setInt(2, p.getPoemID());
				statement.executeUpdate();
			}
		}
		
	}
	
	public String halfString(String title) {
		String newString = "";
		String[] tokens = title.split(" ");
		int size = tokens.length;
		int half = (size / 2) - 1;
		
		for (int i = 0; i <= half; i++) {
			newString += tokens[i] + " ";
		}
		return newString;
	}
	
	@Override
	public void updateAuthorNames() throws SQLException {
		String sql = "SELECT * FROM Poems where poemid > 1112";
		
		String updateSql = "UPDATE poems SET author = ? WHERE poemid = ?";
				
		List<Poem> poems = jdbcTemplateObject.query(sql, new PoemMapper());
		
		List<Poem> newPoems = new ArrayList<Poem>();
		
		for (Poem p: poems)
			newPoems.add(fixAtuthorName1(p));
		
		try (
		        Connection connection = dataSource.getConnection();
		        PreparedStatement statement = connection.prepareStatement(updateSql);
		    ) {
			for (Poem p: poems) {
				statement.setString(1, p.getAuthor());
				statement.setInt(2, p.getPoemID());
				statement.executeUpdate();
			}
		}
		
	}
	
    private Poem fixAtuthorName1(Poem poem) {
    	String name = poem.getAuthor();
    	String temp = "";
    	if (name.startsWith("By "))
    		temp = name.substring(3, name.length());
    	if (temp.contains("(")) {
    		poem.setAuthor(temp.substring(0, temp.indexOf('(')));
    		return poem;
    	}
    	poem.setAuthor(temp);
    	return poem;
    }
	
	
	@Override
	public int insertPoem(Poem poem) {
		int resultID = 0;
		try (
		        Connection connection = dataSource.getConnection();
		        PreparedStatement statement = connection.prepareStatement(insertSql,
		                                      Statement.RETURN_GENERATED_KEYS);
		    ) {
		        statement.setString(1, poem.getAuthor());
		        statement.setString(2, poem.getText());
		        statement.setString(3, poem.getTitle());
		        statement.setTimestamp(4, new Timestamp(new Date().getTime()));

		        int affectedRows = statement.executeUpdate();

		        if (affectedRows == 0) {
		            throw new SQLException("Creating user failed, no rows affected.");
		        }

		        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
		            if (generatedKeys.next()) {
		                resultID = generatedKeys.getInt(1);
		            }
		            else {
		                throw new SQLException("Creating user failed, no ID obtained.");
		            }
		        }
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return resultID;
	}

	@Override
	public void likePoem(PoemLikeWrapper poem) {
		if (poem.getAfterSimilar() < 0)
			return;
		try (
		        Connection connection = dataSource.getConnection();
		        PreparedStatement statement = connection.prepareStatement(insertLikeSql)
    		){
		        statement.setString(1, poem.getUserId());
		        statement.setInt(2, poem.getPoemId());
		        
		        if (poem.getAfterSimilar() == 0) {
		        	statement.setInt(3, 1); 
		        	statement.setInt(4, 0); 
		        }
		        else {
		        	statement.setInt(4, 1);
		        	statement.setInt(3, 0); 
		        }
		        
		        statement.setDouble(5, poem.getWeight());
		        
		        int affectedRows = statement.executeUpdate();

		        if (affectedRows == 0) {
		            throw new SQLException("Creating user failed, no rows affected.");
		        }
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
