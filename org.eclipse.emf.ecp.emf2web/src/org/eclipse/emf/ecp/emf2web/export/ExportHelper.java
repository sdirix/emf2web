/*******************************************************************************
 * Copyright (c) 2014-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jonas Helming - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.emf.ecp.emf2web.export;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.emf2web.generator.json.EcoreJsonGenerator;
import org.eclipse.emf.ecp.emf2web.generator.json.FormsJsonGenerator;
import org.eclipse.emf.ecp.view.model.generator.ViewProvider;
import org.eclipse.emf.ecp.view.spi.model.VView;

public class ExportHelper {

	public static ProjectType checkProjectType() {
		return ProjectType.STANDALONE;
	}

	public static void updateStandAloneProject(File destinationDir,
			Set<EClass> eClasses, Set<VView> views) {
		final Map<EClass, VView> eClassViewModelMap = buildEClassViewModelMap(views);
		final Iterable<EClass> keySet = eClassViewModelMap.keySet();

		destinationDir = new File(destinationDir,
				"/idontknowthecorrectdirectory");

		final EcoreJsonGenerator ecoreJSonExporter = new EcoreJsonGenerator();
		final FormsJsonGenerator viewJSonExporter = new FormsJsonGenerator(
				new NameHelperImpl());
		for (final EClass eClass : keySet) {
			// ecoreJSonExporter.exportEcoreModel(eClass, destinationDir);
			// final EObject eObject = EcoreUtil.create(eClass);
			// // Export eObject
			// VView vView = eClassViewModelMap.get(eClass);
			// if (vView == null) {
			// vView = generateVView(eObject);
			// }
			// viewJSonExporter.exportViewModel(vView, destinationDir);

		}

	}

	private static VView generateVView(EObject eObject) {
		final ViewProvider viewProvider = new ViewProvider();
		final VView vView = viewProvider.generate(eObject,
				new HashMap<String, Object>());
		return vView;
	}

	private static Map<EClass, VView> buildEClassViewModelMap(Set<VView> views) {
		final Map<EClass, VView> ret = new HashMap<>();
		for (final VView vView : views) {
			if (vView.getRootEClass() == null) {
				throw new IllegalArgumentException(
						"Invalid View Model: Root EClass is null");
			}
			ret.put(vView.getRootEClass(), vView);
		}
		return ret;

	}

}
