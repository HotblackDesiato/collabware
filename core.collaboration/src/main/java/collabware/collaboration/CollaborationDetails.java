package collabware.collaboration;

import static collabware.utils.Asserts.assertNotNull;

public final class CollaborationDetails {

	private final String name;
	private final String type;

	public CollaborationDetails(String name, String type) {
		assertNotNull("name", name); assertNotNull("type", type);
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
}
