describe('Login', () => {

	it('Login success', () => {
		cy.login();
		cy.url({ log: true }).should('include', '/app', () => {
			expect(localStorage.getItem('bms.xAuthToken')).to.exist()
		})
		cy.getCookie('BMS_TOK').should('exist');
	})

})
