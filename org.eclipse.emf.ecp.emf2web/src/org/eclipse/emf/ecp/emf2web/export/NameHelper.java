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
package org.eclipse.emf.ecp.emf2web.export;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.view.model.common.edit.provider.CustomReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;

public class NameHelper {

	ComposedAdapterFactory composedAdapterFactory;
	AdapterFactoryItemDelegator adapterFactoryItemDelegator;

	public NameHelper() {
		composedAdapterFactory = new ComposedAdapterFactory(
			new AdapterFactory[] {
				new CustomReflectiveItemProviderAdapterFactory(),
				new ComposedAdapterFactory(
					ComposedAdapterFactory.Descriptor.Registry.INSTANCE) });

		adapterFactoryItemDelegator = new AdapterFactoryItemDelegator(
			composedAdapterFactory);
	}

	private final IItemPropertyDescriptor getItemPropertyDescriptor(
		Setting setting) {
		final IItemPropertyDescriptor descriptor = adapterFactoryItemDelegator
			.getPropertyDescriptor(setting.getEObject(),
				setting.getEStructuralFeature());
		return descriptor;
	}

	public String getDisplayName(Setting setting) {
		final IItemPropertyDescriptor descriptor = getItemPropertyDescriptor(setting);
		return descriptor.getDisplayName(setting.getEObject());
	}

	public String getDisplayName(EClass eClass, EStructuralFeature feat) {
		final EObject object = EcoreUtil.create(eClass);
		final IItemPropertyDescriptor descriptor = adapterFactoryItemDelegator
			.getPropertyDescriptor(object, feat);
		if (descriptor != null) {
			return descriptor.getDisplayName(object);
		}
		return feat.getName();
	}
}
