/*
TODO Move to jhipster folder
 - use webpack gulp integration to copy libs (see gulp/tasks/lib.js)
 */

(function () {
	'use strict';

	var fieldMapApp = angular.module('fieldMapApp', ['ngToast', 'ui.bootstrap']);

	fieldMapApp.config(['ngToastProvider', function (ngToastProvider) {
		ngToastProvider.configure({
			horizontalPosition: 'right',
			animation: 'fade'
		});
	}]);

	fieldMapApp.controller('MainController', ['$scope', 'ngToast', '$uibModal', '$http', '$timeout', function ($scope, ngToast, $uibModal, $http, $timeout) {

		const programUUID = getUrlParameter('programUUID'),
			studyId = getUrlParameter('studyId'),
			instanceId = getUrlParameter('instanceId'),
			cropName = getUrlParameter('cropName'),
			hasLayout = getUrlParameter('hasLayout') === "true",
			brapi_endpoint = '/bmsapi/' + cropName + '/brapi/v2';

		const fieldMap = new BrAPIFieldmap("#map", brapi_endpoint, {
			brapi_auth: JSON.parse(localStorage['bms.xAuthToken']).token
		});

		$scope.flags = {
			isUpdating: false,
			isEditMode: hasLayout
		}
		$scope.length = '';
		$scope.width = '';

		$scope.init = function () {
			fieldMap.setLocation(instanceId);
			if ($scope.flags.isEditMode) {
				$scope.load();
				fieldMap.removeControls();
			}
		};

		$scope.delete = function () {
			var modalInstance = $scope.openConfirmModal('Are you sure you want to delete this georeference?');
			modalInstance.result.then((isOK) => {
				if (isOK) {
					$scope.deleteGeoreference();
				}
			});
		}

		$scope.deleteGeoreference = function () {
			var req = {
				method: 'DELETE',
				url: '/bmsapi/crops/' + cropName + '/programs/' + programUUID + '/studies/' + studyId + '/instances/' + instanceId + '/georeferences',
				headers: {
					'x-auth-token': JSON.parse(localStorage['bms.xAuthToken']).token
				}
			};
			$http(req).then(() => {
				// Once georeference is deleted, we need to reload the page to reinitialize Fieldmap.
				// Since Georeference is already deleted we have to remove hasLayout query string if it exists.
				window.location.href = window.location.href.replace('&hasLayout=true', '');
			});
		}

		$scope.load = function () {
			fieldMap.opts.plotLength = $scope.length;
			fieldMap.opts.plotWidth = $scope.width;
			fieldMap.load(instanceId);
		}

		$scope.update = function () {
			if (hasLayout) {
				var modalInstance = $scope.openConfirmModal('You are going to override the existing layout. Do you like to proceed?');
				modalInstance.result.then((isOK) => {
					if (isOK) {
						$scope._update();
					}
				});
			} else {
				$scope._update();
			}
		};

		$scope._update = function () {
			$scope.flags.isUpdating = true;
			fieldMap.update().then(
				(resp) => {
					$timeout(function () {
						ngToast.success({
							content: resp
						});
						$scope.flags.isUpdating = false;
						$scope.flags.isEditMode = true;
						fieldMap.removeControls();
					});
				},
				(resp) => {
					$timeout(function () {
						ngToast.danger({
							content: resp
						});
						$scope.flags.isUpdating = false;
					});
				});
		}

		$scope.openConfirmModal = function (message) {
			var modalInstance = $uibModal.open({
				animation: true,
				template: '<div class="modal-body">\n' +
					'    <div class="row">\n' +
					'        <div class="col-xs-12 col-md-12">\n' +
					'            <label class="control-label">{{text}}</label>\n' +
					'        </div>\n' +
					'    </div>\n' +
					'</div>\n' +
					'<div class="modal-footer">\n' +
					'    <button class="btn btn-default" ng-click="cancel()">Cancel</button>\n' +
					'    <button class="btn btn-primary" ng-click="confirm()">OK</button>\n' +
					'</div>',
				controller: function ($scope, $uibModalInstance) {
					$scope.text = message;
					$scope.confirm = function () {
						$uibModalInstance.close(true);
					};

					$scope.cancel = function () {
						$uibModalInstance.close(false);
					};
				}
			});

			return modalInstance;
		}


		$scope.init();

	}]);

})();



