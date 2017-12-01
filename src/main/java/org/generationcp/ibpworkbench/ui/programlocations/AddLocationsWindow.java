/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.programlocations;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.actions.SaveNewLocationAction;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.form.AddLocationForm;

/**
 * @author Jeffrey Morales, Joyce Avestro
 * 
 */
public class AddLocationsWindow extends BaseSubWindow {

	private static final long serialVersionUID = 3983198771242295731L;

	private AddLocationForm addLocationForm;

	private Button cancelButton;

	private Button addLocationButton;

	private Component buttonArea;

	private VerticalLayout layout;

	private final ProgramLocationsPresenter programLocationsPresenter;

	public AddLocationsWindow(final ProgramLocationsPresenter programLocationsPresenter) {
		this.programLocationsPresenter = programLocationsPresenter;

		this.assemble();
	}

	protected void initializeComponents() {

		this.addLocationForm = new AddLocationForm(this.programLocationsPresenter, new LocationFormFieldFactory(this.programLocationsPresenter));
		this.addLocationForm.setDebugId("addLocationForm");
		this.buttonArea = this.layoutButtonArea();
	}

	protected void initializeLayout() {
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
		this.setWidth("600px");
		this.setResizable(false);
		this.center();
		this.setCaption("Add New Location");

		this.layout = new VerticalLayout();
		this.layout.setDebugId("AddLocationsWindow_layout");
		this.layout.setWidth("100%");
		this.layout.setHeight("450px");

		final Panel p = new Panel();
		p.setDebugId("AddLocationsWindow_p");
		p.setStyleName("form-panel");
		p.setSizeFull();

		final VerticalLayout vl = new VerticalLayout();
		vl.setDebugId("AddLocationsWindow_vl");
		vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>",
				Label.CONTENT_XHTML));
		vl.addComponent(this.addLocationForm);
		vl.setExpandRatio(this.addLocationForm, 1.0F);

		p.addComponent(vl);
		this.layout.addComponent(p);
		this.layout.addComponent(this.buttonArea);

		this.layout.setExpandRatio(p, 1.0F);
		this.layout.setComponentAlignment(this.buttonArea, Alignment.MIDDLE_CENTER);

		this.layout.setSpacing(true);
		this.layout.setMargin(true);

		this.setContent(this.layout);
	}

	protected void initializeActions() {

		this.addLocationButton.addListener(new SaveNewLocationAction(this.addLocationForm, this, this.programLocationsPresenter));
		this.cancelButton.addListener(new Button.ClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 6449306339821569356L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				AddLocationsWindow.this.getParent().removeWindow(AddLocationsWindow.this);
			}
		});

	}

	protected Component layoutButtonArea() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("addLocationForm_buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
		this.addLocationButton = new Button("Save");
		this.addLocationButton.setDebugId("addLocationButton");
		this.addLocationButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.addLocationButton);

		return buttonLayout;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

}
