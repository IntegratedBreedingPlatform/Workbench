/*global angular*/
'use strict';

(function() {
	var app = angular.module('variableState', ['properties', 'methods', 'scales', 'pascalprecht.translate']);

	app.service('variableStateService', ['$q', '$translate', 'propertiesService', 'methodsService', 'scalesService',
		function($q, $translate, propertiesService, methodsService, scalesService) {

			var variable = {},
				scopeData = {},
				errors = [],
				editInProgress = false;

			function handleFailedUpdate(reject, translationKey) {
				return function(error) {
					$translate(translationKey).then(function(message) {
						errors.push(message);
					});

					reject(error);
				};
			}

			return {

				storeVariableState: function(_variable, _scopeData) {
					variable = _variable || {};
					scopeData = _scopeData || {};
					editInProgress = true;
				},

				getVariableState: function() {
					return {
						variable: variable,
						scopeData: scopeData,
						errors: errors
					};
				},

				updateInProgress: function() {
					return editInProgress;
				},

				reset: function() {
					variable = {};
					scopeData = {};
					errors = [];
					editInProgress = false;
				},

				/*
				This method will update the stored list of properties in scopeData, and sets the property on the variable to
				contain the id and name of the selected property.

				@param propertyId the id of the selected property
				@param propertyName the name of the selected property
				@returns a promise that will resolve when the properties and property have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setProperty: function(propertyId, propertyName) {

					return $q(function(resolve, reject) {
						propertiesService.getProperties().then(function(properties) {
							var isPropertyFound = false;
							scopeData.properties = properties;

							properties.some(function(property) {
								if (property.id === propertyId && property.name === propertyName) {
									variable.property = {
										id: propertyId,
										name: propertyName
									};
									isPropertyFound = true;
								}
							});
							if (!isPropertyFound) {
								$translate('variableStateService.propertyNotFound').then(function(message) {
									errors.push(message);
								});
								reject();
							} else {
								resolve();
							}
						}, handleFailedUpdate(reject, 'variableStateService.couldNotSetProperty'));
					});
				},

				/*
				This method will update the stored list of methods in scopeData, and sets the method on the variable to
				contain the id and name of the selected method.

				@param methodId the id of the selected method
				@param methodName the name of the selected method
				@returns a promise that will resolve when the methods and method have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setMethod: function(methodId, methodName) {

					return $q(function(resolve, reject) {
						methodsService.getMethods().then(function(methods) {
							var isMethodFound = false;
							scopeData.methods = methods;

							methods.some(function(method) {
								if (method.id === methodId && method.name === methodName) {
									variable.method = {
										id: methodId,
										name: methodName
									};
									isMethodFound = true;
								}
							});
							if (!isMethodFound) {
								$translate('variableStateService.methodNotFound').then(function(message) {
									errors.push(message);
								});
								reject();
							} else {
								resolve();
							}
						}, handleFailedUpdate(reject, 'variableStateService.couldNotSetMethod'));
					});
				},

				/*
				This method will update the stored list of scales in scopeData, and sets the scale on the variable to
				contain the id and name of the selected scale.

				@param scaleId the id of the selected scale
				@returns a promise that will resolve when the scales and scale have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setScale: function(scaleId) {

					return $q(function(resolve, reject) {
						scalesService.getScalesWithNonSystemDataTypes().then(function(scales) {
							var isScaleFound = false;
							scopeData.scales = scales;

							scales.some(function(scale) {
								if (scale.id === scaleId) {
									variable.scale = scale;
									isScaleFound = true;
								}
							});
							if (!isScaleFound) {
								$translate('variableStateService.scaleNotFound').then(function(message) {
									errors.push(message);
								});
								reject();
							} else {
								resolve();
							}
						}, handleFailedUpdate(reject, 'variableStateService.couldNotSetScale'));
					});
				}
			};
		}
	]);
}());
