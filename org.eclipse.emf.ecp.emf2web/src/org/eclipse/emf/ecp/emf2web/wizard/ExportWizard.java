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
package org.eclipse.emf.ecp.emf2web.wizard;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecp.emf2web.wizard.pages.ModelPathsPage;
import org.eclipse.emf.ecp.view.model.presentation.SelectEClassWizardPage;
import org.eclipse.emf.ecp.view.model.presentation.SelectEcorePage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Stefan Dirix
 *
 */
public class ExportWizard extends Wizard implements IWorkbenchWizard {

	private SelectEClassWizardPage selectEClassPage;
	private SelectEcorePage selectEcorePage;
	private ModelPathsPage selectPathsPage;
	// private SelectViewsPage selectViewsPage;

	private Object selectedContainer;

	private List<EClass> selectedEClasses;

	/**
	 * Sets the starting information of this Wizard.
	 *
	 * @param selectedContainer
	 *            Either an {@link IFile} containing an ecore or an
	 *            {@link EPackage}.
	 */
	public void setSelectedContainer(Object selectedContainer) {
		this.selectedContainer = selectedContainer;
	}

	/**
	 * Clear the starting information of this Wizard.
	 */
	public void clearSelectedContainer() {
		selectedContainer = null;
		if (selectEcorePage != null) {
			selectEcorePage.setSelectedContainer(null);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		final SelectEClassWizardPage page = new SelectEClassWizardPage();
		page.setDescription("Select one or more eClasses to export");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == selectEcorePage) {
			selectedEClasses = null;
			selectedContainer = selectEcorePage.getSelectedContainer();
			if (selectedContainer != null) {
				if (selectEClassPage == null) {
					selectEClassPage = new SelectEClassWizardPage();
					selectEClassPage.setPageComplete(true);
					addPage(selectEClassPage);
				}
				selectEClassPage
					.setSelectedEPackage(getEPackageFromSelectedContainer());
				return selectEClassPage;
			}
			return null;
		}
		return super.getNextPage(page);
	}

	private EPackage getEPackageFromSelectedContainer() {
		EPackage ePackage = null;
		if (EPackage.class.isInstance(selectedContainer)) {
			ePackage = EPackage.class.cast(selectedContainer);
		} else if (IFile.class.isInstance(selectedContainer)) {
			final ResourceSetImpl resourceSet = new ResourceSetImpl();
			final String path = ((IFile) selectedContainer).getFullPath()
				.toString();
			final URI uri = URI.createPlatformResourceURI(path, true);

			final Resource resource = resourceSet.getResource(uri, true);
			if (resource != null) {

				final EList<EObject> contents = resource.getContents();
				if (contents.size() != 1) {
					return null;
				}

				final EObject object = contents.get(0);
				if (!(object instanceof EPackage)) {
					return null;
				}

				ePackage = (EPackage) object;
			}
		}
		return ePackage;
	}

}
