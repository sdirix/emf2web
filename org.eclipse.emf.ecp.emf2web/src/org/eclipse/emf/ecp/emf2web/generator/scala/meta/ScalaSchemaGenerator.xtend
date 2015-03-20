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

import org.eclipse.emf.ecp.emf2web.export.NameHelperImpl
import org.eclipse.emf.ecp.emf2web.generator.scala.EcoreScalaGenerator
import org.eclipse.emf.ecp.emf2web.generator.scala.FormsScalaGenerator
import org.eclipse.emf.ecp.view.spi.model.VView
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 * 
 */
class ScalaSchemaGenerator extends AbstractScalaFileGenerator {

	@Accessors String ecoreObjectName
	@Accessors String formsObjectName

	new(String packageName, String importInstructions, String ecoreObjectName, String formsObjectName) {
		super(packageName, importInstructions)
		this.ecoreObjectName = ecoreObjectName
		this.formsObjectName = formsObjectName
	}

	def String generateSchemaFile(VView view, String schemaObjectName) {
		'''
			«generatePackage»
			«importInstructions»
			«generateSchemaObject(view, schemaObjectName)»
		'''
	}

	def String generateSchemaObject(VView view, String schemaObjectName) {
		'''
			object «schemaObjectName» {
					«generateModelObjects(view)»
			}
		'''
	}

	private def String generateModelObjects(VView view) {
		val eClass = view.rootEClass
		val ecoreGenerator = new EcoreScalaGenerator
		val formsGenerator = new FormsScalaGenerator(new NameHelperImpl)
		'''
			val «ecoreObjectName» = «ecoreGenerator.generate(eClass)»
			val «formsObjectName» = «formsGenerator.generate(view, ecoreObjectName)»
		'''
	}

}