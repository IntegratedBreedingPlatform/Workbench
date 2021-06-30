
package org.generationcp.breeding.manager.listmanager;

import org.generationcp.breeding.manager.listmanager.util.FillWithOption;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddedColumnsMapperTest {

	private static final int ATTRIBUTE_TYPE_ID2 = 2;
	private static final int ATTRIBUTE_TYPE_ID1 = 1;
	private static final String ATTRIBUTE_TYPE_NAME2 = "New Passport Type";
	private static final String ATTRIBUTE_TYPE_NAME1 = "Ipstat";
	private static final String ATTRIBUTE_TYPE_CODE2 = "NEW_PAZZPORT";
	private static final String ATTRIBUTE_TYPE_CODE1 = "Ipstat";
	private static final String NAMETYPE_NAME3 = "Line Accession Name";
	private static final String NAMETYPE_CODE3 = "LACCNM";
	private static final int NAMETYPE_ID3 = 3;
	private static final int NAME_TYPE_ID2 = 2;
	private static final int NAME_TYPE_ID1 = 1;
	private static final String NAME_TYPE_CODE2 = "COOL_NAME";
	private static final String NAME_TYPE_CODE1 = "COOLER_NAME";
	private static final String NAME_TYPE_NAME2 = "Some Cool Name";
	private static final String NAME_TYPE_NAME1 = "Some Cooler Name";

	private static final String[] STANDARD_COLUMNS =
			{ColumnLabels.GID.getName(), ColumnLabels.DESIGNATION.getName(), ColumnLabels.SEED_SOURCE.getName(),
					ColumnLabels.ENTRY_CODE.getName(), ColumnLabels.GROUP_ID.getName(), ColumnLabels.STOCKID.getName()};
	
	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private GermplasmColumnValuesGenerator valuesGenerator;

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@InjectMocks
	private AddedColumnsMapper addedColumnsMapper;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.addedColumnsMapper.setValuesGenerator(this.valuesGenerator);
		this.addedColumnsMapper.setOntologyVariableDataManager(this.ontologyVariableDataManager);
		this.addedColumnsMapper.setGermplasmListManager(this.germplasmListManager);

		Mockito.doReturn(this.getAttributeVariables()).when(this.ontologyVariableDataManager).getWithFilter(Mockito.any());
		Mockito.doReturn(this.getNameTypes()).when(this.germplasmListManager).getGermplasmNameTypes();
		
	}

	@Test
	public void testGenerateValuesForAddedColumnsWhenNoAddedColumns() {
		this.addedColumnsMapper.generateValuesForAddedColumns(STANDARD_COLUMNS);

		Mockito.verifyZeroInteractions(this.valuesGenerator);
	}

	@Test
	public void testGenerateValuesForAddedColumnsWhenAllPredefinedColumnsAdded() {
		final List<String> columns = new ArrayList<>(Arrays.asList(STANDARD_COLUMNS));
		columns.addAll(ColumnLabels.getAddableGermplasmColumns());
		this.addedColumnsMapper.generateValuesForAddedColumns(columns.toArray());
		
		Mockito.verify(this.valuesGenerator).setPreferredNameColumnValues(ColumnLabels.PREFERRED_NAME.getName());
		Mockito.verify(this.valuesGenerator).setPreferredIdColumnValues(ColumnLabels.PREFERRED_ID.getName());
		Mockito.verify(this.valuesGenerator).setGermplasmDateColumnValues(ColumnLabels.GERMPLASM_DATE.getName());
		Mockito.verify(this.valuesGenerator).setLocationNameColumnValues(ColumnLabels.GERMPLASM_LOCATION.getName());
		Mockito.verify(this.valuesGenerator).setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_NAME.getName(),
				FillWithOption.FILL_WITH_BREEDING_METHOD_NAME);
		Mockito.verify(this.valuesGenerator).setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName(),
				FillWithOption.FILL_WITH_BREEDING_METHOD_ABBREV);
		Mockito.verify(this.valuesGenerator).setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_NUMBER.getName(),
				FillWithOption.FILL_WITH_BREEDING_METHOD_NUMBER);
		Mockito.verify(this.valuesGenerator).setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_GROUP.getName(),
				FillWithOption.FILL_WITH_BREEDING_METHOD_GROUP);
		Mockito.verify(this.valuesGenerator).setCrossFemaleInfoColumnValues(ColumnLabels.FGID.getName(),
				FillWithOption.FILL_WITH_CROSS_FEMALE_GID);
		Mockito.verify(this.valuesGenerator).setCrossFemaleInfoColumnValues(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName(),
				FillWithOption.FILL_WITH_CROSS_FEMALE_NAME);
		Mockito.verify(this.valuesGenerator).setCrossMaleGIDColumnValues(ColumnLabels.MGID.getName());
		Mockito.verify(this.valuesGenerator).setCrossMalePrefNameColumnValues(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName());
		Mockito.verify(this.valuesGenerator).setGroupSourceGidColumnValues(ColumnLabels.GROUP_SOURCE_GID.getName());
		Mockito.verify(this.valuesGenerator).setGroupSourcePreferredNameColumnValues(ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName());
		Mockito.verify(this.valuesGenerator).setImmediateSourceGidColumnValues(ColumnLabels.IMMEDIATE_SOURCE_GID.getName());
		Mockito.verify(this.valuesGenerator).setImmediateSourcePreferredNameColumnValues(ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName());
		Mockito.verify(this.valuesGenerator, Mockito.never()).fillWithAttribute(Matchers.anyInt(), Matchers.anyString());
		Mockito.verify(this.valuesGenerator, Mockito.never()).fillWithGermplasmName(Matchers.anyInt(), Matchers.anyString());
	}
	
	@Test
	public void testGenerateValuesForAddedColumnsWhenAttributeColumnsAdded() {
		final List<String> columns = new ArrayList<>(Arrays.asList(STANDARD_COLUMNS));
		columns.add(ATTRIBUTE_TYPE_CODE1);
		columns.add(ATTRIBUTE_TYPE_CODE2);
		
		this.addedColumnsMapper.generateValuesForAddedColumns(columns.toArray());
		
		// Check that attribute type columns are capitalized
		Mockito.verify(this.valuesGenerator).fillWithAttribute(ATTRIBUTE_TYPE_ID1, ATTRIBUTE_TYPE_CODE1.toUpperCase());
		Mockito.verify(this.valuesGenerator).fillWithAttribute(ATTRIBUTE_TYPE_ID2, ATTRIBUTE_TYPE_CODE2.toUpperCase());
	}
	
	@Test
	public void testGenerateValuesForAddedColumnsWhenGermplasmNameColumnsAdded() {
		final List<String> columns = new ArrayList<>(Arrays.asList(STANDARD_COLUMNS));
		columns.add(NAME_TYPE_NAME1);
		columns.add(NAME_TYPE_NAME2);
		
		this.addedColumnsMapper.generateValuesForAddedColumns(columns.toArray());
		
		// Check that name type columns are capitalized
		Mockito.verify(this.valuesGenerator).fillWithGermplasmName(NAME_TYPE_ID1, NAME_TYPE_NAME1.toUpperCase());
		Mockito.verify(this.valuesGenerator).fillWithGermplasmName(NAME_TYPE_ID2, NAME_TYPE_NAME2.toUpperCase());
	}
	
	@Test
	public void testGetAttributeTypesMap() {
		final List<Variable> expectedAttributeVariables = this.getAttributeVariables();
		final Map<String, Integer> attributeTypesMap = this.addedColumnsMapper.getAllAttributeTypesMap();
		for (final Variable variable : expectedAttributeVariables) {
			final String name = variable.getName().toUpperCase();
			Assert.assertTrue(attributeTypesMap.containsKey(name));
			Assert.assertEquals(Integer.valueOf(variable.getId()), attributeTypesMap.get(name));
		}
	}
	
	@Test
	public void testGetNameTypesMap() {
		final List<UserDefinedField> expectedNameTypes = this.getNameTypes();
		final Map<String, Integer> nameTypesMap = this.addedColumnsMapper.getAllNameTypesMap();
		for (final UserDefinedField nameType : expectedNameTypes) {
			final String fieldName = nameType.getFname().toUpperCase();
			Assert.assertTrue(nameTypesMap.containsKey(fieldName));
			Assert.assertEquals(nameType.getFldno(), nameTypesMap.get(fieldName));
		}
	}

	private List<Variable> getAttributeVariables() {
		final Variable variable1 = new Variable();
		variable1.setId(ATTRIBUTE_TYPE_ID1);
		variable1.setName(AddedColumnsMapperTest.ATTRIBUTE_TYPE_CODE1);
		variable1.setDefinition(AddedColumnsMapperTest.ATTRIBUTE_TYPE_NAME1);

		final Variable variable2 = new Variable();
		variable2.setId(ATTRIBUTE_TYPE_ID2);
		variable2.setName(AddedColumnsMapperTest.ATTRIBUTE_TYPE_CODE2);
		variable2.setDefinition(AddedColumnsMapperTest.ATTRIBUTE_TYPE_NAME2);

		final Variable variable3 = new Variable();
		variable3.setId(3);
		variable3.setName("Grow");
		variable3.setDefinition("Grower");

		return Arrays.asList(variable1, variable2, variable3);
	}
	
	private List<UserDefinedField> getNameTypes() {
		final UserDefinedField type1 = new UserDefinedField(AddedColumnsMapperTest.NAME_TYPE_ID1);
		type1.setFname(AddedColumnsMapperTest.NAME_TYPE_NAME1);
		type1.setFcode(AddedColumnsMapperTest.NAME_TYPE_CODE1);
		final UserDefinedField type2 = new UserDefinedField(AddedColumnsMapperTest.NAME_TYPE_ID2);
		type2.setFname(AddedColumnsMapperTest.NAME_TYPE_NAME2);
		type2.setFcode(AddedColumnsMapperTest.NAME_TYPE_CODE2);
		final UserDefinedField type3 = new UserDefinedField(NAMETYPE_ID3);
		type3.setFname(NAMETYPE_NAME3);
		type3.setFcode(NAMETYPE_CODE3);
		return Arrays.asList(type1, type2, type3);
	}

}
