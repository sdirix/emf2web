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
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EcorePackage

/** 
 * The class which handles the conversion from ecore files to qbForm files.
 * 
 * */
class EcoreJSonExporter {

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
	def public void exportEcoreModel(EClass eClass, File destinationDir) {
		val controllerDest = "public/" + eClass.name + ".json"

	// val viewModel = buildEcoreModel(view)
	// FileUtils.writeStringToFile(new File(destinationDir, controllerDest), viewModel);
	}

	def String buildEClass(EClass eClass) '''
		{
		  "type": "object",
		  "properties": {
		    «FOR eAttribute : eClass.EAllAttributes SEPARATOR ','»
		    	«buildEAttribute(eAttribute)»
		    «ENDFOR»
		  }
		}
	'''

	def String buildEAttribute(EAttribute eAttribute) {
		if (eAttribute.EAttributeType.equals(EcorePackage.eINSTANCE.EDate)) {
			return buildDateEAttribute(eAttribute.name)
		}
		return buildEAttribute(eAttribute.name, getQBType(eAttribute.getEAttributeType().name))
	}

	def String getQBType(String name) {
		name.toLowerCase.substring(1)
	}

	def String buildEAttribute(String name, String type) '''
		"«name»": {"type": "«type»"}
	'''

	def String buildEnum(String name, List<String> enumValues) '''
		"«name»": {
		  "type": "string",
		  "enum": [
		    «FOR value : enumValues SEPARATOR ','»
		    	"«value»"
		    «ENDFOR»
		  ]
		}
	'''

	def String buildDateEAttribute(String name) '''
		"«name»": {
		  "type": "string",
		  "format": "date-time"
		}
	'''

}
