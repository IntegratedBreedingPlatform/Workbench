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

package org.generationcp.browser.study.containers;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Query which is needed for using the LazyQueryContainer.
 *
 * @author Mae Turiana
 */
public class StudyDetailsQuery implements Query {

	private static final Logger LOG = LoggerFactory.getLogger(StudyDetailsQuery.class);

	private final String programUUID;
	private final StudyDataManager studyDataManager;
	private final StudyTypeDto studyType;
	private final List<String> columnIds;
	private int size;

	public StudyDetailsQuery(
		final StudyDataManager studyDataManager, final StudyTypeDto studyType, final List<String> columnIds, final String programUUID) {
		super();
		this.studyDataManager = studyDataManager;
		this.studyType = studyType;
		this.columnIds = columnIds;
		this.programUUID = programUUID;
		this.size = -1;
	}

	@Override
	public Item constructItem() {
		final PropertysetItem item = new PropertysetItem();
		for (final String id : this.columnIds) {
			item.addItemProperty(id, new ObjectProperty<String>(""));
		}
		return item;
	}

	@Override
	public boolean deleteAllItems() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Item> loadItems(final int startIndex, final int count) {
		final List<Item> items = new ArrayList<>();
		final List<StudyDetails> list = this.getStudyDetailsList(startIndex, count);

		for (final StudyDetails studyDetails : list) {
			items.add(this.getStudyItem(studyDetails));
		}
		return items;
	}

	private Item getStudyItem(final StudyDetails studyDetails) {
		final Item item = new PropertysetItem();
		String value = null;
		final int numOfCols = this.columnIds.size();
		final SimpleDateFormat backendDateFormat = Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);
		final SimpleDateFormat frontendDateFormat = Util.getSimpleDateFormat(Util.FRONTEND_DATE_FORMAT);

		for (int i = 0; i < numOfCols; i++) {
			switch (i) {
				case 0:
					value = studyDetails.getStudyName();
					break;
				case 1:
					value = studyDetails.getDescription();
					break;
				case 2:
					value = studyDetails.getObjective();
					break;
				case 3:
					value =
							this.getStudyDate(studyDetails.getStartDate(), backendDateFormat,
									frontendDateFormat);
					break;
				case 4:
					value =
							this.getStudyDate(studyDetails.getEndDate(), backendDateFormat,
									frontendDateFormat);
					break;
				case 5:
					value = studyDetails.getPiName();
					break;
				case 6:
					value = studyDetails.getSiteName();
					break;
				case 7:
					value = studyDetails.getStudyType().getLabel();
					break;
				default:
					break;
			}
			item.addItemProperty(this.columnIds.get(i), value != null ? new ObjectProperty<>(value) : null);
		}
		return item;
	}

	protected List<StudyDetails> getStudyDetailsList(final int startIndex, final int count) {
		List<StudyDetails> studyDetailsList = new ArrayList<>();
		try {
			if (this.studyType != null) {
				studyDetailsList = this.studyDataManager.getStudyDetails(this.studyType, this.programUUID, startIndex, count);
			} else {
				studyDetailsList = this.studyDataManager.getNurseryAndTrialStudyDetails(this.programUUID, startIndex, count);
			}
		} catch (final MiddlewareQueryException e) {
			StudyDetailsQuery.LOG.error("Error in getting all study details for study type: " + this.studyType + "\n" + e.toString(), e);
		}
		return studyDetailsList;
	}

	protected String getStudyDate(final String date, final SimpleDateFormat oldFormat, final SimpleDateFormat format) {
		String value;
		try {
			if (date == null) {
				value = "";
			} else {
				value = format.format(oldFormat.parse(date));
			}
		} catch (final ParseException e) {
			StudyDetailsQuery.LOG.debug(e.getMessage());
			value = "N/A";
		}
		return value;
	}

	@Override
	public void saveItems(final List<Item> arg0, final List<Item> arg1, final List<Item> arg2) {
		throw new UnsupportedOperationException();

	}

	@Override
	public int size() {
		if (this.size == -1) {
			try {
				if (this.studyType != null) {
					final Long count = this.studyDataManager.countAllStudyDetails(this.studyType, this.programUUID);
					this.size = count.intValue();
				} else {
					final Long count = this.studyDataManager.countAllNurseryAndTrialStudyDetails(this.programUUID);
					this.size = count.intValue();
				}
			} catch (final MiddlewareQueryException ex) {
				StudyDetailsQuery.LOG.error(ex.getMessage(), ex);
			}
		}
		return this.size;
	}

}
