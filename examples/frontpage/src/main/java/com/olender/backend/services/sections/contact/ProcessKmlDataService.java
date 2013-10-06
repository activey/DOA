/**
 * 
 */
package com.olender.backend.services.sections.contact;

import java.util.List;

import pl.doa.GeneralDOAException;
import pl.doa.document.IDocument;
import pl.doa.resource.IStaticResource;

import com.olender.backend.services.BaseServiceDefinitionLogic;
import com.olender.backend.utils.kml.KmlExt;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;

/**
 * @author activey
 * 
 */
public class ProcessKmlDataService extends BaseServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();
		final IDocument existingSection =
				(IDocument) input.getFieldValue("section");
		IStaticResource kmlFile =
				(IStaticResource) input.getFieldValue("kmlFile");

		Kml kml = KmlExt.unmarshalEx(kmlFile.getContentStream());

		Feature feature = kml.getFeature();
		if (feature instanceof Document) {
			double latitude = 0;
			double longitude = 0;
			String description = feature.getDescription();

			Document doc = (Document) feature;
			List<Feature> docFeatures = doc.getFeature();
			for (Feature docFeature : docFeatures) {
				if (docFeature instanceof Placemark) {
					Placemark placemark = (Placemark) docFeature;
					Geometry geometry = placemark.getGeometry();
					if (geometry instanceof Point) {
						Point point = (Point) geometry;
						List<Coordinate> coordinates = point.getCoordinates();
						for (Coordinate coordinate : coordinates) {
							latitude = coordinate.getLatitude();
							longitude = coordinate.getLongitude();
							break;
						}
					}
				}
			}

			existingSection.setFieldValue("latitude", latitude);
			existingSection.setFieldValue("longitude", longitude);
			existingSection.setFieldValue("description", description);
		}
		setOutput(existingSection);
	}

}
