package collabware.api.operations;

import collabware.api.document.ModifyableDocument;

/**
 * An operation that can be applied to a document.
 */
public interface Operation {
	/**
	 * Applies this operation to a given document.
	 * @param document The document to apply this operation to. Must not be null.
	 * @throws OperationApplicationException if something goes wrong during the application of this operation.
	 */
	void apply(ModifyableDocument document) throws OperationApplicationException;

	/**
	 * 
	 * @return The inverse of this operation. Must not return null.
	 */
	Operation inverse();
}
