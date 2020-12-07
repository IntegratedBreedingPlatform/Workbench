/*
TODO Move to jhipster folder
 - use webpack gulp integration to copy libs (see gulp/tasks/lib.js)
 */

(function () {
	'use strict';

	var fieldMapApp = angular.module('fieldMapApp', ['ui.bootstrap']);

	fieldMapApp.controller('MainController', ['$scope', '$uibModal', '$http', function ($scope, $uibModal, $http) {

		const instanceId = getUrlParameter('instanceId'),
			cropName = getUrlParameter('cropName'),
			hasLayout = getUrlParameter('hasLayout') === "true",
			brapi_endpoint = '/bmsapi/' + cropName + '/brapi/v2';

		const fieldMap = new BrAPIFieldmap("#map", brapi_endpoint, {
			brapi_auth: JSON.parse(localStorage['bms.xAuthToken']).token
		});

		$scope.editMode = false;
		$scope.length = '';
		$scope.width = '';

		$scope.init = function () {
			fieldMap.setLocation(instanceId);
			if (hasLayout) {
				$scope.load();
			} else {
				$scope.editMode = true;
			}
		};


		$scope.load = function () {
			fieldMap.opts.plotLength = $scope.length;
			fieldMap.opts.plotWidth = $scope.width;
			fieldMap.load(instanceId);
		}

		$scope.update = function () {
			if (hasLayout) {
				var modalInstance = $scope.openConfirmModal('You are going to override the existing layout');
				modalInstance.result.then((isOK) => {
					if (isOK) {
						fieldMap.update().then(
							// TODO toast
							(resp) => alert(resp),
							(resp) => alert(resp));
					}
				});
			} else {
				fieldMap.update().then(
					// TODO toast
					(resp) => alert(resp),
					(resp) => alert(resp));
			}
		};

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



