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
package org.eclipse.emf.ecp.emf2web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.emf2web.json.JSONCrudOperator;
import org.eclipse.emf.ecp.emf2web.util.ClassMapping;
import org.eclipse.emf.ecp.emf2web.util.EcoreHelper;

public class WebHandler {

	private final String url;
	private final Map<EObject, WebInfo> infoMapping;

	public WebHandler(String url) {
		this.url = url;
		infoMapping = new HashMap<EObject, WebInfo>();
	}

	public int getNumberOfWebElements(EClass eClass) throws IOException {
		return getNumberOfWebElements(eClass.getName());
	}

	public int getNumberOfWebElements(String nameOfEClass) throws IOException {
		final JSONCrudOperator operator = new JSONCrudOperator();
		return operator.readElements(url, nameOfEClass.toLowerCase()).size();
	}

	public List<EObject> getWebElements(EClass eClass) throws IOException {
		final List<EObject> result = new ArrayList<EObject>();

		final JSONCrudOperator operator = new JSONCrudOperator();
		final List<Map<String, Object>> jsonElements = operator.readElements(url,
			eClass.getName().toLowerCase());

		for (final Map<String, Object> jsonElement : jsonElements) {
			final EObject eObject = EcoreUtil.create(eClass);
			EcoreHelper.setAttributes(eObject, jsonElement);

			final String id = getId(jsonElement);
			if (id == null) {
				System.out.println("Could not determine id for element");
			}

			final WebInfo info = new WebInfo(id, eClass.getName().toLowerCase());
			infoMapping.put(eObject, info);
			result.add(eObject);
		}

		return result;
	}

	public boolean createWebElement(EObject eObject) throws IOException {
		final Map<String, Object> jsonDescription = ClassMapping
			.eObjectToMap(eObject);

		final String type = eObject.eClass().getName().toLowerCase();

		final JSONCrudOperator operator = new JSONCrudOperator();
		final Map<String, Object> response = operator.createElement(url, type,
			jsonDescription);

		final String id = getId(response);

		final WebInfo info = new WebInfo(type, id);
		infoMapping.put(eObject, info);

		return true;
	}

	public boolean updateWebElement(EObject eObject) throws IOException {
		final Map<String, Object> jsonDescription = ClassMapping
			.eObjectToMap(eObject);

		final WebInfo webInfo = infoMapping.get(eObject);

		final String id = webInfo.getId();
		final String type = webInfo.getType();

		jsonDescription.put("id", id);

		final JSONCrudOperator operator = new JSONCrudOperator();
		return operator.updateElement(url, type, id, jsonDescription);
	}

	private String getId(Map<String, Object> jsonElement) {
		for (final Entry<String, Object> attribute : jsonElement.entrySet()) {
			if (attribute.getKey().equals("id")) {
				return attribute.getValue().toString();
			}
		}
		return null;
	}

	private class WebInfo {
		private final String id;
		private final String type;

		public WebInfo(String type, String id) {
			this.type = type;
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public String getType() {
			return type;
		}
	}
}
