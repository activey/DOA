/**
 *
 */
package pl.doa.mapper.json;

import org.json.JSONException;
import org.json.JSONObject;
import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.IDocumentFieldValue;

import java.util.Iterator;

/**
 * @author activey
 */
public class DocumentMapper extends BasicEntityMapper<IDocument> {

    @Override
    public void map(IDocument entity, JSONObject object)
            throws GeneralDOAException {
        super.map(entity, object);

        IDocumentDefinition definition = entity.getDefinition();
        try {
            object.put("definitionId", definition.getId());
            object.put("definitionLocation", definition.getLocation());

            JSONObject fieldsJson = new JSONObject();
            Iterator<IDocumentFieldValue> fields = entity.getFields();
            while (fields.hasNext()) {
                IDocumentFieldValue field = fields.next();
                fieldsJson.put(field.getFieldName(), field.getFieldValue());

            }
            object.put("fields", fieldsJson);
        } catch (JSONException e) {
            throw new GeneralDOAException(e);
        }

    }

    @Override
    protected IDocument map(JSONObject object, IDocument entity)
            throws Exception {
        super.map(object, entity);

        JSONObject fieldsJson = object.getJSONObject("fields");
        Iterator<String> namesJson = fieldsJson.keys();
        while (namesJson.hasNext()) {
            String nameJson = (String) namesJson.next();
            Object valueJson = fieldsJson.get(nameJson);
            entity.setFieldValue(nameJson, valueJson);
        }
        return entity;
    }
}
