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
package org.eclipse.emf.ecp.emf2web.util

import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EcorePackage

/**
 * @author Stefan Dirix
 * 
 */
class TypeMapper {
	def static isBooleanType(EClassifier eType) {
		switch (eType) {
			case EcorePackage.eINSTANCE.EBoolean: true
			case EcorePackage.eINSTANCE.EBooleanObject: true
			default: false
		}
	}

	def static isStringType(EClassifier eType) {
		switch (eType) {
			case EcorePackage.eINSTANCE.EString: true
			default: false
		}
	}

	def static isNumberType(EClassifier eType) {
		switch (eType) {
			case EcorePackage.eINSTANCE.EDouble: true
			case EcorePackage.eINSTANCE.EDoubleObject: true
			case EcorePackage.eINSTANCE.EFloat: true
			case EcorePackage.eINSTANCE.EFloatObject: true
			default: false
		}
	}

	def static isIntegerType(EClassifier eType) {
		switch (eType) {
			case EcorePackage.eINSTANCE.EBigDecimal: true
			case EcorePackage.eINSTANCE.EBigInteger: true
			case EcorePackage.eINSTANCE.EByte: true
			case EcorePackage.eINSTANCE.EByteObject: true
			case EcorePackage.eINSTANCE.EChar: true
			case EcorePackage.eINSTANCE.ECharacterObject: true
			case EcorePackage.eINSTANCE.EInt: true
			case EcorePackage.eINSTANCE.EIntegerObject: true
			case EcorePackage.eINSTANCE.ELong: true
			case EcorePackage.eINSTANCE.ELongObject: true
			case EcorePackage.eINSTANCE.EShort: true
			case EcorePackage.eINSTANCE.EShortObject: true
			default: false
		}
	}

	def static isDateType(EClassifier eType) {
		EcorePackage.eINSTANCE.EDate.isInstance(eType) || eType.instanceTypeName?.equals("javax.xml.datatype.XMLGregorianCalendar")
	}
	
	def static isEnumType(EClassifier eType) {
		switch (eType) {
			case EcorePackage.eINSTANCE.EEnum.isInstance(eType): true
			default: false
		}
	}

	def static isUnsupportedType(EClassifier eType) {
		switch (eType) {
			case EcorePackage.eINSTANCE.EByteArray: true
			case EcorePackage.eINSTANCE.EDiagnosticChain: true
			case EcorePackage.eINSTANCE.EEList: true
			case EcorePackage.eINSTANCE.EEnumerator: true
			case EcorePackage.eINSTANCE.EFeatureMap: true
			case EcorePackage.eINSTANCE.EFeatureMapEntry: true
			case EcorePackage.eINSTANCE.EInvocationTargetException: true
			case EcorePackage.eINSTANCE.EJavaClass: true
			case EcorePackage.eINSTANCE.EJavaObject: true
			case EcorePackage.eINSTANCE.EMap: true
			case EcorePackage.eINSTANCE.EResource: true
			case EcorePackage.eINSTANCE.EResourceSet: true
			case EcorePackage.eINSTANCE.ETreeIterator: true
			default: false
		}
	}

	def static isAllowed(EClassifier eType) {
		eType.booleanType || eType.stringType || eType.numberType || eType.integerType || eType.dateType || eType.enumType
	}
}



