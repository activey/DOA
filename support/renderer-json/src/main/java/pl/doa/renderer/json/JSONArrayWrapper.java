/**
 * 
 */
package pl.doa.renderer.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author activey
 * 
 */
public class JSONArrayWrapper extends JSONArray {

	public JSONObject putObject() throws JSONException {
		JSONObjectWrapper obj = new JSONObjectWrapper();
		put(obj);
		return obj;
	}

	public void put(Object value, Object whenNullValue)
			throws JSONException {
		if (value == null) {
			put(whenNullValue);
			return;
		}
		put(value);
	}
}
