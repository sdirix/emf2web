package org.eclipse.emf.ecp.emf2web.ui.wizard;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
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
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;

public class SelectLocationPage extends WizardPage {
	private DataBindingContext m_bindingContext;

	private final GenerationInfo generationInfo;
	private Text locationText;
	private Text generatedText;
	private Button btnWrap;
	private ControlDecoration requiredLocationDecoration;

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
	 * 
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

		requiredLocationDecoration = new ControlDecoration(locationText, SWT.LEFT | SWT.TOP);
		final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		requiredLocationDecoration.setImage(fieldDecoration.getImage());
		requiredLocationDecoration.setDescriptionText("Required");
		requiredLocationDecoration.hide();

		Button browseButton = new Button(container, SWT.NONE);
		browseButton.addSelectionListener(new BrowseButtonSelectionListener());
		browseButton.setText("Browse");

		if (generationInfo.getWrapper() != null) {
			Group grpWrapper = new Group(container, SWT.NONE);
			grpWrapper.setLayout(new GridLayout(1, false));
			grpWrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			grpWrapper.setText("Optional Settings");

			btnWrap = new Button(grpWrapper, SWT.CHECK);
			btnWrap.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			final String buttonText = "Wrap in " + generationInfo.getWrapper().getName();
			btnWrap.setText(buttonText);
		}

		Group grpPreview = new Group(container, SWT.NONE);
		grpPreview.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpPreview.setText("Content");

		generatedText = new Text(grpPreview, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		m_bindingContext = initDataBindings();

		WizardPageSupport.create(this, m_bindingContext);
	}

	private class BrowseButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			ResourceDialog resourceDialog = new ResourceDialog(e.display.getActiveShell(), "Select Location",
					SWT.SINGLE | SWT.SAVE);

			int result = resourceDialog.open();
			if (result == ResourceDialog.OK) {
				generationInfo.setLocation(resourceDialog.getURIs().get(0));
				m_bindingContext.updateTargets();
				requiredLocationDecoration.hide();
			}
		}
	}

	private class LocationValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			if (value == null) {
				requiredLocationDecoration.show();
				return ValidationStatus.error("Location must be set");
			}
			requiredLocationDecoration.hide();
			return ValidationStatus.ok();
		}
	}

	private class StringToURIConverter implements IConverter {
		@Override
		public Object getToType() {
			return URI.class;
		}

		@Override
		public Object getFromType() {
			return String.class;
		}

		@Override
		public Object convert(Object fromObject) {
			if (fromObject == null || "".equals(fromObject)) {
				return null;
			}
			String path = (String) fromObject;
			return URI.createFileURI(path);
		}
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLocationTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(locationText);
		IObservableValue locationGenerationInfoObserveValue = PojoProperties.value("location").observe(generationInfo);
		bindingContext.bindValue(
				observeTextLocationTextObserveWidget, locationGenerationInfoObserveValue, new UpdateValueStrategy()
						.setConverter(new StringToURIConverter()).setAfterConvertValidator(new LocationValidator()),
				null);
		//
		IObservableValue observeTextGeneratedTextObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(generatedText);
		IObservableValue generatedStringGenerationInfoObserveValue = PojoProperties.value("generatedString")
				.observe(generationInfo);
		bindingContext.bindValue(observeTextGeneratedTextObserveWidget, generatedStringGenerationInfoObserveValue, null,
				null);
		//
		if (generationInfo.getWrapper() != null) {
			IObservableValue observeSelectionBtnWrapObserveWidget = WidgetProperties.selection().observe(btnWrap);
			IObservableValue wrapGenerationInfoObserveValue = PojoProperties.value("wrap").observe(generationInfo);
			bindingContext.bindValue(observeSelectionBtnWrapObserveWidget, wrapGenerationInfoObserveValue, null, null);
		}
		//
		return bindingContext;
	}
}
