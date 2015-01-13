package org.eclipse.emf.ecp.emf2web.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONResponseHandler<T> implements ResponseHandler<T> {

	@Override
	public T handleResponse(HttpResponse response)
		throws ClientProtocolException, IOException {
		StatusLine statusLine = response.getStatusLine();

		HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
			throw new HttpResponseException(statusLine.getStatusCode(),
				statusLine.getReasonPhrase());
		}
		if (entity == null) {
			throw new ClientProtocolException("Response contains no content");
		}

		Gson gson = new GsonBuilder().create();
		ContentType contentType = ContentType.getOrDefault(entity);
		Charset charset = contentType.getCharset();

		Reader reader = new InputStreamReader(entity.getContent(), charset);

		return gson.fromJson(reader, new TypeToken<T>() {
		}.getType());
	}
}
