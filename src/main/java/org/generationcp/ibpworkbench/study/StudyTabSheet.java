package org.generationcp.ibpworkbench.study;

import java.util.Iterator;

import org.generationcp.ibpworkbench.cross.study.util.StudyBrowserTabCloseHandler;
import org.generationcp.ibpworkbench.util.Util;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class StudyTabSheet extends TabSheet {

	private static final long serialVersionUID = 1L;


	public void createStudyInfoTab(final int studyId, final String studyName, final StudyBrowserMainLayout mainLayout) {
		final VerticalLayout layout = new VerticalLayout();

		layout.addComponent(new StudyAccordionMenu(studyId, new StudyDetailComponent(studyId), 
				false, false));
		final Tab tab = this.addTab(layout, studyName, null);
		tab.setClosable(true);

		this.setSelectedTab(layout);
		this.setCloseHandler(new StudyBrowserTabCloseHandler(mainLayout));
	}
	
	
	public void renameStudyTab(final String oldName, final String newName) {
		final Tab studyTab = Util.getTabAlreadyExist(this, oldName);
		if (studyTab != null) {
			studyTab.setCaption(newName);
		}
		final Component component = studyTab.getComponent();

		if (component instanceof VerticalLayout) {
			final VerticalLayout layout = (VerticalLayout) component;
			final Iterator<Component> componentIterator = layout.getComponentIterator();
			while (componentIterator.hasNext()) {
				final Component child = componentIterator.next();
				if (child instanceof StudyAccordionMenu) {
					final StudyAccordionMenu accordion = (StudyAccordionMenu) child;
					accordion.updateStudyName(newName);
				}
			}
		}
	}

}
