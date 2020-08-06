// FIXME: Refactor these to Angular.
$(document).ready(function () {
	loadLocations().then(function (response) {
		buildLocationsCombo(response);
	});
	loadObservationLevels().then(function (response) {
		buildObservationLevelsCombo(response);
	});
	loadTrials();
});

function loadLocations() {
	var url = "/bmsapi/" + getUrlParameter("cropName") + "/brapi/v1/locations?pageSize=10000";

	return $.get({
		dataType: "json",
		contentType: "application/json;charset=utf-8",
		url: url,
		beforeSend: beforeSend,
		error: error
	});
}

function buildLocationsCombo(response) {
	if (!response
		|| !response.result
		|| !response.result.data) {
		return;
	}

	$('#locations').html('<select multiple ></select>');
	$('#locations select').append(response.result.data.map(function (location) {
		return '<option value="' + location.locationDbId + '">'
			+ location.name + ' - (' + location.abbreviation + ')'
			+ '</option>';
	}));
	$('#locations select').select2({containerCss: {width: '100%'}});
}


function loadObservationLevels() {

	var url = "/bmsapi/" + getUrlParameter("cropName") + "/brapi/v1/observationLevels";

	return $.get({
		dataType: "json",
		contentType: "application/json;charset=utf-8",
		url: url,
		beforeSend: beforeSend,
		error: error
	});
}


function buildObservationLevelsCombo(response) {
	if (!response
		|| !response.result
		|| !response.result.data) {
		return;
	}

	$('#observationLevels').html('<select class="form-control" name="observationLevel"></select>');
	$('#observationLevels select').append(response.result.data.map(function (observationLevel) {
		return '<option value="' + observationLevel + '">' + observationLevel + '</option>';
	}));
}

function loadTrials() {
	var url = "/bmsapi/" + getUrlParameter("cropName") + "/brapi/v1/trials"
		+ '?programDbId=' + getUrlParameter('programUUID');

	$.get({
		dataType: "json",
		contentType: "application/json;charset=utf-8",
		url: url,
		beforeSend: beforeSend,
		error: error
	}).then(function (response) {
		if (!response
			|| !response.result
			|| !response.result.data) {
			return;
		}

		$('#trials').html('<select multiple ></select>');
		$('#trials select').append(response.result.data.map(function (trial) {
			return '<option value="' + trial.trialDbId + '">' + trial.trialName + '</option>';
		}));
		$('#trials select').select2({containerCss: {width: '100%'}});
	});
}

function beforeSend(xhr) {
	var xAuthToken = JSON.parse(localStorage["bms.xAuthToken"]).token;
	xhr.setRequestHeader('Authorization', "Bearer " + xAuthToken);
}

function error(data) {
	if (data.status === 401) {
		// TODO BMS-4743
		alert('Breeding Management System needs to authenticate you again. Redirecting to login page.');
		window.top.location.href = '/ibpworkbench/logout';
	}
}


// Angular ****************************

var mainApp = angular.module('mainApp', ['loadingStatus', 'ui.bootstrap']);

