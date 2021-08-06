describe('Germplasm Manager', () => {
	context('Import germplasm', () => {

		beforeEach(() => {
			cy.login();
			cy.getProgram();
		})

		it('Import germplasm', () => {
			const program = Cypress.env('program');

			cy.visit(`ibpworkbench/main/app/#/germplasm-manager/germplasm-search?cropName=${Cypress.env('cropName')}&programUUID=${program.uuid}`);

			cy.get('#actionMenu').click();
			cy.get('[jhitranslate="search-germplasm.actions.import"]').click().then(() => {
				cy.get('.modal-dialog').should('exist');
				cy.get('[jhitranslate="germplasm.import.header"] > span').contains('Import germplasm');
			});

			const fileName = 'GermplasmImport.xls';

			cy.fixture(fileName, 'binary')
				.then(Cypress.Blob.binaryStringToBlob)
				.then((fileContent) => {
					cy.get('#importFile').attachFile({
						fileContent,
						fileName,
						mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
						encoding:'utf8',
						lastModified: new Date().getTime()
					})
				})

			cy.wait(50);

			// TODO: use data-test=”unique_value” as selectors

			// Click next button to go to basic details modal
			cy.get('jhi-germplasm-import > .modal-footer > .btn-primary').click().then(() => {
				cy.get('jhi-germplasm-import-basic-details').should('exist');
			});

			// Click next button to go inventory modal
			cy.get('jhi-germplasm-import-basic-details > .modal-footer > .btn-primary').click().then(() => {
				cy.get('jhi-germplasm-import-inventory').should('exist');
			});

			// Click next button to go to import review modal
			cy.get('jhi-germplasm-import-inventory > .modal-footer > .btn-primary').click().then(() => {
				cy.get('jhi-germplasm-import-review').should('exist');
			});

			// Click save button
			cy.get('jhi-germplasm-import-review > .modal-footer > .btn-primary').click().then(() => {
				cy.get('jhi-modal-confirm').should('exist');
			});

			cy.intercept('POST', `bmsapi/crops/${Cypress.env('cropName')}/germplasm?programUUID=${program.uuid}`).as('importGermplasm');

			// Click confirm button of confirmation modal
			cy.get('.container > .modal-footer > .btn-primary').click().then(() => {
				cy.get('jhi-germplasm-list-creation').should('exist');
			})

			cy.wait('@importGermplasm').then((interception) => {
				expect(interception.response.statusCode).to.equal(200);
				cy.get('ngb-alert > span').contains('Germplasm imported successfully!');
			});
		})

	})

})
