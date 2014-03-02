package collabware.collaboration.client;


public class ParticipantImpl implements ClientParticipant {

	private final String id;
	private final String imageUrl;
	private final String displayName;
	
	public ParticipantImpl(String id, String imageUrl, String displayName) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.displayName = displayName;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getImageUrl() {
		return imageUrl;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	public String toString() {
		return displayName + " (" + id +")";
	}
	
}
