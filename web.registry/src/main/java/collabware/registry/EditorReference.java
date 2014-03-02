package collabware.registry;

import static collabware.utils.Asserts.*;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import collabware.api.document.Document;

/**
 * Reference to an editor. Editors are responsible for displaying and editing
 * content of a specific content type (see {@link Document#getContentType()}.
 * 
 * Bundles containing an editor must have the following structure:
 * <ul>
 * <li>{@code /collabware} - contains all resources accessible to web browsers.
 * <ul>
 * <li>{@code /index.html} - the editor main page.</li>
 * <li>{@code /images/editor.small.png} - an image (ideally 30x30 pixels) used to represent the editor</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @see EditorRegistry
 */
public final class EditorReference {

	private final static Pattern validIdPattern = Pattern.compile("[A-za-z0-9-_\\.]+");

	private final String contentType;
	private final String description;
	private final String name;
	private final ClassLoader resourceLoader;

	/**
	 * Creates a new EditorReference for registering an editor for a given
	 * content type.
	 * 
	 * @param contentType
	 *            the content type which should be edited using this editor.
	 *            Must match the following regular expression:
	 *            {@code [A-za-z0-9-_\\.]+}
	 * @param name
	 *            the name of the editor.
	 * @param description
	 *            a description of what the editor does.
	 * @param resourceLoader
	 *            the ClassLoader for loading resources used by the editor (e.g.
	 *            CSS, JavaScript, images, ...)
	 */
	public EditorReference(String contentType, String name, String description, ClassLoader resourceLoader) {
		assertValidId(contentType);
		assertNotNull("name", name);
		assertNotNull("description", description);
		assertNotNull("resourceLoader", resourceLoader);

		this.contentType = contentType;
		this.name = name;
		this.description = description;
		this.resourceLoader = resourceLoader;
	}

	private void assertValidId(String id) {
		Matcher matcher = validIdPattern.matcher(id);
		if (!matcher.matches())
			throw new IllegalArgumentException(String.format("Invlaid id '%s'. Id must only contain letters, numbers, '-', '.' or'_'.", id));
	}

	public String getContentType() {
		return contentType;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param path the path to resource relative to {@code /collabware} in the bundle's root folder.
	 * @return the URL to resource or {@code null}.
	 */
	public URL getResource(String path) {
		return resourceLoader.getResource("collabware" + ensureLeadingSlash(path));
	}

	private String ensureLeadingSlash(String path) {
		if (path.startsWith("/"))
			return path;
		else
			return "/" + path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EditorReference other = (EditorReference) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
