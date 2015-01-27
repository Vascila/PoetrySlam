package database.util;

import java.util.List;

public class DBUtil {

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
