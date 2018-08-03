package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.mysql.jdbc.StringUtils;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class SingleSiteAnalysisStudyDetailsComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout {
	
	private static final long serialVersionUID = 1L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	private Label lblDataSelectedForAnalysisHeader;
	private Label lblDatasetName;
	private Label lblStudyName;
	private Label lblProjectType;
	private Label lblAnalysisName;
	private Label lblDescription;
	private Label lblObjective;
	
	private Label valueProjectType;
	private TextField txtAnalysisName;
	private Label valueDatasetName;
	private Label valueStudyName;
	private Label valueObjective;
	private Label valueDescription;
	
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;

	public SingleSiteAnalysisStudyDetailsComponent(final SingleSiteAnalysisDetailsPanel ssaDetailsPanel) {
		super();
		this.ssaDetailsPanel = ssaDetailsPanel;
	}
	
	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}
	
	@Override
	public void updateLabels() {
		this.messageSource.setValue(this.lblProjectType, Message.BV_PROJECT_TYPE);
		this.messageSource.setValue(this.lblAnalysisName, Message.BV_ANALYSIS_NAME);
		this.messageSource.setValue(this.lblDatasetName, Message.BV_DATASET_NAME);
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
		this.lblDataSelectedForAnalysisHeader = new Label("<span class='bms-dataset' style='position:relative; top: -1px; color: #FF4612; "
				+ "font-size: 20px; font-weight: bold;'></span><b>&nbsp;" + this.messageSource
				.getMessage(Message.BV_DATA_SELECTED_FOR_ANALYSIS_HEADER) + "</b>", Label.CONTENT_XHTML);
		this.lblDataSelectedForAnalysisHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblDatasetName = new Label();
		this.lblDatasetName.setDebugId("lblDatasetName");
		this.lblDatasetName.setContentMode(Label.CONTENT_XHTML);
		this.lblDatasetName.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblStudyName = new Label();
		this.lblStudyName.setDebugId("lblDatasourceName");
		this.lblStudyName.setContentMode(Label.CONTENT_XHTML);
		this.lblStudyName.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblProjectType = new Label();
		this.lblProjectType.setDebugId("lblProjectType");
		this.lblProjectType.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblProjectType.setWidth("100px");
		
		this.lblAnalysisName = new Label();
		this.lblAnalysisName.setDebugId("lblAnalysisName");
		this.lblAnalysisName.setContentMode(Label.CONTENT_XHTML);
		this.lblAnalysisName.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		
		this.lblDescription = new Label();
		this.lblDescription.setDebugId("lblDescription");
		this.lblDescription.setContentMode(Label.CONTENT_XHTML);
		this.lblDescription.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		
		this.lblObjective = new Label();
		this.lblObjective.setDebugId("lblObjective");
		this.lblObjective.setContentMode(Label.CONTENT_XHTML);
		this.lblObjective.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		
		this.valueProjectType = new Label();
		this.valueProjectType.setDebugId("valueProjectType");

		this.valueDatasetName = new Label();
		this.valueDatasetName.setDebugId("valueDatasetName");
		this.valueDatasetName.setWidth("100%");
	
		this.valueStudyName = new Label();
		this.valueStudyName.setDebugId("valueStudyName");
		this.valueStudyName.setWidth("100%");
		
		this.valueDescription = new Label();
		this.valueDescription.setDebugId("valueDescription");
		this.valueDescription.setWidth("100%");
		
		this.valueObjective = new Label();
		this.valueObjective.setDebugId("valueObjective");
		this.valueObjective.setWidth("100%");
		
		this.txtAnalysisName = new TextField();
		this.txtAnalysisName.setDebugId("txtAnalysisName");
		this.txtAnalysisName.setNullRepresentation("");
		this.txtAnalysisName.setRequired(false);
		this.txtAnalysisName.setWidth("450");
	}

	@Override
	public void initializeValues() {
		this.lblStudyName.setValue(this.messageSource.getMessage(Message.STUDY_NAME_LABEL) + ":");
		this.lblDescription.setValue(this.messageSource.getMessage(Message.DESCRIPTION_HEADER) + ":");
		this.lblObjective.setValue(this.messageSource.getMessage(Message.OBJECTIVE_LABEL) + ":");
		
		this.valueProjectType.setValue("Field Trial");
		this.valueDatasetName.setValue(this.ssaDetailsPanel.getBreedingViewInput().getDatasetName());
		this.valueDescription.setValue(this.ssaDetailsPanel.getBreedingViewInput().getDescription());
		this.valueObjective.setValue(this.ssaDetailsPanel.getBreedingViewInput().getObjective());
		this.valueStudyName.setValue(this.ssaDetailsPanel.getBreedingViewInput().getDatasetSource());
		
		setAnalysisName();
	}

	public void setAnalysisName() {
		final String analysisName = this.ssaDetailsPanel.getBreedingViewInput().getBreedingViewAnalysisName();
		if (!StringUtils.isNullOrEmpty(analysisName)) {
			this.txtAnalysisName.setValue(analysisName);
		}
	}

	@Override
	public void addListeners() {
		// No listeners
	}

	@Override
	public void layoutComponents() {
		this.setDebugId("this");
		this.setSizeUndefined();
		this.setWidth("100%");
		this.setSpacing(true);

		final HorizontalLayout row1 = new HorizontalLayout();
		row1.setDebugId("row1");
		row1.setSpacing(true);
		row1.addComponent(this.lblDataSelectedForAnalysisHeader);
		
		final HorizontalLayout row2 = new HorizontalLayout();
		row2.setDebugId("row2");
		row2.setSpacing(true);
		row2.addComponent(this.lblStudyName);
		row2.addComponent(this.valueStudyName);

		final HorizontalLayout row3 = new HorizontalLayout();
		row3.setDebugId("row3");
		row3.setSpacing(true);
		row3.addComponent(this.lblDatasetName);
		row3.addComponent(this.valueDatasetName);
		
		final HorizontalLayout row4 = new HorizontalLayout();
		row4.setDebugId("row4");
		row4.setSpacing(true);
		row4.addComponent(this.lblProjectType);
		row4.addComponent(this.valueProjectType);
		
		final HorizontalLayout row5 = new HorizontalLayout();
		row5.setDebugId("row5");
		row5.setSpacing(true);
		row5.addComponent(this.lblDescription);
		row5.addComponent(this.valueDescription);
		
		final HorizontalLayout row6 = new HorizontalLayout();
		row6.setDebugId("row6");
		row6.setSpacing(true);
		row6.setHeight("40px");
		row6.addComponent(this.lblObjective);
		row6.addComponent(this.valueObjective);
		
		final HorizontalLayout row7 = new HorizontalLayout();
		row7.setDebugId("row7");
		row7.setSpacing(true);
		row7.addComponent(this.lblAnalysisName);

		final HorizontalLayout row8 = new HorizontalLayout();
		row8.setDebugId("row8");
		row8.setSpacing(true);
		row8.addComponent(this.txtAnalysisName);
		
		this.addComponent(row1);
		this.addComponent(row2);
		this.addComponent(row3);
		this.addComponent(row4);
		this.addComponent(row5);
		this.addComponent(row6);
		this.addComponent(row7);
		this.addComponent(row8);
	}
	
	public String getTxtAnalysisName() {
		return (String) this.txtAnalysisName.getValue();
	}
	
}
