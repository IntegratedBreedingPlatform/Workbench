
package org.generationcp.ibpworkbench.ui;

import java.util.Iterator;
import java.util.Properties;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import junit.framework.Assert;

public class WorkbenchMainViewTest {

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private SessionData sessionData;

	@Mock
	private Properties workbenchProperties;

	@InjectMocks
	private WorkbenchMainView workbenchMainView;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		final Person person = new Person("A", "B", "C");
		person.setEmail("a@leafnode.io");
		final User currentUser = new User(1);
		currentUser.setPerson(person);
		Mockito.doReturn(currentUser).when(this.sessionData).getUserData();

		this.workbenchMainView.initializeComponents();
		this.workbenchMainView.initializeLayout();
	}

	@Test
	public void testHeaderLayoutWhenDashboardIsShowing() {
		// verify header already as dashboard is default content of main window
		this.verifyHeaderLayoutWHenShowingDashboard();
	}

	@Test
	public void testHeaderLayoutWhenDashboardIsNotShowing() {
		this.workbenchMainView.showContent(new VerticalLayout());

		this.verifyHeaderLayoutWhenNotShowingDashboard();
	}

	private void verifyHeaderLayoutWHenShowingDashboard() {
		final HorizontalLayout workbenchHeaderLayout = this.workbenchMainView.getWorkbenchHeaderLayout();
		Assert.assertEquals(8, workbenchHeaderLayout.getComponentCount());

		final Iterator<Component> componentIterator = workbenchHeaderLayout.getComponentIterator();
		boolean addProgramButtonShown = false;
		while (componentIterator.hasNext()) {
			final Component component = componentIterator.next();
			// Verify that button to toggle sidebar is not showing
			if (component.equals(this.workbenchMainView.getSidebarToggleButton())) {
				Assert.fail("Toggle button to show sidebar should be hidden but was not.");

				// Verify "My Programs" button is not showing
			} else if (component.equals(this.workbenchMainView.getHomeButton())) {
				Assert.fail("My Programs button should be hidden but was not.");

			} else if (component.equals(this.workbenchMainView.getAddProgramButton())) {
				addProgramButtonShown = true;
			}
		}

		// Verify "Add Program" button is showing
		Assert.assertTrue(addProgramButtonShown);
	}

	private void verifyHeaderLayoutWhenNotShowingDashboard() {
		final HorizontalLayout workbenchHeaderLayout = this.workbenchMainView.getWorkbenchHeaderLayout();
		Assert.assertEquals(9, workbenchHeaderLayout.getComponentCount());

		final Iterator<Component> componentIterator = workbenchHeaderLayout.getComponentIterator();
		boolean toggleSidebarButtonShowing = false;
		boolean myProgramsButtonShowing = false;
		while (componentIterator.hasNext()) {
			final Component component = componentIterator.next();

			if (component.equals(this.workbenchMainView.getSidebarToggleButton())) {
				toggleSidebarButtonShowing = true;
			} else if (component.equals(this.workbenchMainView.getHomeButton())) {
				myProgramsButtonShowing = true;
				// Verify that Add Program button should not be showing - it's only appears on Dashboard page
			} else if (component.equals(this.workbenchMainView.getAddProgramButton())) {
				Assert.fail("Add Program button should be hidden but was not.");
			}
		}

		// Verify "My Programs" button is showing
		Assert.assertTrue(myProgramsButtonShowing);
		// Verify that button to toggle sidebar is showing
		Assert.assertTrue(toggleSidebarButtonShowing);
	}
}
