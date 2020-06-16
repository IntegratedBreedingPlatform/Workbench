
package org.generationcp.breeding.manager.listimport;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import org.generationcp.breeding.manager.data.initializer.ImportedGermplasmListDataInitializer;
import org.generationcp.breeding.manager.listimport.util.GermplasmListUploader;
import org.generationcp.breeding.manager.pojos.ImportedGermplasm;
import org.generationcp.breeding.manager.pojos.ImportedGermplasmList;
import org.generationcp.breeding.manager.validator.ShowNameHandlingPopUpValidator;
import org.generationcp.commons.workbook.generator.RowColumnType;
import org.generationcp.middleware.components.validator.ErrorCollection;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class GermplasmImportFileComponentTest {

	public static final String DUMMY_MESSAGE = "DUMMY_MESSAGE";
	private GermplasmImportFileComponent importFileComponent;

	private Window importWindow;

	@Mock
	private GermplasmImportMain importMain;
	@Mock
	private GermplasmDataManager germplasmDataManager;
	@Mock
	private GermplasmListUploader germplasmListUploader;

	@Mock
	private ShowNameHandlingPopUpValidator showNameHandlingPopUpValidator;

	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(this);

		this.importFileComponent = new GermplasmImportFileComponent(this.importMain);
		this.importFileComponent.setGermplasmListUploader(this.germplasmListUploader);
		this.importFileComponent.setShowNameHandlingPopUpValidationRule(this.showNameHandlingPopUpValidator);

		this.importWindow = new Window();
		Mockito.doReturn(this.importWindow).when(this.importMain).getWindow();
	}

	@Test
	public void testCancelActionFromMainImportTool() {
		this.importFileComponent.cancelButtonAction();
		Mockito.verify(this.importMain).reset();
	}

	private List<UserDefinedField> createUserDefinedFieldsForNameType() {
		final List<UserDefinedField> validNameTypes = new ArrayList<UserDefinedField>();
		validNameTypes.add(new UserDefinedField(5, "NAMES", "NAME", "DRVNM", "DERIVATIVE NAMES", "", "", 0, 0, 0, 0));
		return validNameTypes;
	}

	private ImportedGermplasmList initImportedGermplasmList(final boolean withNameFactors) {
		final ImportedGermplasmList importedGermplasmList =
				ImportedGermplasmListDataInitializer.createImportedGermplasmList(10, withNameFactors);

		Mockito.doReturn(this.createUserDefinedFieldsForNameType()).when(this.germplasmDataManager)
				.getUserDefinedFieldByFieldTableNameAndType(RowColumnType.NAME_TYPES.getFtable(), RowColumnType.NAME_TYPES.getFtype());
		Mockito.doReturn(importedGermplasmList).when(this.germplasmListUploader).getImportedGermplasmList();

		return importedGermplasmList;
	}

	@Test
	public void testProceedToNextScreenWhenThereIsImportedNameFactor() {
		final ImportedGermplasmList importedGermplasmList = this.initImportedGermplasmList(true);
		final HorizontalLayout parent = Mockito.mock(HorizontalLayout.class);
		this.importFileComponent.setParent(parent);
		final Window mockWindow = Mockito.mock(Window.class);
		Mockito.doReturn(mockWindow).when(parent).getWindow();

		final List<ImportedGermplasm> list = importedGermplasmList.getImportedGermplasm();
		final ErrorCollection success = new ErrorCollection();
		Mockito.when(this.showNameHandlingPopUpValidator.validate(list)).thenReturn(success);
		Mockito.when(this.germplasmListUploader.getNameFactors()).thenReturn(NameHandlingDialogTest.NAME_FACTORS);

		// Method to test
		this.importFileComponent.proceedToNextScreen();

		// Verify that new NameHandingDialog window was created and added to parent window
		final ArgumentCaptor<Window> windowCaptor = ArgumentCaptor.forClass(Window.class);
		Mockito.verify(mockWindow, Mockito.times(1)).addWindow(windowCaptor.capture());
		Assert.assertTrue(windowCaptor.getValue() instanceof NameHandlingDialog);
		final NameHandlingDialog nameHandlingDialog = (NameHandlingDialog) windowCaptor.getValue();
		Assert.assertEquals(NameHandlingDialogTest.NAME_FACTORS, nameHandlingDialog.getImportedNameFactors());
		Assert.assertEquals(this.importFileComponent, nameHandlingDialog.getSource());
		Mockito.verify(this.importMain, Mockito.never()).nextStep();
	}

	@Test
	public void testProceedToNextScreenWhenThereIsNoImportedNameFactor() {
		final ImportedGermplasmList importedGermplasmList = this.initImportedGermplasmList(false);
		final List<ImportedGermplasm> list = importedGermplasmList.getImportedGermplasm();
		final ErrorCollection error = new ErrorCollection();
		error.add(GermplasmImportFileComponentTest.DUMMY_MESSAGE);
		Mockito.when(this.showNameHandlingPopUpValidator.validate(list)).thenReturn(error);

		this.importFileComponent.proceedToNextScreen();

		Mockito.verify(this.importMain, Mockito.times(1)).nextStep();
		Mockito.verify(this.germplasmListUploader, Mockito.never()).getNameFactors();
	}
}
