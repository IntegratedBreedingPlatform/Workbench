package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Window;

@Configurable
public class StudyFolderNameValidator {
	
	private static final int STUDY_NAME_LIMITS = 255;
	
	@Autowired
	private ContextUtil contextUtil;
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private StudyDataManager studyDataManager;
	
	protected boolean isValidNameInput(final String newFolderName, final Window window) {
		final String programUUID = this.contextUtil.getProjectInContext().getUniqueID();
		if ("".equals(newFolderName.replace(" ", ""))) {
			MessageNotifier.showRequiredFieldError(window, this.messageSource.getMessage(Message.INVALID_ITEM_NAME));
			return false;

		} else if (newFolderName.length() > STUDY_NAME_LIMITS) {
			MessageNotifier.showRequiredFieldError(window,
					this.messageSource.getMessage(Message.INVALID_LONG_STUDY_FOLDER_NAME));
			return false;

		} else if (this.studyDataManager.checkIfProjectNameIsExistingInProgram(newFolderName, programUUID)) {
			MessageNotifier.showRequiredFieldError(window,
					this.messageSource.getMessage(Message.EXISTING_STUDY_ERROR_MESSAGE));
			return false;

		} else if (newFolderName.equalsIgnoreCase(this.messageSource.getMessage(Message.STUDIES))) {
			MessageNotifier.showRequiredFieldError(window,
					this.messageSource.getMessage(Message.EXISTING_STUDY_ERROR_MESSAGE));
			return false;
		}

		return true;
	}

}
