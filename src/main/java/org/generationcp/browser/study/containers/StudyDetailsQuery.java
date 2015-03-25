/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.browser.study.containers;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
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
    private static final SimpleDateFormat BACKEND_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat FRONTEND_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final String programUUID;
    private StudyDataManager studyDataManager;
    private StudyType studyType;
    private List<String> columnIds;
    private int size;

    public StudyDetailsQuery(StudyDataManager studyDataManager,
                             StudyType studyType, List<String> columnIds, String programUUID) {
        super();
        this.studyDataManager = studyDataManager;
        this.studyType = studyType;
        this.columnIds = columnIds;
        this.programUUID = programUUID;
        size = -1;
    }

    @Override
    public Item constructItem() {
        PropertysetItem item = new PropertysetItem();
        for (String id : columnIds) {
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
        List<Item> items = new ArrayList<Item>();
        List<StudyDetails> list = getStudyDetailsList(startIndex, count);

        for (StudyDetails studyDetails : list) {
            items.add(getStudyItem(studyDetails));
        }
        return items;
    }

    private Item getStudyItem(StudyDetails studyDetails) {
        Item item = new PropertysetItem();
        String columnId = null;
        String value = null;
        int numOfCols = columnIds.size();
        for (int i = 0; i < numOfCols; i++) {
            columnId = columnIds.get(i);
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
                    value = getStudyDate(studyDetails.getStartDate(),
                            BACKEND_DATE_FORMAT, FRONTEND_DATE_FORMAT);
                    break;
                case 4:
                    value = getStudyDate(studyDetails.getEndDate(),
                            BACKEND_DATE_FORMAT, FRONTEND_DATE_FORMAT);
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
                //disregard value
            }
            if (value != null) {
                item.addItemProperty(columnId, new ObjectProperty<String>(value));
            } else {
                item.addItemProperty(columnId, null);
            }
        }
        return item;
    }

    protected List<StudyDetails> getStudyDetailsList(int startIndex, int count) {
        List<StudyDetails> studyDetailsList = new ArrayList<StudyDetails>();
        try {
            if (studyType != null) {
                studyDetailsList = studyDataManager.getStudyDetails(studyType, programUUID, startIndex, count);
            } else {
                studyDetailsList = studyDataManager.getNurseryAndTrialStudyDetails(programUUID, startIndex, count);
            }
        } catch (MiddlewareQueryException e) {
            LOG.error("Error in getting all study details for study type: " + studyType + "\n" + e.toString(), e);
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
            LOG.error(e.getMessage(), e);
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
        if (size == -1) {
            try {
                if (studyType != null) {
                    Long count = studyDataManager.countStudyDetails(studyType, programUUID);
                    this.size = count.intValue();
                } else {
                    Long count = studyDataManager.countAllNurseryAndTrialStudyDetails(programUUID);
                    this.size = count.intValue();
                }
            } catch (MiddlewareQueryException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return size;
    }


}
