/**
 * 
 */
package com.olender.backend.utils.kml;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.micromata.opengis.kml.v_2_2_0.Kml;

/**
 * @author activey
 * 
 */
public class KmlExt extends Kml {

	public static Kml unmarshalEx(final InputStream content) {
		try {
			Unmarshaller unmarshaller =
					JAXBContext.newInstance((Kml.class)).createUnmarshaller();
			InputSource input = new InputSource(content);
			SAXSource saxSource =
					new SAXSource(new NamespaceFilterXMLReaderEx(false), input);
			Kml jaxbRootElement = ((Kml) unmarshaller.unmarshal(saxSource));
			return jaxbRootElement;
		} catch (SAXException _x) {
			_x.printStackTrace();
		} catch (ParserConfigurationException _x) {
			_x.printStackTrace();
		} catch (JAXBException _x) {
			_x.printStackTrace();
		}
		return null;
	}
}
