package collabware.api.document.change;


public class ComplexChangeEnded implements Change {
	private final String description;


	public ComplexChangeEnded(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
