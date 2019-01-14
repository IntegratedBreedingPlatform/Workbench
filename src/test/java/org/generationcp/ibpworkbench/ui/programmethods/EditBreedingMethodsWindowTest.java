package org.generationcp.ibpworkbench.ui.programmethods;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EditBreedingMethodsWindowTest {

	public static final int MID = 123;
	public static final String SUCCESS = "Success";
	public static final String ERROR_MESSAGE = "Error Message";
	@Mock
	private BreedingMethodForm breedingMethodForm;

	@Mock
	private BreedingMethodTracker breedingMethodTracker;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ProgramMethodsPresenter presenter;

	@Mock
	private Window window;

	@Mock
	private Window parent;

	@Mock
	private Window parentWindow;

	@Mock
	private Component component;

	@Mock
	private Button.ClickEvent clickEvent;

	private EditBreedingMethodsWindow editBreedingMethodsWindow;

	private MethodView methodView;

	@Before
	public void init() {

		this.methodView = new MethodView();
		this.methodView.setMid(MID);
		this.editBreedingMethodsWindow = new EditBreedingMethodsWindow(this.presenter, this.methodView);
		this.editBreedingMethodsWindow.setBreedingMethodForm(this.breedingMethodForm);
		this.editBreedingMethodsWindow.setBreedingMethodTracker(this.breedingMethodTracker);
		this.editBreedingMethodsWindow.setMessageSource(this.messageSource);
		this.editBreedingMethodsWindow.setParent(this.parent);

		when(this.messageSource.getMessage(Message.SUCCESS)).thenReturn(SUCCESS);

		when(this.window.getParent()).thenReturn(this.parent);
		when(this.parent.getWindow()).thenReturn(this.parentWindow);
	}

	@Test
	public void testEditBreedingMethodButtonListenerSuccess() {

		final MethodView methodViewFromBreedingMethodForm = new MethodView();
		methodViewFromBreedingMethodForm.setMname("New Method Name");
		methodViewFromBreedingMethodForm.setMcode("New Method Code");
		methodViewFromBreedingMethodForm.setMtype("New Method Type");

		final Map<Integer, MethodView> projectBreedingMethodData = mock(new HashMap<Integer, MethodView>().getClass());
		final Set<String> uniqueBreedingMethods = mock(new HashSet<String>().getClass());

		doNothing().when(this.breedingMethodForm).commit();
		when(this.clickEvent.getComponent()).thenReturn(this.component);
		when(this.component.getWindow()).thenReturn(this.window);
		when(this.breedingMethodTracker.getProjectBreedingMethodData()).thenReturn(projectBreedingMethodData);
		when(this.breedingMethodTracker.getUniqueBreedingMethods()).thenReturn(uniqueBreedingMethods);
		when(this.breedingMethodForm.getItemDataSource()).thenReturn(new BeanItem<MethodView>(methodViewFromBreedingMethodForm));
		when(this.presenter.editBreedingMethod(methodViewFromBreedingMethodForm)).thenReturn(methodViewFromBreedingMethodForm);

		final EditBreedingMethodsWindow.EditBreedingMethodButtonListener listener =
				this.editBreedingMethodsWindow.new EditBreedingMethodButtonListener();

		listener.buttonClick(this.clickEvent);

		verify(projectBreedingMethodData).remove(this.methodView.getMid());
		verify(uniqueBreedingMethods).remove(this.methodView);

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);

		verify(this.parentWindow).showNotification(captor.capture());

		final Window.Notification notification = captor.getValue();

		assertEquals(SUCCESS, notification.getCaption());
		assertEquals("</br>" + methodViewFromBreedingMethodForm.getMname()
				+ EditBreedingMethodsWindow.EditBreedingMethodButtonListener.BREEDING_METHOD_IS_UPDATED, notification.getDescription());

		verify(this.parent).removeWindow(this.editBreedingMethodsWindow);
	}

	@Test
	public void testEditBreedingMethodButtonListenerGenerationAdvancementTypeIsNotSelected() {

		final MethodView methodViewFromBreedingMethodForm = new MethodView();
		methodViewFromBreedingMethodForm.setMname("New Method Name");
		methodViewFromBreedingMethodForm.setMcode("New Method Code");
		methodViewFromBreedingMethodForm.setMtype(null);

		final Map<Integer, MethodView> projectBreedingMethodData = mock(new HashMap<Integer, MethodView>().getClass());
		final Set<String> uniqueBreedingMethods = mock(new HashSet<String>().getClass());

		doNothing().when(this.breedingMethodForm).commit();
		when(this.clickEvent.getComponent()).thenReturn(this.component);
		when(this.component.getWindow()).thenReturn(this.window);
		when(this.breedingMethodTracker.getProjectBreedingMethodData()).thenReturn(projectBreedingMethodData);
		when(this.breedingMethodTracker.getUniqueBreedingMethods()).thenReturn(uniqueBreedingMethods);
		when(this.breedingMethodForm.getItemDataSource()).thenReturn(new BeanItem<MethodView>(methodViewFromBreedingMethodForm));


		final EditBreedingMethodsWindow.EditBreedingMethodButtonListener listener =
				this.editBreedingMethodsWindow.new EditBreedingMethodButtonListener();

		listener.buttonClick(this.clickEvent);

		verify(projectBreedingMethodData).remove(this.methodView.getMid());
		verify(uniqueBreedingMethods).remove(this.methodView);

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);

		verify(this.window).showNotification(captor.capture());

		final Window.Notification notification = captor.getValue();

		assertEquals("Invalid Input", notification.getCaption());
		assertEquals("</br>" + EditBreedingMethodsWindow.EditBreedingMethodButtonListener.PLEASE_SELECT_A_GENERATION_ADVANCEMENT_TYPE,
				notification.getDescription());

		verify(this.parent, Mockito.never()).removeWindow(this.editBreedingMethodsWindow);
	}

	@Test
	public void testEditBreedingMethodButtonEmptyValueException() {

		doThrow(new Validator.EmptyValueException(ERROR_MESSAGE)).when(this.breedingMethodForm).commit();
		when(this.clickEvent.getComponent()).thenReturn(this.component);
		when(this.component.getWindow()).thenReturn(this.window);

		final EditBreedingMethodsWindow.EditBreedingMethodButtonListener listener =
				this.editBreedingMethodsWindow.new EditBreedingMethodButtonListener();

		listener.buttonClick(this.clickEvent);

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);

		verify(this.window).showNotification(captor.capture());

		final Window.Notification notification = captor.getValue();

		assertEquals("Invalid Input", notification.getCaption());
		assertEquals("</br>" + ERROR_MESSAGE, notification.getDescription());

		verify(this.parent, Mockito.never()).removeWindow(this.editBreedingMethodsWindow);

	}

	@Test
	public void testEditBreedingMethodButtonInvalidValueException() {

		doThrow(new Validator.InvalidValueException(ERROR_MESSAGE)).when(this.breedingMethodForm).commit();
		when(this.clickEvent.getComponent()).thenReturn(this.component);
		when(this.component.getWindow()).thenReturn(this.window);

		final EditBreedingMethodsWindow.EditBreedingMethodButtonListener listener =
				this.editBreedingMethodsWindow.new EditBreedingMethodButtonListener();

		listener.buttonClick(this.clickEvent);

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);

		verify(this.window).showNotification(captor.capture());

		final Window.Notification notification = captor.getValue();

		assertEquals("Invalid Input", notification.getCaption());
		assertEquals("</br>" + ERROR_MESSAGE, notification.getDescription());

		verify(this.parent, Mockito.never()).removeWindow(this.editBreedingMethodsWindow);

	}

}
