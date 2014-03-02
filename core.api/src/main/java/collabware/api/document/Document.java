package collabware.api.document;

import collabware.api.document.change.ChangeListener;
import collabware.api.operations.ComplexOperation;

/**
 * Basic abstraction of a real-time collaborative document.
 * Collabware supports multiple document types (e.g. graph-based document, a string-based document, etc). 
 * The different types can be distinguished via their document type. 
 * Typically, one implementation of this interface will correspond to one document type. 
 * The document type is different from the content type. 
 * Typically, one document type can support several different content types.
 */
public interface Document {

	/**
	 * @return The document type of this document.
	 */
	String getDocumentType();
	
	/**
	 * @return The content type of this document.
	 */
	String getContentType();
	
	/**
	 * Registers a ChangeListner to be notified about changes to this document.
	 * @param listener The listener. Must not be null.
	 * 
	 */
	void addChangeListener(ChangeListener listener);

	/** 
	 * Unregisters a change listener. If the listener was not registered or is null it does nothing.
	 * @param listener
	 */
	void removeChangeListener(ChangeListener listener);

	/**
	 * Whether or not this document is empty.
	 * @return {@code true} if and only if the document is empty.
	 */
	boolean isEmpty();

	/**
	 * @return The operation that recreates the document.
	 */
	ComplexOperation asOperation();

}