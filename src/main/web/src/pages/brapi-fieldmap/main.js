/*
TODO Move to jhipster folder
 - use webpack gulp integration to copy libs (see gulp/tasks/lib.js)
 */

var instanceId = getUrlParameter('instanceId');
var cropName = getUrlParameter('cropName');
const brapi_endpoint = '/bmsapi/' + cropName + '/brapi/v2';

var fieldMap = new BrAPIFieldmap("#map", brapi_endpoint, {
	brapi_auth: JSON.parse(localStorage['bms.xAuthToken']).token
});
fieldMap.setLocation(instanceId);

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
