var currentData;

$(document).ready(function () {

	var run = false;
	$("#brapi-form").submit(function () {
		if (run) {
			$("#filtered_results").DataTable().destroy();
			$("#filtered_results").html("");
		}
		run = true;
		var form = $(this).serializeArray().reduce(function (vals, entry) {
			vals[entry.name] = entry.value;
			return vals
		}, {});
		var studyDbId = getUrlParameter("studyDbId");

		loadBrAPIData({
			studyDbIds: studyDbId ? [studyDbId] : [],
			locationDbIds: $('#locations select').val() || null,
			observationLevel: form.observationLevel || null,
			programDbIds: [getUrlParameter('programUuid')],
			trialDbIds: $('#trials select').val() || null,
			observationTimeStampRangeStart: form.observationTimeStampRangeStart || null,
			observationTimeStampRangeEnd: form.observationTimeStampRangeEnd || null,
			germplasmDbIds: form.germplasmDbIds ? form.germplasmDbIds.split(",") : []
		}).then(function (response) {
			useBrAPIData(response, (!!form.group));
		});

		return false;
	});
	loadLocations().then(function (response) {
		buildLocationsCombo(response);
	});
	loadObservationLevels().then(function (response) {
		buildObservationLevelsCombo(response);
	});
	loadTrials();
});

function loadLocations() {
	var url = "/bmsapi/" + getUrlParameter("crop") + "/brapi/v1/locations?pageSize=10000";

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

	var url = "/bmsapi/" + getUrlParameter("crop") + "/brapi/v1/observationLevels";

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
	var url = "/bmsapi/" + getUrlParameter("crop") + "/brapi/v1/trials"
		+ '?programDbId=' + getUrlParameter('programUuid');

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

function loadBrAPIData(parameters) {
	var load_url = "/bmsapi/" + getUrlParameter("crop") + "/brapi/v1/phenotypes-search";
	var data = {
		"pageSize": 1000,
		"page": 0
	};
	d3.entries(parameters).forEach(function (entry) {
		data[entry.key] = data[entry.key] || entry.value;
	});

	return $.ajax({
		type: "POST",
		dataType: "json",
		contentType: "application/json;charset=utf-8",
		url: load_url,
		beforeSend: beforeSend,
		data: JSON.stringify(data),
		error: error
	});
}

function tryParseInt(str, defaultValue) {
	var retValue = defaultValue;
	if (str !== null) {
		if (str.length > 0) {
			if (!isNaN(str)) {
				retValue = parseInt(str);
			}
		}
	}
	return retValue;
}

// filters and modifies the response and then creates the root filter object
// and datatable
function useBrAPIData(response, groupByAccession) {
	var traits = {};
	var data = response.result.data
		.map(function (observeUnit) {
			var newObj = {};
			d3.entries(observeUnit).forEach(function (entry) {
				if (entry.key != "observations") {
					newObj[entry.key] = entry.value;
				}
			});
			observeUnit.observations.forEach(function (obs) {
				newObj[obs.observationVariableName] = tryParseInt(obs.value, obs.value);
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

	currentData = data;

	// create the root filter object and datatable
	var gfilter = GraphicalFilter();
	gfilter.create("#filter_div", "#filtered_results", data, tableCols, trait_names);


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

var mainApp = angular.module('mainApp', ['loadingStatus']);

mainApp.controller('MainController', function MainController($scope, $http, $q) {

	$scope.errorMessage = '';
	$scope.rCallObjects = [];
	$scope.selectedRCallObject;

	$scope.onExportClick = function () {
		$scope.errorMessage = '';
		if (currentData) {
			transform(angular.copy($scope.selectedRCallObject), currentData);
		}
	};

	$scope.loadRCallsObjects = function () {
		$http({
			method: 'GET',
			url: '/bmsapi/rpackage/rcalls',
			headers: {'x-auth-token': JSON.parse(localStorage["bms.xAuthToken"]).token}
		}).success(function (data) {
			$scope.rCallObjects = data;
			$scope.selectedRCallObject = $scope.rCallObjects[0];
		});
	};

	$scope.loadRCallsObjects();

	function transform(rObject, data) {
		var meltVariables = 'c(\"instanceNumber\",\"blockNumber\",\"entryNumber\",\"entryType\",\"germplasmDbId\",\"germplasmName\",\"observationLevel\",\"observationLevels\",\"observationUnitDbId\",\"observationUnitName\",\"plantNumber\",\"plotNumber\",\"programName\",\"replicate\",\"studyDbId\",\"studyLocation\",\"studyLocationDbId\",\"studyName\",\"x\",\"y\")';
		var meltParams = {id: meltVariables, data: JSON.stringify(currentData)};
		// melt the data first before transforming
		executeOpenCPU('http://public.opencpu.org/ocpu/library/reshape/R/melt/json', meltParams).then(function (moltenData) {
			rObject.parameters.data = JSON.stringify(moltenData);
			// transform the molten data through R cast function
			return executeOpenCPU(rObject.endpoint + '/csv', rObject.parameters);
		}).then(function (result) {
			// download the transformed data as CSV.
			download(result);
		});
	}

	function executeOpenCPU(url, parameters) {
		var deferred = $q.defer();
		// perform the request
		$http({
			method: 'POST',
			url: url,
			data: $.param(parameters),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function (data) {
			deferred.resolve(data);
		}).error(function (message) {
			$scope.errorMessage = message;
		});
		return deferred.promise;
	}

	function download(data) {
		var link = window.document.createElement('a');
		var blob = new Blob([data]);
		link.href = window.URL.createObjectURL(blob);
		link.download = 'datafile.csv';
		link.click();
	}
});

angular.module('loadingStatus', []).config(function ($httpProvider) {
	$httpProvider.interceptors.push('loadingStatusInterceptor');
}).directive('loadingStatusMessage', function () {
	return {
		link: function ($scope, $element, attrs) {
			var show = function () {
				$element.css('display', 'block');
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