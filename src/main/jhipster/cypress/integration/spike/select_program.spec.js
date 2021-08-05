describe('Programs', () => {

	const cropName = Cypress.config('cropName');

	const getProgramsIframeDocument = () => {
		return cy.get('#programsFrame').its('0.contentDocument').should('exist');
	}

	const getProgramsIframeBody = () => {
		return getProgramsIframeDocument().its('body').should('not.be.undefined').then(cy.wrap);
	}

	beforeEach(() => {
		cy.login();
		cy.getProgram();
	})

	it('Select program', () => {
		const program = Cypress.env('program');

		cy.visit('ibpworkbench/main/app/#/');

		getProgramsIframeBody().find('#cropDropdown select').should('exist').select(cropName, { force : true });
		getProgramsIframeBody().find('#cropDropdown select').should('have.value', cropName);

		getProgramsIframeBody().find('#programDropdown select').should('exist').select(program.name, { force : true });
		getProgramsIframeBody().find('#programDropdown select').should('have.value', program.uuid);

		getProgramsIframeBody().find('#launchButton').should('exist').click().then(() => {
			cy.get('#programName').contains(program.name);
		});
	})

})
