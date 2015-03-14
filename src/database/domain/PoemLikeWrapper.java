package database.domain;

public class PoemLikeWrapper {

	private String userId;
	private int poemId;
	private double weight;
	private int afterSimilar;
	
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getPoemId() {
		return poemId;
	}
	public void setPoemId(int poemId) {
		this.poemId = poemId;
	}
	public int getAfterSimilar() {
		return afterSimilar;
	}
	public void setAfterSimilar(int afterSimilar) {
		this.afterSimilar = afterSimilar;
	}
	
}
