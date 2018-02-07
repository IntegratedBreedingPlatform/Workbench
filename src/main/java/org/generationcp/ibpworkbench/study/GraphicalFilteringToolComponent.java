package org.generationcp.ibpworkbench.study;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.constant.DefaultGermplasmStudyBrowserPath;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created by clarysabel on 2/7/18.
 */
@Configurable
public class GraphicalFilteringToolComponent extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private Integer studyId;

	@Override
	public void updateLabels() {
	}

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	GraphicalFilteringToolComponent(final Integer studyId) {
		this.studyId = studyId;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final ExternalResource graphicalFilteringToolLink = new ExternalResource(
				WorkbenchAppPathResolver.getFullWebAddress(DefaultGermplasmStudyBrowserPath.GRAPHICAL_FILTERING_TOOL_LINK + this.studyId));
		final VerticalLayout graphicalFilteringLayout = new VerticalLayout();
		graphicalFilteringLayout.setMargin(false);
		graphicalFilteringLayout.setWidth("98%");
		graphicalFilteringLayout.setHeight("98%");

		final Embedded germplasmInfo = new Embedded("", graphicalFilteringToolLink);
		germplasmInfo.setType(Embedded.TYPE_BROWSER);
		germplasmInfo.setSizeFull();
		graphicalFilteringLayout.addComponent(germplasmInfo);

		this.setContent(graphicalFilteringLayout);

		this.setWidth("75%");
		this.setHeight("75%");

		this.center();
		this.setResizable(true);
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
	}
}
