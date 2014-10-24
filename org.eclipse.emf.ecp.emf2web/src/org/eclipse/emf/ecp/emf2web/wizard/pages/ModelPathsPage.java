package org.eclipse.emf.ecp.emf2web.wizard.pages;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
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
	private Button projectSettingsButton;
	private Combo combo;

	private Group grpProjectSettings;
	private Text projectSettingsText;
	private Label projectSettingsLabel;

	private boolean createNewProject = false;
	private IProject selectedProject = null;
	private String projectName = "";
	private ControlDecoration projectControlDecoration;

	/**
	 * Create the wizard.
	 */
	public ModelPathsPage(IFile ecoreModel, IFile genModel) {
		super("wizardPage");
		setTitle("ECP Model Exporter");
		setDescription("Select the models and the project to export to");
		this.ecoreModel = ecoreModel;
		this.genModel = genModel;
	}

	public boolean getCreateNewProject() {
		return createNewProject;
	}

	public IProject getSelectedProject() {
		return selectedProject;
	}

	public String getProjectName() {
		return projectName;
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

		grpProjectSettings = new Group(container, SWT.NONE);
		grpProjectSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 2, 1));
		grpProjectSettings.setText("Project Settings");
		grpProjectSettings.setLayout(new GridLayout(2, false));

		Composite composite = new Composite(grpProjectSettings, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		composite.setBounds(0, 0, 64, 64);
		composite.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("Action:");

		combo = new Combo(composite, SWT.NONE);
		combo.addSelectionListener(new ComboSelectionListener());
		combo.setItems(new String[] { "Update existing Project",
				"Create new Project" });
		combo.select(0);
		new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);

		projectSettingsLabel = new Label(grpProjectSettings, SWT.NONE);
		projectSettingsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		projectSettingsLabel.setBounds(0, 0, 55, 15);
		projectSettingsLabel.setText("Select Project");
		new Label(grpProjectSettings, SWT.NONE);

		projectSettingsText = new Text(grpProjectSettings, SWT.BORDER);
		projectSettingsText
				.addModifyListener(new ProjectSettingsTextModifyListener());
		projectSettingsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		projectControlDecoration = new ControlDecoration(projectSettingsText,
				SWT.LEFT | SWT.TOP);
		projectControlDecoration
				.setDescriptionText("Please enter a valid project name");
		projectControlDecoration.setImage(errorImage);
		projectControlDecoration.hide();

		projectSettingsButton = new Button(grpProjectSettings, SWT.NONE);
		projectSettingsButton.setSize(50, 25);
		projectSettingsButton
				.addSelectionListener(new BtnNewButtonSelectionListener());
		projectSettingsButton.setText("Browse");
		new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);

		Label lblNewLabel_1 = new Label(grpProjectSettings, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);

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

		if (!createNewProject) {
			if (selectedProject == null) {
				projectControlDecoration.show();
				pageComplete = false;
			} else {
				projectControlDecoration.hide();
			}
		} else {
			if (selectedProject != null) {
				projectControlDecoration.show();
				pageComplete = false;
			}else{
				projectControlDecoration.hide();
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
				if (getExportWizard() != null && ecoreModel.exists()) {
					getExportWizard().setEcoreModel(ecoreModel);
				}
			} else {
				genModel = file;
				if (getExportWizard() != null && genModel.exists()) {
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
					selectedProject = project;
					projectSettingsText.setText(project.getName());
				}
			}
		}
	}

	private class ComboSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (combo.getSelectionIndex() == 0) {
				projectSettingsLabel.setText("Select Project");
				createNewProject = false;
				if (selectedProject != null) {
					projectSettingsText.setText(selectedProject.getName());
				}
				projectSettingsButton.setEnabled(true);
			} else if (combo.getSelectionIndex() == 1) {
				projectSettingsLabel.setText("Enter new Project Name");
				createNewProject = true;
				projectSettingsButton.setEnabled(false);
			}
			checkForPageCompletion();
		}
	}

	private class ProjectSettingsTextModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			boolean found = false;
			for (IProject project : workspace.getRoot().getProjects()) {
				String searchName = projectSettingsText.getText() != null ? projectSettingsText
						.getText().trim() : "";
				if (searchName.equals(project.getName())) {
					selectedProject = project;
					found = true;
				}
			}
			if (!found) {
				selectedProject = null;
			}
			projectName = projectSettingsText.getText();
			checkForPageCompletion();
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
