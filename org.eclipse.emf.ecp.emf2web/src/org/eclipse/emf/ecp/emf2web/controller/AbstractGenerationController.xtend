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
package org.eclipse.emf.ecp.emf2web.controller

import java.util.Collection
import java.util.Map
import org.eclipse.emf.ecp.view.spi.model.VView
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 * 
 */
abstract class AbstractGenerationController {
	protected val Collection<VView> views

	@FinalFieldsConstructor
	new() {
	}

	abstract def Map<GenerationInfo, String> generateFiles()
}