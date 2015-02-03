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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.emf2web.export.Emf2QbExporter;
import org.eclipse.emf.ecp.emf2web.export.ExportHelper;
import org.eclipse.emf.ecp.emf2web.export.ProjectType;
import org.eclipse.emf.ecp.emf2web.wizard.pages.EClassPage;
import org.eclipse.emf.ecp.emf2web.wizard.pages.ModelPathsPage;
import org.eclipse.emf.ecp.emf2web.wizard.pages.ViewModelsPage;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * Wizard for converting an Ecore model file into a format for the qb Play Application. It supports optional view model
 * files and can create the qb Play Application via a template file.
 *
 */
public class ViewModelExportWizard extends Wizard implements IWorkbenchWizard, IPageChangingListener {

	private IFile ecoreModel;
	private IFile genModel;

	private final ResourceSet resourceSet;
	private Resource ecoreResource;
	private ModelPathsPage modelsPage;
	private EClassPage eClassPage;
	private ViewModelsPage viewModelPage;

	/**
	 * Sets the Ecore model file for the wizard and its pages. Can be used before the wizard is actually opened to
	 * display the given model as default option.
	 *
	 * @param ecoreModel
	 *            the {@link IFile} containing the Ecore model.
	 */
	public void setEcoreModel(IFile ecoreModel) {
		if (this.ecoreModel != null) {
			if (this.ecoreModel.getLocation().toString()
				.equals(ecoreModel.getLocation().toString())) {
				return;
			}
		}
		this.ecoreModel = ecoreModel;

		resolveReferences(ecoreModel);

		if (eClassPage != null && viewModelPage != null) {
			eClassPage.setNewResource(ecoreResource);
			viewModelPage.clear();
		}
	}

	/**
	 * Resolves References for the given Ecore Model. This is needed to properly access the ViewModel.
	 *
	 * @param ecoreModel
	 *            the model for which the references shall be resolved.
	 */
	private void resolveReferences(IFile ecoreModel) {
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
	}

	/**
	 * Returns the .genmodel file if the user entered one.
	 *
	 * @return
	 *         the {@link IFile} containing the .genmodel or {@code null} if the user did not select one.
	 */
	public IFile getGenModel() {
		return genModel;
	}

	/**
	 * Sets the .genmodel file for the wizard and its pages. Can be used before the wizard is actually opened to
	 * display the given model as default option.
	 *
	 * @param genModel
	 *            the {@link IFile} containing the genmodel.
	 */
	public void setGenModel(IFile genModel) {
		this.genModel = genModel;
	}

	/**
	 * Default constructor.
	 */
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

