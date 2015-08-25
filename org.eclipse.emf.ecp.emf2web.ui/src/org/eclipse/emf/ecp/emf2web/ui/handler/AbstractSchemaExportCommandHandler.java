package org.eclipse.emf.ecp.emf2web.ui.handler;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecp.emf2web.controller.GenerationController;
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo;
import org.eclipse.emf.ecp.emf2web.exporter.GenerationExporter;
import org.eclipse.emf.ecp.emf2web.ui.wizard.ExportSchemasWizard;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractSchemaExportCommandHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Collection<VView> views = getViews(event);
		final List<GenerationInfo> generationInfos = getGenerationController().generate(views);
		
		final ExportSchemasWizard wizard = new ExportSchemasWizard(generationInfos, getGenerationExporter());
		final WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}
	
	protected abstract Collection<VView> getViews(ExecutionEvent event);
	protected abstract GenerationController getGenerationController();
	protected abstract GenerationExporter getGenerationExporter();
}
