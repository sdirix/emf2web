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
package org.eclipse.emf.ecp.emf2web.json.util;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecp.emf2web.util.AbstractReferenceHelper;
import org.eclipse.emf.ecp.view.spi.model.VDomainModelReference;

/**
 * @author Stefan Dirix
 *
 */
public class ReferenceHelperImpl extends AbstractReferenceHelper {

	private static final String ROOT = "#"; //$NON-NLS-1$
	private static final String SEPARATOR = "/"; //$NON-NLS-1$
	private static final String PROPERTIES = "properties"; //$NON-NLS-1$


	@Override
	public String getStringRepresentation(VDomainModelReference reference) {
		final EStructuralFeature feature = getEStructuralFeature(reference);
		if(feature != null){
			return ROOT + SEPARATOR + PROPERTIES + SEPARATOR + feature.getName();
		}
		return null;
	}

}
