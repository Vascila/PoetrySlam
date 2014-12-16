package database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import database.domain.Poem;

public class PoemMapper implements RowMapper<Poem> {

	@Override
	public Poem mapRow(ResultSet rs, int rowNum) throws SQLException {
		Poem poem = new Poem();
		poem.setAuthor(rs.getString("Author"));
		poem.setTitle(rs.getString("Title"));
		poem.setPoemID(rs.getInt("PoemID"));
		poem.setText(rs.getString("PoemText"));
		return poem;
	}


	
}
