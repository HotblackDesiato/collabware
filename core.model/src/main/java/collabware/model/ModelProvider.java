package collabware.model;

import collabware.api.document.DocumentProvider;

public interface ModelProvider extends DocumentProvider {

	ModifyableModel createDocument(String type);

	ModifyableModel createModelFromLiteral(String type, String literal);

	void populateModelFromLiteral(ModifyableModel model, String literal);

}
