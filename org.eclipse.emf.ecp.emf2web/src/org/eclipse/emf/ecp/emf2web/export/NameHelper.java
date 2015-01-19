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
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;

/**
 * Helper class for determining the name of Ecore model elements and features as displayed by the
 * {@link ItemPropertyDescriptor}s of EMF.
 *
 */
public class NameHelper {

	private final ComposedAdapterFactory composedAdapterFactory;
	private final AdapterFactoryItemDelegator adapterFactoryItemDelegator;

	/**
	 * Default Constructor.
	 */
	public NameHelper() {
		composedAdapterFactory = new ComposedAdapterFactory(
			new AdapterFactory[] {
				new CustomReflectiveItemProviderAdapterFactory(),
				new ComposedAdapterFactory(
					ComposedAdapterFactory.Descriptor.Registry.INSTANCE) });

		adapterFactoryItemDelegator = new AdapterFactoryItemDelegator(
			composedAdapterFactory);
	}

	/**
	 * Returns the {@link ItemPropertyDescriptor} of the given {@link Setting}.
	 *
	 * @param setting
	 *            The {@link Setting} for which the {@link ItemPropertyDescriptor} is to be determined.
	 * @return
	 *         The {@link ItemPropertyDescriptor} of the given {@link Setting}.
	 */
	private IItemPropertyDescriptor getItemPropertyDescriptor(
		Setting setting) {
		final IItemPropertyDescriptor descriptor = adapterFactoryItemDelegator
			.getPropertyDescriptor(setting.getEObject(),
				setting.getEStructuralFeature());
		return descriptor;
	}

	/**
	 * Returns the name of the given {@link Setting} as displayed by the corresponding {@link ItemPropertyDescriptor}.
	 *
	 * @param setting
	 *            The {@link Setting} for which the name is to be determined.
	 * @return
	 *         The name of the {@link Setting} as displayed by the corresponding {@link ItemPropertyDescriptor}. If no
	 *         {@link ItemPropertyDescriptor} exists the unmodified name is returned.
	 */
	public String getDisplayName(Setting setting) {
		final IItemPropertyDescriptor descriptor = getItemPropertyDescriptor(setting);
		return descriptor.getDisplayName(setting.getEObject());
	}

	/**
	 * Returns the name of the given {@link EStructuralFeature} as displayed by the corresponding
	 * {@link ItemPropertyDescriptor}.
	 *
	 * @param eClass
	 *            The {@link EClass} which contains the {@link EStructuralFeature}.
	 * @param feat
	 *            The {@link EStructuralFeature} for which the name is to be determined.
	 * @return
	 *         The name of the {@link EStructuralFeature} as displayed by the corresponding
	 *         {@link ItemPropertyDescriptor}. If no {@link ItemPropertyDescriptor} exists the unmodified name is
	 *         returned.
	 */
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
