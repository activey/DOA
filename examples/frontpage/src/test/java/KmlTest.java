
import com.olender.backend.utils.kml.KmlExt;
import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.FileInputStream;
import java.util.List;

public class KmlTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        KmlTest test = new KmlTest();
        test.doTest("/home/activey/Pulpit/olender.kml");
    }

    private void doTest(String kmlFileLocation) throws Exception {
        Kml kml = KmlExt.unmarshalEx(new FileInputStream(kmlFileLocation));

        Feature feature = kml.getFeature();
        if (feature instanceof Document) {
            Document doc = (Document) feature;
            List<Feature> docFeatures = doc.getFeature();
            for (Feature docFeature : docFeatures) {
                if (docFeature instanceof Placemark) {
                    Placemark placemark = (Placemark) docFeature;
                    System.out.println("placemark name = "
                            + placemark.getName());
                    Geometry geometry = placemark.getGeometry();
                    if (geometry instanceof Point) {
                        Point point = (Point) geometry;
                        List<Coordinate> coordinates = point.getCoordinates();
                        for (Coordinate coordinate : coordinates) {
                            System.out.println("latitude = "
                                    + coordinate.getLatitude());
                            System.out.println("longitude = "
                                    + coordinate.getLongitude());
                        }
                    }
                }
            }
        }

        System.out.println("name = " + feature.getName());
        System.out.println("description = " + feature.getDescription());

    }

}
