package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDesignDetails;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import org.junit.Assert;

import com.vaadin.ui.Component;

public class UploadBVFilesButtonClickListenerTest {
	
	private static final int STUDY_ID = 101;

	@Mock
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;
	
	@InjectMocks
	private UploadBVFilesButtonClickListener listener;
	
	@Mock
	private Window window;
	
	@Mock
	private ClickEvent event;
	
	@Mock
	private Component component;
	
	private Map<String, Boolean> traitsMap;
	private BreedingViewInput bvInput;
	private Project testProject;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.component).when(this.event).getComponent();
		Mockito.doReturn(this.window).when(this.component).getWindow();
		Mockito.doReturn(this.createTestFactors()).when(this.ssaDetailsPanel).getFactorsInDataset();
		
		this.bvInput = new BreedingViewInput();
		this.bvInput.setStudyId(STUDY_ID);
		this.traitsMap = new HashMap<>();
		this.traitsMap.put("TRT1", true);
		this.traitsMap.put("TRT2", false);
		this.traitsMap.put("TRT3", true);
		this.bvInput.setVariatesSelectionMap(this.traitsMap);
		Mockito.doReturn(this.bvInput).when(this.ssaDetailsPanel).getBreedingViewInput();
		
		this.testProject = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(this.testProject).when(this.ssaDetailsPanel).getProject();
	}
	
	@Test
	public void testButtonClick() {
		this.listener.buttonClick(this.event);
		
		final ArgumentCaptor<Window> uploadWindowCaptor = ArgumentCaptor.forClass(Window.class);
		Mockito.verify(this.window).addWindow(uploadWindowCaptor.capture());
		final FileUploadBreedingViewOutputWindow uploadWindow = (FileUploadBreedingViewOutputWindow) uploadWindowCaptor.getValue();
		Assert.assertEquals(STUDY_ID, uploadWindow.getStudyId());
		Assert.assertEquals(this.testProject, uploadWindow.getProject());
		Assert.assertEquals(this.window, uploadWindow.getWindow());
		Assert.assertEquals(12, uploadWindow.getVariatesStateMap().size());
		
	}
	
	private List<DMSVariableType> createTestFactors() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		int rank = 1;
		final StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		factors.add(new DMSVariableType(TermId.ENTRY_NO.name(), TermId.ENTRY_NO.name(), entryNoVariable, rank++));

		final StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType(TermId.GID.name(), TermId.ENTRY_NO.name(), gidVariable, rank++));

		final StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType("DESIGNATION", "DESIGNATION", desigVariable, rank++));

		final StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name()));
		factors.add(new DMSVariableType(TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name(), entryTypeVariable, rank++));

		final StandardVariable obsUnitIdVariable = new StandardVariable();
		obsUnitIdVariable.setId(TermId.OBS_UNIT_ID.getId());
		obsUnitIdVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		obsUnitIdVariable.setProperty(new Term(1, TermId.OBS_UNIT_ID.name(), TermId.OBS_UNIT_ID.name()));
		factors.add(new DMSVariableType(TermId.OBS_UNIT_ID.name(), TermId.OBS_UNIT_ID.name(), obsUnitIdVariable, rank++));

		final StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.REPLICATION_FACTOR, "REP_NO"));
		factors.add(new DMSVariableType(TermId.REP_NO.name(), TermId.REP_NO.name(), repVariable, rank++));

		final StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.BLOCKING_FACTOR, "BLOCK_NO"));
		factors.add(new DMSVariableType(TermId.BLOCK_NO.name(), TermId.BLOCK_NO.name(), blockVariable, rank++));

		final StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.ROW_FACTOR, "ROW_NO"));
		factors.add(new DMSVariableType("ROW_NO", "ROW_NO", rowVariable, rank++));

		final StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.COLUMN_FACTOR, "COL_NO"));
		factors.add(new DMSVariableType(TermId.COLUMN_NO.name(), TermId.COLUMN_NO.name(), columnVariable, rank++));

		return factors;
	}

	

}
