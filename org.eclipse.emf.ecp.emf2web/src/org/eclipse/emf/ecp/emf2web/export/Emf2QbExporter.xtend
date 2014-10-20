package org.eclipse.emf.ecp.emf2web.export

import java.io.File
import java.util.ArrayList
import org.apache.commons.io.FileUtils
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClass

import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.resource.Resource
import java.util.Set
import java.util.HashSet
import org.eclipse.emf.ecp.view.spi.model.VView
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecp.view.spi.horizontal.model.VHorizontalLayout
import org.eclipse.emf.ecp.view.spi.vertical.model.VVerticalLayout
import org.eclipse.emf.ecp.view.spi.label.model.VLabel
import org.eclipse.emf.ecp.view.spi.group.model.VGroup
import org.eclipse.emf.ecp.view.spi.model.VControl
import java.util.Comparator

class Emf2QbExporter {

	var ClassMapping classMapper = null;
	
	def public void export(Resource ecoreModel, Set<EClass> selectedClasses, Set<Resource> viewModels, File destinationDir){
		classMapper = new ClassMapping()

		val allEEnums = new ArrayList<EEnum>();
		ecoreModel.allContents.filter(EPackage).forEach [ ePackage |
			allEEnums.addAll(ePackage.EClassifiers.filter(EEnum))
		]
		
		classMapper.addAllEEnum(allEEnums)
		
		
		val eClasses = new HashSet<EClass>()
		val vModels = new HashSet<Resource>()
		
		if (selectedClasses == null) {
			ecoreModel.allContents.filter(EPackage).forEach [ ePackage |
				eClasses.addAll(ePackage.EClassifiers.filter(EClass))
			]
		} else {
			eClasses.addAll(selectedClasses)
		}
		
		if(viewModels != null) {
			vModels.addAll(viewModels)
		}
		
		eClasses.forEach[ eClass |
			val controllerText = buildControllerFile(eClass)
			val controllerDest = "app/controllers/" + eClass.name + "Controller.scala"
			
			FileUtils.writeStringToFile(new File(destinationDir, controllerDest), controllerText);
			
			val schemaText = buildSchemaFile(eClass, viewModels)
			val schemaDest = "app/controllers/" + eClass.name + "Schema.scala"
			
			FileUtils.writeStringToFile(new File(destinationDir, schemaDest), schemaText);
		]
		
		val routesText = buildRoutesFile(eClasses)
		val routesDest = "conf/routes"
		FileUtils.writeStringToFile(new File(destinationDir, routesDest), routesText);
		
		
	}
	
	def private String buildRoutesFile(Set<EClass> selectedClasses){
		val classes = new ArrayList<EClass>(selectedClasses)
		classes.sort(new Comparator<EClass>(){	
			override equals(Object arg0) {
				return arg0 == this
			}
			override compare(EClass arg0, EClass arg1) {
				arg0.name.compareTo(arg1.name)
			}	
		})
		
		routesIntro
		+
		'''
		«FOR eClass : selectedClasses»
		GET     /«eClass.name.toLowerCase»/model		controllers.«eClass.name»Controller.getModel
		GET     /«eClass.name.toLowerCase»/view			controllers.«eClass.name»Controller.getView
		->		/«eClass.name.toLowerCase»				controllers.«eClass.name»Router
		
		«ENDFOR»	
		'''
	}
	
	def private String routesIntro(){
		'''
		# Routes
		# This file defines all application routes (Higher priority routes first)
		# ~~~~
		
		# Home page
		GET     /                           controllers.Application.index
		
		# Map static resources from the /public folder to the /assets URL path
		GET     /assets/*file               controllers.Assets.at(path="/public", file)
		
		'''
	}
	
	def private String buildControllerFile(EClass eClass){
		val name = eClass.name
		'''
		«controllerIntro»
		'''
		+
		'''
		object «name»Controller extends MongoController with QBCrudController {

		  lazy val collection = new QBMongoCollection("«name.toLowerCase»")(db) with QBCollectionValidation {
		    override def schema = «name»Schema.modelSchema
		  }
		
		  override def createSchema = «name»Schema.modelSchema -- "id"
		
		  def getView = JsonHeaders {
		    Action {
		      Ok(Json.toJson(«name»Schema.viewSchema))
		    }
		  }
		  
		  def getModel = JsonHeaders {
		    Action {
		      Ok(Json.toJson(«name»Schema.modelSchema))
		    }
		  }
		}
		
		object «name»Router extends QBRouter {
		  override def qbRoutes = «name»Controller.crudRoutes
		}
		'''
	}
	
