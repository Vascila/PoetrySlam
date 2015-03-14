package crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class URLbuilder {

	/*
	 * Used http://www.bartleby.com/\d* /\s to remove dead ends
	 * and http://www.bartleby.com/\d* /. to remove unrelated URLs 
	 */
	
	public static List<URI> readURLs(String fileName) throws IOException, URISyntaxException {
		List<URI> urls = new ArrayList<URI>();
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        String line = br.readLine();
	        while (line != null) {
	        	urls.add(new URI(line));
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
		
		return urls;
	}
}
