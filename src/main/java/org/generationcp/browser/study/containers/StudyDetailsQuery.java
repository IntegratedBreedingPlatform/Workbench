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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.Query;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

/**
 * An implementation of Query which is needed for using the LazyQueryContainer.
 * 
 * @author Mae Turiana
 * 
 */
public class StudyDetailsQuery implements Query{

    private final static Logger LOG = LoggerFactory.getLogger(StudyDetailsQuery.class);

    private StudyDataManager studyDataManager;
    private StudyType studyType;
    private List<String> columnIds;
    private int size;

	public StudyDetailsQuery(StudyDataManager studyDataManager,
			StudyType studyType, List<String> columnIds) {
		super();
		this.studyDataManager = studyDataManager;
		this.studyType = studyType;
		this.columnIds = columnIds;
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
        final SimpleDateFormat oldFormat = new SimpleDateFormat("yyyyMMdd");
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<Item> items = new ArrayList<Item>();
        List<StudyDetails> list = new ArrayList<StudyDetails>();
        try {
        	if(studyType!=null) {
                list = studyDataManager.getStudyDetails(Database.LOCAL,studyType,startIndex,count);
        	} else {
        		list = studyDataManager.getNurseryAndTrialStudyDetails(Database.LOCAL,startIndex,count);
        	}
        } catch (MiddlewareQueryException e) {
        	LOG.error("Error in getting all study details with for study type: " + studyType + "\n" + e.toString());
        }
        
        int numOfCols = columnIds.size();
        for (StudyDetails studyDetails : list) {
        	Item item = new PropertysetItem();
        	String columnId = null;
        	String value = null;
        	for(int i=0;i<numOfCols;i++) {
        		columnId = columnIds.get(i);
        		switch(i) {
        			case 0: value = studyDetails.getStudyName();
        					break;
        			case 1: value = studyDetails.getTitle();
							break;
        			case 2: value = studyDetails.getObjective();
							break;
        			case 3:
                        try {
                            value = format.format(oldFormat.parse(studyDetails.getStartDate()));
                        } catch (ParseException e) {
                            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

                            value = "N/A";
                        }
                        break;
        			case 4:
                        try {
                            value = format.format(oldFormat.parse(studyDetails.getStartDate()));
                        } catch (ParseException e) {
                            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

                            value = "N/A";
                        }
                        break;
        			case 5: value = studyDetails.getPiName();
							break;
        			case 6: value = studyDetails.getSiteName();
							break;
        			case 7: value = studyDetails.getStudyType().getLabel();
							break;
        		}
        		if(value!=null) {
            		item.addItemProperty(columnId, new ObjectProperty<String>(value));
            	} else {
            		item.addItemProperty(columnId, null);
            	}
        	}
        	items.add(item);
		}
		return items;
	}

	@Override
	public void saveItems(List<Item> arg0, List<Item> arg1, List<Item> arg2) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public int size() {
		if(size == -1){
            try {
            	if(studyType!=null) {
            		Long count = studyDataManager.countStudyDetails(Database.LOCAL,studyType);
            		this.size = count.intValue();
            	} else {
            		Long count = studyDataManager.countNurseryAndTrialStudyDetails(Database.LOCAL);
            		this.size = count.intValue();
            	}
            } catch (MiddlewareQueryException ex) {
                LOG.error("Error with getting study details for study type: " + studyType + "\n" + ex.toString());

            }
        }
		return size;
	}
    

}
