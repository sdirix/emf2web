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
package org.eclipse.emf.ecp.emf2web.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;

public class ClassMapping {

	public static final String[] stringIdentifier = new String[] { "EString" };

	public static final String[] byteIdentifier = new String[] { "EByte",
	"EByteObject" };

	public static final String[] charIdentifier = new String[] { "EChar",
	"ECharObject" };

	public static final String[] shortIdentifier = new String[] { "EShort",
	"EShortObject" };

	public static final String[] intIdentifier = new String[] { "EInt",
	"EIntegerObject" };

	public static final String[] longIdentifier = new String[] { "ELong",
	"ELongObject" };

	public static final String[] doubleIdentifier = new String[] { "EDouble",
		"EDoubleObject", "EFloat", "EFloatObject" };

	public static final String[] booleanIdentifier = new String[] { "EBoolean",
	"EBooleanObject" };

	public static final String[] dateIdentifier = new String[] { "EDate" };

	public static final String[] unsupportedIdentifier = new String[] {
		"EByteArray", "EDiagnosticChain", "EElist<E>", "EEnumerator",
		"EFeatureMap", "EFeatureMapEntry", "EInvocationTargetException",
		"EJavaClass<T>", "EJavaObject", "EMap<K,V>", "EResource",
		"EResourceSet", "ETreeIterator<E>" };

	public static Object convertObject(EAttribute eAttribute, Object object)
		throws ParseException, DatatypeConfigurationException {
		final EClassifier eType = eAttribute.getEType();
		if (Arrays.asList(byteIdentifier).contains(eType.getName())) {
			final Double value = (Double) object;
			return (byte) Math.round(value);
		} else if (Arrays.asList(charIdentifier).contains(eType.getName())) {
			final Double value = (Double) object;
			return (char) Math.round(value);
		} else if (Arrays.asList(shortIdentifier).contains(eType.getName())) {
			final Double value = (Double) object;
			return (short) Math.round(value);
		} else if (Arrays.asList(intIdentifier).contains(eType.getName())) {
			final Double value = (Double) object;
			return (int) Math.round(value);
		} else if (Arrays.asList(longIdentifier).contains(eType.getName())) {
			final Double value = (Double) object;
			return Math.round(value);
		} else if (Arrays.asList(doubleIdentifier).contains(eType.getName())) {
			final Double value = (Double) object;
			return value;
		} else if (Arrays.asList(stringIdentifier).contains(eType.getName())) {
			return object.toString();
		} else if (Arrays.asList(booleanIdentifier).contains(eType.getName())) {
			return (Boolean) object;
		} else if (Arrays.asList(dateIdentifier).contains(eType.getName())) {
			final String dateString = object.toString();
			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			final Date date = df.parse(dateString);
			return date;
		} else if (Arrays.asList(unsupportedIdentifier).contains(
			eType.getName())) {
			System.out.println("Type not supported");
		} else if (eType instanceof EEnum) {
			final EEnum eEnum = (EEnum) eAttribute.getEType();
			final EEnumLiteral literal = eEnum.getEEnumLiteral(object.toString());
			return literal.getInstance();
		} else if (eType.getInstanceTypeName().equals(
			"javax.xml.datatype.XMLGregorianCalendar")) {
			final String dateString = object.toString();
			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			final Date date = df.parse(dateString);
			final GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		}

		System.out.println("Unknown Type");
		return null;
	}

	public static Map<String, Object> eObjectToMap(EObject eObject) {
		final Map<String, Object> result = new HashMap<String, Object>();
		for (final EAttribute eAttribute : eObject.eClass().getEAllAttributes()) {

			if (eObject.eGet(eAttribute) == null) {
				result.put(eAttribute.getName(), null);
				continue;
			}

			final EClassifier eType = eAttribute.getEType();

			if (Arrays.asList(dateIdentifier).contains(eType.getName())) {
				final Date date = (Date) eObject.eGet(eAttribute);
				final DateFormat df = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				result.put(eAttribute.getName(), df.format(date));
			} else if (isNonStringIdentifier(eType.getName())) {
				result.put(eAttribute.getName(), eObject.eGet(eAttribute));
			} else if (eType.getInstanceTypeName().equals(
				"javax.xml.datatype.XMLGregorianCalendar")) {
				final XMLGregorianCalendar xcal = (XMLGregorianCalendar) eObject
					.eGet(eAttribute);
				final Date date = xcal.toGregorianCalendar().getTime();
				final DateFormat df = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				result.put(eAttribute.getName(), df.format(date));
			} else {
				result.put(eAttribute.getName(), eObject.eGet(eAttribute)
					.toString());
			}

		}
		return result;
	}

	public static boolean isNonStringIdentifier(String identifier) {
		return Arrays.asList(byteIdentifier).contains(identifier)
			|| Arrays.asList(charIdentifier).contains(identifier)
			|| Arrays.asList(shortIdentifier).contains(identifier)
			|| Arrays.asList(intIdentifier).contains(identifier)
			|| Arrays.asList(longIdentifier).contains(identifier)
			|| Arrays.asList(doubleIdentifier).contains(identifier)
			|| Arrays.asList(booleanIdentifier).contains(identifier);
	}
}
