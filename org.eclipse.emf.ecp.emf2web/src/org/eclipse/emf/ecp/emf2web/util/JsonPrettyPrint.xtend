/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Stefan Dirix - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.ecp.emf2web.util

import com.google.gson.JsonParser
import com.google.gson.GsonBuilder

/**
 * @author Stefan Dirix
 *
 */
class JsonPrettyPrint {
	
	def static String jsonPrettyPrint(CharSequence sequence){
		jsonPrettyPrint(sequence.toString)
	}
	
	def static String jsonPrettyPrint(String jsonString){
		if(jsonString.equals("")){
			return jsonString
		}
		val parser = new JsonParser
		val json = parser.parse(jsonString).asJsonObject
		val gson = new GsonBuilder().setPrettyPrinting().create()
		gson.toJson(json)
	}
}