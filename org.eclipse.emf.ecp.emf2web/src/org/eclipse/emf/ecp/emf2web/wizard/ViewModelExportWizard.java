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
package org.eclipse.emf.ecp.emf2web.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.emf2web.export.Emf2QbExporter;
import org.eclipse.emf.ecp.emf2web.wizard.pages.EClassPage;
import org.eclipse.emf.ecp.emf2web.wizard.pages.IOnEnterWizardPage;
import org.eclipse.emf.ecp.emf2web.wizard.pages.ModelPathsPage;
import org.eclipse.emf.ecp.emf2web.wizard.pages.ViewModelsPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.osgi.framework.Bundle;

public class ViewModelExportWizard extends Wizard implements IWorkbenchWizard {

	private IFile ecoreModel;
	private IFile genModel;

	private final ResourceSet resourceSet;
	private Resource ecoreResource;
	private ModelPathsPage modelsPage;
	private EClassPage eClassPage;
	private ViewModelsPage viewModelPage;

	private boolean createNewPlayApplication = false;

	public IFile getEcoreModel() {
		return ecoreModel;
	}

	public void setCreateNewPlayApplication(boolean create) {
		createNewPlayApplication = create;
	}

	public void setEcoreModel(IFile ecoreModel) {
		if (this.ecoreModel != null) {
			if (this.ecoreModel.getLocation().toString()
				.equals(ecoreModel.getLocation().toString())) {
				return;
			}
		}
		this.ecoreModel = ecoreModel;

		final URI fileURI = URI.createFileURI(ecoreModel.getLocation().toString());
		ecoreResource = resourceSet.getResource(fileURI, true);

		// needed to resolve viewmodel references
		for (final Iterator<EObject> it = ecoreResource.getAllContents(); it
			.hasNext();) {
			final EObject object = it.next();
			if (object instanceof EPackage) {
				final EPackage ePackage = (EPackage) object;
				resourceSet.getPackageRegistry().put(ePackage.getNsURI(),
					ePackage);
			}
		}

		if (eClassPage != null && viewModelPage != null) {
			eClassPage.setNewResource(ecoreResource);
			viewModelPage.clear();
		}
	}

	public IFile getGenModel() {
		return genModel;
	}

	public void setGenModel(IFile genModel) {
		this.genModel = genModel;
	}

	public Resource getEcoreResource() {
		return ecoreResource;
	}

	public ViewModelExportWizard() {
		setWindowTitle("Export View Model to Web");
		resourceSet = new ResourceSetImpl();
	}

	@Override
	public void addPages() {
		modelsPage = new ModelPathsPage(ecoreModel, genModel);
		eClassPage = new EClassPage();
		viewModelPage = new ViewModelsPage();

		addPage(modelsPage);
		addPage(eClassPage);
		addPage(viewModelPage);
	}

	@Override
	public boolean performFinish() {
		File exportDirectory;
		IProject project;

		if (!modelsPage.getCreateNewProject()) {
			project = modelsPage.getSelectedProject();
			exportDirectory = project.getLocation().toFile();

			// check if valid
			if (!exportDirectory.isDirectory()) {
				MessageDialog.openError(getShell(), "Play Application Path Error",
					"The chosen play application directory is not a directory!");
				return false;
			}

			if (!exportDirectory.exists()) {
				if (!exportDirectory.mkdirs()) {
					MessageDialog.openError(getShell(), "Play Application Path Error",
						"The chosen path for the play application could not be generated!");
					return false;
				}
			}
		} else {
			final Bundle bundle = Platform.getBundle("org.eclipse.emf.ecp.emf2web.examples");
			final URL fileURL = bundle.getEntry("projects/org.eclipse.emf.ecp.emf2web.playapplication");

			String name = modelsPage.getProjectName();
			if (name == null || name.trim().equals("")) {
				name = "playapplication";
			}

			File source = null;
			try {
				final URL resolvedURL = FileLocator.resolve(fileURL);

				// eclipse bug #145096
				String resolvedString = resolvedURL.getFile();
				resolvedString = "file:" + resolvedString.replaceAll(" ", "%20");
				final URL escapedURL = new URL(resolvedString);

				source = new File(escapedURL.toURI());

				final IWorkspace workspace = ResourcesPlugin.getWorkspace();
				final File workspaceDirectory = workspace.getRoot().getLocation().toFile();

				final File destination = new File(workspaceDirectory, name);
				destination.mkdir();

				exportDirectory = destination;

				FileUtils.copyDirectory(source, destination);

				// register project in eclipse
				project = importProject(destination, name);

			} catch (final URISyntaxException e) {
				MessageDialog.openError(getShell(), "Play Application Generation Error", e.getMessage());
				e.printStackTrace();
				return false;
			} catch (final IOException e) {
				MessageDialog.openError(getShell(), "Play Application Generation Error", e.getMessage());
				e.printStackTrace();
				return false;
			} catch (final CoreException e) {
				MessageDialog.openError(getShell(), "Play Application Generation Error", e.getMessage());
				e.printStackTrace();
				return false;
			}
		}

		final Set<EClass> eClasses = eClassPage.getSelectedEClasses();
		final Set<Resource> viewModels = new HashSet<Resource>();

		for (final IFile viewFile : viewModelPage.getSelectedViewModels()) {
			final URI fileURI = URI.createFileURI(viewFile.getLocation().toString());
			final Resource viewResource = resourceSet.getResource(fileURI, true);
			viewModels.add(viewResource);
		}

		EcoreUtil.resolveAll(resourceSet);

		final Emf2QbExporter exporter = new Emf2QbExporter();
		exporter.export(ecoreResource, eClasses, viewModels, exportDirectory);

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (final CoreException e) {
			MessageDialog.openError(getShell(), "Refresh Error", e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

	private IProject importProject(final File baseDirectory, final String projectName) throws CoreException {
		final IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
			new Path(baseDirectory.getAbsolutePath() + "/.project"));
		description.setName(projectName);
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		project.create(description, null);
		project.open(null);
		return project;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		for (@SuppressWarnings("unchecked")
		final Iterator<Object> it = selection.iterator(); it.hasNext();) {
			final Object selectedObject = it.next();
			if (selectedObject instanceof IFile) {
				final IFile file = (IFile) selectedObject;
				if (file.getLocation().getFileExtension().equals("ecore")) {
					setEcoreModel(file);
				} else if (file.getLocation().getFileExtension()
					.equals("genmodel")) {
					genModel = file;
				}
			}
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		final IWizardPage nextPage = super.getNextPage(page);
		if (nextPage instanceof IOnEnterWizardPage) {
			((IOnEnterWizardPage) nextPage).onEnterPage();
		}
		return nextPage;
	}

}
