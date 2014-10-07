package org.eclipse.emf.ecp.emf2web.util;

import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

public class EcoreHelper {

	public static void setAttributes(EObject eObject,
			Map<String, Object> attributes) {
		for (Entry<String, Object> entry : attributes.entrySet()) {
			if (entry.getKey().equals("id")) {
				// skip because not present in eObject
				continue;
			}
			for (EAttribute eAttribute : eObject.eClass().getEAllAttributes()) {
				if (eAttribute.getName().equals(entry.getKey())) {
					Object attributeValue;
					try {
						attributeValue = ClassMapping.convertObject(eAttribute,
								entry.getValue());
						if (attributeValue != null) {
							eObject.eSet(eAttribute, attributeValue);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (DatatypeConfigurationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
