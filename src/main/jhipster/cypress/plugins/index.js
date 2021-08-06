/// <reference types="cypress" />
// ***********************************************************
// This example plugins/index.js can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************

// This function is called when a project is opened or re-opened (e.g. due to
// the project's config changing)

/**
 * @type {Cypress.PluginConfig}
 */
const PropertiesReader = require('properties-reader');
const appProperties = PropertiesReader('../../../target/test-classes/test.properties');

module.exports = (on, config) => {

	// If the baseUrl is already defined it is because was passed as a parameter executing cypress by command line
	if (!config.baseUrl) {
		config.baseUrl = appProperties.get('test.e2e.base.url');
	}

	config.env.cropName = appProperties.get('test.e2e.crop');
	config.env.username = appProperties.get('test.e2e.username');
	config.env.password = appProperties.get('test.e2e.password');

	return config;
}
