package org.eclipse.emf.ecp.emf2web.wizard;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class ViewModelExportWizard extends Wizard implements IWorkbenchWizard {

	private IFile ecoreModel;
	private IFile genModel;
	private String exportPath;

	private ResourceSet resourceSet;
	private Resource ecoreResource;
	private ModelPathsPage modelsPage;
	private EClassPage eClassPage;
	private ViewModelsPage viewModelPage;

	public IFile getEcoreModel() {
		return ecoreModel;
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

	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
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
		// check export path
		if (exportPath == null) {
			return false;
		}

		File exportDirectory = new File(exportPath);

		if (!exportDirectory.isDirectory()) {
			return false;
		}

		if (!exportDirectory.exists()) {
			if (!exportDirectory.mkdirs()) {
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

		return true;
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
