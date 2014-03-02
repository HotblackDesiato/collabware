package collabware.web.client;

import java.util.Set;
import java.util.logging.Logger;

import collabware.web.shared.JsonProvider;
import collabware.web.shared.JsonizationException;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;


/*
 * THIS CLASS IS NOT TESTABLE. THINK TWICE BEFORE CHANGING! 
 */
public class GwtJsonProvider implements JsonProvider<JSONObject, JSONArray> {
	
	private static Logger logger = Logger.getLogger("collabware.web.client.GwtJsonProvider");
	
	@Override
	public Object getOpt(JSONObject json, String key) throws JsonizationException {
		JSONValue jsonValue = json.get(key);
		if (jsonValue == null)
			return null;
		if (jsonValue.isBoolean() != null)
			return jsonValue.isBoolean().booleanValue();
		else if (jsonValue.isNumber() != null)
			return jsonValue.isNumber().doubleValue();
		else if (jsonValue.isString() != null)
			return jsonValue.isString().stringValue();
		else
			return null;
	}

	@Override
	public int length(JSONArray a) throws JsonizationException {
		return a.size();
	}

	@Override
	public JSONObject getObject(JSONArray a, int i) throws JsonizationException {
		JSONObject object = a.get(i).isObject();
		if (object != null)
			return object;
		else {
			String message = "JSON array '"+a.toString()+"' does not have a object valued entry at position '"+i+"'.";
			logger.severe(message);
			throw new JsonizationException(message);
		}
	}

	@Override
	public JSONArray getArray(JSONObject o, String key) throws JsonizationException {
		JSONArray array = o.get(key).isArray();
		if (array != null)
			return array;
		else {
			String message = "JSON object '"+o.toString()+"' does not have a array valued property '"+key+"'.";
			logger.severe(message);
			throw new JsonizationException(message);
		}
	}

	@Override
	public int getInt(JSONObject o, String key) throws JsonizationException {
		JSONNumber number = o.get(key).isNumber();
		if (number != null)
			return (int) number.doubleValue();
		else {
			String message = "JSON object '"+o.toString()+"' does not have an int valued property '"+key+"'.";
			logger.severe(message);
			throw new JsonizationException(message);
		}
	}

	@Override
	public String getString(JSONObject o, String key)	throws JsonizationException {
		JSONString string = o.get(key).isString();
		if (string != null)
			return string.stringValue();
		else {
			String message = "JSON object '"+o.toString()+"' does not have an string valued property '"+key+"'.";
			logger.severe(message);
			throw new JsonizationException(message);
		}
	}

	@Override
	public String getOptString(JSONObject json, String key, String defaultValue) throws JsonizationException {
		JSONString string = json.get(key).isString();
		if (string == null)
			return defaultValue;
		else 
			return string.stringValue();
	}
	
	
	@Override
	public JSONArray newJsonArray() throws JsonizationException {
		return new JSONArray();
	}

	@Override
	public void push(JSONArray a, JSONObject o) throws JsonizationException {
		a.set(a.size(), o);
	}

	@Override
	public void put(JSONObject o, String key, Object value) throws JsonizationException {
		JSONValue jsonValue;
		if (value instanceof String) {
			jsonValue = new JSONString((String) value);
		} else if (value instanceof Boolean) {
			jsonValue = JSONBoolean.getInstance((Boolean) value);
		} else if (value instanceof Double) {
			jsonValue = new JSONNumber((Double) value);
		} else if (value instanceof Integer) {
			jsonValue = new JSONNumber((Integer) value);
		} else if (value instanceof JSONValue){
			jsonValue = (JSONValue) value;
		} else if (value == null){
			jsonValue = null;
		} else {
			String message = "Unable to jsonize '"+value.toString()+"' of type '"+value.getClass()+"'";
			logger.severe(message);
			throw new JsonizationException(message);
		}
		o.put(key, jsonValue );
	}

	@Override
	public JSONObject newJsonObject() throws JsonizationException {
		return new JSONObject();
	}

	@Override
	public JSONObject getObject(JSONObject json, String key)	throws JsonizationException {
		JSONObject object = json.get(key).isObject();
		if (object != null)
			return object;
		else {
			String message = "JSON object '"+json.toString()+"' does not have a object valued entry '"+key+"'.";
			logger.severe(message);
			throw new JsonizationException(message);
		}
	}

	@Override
	public JSONObject parse(String jsonPayload) throws JsonizationException {
		logger.info("parsing json " + jsonPayload);
		JSONObject object = JSONParser.parseStrict(jsonPayload).isObject();
		if (object != null) {
			logger.info("result:" + object.toString());
			return object;
		} else {
			String message = "JSON string '"+jsonPayload+"' cannot be parsed into an object.";
			logger.severe(message);
			throw new JsonizationException(message);
		}
	}

	@Override
	public Set<String> getKeys(JSONObject json) {
		return json.keySet();
	}
}
