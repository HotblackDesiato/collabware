package collabware.web.rest;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import collabware.collaboration.Change;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationDetails;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.NoSuchCollaborationException;
import collabware.collaboration.Participant;
import collabware.collaboration.ReplaySequence;
import collabware.registry.EditorReference;
import collabware.registry.EditorRegistry;
import collabware.userManagement.User;
import collabware.userManagement.UserManagement;
import collabware.userManagement.exception.NoSuchUserException;
import collabware.web.AbstractProvider;
import collabware.web.cometd.messages.Jsonizer;
import collabware.web.feeds.ChangeAggregator;
import collabware.web.feeds.DocumentChangeList;

@Controller("restService")
public class RestServices extends AbstractProvider {
	
	@Autowired
	CollaborationProvider collaborationProvider;
	
	@Autowired
	private UserManagement userManagement;
	
	@Autowired
	private EditorRegistry editorRegistry;
	
	@Autowired
	private ChangeAggregator changeAggregator;
	private static DateTimeFormatter f = ISODateTimeFormat.dateTimeNoMillis();
	
	@RequestMapping(method=RequestMethod.GET,value="/users")
	@ResponseBody
	public String getAllUsers(HttpServletResponse response) {
		JSONArray a = new JSONArray();
		for (User u: userManagement.getRegisteredUsers()) {
			try {
				a.put(toJSON(u));			
			} catch (JSONException e) {}
		}
		response.addHeader("Access-Control-Allow-Origin", "*");
		return a.toString();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/contacts",params="!search")
	@ResponseBody
	public String getAllContacts(HttpServletResponse response) throws JSONException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		// TODO proper implementation
		JSONArray a = new JSONArray();
		for (User u: userManagement.getRegisteredUsers()) {
			User loggedInUser = getLoggedinParticipant().getUser();
			if (!u.equals(loggedInUser))
				a.put(Jsonizer.serialize(u));
		}
		JSONObject o = new JSONObject();
		o.put("contacts", a);
		return o.toString();
	}

