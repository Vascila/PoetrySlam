package server.util;

public class RelevanceColors {

	private static String[] colors = { "#FFFF99", "#FDF691", "#FBED8A", "#F9E382",
			"#F7DA7A", "#F5D173", "#F3C86B", "#F1BF63", "#EFB55C", "#EDAC54",
			"#EAA34C", "#E89A45", "#E6913D", "#E48736", "#E27E2E", "#E07526",
			"#DE6C1F", "#DC6317", "#DA590F", "#D85008", "#D64700" };
	
	public static String getColor(double weight) {
		System.out.println("Weight is:" + weight);
		int index = (int) (100 * (double)Math.round(weight * 100) / 100);
		
		if(index >= colors.length)
			return colors[colors.length - 1];
		
		return colors[index];
	}
	
}
