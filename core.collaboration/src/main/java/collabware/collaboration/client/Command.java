package collabware.collaboration.client;

import collabware.api.document.ModifyableDocument;
/**
 * A command (i.e. a sequence of modifications) to be applied to a {@link ModifyableDocument}. 
 *
 */
public interface Command {
	/**
	 * 
	 * @return The description of what this command does.
	 */
	String getDescription();
	
	/**
	 * Applies this command by performing modifications to a {@link ModifyableDocument}.
	 * @param document
	 */
	void apply(ModifyableDocument document);
}
