package database.util;

import java.util.List;

public class DBUtil {

	/**
	 * Method for finishing sql IN satements
	 * 
	 * @param sql statement ending with 'IN('
	 * @param objects to place within the statement
	 * @return compete statement string
	 */
	public static String finishIN(String sql, List<?> objects) {
		for (int i = 0; i < objects.size(); i++) {
			sql += objects.get(i);
			if (i != objects.size() - 1)
				sql += ", ";
			else
				sql += ")";
		}
		
		return sql;
	}
	
}
