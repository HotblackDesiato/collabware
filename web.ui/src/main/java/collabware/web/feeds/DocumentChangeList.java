package collabware.web.feeds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import collabware.collaboration.Change;
import collabware.collaboration.Collaboration;
import collabware.collaboration.Participant;
import collabware.userManagement.User;

public class DocumentChangeList {
	
	private final List<Change> latestChanges = new ArrayList<Change>();
	private Collaboration collaboration;
	private int maxEntries;
	
	public DocumentChangeList(Collaboration collaboration) {
		this(collaboration, 10);
	}
	public DocumentChangeList(Collaboration collaboration, int maxEntries) {
		this.collaboration = collaboration;
		this.maxEntries = maxEntries;
	}

	public String getDocumentName() {
		return collaboration.getName();
	}
	public String getDocumentId() {
		return collaboration.getId();
	}
	public String getDocumentTypeId() {
		return collaboration.getType();
	}
	
	public List<Change> getLatestChanges() {
		return latestChanges;
	}
	
	public Participant getDocumentOwner() {
		return collaboration.getOwner();
	}
	
	public Date getLastChanged() {
		if (latestChanges.size() >0) {
			return latestChanges.get(0).getDateTime();
		} else {
			return new Date(0,0,0,0,0);
		}
	}
	
	public void add(Change c) {
		latestChanges.add(0, c);
		if (latestChanges.size() > maxEntries) {
			latestChanges.remove(latestChanges.size() - 1);
		}
	}

	public boolean affects(User u) {
		for (Participant p: collaboration.getParticipants()) {
			if (p.getUser().equals(u)) 
				return true;
		}
		return false;
	}
	
	public JSONObject toJson() {
		JSONObject o = new JSONObject();
		try {
			o.put("name", getDocumentName());
			o.put("changeDate", getLastChanged());
			o.put("type", this.collaboration.getType());
			o.put("changes", jsonizeChanges());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return o;
	}
	private JSONArray jsonizeChanges() throws JSONException {
		JSONArray a = new JSONArray();
		for (Change c: this.latestChanges) {
			JSONObject o = new JSONObject();
			o.put("description", c.getDescription());
			o.put("dateTime", c.getDateTime());
			o.put("userName", c.getUser().getUserName());
			o.put("userDisplayName", c.getUser().getDisplayName());
			a.put(o);
		}
		return a;
	}
}
