/**
 * 
 */
package pl.doa.renderer.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author activey
 * 
 */
public class JSONObjectWrapper extends JSONObject {

	public JSONArrayWrapper putArray(String key) throws JSONException {
		JSONArrayWrapper array = new JSONArrayWrapper();
		put(key, array);
		return array;
	}

	public JSONObject putObject(String key) throws JSONException {
		JSONObjectWrapper obj = new JSONObjectWrapper();
		put(key, obj);
		return obj;
	}

	public void put(String key, Object value, Object whenNullValue)
			throws JSONException {
		if (value == null) {
			put(key, whenNullValue);
			return;
		}
		put(key, value);
	}
}
