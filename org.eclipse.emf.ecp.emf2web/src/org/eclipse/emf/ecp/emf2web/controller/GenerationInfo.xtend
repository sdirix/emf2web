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
import org.eclipse.xtend.lib.annotations.Data

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 * 
 */
@Data
class GenerationInfo {
	val GenerationInfoType type
	val EClass eClass
	val VView view
	val String nameProposal
}

enum GenerationInfoType {
	MODEL,
	VIEW,
	MODEL_AND_VIEW,
	META_CONTROLLER,
	META_ROUTES,
	META_OTHER
}