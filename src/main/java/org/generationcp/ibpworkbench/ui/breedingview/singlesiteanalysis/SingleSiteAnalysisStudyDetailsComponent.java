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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class SingleSiteAnalysisStudyDetailsComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout {
	
	private static final long serialVersionUID = 1L;

	private static final String LABEL_BOLD_STYLING = "label-bold";
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	private Label lblDataSelectedForAnalysisHeader;
	private Label lblDatasetName;
	private Label lblDatasourceName;
	private Label lblProjectType;
	private Label lblAnalysisName;
	
	private Label valueProjectType;
	private TextField txtAnalysisName;
	private Label valueDatasetName;
	private Label valueDatasourceName;
	
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
		this.messageSource.setValue(this.lblDatasourceName, Message.BV_DATASOURCE_NAME);
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
		this.lblDatasetName.setStyleName(LABEL_BOLD_STYLING);
		this.lblDatasourceName = new Label();
		this.lblDatasourceName.setDebugId("lblDatasourceName");
		this.lblDatasourceName.setContentMode(Label.CONTENT_XHTML);
		this.lblDatasourceName.setStyleName(LABEL_BOLD_STYLING);

		this.lblProjectType = new Label();
		this.lblProjectType.setDebugId("lblProjectType");
		this.lblProjectType.setStyleName(LABEL_BOLD_STYLING);
		this.lblProjectType.setWidth("100px");
		this.lblAnalysisName = new Label();
		this.lblAnalysisName.setDebugId("lblAnalysisName");
		this.lblAnalysisName.setContentMode(Label.CONTENT_XHTML);
		this.lblAnalysisName.setStyleName(LABEL_BOLD_STYLING);
		
		this.valueProjectType = new Label();
		this.valueProjectType.setDebugId("valueProjectType");

		this.valueDatasetName = new Label();
		this.valueDatasetName.setDebugId("valueDatasetName");
		this.valueDatasetName.setWidth("100%");
		this.valueDatasetName.setValue(this.ssaDetailsPanel.getBreedingViewInput().getDatasetName());

		this.valueDatasourceName = new Label();
		this.valueDatasourceName.setDebugId("valueDatasourceName");
		this.valueDatasourceName.setWidth("100%");
		
		this.txtAnalysisName = new TextField();
		this.txtAnalysisName.setDebugId("txtAnalysisName");
		this.txtAnalysisName.setNullRepresentation("");
		this.txtAnalysisName.setRequired(false);
		this.txtAnalysisName.setWidth("450");
	}

	@Override
	public void initializeValues() {
		this.valueProjectType.setValue("Field Trial");
		
		this.valueDatasetName.setValue(this.ssaDetailsPanel.getBreedingViewInput().getDatasetName());
		
		this.valueDatasourceName.setValue(this.ssaDetailsPanel.getBreedingViewInput().getDatasetSource());
		
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

		final HorizontalLayout row2a = new HorizontalLayout();
		row2a.setDebugId("row2a");
		row2a.setSpacing(true);
		row2a.addComponent(this.lblDatasetName);
		row2a.addComponent(this.valueDatasetName);
		final HorizontalLayout row2b = new HorizontalLayout();
		row2b.setDebugId("row2b");
		row2b.setSpacing(true);
		row2b.addComponent(this.lblProjectType);
		row2b.addComponent(this.valueProjectType);

		final GridLayout row2 = new GridLayout(2, 1);
		row2.setDebugId("row2");
		row2.setSizeUndefined();
		row2.setWidth("100%");
		row2.setColumnExpandRatio(0, 0.45f);
		row2.setColumnExpandRatio(1, 0.55f);
		row2.addComponent(row2a);
		row2.addComponent(row2b);

		final HorizontalLayout row3 = new HorizontalLayout();
		row3.setDebugId("row3");
		row3.setSpacing(true);
		row3.addComponent(this.lblDatasourceName);
		row3.addComponent(this.valueDatasourceName);

		final VerticalLayout row4 = new VerticalLayout();
		row4.setDebugId("row4");
		row4.setSpacing(true);
		row4.addComponent(this.lblAnalysisName);
		row4.addComponent(this.txtAnalysisName);

		this.addComponent(row1);
		this.addComponent(row2);
		this.addComponent(row3);
		this.addComponent(row4);
	}
	
	public String getTxtAnalysisName() {
		return (String) this.txtAnalysisName.getValue();
	}
	
}
