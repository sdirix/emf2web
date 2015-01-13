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
		final StatusLine statusLine = response.getStatusLine();

		final HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
			throw new HttpResponseException(statusLine.getStatusCode(),
				statusLine.getReasonPhrase());
		}
		if (entity == null) {
			throw new ClientProtocolException("Response contains no content");
		}

		final Gson gson = new GsonBuilder().create();
		final ContentType contentType = ContentType.getOrDefault(entity);
		final Charset charset = contentType.getCharset();

		final Reader reader = new InputStreamReader(entity.getContent(), charset);

		return gson.fromJson(reader, new TypeToken<T>() {
		}.getType());
	}
}
