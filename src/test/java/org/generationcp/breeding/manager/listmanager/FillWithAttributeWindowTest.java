
package org.generationcp.breeding.manager.listmanager;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import org.generationcp.breeding.manager.containers.GermplasmQuery;
import org.generationcp.breeding.manager.listmanager.api.AddColumnSource;
import org.generationcp.breeding.manager.listmanager.listeners.FillWithAttributeButtonClickListener;
import org.generationcp.middleware.api.germplasm.GermplasmAttributeService;
import org.generationcp.middleware.domain.ontology.Variable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FillWithAttributeWindowTest {

	private static final int ATTRIBUTE_VARIABLE_ID1 = 1;
	private static final int ATTRIBUTE_VARIABLE_ID2 = 2;
	private static final int ATTRIBUTE_VARIABLE_ID3 = 3;

	private static final String ATTRIBUTE_VARIABLE_NAME1 = "Ipstat";
	private static final String ATTRIBUTE_VARIABLE_NAME2 = "NEW_PAZZPORT";
	private static final String ATTRIBUTE_VARIABLE_NAME3 = "Grow";

	private static final String ATTRIBUTE_VARIABLE_DEFINITION1 = "Ip Status";
	private static final String ATTRIBUTE_VARIABLE_DEFINITION2 = "New Passport Type";
	private static final String ATTRIBUTE_VARIABLE_DEFINITION3 = "Grower";

	private static final List<Integer> GID_LIST = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

	@Mock
	private AddColumnSource addColumnSource;

	@Mock
	private GermplasmAttributeService germplasmAttributeService;

	@InjectMocks
	private final FillWithAttributeWindow fillWithAttributeWindow = new FillWithAttributeWindow(this.addColumnSource,
			GermplasmQuery.GID_REF_PROPERTY, false);

	private List<Variable> attributeTypes;

	@Before
	public void setup() {

		Mockito.doReturn(FillWithAttributeWindowTest.GID_LIST).when(this.addColumnSource).getAllGids();
		this.attributeTypes = this.getAttributeTypesVariables();
		Mockito.doReturn(this.attributeTypes).when(this.germplasmAttributeService)
			.getGermplasmAttributeVariables(Mockito.any(), Mockito.isNull());
	}

	@Test
	public void testPopulateAttributeTypes() {
		this.fillWithAttributeWindow.instantiateComponents();
		this.fillWithAttributeWindow.initializeValues();

		Mockito.verify(this.addColumnSource).getAllGids();
		Mockito.verify(this.germplasmAttributeService)
			.getGermplasmAttributeVariables(Mockito.any(), Mockito.isNull());
		final ComboBox attributeTypesComboBox = this.fillWithAttributeWindow.getAttributeBox();
		Assert.assertNotNull(attributeTypesComboBox);
		Assert.assertEquals(3, attributeTypesComboBox.size());
		for (final Variable variable : this.attributeTypes) {
			final Integer id = variable.getId();
			Assert.assertNotNull(attributeTypesComboBox.getItem(id));
			Assert.assertEquals(variable.getName(), attributeTypesComboBox.getItemCaption(id));
		}
	}
	
	@Test
	public void testPopulateAttributeTypesWithAddedColumnAlready() {
		Mockito.doReturn(true).when(this.addColumnSource).columnExists(ATTRIBUTE_VARIABLE_NAME3.toUpperCase());
		this.fillWithAttributeWindow.instantiateComponents();
		this.fillWithAttributeWindow.initializeValues();

		Mockito.verify(this.addColumnSource).getAllGids();
		Mockito.verify(this.germplasmAttributeService)
			.getGermplasmAttributeVariables(Mockito.any(), Mockito.isNull());
		final ComboBox attributeTypesComboBox = this.fillWithAttributeWindow.getAttributeBox();
		Assert.assertNotNull(attributeTypesComboBox);
		Assert.assertEquals(2, attributeTypesComboBox.size());
		final List<Variable> subList = this.attributeTypes.subList(0, 2);
		for (final Variable attributeType : subList) {
			final Integer id = attributeType.getId();
			Assert.assertNotNull(attributeTypesComboBox.getItem(id));
			Assert.assertEquals(attributeType.getName(), attributeTypesComboBox.getItemCaption(id));
		}
	}


	@Test
	public void testAddListeners() {
		this.fillWithAttributeWindow.instantiateComponents();
		this.fillWithAttributeWindow.addListeners();

		final Collection<?> clickListeners = this.fillWithAttributeWindow.getOkButton().getListeners(ClickEvent.class);
		Assert.assertNotNull(clickListeners);
		Assert.assertEquals(1, clickListeners.size());
		Assert.assertTrue(clickListeners.iterator().next() instanceof FillWithAttributeButtonClickListener);
	}

	private List<Variable> getAttributeTypesVariables() {
		final Variable variable1 = new Variable();
		variable1.setId(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_ID1);
		variable1.setName(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_NAME1);
		variable1.setDefinition(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_DEFINITION1);

		final Variable variable2 = new Variable();
		variable2.setId(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_ID2);
		variable2.setName(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_NAME2);
		variable2.setDefinition(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_DEFINITION2);

		final Variable variable3 = new Variable();
		variable3.setId(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_ID3);
		variable3.setName(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_NAME3);
		variable3.setDefinition(FillWithAttributeWindowTest.ATTRIBUTE_VARIABLE_DEFINITION3);

		return Arrays.asList(variable1, variable2, variable3);
	}

}
