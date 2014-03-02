package collabware.web.documents;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationDetails;
import collabware.collaboration.NoSuchCollaborationException;
import collabware.collaboration.Participant;
import collabware.registry.EditorReference;
import collabware.registry.EditorRegistry;
import collabware.registry.NoSuchEditorReferenceException;
import collabware.userManagement.User;
import collabware.userManagement.UserManagement;
import collabware.web.AbstractProvider;
import collabware.web.feeds.ChangeAggregator;
import collabware.web.feeds.DocumentChangeList;

@Controller
public class DocumentsProvider extends AbstractProvider {
	
	@Autowired
	private EditorRegistry editorRegistry;
	
	@Autowired
	private UserManagement userManagement;
	
	@Autowired
	private ChangeAggregator changeAggregator;
	
	@PostConstruct
	public void init() {
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/")
	public ModelAndView getDocuments() {
		Participant participant = getLoggedinParticipant();
		ModelAndView mav = new ModelAndView("ShowAllDocuments");
		mav.addObject("collaborations", participant.getParticipatingCollaborations());
		mav.addObject("participant", participant);
		mav.addObject("contacts", this.userManagement.getRegisteredUsers());
		return mav;
	}

	@RequestMapping(method=RequestMethod.GET, value="/show/{id}")
	public ModelAndView getDocument(@PathVariable("id") String id) {
		// TODO Security: check whether logged in user is allowed to see collaboration
		try {
			ModelAndView mav = new ModelAndView("ShowDocument");
			mav.addObject("id", id);
			Collaboration collaboration = collaborationProvider.getCollaboration(id);
			mav.addObject("collaboration", collaboration);
			String type = collaboration.getType();
			EditorReference reference = editorRegistry.getEditorReferenceFor(type);
			mav.addObject("editor",reference);
			return mav;
		} catch (NoSuchCollaborationException e) {
			ModelAndView mav = new ModelAndView("Error");
			mav.addObject("error", e);
			mav.addObject("message", "The document that you have requested does not exist.");
			return mav;
		} catch (NoSuchEditorReferenceException e) {
			ModelAndView mav = new ModelAndView("Error");
			mav.addObject("error", e);
			mav.addObject("message", "Currently, there is no editor registered to display your document.");
			return mav;
		}
	}

	@RequestMapping(method=RequestMethod.GET, value="/create")
	public ModelAndView prepareCreateDocument() {
		ModelAndView mav = new ModelAndView("CreateDocument");
		mav.addObject("documentTypes", editorRegistry.getRegisteredEditors());
		mav.addObject("participant", getLoggedinParticipant());
		return mav;
	}

	@RequestMapping(method=RequestMethod.POST, value="/create")
	public String createDocument(@RequestParam("type") String type, @RequestParam("name") String name) {
		ModelAndView mav = new ModelAndView("DocumentCreated");
		if (editorRegistry.hasEditorReferenceFor(type)) {
			CollaborationDetails details = new CollaborationDetails(name, type);
			Collaboration collaboration = collaborationProvider.createCollaboration(details, getLoggedinParticipant());
			mav.addObject("collaboration", collaboration);
		} else {
			mav.addObject("exception", "No such document type '" + type + "'.");				
		}
		return "redirect:/documents/";
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/types")
	public ModelAndView getKnownModelTypes() {
		ModelAndView mav = new ModelAndView("KnownModelTypes");
		mav.addObject("documentTypes", editorRegistry.getRegisteredEditors());
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/newsfeed")
	public ModelAndView getNewsFeed() {
		User loggedInUser = getLoggedinParticipant().getUser();
		List<DocumentChangeList> changesForUser = changeAggregator.getAllChangesForUser(loggedInUser);
		ModelAndView mav = new ModelAndView("NewsFeed");
		mav.addObject("documentChanges", changesForUser);
		return mav;
	}

}
