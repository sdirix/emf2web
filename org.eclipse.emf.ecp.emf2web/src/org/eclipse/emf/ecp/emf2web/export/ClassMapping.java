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
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;

public class ClassMapping {

	public static final String[] stringIdentifier = new String[] { "EString" };
	public static final String[] numberIdentifier = new String[] { "EDouble",
		"EDoubleObject", "EFloat", "EFloatObject" };

	public static final String[] integerIdentifier = new String[] {
		"EBigDecimal", "EBigInteger", "EByte", "EByteObject", "EChar",
		"ECharacterObject", "EInt", "EIntegerObject", "ELong",
		"ELongObject", "EShort", "EShortObject" };

	public static final String[] booleanIdentifier = new String[] { "EBoolean",
	"EBooleanObject" };

	public static final String[] dateIdentifier = new String[] { "EDate" };

	public static final String[] unsupportedIdentifier = new String[] {
		"EByteArray", "EDiagnosticChain", "EElist<E>", "EEnumerator",
		"EFeatureMap", "EFeatureMapEntry", "EInvocationTargetException",
		"EJavaClass<T>", "EJavaObject", "EMap<K,V>", "EResource",
		"EResourceSet", "ETreeIterator<E>" };

	private final List<EEnum> enums = new ArrayList<EEnum>();

	public void addEEnum(EEnum eEnum) {
		enums.add(eEnum);
	}

	public void addAllEEnum(List<EEnum> eEnums) {
		enums.addAll(eEnums);
	}

	public boolean isAllowed(EClassifier eType) {
		final String qbName = getQBName(eType);
		return !qbName.equals("unknown") && !qbName.equals("unsupported");
	}

	public String getQBName(EClassifier eType) {
		final String eAttributeName = eType.getName();
		if (Arrays.asList(stringIdentifier).contains(eAttributeName)) {
			return "qbString";
		} else if (Arrays.asList(integerIdentifier).contains(eAttributeName)) {
			return "qbInteger";
		} else if (Arrays.asList(numberIdentifier).contains(eAttributeName)) {
			return "qbNumber";
		} else if (Arrays.asList(booleanIdentifier).contains(eAttributeName)) {
			return "qbBoolean";
		} else if (Arrays.asList(dateIdentifier).contains(eAttributeName)) {
			return "qbDateTime";
		} else if (Arrays.asList(unsupportedIdentifier)
			.contains(eAttributeName)) {
			return "unsupported";
		} else if (eType.getInstanceTypeName() != null
			&& eType.getInstanceTypeName().equals(
				"javax.xml.datatype.XMLGregorianCalendar")) {
			return "qbDateTime";
		} else {
			// check for enums
			for (final EEnum eEnum : enums) {
				if (eAttributeName.equals(eEnum.getName())) {
					return "qbEnum(" + enumToQB(eEnum) + ")";
				}
			}
		}
		// throw new IllegalArgumentException(eAttributeName + " is unknown");
		return "unknown";
	}

	private String enumToQB(EEnum eEnum) {
		String result = "";
		final List<EEnumLiteral> literals = new ArrayList<EEnumLiteral>(
			eEnum.getELiterals());
		if (literals.size() > 0) {
			result += "\"" + literals.get(0).getLiteral() + "\"";
			literals.remove(0);
		}
		for (final EEnumLiteral literal : literals) {
			result += ", \"" + literal.getLiteral() + "\"";
		}
		return result;
	}
}
