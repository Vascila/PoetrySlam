package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import database.dao.PoemDao;
import database.domain.Poem;
import database.mapper.PoemMapper;

public class PoemJDBCTemplate implements PoemDao {

	private static final String insertSql = "INSERT INTO Poems (Author, PoemText, Title) values (?, ?, ?)";
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	
	
	@Override
	public List<Poem> getAllPoems() {
		String sql =
				"SELECT * " +
				"FROM Poems ";
		List<Poem> poems =  jdbcTemplateObject.query(sql, new PoemMapper());
		System.out.println(poems.size());
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
