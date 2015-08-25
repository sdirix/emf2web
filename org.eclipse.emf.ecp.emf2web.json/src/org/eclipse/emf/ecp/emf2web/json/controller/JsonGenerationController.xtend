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
package org.eclipse.emf.ecp.emf2web.json.controller

import java.util.Collection
import java.util.LinkedList
import java.util.List
import org.eclipse.emf.ecp.emf2web.controller.AbstractGenerationController
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo
import org.eclipse.emf.ecp.emf2web.json.generator.EcoreJsonGenerator
import org.eclipse.emf.ecp.emf2web.json.generator.FormsJsonGenerator
import org.eclipse.emf.ecp.emf2web.json.util.ReferenceHelperImpl
import org.eclipse.emf.ecp.view.spi.model.VView

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 *
 */
class JsonGenerationController extends AbstractGenerationController {
	
	override List<GenerationInfo> generate(Collection<? extends VView> views) {
		val result = new LinkedList<GenerationInfo>

		val modelGenerator = new EcoreJsonGenerator
		val formsGenerator = new FormsJsonGenerator(new ReferenceHelperImpl)

		for (view : views) {
			val eClass = view.rootEClass
			val schemaIdentifier = eClass.name

			val schemaFile = modelGenerator.generate(eClass)
			val schemaInfo = new GenerationInfo(GenerationInfo.MODEL_TYPE, eClass, null,
				schemaIdentifier + "Model.json")
			schemaInfo.generatedString = schemaFile
			result.add(schemaInfo)

			val controllerFile = formsGenerator.generate(view)
			val controllerInfo = new GenerationInfo(GenerationInfo.VIEW_TYPE, null, view,
				schemaIdentifier + "View.json")
			controllerInfo.generatedString = controllerFile
			result.add(controllerInfo)
		}

		result
	}
	
}