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

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecp.view.spi.model.VView
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 * 
 */
@Accessors
class GenerationInfo {
	@Accessors(PUBLIC_GETTER)val String type
	@Accessors(PUBLIC_GETTER)val EClass eClass
	@Accessors(PUBLIC_GETTER)val VView view
	@Accessors(PUBLIC_GETTER)val String nameProposal
	@Accessors var String generatedString
	@Accessors var String location
	
	val public static final String MODEL_TYPE = "Model"
	val public static final String VIEW_TYPE = "View";
	val public static final String MODEL_AND_VIEW_TYPE = "Model and View"
}