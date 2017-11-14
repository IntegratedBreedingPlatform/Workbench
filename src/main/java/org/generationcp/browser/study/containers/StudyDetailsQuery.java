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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.Query;

/**
 * An implementation of Query which is needed for using the LazyQueryContainer.
 *
 * @author Mae Turiana
 */
public class StudyDetailsQuery implements Query {

	private static final Logger LOG = LoggerFactory.getLogger(StudyDetailsQuery.class);

	private final String programUUID;
	private final StudyDataManager studyDataManager;
	private final StudyType studyType;
	private final List<String> columnIds;
	private int size;

	public StudyDetailsQuery(StudyDataManager studyDataManager, StudyType studyType, List<String> columnIds, String programUUID) {
		super();
		this.studyDataManager = studyDataManager;
		this.studyType = studyType;
		this.columnIds = columnIds;
		this.programUUID = programUUID;
		this.size = -1;
	}

	@Override
	public Item constructItem() {
		PropertysetItem item = new PropertysetItem();
		for (String id : this.columnIds) {
			item.addItemProperty(id, new ObjectProperty<String>(""));
		}
		return item;
	}

	@Override
	public boolean deleteAllItems() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Item> loadItems(int startIndex, int count) {
		List<Item> items = new ArrayList<>();
		List<StudyDetails> list = this.getStudyDetailsList(startIndex, count);

		for (StudyDetails studyDetails : list) {
			items.add(this.getStudyItem(studyDetails));
		}
		return items;
	}

	private Item getStudyItem(StudyDetails studyDetails) {
		Item item = new PropertysetItem();
		String value = null;
		int numOfCols = this.columnIds.size();
		final SimpleDateFormat backendDateFormat = Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);
		final SimpleDateFormat frontendDateFormat = Util.getSimpleDateFormat(Util.FRONTEND_DATE_FORMAT);

		for (int i = 0; i < numOfCols; i++) {
			switch (i) {
				case 0:
					value = studyDetails.getStudyName();
					break;
				case 1:
					value = studyDetails.getTitle();
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

	protected List<StudyDetails> getStudyDetailsList(int startIndex, int count) {
		List<StudyDetails> studyDetailsList = new ArrayList<>();
		try {
			if (this.studyType != null) {
				studyDetailsList = this.studyDataManager.getStudyDetails(this.studyType, this.programUUID, startIndex, count);
			} else {
				studyDetailsList = this.studyDataManager.getNurseryAndTrialStudyDetails(this.programUUID, startIndex, count);
			}
		} catch (MiddlewareQueryException e) {
			StudyDetailsQuery.LOG.error("Error in getting all study details for study type: " + this.studyType + "\n" + e.toString(), e);
		}
		return studyDetailsList;
	}

	protected String getStudyDate(String date, SimpleDateFormat oldFormat, SimpleDateFormat format) {
		String value;
		try {
			if (date == null) {
				value = "";
			} else {
				value = format.format(oldFormat.parse(date));
			}
		} catch (ParseException e) {
			StudyDetailsQuery.LOG.debug(e.getMessage());
			value = "N/A";
		}
		return value;
	}

	@Override
	public void saveItems(List<Item> arg0, List<Item> arg1, List<Item> arg2) {
		throw new UnsupportedOperationException();

	}

	@Override
	public int size() {
		if (this.size == -1) {
			try {
				if (this.studyType != null) {
					Long count = this.studyDataManager.countStudyDetails(this.studyType, this.programUUID);
					this.size = count.intValue();
				} else {
					Long count = this.studyDataManager.countAllNurseryAndTrialStudyDetails(this.programUUID);
					this.size = count.intValue();
				}
			} catch (MiddlewareQueryException ex) {
				StudyDetailsQuery.LOG.error(ex.getMessage(), ex);
			}
		}
		return this.size;
	}

}
