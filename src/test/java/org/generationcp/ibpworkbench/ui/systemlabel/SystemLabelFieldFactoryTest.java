package org.generationcp.ibpworkbench.ui.systemlabel;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import org.generationcp.middleware.domain.oms.Term;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by srbatc12 on 7/21/16.
 */
public class SystemLabelFieldFactoryTest {

	private SystemLabelFieldFactory systemLabelFieldFactory = new SystemLabelFieldFactory();

	@Test
	public void testCreateFieldForNamePropertyId() {

		Container container = new BeanItemContainer<Term>(Term.class);

		Field field = systemLabelFieldFactory.createField(container, "", SystemLabelView.NAME, new Table());

		Assert.assertTrue(field instanceof TextField);
		Assert.assertTrue(field.isRequired());
		Assert.assertEquals(200, ((TextField) field).getMaxLength());

	}

}
