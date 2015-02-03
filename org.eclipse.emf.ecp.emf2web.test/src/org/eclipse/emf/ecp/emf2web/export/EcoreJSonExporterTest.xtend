package org.eclipse.emf.ecp.emf2web.export

import com.google.gson.Gson
import com.google.gson.JsonParser
import java.util.ArrayList
import java.util.Arrays
import java.util.List
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EStructuralFeature.Setting
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecp.view.spi.horizontal.model.VHorizontalFactory
import org.eclipse.emf.ecp.view.spi.model.VViewFactory
import org.eclipse.emf.ecp.view.spi.vertical.model.VVerticalFactory
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*
import com.google.gson.JsonElement
import com.google.gson.GsonBuilder

class EcoreJSonExporterTest {
	static final val ECORE_PACKAGE = EcorePackage.eINSTANCE
	static final val ECORE_FACTORY = EcoreFactory.eINSTANCE
	static final val TEST_ECLASS_NAME = "TestEClass";
	static final val TEST_EATTRIBUTE_NAME = "testAttribute";
	static final val TEST_TYPE = "testType";

	final val List<String> testEnumValues = new ArrayList<String>(Arrays.asList("1A", "2B"))

	private EcoreJSonExporter exporter;

	@Before
	def void init() {
		exporter = new EcoreJSonExporter()
	}

	@Test
	def testBuildEAttribute() {
		val result = exporter.buildEAttribute(org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME,
			TEST_TYPE)
		assertEquals(testEAttribute, result);
	}

	def String testEAttribute() '''
		"«org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME»": {"type": "«TEST_TYPE»"}
	'''

	@Test
	def testBuildEnum() {
		val result = exporter.buildEnum(org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME,
			testEnumValues)
		assertEquals(testEnum, result);
	}

	def String testEnum() '''
		"«org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME»": {
		  "type": "string",
		  "enum": [
		    "«testEnumValues.get(0)»",
		    "«testEnumValues.get(1)»"
		  ]
		}
	'''

	@Test
	def void testBuildEAttributeInEClass() {
		val eClass = ECORE_FACTORY.createEClass;
		val eAttribute = ECORE_FACTORY.createEAttribute
		eAttribute.name = org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME
		eAttribute.EType = ECORE_PACKAGE.EString
		eClass.EAttributes.add(eAttribute)
		val result = exporter.buildEClass(eClass)
		assertEquals(testEAttributeInEClass, result);
	}

	@Test
	def void testBuild2EAttributeInEClass() {
		val eClass = ECORE_FACTORY.createEClass;
		val eAttribute = ECORE_FACTORY.createEAttribute
		eAttribute.name = org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME
		eAttribute.EType = ECORE_PACKAGE.EString
		eClass.EAttributes.add(eAttribute)
		val eAttribute2 = ECORE_FACTORY.createEAttribute
		eAttribute2.name = org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME
		eAttribute2.EType = ECORE_PACKAGE.EString
		eClass.EAttributes.add(eAttribute2)

		val result = exporter.buildEClass(eClass)
		assertEquals(test2EAttributeInEClass, result);
	}

	def String test2EAttributeInEClass() '''
		{
		  "type": "object",
		  "properties": {
		    "«org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME»": {"type": "string"},
		    "«org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME»": {"type": "string"}
		  }
		}
	'''

	def String testEAttributeInEClass() '''
		{
		  "type": "object",
		  "properties": {
		    "«org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME»": {"type": "string"}
		  }
		}
	'''

	@Test
	def void testBuildDateEAttributeInEClass() {
		val eClass = ECORE_FACTORY.createEClass;
		val eAttribute = eAttributeWithTestName(ECORE_PACKAGE.EDate)
		eClass.EAttributes.add(eAttribute)
		val result = exporter.buildEClass(eClass)
		assertEquals(testDateEAttributeInEClass, result);
	}

	def eAttributeWithTestName() {
		val eAttribute = ECORE_FACTORY.createEAttribute
		eAttribute.name = org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME
		eAttribute
	}

	def eAttributeWithTestName(EDataType type) {
		val eAttribute = eAttributeWithTestName()
		eAttribute.EType = type
		eAttribute
	}

	def String testDateEAttributeInEClass() '''
		{
		  "type": "object",
		  "properties": {
		    "«org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME»": {
		      "type": "string",
		      "format": "date-time"
		    }
		  }
		}
	'''

	@Test
	def void testGetQBType() {
		val result = exporter.asQBType("EString")
		assertEquals(result, "string")
	}

	@Test
	def void testBuildDateEAttribute() {
		val result = exporter.buildDateEAttribute(
			org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME)
		assertEquals(testDateEAttribute, result);
	}

	def String testDateEAttribute() '''
		"«org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME»": {
		  "type": "string",
		  "format": "date-time"
		}
	'''

	@Test
	def testBuildEnumAsEAttributeType() {
		val attributeWithEnumType = eAttributeWithTestName(buildEnum)
		val result = exporter.buildEAttribute(attributeWithEnumType)
		assertEquals(testEnum, result);
	}

	def buildEnum() {
		val eEnum = ECORE_FACTORY.createEEnum
		eEnum.name = org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME
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
		eClass.name = org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME

		val eReference = createReference()
		eReference.EType = null // TODO
		eClass.EStructuralFeatures.add(eReference)

	// TODO
	//assertEquals(testEnum, result);
	}

	def createReference() {
		val eReference = ECORE_FACTORY.createEReference
		eReference.name = org.eclipse.emf.ecp.emf2web.export.EcoreJSonExporterTest.TEST_ECLASS_NAME + "Ref"
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
