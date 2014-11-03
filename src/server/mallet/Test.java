package server.mallet;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Test {

	public static void main(String[] args) {
		
		
		ClassLoader cl = Test.class.getClass().getClassLoader();
		// Create icons
		Icon saveIcon  = new ImageIcon(cl.getResource("images/Open16.gif"));
		Icon cutIcon   = new ImageIcon(cl.getResource("images/cut.gif"));
		
	}
	
}
