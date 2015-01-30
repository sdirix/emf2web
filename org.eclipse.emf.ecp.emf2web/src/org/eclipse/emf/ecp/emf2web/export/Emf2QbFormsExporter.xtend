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
package org.eclipse.emf.ecp.emf2web.export

import java.io.File
import java.util.Set
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.resource.Resource
import org.apache.commons.io.FileUtils
import org.eclipse.emf.ecp.view.spi.model.VView
import org.eclipse.emf.ecp.view.spi.model.VControl
import org.eclipse.emf.ecp.view.spi.horizontal.model.VHorizontalLayout
import org.eclipse.emf.ecp.view.spi.vertical.model.VVerticalLayout
import org.eclipse.emf.ecp.view.spi.model.VElement
import org.eclipse.emf.ecp.view.spi.model.VContainer
import org.eclipse.emf.ecore.EStructuralFeature
import java.util.List
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecp.view.spi.model.VContainedElement

/** 
 * The class which handles the conversion from ecore files to qbForm files.
 * 
 * */
class Emf2QbFormsExporter {
	
	new(NameHelper nameHelper) {
		this.nameHelper = nameHelper;
	}

	/**
	 * The main method which handles the conversion and export of the ecore files.
	 *
	 * @param ecoreModel
	 *            The {@link Resource} containing the ecore model.
	 * @param selectedClasses
	 *            Collection of classes which shall be converted to the qb format.
	 * @param viewModels
	 *            Collection of view models for the {@code selectedClasses} which shall not use the default layout.
	 * @param destinationDir
	 *            The directory where the converted files shall be saved.
	 * */
	NameHelper nameHelper

	def public void exportViewModel(VView view,
		File destinationDir) {
		val controllerDest = "public/" + view.name+".json"
		val viewModel = buildViewModel(view)
		FileUtils.writeStringToFile(new File(destinationDir, controllerDest), viewModel);
	}

	/** 
	 * Generates the default QB view model for the given {@link EClass}.
	 * 
	 * */
	def protected String buildViewModel(VView view) {
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
