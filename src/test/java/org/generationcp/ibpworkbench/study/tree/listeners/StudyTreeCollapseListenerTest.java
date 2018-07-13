package org.generationcp.ibpworkbench.study.tree.listeners;

import org.generationcp.ibpworkbench.study.tree.StudyTree;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Tree.CollapseEvent;

public class StudyTreeCollapseListenerTest {
	
	private static final int ITEM_ID = 100;

	@Mock
	private StudyTree studyTree;
	
	@Mock
	private CollapseEvent event;
	
	@InjectMocks
	private StudyTreeCollapseListener collapseListener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(ITEM_ID).when(this.event).getItemId();
	}
	
	@Test
	public void testCollapse() {
		this.collapseListener.nodeCollapse(event);
		Mockito.verify(this.studyTree).selectItem(ITEM_ID);
	}
	

}
