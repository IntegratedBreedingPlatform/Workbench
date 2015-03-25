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
				This method will update the stored list of properties in scopeData, and sets the propertySummary on the variable to
				contain the id and name of the selected property.

				@param propertyId the id of the selected property
				@param propertyName the name of the selected property
				@returns a promise that will resolve when the properties and property summary have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setProperty: function(propertyId, propertyName) {
					var service = this;

					return $q(function(resolve, reject) {
						propertiesService.getProperties().then(function(properties) {
							service.scopeData.properties = properties;

							service.variable.propertySummary = {
								id: propertyId,
								name: propertyName
							};

							resolve();
						}, function(error) {
							reject(error);
						});
					});
				},

				/*
				This method will update the stored list of methods in scopeData, and sets the methodSummary on the variable to
				contain the id and name of the selected method.

				@param methodId the id of the selected method
				@param methodName the name of the selected method
				@returns a promise that will resolve when the methods and method summary have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setMethod: function(methodId, methodName) {
					var service = this;

					return $q(function(resolve, reject) {
						methodsService.getMethods().then(function(methods) {
							service.scopeData.methods = methods;

							service.variable.methodSummary = {
								id: methodId,
								name: methodName
							};

							resolve();
						}, function(error) {
							reject(error);
						});
					});
				},

				/*
				This method will update the stored list of scales in scopeData, and sets the scaleSummary on the variable to
				contain the id and name of the selected scale.

				@param scaleId the id of the selected scale
				@returns a promise that will resolve when the scales and scale summary have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setScale: function(scaleId) {
					var service = this;

					return $q(function(resolve, reject) {
						scalesService.getScales().then(function(scales) {
							service.scopeData.scales = scales;

							scales.some(function(scale) {
								if (scale.id === scaleId) {
									service.variable.scale = scale;
								}
							});

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
