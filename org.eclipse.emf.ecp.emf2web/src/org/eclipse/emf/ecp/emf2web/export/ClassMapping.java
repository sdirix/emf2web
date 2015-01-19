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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;

/**
 * Class for mapping between Ecore and QB identifiers.
 *
 */
public class ClassMapping {

	/**
	 * Ecore identifiers constituting a atring.
	 */
	public static final String[] STRING_IDENTIFIER = new String[] { "EString" }; //$NON-NLS-1$

	/**
	 * Ecore identifiers constituting a floating point number.
	 */
	public static final String[] NUMBER_IDENTIFIER = new String[] { "EDouble", //$NON-NLS-1$
		"EDoubleObject", "EFloat", "EFloatObject" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * Ecore identifiers constituting an integer.
	 */
	public static final String[] INTEGER_IDENTIFIER = new String[] {
		"EBigDecimal", "EBigInteger", "EByte", "EByteObject", "EChar", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"ECharacterObject", "EInt", "EIntegerObject", "ELong", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"ELongObject", "EShort", "EShortObject" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * Ecore identifiers constituting a boolean.
	 */
	public static final String[] BOOLEAN_IDENTIFIER = new String[] { "EBoolean", //$NON-NLS-1$
		"EBooleanObject" }; //$NON-NLS-1$

	/**
	 * Ecore identifiers constituting a date.
	 */
	public static final String[] DATE_IDENTIFIER = new String[] { "EDate" }; //$NON-NLS-1$

	/**
	 * Ecore identifiers which are not yet supported by qb.
	 */
	public static final String[] UNSUPPORTED_IDENTIFIER = new String[] {
		"EByteArray", "EDiagnosticChain", "EElist<E>", "EEnumerator", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"EFeatureMap", "EFeatureMapEntry", "EInvocationTargetException", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"EJavaClass<T>", "EJavaObject", "EMap<K,V>", "EResource", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"EResourceSet", "ETreeIterator<E>" }; //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Indicator for an unsupported mapping.
	 */
	public static final String UNSUPPORTED = "unsupported"; //$NON-NLS-1$

	/**
	 * Indicator for an undeterminable mapping.
	 */
	public static final String UNKNOWN = "unknown"; //$NON-NLS-1$

	private final List<EEnum> enums = new ArrayList<EEnum>();

	/**
	 * Adds the given {@link EEnum} to the ClassMapping. The {@code eEnum} is used during the mapping process to
	 * properly identify the dynamic names of ecore enums.
	 *
	 * @param eEnum
	 *            The {@link EEnum} which shall be used during the mapping process.
	 */
	public void addEEnum(EEnum eEnum) {
		enums.add(eEnum);
	}

	/**
	 * Adds all given {@link EEnum}s to the ClassMapping. See {@link #addEEnum(EEnum)} for more information.
	 *
	 * @param eEnums
	 *            A collection of {@link EEnum}s which shall be used during the mapping process.
	 */
	public void addAllEEnum(Collection<? extends EEnum> eEnums) {
		enums.addAll(eEnums);
	}

	/**
	 * Indicates if the given {@link EClassifier} can be properly mapped.
	 *
	 * @param eType
	 *            The {@link EClassifier} which shall be checked.
	 * @return
	 *         {@code true} if the given {@link EClassifier} can be successfully mapped to a QB variant, {@code false}
	 *         if the {@link EClassifier} is unknown or unsupported.
	 */
	public boolean isAllowed(EClassifier eType) {
		final String qbName = getQBName(eType);
		return !qbName.equals("unknown") && !qbName.equals("unsupported"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Determines the QB mapping of the given {@link EClassifier}. For the mapping a collection of static identifiers
	 * and previously added {@link EEnum}s are used.
	 *
	 * @param eType
	 *            The {@link EClassifier} which shall be mapped.
	 * @return
	 *         The QB mapping of the given {@link EClassifier}. If the mapping could not be determined
	 *         the result will be of type {@link #UNSUPPORTED} or {@link #UNKNOWN}.
	 */
	public String getQBName(EClassifier eType) {
		final String eAttributeName = eType.getName();
		if (Arrays.asList(STRING_IDENTIFIER).contains(eAttributeName)) {
			return "qbString"; //$NON-NLS-1$
		} else if (Arrays.asList(INTEGER_IDENTIFIER).contains(eAttributeName)) {
			return "qbInteger"; //$NON-NLS-1$
		} else if (Arrays.asList(NUMBER_IDENTIFIER).contains(eAttributeName)) {
			return "qbNumber"; //$NON-NLS-1$
		} else if (Arrays.asList(BOOLEAN_IDENTIFIER).contains(eAttributeName)) {
			return "qbBoolean"; //$NON-NLS-1$
		} else if (Arrays.asList(DATE_IDENTIFIER).contains(eAttributeName)) {
			return "qbDateTime"; //$NON-NLS-1$
		} else if (Arrays.asList(UNSUPPORTED_IDENTIFIER)
			.contains(eAttributeName)) {
			return UNSUPPORTED;
		} else if (eType.getInstanceTypeName() != null
			&& eType.getInstanceTypeName().equals("javax.xml.datatype.XMLGregorianCalendar")) { //$NON-NLS-1$
			return "qbDateTime"; //$NON-NLS-1$
		} else {
			// check for enums
			for (final EEnum eEnum : enums) {
				if (eAttributeName.equals(eEnum.getName())) {
					return "qbEnum(" + enumToQB(eEnum) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return UNKNOWN;
	}

	private String enumToQB(EEnum eEnum) {
		String result = ""; //$NON-NLS-1$
		final List<EEnumLiteral> literals = new ArrayList<EEnumLiteral>(
			eEnum.getELiterals());
		if (literals.size() > 0) {
			result += "\"" + literals.get(0).getLiteral() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
			literals.remove(0);
		}
		for (final EEnumLiteral literal : literals) {
			result += ", \"" + literal.getLiteral() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}
}
