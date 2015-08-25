package org.eclipse.emf.ecp.emf2web.ui.wizard;

import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;

public class SelectLocationPage extends WizardPage {
	private DataBindingContext m_bindingContext;

	private final GenerationInfo generationInfo;
	private Text locationText;
	private Text generatedText;
	
	/**
	 * Create the wizard.
	 */
	public SelectLocationPage(GenerationInfo generationInfo) {
		super("wizardPage");
		this.generationInfo = generationInfo;
		
		setTitle("Select locations for generated schemas");
		setDescription("Select a location for schema " + generationInfo.getNameProposal());
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblLocation = new Label(container, SWT.NONE);
		lblLocation.setText("Location:");
		new Label(container, SWT.NONE);
		
		locationText = new Text(container, SWT.BORDER);
		locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button browseButton = new Button(container, SWT.NONE);
		browseButton.addSelectionListener(new BrowseButtonSelectionListener());
		browseButton.setText("Browse");
		
		Group grpPreview = new Group(container, SWT.NONE);
		grpPreview.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpPreview.setText("Preview");
		
		generatedText = new Text(grpPreview, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLocationTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(locationText);
		IObservableValue locationGenerationInfoObserveValue = PojoProperties.value("location").observe(generationInfo);
		bindingContext.bindValue(observeTextLocationTextObserveWidget, locationGenerationInfoObserveValue, null, null);
		//
		IObservableValue observeTextGeneratedTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(generatedText);
		IObservableValue generatedStringGenerationInfoObserveValue = PojoProperties.value("generatedString").observe(generationInfo);
		bindingContext.bindValue(observeTextGeneratedTextObserveWidget, generatedStringGenerationInfoObserveValue, null, null);
		//
		return bindingContext;
	}
	private class BrowseButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			FileDialog fileDialog = new FileDialog(e.display.getActiveShell());
			String fileString = fileDialog.open();
			if (fileString != null) {
				generationInfo.setLocation(fileString);
				m_bindingContext.updateTargets();
			}
		}
	}
}
