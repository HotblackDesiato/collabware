package collabware.api.document;

import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.api.operations.Operation;

/**
 * A Document that can be modified by applying operations.
 *
 */
public interface ModifyableDocument extends Document {
	
	/**
	 * Applies an operation to this document
	 * 
	 * @param op the operation to apply.
	 * @throws DocumentException if anything goes wrong.
	 */
	void apply(Operation op) throws DocumentException;

	/**
	 * Clears the content of this document.
	 */
	void clear();

	/**
	 * Notifies all registered {@link ChangeListener} about a {@link Change}
	 * @param change
	 */
	void notifyChange(Change change);

}
