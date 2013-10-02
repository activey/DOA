package pl.doa.servlet.filter.processor.rest.json.simple;

import org.json.JSONException;
import org.json.JSONObject;

import pl.doa.servlet.filter.processor.rest.json.JsonResponse;

public class SimpleValueResponse<T extends Object> extends JsonResponse<T> {

    public SimpleValueResponse(T object) {
        super(object);
    }

    @Override
    protected void buildJSON(JSONObject json) throws JSONException {
        json.put("type", "simple");
        json.put("value", object);
    }

}
