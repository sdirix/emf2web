package org.eclipse.emf.ecp.emf2web.export;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;

public interface NameHelper {

	/**
	 * Returns the name of the given {@link Setting} as displayed by the corresponding {@link ItemPropertyDescriptor}.
	 *
	 * @param setting
	 *            The {@link Setting} for which the name is to be determined.
	 * @return
	 *         The name of the {@link Setting} as displayed by the corresponding {@link ItemPropertyDescriptor}. If no
	 *         {@link ItemPropertyDescriptor} exists the unmodified name is returned.
	 */
	public abstract String getDisplayName(Setting setting);

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
	public abstract String getDisplayName(EClass eClass, EStructuralFeature feat);

}