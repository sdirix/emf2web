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
package org.eclipse.emf.ecp.emf2web.wizard.pages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
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

	private ControlDecoration ecoreControlDecoration;
	private ControlDecoration genControlDecoration;
	private Button selectProjectButton;

	private Group grpProjectSettings;
	private Text projectSettingsText;
	private Label selectProjectLabel;

	private ControlDecoration projectControlDecoration;
	private Button btnUpdateExistingProject;
	private Button btnCreateANew;
	private Label lblSelectProjectTemplate;
	private Text projectTemplateText;
	private Button projectTemplateButton;
	private Label lblDownloadFrom;
	private Composite selectProjectComposite;
	private Composite projectTemplateComposite;
	private Composite locationButtonsComposite;
	private Button btnInWorkspace;
	private Button btnInFileSystem;

	private boolean isCreateNewProject;
	private boolean isInFileSystem;
	private IProject selectedProject;
	private String projectPath;
	private String templatePath;

	private IFile ecoreModelFile;
	private IFile genModelFile;

	/**
	 * Create the wizard.
	 */
	public ModelPathsPage(IFile ecoreModel, IFile genModel) {
		super("wizardPage"); //$NON-NLS-1$
		setTitle("ECP Model Exporter"); //$NON-NLS-1$
		setDescription("Select the models and the project to export to"); //$NON-NLS-1$
		ecoreModelFile = ecoreModel;
		genModelFile = genModel;
	}

	/**
	 * @return the ecoreModelFile, {@code null} if invalid or not set.
	 */
	public IFile getEcoreModelFile() {
		return ecoreModelFile;
	}

	/**
	 * @return the genModelFile, {@code null} if invalid or not set.
	 */
	public IFile getGenModelFile() {
		return genModelFile;
	}

	/**
	 * Indicates if the user wants to create a new project.
	 *
	 * @return {@code true} if the user wants to create a new project, {@code false} otherwise.
	 */
	public boolean isCreateNewProject() {
		return isCreateNewProject;
	}

	/**
	 * Indicates if the user wants to update an existing project.
	 *
	 * @return {@code true} if the user wants to update an existing project, {@code false} otherwise.
	 */
	public boolean isUpdateProject() {
		return !isCreateNewProject;
	}

	/**
	 * Indicates if the user selected an existing project in the workspace or wants to create a new project within the
	 * workspace.
	 *
	 * @return {@code true} if the user selected an existing project in the workspace or wants to create a new project
	 *         within the workspace, {@code false} otherwise.
	 */
	public boolean isInWorkspace() {
		return !isInFileSystem;
	}

	/**
	 * Indicates if the user selected an existing project in the file system or wants to create a new project within the
	 * file system.
	 *
	 * @return {@code true} if the user selected an existing project in the file system or wants to create a new project
	 *         within the file system, {@code false} otherwise.
	 */
	public boolean isInFileSystem() {
		return isInFileSystem;
	}

	/**
	 * The existing project the user wants to update.
	 *
	 * @return the selected project the user wants to update within the workspace, {@code null} otherwise.
	 */
	public IProject getSelectedProject() {
		return selectedProject;
	}

	/**
	 * Specifies the path or the name of the project.
	 *
	 * @return the name of the project if it resides within the workspace, the absolute path otherwise.
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * Specifies the path of the project template.
	 *
	 * @return
	 *         the absolute path of the project template.
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);

		final FieldDecoration errorFieldDecoration = FieldDecorationRegistry
			.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_ERROR);
		final Image errorImage = errorFieldDecoration.getImage();

		final FieldDecoration warningFieldDecoration = FieldDecorationRegistry
			.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_WARNING);
		final Image warningImage = warningFieldDecoration.getImage();

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		final Label lblEmfEcoreModel = new Label(container, SWT.NONE);
		lblEmfEcoreModel.setText("EMF Ecore Model"); //$NON-NLS-1$
		new Label(container, SWT.NONE);

		ecoremodelText = new Text(container, SWT.BORDER);
		ecoremodelText.addModifyListener(new EcoremodelTextModifyListener());
		ecoremodelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
			false, 1, 1));

		ecoreControlDecoration = new ControlDecoration(ecoremodelText, SWT.LEFT
			| SWT.TOP);
		ecoreControlDecoration.setDescriptionText("Please enter a valid file"); //$NON-NLS-1$
		ecoreControlDecoration.setImage(errorImage);
		ecoreControlDecoration.hide();

		ecoreBrowse = new Button(container, SWT.NONE);
		ecoreBrowse.addSelectionListener(new EcoreBrowseSelectionListener());
		ecoreBrowse.setText("Browse"); //$NON-NLS-1$

		final Label lblEmfEcoreGen = new Label(container, SWT.NONE);
		lblEmfEcoreGen.setText("EMF Ecore Gen Model (optional)"); //$NON-NLS-1$
		new Label(container, SWT.NONE);

		genmodelText = new Text(container, SWT.BORDER);
		genmodelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
			false, 1, 1));

		genControlDecoration = new ControlDecoration(genmodelText, SWT.LEFT
			| SWT.TOP);
		genControlDecoration.setDescriptionText("Please enter a valid file"); //$NON-NLS-1$
		genControlDecoration.setImage(errorImage);
		genControlDecoration.hide();

		genBrowse = new Button(container, SWT.NONE);
		genBrowse.addSelectionListener(new EcoreBrowseSelectionListener());
		genBrowse.setText("Browse"); //$NON-NLS-1$
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		grpProjectSettings = new Group(container, SWT.NONE);
		grpProjectSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
			false, false, 2, 1));
		grpProjectSettings.setText("Project Settings"); //$NON-NLS-1$
		grpProjectSettings.setLayout(new GridLayout(1, false));

		final Composite actionComposite = new Composite(grpProjectSettings, SWT.NONE);
		actionComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
			false, 2, 1));
		actionComposite.setBounds(0, 0, 64, 64);
		actionComposite.setLayout(new GridLayout(3, false));

		final Label lblNewLabel = new Label(actionComposite, SWT.NONE);
		lblNewLabel.setText("Action:"); //$NON-NLS-1$

		btnUpdateExistingProject = new Button(actionComposite, SWT.RADIO);
		btnUpdateExistingProject.setSelection(true);
		btnUpdateExistingProject.addSelectionListener(new BtnUpdateExistingProjectSelectionListener());
		btnUpdateExistingProject.setText("Update existing Application"); //$NON-NLS-1$

		btnCreateANew = new Button(actionComposite, SWT.RADIO);
		btnCreateANew.addSelectionListener(new BtnCreateANewSelectionListener());
		btnCreateANew.setText("Create new Application"); //$NON-NLS-1$

		selectProjectComposite = new Composite(grpProjectSettings, SWT.NONE);
		selectProjectComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		selectProjectComposite.setLayout(new GridLayout(2, false));

		selectProjectLabel = new Label(selectProjectComposite, SWT.NONE);
		selectProjectLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		selectProjectLabel.setBounds(0, 0, 493, 15);
		selectProjectLabel.setText("Select Project"); //$NON-NLS-1$

		new Label(selectProjectComposite, SWT.NONE);

		projectSettingsText = new Text(selectProjectComposite, SWT.BORDER);
		projectSettingsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projectSettingsText.setSize(493, 21);
		projectSettingsText
		.addModifyListener(new ProjectSettingsTextModifyListener());

		projectControlDecoration = new ControlDecoration(projectSettingsText,
			SWT.LEFT | SWT.TOP);
		projectControlDecoration
		.setDescriptionText("Please enter a valid project name"); //$NON-NLS-1$
		projectControlDecoration.setImage(errorImage);
		projectControlDecoration.hide();

		selectProjectButton = new Button(selectProjectComposite, SWT.NONE);
		selectProjectButton.setSize(50, 25);
		selectProjectButton
		.addSelectionListener(new BtnNewButtonSelectionListener());
		selectProjectButton.setText("Browse"); //$NON-NLS-1$

		locationButtonsComposite = new Composite(selectProjectComposite, SWT.NONE);
		locationButtonsComposite.setLayout(new GridLayout(2, false));

		btnInWorkspace = new Button(locationButtonsComposite, SWT.RADIO);
		btnInWorkspace.setSelection(true);
		btnInWorkspace.addSelectionListener(new BtnInWorkspaceSelectionListener());
		btnInWorkspace.setText("In Workspace"); //$NON-NLS-1$

		btnInFileSystem = new Button(locationButtonsComposite, SWT.RADIO);
		btnInFileSystem.addSelectionListener(new BtnInFileSystemSelectionListener());
		btnInFileSystem.setText("In File System"); //$NON-NLS-1$
		new Label(selectProjectComposite, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);

		projectTemplateComposite = new Composite(grpProjectSettings, SWT.NONE);
		projectTemplateComposite.setEnabled(false);
		projectTemplateComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projectTemplateComposite.setLayout(new GridLayout(2, false));

		lblSelectProjectTemplate = new Label(projectTemplateComposite, SWT.NONE);
		lblSelectProjectTemplate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblSelectProjectTemplate.setText("Select Application Template (Zip)"); //$NON-NLS-1$

		new Label(projectTemplateComposite, SWT.NONE);

		projectTemplateText = new Text(projectTemplateComposite, SWT.BORDER);
		projectTemplateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		projectTemplateButton = new Button(projectTemplateComposite, SWT.NONE);
		projectTemplateButton.addSelectionListener(new ProjectTemplateButtonSelectionListener());
		projectTemplateButton.setText("Browse"); //$NON-NLS-1$

		lblDownloadFrom = new Label(projectTemplateComposite, SWT.NONE);
		lblDownloadFrom.setText("... Download from http://download.com"); //$NON-NLS-1$
		new Label(projectTemplateComposite, SWT.NONE);

		final Label lblNewLabel_1 = new Label(grpProjectSettings, SWT.NONE);

		setEnableTemplateSelection(false);
		init();
	}

	private void init() {
		if (ecoreModelFile != null) {
			ecoremodelText.setText(ecoreModelFile.getFullPath().toString());
		}

		if (genModelFile != null) {
			genmodelText.setText(genModelFile.getFullPath().toString());
		}

		checkForPageCompletion();
	}

	private void checkForPageCompletion() {
		boolean pageComplete = true;
		String message = null;

		if (!isCreateNewProject) {
			if (!isInFileSystem && selectedProject == null) {
				projectControlDecoration.show();
				pageComplete = false;
			} else if (isInFileSystem && isInvalidPath(projectPath)) {
				projectControlDecoration.show();
				pageComplete = false;
			} else {
				projectControlDecoration.hide();
			}
		} else {
			if (selectedProject != null) {
				projectControlDecoration.show();
				pageComplete = false;
			} else {
				projectControlDecoration.hide();
			}
		}

		if (ecoreModelFile == null) {
			pageComplete = false;
		} else if (!ecoreModelFile.exists()) {
			ecoreControlDecoration.show();
			message = "Please enter a valid ecore file"; //$NON-NLS-1$
			pageComplete = false;
		} else {
			ecoreControlDecoration.hide();
			setMessage(null);
		}

		if (genModelFile == null) {
			// do nothing
		} else if (!genModelFile.exists()) {
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
			final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());

			final String modelType;
			final String modelExtension;
			final Text modelText;

			if (e.getSource() == ecoreBrowse) {
				modelType = "Ecore Model"; //$NON-NLS-1$
				modelExtension = ".ecore"; //$NON-NLS-1$
				modelText = ecoremodelText;
			} else {
				modelType = "Ecore Gen Model"; //$NON-NLS-1$
				modelExtension = ".genmodel"; //$NON-NLS-1$
				modelText = genmodelText;
			}

			dialog.setTitle(modelType + " Selection"); //$NON-NLS-1$
			dialog.setMessage("Select a " + modelType + " from the workspace"); //$NON-NLS-1$ //$NON-NLS-2$

			dialog.addFilter(new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement,
					Object element) {
					if (element instanceof IFile) {
						final IFile file = (IFile) element;
						return file.getName().endsWith(modelExtension);
					}
					return true;
				}
			});

			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			if (dialog.open() == Window.OK) {
				final Object result = dialog.getFirstResult();
				if (result instanceof IFile) {
					final IFile file = (IFile) result;
					if (e.getSource() == ecoreBrowse) {
						ecoreModelFile = file;
					} else {
						genModelFile = file;
					}
					modelText.setText(file.getFullPath().toString());
				}
			}
		}
	}

	private class EcoremodelTextModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			final String text = ((Text) e.getSource()).getText();

			final IPath path = new Path(text);
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IFile file = workspace.getRoot().getFile(path);

			if (e.getSource() == ecoremodelText) {
				ecoreModelFile = file;
			} else {
				genModelFile = file;
			}

			checkForPageCompletion();
		}
	}

	private class BtnNewButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			// in workspace
			if (!isInFileSystem) {
				final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					getShell(), new WorkbenchLabelProvider(),
					new BaseWorkbenchContentProvider());

				dialog.setTitle("Select Play Application"); //$NON-NLS-1$
				dialog.setMessage("Select your Play Application Project"); //$NON-NLS-1$

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

				if (dialog.open() == Window.OK) {
					final Object result = dialog.getFirstResult();
					if (result instanceof IProject) {
						final IProject project = (IProject) result;
						selectedProject = project;
						projectSettingsText.setText(project.getName());
					}
				}
			} else {
				// in file system
				final DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setMessage("Select your Play Application Project"); //$NON-NLS-1$

				final String path = dialog.open();
				if (path != null) {
					projectSettingsText.setText(path);
					selectedProject = null;
				}
			}

		}
	}

	private class ProjectSettingsTextModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			boolean found = false;
			for (final IProject project : workspace.getRoot().getProjects()) {
				final String searchName = projectSettingsText.getText() != null ? projectSettingsText
					.getText().trim() : ""; //$NON-NLS-1$
					if (searchName.equals(project.getName())) {
						selectedProject = project;
						found = true;
					}
			}
			if (!found) {
				selectedProject = null;
			}
			projectPath = projectSettingsText.getText();
			checkForPageCompletion();
		}
	}

	private class BtnInWorkspaceSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			isInFileSystem = false;

			if (isCreateNewProject) {
				setProjectNameEntering();
			} else {
				setProjectSelection();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	private class BtnInFileSystemSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			isInFileSystem = true;

			setProjectSelection();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	private class BtnUpdateExistingProjectSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			isCreateNewProject = false;

			setProjectSelection();
			setEnableTemplateSelection(isCreateNewProject);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	private class BtnCreateANewSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			isCreateNewProject = true;
			if (!isInFileSystem) {
				setProjectNameEntering();
			} else {
				setProjectSelection();
			}
			setEnableTemplateSelection(isCreateNewProject);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	private class ProjectTemplateButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterExtensions(new String[] { "*.zip", "*.*" });
			dialog.setFilterNames(new String[] { "*Template Zip File", "All Files" });
			final String path = dialog.open();
			if (path != null) {
				projectTemplateText.setText(path);
			}
		}
	}

	private void setProjectSelection() {
		selectProjectButton.setEnabled(true);
		selectProjectLabel.setText("Select Project");
	}

	private void setProjectNameEntering() {
		selectProjectButton.setEnabled(false);
		selectProjectLabel.setText("Enter Project Name");
	}

	private void setEnableTemplateSelection(boolean enable) {
		for (final Control control : projectTemplateComposite.getChildren()) {
			control.setEnabled(enable);
		}
	}

	private boolean isInvalidPath(String path) {
		return path == null || path.trim().equals("");
	}

}
