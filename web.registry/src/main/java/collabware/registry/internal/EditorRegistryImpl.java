package collabware.registry.internal;

import static collabware.utils.Asserts.assertNotNull;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import collabware.registry.DuplicateRegistrationException;
import collabware.registry.EditorRegistry;
import collabware.registry.EditorReference;
import collabware.registry.NoSuchEditorReferenceException;

public class EditorRegistryImpl implements EditorRegistry {
	
	private final Map<String, EditorReference> registeredModels = new HashMap<String, EditorReference>();
	
	public void register(EditorReference reference) throws DuplicateRegistrationException {
		assertNotNull("reference", reference); 
		assertModelTypeNotRegisted(reference);
		
		registeredModels.put(reference.getContentType(), reference);
	}

	private void assertModelTypeNotRegisted(EditorReference modelType) throws DuplicateRegistrationException {
		if (registeredModels.containsKey(modelType.getContentType()))
			throw new DuplicateRegistrationException(modelType);
	}

	public Collection<EditorReference> getRegisteredEditors() {
		return Collections.unmodifiableCollection(registeredModels.values());
	}

	public EditorReference getEditorReferenceFor(String modelTypeId) throws NoSuchEditorReferenceException {
		assertNotNull("id", modelTypeId);
		assertEditorRegisteredFor(modelTypeId);
		
		return registeredModels.get(modelTypeId);
	}

	public void unregister(EditorReference registration) {
		assertNotNull("registration", registration);
		
		String modelTypeId = registration.getContentType();
		registeredModels.remove(modelTypeId);
	}

	private void assertEditorRegisteredFor(String modelTypeId) throws NoSuchEditorReferenceException {
		if (!registeredModels.containsKey(modelTypeId))
			throw new NoSuchEditorReferenceException(modelTypeId);
	}

	public boolean hasEditorReferenceFor(String modelTypeId) {
		return registeredModels.containsKey(modelTypeId);
	}

	@Override
	public URL getResource(String editorId, String path) {
		return this.registeredModels.get(editorId).getResource(path);
	}
}
