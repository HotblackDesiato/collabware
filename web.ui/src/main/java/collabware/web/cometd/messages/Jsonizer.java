package collabware.web.cometd.messages;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Participant;
import collabware.userManagement.User;
import collabware.web.cometd.utils.ServerJsonProvider;
import collabware.web.shared.OperationJsonizer;

public class Jsonizer {

	public static JSONObject serialize(Participant participant) {
		return serialize(participant.getUser());
	}

	public static JSONObject serialize(User user) {
		JSONObject jsonParticipant = new JSONObject();
		try {
			jsonParticipant.put("displayName", user.getDisplayName());
			jsonParticipant.put("id", user.getUserName());
			jsonParticipant.put("imageUrl", "/collabware/images/"+user.getUserName()+".png");
		} catch (JSONException mustNeverHappen) {
			assert(false);
		}
		return jsonParticipant;
	}

	public static JSONObject serialize(ComplexOperation operation) {
		OperationJsonizer<JSONObject, JSONArray> jsonizer = new OperationJsonizer<JSONObject, JSONArray>(new ServerJsonProvider());
		return jsonizer.jsonize(operation);
	}

	public static JSONArray serialize(List<Participant> participants) {
		JSONArray jsonParticipants = new JSONArray();
		for (Participant p: participants) {
			jsonParticipants.put(serialize(p));
		}
		return jsonParticipants;
	}

}
