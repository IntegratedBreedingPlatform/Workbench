/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * A Window that shows a web page in an embedded iframe.
 * 
 * Usage: ExternalWebToolWindow window = new
 * ExternalWebToolWindow("Germplasm Browser",
 * "http://localhost:8081/GermplasmBrowser/"); window.setWidth("800px");
 * window.setHeight("600px");
 * 
 * @author Glenn Marintes
 */
public class ExternalWebToolWindow extends Window{

    private static final long serialVersionUID = 1L;

    private String url;

    private Embedded browser;

    public ExternalWebToolWindow(String caption, String url) {
        super(caption);

        this.url = url;

        assemble();
    }

    protected void initializeComponents() {
        browser = new Embedded("", new ExternalResource(url));
        browser.setType(Embedded.TYPE_BROWSER);
    }

    protected void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        browser.setSizeFull();
        layout.addComponent(browser);
        layout.setExpandRatio(browser, 1.0f);

        setContent(layout);
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
    }
}
