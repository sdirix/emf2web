package org.eclipse.emf.ecp.emf2web.ui.json.internal.handler;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecp.emf2web.controller.GenerationController;
import org.eclipse.emf.ecp.emf2web.exporter.FileGenerationExporter;
import org.eclipse.emf.ecp.emf2web.exporter.GenerationExporter;
import org.eclipse.emf.ecp.emf2web.json.controller.JsonGenerationController;
import org.eclipse.emf.ecp.emf2web.ui.handler.AbstractSchemaExportCommandHandler;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExportJSONFormsHandler extends AbstractSchemaExportCommandHandler {

	@Override
	protected Collection<VView> getViews(ExecutionEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		
		ResourceSet resourceSet = new ResourceSetImpl();
		
		List<VView> views = new LinkedList<VView>();
		
		for(@SuppressWarnings("unchecked")
		final Iterator<Object> it = selection.iterator(); it.hasNext();) {
			final Object selectedObject = it.next();
			if (selectedObject instanceof IFile) {
				final IFile file = (IFile) selectedObject;
				if (file.getLocation().getFileExtension().equals("view")) { //$NON-NLS-1$
					final URI fileURI = URI.createFileURI(file.getLocation().toString());
					final Resource viewResource = resourceSet.getResource(fileURI, true);
					views.add((VView) viewResource.getContents().get(0));
				}
			}
		}
		
		return views;
	}

	@Override
	protected GenerationController getGenerationController() {
		return new JsonGenerationController();
	}

	@Override
	protected GenerationExporter getGenerationExporter() {
		return new FileGenerationExporter();
	}

}
