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

	private String url;
	private Map<EObject, WebInfo> infoMapping;

	public WebHandler(String url) {
		this.url = url;
		infoMapping = new HashMap<EObject, WebInfo>();
	}

	public int getNumberOfWebElements(EClass eClass) throws IOException {
		return getNumberOfWebElements(eClass.getName());
	}

	public int getNumberOfWebElements(String nameOfEClass) throws IOException {
		JSONCrudOperator operator = new JSONCrudOperator();
		return operator.readElements(url, nameOfEClass.toLowerCase()).size();
	}

	public List<EObject> getWebElements(EClass eClass) throws IOException {
		List<EObject> result = new ArrayList<EObject>();

		JSONCrudOperator operator = new JSONCrudOperator();
		List<Map<String, Object>> jsonElements = operator.readElements(url,
			eClass.getName().toLowerCase());

		for (Map<String, Object> jsonElement : jsonElements) {
			EObject eObject = EcoreUtil.create(eClass);
			EcoreHelper.setAttributes(eObject, jsonElement);

			String id = getId(jsonElement);
			if (id == null) {
				System.out.println("Could not determine id for element");
			}

			WebInfo info = new WebInfo(id, eClass.getName().toLowerCase());
			infoMapping.put(eObject, info);
			result.add(eObject);
		}

		return result;
	}

	public boolean createWebElement(EObject eObject) throws IOException {
		Map<String, Object> jsonDescription = ClassMapping
			.eObjectToMap(eObject);

		String type = eObject.eClass().getName().toLowerCase();

		JSONCrudOperator operator = new JSONCrudOperator();
		Map<String, Object> response = operator.createElement(url, type,
			jsonDescription);

		String id = getId(response);

		WebInfo info = new WebInfo(type, id);
		infoMapping.put(eObject, info);

		return true;
	}

	public boolean updateWebElement(EObject eObject) throws IOException {
		Map<String, Object> jsonDescription = ClassMapping
			.eObjectToMap(eObject);

		WebInfo webInfo = infoMapping.get(eObject);

		String id = webInfo.getId();
		String type = webInfo.getType();

		jsonDescription.put("id", id);

		JSONCrudOperator operator = new JSONCrudOperator();
		return operator.updateElement(url, type, id, jsonDescription);
	}

	private String getId(Map<String, Object> jsonElement) {
		for (Entry<String, Object> attribute : jsonElement.entrySet()) {
			if (attribute.getKey().equals("id")) {
				return attribute.getValue().toString();
			}
		}
		return null;
	}

	private class WebInfo {
		private String id;
		private String type;

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
