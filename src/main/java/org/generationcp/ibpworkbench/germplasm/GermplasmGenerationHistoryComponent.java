/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.germplasm;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.breeding.manager.listmanager.GermplasmDetailsUrlService;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.germplasm.containers.GermplasmIndexContainer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class GermplasmGenerationHistoryComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	GermplasmIndexContainer dataIndexContainer;
	GermplasmDetailModel gDetailModel;

	private Table generationHistoryTable;
	private Label noDataAvailableLabel;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private GermplasmDetailsUrlService germplasmDetailsUrlService;

	public GermplasmGenerationHistoryComponent(final GermplasmIndexContainer dataIndexContainer, final GermplasmDetailModel gDetailModel) {
		this.dataIndexContainer = dataIndexContainer;
		this.gDetailModel = gDetailModel;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
		this.layoutComponents();
	}

	private void initializeComponents() {
		final IndexedContainer generationHistory = this.dataIndexContainer.getGermplasmGenerationHistory(this.gDetailModel);

		if (generationHistory.getItemIds().isEmpty()) {
			this.noDataAvailableLabel = new Label("There is no Generation History Information for this gemrplasm.");
		} else {
			this.generationHistoryTable = new Table();
			this.generationHistoryTable.setContainerDataSource(generationHistory);
			if (generationHistory.getItemIds().size() < 10) {
				this.generationHistoryTable.setPageLength(generationHistory.getItemIds().size());
			} else {
				this.generationHistoryTable.setPageLength(10);
			}
			this.generationHistoryTable.setSelectable(true);
			this.generationHistoryTable.setMultiSelect(false);
			this.generationHistoryTable.setImmediate(true); // react at once when something is selected turn on column reordering and
			// collapsing
			this.generationHistoryTable.setColumnReorderingAllowed(true);
			this.generationHistoryTable.setColumnCollapsingAllowed(true);
			this.generationHistoryTable.setColumnHeaders(new String[] {
				this.messageSource.getMessage(Message.GID_LABEL),
				this.messageSource.getMessage(Message.PREFNAME_LABEL)});

			this.generationHistoryTable.addGeneratedColumn("gid", new Table.ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final String gid = source.getItem(itemId).getItemProperty(columnId).getValue().toString();
					final Link link =
						new Link(gid, GermplasmGenerationHistoryComponent.this.germplasmDetailsUrlService
							.getExternalResource(Integer.parseInt(gid), false));
					link.setTargetName("_blank");
					return link;
				}
			});
		}
	}

	private void layoutComponents() {
		if (this.generationHistoryTable != null) {
			this.addComponent(this.generationHistoryTable);
		} else {
			this.addComponent(this.noDataAvailableLabel);
		}
	}

	@Override
	public void updateLabels() {

	}

}
