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
package org.eclipse.emf.ecp.emf2web.exporter;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo;

/**
 * @author Stefan Dirix
 *
 */
public interface GenerationExporter {

	public void export(Collection<? extends GenerationInfo> generationInfos) throws IOException;
}
