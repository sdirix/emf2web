package org.eclipse.emf.ecp.emf2web.ui.wizard;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo;
import org.eclipse.emf.ecp.emf2web.exporter.GenerationExporter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

public class ExportSchemasWizard extends Wizard {

	protected final Collection<? extends GenerationInfo> generationInfos;
	protected final GenerationExporter exporter;
	
	public ExportSchemasWizard(Collection<? extends GenerationInfo> generationInfos, GenerationExporter exporter) {
		setWindowTitle("Export Schemas Wizard");
		this.generationInfos = generationInfos;
		this.exporter = exporter;
	}

	@Override
	public void addPages() {
		for(GenerationInfo generationInfo : generationInfos){
			addPage(new SelectLocationPage(generationInfo));
		}
	}

	@Override
	public boolean performFinish() {
		try {
			exporter.export(generationInfos);
			MessageDialog.openInformation(getShell(), "Success", "Export successful");
			return true;
		} catch (IOException e) {
			MessageDialog.openError(getShell(), "Error while exporting", e.getMessage());
		}
		return false;
	}

}
