package components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class TestIvy {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mmXXX";

	public static long parseTimestamp(String timestamp) throws ParseException {
		SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);
		return dateParser.parse(timestamp).getTime();
	}

	public static String toTimestamp(long lastRevision) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormatter.format(lastRevision).toString();
	}
	
	public static void main(String[] args) throws ParseException {
		
		String dateP = "2012-01-01T11:22:33Z";
		String date = "2007-07-14T19:20Z";
		System.out.println(parseTimestamp(dateP));
		
	}
	
}
