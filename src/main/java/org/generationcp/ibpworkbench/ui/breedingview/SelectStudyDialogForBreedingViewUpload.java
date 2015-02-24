package org.generationcp.ibpworkbench.ui.breedingview;

import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.manager.StudyDataManagerImpl;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class SelectStudyDialogForBreedingViewUpload extends SelectStudyDialog {

	private static final long serialVersionUID = 1L;

	public SelectStudyDialogForBreedingViewUpload(Window parentWindow, Component source,
			StudyDataManagerImpl studyDataManager) {
		super(parentWindow, source, studyDataManager);
	}
	
	@Override
	public void updateLabels() {
			messageSource.setCaption(this, Message.BV_SELECT_STUDY_FOR_UPLOAD);
			messageSource.setValue(lblStudyTreeDetailDescription, Message.BV_SELECT_STUDY_FOR_UPLOAD_DESCRIPTION);		
	}
	
	@Override
	protected void openStudy(Reference r){
		SingleSiteAnalysisPanel ssaPanel = (SingleSiteAnalysisPanel) source;
		FileUploadBreedingViewOutputWindow dialog = new FileUploadBreedingViewOutputWindow(parentWindow, r.getId(), ssaPanel.getCurrentProject(), null);
		parentWindow.getWindow().addWindow(dialog);
		parentWindow.getWindow().removeWindow(this);
	}

}
