package org.eclipse.emf.ecp.emf2web.actions;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecp.emf2web.wizard.ViewModelExportWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class Emf2WebCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);

		final ViewModelExportWizard wizard = new ViewModelExportWizard();

		for (Iterator<Object> it = selection.iterator(); it.hasNext();) {
			Object selectedObject = it.next();
			if (selectedObject instanceof IFile) {
				IFile file = (IFile) selectedObject;
				if (file.getLocation().getFileExtension().equals("ecore")) {
					wizard.setEcoreModel(file);
				} else if (file.getLocation().getFileExtension()
						.equals("genmodel")) {
					wizard.setGenModel(file);
				}
			}
		}

		final WizardDialog dialog = new WizardDialog(
				HandlerUtil.getActiveShell(event), wizard);
		dialog.open();

		return null;
	}
}
