
package org.generationcp.ibpworkbench.ui.dashboard.listener;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreviewPresenter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;

public class DeleteConfirmDialogListenerTest {

	private SimpleResourceBundleMessageSource messageSource;
	private NurseryListPreviewPresenter presenter;
	private Button.ClickEvent event;
	private Tree treeView;
	private Integer finalId;

	@Before
	public void setUp() {
		this.finalId = 1;
		this.messageSource = Mockito.mock(SimpleResourceBundleMessageSource.class);
		this.presenter = Mockito.mock(NurseryListPreviewPresenter.class);
		this.event = Mockito.mock(Button.ClickEvent.class);
		this.treeView = Mockito.mock(Tree.class);
		Mockito.when(this.treeView.getValue()).thenReturn(this.finalId);
	}

	@Test
	public void testDeleteStudyWithRootFolderAsParent() throws MiddlewareQueryException {
		DmsProject parent = new DmsProject();
		parent.setProjectId(NurseryListPreview.ROOT_FOLDER);
		Mockito.when(this.presenter.getStudyNodeParent(this.finalId)).thenReturn(parent);

		DeleteConfirmDialogListener listener = new DeleteConfirmDialogListener(this.presenter, this.treeView, this.finalId, this.event);
		listener.setMessageSource(this.messageSource);
		boolean isDeleted = listener.deleteStudy();
		Mockito.verify(this.presenter, Mockito.times(1)).deleteNurseryListFolder(this.finalId);
		Mockito.verify(this.treeView, Mockito.times(1)).removeItem(this.finalId);
		Mockito.verify(this.treeView, Mockito.times(1)).select(NurseryListPreview.NURSERIES_AND_TRIALS);
		Mockito.verify(this.presenter, Mockito.times(1)).processToolbarButtons(NurseryListPreview.NURSERIES_AND_TRIALS);
		Assert.assertTrue("Study was deleted successfully", isDeleted);
	}

	@Test
	public void testDeleteStudyWithNonRootFolderAsParent() throws MiddlewareQueryException {
		DmsProject parent = new DmsProject();
		parent.setProjectId(2);
		Mockito.when(this.presenter.getStudyNodeParent(this.finalId)).thenReturn(parent);

		DeleteConfirmDialogListener listener = new DeleteConfirmDialogListener(this.presenter, this.treeView, this.finalId, this.event);
		listener.setMessageSource(this.messageSource);
		boolean isDeleted = listener.deleteStudy();
		Mockito.verify(this.presenter, Mockito.times(1)).deleteNurseryListFolder(this.finalId);
		Mockito.verify(this.treeView, Mockito.times(1)).removeItem(this.finalId);
		Mockito.verify(this.treeView, Mockito.times(1)).select(parent.getProjectId());
		Mockito.verify(this.presenter, Mockito.times(1)).processToolbarButtons(parent.getProjectId());
		Assert.assertTrue("Study was deleted successfully", isDeleted);
	}

	@Test
	public void testDeleteStudyUserConfirmed() {
		DmsProject parent = new DmsProject();
		parent.setProjectId(2);
		Mockito.when(this.presenter.getStudyNodeParent(this.finalId)).thenReturn(parent);

		DeleteConfirmDialogListener listener =
				Mockito.spy(new DeleteConfirmDialogListener(this.presenter, this.treeView, this.finalId, this.event));

		Mockito.doReturn(true).when(listener).deleteStudy();
		listener.onClose(new CustomConfirmDialog(true));
		Mockito.verify(listener, Mockito.times(1)).deleteStudy();
	}

	@Test
	public void testDeleteStudyUserDidNotConfirmed() {
		DmsProject parent = new DmsProject();
		parent.setProjectId(2);
		Mockito.when(this.presenter.getStudyNodeParent(this.finalId)).thenReturn(parent);

		DeleteConfirmDialogListener listener =
				Mockito.spy(new DeleteConfirmDialogListener(this.presenter, this.treeView, this.finalId, this.event));
		Mockito.doReturn(true).when(listener).deleteStudy();
		listener.onClose(new CustomConfirmDialog(false));
		Mockito.verify(listener, Mockito.times(0)).deleteStudy();
	}

	// we will use this class to simulate that the user has confirmed in the dialog
	private class CustomConfirmDialog extends ConfirmDialog {

		private static final long serialVersionUID = 1L;

		CustomConfirmDialog(boolean isConfirmed) {
			super();
			this.setConfirmed(isConfirmed);
		}
	}
}
