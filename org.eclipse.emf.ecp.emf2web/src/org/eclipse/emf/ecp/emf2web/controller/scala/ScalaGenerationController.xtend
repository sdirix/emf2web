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
package org.eclipse.emf.ecp.emf2web.controller.scala

import java.util.Collection
import java.util.HashMap
import java.util.HashSet
import java.util.Map
import org.eclipse.emf.ecp.emf2web.controller.AbstractGenerationController
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfoType
import org.eclipse.emf.ecp.view.spi.model.VView

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 * 
 */
class ScalaGenerationController extends AbstractGenerationController {

	protected val modelObjectName = "modelSchema"
	protected val formsObjectName = "viewSchema"
	protected val schemaPackageName = "controllers"
	protected val controllerPackageName = "controllers"

	protected val schemaImport = '''
		import controllers.QBView._
		import org.qbproject.api.schema.QBSchema._
		import org.qbproject.api.mongo.MongoSchemaExtensions._
	'''

	protected val controllerImport = '''
		import org.qbproject.api.controllers.{JsonHeaders, QBCrudController}
		import org.qbproject.api.mongo.{QBCollectionValidation, QBMongoCollection}
		import org.qbproject.api.routing.QBRouter
		import org.qbproject.api.schema.QBSchema._
		import play.api.libs.concurrent.Execution.Implicits.defaultContext
		import play.api.libs.json.{JsUndefined, JsValue, Json}
		import play.api.mvc.Action
		import play.modules.reactivemongo.MongoController
	'''

	new(Collection<VView> views) {
		super(views)
	}

	override Map<GenerationInfo, String> generateFiles() {
		val result = new HashMap<GenerationInfo, String>

		val schemaGenerator = new ScalaSchemaGenerator(schemaPackageName, schemaImport, modelObjectName,
			formsObjectName)
		val controllerGenerator = new ScalaControllerGenerator(controllerPackageName, controllerImport)
		val routesGenerator = new PlayRoutesGenerator

		val generatedSchemas = new HashSet<String>

		for (view : views) {
			val eClass = view.rootEClass
			val schemaIdentifier = eClass.name

			val schemaFile = schemaGenerator.generateSchemaFile(view, schemaIdentifier)
			val schemaInfo = new GenerationInfo(GenerationInfoType.MODEL_AND_VIEW, eClass, view,
				schemaIdentifier + "Schema.scala")
			result.put(schemaInfo, schemaFile)

			val controllerFile = controllerGenerator.generateControllerFile(schemaIdentifier)
			val controllerInfo = new GenerationInfo(GenerationInfoType.META_CONTROLLER, null, null,
				schemaIdentifier + "Controller.scala")
			result.put(controllerInfo, controllerFile)

			generatedSchemas.add(schemaIdentifier)
		}

		val routesFile = routesGenerator.buildRoutesFile(generatedSchemas)
		val routesInfo = new GenerationInfo(GenerationInfoType.META_ROUTES, null, null, "routes")
		result.put(routesInfo, routesFile)

		result
	}

}