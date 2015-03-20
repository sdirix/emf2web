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
package org.eclipse.emf.ecp.emf2web.generator

import java.io.File
import java.util.List
import org.apache.commons.io.FileUtils
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecp.emf2web.export.NameHelper
import org.eclipse.emf.ecp.view.spi.model.VContainedElement
import org.eclipse.emf.ecp.view.spi.model.VContainer
import org.eclipse.emf.ecp.view.spi.model.VControl
import org.eclipse.emf.ecp.view.spi.model.VElement
import org.eclipse.emf.ecp.view.spi.model.VView
import com.google.gson.JsonParser
import com.google.gson.GsonBuilder

/** 
 * The class which handles the conversion from ecore files to qbForm files.
 * 
 * */
class FormsJsonGenerator {
	
	new(NameHelper nameHelper) {
		this.nameHelper = nameHelper;
	}

	NameHelper nameHelper
	
	//TODO: Use Gson library to build Json instead of templating it via Xtend. See EcoreJsonGenerator as an example.
	 * Generates the default QB view model for the given {@link EClass}.
	def String generate(VView view) {
		val parser = new JsonParser
		val json = parser.parse(buildViewModelElement(view)).asJsonObject
		val gson = new GsonBuilder().setPrettyPrinting().create()
		gson.toJson(json)
	}
	
	def protected String buildViewModelElement(VView view){
		'''
			{
			  "elements": [
			    «buildChildren(view.children)»
				]
			)
		'''
	}

	def protected String buildViewModelElement(VElement vElement) {
		switch vElement {
			VContainer: buildContainer(vElement)
			VControl: buildControl(vElement)
		}
	}
	
	def protected String buildControl(VControl control){
		val EStructuralFeature feature = control.domainModelReference.EStructuralFeatureIterator.next
		val EClass eClass = feature.EContainingClass
		val String name =nameHelper.getDisplayName(eClass, feature)
		buildControl(name, feature.name)
	}

	def protected String buildControl(String name, String path) {
		'''
			{
			  "type": "Control",
			  "path": "«path»",
			  "name": "«name»"
			}
		'''
	}

	def protected String buildContainer(VContainer container) {
		'''
			{
			  "type": "«getQBType(container)»",
			  "elements": [
			    «buildChildren(container.children)»
			  ]
			}
		'''
	}
	
	def String getQBType(VElement vElement) {
		"QB"+vElement.eClass.name
	}

	def String buildChildren(List<VContainedElement> children) {
		'''
			«FOR element : children SEPARATOR ','»
			  «buildViewModelElement(element)»
			«ENDFOR»
		'''
	}
}
