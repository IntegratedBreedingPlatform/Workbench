function getUrlParameter(name) {
	name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
	var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
	var results = regex.exec(location);// FIXME Improvement to search the parameter in the whole URL. problem-related to recover the parameter after the Hash.
	return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
};
