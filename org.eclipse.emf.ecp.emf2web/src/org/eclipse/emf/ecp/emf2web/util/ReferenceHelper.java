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
package org.eclipse.emf.ecp.emf2web.util;

import org.eclipse.emf.ecp.view.spi.model.VDomainModelReference;

/**
 * A helper class for the handling of {@link VDomainModelReference}s.
 *
 * @author Stefan Dirix
 *
 */
public interface ReferenceHelper {
	/**
	 * Determines a string representation for the given {@code reference}, for example a path.
	 *
	 * @param reference
	 *            The {@link VDomainModelReference} for which a string representation is to be determined.
	 * @return
	 * 		The string representation of the given {@code reference}.
	 */
	public String getStringRepresentation(VDomainModelReference reference);

	/**
	 * Determines a label for the given {@code reference}.
	 *
	 * @param reference
	 *            The {@link VDomainModelReference} for which a label is to be determined.
	 * @return
	 * 		The label for the given {@code reference}.
	 */
	public String getLabel(VDomainModelReference reference);
}
