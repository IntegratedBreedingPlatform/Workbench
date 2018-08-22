package org.generationcp.ibpworkbench.sample;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.sample.SampleGermplasmDetailDTO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.service.api.SampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

@Configurable
public class SampleInfoComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;
	private final static Logger LOG = LoggerFactory.getLogger(SampleInfoComponent.class);

	// Sample Information Model
	private static final String SAMPLE_ID = "Sample ID";
	private static final String SAMPLE_LIST = "Sample List";
	private static final String STUDY_NAME = "Study Name";
	private static final String PLOT_ID = "Plot ID";
	private static final String PLANT_ID = "Plant ID";
	private static final String PLATE_ID = "Plate ID";
	private static final String WELL = "WELL";
	private static final String URL_GENOTYPING_DATASET = "/GDMS/main/?restartApplication&datasetId=";

	private static final String GENOTYPING_DATA = "genotyping dataset";
	private static final String PARENT_WINDOW = "_parent";

	private static final String URL_STUDY_TRIAL = "/Fieldbook/TrialManager/openTrial/%s#/trialSettings";

	private final Integer gid;

	private Table sampleTable;
	private Label noDataAvailableLabel;

	@Resource
	private SampleService sampleService;

	@Autowired
	private PlatformTransactionManager transactionManager;


	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Resource
	private ContextUtil contextUtil;

	public SampleInfoComponent(final Integer gid) {
		this.gid = gid;
	}

	@Override
	public void updateLabels() {

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
		this.initializeValues();
		this.layoutComponents();
	}

	private void initializeComponents() {


		this.noDataAvailableLabel = new Label("There is no sample information.");

		// add the column ids to the LazyQueryContainer tells the container the columns to display for the Table
		this.setSampleTable(new Table());
		this.getSampleTable().setWidth("100%");

		// prepare the container
		this.getSampleTable().addContainerProperty(SampleInfoComponent.SAMPLE_ID, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.SAMPLE_LIST, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.STUDY_NAME, LinkButton.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.PLOT_ID, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.PLANT_ID, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.PLATE_ID, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.WELL, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.GENOTYPING_DATA, HorizontalLayout.class, null);
		this.getSampleTable().setSelectable(true);
		this.getSampleTable().setMultiSelect(false);
		this.getSampleTable().setImmediate(true); // react at once when something is selected turn on column reordering and collapsing
		this.getSampleTable().setColumnReorderingAllowed(true);
		this.getSampleTable().setColumnCollapsingAllowed(true);

		this.getSampleTable().setColumnHeader(SampleInfoComponent.SAMPLE_ID, SampleInfoComponent.SAMPLE_ID);
		this.getSampleTable().setColumnHeader(SampleInfoComponent.SAMPLE_LIST, SampleInfoComponent.SAMPLE_LIST);
		this.getSampleTable().setColumnHeader(SampleInfoComponent.STUDY_NAME, SampleInfoComponent.STUDY_NAME);
		this.getSampleTable().setColumnHeader(SampleInfoComponent.PLOT_ID,SampleInfoComponent.PLOT_ID);
		this.getSampleTable().setColumnHeader(SampleInfoComponent.PLANT_ID, SampleInfoComponent.PLANT_ID);
		this.getSampleTable().setColumnHeader(SampleInfoComponent.PLATE_ID, SampleInfoComponent.PLATE_ID);
		this.getSampleTable().setColumnHeader(SampleInfoComponent.WELL, SampleInfoComponent.WELL);
		this.getSampleTable().setColumnHeader(SampleInfoComponent.GENOTYPING_DATA, SampleInfoComponent.GENOTYPING_DATA);

		this.getSampleTable().setVisibleColumns(
			new String[] {SampleInfoComponent.SAMPLE_ID, SampleInfoComponent.SAMPLE_LIST, SampleInfoComponent.STUDY_NAME,
				SampleInfoComponent.PLOT_ID, SampleInfoComponent.PLANT_ID, SampleInfoComponent.PLATE_ID, SampleInfoComponent.WELL,
				SampleInfoComponent.GENOTYPING_DATA});
	}

	private void initializeValues() {
		final List<SampleGermplasmDetailDTO> sampleList = retrieveSampleInformation(this.gid);
		int count = 1;

		if (sampleList.size() > 10) {
			this.sampleTable.setPageLength(10);
		} else {
			this.sampleTable.setPageLength(sampleList.size());
		}

		final String authParams = "&" + getAuthParams(contextUtil);

		for (final SampleGermplasmDetailDTO sample : sampleList) {
			final StudyReference study = sample.getStudy();
			final ExternalResource urlToOpenStudy = getURLStudy(study, authParams);
			final LinkButton linkStudyButton = new LinkButton(urlToOpenStudy, study.getName(), PARENT_WINDOW);
			linkStudyButton.setDebugId("linkStudyButton");
			linkStudyButton.addStyleName(BaseTheme.BUTTON_LINK);

			final HorizontalLayout horizontalLayoutForDatasetButton = new HorizontalLayout();
			horizontalLayoutForDatasetButton.setDebugId("HDatasets");
			int total = sample.getDatasets().size();
			for (final SampleGermplasmDetailDTO.Dataset dataset : sample.getDatasets()) {
				final ExternalResource urlToOpenGenotypingData =
					new ExternalResource(URL_GENOTYPING_DATASET + dataset.getDatasetId() + authParams);
				final LinkButton linkGenotypingDataButton = new LinkButton(urlToOpenGenotypingData, dataset.getDatasetName(), PARENT_WINDOW);
				linkGenotypingDataButton.setDebugId("linkGenotypingDataButton");
				linkGenotypingDataButton.addStyleName(BaseTheme.BUTTON_LINK);
				horizontalLayoutForDatasetButton.addComponent(linkGenotypingDataButton);

				if (--total != 0) {
					horizontalLayoutForDatasetButton.addComponent(new Label(","));
				}
			}

			this.sampleTable.addItem(
				new Object[] {sample.getSampleBk(), sample.getSampleListName(), linkStudyButton, sample.getPlotId(), sample.getPlantBk(),sample.getPlateId(),sample.getWell(),
					horizontalLayoutForDatasetButton}, count);
			count++;
		}
	}

	private void layoutComponents() {
		if (null != this.sampleTable && !this.sampleTable.getItemIds().isEmpty()) {
			this.addComponent(this.sampleTable);
		} else {
			this.addComponent(this.noDataAvailableLabel);
		}
	}


	private static ExternalResource getURLStudy(final StudyReference study, final String authParams) {
		final String aditionalParameters = "?restartApplication&" + authParams;

		return new ExternalResource(String.format(URL_STUDY_TRIAL, study.getId() + aditionalParameters));
	}

	private static String getAuthParams(final ContextUtil contextUtil) {
		final String authToken = contextUtil.getContextInfoFromSession().getAuthToken();
		return "loggedInUserId=" + contextUtil.getContextInfoFromSession().getLoggedInUserId() + "&selectedProjectId=" + contextUtil
			.getContextInfoFromSession().getSelectedProjectId() + "&authToken=" + (authToken != null ? authToken : "");
	}

	public Table getSampleTable() {
		return sampleTable;
	}

	public void setSampleTable(final Table sampleTable) {
		this.sampleTable = sampleTable;
	}

	public Label getNoDataAvailableLabel() {
		return noDataAvailableLabel;
	}

	public void setNoDataAvailableLabel(final Label noDataAvailableLabel) {
		this.noDataAvailableLabel = noDataAvailableLabel;
	}

	public SampleService getSampleService() {
		return sampleService;
	}

	public void setSampleService(final SampleService sampleService) {
		this.sampleService = sampleService;
	}

	private List<SampleGermplasmDetailDTO> retrieveSampleInformation(final Integer gid) {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);

		return transactionTemplate.execute(new TransactionCallback<List<SampleGermplasmDetailDTO>>() {

			@Override
			public List<SampleGermplasmDetailDTO> doInTransaction(final TransactionStatus status) {
				try {
					return sampleService.getByGid(gid);
				} catch (final MiddlewareException e) {
					status.setRollbackOnly();
					throw new MiddlewareException(e.getMessage(), e);
				}
			}
		});
	}
}