	def private String controllerIntro(){
		'''
		package controllers

		import play.api.libs.concurrent.Execution.Implicits.defaultContext
		import play.api.mvc.Action
		import play.modules.reactivemongo.MongoController
		import org.qbproject.api.schema.QBSchema._
		import org.qbproject.api.controllers.{JsonHeaders, QBCrudController}
		import org.qbproject.api.mongo.{QBCollectionValidation, QBMongoCollection}
		import org.qbproject.api.routing.QBRouter
		import play.api.libs.json.{JsUndefined, JsValue, Json}
		'''
	}
	
	def private String buildSchemaFile(EClass eClass, Set<Resource> viewModels){
		'''
		«scalaIntro»
		'''
		+
		'''
		object «eClass.name»Schema {
				«buildModelObject(eClass)»
				«buildViewModelObject(eClass, viewModels)»
		}
		'''
	}
	
	def private String buildViewModelObject(EClass eClass, Set<Resource> viewModels){
		val viewModel = findViewModel(eClass, viewModels)
		if(viewModel == null){
			buildDefaultViewModel(eClass)
		}else{
			'''
			val viewSchema = QBViewModel(
				modelSchema,
				«buildViewModel(eClass, viewModel.contents.get(0))»
			)
			'''
		}
	}
	
	def private String buildViewModel(EClass eClass, EObject viewModelElement) {
		switch viewModelElement {
			VView:
				'''
				«FOR element : viewModelElement.children SEPARATOR ','»
				«buildViewModel(eClass, element)»
				«ENDFOR»
				'''
			VHorizontalLayout:
				'''
				QBHorizontalLayout(
					«FOR element : viewModelElement.children SEPARATOR ','»
					«buildViewModel(eClass, element)»
					«ENDFOR»
				)
				'''
			VVerticalLayout:
				'''
				QBVerticalLayout(
					«FOR element : viewModelElement.children SEPARATOR ','»
					«buildViewModel(eClass, element)»
					«ENDFOR»
				)
				'''
			VGroup:
				'''
				QBGroup("«viewModelElement.name»",
					«FOR element : viewModelElement.children SEPARATOR ','»
					«buildViewModel(eClass, element)»
					«ENDFOR»	
				)
				'''
			VLabel:
				'''
				QBLabel("«viewModelElement.name»")
				'''	
			VControl:
				'''
				QBViewControl("«viewModelElement.domainModelReference.EStructuralFeatureIterator.next.name»", QBViewPath("«viewModelElement.domainModelReference.EStructuralFeatureIterator.next.name»"))
				'''
			default: ""
		}
	}
	
	def private Resource findViewModel(EClass eClass, Set<Resource> viewModels){
		viewModels.findFirst[viewResource |
			val root = viewResource.contents.get(0)
			if(root instanceof VView){
				return root.rootEClass == eClass
			}
			return false
		]
	} 
	
	def private String scalaIntro(){
		'''
		package controllers
		
		import org.qbproject.api.schema.QBSchema._
		import org.qbproject.api.mongo.MongoSchemaExtensions._
		import controllers.QBView._
		'''
	}
	
	def private String buildModelObject(EClass eClass){
		val requiredFeatures = eClass.EAllStructuralFeatures.filter[f | classMapper.isAllowed(f.EType) && f.lowerBound > 0];
		val optionalFeatures = eClass.EAllStructuralFeatures.filter[f | classMapper.isAllowed(f.EType) && f.lowerBound == 0];
		'''
		val modelSchema = qbClass(	
			"id" -> objectId,
			«FOR eStructuralFeature : requiredFeatures SEPARATOR ','»
				"«eStructuralFeature.name»" -> «classMapper.getQBName(eStructuralFeature.EType)»
			«ENDFOR»
			«IF requiredFeatures.size > 0 && optionalFeatures.size > 0»,«ENDIF»
			«FOR eStructuralFeature : optionalFeatures SEPARATOR ','»
				"«eStructuralFeature.name»" -> optional(«classMapper.getQBName(eStructuralFeature.EType)»)
			«ENDFOR»
		)
		'''
	}
	
	def private String buildDefaultViewModel(EClass eClass){
		'''
		val viewSchema = QBViewModel(	
			modelSchema,
			«FOR eStructuralFeature : eClass.EAllStructuralFeatures.filter[f | classMapper.isAllowed(f.EType)] SEPARATOR ','»
			QBViewControl("«eStructuralFeature.name»", QBViewPath("«eStructuralFeature.name»"))
			«ENDFOR»
		)
		'''
	}
}
