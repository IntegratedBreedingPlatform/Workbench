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

package org.generationcp.ibpworkbench.ui.window;

import java.util.List;

import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Location;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author Jeffrey Morales, Joyce Avestro
 * 
 */
public class ConfirmLocationsWindow extends BaseSubWindow {

	private static final long serialVersionUID = 3983198771242295731L;

	private Label confirmMessage;

	private Button cancelButton;

	private Button okButton;

	private final ClickListener okButtonListener;

	private Component buttonArea;

	private VerticalLayout layout;

	private final Window window;

	private final List<Location> existingLocations;

	private Table locationsTable;

	private final ProgramLocationsPresenter programLocationsPresenter;

	public ConfirmLocationsWindow(Window window, List<Location> existingLocations, ProgramLocationsPresenter programLocationsPresenter,
			ClickListener okButtonListener) {
		this.window = window;
		this.existingLocations = existingLocations;
		this.programLocationsPresenter = programLocationsPresenter;
		this.okButtonListener = okButtonListener;

		this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.initialize();
	}

	public void show() {
		this.window.getParent().addWindow(this);
	}

	private void initialize() {
		/*
		 * Make the window modal, which will disable all other components while it is visible
		 */
		this.setModal(true);

		/* Make the sub window 50% the size of the browser window */
		this.setWidth("800px");
		/*
		 * Center the window both horizontally and vertically in the browser window
		 */
		this.center();

		this.assemble();

		this.setCaption("Confirm Location");

	}

	protected void initializeComponents() {
		this.layout = new VerticalLayout();
		this.setContent(this.layout);
		this.setParentWindow(this.window);

		this.confirmMessage = new Label();
		if (this.existingLocations.size() == 1) {
			this.confirmMessage.setCaption("There is already 1 location of the name you've specified:\n");
		} else {
			this.confirmMessage.setCaption("There are already " + this.existingLocations.size()
					+ " locations of the name you've specified:\n");
		}

		this.confirmMessage.setStyleName(Bootstrap.Typography.H3.styleName());

		this.layout.addComponent(this.confirmMessage);

		this.locationsTable = new Table();
		this.locationsTable.setMultiSelect(false);
		this.locationsTable.setSelectable(false);
		this.locationsTable.setImmediate(true);
		this.locationsTable.setWidth("100%");
		this.locationsTable.setColumnReorderingAllowed(true);
		BeanItemContainer<LocationViewModel> container = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		try {
			for (Location loc : this.existingLocations) {
				LocationViewModel l = this.programLocationsPresenter.getLocationDetailsByLocId(loc.getLocid());
				container.addItem(l);
			}
		} catch (MiddlewareQueryException e) {

			e.printStackTrace();
		}

		this.locationsTable.setContainerDataSource(container);
		this.layout.addComponent(this.locationsTable);
		this.locationsTable.setVisibleColumns(new String[] {"locationName", "cntryFullName", "locationAbbreviation", "ltypeStr"});
		this.locationsTable.setColumnHeaders(new String[] {"LOCATION NAME", "COUNTRY FULL NAME", "LOCATION ABBREVIATION", "LOCATION TYPE"});

		Label confirmMessage2 = new Label("Continue to save anyway?");
		this.layout.addComponent(confirmMessage2);

		this.cancelButton = new Button("Cancel");
		this.okButton = new Button("Save");
		this.okButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.buttonArea = this.layoutButtonArea();
		this.layout.addComponent(this.buttonArea);

	}

	protected void initializeLayout() {
		this.layout.setSpacing(true);
		this.layout.setMargin(true);
	}

	protected void initializeActions() {

		this.okButton.addListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				ConfirmLocationsWindow.this.window.getParent().removeWindow(ConfirmLocationsWindow.this);
				ConfirmLocationsWindow.this.okButtonListener.buttonClick(event);
			}

		});

		this.cancelButton.addListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmLocationsWindow.this.window.focus();
				ConfirmLocationsWindow.this.window.getParent().removeWindow(ConfirmLocationsWindow.this);
			}
		});

	}

	protected Component layoutButtonArea() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.okButton);

		return buttonLayout;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

}
