package org.generationcp.ibpworkbench.ui.dashboard.listener;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreviewPresenter;
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
	public void setUp(){
		finalId = 1;
		messageSource = Mockito.mock(SimpleResourceBundleMessageSource.class);
		presenter = Mockito.mock(NurseryListPreviewPresenter.class);
		event = Mockito.mock(Button.ClickEvent.class); 
		treeView = Mockito.mock(Tree.class); 
		Mockito.when(treeView.getValue()).thenReturn(finalId);
	}
	
	@Test
	public void testDeleteStudyWithRootFolderAsParent(){
		DmsProject parent = new DmsProject();
		parent.setProjectId(NurseryListPreview.ROOT_FOLDER);
		Mockito.when(presenter.getStudyNodeParent(finalId)).thenReturn(parent);
		
		DeleteConfirmDialogListener listener = new DeleteConfirmDialogListener(presenter, treeView, finalId, event);
		listener.setMessageSource(messageSource);
		boolean isDeleted = listener.deleteStudy();
		Mockito.verify(presenter, Mockito.times(1)).deleteNurseryListFolder(finalId);
		Mockito.verify(treeView, Mockito.times(1)).removeItem(finalId);
		Mockito.verify(treeView, Mockito.times(1)).select(NurseryListPreview.MY_STUDIES);
		Mockito.verify(presenter, Mockito.times(1)).processToolbarButtons(NurseryListPreview.MY_STUDIES);		
        Assert.assertTrue("Study was deleted successfully", isDeleted);
	}
	
	@Test
	public void testDeleteStudyWithNonRootFolderAsParent(){
		DmsProject parent = new DmsProject();
		parent.setProjectId(2);
		Mockito.when(presenter.getStudyNodeParent(finalId)).thenReturn(parent);
		
		DeleteConfirmDialogListener listener = new DeleteConfirmDialogListener(presenter, treeView, finalId, event);
		listener.setMessageSource(messageSource);
		boolean isDeleted = listener.deleteStudy();
		Mockito.verify(presenter, Mockito.times(1)).deleteNurseryListFolder(finalId);
		Mockito.verify(treeView, Mockito.times(1)).removeItem(finalId);
		Mockito.verify(treeView, Mockito.times(1)).select(parent.getProjectId());
		Mockito.verify(presenter, Mockito.times(1)).processToolbarButtons(parent.getProjectId());
		Assert.assertTrue("Study was deleted successfully", isDeleted);
	}
}
