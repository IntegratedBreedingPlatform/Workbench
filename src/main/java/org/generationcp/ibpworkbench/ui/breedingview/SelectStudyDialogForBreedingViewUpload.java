
package org.generationcp.ibpworkbench.ui.breedingview;

import javax.annotation.Resource;

import org.generationcp.commons.security.AuthorizationUtil;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

@Configurable
public class SelectStudyDialogForBreedingViewUpload extends SelectStudyDialog {

	private static final long serialVersionUID = 1L;
	
	@Resource
	private ContextUtil contextUtil;

	public SelectStudyDialogForBreedingViewUpload(Window parentWindow, Component source,
			Project project) {
		super(parentWindow, source, project);
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this, Message.BV_SELECT_STUDY_FOR_UPLOAD);
		this.messageSource.setValue(this.lblStudyTreeDetailDescription, Message.BV_SELECT_STUDY_FOR_UPLOAD_DESCRIPTION);
	}

	@Override
	protected void openStudy(Reference r) {
		final Integer studyId = r.getId();
		final StudyReference study = this.studyDataManager.getStudyReference(studyId);
		// Prevent user with no permission for locked study from uploading means dataset
		if (AuthorizationUtil.userLacksPermissionForStudy(study, this.contextUtil.getContextInfoFromSession().getLoggedInUserId())) {
			MessageNotifier.showError(this.parentWindow, this.messageSource.getMessage(Message.ERROR_WITH_MODIFYING_STUDY_TREE),
					this.messageSource.getMessage(Message.LOCKED_STUDY_CANT_BE_MODIFIED, study.getOwnerName()));
			return;
		}
		SingleSiteAnalysisPanel ssaPanel = (SingleSiteAnalysisPanel) this.source;
		FileUploadBreedingViewOutputWindow dialog =
				new FileUploadBreedingViewOutputWindow(this.parentWindow, studyId, ssaPanel.getCurrentProject(), null);
		this.parentWindow.getWindow().addWindow(dialog);
		this.parentWindow.getWindow().removeWindow(this);
	}

}
