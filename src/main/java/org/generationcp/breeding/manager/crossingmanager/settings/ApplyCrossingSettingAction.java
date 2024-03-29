
package org.generationcp.breeding.manager.crossingmanager.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.generationcp.breeding.manager.crossingmanager.CrossesMadeContainer;
import org.generationcp.breeding.manager.crossingmanager.CrossesMadeContainerUpdateListener;
import org.generationcp.breeding.manager.crossingmanager.pojos.GermplasmListEntry;
import org.generationcp.breeding.manager.util.BreedingManagerUtil;
import org.generationcp.commons.util.CrossingUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ApplyCrossingSettingAction implements CrossesMadeContainerUpdateListener {

	private static final Logger LOG = LoggerFactory.getLogger(ApplyCrossingSettingAction.class);

	@Autowired
	private GermplasmDataManager germplasmDataManager;
	@Autowired
	private GermplasmListManager germplasmListManager;

	private CrossesMadeContainer container;

	@Override
	public boolean updateCrossesMadeContainer(final CrossesMadeContainer container) {
		this.container = container;
		return this.applyBreedingMethodSetting() && this.applyNameSetting() && this.applyAdditionalDetailsSetting();
	}

	/**
	 * Set breeding method of germplasm based on configuration in setting. Can be same for all crosses or based on status of parental lines
	 *
	 * @return
	 */
	private boolean applyBreedingMethodSetting() {
		if (this.container != null && this.container.getCrossesMade() != null && this.container.getCrossesMade().getCrossesList() != null) {

			// Use same breeding method for all crosses
			for (final Triple<Germplasm, Name, List<Progenitor>> triple: this.container.getCrossesMade().getCrossesList()) {
				//Set the method id to Single Cross(101) for now, it will be overwritten in the nursery side
				triple.getLeft().setMethod(new Method(101));
			}

			Integer crossingNameTypeId = null;
			try {
				crossingNameTypeId = BreedingManagerUtil.getIDForUserDefinedFieldCrossingName(this.germplasmListManager);
			} catch (final MiddlewareQueryException e) {
				ApplyCrossingSettingAction.LOG.error(e.getMessage(), e);
			}

			CrossingUtil.applyMethodNameType(this.germplasmDataManager, this.container.getCrossesMade().getCrossesList(), crossingNameTypeId);
			return true;

		}

		return false;
	}

	/**
	 * Generate values for NAME record plus Germplasm List Entry designation based on cross name setting configuration
	 *
	 * @return
	 */
	private boolean applyNameSetting() {
		if (this.container != null && this.container.getCrossesMade() != null && this.container.getCrossesMade().getCrossesList() != null) {
			int ctr = 1;

			final List<GermplasmListEntry> oldCrossNames = new ArrayList<>();

			final List<Triple<Germplasm, Name, List<Progenitor>>> crossesList = this.container.getCrossesMade().getCrossesList();

			// Store old cross name and generate new names based on prefix, suffix specifications
			for (final Triple<Germplasm, Name, List<Progenitor>> triple: crossesList) {
				final Germplasm germplasm = triple.getLeft();
				final Name nameObject = triple.getMiddle();
				final String oldCrossName = nameObject.getNval();
				nameObject.setNval(String.valueOf(ctr++));

				final Integer tempGid = germplasm.getGid();
				final GermplasmListEntry oldNameEntry = new GermplasmListEntry(tempGid, tempGid, tempGid, oldCrossName);

				oldCrossNames.add(oldNameEntry);
			}
			// Only store the "original" cross names, would not store previous names on 2nd, 3rd, ... change
			if (this.container.getCrossesMade().getOldCrossNames() == null
					|| this.container.getCrossesMade().getOldCrossNames().isEmpty()) {
				this.container.getCrossesMade().setOldCrossNames(oldCrossNames);
			}

			return true;

		}

		return false;

	}

	/**
	 * Set GERMPLSM location id and gdate and NAME location id and ndate based on harvest date and location information given in setting
	 *
	 * @return
	 */
	private boolean applyAdditionalDetailsSetting() {
		if (this.container != null && this.container.getCrossesMade() != null && this.container.getCrossesMade().getCrossesList() != null) {
			//the date and harvest location will be overwritten in the nursery side.
			final Integer dateIntValue = 0;
			final Integer harvestLocationId = 0;
			final List<Triple<Germplasm, Name, List<Progenitor>>> crossesList = this.container.getCrossesMade().getCrossesList();
			for (final Triple<Germplasm, Name, List<Progenitor>> triple: crossesList) {
				final Germplasm germplasm = triple.getLeft();
				germplasm.setLocationId(harvestLocationId);
				germplasm.setGdate(dateIntValue);

				final Name name = triple.getMiddle();
				name.setLocationId(harvestLocationId);
				name.setNdate(dateIntValue);
			}
			return true;
		}

		return false;
	}
}