mainApp.controller('MainController', ['$scope', '$uibModal', '$http', function ($scope, $uibModal, $http) {

	$scope.flags = {
		groupByAccession: false,
		isDataLoaded: false
	};

	$scope.tools = [
		{id: 'graphical-filtering', name: 'Graphical Filtering'},
		{id: 'study-comparison', name: 'Study Comparison'}
	];
	$scope.toolId = $scope.tools[0].id;

	$scope.filteredDataResult = [];

	$scope.loadData = function () {

		var form = $("#brapi-form").serializeArray().reduce(function (vals, entry) {
			vals[entry.name] = entry.value;
			return vals
		}, {});
		var studyDbId = getUrlParameter("studyDbId");

		const phenotypesSearchPromise = $scope.phenotypesSearch({
			studyDbIds: studyDbId ? [studyDbId] : [],
			locationDbIds: $('#locations select').val() || null,
			observationLevel: form.observationLevel || null,
			programDbIds: [getUrlParameter('programUUID')],
			trialDbIds: $('#trials select').val() || null,
			observationTimeStampRangeStart: form.observationTimeStampRangeStart || null,
			observationTimeStampRangeEnd: form.observationTimeStampRangeEnd || null,
			germplasmDbIds: form.germplasmDbIds ? form.germplasmDbIds.split(",") : [],
			pageSize: form.pageSize,
			page: 0
		});

		switch ($scope.toolId) {
			case 'graphical-filtering':
				phenotypesSearchPromise.then(function (response) {

					if ($.fn.DataTable.isDataTable("#filtered_results")) {
						$("#filtered_results").DataTable().destroy();
						$("#filtered_results").html("");
					}

					$scope.flags.isDataLoaded = response.data.result.data.length > 0;
					$scope.processGraphicalFilterBrAPIData(response.data.result.data, (!!form.group));
				});
				break;
			case 'study-comparison':
				phenotypesSearchPromise.then(function (response) {
					$scope.flags.isDataLoaded = response.data.result.data.length > 0;
					$scope.createStudyComparison(response.data.result.data);
				});
				break;
		}

	};

	$scope.$watch('flags.groupByAccession', function (newValue, oldValue) {
		if (newValue !== oldValue && !newValue) {
			// reload the table if the groupByAccession is unchecked.
			$scope.loadData();
		}
	});

	$scope.openExportModal = function () {
		$uibModal.open({
			templateUrl: 'pages/BrAPI-Graphical-Queries/exportModal.html',
			controller: 'ExportModalController',
			resolve: {
				filteredDataResult: function () {
					return $scope.filteredDataResult;
				}
			}
		});
	};

	// filters and modifies the response and then creates the root filter object
	// and datatable
	$scope.processGraphicalFilterBrAPIData = function (responseData, groupByAccession) {
		var traits = {};
		var data = responseData.map(function (observeUnit) {
			var newObj = {};
			d3.entries(observeUnit).forEach(function (entry) {
				if (entry.key != "observations") {
					newObj[entry.key] = entry.value;
				}
			});
			observeUnit.observations.forEach(function (obs) {
				newObj[obs.observationVariableName] = tryParseNumber(obs.value);
				traits[obs.observationVariableName] = true;
			});
			return newObj;
		});

		var trait_names = d3.keys(traits);
		data.forEach(function (datum) {
			trait_names.forEach(function (trait) {
				if (datum[trait] == undefined || datum[trait] == null || datum[trait] == NaN) {
					datum[trait] = null
				}
			})
		});
		var tableCols = [
			{title: "TrialInstance", data: "instanceNumber"},
			{title: "StudyDbId", data: "studyDbId"},
			{title: "Study", data: "studyName"},
			{title: "Name", data: "observationUnitName"},
			{title: "observationUnitDbId", data: "observationUnitDbId"},
			{title: "Accession", data: "germplasmName"},
			{title: "EntryNumber", data: "entryNumber"},
			{title: "GID", data: "germplasmDbId"},
			{title: "Location", data: "studyLocation"},
		];
		if (groupByAccession) {
			var grouped = d3.nest().key(function (d) {
				return d.germplasmDbId
			}).entries(data);
			var newdata = grouped.map(function (group) {
				var newDatum = {};
				newDatum.germplasmName = group.values[0].germplasmName;
				newDatum.germplasmDbId = group.key;
				newDatum.count = group.values.length;
				newDatum.group = group.values;
				trait_names.forEach(function (trait_key) {
					var avg = d3.mean(group.values, function (d) {
						if (d[trait_key] !== null) {
							return d[trait_key];
						}
					});
					newDatum[trait_key] = avg == undefined ? null : avg;
				});
				return newDatum;
			});
			var tableCols = [
				{title: "Accession", data: "germplasmName"},
				{title: "Unit Count", data: "count"}
			];
			data = newdata;
		}


		// use the list of shared traits to create dataTables columns
		tableCols = tableCols.concat(trait_names.map(function (d) {
			return {title: d, data: d.replace(/\./g, "\\.")};
		}));

		// create the root filter object and datatable
		var gfilter = GraphicalFilter();
		gfilter.create("#filter_div", "#filtered_results", data, tableCols, trait_names);

		// Store the filtered data so we can transform and send it to OpenCPU api later.
		$scope.filteredDataResult = gfilter.filteredData;
	};

	$scope.createStudyComparison = function (data) {

		var scomp = StudyComparison().links(function(dbId){
			return '/ibpworkbench/maingpsb/germplasm-' + dbId;
		});
		var sharedVars = scomp.loadData(data);

		var varOpts = d3.select("#scomp-select-var")
			.selectAll("option:not([disabled])")
			.data(sharedVars);
		varOpts.exit().remove();
		varOpts.enter().append("option").merge(varOpts)
			.attr("value",function(d){return d})
			.text(function(d){return d})
			.raise();

		$("#graph_div").html("");
		$("#hist_div").html("");

		$("#scomp-form").click(function(){
			scomp.setVariable($("#scomp-select-var").val());
			scomp.graphGrid("#graph_div");
			scomp.multiHist("#hist_div");
		});
	};

	$scope.phenotypesSearch = function (data) {
		return $http({
			method: 'POST',
			url: "/bmsapi/" + getUrlParameter("cropName") + "/brapi/v1/phenotypes-search",
			headers: {'x-auth-token': JSON.parse(localStorage["bms.xAuthToken"]).token},
			data: data
		});
	}

	function tryParseNumber(str) {
		var retValue = null;
		if (str && !isNaN(str)) {
			return Number(str);
		}
		return retValue;
	}

}]);

