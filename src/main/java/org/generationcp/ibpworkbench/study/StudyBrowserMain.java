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

package org.generationcp.ibpworkbench.study;

import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.tree.BrowseStudyTreeComponent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import javax.annotation.Resource;

@Configurable
public class StudyBrowserMain extends VerticalLayout implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;

	private HorizontalLayout titleLayout;
	private Label toolTitle;
	private Label headingLabel;
	private Button browseForStudy;
	private Label or;
	private Button searchForStudy;
	private Label browseStudyDescriptionLabel;

	private BrowseStudyTreeComponent browseTreeComponent;
	private StudySearchMainComponent searchStudyComponent;

	private StudyBrowserMainLayout mainLayout;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public static final ThemeResource STUDY_DETAILS_ICON = new ThemeResource("images/study-details.png");

	public StudyBrowserMain() {
	}

	@Override
	public void afterPropertiesSet() {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	@Override
	public void instantiateComponents() {

		this.setTitleContent();

		this.headingLabel = new Label(this.messageSource.getMessage(Message.STUDIES));
		this.headingLabel.setImmediate(true);
		this.headingLabel.setWidth("300px");
		this.headingLabel.setHeight("30px");
		this.headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		this.headingLabel.addStyleName("bold");

		this.browseForStudy = new Button();
		this.browseForStudy.setImmediate(true);
		this.browseForStudy.setStyleName(BaseTheme.BUTTON_LINK);
		this.browseForStudy.setWidth("45px");

		this.or = new Label("or");

		this.searchForStudy = new Button();
		this.searchForStudy.setImmediate(true);
		this.searchForStudy.setStyleName(BaseTheme.BUTTON_LINK);
		this.searchForStudy.setWidth("40px");

		this.browseStudyDescriptionLabel = new Label("for a study to work with.");

		this.mainLayout = new StudyBrowserMainLayout(this);

		this.browseTreeComponent = new BrowseStudyTreeComponent(this);
		this.searchStudyComponent = new StudySearchMainComponent(this);
	}

	private void setTitleContent() {
		this.titleLayout = new HorizontalLayout();
		this.titleLayout.setSpacing(true);
		this.titleLayout.setHeight("40px");

		this.toolTitle = new Label(this.messageSource.getMessage(Message.BROWSE_STUDIES));
		this.toolTitle.setDebugId("toolTitle");
		this.toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		this.toolTitle.setContentMode(Label.CONTENT_XHTML);
		this.toolTitle.setWidth("220px");

		this.titleLayout.addComponent(this.toolTitle);
		this.titleLayout.addComponent(new HelpButton(HelpModule.BROWSE_STUDIES, "View Browse Studies Tutorial"));
	}

	@Override
	public void initializeValues() {
		this.browseForStudy.setCaption(this.messageSource.getMessage(Message.BROWSE_LABEL) + " ");
		this.searchForStudy.setCaption((this.messageSource.getMessage(Message.SEARCH_LABEL) + " ").toLowerCase());
	}

	@Override
	public void addListeners() {
		this.browseForStudy.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				StudyBrowserMain.this.openBrowseForStudyWindow();
			}
		});

		this.searchForStudy.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				StudyBrowserMain.this.openSearchForStudyWindow();
			}
		});
	}

	@Override
	public void layoutComponents() {
		this.setSpacing(true);
		this.setMargin(false, true, true, true);

		final HeaderLabelLayout headingLayout = new HeaderLabelLayout(StudyBrowserMain.STUDY_DETAILS_ICON, this.headingLabel);

		HorizontalLayout directionLayout = new HorizontalLayout();
		directionLayout.addStyleName("study-browser-main");
		directionLayout.setHeight("17px");
		directionLayout.setSpacing(true);
		directionLayout.addComponent(this.browseForStudy);
		directionLayout.addComponent(this.or);
		directionLayout.addComponent(this.searchForStudy);
		directionLayout.addComponent(this.browseStudyDescriptionLabel);
		directionLayout.setComponentAlignment(this.browseForStudy, Alignment.BOTTOM_CENTER);
		directionLayout.setComponentAlignment(this.searchForStudy, Alignment.BOTTOM_CENTER);

		this.addComponent(this.titleLayout);
		this.addComponent(headingLayout);
		this.addComponent(directionLayout);
		this.addComponent(this.mainLayout);
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		// not implemented
	}

	public void openBrowseForStudyWindow() {
		this.browseTreeComponent.refreshTree();
		this.launchListSelectionWindow(this.getWindow(), this.browseTreeComponent, this.messageSource.getMessage(Message.BROWSE_STUDIES))
				.addListener(this.browseTreeComponent.getSaveTreeStateListener());
	}

	public void openSearchForStudyWindow() {
		this.launchListSelectionWindow(this.getWindow(), this.searchStudyComponent, this.messageSource.getMessage(Message.SEARCH_STUDIES));
	}

	private Window launchListSelectionWindow(final Window window, final Component content, final String caption) {

		final CssLayout layout = new CssLayout();
		layout.setMargin(true);
		layout.addComponent(content);

		final BaseSubWindow popupWindow = new BaseSubWindow();

		if (caption.equals(this.messageSource.getMessage(Message.SEARCH_STUDIES))) {
			popupWindow.setHeight("400px");

			layout.setHeight("340px");
			layout.setWidth("780px");
		} else {
			popupWindow.setHeight("550px");
			layout.setHeight("490px");
			layout.setWidth("100%");
		}
		popupWindow.setWidth("782px");

		popupWindow.setModal(true);
		popupWindow.setResizable(false);
		popupWindow.center();
		popupWindow.setCaption(caption);
		popupWindow.setContent(layout);
		popupWindow.addStyleName(Reindeer.WINDOW_LIGHT);

		window.addWindow(popupWindow);

		return popupWindow;
	}

	public BrowseStudyTreeComponent getBrowseTreeComponent() {
		return this.browseTreeComponent;
	}

	public StudySearchMainComponent getStudySearchComponent() {
		return this.searchStudyComponent;
	}

	public StudyBrowserMainLayout getMainLayout() {
		return this.mainLayout;
	}
}
