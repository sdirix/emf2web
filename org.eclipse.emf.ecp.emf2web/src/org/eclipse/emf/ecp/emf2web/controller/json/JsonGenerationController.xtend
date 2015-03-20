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
package org.eclipse.emf.ecp.emf2web.controller.json

import org.eclipse.emf.ecp.emf2web.controller.AbstractGenerationController
import java.util.Collection
import org.eclipse.emf.ecp.view.spi.model.VView
import java.util.Map
import java.util.HashMap
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo
import org.eclipse.emf.ecp.emf2web.generator.EcoreJsonGenerator
import org.eclipse.emf.ecp.emf2web.generator.FormsJsonGenerator
import org.eclipse.emf.ecp.emf2web.export.NameHelperImpl
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfoType

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 *
 */
class JsonGenerationController extends AbstractGenerationController {
	
	new(Collection<VView> views) {
		super(views)
	}
	
	override Map<org.eclipse.emf.ecp.emf2web.controller.GenerationInfo, String> generateFiles() {
		val result = new HashMap<GenerationInfo, String>

		val modelGenerator = new EcoreJsonGenerator
		val formsGenerator = new FormsJsonGenerator(new NameHelperImpl)

		for (view : views) {
			val eClass = view.rootEClass
			val schemaIdentifier = eClass.name

			val schemaFile = modelGenerator.generate(eClass)
			val schemaInfo = new GenerationInfo(GenerationInfoType.MODEL, eClass, null,
				schemaIdentifier + "Model.json")
			result.put(schemaInfo, schemaFile)

			val controllerFile = formsGenerator.generate(view)
			val controllerInfo = new GenerationInfo(GenerationInfoType.VIEW, null, null,
				schemaIdentifier + "View.json")
			result.put(controllerInfo, controllerFile)
		}

		result
	}
	
}