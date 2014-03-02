package collabware.web.shared;

import java.util.Set;

public interface JsonProvider<OT,AT> {

	Object getOpt(OT json, String key) throws JsonizationException;

	int length(AT a) throws JsonizationException;

	OT getObject(OT json, String string) throws JsonizationException;
	OT getObject(AT json, int i) throws JsonizationException;

	AT getArray(OT o, String key) throws JsonizationException;

	int getInt(OT json, String key) throws JsonizationException;

	String getString(OT json, String key) throws JsonizationException;

	String getOptString(OT json, String key, String defaultValue) throws JsonizationException;
	
	AT newJsonArray() throws JsonizationException;

	void push(AT a, OT o) throws JsonizationException;

	void put(OT o, String key, Object value) throws JsonizationException;

	OT newJsonObject() throws JsonizationException;

	OT parse(String jsonPayload) throws JsonizationException;
	
	Set<String> getKeys(OT json);
}
