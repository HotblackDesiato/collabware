package collabware.web.cometd.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import collabware.web.shared.JsonProvider;
import collabware.web.shared.JsonizationException;

public class ServerJsonProvider implements JsonProvider<JSONObject, JSONArray> {
	@Override
	public JSONObject newJsonObject() {
		return new JSONObject();
	}

	@Override
	public void put(JSONObject o, String key, Object value) throws JsonizationException {
		try {
			o.put(key, value);
		} catch (JSONException e) {
			throw new JsonizationException(e);
		}
	}
	
	@Override
	public void push(JSONArray a, JSONObject o) {
		a.put(o);
	}

	@Override
	public JSONArray newJsonArray() {
		return new JSONArray();
	}
	
	@Override
	public String getOptString(JSONObject json, String key, String defaultValue) throws JsonizationException {
		return json.optString(key, defaultValue);
	}
	
	@Override
	public String getString(JSONObject json, String key) throws JsonizationException {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			throw new JsonizationException(e);
		}
	}
	
	@Override
	public int getInt(JSONObject json, String key) throws JsonizationException {
		try {
			return json.getInt(key);
		} catch (JSONException e) {
			throw new JsonizationException(e);
		}
	}
	
	@Override
	public JSONArray getArray(JSONObject o, String key) throws JsonizationException {
		try {
			return o.getJSONArray(key);
		} catch (JSONException e) {
			throw new JsonizationException(e);
		}
	}
	
	@Override
	public JSONObject getObject(JSONArray a, int i) throws JsonizationException {
		try {
			return a.getJSONObject(i);
		} catch (JSONException e) {
			throw new JsonizationException(e);
		}
	}
	
	@Override
	public int length(JSONArray a) {
		return a.length();
	}

	@Override
	public Object getOpt(JSONObject json, String key) {
		return json.opt(key);
	}

	@Override
	public JSONObject getObject(JSONObject json, String key) throws JsonizationException {
		try {
			return json.getJSONObject(key);
		} catch (JSONException e) {
			throw new JsonizationException(e);
		}
	}

	@Override
	public JSONObject parse(String jsonPayload) throws JsonizationException {
		try {
			return new JSONObject(jsonPayload);
		} catch (JSONException e) {
			throw new JsonizationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getKeys(JSONObject json) {
		Set<String> keys = new HashSet<String>();
		for (Iterator<String> i = json.keys(); i.hasNext();)
			keys.add(i.next());
		return keys;
	}
}