	@RequestMapping(method=RequestMethod.GET,value="/contacts")
	@ResponseBody
	public String searchContacts(@RequestParam("search") String search) {
		return search;
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/contacts")
	public void addContact(@RequestParam("userName") String userName) {
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/documents/{collaborationId}/participants")
	@ResponseBody
	public String getParticipants(@PathVariable("collaborationId") String collaborationId) throws NoSuchCollaborationException {
		// TODO security: check whether logged in user is allowed to see participants
		List<Participant> participants = collaborationProvider.getCollaboration(collaborationId).getParticipants();
		JSONArray a = new JSONArray();
		for (Participant p: participants) {
			try {
				a.put(toJSON(p.getUser()));			
			} catch (JSONException e) {}
		}
		return a.toString();
	}

	@RequestMapping(method=RequestMethod.GET,value="/documents")
	@ResponseBody
	public String getDocuments() throws NoSuchCollaborationException, JSONException {
		// TODO security: check whether logged in user is allowed to see participants
		Set<Collaboration> collaborations = this.getLoggedinParticipant().getParticipatingCollaborations();
		JSONArray a = new JSONArray();
		for (Collaboration c:collaborations) {
			try {
				a.put(toJSON(c));			
			} catch (JSONException e) {}
		}
		JSONObject o = new JSONObject();
		o.put("documents", a);
		return o.toString();
	}

	@RequestMapping(method=RequestMethod.GET,value="/documents/{collaborationId}")
	@ResponseBody
	public String getDocument(@PathVariable("collaborationId") String collaborationId, HttpServletResponse response) throws NoSuchCollaborationException, JSONException {
		// TODO security: check whether logged in user is allowed to see participants
		Collaboration collaboration = collaborationProvider.getCollaboration(collaborationId);
		if (collaboration.getParticipants().contains(this.getLoggedinParticipant())) {
			return toJSON(collaboration).toString();
		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return "";
		}
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/documents")
	public void createDocument(@RequestParam("type") String type, @RequestParam("name") String name, HttpServletResponse response) throws JSONException {
		JSONObject o = new JSONObject();
		if (editorRegistry.hasEditorReferenceFor(type)) {
			CollaborationDetails details = new CollaborationDetails(name, type);
			Collaboration collaboration = collaborationProvider.createCollaboration(details, getLoggedinParticipant());
			o.put("success", true);
			o.put("collaboration", toJSON(collaboration));
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			o.put("success", false);
			o.put("exception", "No such document type '" + type + "'.");				
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		//return o.toString();
	}

	@RequestMapping(method=RequestMethod.GET,value="/editors")
	@ResponseBody
	public String getEditors() throws NoSuchCollaborationException, JSONException {
		// TODO security: check whether logged in user is allowed to see participants
		Collection<EditorReference> editors = this.editorRegistry.getRegisteredEditors();
		JSONArray a = new JSONArray();
		for (EditorReference e:editors) {
			try {
				a.put(toJSON(e));			
			} catch (JSONException ex) {}
		}
		JSONObject o = new JSONObject();
		o.put("editors", a);
		return o.toString();
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/news")
	@ResponseBody
	public String getNewsFeed() throws JSONException {
		User loggedInUser = getLoggedinParticipant().getUser();
		List<DocumentChangeList> changesForUser = changeAggregator.getAllChangesForUser(loggedInUser);
		JSONArray a = new JSONArray();
		for (DocumentChangeList change:changesForUser) {
				a.put(change.toJson());			
		}
		JSONObject o = new JSONObject();
		o.put("news", a);
		return o.toString();
	}
	

	private JSONObject toJSON(User contact) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("id", contact.getUserName());
		o.put("displayName", contact.getDisplayName());
		o.put("imageUrl", "/collabware/images/"+contact.getUserName()+".png");
		return o;
	}

	private JSONObject toJSON(EditorReference e) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("id", e.getContentType());
		o.put("name", e.getName());
		o.put("description", e.getDescription());
		o.put("imageUrl", "/collabware/editor/"+e.getContentType()+"/images/editor.small.png");
		return o;
	}

	private JSONObject toJSON(Collaboration c) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("id", c.getId());
		o.put("name", c.getName());
		o.put("owner", toJSON(c.getOwner().getUser()));
		o.put("participants", toJSON(c.getParticipants()));
		o.put("type", c.getType());
		o.put("imageUrl", "/collabware/editor/"+c.getType()+"/images/editor.small.png");
		o.put("createdOn", toIsoDateTime(c.getCreatedOn()));
		o.put("lastChange",toJson(c.getLastChange()));
		
		return o;
	}

	private JSONObject toJson(Change c) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("dateTime", f.print(new DateTime(c.getDateTime())));
		o.put("participant", toJSON(c.getUser()));
		o.put("description", c.getDescription());
		
		return o;
	}


	private String toIsoDateTime(Date d) {
		DateTime dt = new DateTime(d);
		return f.print(dt);
	}
	
	private JSONArray toJSON(List<Participant> participants) throws JSONException {
		JSONArray a = new JSONArray();
		for (Participant p:participants) {
			a.put(toJSON(p.getUser()));
		}
		return a;
	}

	@RequestMapping(method=RequestMethod.POST,value="/documents/{collaborationId}/participants")
	public void addParticipants(@PathVariable("collaborationId") String collaborationId, @RequestParam("userName") String userName, HttpServletResponse response) throws NoSuchUserException, NoSuchCollaborationException {
		// TODO security: check whether logged in user is allowed to add participants
		User user = userManagement.getUser(userName);
		Participant participant = collaborationProvider.getParticipant(user);
		Collaboration collaboration = collaborationProvider.getCollaboration(collaborationId);
		collaboration.addParticipant(participant);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@RequestMapping(method=RequestMethod.GET,value="/documents/{collaborationId}/replay")
	@ResponseBody
	public String getReplay(@PathVariable("collaborationId") String collaborationId) throws NoSuchCollaborationException, JSONException {
		// TODO security: check whether logged in user is allowed to replay collaboration
		Collaboration collaboration = collaborationProvider.getCollaboration(collaborationId);
		ReplaySequence replaySequence = collaboration.getReplaySequence();
		return serialize(replaySequence);
	}

	private String serialize(ReplaySequence replaySequence) throws JSONException {
		JSONObject o = new JSONObject();
		o.put("init", Jsonizer.serialize(replaySequence.getInit()));
		JSONArray array = new JSONArray();
		for (int i = 0; i < replaySequence.length(); i++) {
			array.put(i, Jsonizer.serialize(replaySequence.get(i)));
		}
		o.put("replaySequence", array);
		return o.toString();
	}
}
