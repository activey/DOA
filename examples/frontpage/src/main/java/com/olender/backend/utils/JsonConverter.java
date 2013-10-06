/**
 * 
 */
package com.olender.backend.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.doa.IDOA;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.DocumentFieldDataType;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;

/**
 * @author activey
 * 
 */
public class JsonConverter {

	public IDocument readDocument(IDOA doa, JSONObject jsonObject)
			throws Exception {
		String definitionLocation = jsonObject.getString("definition");
		IDocumentDefinition definition =
				(IDocumentDefinition) doa
						.lookupEntityByLocation(definitionLocation);

		String name = jsonObject.getString("name");
		IDocument document = definition.createDocumentInstance(name);

		// czytanie pol dokumentu
		JSONObject fields = jsonObject.getJSONObject("fields");
		if (fields != null && fields.names() != null
				&& fields.names().length() > 0) {
			JSONArray fieldsNames = fields.names();
			for (int i = 0; i < fieldsNames.length(); i++) {
				String fieldName = fieldsNames.getString(i);
				JSONObject field = fields.getJSONObject(fieldName);
				Object fieldValue = field.get("value");

				String fieldType = field.getString("type");
				if (DocumentFieldDataType.list.name().equals(fieldType)) {
					IListDocumentFieldValue listField =
							(IListDocumentFieldValue) document.getField(
									fieldName, true);
					JSONArray listValue = (JSONArray) fieldValue;
					for (int j = 0; j < listValue.length(); j++) {
						JSONObject innerJsonField = listValue.getJSONObject(j);

						String innerFieldName =
								innerJsonField.names().getString(0);
						innerJsonField =
								innerJsonField.getJSONObject(innerFieldName);
						Object innerFieldValue = innerJsonField.get("value");
						String innerFieldType =
								innerJsonField.getString("type");

						IDocumentFieldValue innerField =
								listField.addField(innerFieldName,
										DocumentFieldDataType
												.valueOf(innerFieldType));
						innerField.setFieldValue(innerFieldValue);

					}
					continue;
				}

				// ustawianie wartosci pola w detached document
				document.setFieldValue(fieldName, fieldValue);
			}
		}

		return document;
	}
}
