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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ViewModelsPage extends WizardPage implements IOnEnterWizardPage {

	private final Set<IFile> selectedViewModels;
	private TableViewer tableViewer;

	/**
	 * Create the wizard.
	 */
	public ViewModelsPage() {
		super("wizardPage");
		setTitle("View Model Selection");
		setDescription("Add all non-default view models");

		selectedViewModels = new LinkedHashSet<IFile>();
	}

	/**
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		tableViewer = new TableViewer(container, SWT.BORDER
			| SWT.FULL_SELECTION);
		final Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer.setLabelProvider(new WorkbenchLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setComparator(new ViewerComparator());
		tableViewer.setInput(selectedViewModels);

		final Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		final Button btnAdd = new Button(composite, SWT.NONE);
		btnAdd.addSelectionListener(new BtnAddSelectionListener());
		btnAdd.setText("Add...");

		final Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new BtnNewButtonSelectionListener());
		btnNewButton.setText("Remove");
	}

	public Set<IFile> getSelectedViewModels() {
		return selectedViewModels;
	}

	public void clear() {
		selectedViewModels.clear();
	}

	@Override
	public void onEnterPage() {
		tableViewer.refresh();
	}

	private class BtnAddSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());

			dialog.setTitle("View Model Selection");
			dialog.setMessage("Select at least one view model from the workspace");

			dialog.addFilter(new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement,
					Object element) {
					if (element instanceof IFile) {
						final IFile file = (IFile) element;
						if (file.getName().toLowerCase().endsWith(".view")) {
							return true;
						}
						if (file.getName().toLowerCase().endsWith(".viewmodel")) {
							return true;
						}
						return false;
					}
					return true;
				}
			});

			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			if (dialog.open() == Window.OK) {
				for (final Object result : dialog.getResult()) {
					if (result instanceof IFile) {
						final IFile file = (IFile) result;
						selectedViewModels.add(file);
					}
				}
				tableViewer.refresh();
			}
		}
	}

	private class BtnNewButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final IStructuredSelection selection = (IStructuredSelection) tableViewer
				.getSelection();
			selectedViewModels.removeAll(selection.toList());
			tableViewer.refresh();
		}
	}
}