mainApp.controller('ExportModalController', ['$scope', '$q', '$uibModalInstance', 'rCallService', 'filteredDataResult',
	function ($scope, $q, $uibModalInstance, rCallService, filteredDataResult) {

		$scope.errorMessage = '';
		$scope.rCallObjects = [];
		$scope.selectedRCallObject = {};
		$scope.meltRCallObject = {};
		$scope.isExporting = false;

		$scope.proceed = function () {
			$scope.errorMessage = '';
			transform(angular.copy($scope.selectedRCallObject), filteredDataResult);
		};

		$scope.cancel = function () {
			$uibModalInstance.close();
		};

		$scope.loadRCallsObjects = function () {
			var castPackageId = 1;
			rCallService.getRCallsObjects(castPackageId).success(function (data) {
				$scope.rCallObjects = data;
				$scope.selectedRCallObject = $scope.rCallObjects[0];
			});
		};

		$scope.retrieveMeltRCallObject = function () {
			var meltPackageId = 2;
			rCallService.getRCallsObjects(meltPackageId).success(function (data) {
				$scope.meltRCallObject = data[0];
			});
		};

		$scope.init = function () {
			$scope.loadRCallsObjects();
			$scope.retrieveMeltRCallObject();
		};

		$scope.init();

		function transform(rObject, data) {
			$scope.isExporting = true;
			$scope.meltRCallObject.parameters.data = JSON.stringify(data);
			// melt the data first before transforming
			rCallService.executeRCallAsJSON($scope.meltRCallObject.endpoint, $scope.meltRCallObject.parameters).then(function (response) {
				rObject.parameters.data = JSON.stringify(response.data);
				// transform the molten data through R cast function
				return rCallService.executeRCallAsCSV(rObject.endpoint, rObject.parameters);
			}).then(function (response) {
				// download the transformed data as CSV.
				download(response.data);
				$uibModalInstance.close();
				$scope.isExporting = false;
			}).catch(function (errorResponse) {
				$scope.errorMessage = 'An error occurred while connecting to OpenCPU API. ' + errorResponse.data;
			});
		}

		function download(data) {
			var link = window.document.createElement('a');
			var blob = new Blob([data]);
			link.href = window.URL.createObjectURL(blob);
			link.download = 'datafile.csv';
			link.click();
		}

	}]);

mainApp.factory('rCallService', ['$http', function ($http) {

	var rCallService = {};

	rCallService.getRCallsObjects = function (packageId) {
		return $http({
			method: 'GET',
			url: '/bmsapi/r-packages/' + +packageId + '/r-calls',
			headers: {'x-auth-token': JSON.parse(localStorage["bms.xAuthToken"]).token}
		});
	};

	rCallService.executeRCallAsCSV = function (url, parameters) {
		return $http({
			method: 'POST',
			url: url + '/csv',
			data: $.param(parameters),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		});
	}

	rCallService.executeRCallAsJSON = function (url, parameters) {
		return $http({
			method: 'POST',
			url: url + '/json',
			data: $.param(parameters),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		});
	}

	return rCallService;


}]);

angular.module('loadingStatus', []).config(function ($httpProvider) {
	$httpProvider.interceptors.push('loadingStatusInterceptor');
}).directive('loadingStatusMessage', function () {
	return {
		link: function ($scope, $element, attrs) {
			var show = function () {
				$element.css('display', 'inline-block');
			};
			var hide = function () {
				$element.css('display', 'none');
			};
			$scope.$on('loadingStatusActive', show);
			$scope.$on('loadingStatusInactive', hide);
			hide();
		}
	};
}).factory('loadingStatusInterceptor', function ($q, $rootScope) {
	var activeRequests = 0;
	var started = function () {
		if (activeRequests == 0) {
			$rootScope.$broadcast('loadingStatusActive');
		}
		activeRequests++;
	};
	var ended = function () {
		activeRequests--;
		if (activeRequests == 0) {
			$rootScope.$broadcast('loadingStatusInactive');
		}
	};
	return {
		request: function (config) {
			started();
			return config || $q.when(config);
		},
		response: function (response) {
			ended();
			return response || $q.when(response);
		},
		responseError: function (rejection) {
			ended();
			return $q.reject(rejection);
		}
	};
});
