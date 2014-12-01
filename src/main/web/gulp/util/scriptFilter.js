var path = require('path');

// Filters out non .js files. Prevents accidental inclusion of possible hidden files
module.exports = function(name) {
	'use strict';
	return /(\.(js)$)/i.test(path.extname(name));
};
