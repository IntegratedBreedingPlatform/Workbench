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
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.service.api.SampleService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Configurable
public class SampleInfoComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	// Sample Information Model
	private static final String SAMPLE_ID = "SAMPLE ID";
	private static final String SAMPLE_LIST = "SAMPLE LIST";
	private static final String STUDY_NAME = "STUDY NAME";
	private static final String TAKEN_BY = "TAKEN BY";
	private static final String SAMPLING_DATE = "SAMPLING DATE";
	private static final String DATASET_TYPE = "DATASET_TYPE";
	private static final String OBS_UNIT_ID = "OBS UNIT ID";
	private static final String ENUMERATOR = "ENUMERATOR";
	private static final String PLATE_ID = "PLATE ID";
	private static final String WELL = "WELL";
	private static final String URL_GENOTYPING_DATASET = "/GDMS/main/?restartApplication&datasetId=";

	private static final String GENOTYPING_DATA = "genotyping dataset";
	private static final String WORKBENCHMAINVIEW_IFRAME_NAME = "PID_Sbrowser";

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
		// do nothing here
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
		this.getSampleTable().addContainerProperty(SampleInfoComponent.TAKEN_BY, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.SAMPLING_DATE, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.DATASET_TYPE, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.OBS_UNIT_ID, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.ENUMERATOR, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.PLATE_ID, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.WELL, String.class, null);
		this.getSampleTable().addContainerProperty(SampleInfoComponent.GENOTYPING_DATA, HorizontalLayout.class, null);
		this.getSampleTable().setSelectable(true);
		this.getSampleTable().setMultiSelect(false);
		this.getSampleTable().setImmediate(true); // react at once when something is selected turn on column reordering and collapsing
		this.getSampleTable().setColumnReorderingAllowed(true);
		this.getSampleTable().setColumnCollapsingAllowed(true);

		this.getSampleTable().setVisibleColumns(
			new String[] {
				SampleInfoComponent.SAMPLE_ID, SampleInfoComponent.SAMPLE_LIST, SampleInfoComponent.STUDY_NAME,
				SampleInfoComponent.TAKEN_BY, SampleInfoComponent.SAMPLING_DATE, SampleInfoComponent.DATASET_TYPE,
				SampleInfoComponent.OBS_UNIT_ID,
				SampleInfoComponent.ENUMERATOR, SampleInfoComponent.PLATE_ID, SampleInfoComponent.WELL,
				SampleInfoComponent.GENOTYPING_DATA});
	}

	private void initializeValues() {
		final List<SampleDTO> sampleList = this.retrieveSampleInformation(this.gid);
		int count = 1;

		if (sampleList.size() > 10) {
			this.sampleTable.setPageLength(10);
		} else {
			this.sampleTable.setPageLength(sampleList.size());
		}

		final String authParams = "&" + getAuthParams(this.contextUtil);

		final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		for (final SampleDTO sample : sampleList) {

			final ExternalResource urlToOpenStudy = getURLStudy(sample.getStudyId(), authParams);
			final LinkButton linkStudyButton = new LinkButton(urlToOpenStudy, sample.getStudyName(), WORKBENCHMAINVIEW_IFRAME_NAME);

			try {
				availableLinkToStudy(linkStudyButton);
			} catch (final AccessDeniedException e) {
				linkStudyButton.setEnabled(false);
			}
			linkStudyButton.setDebugId("linkStudyButton");
			linkStudyButton.addStyleName(BaseTheme.BUTTON_LINK);

			final HorizontalLayout horizontalLayoutForDatasetButton = new HorizontalLayout();
			horizontalLayoutForDatasetButton.setDebugId("HDatasets");
			int total = sample.getDatasets().size();
			for (final SampleDTO.Dataset dataset : sample.getDatasets()) {
				final ExternalResource urlToOpenGenotypingData =
					new ExternalResource(URL_GENOTYPING_DATASET + dataset.getDatasetId() + authParams);
				final LinkButton linkGenotypingDataButton = new LinkButton(urlToOpenGenotypingData, dataset.getName(),
					WORKBENCHMAINVIEW_IFRAME_NAME);
				linkGenotypingDataButton.setDebugId("linkGenotypingDataButton");
				linkGenotypingDataButton.addStyleName(BaseTheme.BUTTON_LINK);
				horizontalLayoutForDatasetButton.addComponent(linkGenotypingDataButton);

				if (--total != 0) {
					horizontalLayoutForDatasetButton.addComponent(new Label(","));
				}
			}

			this.sampleTable.addItem(
				new Object[] {
					sample.getSampleBusinessKey(), sample.getSampleList(), linkStudyButton, sample.getTakenBy(),
					sample.getSamplingDate() != null ? formatter.format(sample.getSamplingDate()) : "-",
					sample.getDatasetType(), sample.getObservationUnitId(), sample.getEnumerator(), sample.getPlateId(), sample.getWell(),
					horizontalLayoutForDatasetButton}, count);
			count++;
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGE_STUDIES','ROLE_STUDIES')")
	private void availableLinkToStudy(final LinkButton linkStudyButton) {
		linkStudyButton.setEnabled(true);
	}

	private void layoutComponents() {
		if (null != this.sampleTable && !this.sampleTable.getItemIds().isEmpty()) {
			this.addComponent(this.sampleTable);
		} else {
			this.addComponent(this.noDataAvailableLabel);
		}
	}

	private static ExternalResource getURLStudy(final Integer studyId, final String authParams) {
		final String aditionalParameters = "?restartApplication&" + authParams;

		return new ExternalResource(String.format(URL_STUDY_TRIAL, studyId + aditionalParameters));
	}

	private static String getAuthParams(final ContextUtil contextUtil) {
		return "loggedInUserId=" + contextUtil.getContextInfoFromSession().getLoggedInUserId() + "&selectedProjectId=" + contextUtil
			.getContextInfoFromSession().getSelectedProjectId();
	}

	public Table getSampleTable() {
		return this.sampleTable;
	}

	public void setSampleTable(final Table sampleTable) {
		this.sampleTable = sampleTable;
	}

	public Label getNoDataAvailableLabel() {
		return this.noDataAvailableLabel;
	}

	public void setNoDataAvailableLabel(final Label noDataAvailableLabel) {
		this.noDataAvailableLabel = noDataAvailableLabel;
	}

	public SampleService getSampleService() {
		return this.sampleService;
	}

	public void setSampleService(final SampleService sampleService) {
		this.sampleService = sampleService;
	}

	private List<SampleDTO> retrieveSampleInformation(final Integer gid) {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);

		return transactionTemplate.execute(new TransactionCallback<List<SampleDTO>>() {

			@Override
			public List<SampleDTO> doInTransaction(final TransactionStatus status) {
				try {
					return SampleInfoComponent.this.sampleService.getByGid(gid);
				} catch (final MiddlewareException e) {
					status.setRollbackOnly();
					throw new MiddlewareException(e.getMessage(), e);
				}
			}
		});
	}
}
