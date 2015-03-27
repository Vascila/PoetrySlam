package database.domain;

/**
 *	Poem domain class 
 *
 */
public class Poem {

	private Integer poemID;
	private String author;
	private String title;
	private String text;
	private Double distribution;
	
	public Integer getPoemID() {
		return poemID;
	}
	public void setPoemID(Integer poemID) {
		this.poemID = poemID;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Double getDistribution() {
		return distribution;
	}
	public void setDistribution(Double distribution) {
		this.distribution = distribution;
	}
}
