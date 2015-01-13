package org.eclipse.emf.ecp.emf2web.wizard;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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

	private ResourceSet resourceSet;
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

		URI fileURI = URI.createFileURI(ecoreModel.getLocation().toString());
		ecoreResource = resourceSet.getResource(fileURI, true);

		// needed to resolve viewmodel references
		for (Iterator<EObject> it = ecoreResource.getAllContents(); it
			.hasNext();) {
			EObject object = it.next();
			if (object instanceof EPackage) {
				EPackage ePackage = (EPackage) object;
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
			Bundle bundle = Platform.getBundle("org.eclipse.emf.ecp.emf2web.examples");
			URL fileURL = bundle.getEntry("projects/org.eclipse.emf.ecp.emf2web.playapplication");

			String name = modelsPage.getProjectName();
			if (name == null || name.trim().equals("")) {
				name = "playapplication";
			}

			File source = null;
			try {
				URL resolvedURL = FileLocator.resolve(fileURL);

				// eclipse bug #145096
				String resolvedString = resolvedURL.getFile();
				resolvedString = "file:" + resolvedString.replaceAll(" ", "%20");
				URL escapedURL = new URL(resolvedString);

				source = new File(escapedURL.toURI());

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				File workspaceDirectory = workspace.getRoot().getLocation().toFile();

				File destination = new File(workspaceDirectory, name);
				destination.mkdir();

				exportDirectory = destination;

				FileUtils.copyDirectory(source, destination);

				// register project in eclipse
				project = importProject(destination, name);

			} catch (URISyntaxException e) {
				MessageDialog.openError(getShell(), "Play Application Generation Error", e.getMessage());
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				MessageDialog.openError(getShell(), "Play Application Generation Error", e.getMessage());
				e.printStackTrace();
				return false;
			} catch (CoreException e) {
				MessageDialog.openError(getShell(), "Play Application Generation Error", e.getMessage());
				e.printStackTrace();
				return false;
			}
		}

		Set<EClass> eClasses = eClassPage.getSelectedEClasses();
		Set<Resource> viewModels = new HashSet<Resource>();

		for (IFile viewFile : viewModelPage.getSelectedViewModels()) {
			URI fileURI = URI.createFileURI(viewFile.getLocation().toString());
			Resource viewResource = resourceSet.getResource(fileURI, true);
			viewModels.add(viewResource);
		}

		EcoreUtil.resolveAll(resourceSet);

		Emf2QbExporter exporter = new Emf2QbExporter();
		exporter.export(ecoreResource, eClasses, viewModels, exportDirectory);

		try {
			project.refreshLocal(IProject.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), "Refresh Error", e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

	private IProject importProject(final File baseDirectory, final String projectName) throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
			new Path(baseDirectory.getAbsolutePath() + "/.project"));
		description.setName(projectName);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		project.create(description, null);
		project.open(null);
		return project;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		for (@SuppressWarnings("unchecked")
		Iterator<Object> it = selection.iterator(); it.hasNext();) {
			Object selectedObject = it.next();
			if (selectedObject instanceof IFile) {
				IFile file = (IFile) selectedObject;
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
		IWizardPage nextPage = super.getNextPage(page);
		if (nextPage instanceof IOnEnterWizardPage) {
			((IOnEnterWizardPage) nextPage).onEnterPage();
		}
		return nextPage;
	}

}
