/*global browser*/
'use strict';

var chai = require('chai'),
	chaiAsPromised = require('chai-as-promised'),
	expect = chai.expect;

chai.use(chaiAsPromised);

describe('BMS Log In Page', function() {
	it('should have a title', function() {

		// Necessary if Angular is not present
		browser.ignoreSynchronization = true;
		browser.get('http://localhost:18080/ibpworkbench/controller/auth/login');

		expect(browser.getTitle()).to.eventually.equal('Log In to the BMS');
	});
});
