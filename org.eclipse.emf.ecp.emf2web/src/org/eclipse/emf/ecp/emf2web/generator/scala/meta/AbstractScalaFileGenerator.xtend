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

import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author Stefan Dirix
 * 
 */
abstract class AbstractScalaFileGenerator {
	@Accessors String packageName;
	@Accessors String importInstructions;

	new(String packageName, String importInstructions) {
		this.packageName = packageName
		this.importInstructions = importInstructions
	}

	def String generatePackage() {
		'''
			package «packageName»
		'''
	}
}