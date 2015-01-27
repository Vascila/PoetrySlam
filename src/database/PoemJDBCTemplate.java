package database;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import database.dao.PoemDao;
import database.domain.Poem;
import database.mapper.PoemMapper;
import database.util.DBUtil;

public class PoemJDBCTemplate implements PoemDao {

	private static final String getAllPoems = "SELECT * FROM Poems ";
	private static final String getPoemByID = "SELECT * FROM Poems WHERE Poems.poemid = ? ";
	private static final String insertSql = "INSERT INTO Poems (Author, PoemText, Title, date) values (?, ?, ?, ?)";
	private static final String getRecent = "SELECT * FROM poems WHERE date > ? ORDER BY date DESC";
	private static final String getPoemsByID = "SELECT * FROM poems WHERE poemid IN (";
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Poem> getNewestPoems() {
		Date asd = new Date(2015, 1, 1);
		Object[] args = {new Timestamp(asd.getTime())};
		List<Poem> poems = jdbcTemplateObject.query(getRecent, args, new PoemMapper());
		System.out.println(poems.size());
		return poems;
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
	public List<Poem> getPoemsByID(List<Integer> nId) {
		String sql = DBUtil.finishIN(getPoemsByID, nId);
		List<Poem> poems = jdbcTemplateObject.query(sql, new PoemMapper());
		return poems;
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

}
