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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo;
import org.eclipse.emf.ecp.emf2web.internal.messages.Messages;

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
		final String exportString = wrapGeneration(generationInfo);
		final URI location = generationInfo.getLocation();
		export(exportString, location);
	}

	protected String wrapGeneration(GenerationInfo generationInfo) {
		if (generationInfo.getWrapper() == null) {
			return generationInfo.getGeneratedString();
		}
		return generationInfo.getWrapper().wrap(generationInfo.getGeneratedString(), generationInfo.getType());
	}

	protected void export(String exportString, URI location) throws IOException {
		if (location.isPlatform()) {
			final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			final IFile file = root.getFile(new Path(location.toPlatformString(true)));
			try {
				file.setContents(new ByteArrayInputStream(exportString.getBytes()), 0, null);
			} catch (final CoreException ex) {
				throw new IOException(ex);
			}
		} else if (location.isFile()) {
			final File file = new File(location.toFileString());
			writeToFileSystemFile(exportString, file);
		} else {
			throw new IOException(MessageFormat.format(Messages.FileGenerationExporter_URI_Error, location.toString()));
		}
	}

	private void writeToFileSystemFile(String exportString, File file) throws IOException {
		final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8")); //$NON-NLS-1$
		try {
			writer.write(exportString);
		} finally {
			writer.close();
		}
	}
}
