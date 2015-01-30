package org.eclipse.emf.ecp.emf2web.export

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EStructuralFeature.Setting
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecp.view.spi.horizontal.model.VHorizontalFactory
import org.eclipse.emf.ecp.view.spi.model.VViewFactory
import org.eclipse.emf.ecp.view.spi.vertical.model.VVerticalFactory
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*
import org.eclipse.emf.ecore.EcoreFactory
import java.util.List
import java.util.ArrayList
import java.util.Arrays

class EcoreJSonExporterTest {
	private EcoreJSonExporter exporter;
	val testName = "testName";
	val testType = "testType";
	val List<String> testEnumValues = new ArrayList<String>(Arrays.asList("1A", "2B"))

	@Before
	def void init() {
		exporter = new EcoreJSonExporter()
	}

	@Test
	def testBuildEClass() {
		val result = exporter.buildEClass(EcoreFactory.eINSTANCE.createEClass);
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
		val eClass = EcoreFactory.eINSTANCE.createEClass;
		val eAttribute = EcoreFactory.eINSTANCE.createEAttribute
		eAttribute.name = testName
		eAttribute.EType = EcorePackage.eINSTANCE.EString
		eClass.EAttributes.add(eAttribute)
		val result = exporter.buildEClass(eClass)
		assertEquals(testEAttributeInEClass, result);
	}

	@Test
	def void testBuild2EAttributeInEClass() {
		val eClass = EcoreFactory.eINSTANCE.createEClass;
		val eAttribute = EcoreFactory.eINSTANCE.createEAttribute
		eAttribute.name = testName
		eAttribute.EType = EcorePackage.eINSTANCE.EString
		eClass.EAttributes.add(eAttribute)
		val eAttribute2 = EcoreFactory.eINSTANCE.createEAttribute
		eAttribute2.name = testName
		eAttribute2.EType = EcorePackage.eINSTANCE.EString
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
		val eClass = EcoreFactory.eINSTANCE.createEClass;
		val eAttribute = EcoreFactory.eINSTANCE.createEAttribute
		eAttribute.name = testName
		eAttribute.EType = EcorePackage.eINSTANCE.EDate
		eClass.EAttributes.add(eAttribute)
		val result = exporter.buildEClass(eClass)
		assertEquals(testDateEAttributeInEClass, result);
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
		val result = exporter.getQBType("EString")
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

}
