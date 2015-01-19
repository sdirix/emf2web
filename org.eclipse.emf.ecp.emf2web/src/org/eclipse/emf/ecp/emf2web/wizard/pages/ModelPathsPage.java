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

import java.text.MessageFormat;

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
	private Text projectPathText;
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

	private boolean isCreateNewApplication;
	private boolean isInFileSystem;
	private IProject selectedProject;
	private String projectPath;
	private String templatePath;

	private IFile ecoreModelFile;
	private IFile genModelFile;

	/**
	 * Create a model selection page where the given models are preselected.
	 *
	 * @param ecoreModel
	 *            The {@link IFile} containing the ecore model. Use {@code null} if no ecore model shall be
	 *            preselected.
	 * @param genModel
	 *            The {@link IFile} containing the gen model. Use {@code null} if no gen model shall be
	 *            preselected.
	 */
	public ModelPathsPage(IFile ecoreModel, IFile genModel) {
		super(Messages.ModelPathsPage_ConstructorTitle);
		setTitle(Messages.ModelPathsPage_Title);
		setDescription(Messages.ModelPathsPage_Description);
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
	public boolean isCreateNewApplication() {
		return isCreateNewApplication;
	}

	/**
	 * Indicates if the user wants to update an existing project.
	 *
	 * @return {@code true} if the user wants to update an existing project, {@code false} otherwise.
	 */
	public boolean isUpdateProject() {
		return !isCreateNewApplication;
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

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		final Label lblEmfEcoreModel = new Label(container, SWT.NONE);
		lblEmfEcoreModel.setText(Messages.ModelPathsPage_EMFEcoreModel);
		new Label(container, SWT.NONE);

		ecoremodelText = new Text(container, SWT.BORDER);
		ecoremodelText.addModifyListener(new EcoremodelTextModifyListener());
		ecoremodelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
			false, 1, 1));

		ecoreControlDecoration = new ControlDecoration(ecoremodelText, SWT.LEFT
			| SWT.TOP);
		ecoreControlDecoration.setDescriptionText(Messages.ModelPathsPage_ValidFileError);
		ecoreControlDecoration.setImage(errorImage);
		ecoreControlDecoration.hide();

		ecoreBrowse = new Button(container, SWT.NONE);
		ecoreBrowse.addSelectionListener(new EcoreBrowseSelectionListener());
		ecoreBrowse.setText(Messages.ModelPathsPage_Browse);

		final Label lblEmfEcoreGen = new Label(container, SWT.NONE);
		lblEmfEcoreGen.setText(Messages.ModelPathsPage_GenModel);
		new Label(container, SWT.NONE);

		genmodelText = new Text(container, SWT.BORDER);
		genmodelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
			false, 1, 1));

		genControlDecoration = new ControlDecoration(genmodelText, SWT.LEFT
			| SWT.TOP);
		genControlDecoration.setDescriptionText(Messages.ModelPathsPage_ValidFileError);
		genControlDecoration.setImage(errorImage);
		genControlDecoration.hide();

		genBrowse = new Button(container, SWT.NONE);
		genBrowse.addSelectionListener(new EcoreBrowseSelectionListener());
		genBrowse.setText(Messages.ModelPathsPage_Browse);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		grpProjectSettings = new Group(container, SWT.NONE);
		grpProjectSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
			false, false, 2, 1));
		grpProjectSettings.setText(Messages.ModelPathsPage_ProjectSettings);
		grpProjectSettings.setLayout(new GridLayout(1, false));

		final Composite actionComposite = new Composite(grpProjectSettings, SWT.NONE);
		actionComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
			false, 2, 1));
		actionComposite.setBounds(0, 0, 64, 64);
		actionComposite.setLayout(new GridLayout(3, false));

		final Label lblNewLabel = new Label(actionComposite, SWT.NONE);
		lblNewLabel.setText(Messages.ModelPathsPage_Action);

		btnUpdateExistingProject = new Button(actionComposite, SWT.RADIO);
		btnUpdateExistingProject.setSelection(true);
		btnUpdateExistingProject.addSelectionListener(new BtnUpdateExistingApplicationSelectionListener());
		btnUpdateExistingProject.setText(Messages.ModelPathsPage_UpdateExisting);

		btnCreateANew = new Button(actionComposite, SWT.RADIO);
		btnCreateANew.addSelectionListener(new BtnCreateANewSelectionListener());
		btnCreateANew.setText(Messages.ModelPathsPage_CreateNew);

		selectProjectComposite = new Composite(grpProjectSettings, SWT.NONE);
		selectProjectComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		selectProjectComposite.setLayout(new GridLayout(2, false));

		selectProjectLabel = new Label(selectProjectComposite, SWT.NONE);
		selectProjectLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		selectProjectLabel.setBounds(0, 0, 493, 15);
		selectProjectLabel.setText(Messages.ModelPathsPage_SelectProject);

		new Label(selectProjectComposite, SWT.NONE);

		projectPathText = new Text(selectProjectComposite, SWT.BORDER);
		projectPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projectPathText.setSize(493, 21);
		projectPathText
			.addModifyListener(new ProjectPathTextModifyListener());

		projectControlDecoration = new ControlDecoration(projectPathText,
			SWT.LEFT | SWT.TOP);
		projectControlDecoration
			.setDescriptionText(Messages.ModelPathsPage_ValidNameError);
		projectControlDecoration.setImage(errorImage);
		projectControlDecoration.hide();

		selectProjectButton = new Button(selectProjectComposite, SWT.NONE);
		selectProjectButton.setSize(50, 25);
		selectProjectButton
			.addSelectionListener(new BrowseApplicationSelectionListener());
		selectProjectButton.setText(Messages.ModelPathsPage_Browse);

		locationButtonsComposite = new Composite(selectProjectComposite, SWT.NONE);
		locationButtonsComposite.setLayout(new GridLayout(2, false));

		btnInWorkspace = new Button(locationButtonsComposite, SWT.RADIO);
		btnInWorkspace.setSelection(true);
		btnInWorkspace.addSelectionListener(new BtnInWorkspaceSelectionListener());
		btnInWorkspace.setText(Messages.ModelPathsPage_Workspace);

		btnInFileSystem = new Button(locationButtonsComposite, SWT.RADIO);
		btnInFileSystem.addSelectionListener(new BtnInFileSystemSelectionListener());
		btnInFileSystem.setText(Messages.ModelPathsPage_FileSystem);
		new Label(selectProjectComposite, SWT.NONE);
		new Label(grpProjectSettings, SWT.NONE);

		projectTemplateComposite = new Composite(grpProjectSettings, SWT.NONE);
		projectTemplateComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projectTemplateComposite.setLayout(new GridLayout(2, false));

		lblSelectProjectTemplate = new Label(projectTemplateComposite, SWT.NONE);
		lblSelectProjectTemplate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblSelectProjectTemplate.setText(Messages.ModelPathsPage_ApplicationTemplate);

		new Label(projectTemplateComposite, SWT.NONE);

		projectTemplateText = new Text(projectTemplateComposite, SWT.BORDER);
		projectTemplateText.addModifyListener(new ApplicationTemplateTextModifyListener());
		projectTemplateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		projectTemplateButton = new Button(projectTemplateComposite, SWT.NONE);
		projectTemplateButton.addSelectionListener(new ApplicationTemplateButtonSelectionListener());
		projectTemplateButton.setText(Messages.ModelPathsPage_Browse);

		lblDownloadFrom = new Label(projectTemplateComposite, SWT.NONE);
		lblDownloadFrom.setText(Messages.ModelPathsPage_DownloadLink);
		new Label(projectTemplateComposite, SWT.NONE);

		new Label(grpProjectSettings, SWT.NONE);

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

		if (!isCreateNewApplication) {
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
			message = Messages.ModelPathsPage_ValidEcoreError;
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

	/**
	 * SelectionListener for the Ecore model Browse buttons.
	 *
	 */
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
				modelType = Messages.ModelPathsPage_EcoreModel;
				modelExtension = ".ecore"; //$NON-NLS-1$
				modelText = ecoremodelText;
			} else {
				modelType = Messages.ModelPathsPage_EcoreGenModel;
				modelExtension = ".genmodel"; //$NON-NLS-1$
				modelText = genmodelText;
			}

			dialog.setTitle(modelType + Messages.ModelPathsPage_DialogTitle);
			dialog.setMessage(MessageFormat.format(Messages.ModelPathsPage_DialogMessage, modelType));

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

	/**
	 * TextModifyListener for the text containing the path to the ecore model.
	 *
	 */
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

	/**
	 * The selection listener for the Browse button of the Application text.
	 *
	 */
	private class BrowseApplicationSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			// in workspace
			if (!isInFileSystem) {
				final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					getShell(), new WorkbenchLabelProvider(),
					new BaseWorkbenchContentProvider());

				dialog.setTitle(Messages.ModelPathsPage_ApplicationDialog_Title);
				dialog.setMessage(Messages.ModelPathsPage_ApplicationDialog_Message);

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
						projectPathText.setText(project.getName());
					}
				}
			} else {
				// in file system
				final DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setMessage(Messages.ModelPathsPage_ApplicationDialog_Message);

				final String path = dialog.open();
				if (path != null) {
					projectPathText.setText(path);
					selectedProject = null;
				}
			}

		}
	}

	/**
	 * The TextModifyListener for the project path text.
	 *
	 */
	private class ProjectPathTextModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			boolean found = false;
			for (final IProject project : workspace.getRoot().getProjects()) {
				final String searchName = projectPathText.getText() != null ? projectPathText
					.getText().trim() : ""; //$NON-NLS-1$
				if (searchName.equals(project.getName())) {
					selectedProject = project;
					found = true;
				}
			}
			if (!found) {
				selectedProject = null;
			}
			projectPath = projectPathText.getText();
			checkForPageCompletion();
		}
	}

	/**
	 * Selection Listener for the "In Workspace" Radio Button.
	 *
	 */
	private class BtnInWorkspaceSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			isInFileSystem = false;

			if (isCreateNewApplication) {
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

	/**
	 * Selection Listener for the "In File System" Radio Button.
	 *
	 */
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

	/**
	 * Selection Listener for the "Update Existing Application" Radio Button.
	 *
	 */
	private class BtnUpdateExistingApplicationSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			isCreateNewApplication = false;

			setProjectSelection();
			setEnableTemplateSelection(isCreateNewApplication);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	/**
	 * Selection Listener for the "Create New Application" Radio Button.
	 *
	 */
	private class BtnCreateANewSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			isCreateNewApplication = true;
			if (!isInFileSystem) {
				setProjectNameEntering();
			} else {
				setProjectSelection();
			}
			setEnableTemplateSelection(isCreateNewApplication);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	/**
	 * Selection Listener for the application template text.
	 *
	 */
	private class ApplicationTemplateButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterExtensions(new String[] { "*.zip", "*.*" }); //$NON-NLS-1$//$NON-NLS-2$
			dialog.setFilterNames(new String[] { Messages.ModelPathsPage_TemplateZipFile,
				Messages.ModelPathsPage_AllFiles });
			final String path = dialog.open();
			if (path != null) {
				projectTemplateText.setText(path);
			}
		}
	}

	/**
	 * TextModifyListener for the application template text.
	 *
	 */
	private class ApplicationTemplateTextModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			templatePath = projectTemplateText.getText();
		}
	}

	private void setProjectSelection() {
		selectProjectButton.setEnabled(true);
		selectProjectLabel.setText(Messages.ModelPathsPage_SelectProject);
	}

	private void setProjectNameEntering() {
		selectProjectButton.setEnabled(false);
		selectProjectLabel.setText(Messages.ModelPathsPage_EnterProjectName);
	}

	private void setEnableTemplateSelection(boolean enable) {
		for (final Control control : projectTemplateComposite.getChildren()) {
			control.setEnabled(enable);
		}
	}

	private boolean isInvalidPath(String path) {
		return path == null || path.trim().equals(""); //$NON-NLS-1$
	}

}
