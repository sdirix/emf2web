package org.eclipse.emf.ecp.emf2web.generator

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
import org.eclipse.emf.ecp.emf2web.generator.FormsJsonGenerator
import org.eclipse.emf.ecp.emf2web.export.NameHelper

class FormsJsonExporterTest {
	private FormsJsonGenerator exporter;
	val testName = "testName";
	val testPath = "testPath";
	val EStructuralFeature mockFeature = EcorePackage.eINSTANCE.EClass_Abstract

	@Before
	def void init() {
		exporter = new FormsJsonGenerator(
			new NameHelper() {

				override getDisplayName(Setting setting) {
					throw new UnsupportedOperationException("TODO: auto-generated method stub")
				}

				override getDisplayName(EClass eClass, EStructuralFeature feat) {
					return testName
				}

			});
	}

	@Test
	def testBuildEmptyViewModel() {
		val view = VViewFactory.eINSTANCE.createView;
		val result = exporter.generate(view);
		assertEquals(emptyViewModel(), result);
	}

	@Test
	def testBuildViewWithAllContentsModel() {
		val view = VViewFactory.eINSTANCE.createView;
		val horizontal = VHorizontalFactory.eINSTANCE.createHorizontalLayout
		val vertical = VVerticalFactory.eINSTANCE.createVerticalLayout
		val control = VViewFactory.eINSTANCE.createControl

		//Use Ecore Ecore as a mock
		control.setDomainModelReference(mockFeature)
		
		view.children.add(horizontal)
		view.children.add(vertical)
		view.children.add(control)

		val result = exporter.generate(view);
		assertEquals(viewWithAllContentsModel(), result);
	}
	
	def String viewWithAllContentsModel() {
		'''
{
  "elements": [
    {
      "type": "QBHorizontalLayout",
      "elements": [
      ]
    },
    {
      "type": "QBVerticalLayout",
      "elements": [
      ]
    },
    {
      "type": "Control",
      "path": "abstract",
      "name": "testName"
    }
	]
)
		'''
	}

	@Test
	def testBuildControl() {
		val result = exporter.buildControl(testName, testPath)
		assertEquals(testControl, result);
	}

	@Test
	def testBuildHorizontalLayoutl() {
		val horizontal = VHorizontalFactory.eINSTANCE.createHorizontalLayout
		val result = exporter.buildContainer(horizontal)
		assertEquals(testHorizontal, result);
	}

	@Test
	def testBuildVerticalLayout() {
		val vertical = VVerticalFactory.eINSTANCE.createVerticalLayout
		val result = exporter.buildContainer(vertical)
		assertEquals(testVertical, result);
	}

	@Test
	def testBuildVerticalInHorizontalLayout() {
		val horizontal = VHorizontalFactory.eINSTANCE.createHorizontalLayout
		val vertical = VVerticalFactory.eINSTANCE.createVerticalLayout
		horizontal.children.add(vertical)
		val result = exporter.buildContainer(horizontal)
		assertEquals(testVerticalInHorizontal, result);
	}

	@Test
	def testBuildHorizontalInVerticalLayout() {
		val horizontal = VHorizontalFactory.eINSTANCE.createHorizontalLayout
		val vertical = VVerticalFactory.eINSTANCE.createVerticalLayout
		vertical.children.add(horizontal)
		val result = exporter.buildContainer(vertical)
		assertEquals(testHorizontalInVertical, result);
	}

	@Test
	def testBuild2HorizontalsInVerticalLayout() {
		val horizontal = VHorizontalFactory.eINSTANCE.createHorizontalLayout
		val horizontal2 = VHorizontalFactory.eINSTANCE.createHorizontalLayout

		val vertical = VVerticalFactory.eINSTANCE.createVerticalLayout
		vertical.children.add(horizontal)
		vertical.children.add(horizontal2)
		val result = exporter.buildContainer(vertical)
		assertEquals(test2HorizontalInVertical, result);
	}

	@Test
	def testBuildControlInVerticalLayout() {
		val vertical = VVerticalFactory.eINSTANCE.createVerticalLayout
		val control = VViewFactory.eINSTANCE.createControl

		//Use Ecore Ecore as a mock
		control.setDomainModelReference(mockFeature)
		vertical.children.add(control);
		val result = exporter.buildContainer(vertical)
		assertEquals(testControlInVertical, result);
	}

	@Test
	def testBuild2ControlsInVerticalLayout() {
		val vertical = VVerticalFactory.eINSTANCE.createVerticalLayout
		val control = VViewFactory.eINSTANCE.createControl
		val control2 = VViewFactory.eINSTANCE.createControl

		//Use Ecore Ecore as a mock
		control.setDomainModelReference(mockFeature)
		control2.setDomainModelReference(mockFeature)
		vertical.children.add(control);
		vertical.children.add(control2);
		val result = exporter.buildContainer(vertical)
		assertEquals(test2ControlsInVertical, result);
	}

	def String test2HorizontalInVertical() {
		'''
			{
			  "type": "QBVerticalLayout",
			  "elements": [
			    {
			      "type": "QBHorizontalLayout",
			      "elements": [
			      ]
			    },
			    {
			      "type": "QBHorizontalLayout",
			      "elements": [
			      ]
			    }
			  ]
			}
		'''
	}

	def String test2ControlsInVertical() {
		'''
			{
			  "type": "QBVerticalLayout",
			  "elements": [
			    {
			      "type": "Control",
			      "path": "«mockFeature.name»",
			      "name": "«testName»"
			    },
			    {
			      "type": "Control",
			      "path": "«mockFeature.name»",
			      "name": "«testName»"
			    }
			  ]
			}
		'''
	}

	def String testControlInVertical() {
		'''
			{
			  "type": "QBVerticalLayout",
			  "elements": [
			    {
			      "type": "Control",
			      "path": "«mockFeature.name»",
			      "name": "«testName»"
			    }
			  ]
			}
		'''
	}

	@Test
	def testgetQBType() {
		val vertical = VVerticalFactory.eINSTANCE.createVerticalLayout;
		val result = exporter.getQBType(vertical)
		assertEquals("QBVerticalLayout", result)
	}

	def String testVerticalInHorizontal() {
		'''
			{
			  "type": "QBHorizontalLayout",
			  "elements": [
			    {
			      "type": "QBVerticalLayout",
			      "elements": [
			      ]
			    }
			  ]
			}
		'''
	}

	def String testHorizontalInVertical() {
		'''
			{
			  "type": "QBVerticalLayout",
			  "elements": [
			    {
			      "type": "QBHorizontalLayout",
			      "elements": [
			      ]
			    }
			  ]
			}
		'''
	}

	def String testHorizontal() {
		'''
			{
			  "type": "QBHorizontalLayout",
			  "elements": [
			  ]
			}
		'''
	}

	def String testVertical() {
		'''
			{
			  "type": "QBVerticalLayout",
			  "elements": [
			  ]
			}
		'''
	}

	def String emptyViewModel() {
		'''
			{
			  "elements": [
				]
			)
		'''
	}

	def String testControl() {
		'''
			{
			  "type": "Control",
			  "path": "«testPath»",
			  "name": "«testName»"
			}
		'''
	}

}
