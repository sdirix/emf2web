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

import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 * 
 */
class ScalaControllerGenerator extends AbstractScalaFileGenerator {

	@Accessors String modelObjectName
	@Accessors String formsObjectName

	new(String packageName, String importInstructions) {
		super(packageName, importInstructions)
	}

	/** 
	 *  Generates the Scala file which links between the routes and the generated qb schema and view model files.
	 * */
	def String generateControllerFile(String schemaIdentifier) {
		'''
			«generatePackage»
			
			«importInstructions»
			
			object «schemaIdentifier»Controller extends MongoController with QBCrudController {
			
			  lazy val collection = new QBMongoCollection("«schemaIdentifier.toLowerCase»")(db) with QBCollectionValidation {
			    override def schema = «schemaIdentifier»Schema.«modelObjectName»
			  }
			
			  override def createSchema = «schemaIdentifier»Schema.«modelObjectName» -- "id"
			
			  def getView = JsonHeaders {
			    Action {
			      Ok(Json.toJson(«schemaIdentifier»Schema.«formsObjectName»))
			    }
			  }
			  
			  def getModel = JsonHeaders {
			    Action {
			      Ok(Json.toJson(«schemaIdentifier»Schema.«modelObjectName»))
			    }
			  }
			}
			
			object «schemaIdentifier»Router extends QBRouter {
			  override def qbRoutes = «schemaIdentifier»Controller.crudRoutes
			}
		'''
	}

}