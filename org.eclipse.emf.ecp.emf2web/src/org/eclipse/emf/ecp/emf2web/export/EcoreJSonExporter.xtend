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
 * Philip Langer - re-implementation based on Gson 
 *******************************************************************************/
package org.eclipse.emf.ecp.emf2web.export

import java.io.File
import java.util.Set
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.resource.Resource
import org.apache.commons.io.FileUtils
import org.eclipse.emf.ecp.view.spi.model.VView
import org.eclipse.emf.ecp.view.spi.model.VControl
import org.eclipse.emf.ecp.view.spi.horizontal.model.VHorizontalLayout
import org.eclipse.emf.ecp.view.spi.vertical.model.VVerticalLayout
import org.eclipse.emf.ecp.view.spi.model.VElement
import org.eclipse.emf.ecp.view.spi.model.VContainer
import org.eclipse.emf.ecore.EStructuralFeature
import java.util.List
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.EEnum
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapterFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonObject
import org.eclipse.emf.ecore.EObject
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.util.Collection
import org.eclipse.emf.ecore.EReference
import com.google.gson.JsonArray
import java.util.HashSet

/** 
 * The class which handles the conversion from ecore files to qbForm files.
 * 
 */
class EcoreJSonExporter {

	private static final val TYPE = "type"
	private static final val OBJECT = "object"
	private static final val REQUIRED = "required"
	private static final val PROPERTIES = "properties"
	private static final val ADDITIONAL_PROPERTIES = "additionalProperties"

	private val GsonBuilder builder = new GsonBuilder()

	def EcoreJSonExporter() {
		initializeBuilder()
	}

	private def initializeBuilder() {
		builder.registerTypeAdapter(EClass,
			[ EClass eClass, Type type, JsonSerializationContext context |
				createJsonSchemaElement(eClass)
			]);
		builder.registerTypeAdapter(EStructuralFeature,
			[ EStructuralFeature feature, Type type, JsonSerializationContext context |
				createJsonSchemaElement(feature)
			]);
	}
	
	
	/**
	 * The main method which handles the conversion and export of the ecore files.
	 *
	 * @param ecoreModel
	 *            The {@link Resource} containing the ecore model.
	 * @param selectedClasses
	 *            Collection of classes which shall be converted to the qb format.
	 * @param viewModels
	 *            Collection of view models for the {@code selectedClasses} which shall not use the default layout.
	 * @param destinationDir
	 *            The directory where the converted files shall be saved.
	 * */
	def public void exportEcoreModel(EClass eClass, File destinationDir) {
		val controllerDest = "public/" + eClass.name + ".json"
		
		// TODO implement in the end

	// val viewModel = buildEcoreModel(view)
	// FileUtils.writeStringToFile(new File(destinationDir, controllerDest), viewModel);
	}
	

	def dispatch JsonElement createJsonSchemaElement(EClass eClass) {
		val jsonObject = new JsonObject().withObjectType
		jsonObject.withProperties(eClass.EAllStructuralFeatures)
		jsonObject.with(ADDITIONAL_PROPERTIES, false)
		jsonObject.withRequiredProperties(eClass.EAllStructuralFeatures.filter[required])
	}

	def dispatch JsonElement createJsonSchemaElement(EAttribute attribute) {
		new JsonObject().withTypeProperties(attribute.EType, attribute.upperBound)
	}

	private def JsonObject withTypeProperties(JsonObject jsonObject, EClassifier eClassifier, int upper) {
		if (upper > 1) {
			jsonObject.withType("array")
			jsonObject.with("items", new JsonObject().withTypeProperties(eClassifier))
		} else {
			jsonObject.withTypeProperties(eClassifier)
		}
		jsonObject
	}

	private def JsonObject withTypeProperties(JsonObject jsonObject, EClassifier eClassifier) {
		jsonObject.withType(jsonType(eClassifier))
		if (eClassifier.equals(EcorePackage.eINSTANCE.EDate)) {
			jsonObject.with("format", "date-time")
		} else if (eClassifier instanceof EEnum) {
			val eEnum = eClassifier as EEnum
			val literalArray = new JsonArray
			for (name : eEnum.ELiterals.map[name]) {
				literalArray.add(new JsonPrimitive(name))
			}
			jsonObject.with("enum", literalArray)
		}
		jsonObject
	}

	private def jsonType(EClassifier eClassifier) {
		switch (eClassifier) {
			case eClassifier.equals(EcorePackage.eINSTANCE.EBoolean): "boolean"
			case eClassifier.equals(EcorePackage.eINSTANCE.EInt): "integer"
			case eClassifier.equals(EcorePackage.eINSTANCE.EDouble): "number"
			case eClassifier.equals(EcorePackage.eINSTANCE.EFloat): "number"
			default: "string"
		}
	}

	def dispatch JsonElement createJsonSchemaElement(EReference reference) {
		new JsonObject()
		// TODO implement
	}

	def dispatch JsonElement createJsonSchemaElement(EObject eObject) {
		throw new UnsupportedOperationException(
			"Cannot create a Json Schema element for EObjects that are not " +
				"EClasses, EEnums, EAttributes, or EReferences.")
	}

	/* ******************************************************************
	 * Builder methods for a more fluent style of creating Json elements
	 * ******************************************************************/
	private def withObjectType(JsonObject object) {
		object.withType(OBJECT)
	}

	private def withType(JsonObject jsonObject, String type) {
		jsonObject.with(TYPE, type)
	}

	private def withRequiredProperties(JsonObject jsonObject, Iterable<EStructuralFeature> requiredProperties) {
		val requiredPropertiesArray = new JsonArray
		for (name : requiredProperties.map[name]) {
			requiredPropertiesArray.add(new JsonPrimitive(name))
		}
		if (!requiredPropertiesArray.empty) {
			jsonObject.with(REQUIRED, requiredPropertiesArray)
		}
	}

	private def withProperties(JsonObject jsonObject, Collection<? extends EStructuralFeature> features) {
		val propertyObject = new JsonObject
		for (feature : features) {
			val jsonElement = builder.create.toJsonTree(feature, EStructuralFeature)
			propertyObject.add(feature.name, jsonElement)
		}
		jsonObject.with(PROPERTIES, propertyObject)
	}

	private def dispatch with(JsonObject jsonObject, String propertyName, JsonElement value) {
		jsonObject.add(propertyName, value)
		jsonObject
	}

	private def dispatch with(JsonObject jsonObject, String propertyName, String value) {
		jsonObject.addProperty(propertyName, value)
		jsonObject
	}

	private def dispatch with(JsonObject jsonObject, String propertyName, Number value) {
		jsonObject.addProperty(propertyName, value)
		jsonObject
	}

	private def dispatch with(JsonObject jsonObject, String propertyName, Boolean value) {
		jsonObject.addProperty(propertyName, value)
		jsonObject
	}

	private def dispatch with(JsonObject jsonObject, String propertyName, Character value) {
		jsonObject.addProperty(propertyName, value)
		jsonObject
	}

	private def dispatch with(JsonObject jsonObject, String propertyName, Collection<? extends EObject> collection) {
		val jsonElements = collection.map [ item |
			createJsonSchemaElement(item)
		]
		val jsonArray = new JsonArray()
		for (jsonElement : jsonElements) {
			jsonArray.add(jsonElement)
		}
		jsonObject.add(propertyName, jsonArray)
		jsonObject
	}
}
