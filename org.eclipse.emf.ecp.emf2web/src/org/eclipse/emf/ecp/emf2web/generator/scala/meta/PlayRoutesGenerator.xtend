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
package org.eclipse.emf.ecp.emf2web.generator.scala.meta

import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import java.util.List

/**
 * @author Stefan Dirix
 * 
 */
class PlayRoutesGenerator {

	def String buildRoutesFile(Collection<String> controllerIdentifier) {
		val orderedIdentifier = sortInNew(controllerIdentifier)
		'''
			«routesIntro»
					
			«FOR eClassName : orderedIdentifier»
				GET     /«eClassName.toLowerCase»/model		controllers.«eClassName»Controller.getModel
				GET     /«eClassName.toLowerCase»/view		controllers.«eClassName»Controller.getView
				->		/«eClassName.toLowerCase»			controllers.«eClassName»Router
				
			«ENDFOR»	
		'''
	}

	def private List<String> sortInNew(Collection<String> collection) {
		val orderedCollection = new ArrayList<String>(collection)
		Collections.sort(orderedCollection)
		orderedCollection
	}

	def private String routesIntro() {
		'''
			# Routes
			# This file defines all application routes (Higher priority routes first)
			# ~~~~
			
			# Home page
			GET     /                           controllers.Application.index
			
			# Map static resources from the /public folder to the /assets URL path
			GET     /assets/*file               controllers.Assets.at(path="/public", file)
			
		'''
	}
}