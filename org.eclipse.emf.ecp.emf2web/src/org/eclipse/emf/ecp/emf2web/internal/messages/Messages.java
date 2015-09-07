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
package org.eclipse.emf.ecp.emf2web.internal.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Stefan Dirix
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.ecp.emf2web.internal.messages.messages"; //$NON-NLS-1$
	public static String FileGenerationExporter_URI_Error;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
