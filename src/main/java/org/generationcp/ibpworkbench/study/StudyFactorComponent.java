/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.study;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.containers.StudyDataContainerBuilder;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class StudyFactorComponent extends Table implements InitializingBean, InternationalizableComponent {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(StudyFactorComponent.class);
	private static final long serialVersionUID = 1053068118831514119L;

	private static final String NAME = "NAME";
	private static final String DESC = "DESCRIPTION";
	private static final String PROP = "PROPERTY";
	private static final String SCA = "SCALE";
	private static final String METH = "METHOD";
	private static final String DTYPE = "DATATYPE";
	private static final String VALUE = "VALUE";

	private final int studyId;

	private final StudyDataManager studyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public StudyFactorComponent(final StudyDataManager studyDataManager, final int studyId) {
		this.studyDataManager = studyDataManager;
		this.studyId = studyId;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final StudyDataContainerBuilder studyDataContainerBuilder = new StudyDataContainerBuilder(this.studyDataManager, this.studyId);
		final IndexedContainer dataStudyFactor;

		dataStudyFactor = studyDataContainerBuilder.buildIndexedContainerForStudyFactor();

		this.setContainerDataSource(dataStudyFactor);

		this.setSelectable(true);
		this.setMultiSelect(false);
		this.setImmediate(true); // react at once when something is
		this.setSizeFull();
		this.setColumnReorderingAllowed(true);
		this.setColumnCollapsingAllowed(true);
		this.setColumnHeaders(
				new String[] {StudyFactorComponent.NAME, StudyFactorComponent.DESC, StudyFactorComponent.PROP, StudyFactorComponent.SCA,
						StudyFactorComponent.METH, StudyFactorComponent.DTYPE, StudyFactorComponent.VALUE});
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setColumnHeader(this, "NAME", Message.NAME_HEADER);
		this.messageSource.setColumnHeader(this, "DESCRIPTION", Message.DESCRIPTION_HEADER);
		this.messageSource.setColumnHeader(this, "PROPERTY", Message.PROPERTY_HEADER);
		this.messageSource.setColumnHeader(this, "SCALE", Message.SCALE_HEADER);
		this.messageSource.setColumnHeader(this, "METHOD", Message.METHOD_HEADER);
		this.messageSource.setColumnHeader(this, "DATATYPE", Message.DATATYPE_HEADER);
		this.messageSource.setColumnHeader(this, "VALUE", Message.VALUE_HEADER);
	}

}
