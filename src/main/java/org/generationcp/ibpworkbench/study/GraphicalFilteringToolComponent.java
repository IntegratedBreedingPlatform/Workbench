package org.generationcp.ibpworkbench.study;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.constant.DefaultGermplasmStudyBrowserPath;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;

/**
 * Created by clarysabel on 2/7/18.
 */
@Configurable
public class GraphicalFilteringToolComponent extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private final String crop;
	private final Integer studyId;

	@Override
	public void updateLabels() {
	}

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	GraphicalFilteringToolComponent(final Integer studyId, final String crop) {
		this.studyId = studyId;
		this.crop = crop;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final ExternalResource graphicalFilteringToolLink = new ExternalResource(WorkbenchAppPathResolver
			.getFullWebAddress(String.format(DefaultGermplasmStudyBrowserPath.GRAPHICAL_FILTERING_TOOL_LINK, this.studyId, crop)));
		final VerticalLayout graphicalFilteringLayout = new VerticalLayout();
		graphicalFilteringLayout.setMargin(false);
		graphicalFilteringLayout.setWidth("100%");
		graphicalFilteringLayout.setHeight("100%");

		final Embedded graphicalFilteringToolEmbedded = new Embedded("", graphicalFilteringToolLink);
		graphicalFilteringToolEmbedded.setType(Embedded.TYPE_BROWSER);
		graphicalFilteringToolEmbedded.setSizeFull();
		graphicalFilteringLayout.addComponent(graphicalFilteringToolEmbedded);

		this.setContent(graphicalFilteringLayout);

		this.setWidth("98%");
		this.setHeight("98%");

		this.setName("BrAPI Graphical Filtering");
		this.setCaption("BrAPI Graphical Filtering");

		this.center();
		this.setResizable(true);
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
		
	}
}
