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

import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.breeding.manager.listmanager.GermplasmDetailsUrlService;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.germplasm.containers.ManagementNeighborsQuery;
import org.generationcp.ibpworkbench.germplasm.containers.ManagementNeighborsQueryFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

@Configurable
public class GermplasmManagementNeighborsComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	private final Integer gid;

	private Table managementNeighborsTable;
	private Label noDataAvailableLabel;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private GermplasmDetailsUrlService germplasmDetailsUrlService;

	public GermplasmManagementNeighborsComponent(final Integer gid) {
		this.gid = gid;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
		this.layoutComponents();
	}

	private void initializeComponents() {
		final ManagementNeighborsQueryFactory factory = new ManagementNeighborsQueryFactory(this.gid);
		final LazyQueryContainer container = new LazyQueryContainer(factory, false, 50);

		if (container.size() > 0) {
			this.managementNeighborsTable = new Table();
			// add the column ids to the LazyQueryContainer tells the container the columns to display for the Table
			container.addContainerProperty(ManagementNeighborsQuery.GID, String.class, null);
			container.addContainerProperty(ManagementNeighborsQuery.PREFERRED_NAME, String.class, null);

			container.getQueryView().getItem(0); // initialize the first batch of data to be displayed

			this.managementNeighborsTable.setContainerDataSource(container);
			if (container.size() < 10) {
				this.managementNeighborsTable.setPageLength(container.size());
			} else {
				this.managementNeighborsTable.setPageLength(10);
			}
			this.managementNeighborsTable.setSelectable(true);
			this.managementNeighborsTable.setMultiSelect(false);
			this.managementNeighborsTable.setImmediate(true); // react at once when something is selected turn on column reordering and
			// collapsing
			this.managementNeighborsTable.setColumnReorderingAllowed(true);
			this.managementNeighborsTable.setColumnCollapsingAllowed(true);

			this.managementNeighborsTable.setColumnHeader(ManagementNeighborsQuery.GID, this.messageSource.getMessage(Message.GID_LABEL));
			this.managementNeighborsTable.setColumnHeader(ManagementNeighborsQuery.PREFERRED_NAME,
				this.messageSource.getMessage(Message.PREFNAME_LABEL));

			this.managementNeighborsTable.addGeneratedColumn(ManagementNeighborsQuery.GID, new Table.ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final String gid = source.getItem(itemId).getItemProperty(columnId).getValue().toString();
					final Link link =
						new Link(gid, GermplasmManagementNeighborsComponent.this.germplasmDetailsUrlService
							.getExternalResource(Integer.parseInt(gid), false));
					link.setTargetName("_blank");
					return link;
				}
			});
		} else {
			this.noDataAvailableLabel = new Label("There is no Management Neighbors Information for this germplasm.");
		}
	}

	private void layoutComponents() {
		if (this.managementNeighborsTable != null) {
			this.addComponent(this.managementNeighborsTable);
		} else {
			this.addComponent(this.noDataAvailableLabel);
		}
	}

	@Override
	public void updateLabels() {
	}

}
