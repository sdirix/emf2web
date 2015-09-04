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
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecp.emf2web.exporter.SchemaWrapper

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
	@Accessors(PUBLIC_GETTER)val SchemaWrapper wrapper
	@Accessors var String generatedString
	@Accessors var URI location
	@Accessors var boolean wrap
	
	val public static final String MODEL_TYPE = "Model"
	val public static final String VIEW_TYPE = "View";
	val public static final String MODEL_AND_VIEW_TYPE = "Model and View"
}