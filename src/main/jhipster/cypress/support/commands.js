// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })

import 'cypress-file-upload';

Cypress.Commands.add('login', () => {
	cy.visit('ibpworkbench/main/');

	cy.intercept('POST', '/ibpworkbench/controller/auth/validateLogin').as('doLogin');

	cy.get('.js-login-username').type(Cypress.env('username'));
	cy.get('.js-login-password').type(Cypress.env('password'));
	cy.get('.js-login-form').submit();

	cy.wait('@doLogin');
})

function getAccessToken() {
	const token = localStorage.getItem('bms.xAuthToken');
	return JSON.parse(token).token;
}

Cypress.Commands.add('getProgram', () => {

	cy.request({
		method: 'GET',
		url: `/bmsapi/${Cypress.env('cropName')}/brapi/v1/programs`,
		headers: {
			'X-Auth-Token': getAccessToken()
		}
	}).its('body').then((body) => {
		const program = body.result.data[0];
		Cypress.env('program', { uuid: program.programDbId, name: program.name });
	})

})
