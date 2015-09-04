package org.eclipse.emf.ecp.emf2web.json.generator.seed

import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo
import org.eclipse.emf.ecp.emf2web.exporter.SchemaWrapper

class SeedWrapper implements SchemaWrapper {

	override getName() {
		return "JavaScript Example";
	}

	override getFileExtension() {
		"js"
	}

	override wrap(String toWrap, String type) {
		switch (type) {
			case GenerationInfo.MODEL_TYPE:
				wrapModel(toWrap).toString
			case GenerationInfo.VIEW_TYPE:
				wrapView(toWrap).toString
			default:
				throw new IllegalArgumentException("Could not wrap: " + type)
		}

	}

	def wrapModel(String model) {
		'''
			'use strict';
			
			var app = angular.module('jsonforms-seed');
			app.factory('SchemaService', function() {
			    return {
			        schema: «model»
			    }
			});
		'''
	}

	def wrapView(String view) {
		'''
			'use strict';
			
			var app = angular.module('jsonforms-seed');
			app.service('UISchemaService', function() {
			
			    this.uiSchema = «view»;
			
			});
		'''
	}

}