package org.eclipse.emf.ecp.emf2web.wizard.pages;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecp.emf2web.wizard.ViewModelExportWizard;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ModelPathsPage extends WizardPage {
	private Text ecoremodelText;
	private Text genmodelText;
	private Button ecoreBrowse;
	private Button genBrowse;

	private IFile ecoreModel;
	private IFile genModel;
	private ControlDecoration ecoreControlDecoration;
	private ControlDecoration genControlDecoration;
	private ControlDecoration exportControlDecoration;
	private Text exportDirectoryText;
	private Button btnNewButton;
	
	private Button generateNew;

	private String exportDirectoryPath;

	/**
	 * Create the wizard.
	 */
	public ModelPathsPage(IFile ecoreModel, IFile genModel) {
		super("wizardPage");
		setTitle("ECP Model Exporter");
		setDescription("Select the models and the directory to export to");
		this.ecoreModel = ecoreModel;
		this.genModel = genModel;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		FieldDecoration errorFieldDecoration = FieldDecorationRegistry
				.getDefault().getFieldDecoration(
						FieldDecorationRegistry.DEC_ERROR);
		Image errorImage = errorFieldDecoration.getImage();

		FieldDecoration warningFieldDecoration = FieldDecorationRegistry
				.getDefault().getFieldDecoration(
						FieldDecorationRegistry.DEC_WARNING);
		Image warningImage = warningFieldDecoration.getImage();

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		Label lblEmfEcoreModel = new Label(container, SWT.NONE);
		lblEmfEcoreModel.setText("EMF Ecore Model");
		new Label(container, SWT.NONE);

		ecoremodelText = new Text(container, SWT.BORDER);
		ecoremodelText.addModifyListener(new EcoremodelTextModifyListener());
		ecoremodelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		ecoreControlDecoration = new ControlDecoration(ecoremodelText, SWT.LEFT
				| SWT.TOP);
		ecoreControlDecoration.setDescriptionText("Please enter a valid file");
		ecoreControlDecoration.setImage(errorImage);
		ecoreControlDecoration.hide();

		ecoreBrowse = new Button(container, SWT.NONE);
		ecoreBrowse.addSelectionListener(new EcoreBrowseSelectionListener());
		ecoreBrowse.setText("Browse");

		Label lblEmfEcoreGen = new Label(container, SWT.NONE);
		lblEmfEcoreGen.setText("EMF Ecore Gen Model (optional)");
		new Label(container, SWT.NONE);

		genmodelText = new Text(container, SWT.BORDER);
		genmodelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		genControlDecoration = new ControlDecoration(genmodelText, SWT.LEFT
				| SWT.TOP);
		genControlDecoration.setDescriptionText("Please enter a valid file");
		genControlDecoration.setImage(errorImage);
		genControlDecoration.hide();

		genBrowse = new Button(container, SWT.NONE);
		genBrowse.addSelectionListener(new EcoreBrowseSelectionListener());
		genBrowse.setText("Browse");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblPlayApplicationDirectory = new Label(container, SWT.NONE);
		lblPlayApplicationDirectory.setText("Play Application Directory");
		new Label(container, SWT.NONE);

		exportDirectoryText = new Text(container, SWT.BORDER);
		exportDirectoryText
				.addModifyListener(new ExportDirectoryTextModifyListener());
		exportDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		exportControlDecoration = new ControlDecoration(exportDirectoryText,
				SWT.LEFT | SWT.TOP);
		exportControlDecoration
				.setDescriptionText("The directory does not exist! It will be created if possible.");
		exportControlDecoration.setImage(warningImage);
		exportControlDecoration.hide();

		btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new BtnNewButtonSelectionListener());
		btnNewButton.setText("Browse");
		
		generateNew = new Button(container, SWT.CHECK);
		generateNew.setText("Create new Play Application");
		generateNew.addSelectionListener(new GenerateNewSelectionListener());

		init();
	}

	private void init() {
		if (ecoreModel != null) {
			ecoremodelText.setText(ecoreModel.getFullPath().toString());
		}

		if (genModel != null) {
			genmodelText.setText(ecoreModel.getFullPath().toString());
		}
		
		checkForPageCompletion();
	}

	private void checkForPageCompletion() {
		boolean pageComplete = true;
		String message = null;

		if (exportDirectoryPath == null
				|| exportDirectoryPath.trim().equals("")) {
			pageComplete = false;
			message = "Please enter a directory to export to";
		} else {
			File file = new File(exportDirectoryPath);
			if (!file.exists()) {
				// try workspace path
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				String workspacePath = workspace.getRoot().getLocation()
						.toString()
						+ exportDirectoryPath;
				file = new File(workspacePath);
				if (!file.exists()) {
					if (!generateNew.getSelection()) {
						exportControlDecoration.show();
					} else {
						exportControlDecoration.hide();
					}
				} else {
					exportDirectoryPath = workspacePath;
					exportControlDecoration.hide();
				}
			} else {
				exportControlDecoration.hide();
			}
		}

		if (ecoreModel == null) {
			pageComplete = false;
		} else if (!ecoreModel.exists()) {
			ecoreControlDecoration.show();
			message = "Please enter a valid ecore file";
			pageComplete = false;
		} else {
			ecoreControlDecoration.hide();
			setMessage(null);
		}

		if (genModel == null) {
			// do nothing
		} else if (!genModel.exists()) {
			genControlDecoration.show();
		} else {
			genControlDecoration.hide();
		}

		if (isPageComplete() != pageComplete) {
			setPageComplete(pageComplete);
		}
		if (getMessage() == null || !getMessage().equals(message)) {
			setMessage(message);
		}
	}
	
	private class GenerateNewSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			boolean value = ((Button) e.getSource()).getSelection();
			getExportWizard().setCreateNewPlayApplication(value);

			if (value) {
				exportControlDecoration.hide();
			}
		}
	}

	private class EcoreBrowseSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					getShell(), new WorkbenchLabelProvider(),
					new BaseWorkbenchContentProvider());

			final String modelType;
			final String modelExtension;
			final Text modelText;

			if (e.getSource() == ecoreBrowse) {
				modelType = "Ecore Model";
				modelExtension = ".ecore";
				modelText = ecoremodelText;
			} else {
				modelType = "Ecore Gen Model";
				modelExtension = ".genmodel";
				modelText = genmodelText;
			}

			dialog.setTitle(modelType + " Selection");
			dialog.setMessage("Select a " + modelType + " from the workspace");

			dialog.addFilter(new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement,
						Object element) {
					if (element instanceof IFile) {
						IFile file = (IFile) element;
						return file.getName().endsWith(modelExtension);
					}
					return true;
				}
			});

			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			if (dialog.open() == ElementTreeSelectionDialog.OK) {
				Object result = dialog.getFirstResult();
				if (result instanceof IFile) {
					IFile file = (IFile) result;
					modelText.setText(file.getFullPath().toString());
				}
			}
		}
	}

	private class EcoremodelTextModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			String text = ((Text) e.getSource()).getText();

			IPath path = new Path(text);
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IFile file = workspace.getRoot().getFile(path);

			if (e.getSource() == ecoremodelText) {
				ecoreModel = file;
				if (getExportWizard() != null) {
					getExportWizard().setEcoreModel(ecoreModel);
				}
			} else {
				genModel = file;
				if (getExportWizard() != null) {
					getExportWizard().setGenModel(genModel);
				}
			}

			checkForPageCompletion();
		}
	}

	private class BtnNewButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					getShell(), new WorkbenchLabelProvider(),
					new BaseWorkbenchContentProvider());

			dialog.setTitle("Select Play Application");
			dialog.setMessage("Select your Play Application Project");

			dialog.addFilter(new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement,
						Object element) {
					if (element instanceof IProject) {
						return true;
					}
					return false;
				}
			});

			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

			if (dialog.open() == ElementTreeSelectionDialog.OK) {
				Object result = dialog.getFirstResult();
				if (result instanceof IProject) {
					IProject project = (IProject) result;
					exportDirectoryText.setText(project.getFullPath()
							.toString());
				}
			}
		}
	}

	private class ExportDirectoryTextModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			exportDirectoryPath = ((Text) e.getSource()).getText();
			checkForPageCompletion();
			if (getExportWizard() != null) {
				getExportWizard().setExportPath(exportDirectoryPath);
			}
		}
	}

	private ViewModelExportWizard getExportWizard() {
		IWizard wizard = getWizard();
		if (wizard instanceof ViewModelExportWizard) {
			return (ViewModelExportWizard) wizard;
		} else {
			return null;
		}
	}
}
