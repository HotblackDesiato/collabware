package collabware.registry.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import collabware.registry.DuplicateRegistrationException;
import collabware.registry.EditorReference;
import collabware.registry.EditorRegistry;
import collabware.registry.NoSuchEditorReferenceException;

public class EditorRegistryTest {

	private EditorRegistry editorRegistry = new EditorRegistryImpl();

	@Test
	public void registerEditor() throws Exception {
		EditorReference umlModelType = new EditorReference("collabware.uml.class", "UML", "Blah", this.getClass().getClassLoader());
		
		editorRegistry.register(umlModelType);
		
		assertEditorRegistered(umlModelType);
	}

	@Test(expected=IllegalArgumentException.class)
	public void registerEditorWithModelTypeNull() throws Exception {
		editorRegistry.register(null);
	}

	@Test(expected=DuplicateRegistrationException.class)
	public void registerDuplicateEditor() throws Exception {
		EditorReference ref = new EditorReference("collabware.uml.class", "UML", "Blah", this.getClass().getClassLoader());
		editorRegistry.register(ref);
		
		editorRegistry.register(ref);
	}
		
	@Test(expected=NoSuchEditorReferenceException.class)
	public void getEditorReferenceForNonexistingModelTypeId() throws Exception {
		editorRegistry.getEditorReferenceFor("typeDoesNotExist");		
	}
	
	@Test
	public void removeRegistration() throws Exception {
		EditorReference modelType = new EditorReference("collabware.uml.class", "UML", "Blah", this.getClass().getClassLoader());
		editorRegistry.register(modelType);
		
		editorRegistry.unregister(modelType);
		
		assertEditorNotRegistered(modelType);
	}

	private void assertEditorNotRegistered(EditorReference modelType) {
		assertFalse(editorRegistry.hasEditorReferenceFor(modelType.getContentType()));
		assertFalse(editorRegistry.getRegisteredEditors().contains(modelType));
	}
	
	private void assertEditorRegistered(EditorReference modelType) throws Exception {
		Collection<EditorReference> registeredModelTypes = editorRegistry.getRegisteredEditors();
		
		assertTrue(editorRegistry.hasEditorReferenceFor(modelType.getContentType()));
		assertTrue(registeredModelTypes.contains(modelType));
		assertSame(modelType, editorRegistry.getEditorReferenceFor(modelType.getContentType()));
	}
}
