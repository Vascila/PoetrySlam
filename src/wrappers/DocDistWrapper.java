package wrappers;

public class DocDistWrapper {

	private int docName;
	private double distribution;
	
	public DocDistWrapper() {
		this.docName = 0;
		this.distribution = 0.0;
	}
	
	public DocDistWrapper(int docName, double distribution) {
		this.docName = docName;
		this.distribution = distribution;
	}
	
	public int getDocName() {
		return docName;
	}
	public void setDocName(int docName) {
		this.docName = docName;
	}
	public double getDistribution() {
		return distribution;
	}
	public void setDistribution(double distribution) {
		this.distribution = distribution;
	}
	
}