	/**
	 * Creates or updates a qb Play Application within the workspace or file system.
	 *
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final File exportDirectory;
		IProject project = null;

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final File workspaceDirectory = workspace.getRoot().getLocation().toFile();

		if (modelsPage.isCreateNewApplication()) {
			// determine destination
			final String newProjectName = modelsPage.getProjectPath();
			if (modelsPage.isInWorkspace()) {
				exportDirectory = new File(workspaceDirectory, newProjectName);
			} else {
				exportDirectory = new File(modelsPage.getProjectPath());
			}

			final String templatePath = modelsPage.getTemplatePath();
			if (templatePath != null && !templatePath.trim().equals("")) { //$NON-NLS-1$
				try {
					extractZip(templatePath, exportDirectory.getAbsolutePath());
					if (modelsPage.isInWorkspace()) {
						project = importProject(exportDirectory, newProjectName);
					}
				} catch (final IOException ex) {
					MessageDialog.openError(getShell(), "IO Error", ex.getMessage());
				} catch (final CoreException ex) {
					MessageDialog.openError(getShell(), "Core Error", ex.getMessage());
				}
			}

		} else {
			// Update
			if (modelsPage.isInWorkspace()) {
				project = modelsPage.getSelectedProject();
				exportDirectory = project.getLocation().toFile();
			} else {
				exportDirectory = new File(modelsPage.getProjectPath());
			}
		}

		// Update Application

		final Set<EClass> eClasses = eClassPage.getSelectedEClasses();
		final Set<Resource> viewModels = new HashSet<Resource>();

		for (final IFile viewFile : viewModelPage.getSelectedViewModels()) {
			final URI fileURI = URI.createFileURI(viewFile.getLocation().toString());
			final Resource viewResource = resourceSet.getResource(fileURI, true);
			viewModels.add(viewResource);
		}

		EcoreUtil.resolveAll(resourceSet);
		
		ProjectType projectType = ExportHelper.checkProjectType();
		//TODO: Check for validity (e.g. .project file and target directories)
		
		switch (projectType) {
		case PLAY:
			updatePlayApplication(ecoreResource, eClasses, viewModels, exportDirectory);			
			break;
			
		case STANDALONE:
			Set<VView> views = new HashSet<>();
			for (Resource resource : viewModels) {
				views.add((VView) resource.getContents().get(0));
			}
			ExportHelper.updateStandAloneProject(exportDirectory, eClasses, views);

		default:
			break;
		}
		if (project != null) {
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (final CoreException e) {
				MessageDialog.openError(getShell(), "Refresh Error", e.getMessage());
				e.printStackTrace();
			}
		}

		return true;
	}
	
	private void updatePlayApplication(Resource ecoreResource2,
			Set<EClass> eClasses, Set<Resource> viewModels, File exportDirectory) {
		final Emf2QbExporter exporter = new Emf2QbExporter();
		exporter.export(ecoreResource, eClasses, viewModels, exportDirectory);
		
	}

	

	

	/**
	 * Extracts the given zip file to the given destination.
	 *
	 * @param zipFilePath
	 *            The absolute path of the zip file.
	 * @param destinationPath
	 *            The absolute path of the destination directory;
	 * @throws IOException when extracting or copying fails.
	 */
	private void extractZip(String zipFilePath, String destinationPath) throws IOException {
		final ZipFile zipFile = new ZipFile(zipFilePath);
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			final File entryDestination = new File(destinationPath, entry.getName());
			entryDestination.getParentFile().mkdirs();
			if (entry.isDirectory()) {
				entryDestination.mkdirs();
			} else {
				final InputStream in = zipFile.getInputStream(entry);
				final OutputStream out = new FileOutputStream(entryDestination);
				IOUtils.copy(in, out);
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
		zipFile.close();
	}

	/**
	 * Imports the given project into the workspace with the specified name.
	 *
	 * @param projectDirectory
	 *            The directory containing the project and the ".project" file.
	 * @param projectName
	 *            The name for the new project.
	 * @return
	 *         The imported project.
	 * @throws CoreException
	 */
	private IProject importProject(final File projectDirectory, final String projectName) throws CoreException {
		final IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
			new Path(new File(projectDirectory, ".project").getAbsolutePath())); //$NON-NLS-1$
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
				if (file.getLocation().getFileExtension().equals("ecore")) { //$NON-NLS-1$
					setEcoreModel(file);
				} else if (file.getLocation().getFileExtension()
					.equals("genmodel")) { //$NON-NLS-1$
					genModel = file;
				}
			}
		}
	}

	/**
	 * Transfers information from the {@link ModelPathsPage} to the {@link EClassPage} when changing the page.
	 *
	 * @see org.eclipse.jface.dialogs.IPageChangingListener#handlePageChanging(org.eclipse.jface.dialogs.PageChangingEvent)
	 */
	@Override
	public void handlePageChanging(PageChangingEvent event) {
		if (event.getCurrentPage() == modelsPage) {
			final IFile ecoreModelFile = modelsPage.getEcoreModelFile();
			if (ecoreModelFile != null) {
				setEcoreModel(ecoreModelFile);
			}
			if (ecoreResource != eClassPage.getEcoreResource()) {
				eClassPage.setNewResource(ecoreResource);
			}
		}
	}

}
