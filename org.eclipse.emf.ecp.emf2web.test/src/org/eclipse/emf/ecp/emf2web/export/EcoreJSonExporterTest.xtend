package org.eclipse.emf.ecp.emf2web.export

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

class EcoreJSonExporterTest {
	static final val ecorePackage = EcorePackage.eINSTANCE
	static final val ecoreFactory = EcoreFactory.eINSTANCE
	static final val testName = "testName";
	static final val testType = "testType";
	
	final val List<String> testEnumValues = new ArrayList<String>(Arrays.asList("1A", "2B"))
	
	private EcoreJSonExporter exporter;
	

	@Before
	def void init() {
		exporter = new EcoreJSonExporter()
	}

	@Test
	def testBuildEClass() {
		val result = exporter.buildEClass(ecoreFactory.createEClass);
		assertEquals(emptyEClass, result);
	}

	def String emptyEClass() '''
		{
		  "type": "object",
		  "properties": {
		  }
		}
	'''

	@Test
	def testBuildEAttribute() {
		val result = exporter.buildEAttribute(testName, testType)
		assertEquals(testEAttribute, result);
	}

	def String testEAttribute() '''
		"«testName»": {"type": "«testType»"}
	'''

	@Test
	def testBuildEnum() {
		val result = exporter.buildEnum(testName, testEnumValues)
		assertEquals(testEnum, result);
	}

	def String testEnum() '''
		"«testName»": {
		  "type": "string",
		  "enum": [
		    "«testEnumValues.get(0)»",
		    "«testEnumValues.get(1)»"
		  ]
		}
	'''

	@Test
	def void testBuildEAttributeInEClass() {
		val eClass = ecoreFactory.createEClass;
		val eAttribute = ecoreFactory.createEAttribute
		eAttribute.name = testName
		eAttribute.EType = ecorePackage.EString
		eClass.EAttributes.add(eAttribute)
		val result = exporter.buildEClass(eClass)
		assertEquals(testEAttributeInEClass, result);
	}
	
	@Test
	def void testBuild2EAttributeInEClass() {
		val eClass = ecoreFactory.createEClass;
		val eAttribute = ecoreFactory.createEAttribute
		eAttribute.name = testName
		eAttribute.EType = ecorePackage.EString
		eClass.EAttributes.add(eAttribute)
		val eAttribute2 = ecoreFactory.createEAttribute
		eAttribute2.name = testName
		eAttribute2.EType = ecorePackage.EString
		eClass.EAttributes.add(eAttribute2)

		val result = exporter.buildEClass(eClass)
		assertEquals(test2EAttributeInEClass, result);
	}

	def String test2EAttributeInEClass() '''
			{
			  "type": "object",
			  "properties": {
			    "«testName»": {"type": "string"},
			    "«testName»": {"type": "string"}
			  }
			}
	'''

	def String testEAttributeInEClass() '''
		{
		  "type": "object",
		  "properties": {
		    "«testName»": {"type": "string"}
		  }
		}
	'''
	
	@Test
	def void testBuildDateEAttributeInEClass() {
		val eClass = ecoreFactory.createEClass;
		val eAttribute = eAttributeWithTestName(ecorePackage.EDate)
		eClass.EAttributes.add(eAttribute)
		val result = exporter.buildEClass(eClass)
		assertEquals(testDateEAttributeInEClass, result);
	}
	
	def eAttributeWithTestName() {
		val eAttribute = ecoreFactory.createEAttribute
		eAttribute.name = testName
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
		    "«testName»": {
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
		val result = exporter.buildDateEAttribute(testName)
		assertEquals(testDateEAttribute, result);
	}
	
	def String testDateEAttribute() '''
		"«testName»": {
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
		val eEnum = ecoreFactory.createEEnum
		eEnum.name = testName
		for (String literal : testEnumValues) {
			val enumLiteral = ecoreFactory.createEEnumLiteral
			enumLiteral.name = literal
			eEnum.ELiterals.add(enumLiteral)
		}
		eEnum
	}

}
