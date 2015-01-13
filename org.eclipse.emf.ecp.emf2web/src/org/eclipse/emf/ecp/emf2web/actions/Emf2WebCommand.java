/*******************************************************************************
 * Copyright (c) 2014-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Stefan Dirix - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.emf.ecp.emf2web.actions;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecp.emf2web.wizard.ViewModelExportWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class Emf2WebCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
			.getCurrentSelection(event);

		final ViewModelExportWizard wizard = new ViewModelExportWizard();

		for (final Iterator<Object> it = selection.iterator(); it.hasNext();) {
			final Object selectedObject = it.next();
			if (selectedObject instanceof IFile) {
				final IFile file = (IFile) selectedObject;
				if (file.getLocation().getFileExtension().equals("ecore")) {
					wizard.setEcoreModel(file);
				} else if (file.getLocation().getFileExtension()
					.equals("genmodel")) {
					wizard.setGenModel(file);
				}
			}
		}

		final WizardDialog dialog = new WizardDialog(
			HandlerUtil.getActiveShell(event), wizard);
		dialog.open();

		return null;
	}
}
