
package org.generationcp.browser.study.containers;

import javax.annotation.Resource;

import org.generationcp.commons.security.AuthorizationUtil;
import org.generationcp.commons.spring.util.ContextUtil;
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

@Configurable
public class StudyButtonRenderer {

	private static final String[] URL_STUDY_TRIAL = {"/Fieldbook/TrialManager/openTrial/", "#/trialSettings"};
	private static final String PARENT_WINDOW = "_parent";

	@Resource
	private ContextUtil contextUtil;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private StudyReference study;

	public StudyButtonRenderer(final StudyReference study) {
		super();
		this.study = study;
	}

	public Button renderStudyButton() {
		final Integer currentUserId = contextUtil.getContextInfoFromSession().getLoggedInUserId();
		final ExternalResource urlToOpenStudy = getURLStudy();
		Button studyButton = new LinkButton(urlToOpenStudy, study.getName(), PARENT_WINDOW);
		studyButton.setDebugId("linkStudyButton");
		studyButton.addStyleName(BaseTheme.BUTTON_LINK);
		
		// If user doesn't have proper permissions for a locked study, show error message
		if (AuthorizationUtil.userLacksPermissionForStudy(study, currentUserId)) {
			studyButton = new Button(study.getName(), new LockedStudyButtonClickListener());
			
		} else if (!contextUtil.getCurrentProgramUUID().equals(study.getProgramUUID())) {
			studyButton.setCaption(studyButton.getCaption());
			studyButton.setEnabled(false);
		}

		return studyButton;
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

}
