/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Stefan Dirix - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.ecp.emf2web.util;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.emf2web.Activator;
import org.eclipse.emf.ecp.view.model.common.edit.provider.CustomReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.ecp.view.spi.model.VDomainModelReference;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emfforms.spi.core.services.databinding.DatabindingFailedException;
import org.eclipse.emfforms.spi.core.services.databinding.EMFFormsDatabinding;

/**
 * @author Stefan Dirix
 *
 */
public abstract class AbstractReferenceHelper implements ReferenceHelper {

	private final EMFFormsDatabinding dataBinding;
	private final ComposedAdapterFactory composedAdapterFactory;
	private final AdapterFactoryItemDelegator adapterFactoryItemDelegator;

	public AbstractReferenceHelper() {
		composedAdapterFactory = new ComposedAdapterFactory(
			new AdapterFactory[] {
				new CustomReflectiveItemProviderAdapterFactory(),
				new ComposedAdapterFactory(
					ComposedAdapterFactory.Descriptor.Registry.INSTANCE) });

		adapterFactoryItemDelegator = new AdapterFactoryItemDelegator(
			composedAdapterFactory);

		dataBinding = Activator.getDefault().getEMFFormsDatabindingService();
	}

	protected EStructuralFeature getEStructuralFeature(VDomainModelReference reference) {
		try {
			final IValueProperty valueProperty = dataBinding.getValueProperty(reference, null);

			if (valueProperty != null) {
				return (EStructuralFeature) valueProperty.getValueType();
			}
		} catch (final DatabindingFailedException ex) {
			handleDatabindingFailedException(ex);
		}
		return null;
	}

	protected void handleDatabindingFailedException(DatabindingFailedException exception) {
		// Do nothing in default
		exception.printStackTrace();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.emf2web.util.ReferenceHelper#getLabel(org.eclipse.emf.ecp.view.spi.model.VDomainModelReference)
	 */
	@Override
	public String getLabel(VDomainModelReference reference) {
		final EStructuralFeature feature = getEStructuralFeature(reference);
		if (feature == null) {
			return null;
		}
		final EClass eClass = feature.getEContainingClass();

		final EObject object = EcoreUtil.create(eClass);
		final IItemPropertyDescriptor descriptor = adapterFactoryItemDelegator
			.getPropertyDescriptor(object, feature);
		if (descriptor != null) {
			return descriptor.getDisplayName(object);
		}
		return feature.getName();
	}

}
