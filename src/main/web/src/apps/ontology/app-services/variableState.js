/*global angular*/
'use strict';

(function() {
	var app = angular.module('variableState', ['properties', 'methods', 'scales']);

	app.service('variableStateService', ['$q', 'propertiesService', 'methodsService', 'scalesService',
		function($q, propertiesService, methodsService, scalesService) {

			return {

				variable: {},
				scopeData: {},
				editInProgress: false,

				storeVariableState: function(_variable, _scopeData) {
					this.variable = _variable || {};
					this.scopeData = _scopeData || {};
					this.editInProgress = true;
				},

				getVariableState: function() {
					return {
						variable: this.variable,
						scopeData: this.scopeData
					};
				},

				updateInProgress: function() {
					return this.editInProgress;
				},

				reset: function() {
					this.variable = {};
					this.scopeData = {};
					this.editInProgress = false;
				},

				/*
				This method will update the stored list of properties in scopeData, and sets the id of the selected property on
				the variables.

				@param propertyId the id of the property to select
				@returns a promise that will resolve when the properties and selected property id have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setProperty: function(propertyId) {

					var service = this;

					return $q(function(resolve, reject) {
						propertiesService.getProperties().then(function(properties) {
							service.scopeData.properties = properties;
							service.variable.property = propertyId;
							resolve();
						}, function(error) {
							reject(error);
						});
					});
				},

				/*
				This method will update the stored list of methods in scopeData, and sets the id of the selected method on
				the variables.

				@param methodId the id of the method to select
				@returns a promise that will resolve when the methods and selected method id have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setMethod: function(methodId) {

					var service = this;

					return $q(function(resolve, reject) {
						methodsService.getMethods().then(function(methods) {
							service.scopeData.methods = methods;
							service.variable.method = methodId;
							resolve();
						}, function(error) {
							reject(error);
						});
					});
				},

				/*
				This method will update the stored list of scales in scopeData, and sets the id of the selected scale on
				the variables.

				@param scaleId the id of the scale to select
				@returns a promise that will resolve when the scales and selected scale id have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setScale: function(scaleId) {

					var service = this;

					return $q(function(resolve, reject) {
						scalesService.getScales().then(function(scales) {
							service.scopeData.scales = scales;
							service.variable.scale = scaleId;
							resolve();
						}, function(error) {
							reject(error);
						});
					});
				}
			};
		}
	]);
}());
