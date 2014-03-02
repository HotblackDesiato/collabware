package collabware.registry;

import java.net.URL;
import java.util.Collection;

/**
 * The central place for registering editors for a given content type.
 * @see EditorReference
 */
public interface EditorRegistry {

	/**
	 * Registers an editor.
	 * @param reference
	 * @throws DuplicateRegistrationException if an editor for the specified content type already exists.
	 */
	void register(EditorReference reference) throws DuplicateRegistrationException;

	/**
	 * 
	 * @return all registered editors.
	 */
	Collection<EditorReference> getRegisteredEditors();

	/**
	 * 
	 * @param contentType
	 * @return true if and only if an editor for {@code contentType} is registered.
	 */
	boolean hasEditorReferenceFor(String contentType);

	/**
	 * @param contentType
	 * @return the {@code EditorReference} for a given {@code contentType}.
	 * @throws NoSuchEditorReferenceException if no editor is registered for {@code contentType}
	 */
	EditorReference getEditorReferenceFor(String contentType) throws NoSuchEditorReferenceException;

	/**
	 * Unregisters and editor
	 * @param registration
	 */
	void unregister(EditorReference registration);

	/**
	 * 
	 * @param contentType
	 * @param path
	 * @return The URL pointing to a resource provided by the editor, or {@code null}.
	 */
	URL getResource(String editorId, String path);
}
