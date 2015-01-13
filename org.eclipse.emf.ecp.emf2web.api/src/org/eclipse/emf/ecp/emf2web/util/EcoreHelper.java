/*******************************************************************************
 * Copyright (c) 2014-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Stefan Dirix - initial API and implementation
 *
 *******************************************************************************/
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
		for (final Entry<String, Object> entry : attributes.entrySet()) {
			if (entry.getKey().equals("id")) {
				// skip because not present in eObject
				continue;
			}
			for (final EAttribute eAttribute : eObject.eClass().getEAllAttributes()) {
				if (eAttribute.getName().equals(entry.getKey())) {
					Object attributeValue;
					try {
						attributeValue = ClassMapping.convertObject(eAttribute,
							entry.getValue());
						if (attributeValue != null) {
							eObject.eSet(eAttribute, attributeValue);
						}
					} catch (final ParseException e) {
						e.printStackTrace();
					} catch (final DatatypeConfigurationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
