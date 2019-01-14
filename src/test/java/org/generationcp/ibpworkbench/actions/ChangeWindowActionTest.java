package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.metaanalysis.MetaAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.MultiSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.ibpworkbench.ui.recovery.BackupAndRestoreView;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChangeWindowActionTest {

	public static final String MESSAGE = "message";

	@Mock
	private IContentWindow window;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	private ChangeWindowAction changeWindowAction;

	private Project project;

	@Before
	public void init() {

		project = new Project();

		changeWindowAction = new ChangeWindowAction(null, project);
		changeWindowAction.setContextUtil(contextUtil);
		changeWindowAction.setMessageSource(messageSource);

		when(messageSource.getMessage(any(Message.class))).thenReturn(MESSAGE);

	}

	@Test
	public void testLaunchMembers() {

		changeWindowAction.launchWindow(window, ChangeWindowAction.WindowEnums.MEMBER.getwindowName(), false);

		verify(window).showContent(any(ProgramMembersPanel.class));
		verify(contextUtil).logProgramActivity(eq(ChangeWindowAction.WindowEnums.MEMBER.getwindowName()), ArgumentMatchers.<String>isNull());

	}

	@Test
	public void testLaunchRecovery() {

		changeWindowAction.launchWindow(window, ChangeWindowAction.WindowEnums.BACKUP_RESTORE.getwindowName(), false);

		verify(window).showContent(any(BackupAndRestoreView.class));
		verify(contextUtil).logProgramActivity(eq(ChangeWindowAction.WindowEnums.BACKUP_RESTORE.getwindowName()), ArgumentMatchers.<String>isNull());

	}

	@Test
	public void testLaunchBreedingViewMultiSite() {

		changeWindowAction.launchWindow(window, ChangeWindowAction.WindowEnums.BREEDING_GXE.getwindowName(), false);

		verify(window).showContent(any(MultiSiteAnalysisPanel.class));
		verify(contextUtil).logProgramActivity(eq(ChangeWindowAction.WindowEnums.BREEDING_GXE.getwindowName()), ArgumentMatchers.<String>isNull());

	}

	@Test
	public void testLaunchBreedingViewSingleSite() {

		changeWindowAction.launchWindow(window, ChangeWindowAction.WindowEnums.BREEDING_VIEW.getwindowName(), false);

		verify(window).showContent(any(SingleSiteAnalysisPanel.class));
		verify(contextUtil).logProgramActivity(eq(ChangeWindowAction.WindowEnums.BREEDING_VIEW.getwindowName()), ArgumentMatchers.<String>isNull());

	}

	@Test
	public void testLaunchBMetaAnalysis() {

		changeWindowAction.launchWindow(window, ChangeWindowAction.WindowEnums.BV_META_ANALYSIS.getwindowName(), false);

		verify(window).showContent(any(MetaAnalysisPanel.class));
		verify(contextUtil).logProgramActivity(eq(ChangeWindowAction.WindowEnums.BV_META_ANALYSIS.getwindowName()), ArgumentMatchers.<String>isNull());

	}

}
