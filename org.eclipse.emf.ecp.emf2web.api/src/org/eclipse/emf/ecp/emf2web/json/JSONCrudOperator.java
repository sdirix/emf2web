package org.eclipse.emf.ecp.emf2web.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONCrudOperator {

	public List<Map<String, Object>> readElements(String url, String type)
			throws IOException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url + "/" + type);

			JSONResponseHandler<List<Map<String, Object>>> responseHandler = new JSONResponseHandler<List<Map<String, Object>>>();

			List<Map<String, Object>> resultList = responseHandler
					.handleResponse(httpClient.execute(httpGet));

			for (Map<String, Object> object : resultList) {
				if (object == null) {
					continue;
				}

				result.add(object);
			}
		} finally {
			httpClient.close();
		}

		return result;
	}

	public Map<String, Object> createElement(String url, String type,
			Map<String, Object> jsonDescription) throws IOException {
		String completeUrl = url + "/" + type;
		Map<String, Object> response = sendPost(completeUrl, jsonDescription);
		return response;
	}

	public boolean updateElement(String url, String type, String id,
			Map<String, Object> element) throws IOException {
		String completeUrl = url + "/" + type + "/" + id;
		sendPost(completeUrl, element);
		return true;
	}

	/**
	 * @deprecated not yet implemented
	 */
	public boolean deleteElement(String url, String type, String id,
			Map<String, Object> element) throws IOException {
		return false;
	}

	private Map<String, Object> sendPost(String completeUrl,
			Map<String, Object> element) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost postRequest = new HttpPost(completeUrl);

			Gson gson = new GsonBuilder().create();
			String json = gson.toJson(element);

			postRequest.setEntity(new StringEntity(json, ContentType
					.create("application/json")));

			JSONResponseHandler<Map<String, Object>> responseHandler = new JSONResponseHandler<Map<String, Object>>();

			Map<String, Object> response = responseHandler
					.handleResponse(httpClient.execute(postRequest));

			return response;
		} finally {
			httpClient.close();
		}
	}
}
