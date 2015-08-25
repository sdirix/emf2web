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

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo;

/**
 * @author Stefan Dirix
 *
 */
public class FileGenerationExporter implements GenerationExporter {

	@Override
	public void export(Collection<? extends GenerationInfo> generationInfos) throws IOException {
		for (final GenerationInfo generationInfo : generationInfos) {
			export(generationInfo);
		}
	}

	protected void export(GenerationInfo generationInfo) throws IOException {
		final String generatedString = generationInfo.getGeneratedString();
		final String location = generationInfo.getLocation();
		export(generatedString, location);
	}

	protected void export(String generatedString, String location) throws IOException {
		FileUtils.writeStringToFile(new File(location), generatedString);
	}
}
