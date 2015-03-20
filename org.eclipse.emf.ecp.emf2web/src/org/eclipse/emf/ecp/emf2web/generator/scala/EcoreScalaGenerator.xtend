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
package org.eclipse.emf.ecp.emf2web.generator.scala

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EEnum

import static extension org.eclipse.emf.ecp.emf2web.util.TypeMapper.isAllowed
import static extension org.eclipse.emf.ecp.emf2web.util.TypeMapper.isBooleanType
import static extension org.eclipse.emf.ecp.emf2web.util.TypeMapper.isDateType
import static extension org.eclipse.emf.ecp.emf2web.util.TypeMapper.isEnumType
import static extension org.eclipse.emf.ecp.emf2web.util.TypeMapper.isIntegerType
import static extension org.eclipse.emf.ecp.emf2web.util.TypeMapper.isNumberType
import static extension org.eclipse.emf.ecp.emf2web.util.TypeMapper.isStringType

/**
 * @author Stefan Dirix <sdirix@eclipsesource.com>
 * 
 */
class EcoreScalaGenerator{

	/**
	 * Generates the QB schema object for the given {@link EClass}. 
	 * 
	 * @param eClass
	 * 		The {@link EClass} for which a string representation of a QB Scala Object is to be determined.
	 * 
	 * @return
	 * 		The string representing the QB Scala Object.
	 */
	def String generate(EClass eClass) {
		val requiredFeatures = eClass.getEAllStructuralFeatures.filter[f|f.getEType.isAllowed && f.lowerBound > 0];
		val optionalFeatures = eClass.getEAllStructuralFeatures.filter[f|f.getEType.isAllowed && f.lowerBound == 0];
		'''
		qbClass(	
			"id" -> objectId,
			«FOR eStructuralFeature : requiredFeatures SEPARATOR ','»
				"«eStructuralFeature.name»" -> «getName(eStructuralFeature.getEType)»
			«ENDFOR»
			«IF requiredFeatures.size > 0 && optionalFeatures.size > 0»,«ENDIF»
			«FOR eStructuralFeature : optionalFeatures SEPARATOR ','»
				"«eStructuralFeature.name»" -> optional(«getName(eStructuralFeature.getEType)»)
			«ENDFOR»
		)
		'''
	}
	
	private def String getName(EClassifier eType){
		switch (eType){
			case eType.isBooleanType: "Boolean"
			case eType.isDateType: "DateTime"
			case eType.isIntegerType: "Integer"
			case eType.isNumberType: "Number"
			case eType.isStringType: "String"
			case eType.isEnumType: "Enum(" + getEnumList(eType) + ")"
			default: "String"
		}
		//TODO: correct default?
	}
	
	private def String getEnumList(EClassifier eType){
		'''
		«FOR name : (eType as EEnum).getELiterals.map[name] SEPARATOR ','»"«name»"«ENDFOR»
		'''
	}
}