package org.eclipse.emf.ecp.emf2web.generator

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.util.ArrayList
import java.util.Arrays
import java.util.List
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.emf.ecore.EcorePackage
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

class EcoreJsonExporterTest {
	static final val ECORE_PACKAGE = EcorePackage.eINSTANCE
	static final val ECORE_FACTORY = EcoreFactory.eINSTANCE
	static final val TEST_ECLASS_NAME = "TestEClass";
	static final val TEST_EATTRIBUTE_NAME = "testAttribute";
	static final val TEST_TYPE = "testType";

	final val List<String> testEnumValues = new ArrayList<String>(Arrays.asList("1A", "2B"))

	private EcoreJsonGenerator exporter;

	@Before
	def void init() {
		exporter = new EcoreJsonGenerator()
	}

	def buildEnum() {
		val eEnum = ECORE_FACTORY.createEEnum
		eEnum.name = EcoreJsonExporterTest.TEST_ECLASS_NAME
		for (String literal : testEnumValues) {
			val enumLiteral = ECORE_FACTORY.createEEnumLiteral
			enumLiteral.name = literal
			eEnum.ELiterals.add(enumLiteral)
		}
		eEnum
	}

	@Test
	def void testBuildClassWithReference() {
		val eClass = ECORE_FACTORY.createEClass
		eClass.name = EcoreJsonExporterTest.TEST_ECLASS_NAME

		val eReference = createReference()
		eReference.EType = null // TODO
		eClass.EStructuralFeatures.add(eReference)

	// TODO
	//assertEquals(testEnum, result);
	}

	def createReference() {
		val eReference = ECORE_FACTORY.createEReference
		eReference.name = EcoreJsonExporterTest.TEST_ECLASS_NAME + "Ref"
		eReference.containment = true
		eReference
	}

	/*
	 * New tests
	 */
	@Test
	def void createJsonSchemaElementFromEmptyEClass() {
		val eClass = emptyEClass()
		val result = exporter.createJsonSchemaElement(eClass)
		assertEqualsJson(emptyEClassJsonElement(), result);
	}

	@Test
	def void createJsonSchemaElementFromEClassWithOptionalStringAttribute() {
		val eClass = emptyEClass()
		val optionalStringEAttribute = stringEAttribute(0, 1)
		eClass.EStructuralFeatures.add(optionalStringEAttribute)
		val result = exporter.createJsonSchemaElement(eClass)
		assertEqualsJson(eClassWithOptionalStringEAttributeJsonElement(), result);
	}
	
	@Test
	def void createJsonSchemaElementFromEClassWithMandatoryStringAttribute() {
		val eClass = emptyEClass()
		val mandatoryStringEAttribute = stringEAttribute(1, 1)
		eClass.EStructuralFeatures.add(mandatoryStringEAttribute)
		val result = exporter.createJsonSchemaElement(eClass)
		assertEqualsJson(eClassWithMandatoryStringEAttributeJsonElement(), result);
	}

	private def emptyEClass() {
		val eClass = ECORE_FACTORY.createEClass
		eClass.name = TEST_ECLASS_NAME
		eClass
	}

	private def stringEAttribute(int lower, int upper) {
		val eAttribute = ECORE_FACTORY.createEAttribute
		eAttribute.name = TEST_EATTRIBUTE_NAME
		eAttribute.lowerBound = lower
		eAttribute.upperBound = upper
		eAttribute.EType = ECORE_PACKAGE.EString
		eAttribute
	}

	private def emptyEClassJsonElement() {
		'''
			{
			  "type": "object",
			  "properties": {
			  },
			  "additionalProperties": false
			}
		'''.toJsonElement
	}
	
	private def eClassWithOptionalStringEAttributeJsonElement() {
		'''
			{
			  "type": "object",
			  "properties": {
			  	"«TEST_EATTRIBUTE_NAME»": {"type": "string"}
			  },
			  "additionalProperties": false
			}
		'''.toJsonElement
	}
	
	private def eClassWithMandatoryStringEAttributeJsonElement() {
		'''
			{
			  "type": "object",
			  "properties": {
			  	"«TEST_EATTRIBUTE_NAME»": {"type": "string"}
			  },
			  "additionalProperties": false,
			  "required": [
			    "«TEST_EATTRIBUTE_NAME»"
			  ]
			}
		'''.toJsonElement
	}

	private def toJsonElement(CharSequence chars) {
		new JsonParser().parse(chars.toString)
	}
	
	private def assertEqualsJson(JsonElement expected, JsonElement actual) {
		if (!expected.equals(actual)) {
			val gson = new GsonBuilder().setPrettyPrinting().create()
			assertEquals(gson.toJson(expected), gson.toJson(actual))
		} else {
			assertEquals(expected, actual)
		}
	}
}
