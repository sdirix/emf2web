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
package org.eclipse.emf.ecp.emf2web.export

import java.io.File
import java.util.ArrayList
import java.util.Comparator
import java.util.HashSet
import java.util.Set
import org.apache.commons.io.FileUtils
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecp.view.spi.group.model.VGroup
import org.eclipse.emf.ecp.view.spi.horizontal.model.VHorizontalLayout
import org.eclipse.emf.ecp.view.spi.label.model.VLabel
import org.eclipse.emf.ecp.view.spi.model.VControl
import org.eclipse.emf.ecp.view.spi.model.VView
import org.eclipse.emf.ecp.view.spi.vertical.model.VVerticalLayout

import static extension org.eclipse.emf.ecp.emf2web.export.ClassMapping.getQBName
import static extension org.eclipse.emf.ecp.emf2web.export.ClassMapping.isAllowed
import org.eclipse.emf.ecp.emf2web.generator.FormsScalaGenerator
import org.eclipse.emf.ecp.emf2web.generator.EcoreScalaGenerator

/** 
 * The class which handles the conversion from ecore files to qb files.
 * 
 * */
class Emf2QbExporter {
	var NameHelperImpl nameHelper = null;
	
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
	def public void export(Resource ecoreModel, Set<EClass> selectedClasses, Set<Resource> viewModels, File destinationDir){
		nameHelper = new NameHelperImpl()
		
		
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
	
	/**
	 * Returns the file for the qb application which defines the routes used in the web.
	 *
	 * @param selectedClasses
	 *            Collection of {@link EClass}es for which a route has to be defined.
	 * 
	 * @return the routes file which contains the routes for the given {@link EClass}es.
	 * */
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
	
	/** 
	 * The static head of the routes file. 
	 * 
	 * @return The static part of the routes file.
	 * */
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
	
	/** 
	 *  Generates the Scala file which links between the routes and the generated qb schema and view model files.
	 * */
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
	
	/** 
	 * The static head of the Scala QB controller file.
	 * */
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
	
	/**
	 * Generates the QB schema file for the given {@link EClass} and view models.
	 */
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
	
	/**
	 * Generates the QB view model object for the {@link EClass} and EMF Forms view models.
	 */
	def private String buildViewModelObject(EClass eClass, Set<Resource> viewModels){
		val generator = new FormsScalaGenerator(new NameHelperImpl)
		
		val viewModel = findViewModel(eClass, viewModels)
		if(viewModel == null){
			buildDefaultViewModel(eClass)
		}else{
			'''
			val viewSchema = «generator.generate(viewModel.contents.get(0) as VView, "modelSchema")»
			'''
		}
	}
	
	/** 
	 * Generates the QB view model for the {@link EClass} and EMF Forms view model.
	 * */
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
				QBViewControl("«nameHelper.getDisplayName(eClass,viewModelElement.domainModelReference.EStructuralFeatureIterator.next)»", QBViewPath("«viewModelElement.domainModelReference.EStructuralFeatureIterator.next.name»"))
				'''
			default: ""
		}
	}
	
	/**
	 * Selects the corresponding view model for the given {@link EClass} from a collection of view models.
	 * 
	 */
	def private Resource findViewModel(EClass eClass, Set<Resource> viewModels){
		viewModels.findFirst[viewResource |
			val root = viewResource.contents.get(0)
			if(root instanceof VView){
				return root.rootEClass == eClass
			}
			return false
		]
	} 
	
	/** 
	 * The static part of the Scala schema file.
	 * */
	def private String scalaIntro(){
		'''
		package controllers
		
		import org.qbproject.api.schema.QBSchema._
		import org.qbproject.api.mongo.MongoSchemaExtensions._
		import controllers.QBView._
		'''
	}
	
	/**
	 * Generates the QB schema object for the given {@link EClass}.
	 * 
	 */	
	def private String buildModelObject(EClass eClass){
		val generator = new EcoreScalaGenerator
		
		'''
		val modelSchema = «generator.generate(eClass)»
		'''
	}
	
	/** 
	 * Generates the default QB view model for the given {@link EClass}.
	 * 
	 * */
	def private String buildDefaultViewModel(EClass eClass){
		'''
		val viewSchema = QBViewModel(	
			modelSchema,
			«FOR eStructuralFeature : eClass.EAllStructuralFeatures.filter[f | f.EType.isAllowed] SEPARATOR ','»
			QBViewControl("«nameHelper.getDisplayName(eClass,eStructuralFeature)»", QBViewPath("«eStructuralFeature.name»"))
			«ENDFOR»
		)
		'''
	}
}
