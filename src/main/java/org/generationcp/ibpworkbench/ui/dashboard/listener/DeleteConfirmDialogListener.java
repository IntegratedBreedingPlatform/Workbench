
package org.generationcp.ibpworkbench.ui.dashboard.listener;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreviewPresenter;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;

public class DeleteConfirmDialogListener implements ConfirmDialog.Listener {

	private static final Logger LOG = LoggerFactory.getLogger(DeleteConfirmDialogListener.class);

	private static final long serialVersionUID = 1L;

	private final NurseryListPreviewPresenter presenter;
	private final Tree treeView;
	private final Integer finalId;
	private final Button.ClickEvent event;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public DeleteConfirmDialogListener(NurseryListPreviewPresenter presenter, Tree treeView, Integer finalId, Button.ClickEvent event) {
		super();
		this.presenter = presenter;
		this.treeView = treeView;
		this.finalId = finalId;
		this.event = event;
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			this.deleteStudy();
		}
	}

	protected boolean deleteStudy() {
		boolean isDeleted = false;
		try {
			DmsProject parent = (DmsProject) this.presenter.getStudyNodeParent(this.finalId);
			this.presenter.deleteNurseryListFolder(this.finalId);
			this.treeView.removeItem(this.treeView.getValue());
			if (parent.getProjectId().intValue() == NurseryListPreview.ROOT_FOLDER) {
				this.treeView.select(NurseryListPreview.NURSERIES_AND_TRIALS);
				this.presenter.processToolbarButtons(NurseryListPreview.NURSERIES_AND_TRIALS);
			} else {
				this.treeView.select(parent.getProjectId());
				this.presenter.processToolbarButtons(parent.getProjectId());
			}
			this.treeView.setImmediate(true);
			isDeleted = true;
		} catch (Exception e) {
			DeleteConfirmDialogListener.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(this.event.getComponent().getWindow(), this.messageSource.getMessage(Message.INVALID_OPERATION),
					e.getMessage());
		}
		return isDeleted;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
