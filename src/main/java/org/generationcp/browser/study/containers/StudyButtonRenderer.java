
package org.generationcp.browser.study.containers;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.StudyPermissionValidator;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Resource;

@Configurable
public class StudyButtonRenderer {

	private static final String MANAGE_STUDY_URL = "/Fieldbook/TrialManager";

	protected static final String[] URL_STUDY_TRIAL = {MANAGE_STUDY_URL + "/openTrial/", "#/trialSettings"};
	protected static final String WORKBENCHMAINVIEW_IFRAME_NAME = "PID_Sbrowser";

	@Autowired
	private StudyPermissionValidator studyPermissionValidator;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Resource
	private ContextUtil contextUtil;

	private final StudyReference study;

	public StudyButtonRenderer(final StudyReference study) {
		super();
		this.study = study;
	}

	public Button renderStudyButton() {
		final ExternalResource urlToOpenStudy = this.getURLStudy();
		Button studyButton = new LinkButton(urlToOpenStudy, this.study.getName(), WORKBENCHMAINVIEW_IFRAME_NAME);
		studyButton.addListener((ClickListener) event -> {
			event.getComponent().getWindow().executeJavaScript("window.top.postMessage({ toolSelected: '" + MANAGE_STUDY_URL + "'}, '*');");
		});

		try {
			this.availableLinkToStudy(studyButton);
		} catch (final AccessDeniedException e) {
			studyButton.setEnabled(false);
		}

		// If user doesn't have proper permissions for a locked study, show error message
		if (this.studyPermissionValidator.userLacksPermissionForStudy(this.study)) {
			studyButton = new Button(this.study.getName(), new LockedStudyButtonClickListener());

		} else if (!this.contextUtil.getCurrentProgramUUID().equals(this.study.getProgramUUID())) {
			studyButton.setCaption(studyButton.getCaption());
			studyButton.setEnabled(false);
		}

		studyButton.setDebugId("linkStudyButton");
		studyButton.addStyleName(BaseTheme.BUTTON_LINK);
		return studyButton;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGE_STUDIES','ROLE_STUDIES')")
	private void availableLinkToStudy(final Button studyButton) {
		studyButton.setEnabled(true);
	}

	private ExternalResource getURLStudy() {
		final String aditionalParameters =
			"?restartApplication&loggedInUserId=" + this.contextUtil.getContextInfoFromSession().getLoggedInUserId() + "&selectedProjectId="
				+ this.contextUtil.getContextInfoFromSession().getSelectedProjectId();

		return new ExternalResource(URL_STUDY_TRIAL[0] + this.study.getId() + aditionalParameters + URL_STUDY_TRIAL[1]);
	}

	protected class LockedStudyButtonClickListener implements ClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(final ClickEvent event) {
			MessageNotifier.showError(event.getButton().getWindow(), StudyButtonRenderer.this.messageSource.getMessage(Message.ERROR),
				StudyButtonRenderer.this.messageSource.getMessage(Message.LOCKED_STUDY_CANT_BE_MODIFIED,
					StudyButtonRenderer.this.study.getOwnerName()));
		}

	}

	public void setStudyPermissionValidator(final StudyPermissionValidator studyPermissionValidator) {
		this.studyPermissionValidator = studyPermissionValidator;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

}
