package org.generationcp.ibpworkbench.ui.systemlabel;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import junit.framework.Assert;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SystemLabelViewTest {

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private Component parent;

	@Mock
	private Window window;

	@InjectMocks
	private SystemLabelView view = new SystemLabelView();

	@Before
	public void init() {

		Mockito.when(parent.getWindow()).thenReturn(window);
		view.setParent(parent);
	}

	@Test
	public void testInitializeTable() {

		Table table = new Table();
		view.initializeTable(table);

		Assert.assertTrue(table.getContainerDataSource() instanceof BeanItemContainer);
		Assert.assertTrue(table.isEditable());
		Assert.assertTrue(table.isImmediate());
		Assert.assertTrue(table.getTableFieldFactory() instanceof SystemLabelFieldFactory);
		Assert.assertNotNull(table.getColumnGenerator(SystemLabelView.ID));
		Assert.assertNotNull(table.getColumnGenerator(SystemLabelView.DEFINITION));

	}

	@Test
	public void testShowSuccessMessage() {

		view.instantiateComponents();
		view.showSaveSuccessMessage();

		Mockito.verify(messageSource).getMessage(Message.SUCCESS);
		Mockito.verify(messageSource).getMessage(Message.SYSTEM_LABEL_UPDATE_SUCCESS);
		Mockito.verify(window).showNotification(Mockito.any(Window.Notification.class));

	}

	@Test
	public void testShowErrorMessage() {

		view.instantiateComponents();
		view.showSaveErrorMessage();

		Mockito.verify(messageSource).getMessage(Message.ERROR);
		Mockito.verify(messageSource).getMessage(Message.SYSTEM_LABEL_UPDATE_ERROR);
		Mockito.verify(window).showNotification(Mockito.any(Window.Notification.class));

	}


}
