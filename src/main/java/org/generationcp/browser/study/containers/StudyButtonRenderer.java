
package org.generationcp.browser.study.containers;

import javax.annotation.Resource;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.StudyPermissionValidator;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

@Configurable
public class StudyButtonRenderer {

	protected static final String[] URL_STUDY_TRIAL = {"/Fieldbook/TrialManager/openTrial/", "#/trialSettings"};
	protected static final String PARENT_WINDOW = "_parent";
	
	@Autowired
	private StudyPermissionValidator studyPermissionValidator;
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Resource
	private ContextUtil contextUtil;

	private StudyReference study;

	public StudyButtonRenderer(final StudyReference study) {
		super();
		this.study = study;
	}

	public Button renderStudyButton() {
		final ExternalResource urlToOpenStudy = getURLStudy();
		Button studyButton = new LinkButton(urlToOpenStudy, study.getName(), PARENT_WINDOW);

		try {
			availableLinkToStudy(studyButton);
		} catch (final AccessDeniedException e) {
			studyButton.setEnabled(false);
		}

		// If user doesn't have proper permissions for a locked study, show error message
		if (this.studyPermissionValidator.userLacksPermissionForStudy(study)) {
			studyButton = new Button(study.getName(), new LockedStudyButtonClickListener());
			
		} else if (!contextUtil.getCurrentProgramUUID().equals(study.getProgramUUID())) {
			studyButton.setCaption(studyButton.getCaption());
			studyButton.setEnabled(false);
		}

		studyButton.setDebugId("linkStudyButton");
		studyButton.addStyleName(BaseTheme.BUTTON_LINK);
		return studyButton;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGE_STUDIES','ROLE_BREEDING_ACTIVITIES')")
	private void availableLinkToStudy(final Button studyButton) {
		studyButton.setEnabled(true);
	}

	private ExternalResource getURLStudy() {
		final String aditionalParameters =
				"?restartApplication&loggedInUserId=" + contextUtil.getContextInfoFromSession().getLoggedInUserId() + "&selectedProjectId="
						+ contextUtil.getContextInfoFromSession().getSelectedProjectId() + "&authToken="
						+ contextUtil.getContextInfoFromSession().getAuthToken();

		return new ExternalResource(URL_STUDY_TRIAL[0] + study.getId() + aditionalParameters + URL_STUDY_TRIAL[1]);
	}

	
	protected class LockedStudyButtonClickListener implements ClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			MessageNotifier.showError(event.getButton().getWindow(), StudyButtonRenderer.this.messageSource.getMessage(Message.ERROR),
					StudyButtonRenderer.this.messageSource.getMessage(Message.LOCKED_STUDY_CANT_BE_MODIFIED,
							StudyButtonRenderer.this.study.getOwnerName()));
		}

	}


	
	public void setStudyPermissionValidator(StudyPermissionValidator studyPermissionValidator) {
		this.studyPermissionValidator = studyPermissionValidator;
	}

	
	public void setContextUtil(ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

}
