/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.breeding.manager.listmanager;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.listmanager.api.AddColumnSource;
import org.generationcp.breeding.manager.listmanager.listeners.FillWithAttributeButtonClickListener;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchService;
import org.generationcp.middleware.domain.ontology.Variable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * This class opens a pop-up window for selecting attribute types available for
 * all GIDs of source screen. It will proceed to fill to add selected attribute
 * type as column to source table, if no existing propery specified, and fill up
 * attribute values for chosen attribute type per germplasm on target table.
 *
 */

@Configurable
public class FillWithAttributeWindow extends BaseSubWindow
		implements InternationalizableComponent, InitializingBean, BreedingManagerLayout {

	private static final long serialVersionUID = -8850686249688989080L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private AddColumnSource addColumnSource;
	private final String targetPropertyId;
	private HorizontalLayout attributeLayout;
	private ComboBox attributeBox;
	private Button okButton;
	private List<Variable> attributeVariables;
	private final boolean isFromGermplasmSearchWindow;

	@Autowired
	private GermplasmSearchService germplasmSearchService;

	@Autowired
	private GermplasmService germplasmService;

	public FillWithAttributeWindow(final AddColumnSource addColumnSource, final String targetPropertyId,
			final boolean isFromGermplasmSearchWindow) {
		this.addColumnSource = addColumnSource;
		this.targetPropertyId = targetPropertyId;
		this.isFromGermplasmSearchWindow = isFromGermplasmSearchWindow;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	@Override
	public void instantiateComponents() {
		this.attributeBox = new ComboBox();
		this.attributeBox.setDebugId("attributeBox");
		this.attributeBox.setNullSelectionAllowed(false);
		this.okButton = new Button();
		this.okButton.setDebugId("okButton");
	}

	@Override
	public void initializeValues() {
		this.attributeVariables = this.germplasmService.getGermplasmAttributeVariables(this.addColumnSource.getAllGids(), null);
		for (final Variable variable : this.attributeVariables) {
			if (!this.addColumnSource.columnExists(variable.getName().toUpperCase())) {
				this.attributeBox.addItem(variable.getId());
				this.attributeBox.setItemCaption(variable.getId(), variable.getName());
			}
		}
	}

	@Override
	public void addListeners() {
		this.okButton.addListener(new FillWithAttributeButtonClickListener(this.addColumnSource, this.attributeBox,
				this.targetPropertyId, this.isFromGermplasmSearchWindow));
	}

	@Override
	public void layoutComponents() {
		this.attributeBox.setWidth("300px");

		this.attributeLayout = new HorizontalLayout();
		this.attributeLayout.setDebugId("attributeLayout");
		this.attributeLayout.setMargin(true);
		this.attributeLayout.setSpacing(true);

		this.attributeLayout.addComponent(this.attributeBox);
		this.attributeLayout.addComponent(this.okButton);

		// set window properties
		this.setContent(this.attributeLayout);
		this.setWidth("400px");
		this.setHeight("30px");
		this.center();
		this.setResizable(false);
		this.setModal(true);
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this, Message.FILL_WITH_ATTRIBUTE_WINDOW);
		this.messageSource.setCaption(this.okButton, Message.OK);
	}

	public AddColumnSource getAddColumnSource() {
		return this.addColumnSource;
	}

	public ComboBox getAttributeBox() {
		return this.attributeBox;
	}

	public Button getOkButton() {
		return this.okButton;
	}
}
