/*
TODO Move to jhipster folder
 - use webpack gulp integration to copy libs (see gulp/tasks/lib.js)
 */

const instanceId = getUrlParameter('instanceId'),
	cropName = getUrlParameter('cropName'),
	hasLayout = getUrlParameter('hasLayout') === "true",
	brapi_endpoint = '/bmsapi/' + cropName + '/brapi/v2';

const fieldMap = new BrAPIFieldmap("#map", brapi_endpoint, {
	brapi_auth: JSON.parse(localStorage['bms.xAuthToken']).token
});

fieldMap.setLocation(instanceId);

if (hasLayout) {
	load();
} else {
	$('#load').show();
}

// Functions

function load() {
	fieldMap.opts.plotLength = d3.select('#length').node().value;
	fieldMap.opts.plotWidth = d3.select('#width').node().value;
	fieldMap.load(instanceId);
}

function update() {
	fieldMap.update().then(
		(resp) => alert(resp),
		(resp) => alert(resp));
}
