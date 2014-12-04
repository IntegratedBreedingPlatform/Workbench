'use strict';

var chai = require('chai'),
	expect = chai.expect;

describe('mocha', function() {

	describe('chai', function() {

		it('should have a test', function() {
			expect(2).to.equal(2);
		});

		it('should have a second test', function() {
			expect(4).to.equal(4);
		});
	});

});
