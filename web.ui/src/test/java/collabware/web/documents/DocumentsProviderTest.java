package collabware.web.documents;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.NoSuchCollaborationException;
import collabware.registry.EditorRegistry;
import collabware.registry.NoSuchEditorReferenceException;

public class DocumentsProviderTest {
	@Test
	public void showDocumentReturnsErrorForNonexistingCollaboration() throws Exception {
		DocumentsProvider docProvider = new DocumentsProvider();
		CollaborationProvider collabProvider = createMock(CollaborationProvider.class);
		expect(collabProvider.getCollaboration(anyObject(String.class))).andThrow(new NoSuchCollaborationException());
		replay(collabProvider);
		
		ReflectionTestUtils.setField(docProvider, "collaborationProvider", collabProvider);
		
		ModelAndView modelAndView = docProvider.getDocument("nonExistingCollaborationId");
		
		assertThat(modelAndView.getViewName(), is(equalTo("Error")));
		assertThat(modelAndView.getModelMap().get("error"), instanceOf(NoSuchCollaborationException.class));
		assertThat((String)modelAndView.getModelMap().get("message"), is(equalTo("The document that you have requested does not exist.")));
	}

	@Test
	public void showDocumentReturnsErrorForNonexistingEditor() throws Exception {
		DocumentsProvider docProvider = new DocumentsProvider();
		CollaborationProvider collabProvider = createMock(CollaborationProvider.class);
		Collaboration col = createNiceMock(Collaboration.class);
		expect(collabProvider.getCollaboration(anyObject(String.class))).andReturn(col);
		replay(collabProvider, col);
		
		EditorRegistry editorRegistry = createMock(EditorRegistry.class);
		expect(editorRegistry.getEditorReferenceFor(anyObject(String.class))).andThrow(new NoSuchEditorReferenceException(""));
		replay(editorRegistry);
		
		ReflectionTestUtils.setField(docProvider, "collaborationProvider", collabProvider);
		ReflectionTestUtils.setField(docProvider, "editorRegistry", editorRegistry);
		
		ModelAndView modelAndView = docProvider.getDocument("collaborationIdWithNoEditorRegistered");
		
		assertThat(modelAndView.getViewName(), is(equalTo("Error")));
		assertThat(modelAndView.getModelMap().get("error"), instanceOf(NoSuchEditorReferenceException.class));
		assertThat((String)modelAndView.getModelMap().get("message"), is(equalTo("Currently, there is no editor registered to display your document.")));
	}
}
