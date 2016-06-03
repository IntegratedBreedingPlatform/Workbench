/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 * @author Kevin L. Manansala
 *
 *         This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 *         Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 **************************************************************/

package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.generationcp.commons.breedingview.xml.SSAParameters;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.sea.xml.BreedingViewProject;
import org.generationcp.commons.sea.xml.BreedingViewSession;
import org.generationcp.commons.sea.xml.DataConfiguration;
import org.generationcp.commons.sea.xml.DataFile;
import org.generationcp.commons.sea.xml.Design;
import org.generationcp.commons.sea.xml.Environments;
import org.generationcp.commons.sea.xml.Pipeline;
import org.generationcp.commons.sea.xml.Pipelines;
import org.generationcp.commons.sea.xml.Traits;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

@Configurable
public class BreedingViewXMLWriter implements InitializingBean, Serializable {

	private static final long serialVersionUID = 8844276834893749854L;

	private static final Logger LOG = LoggerFactory.getLogger(BreedingViewXMLWriter.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SessionData sessionData;

	@Value("${web.api.url}")
	private String webApiUrl;

	@Value("${workbench.is.server.app}")
	private String isServerApp;

	private final BreedingViewInput breedingViewInput;

	private final List<Integer> numericTypes;
	private final List<Integer> characterTypes;

	public BreedingViewXMLWriter(BreedingViewInput breedingViewInput) {

		this.breedingViewInput = breedingViewInput;

		this.numericTypes = new ArrayList<Integer>();
		this.characterTypes = new ArrayList<Integer>();

		this.numericTypes.add(TermId.NUMERIC_VARIABLE.getId());
		this.numericTypes.add(TermId.MIN_VALUE.getId());
		this.numericTypes.add(TermId.MAX_VALUE.getId());
		this.numericTypes.add(TermId.DATE_VARIABLE.getId());
		this.numericTypes.add(TermId.NUMERIC_DBID_VARIABLE.getId());

		this.characterTypes.add(TermId.CHARACTER_VARIABLE.getId());
		this.characterTypes.add(TermId.CHARACTER_DBID_VARIABLE.getId());
		this.characterTypes.add(1128);
		this.characterTypes.add(1130);

	}

	public void writeProjectXML() throws BreedingViewXMLWriterException {
		DataFile data = new DataFile();
		data.setName(this.breedingViewInput.getSourceXLSFilePath());
		BreedingViewProject project = this.createBreedingViewProject();
		SSAParameters ssaParameters = this.createSSAParameters();
		BreedingViewSession bvSession = this.createBreedingViewSession(project, data, ssaParameters);
		this.createProjectXMLFile(bvSession);
		this.removePreviousDatastore(ssaParameters.getOutputDirectory());
	}

	private void createProjectXMLFile(BreedingViewSession bvSession) throws BreedingViewXMLWriterException {
		// prepare the writing of the xml
		Marshaller marshaller = this.getMarshaller();
		// write the xml
		String filePath = this.breedingViewInput.getDestXMLFilePath();
		try {
			new File(new File(filePath).getParent()).mkdirs();
			FileWriter fileWriter = new FileWriter(filePath);
			BreedingViewXMLWriter.LOG.debug(filePath);
			marshaller.marshal(bvSession, fileWriter);
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception ex) {
			throw new BreedingViewXMLWriterException("Error with writing xml to: " + filePath + ": " + ex.getMessage(), ex);
		}
	}

	private Marshaller getMarshaller() throws BreedingViewXMLWriterException {
		JAXBContext context = null;
		Marshaller marshaller = null;
		try {
			context = JAXBContext.newInstance(BreedingViewSession.class, Pipelines.class, Environments.class, Pipeline.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (JAXBException ex) {
			throw new BreedingViewXMLWriterException("Error with opening JAXB context and marshaller: " + ex.getMessage(), ex);
		}
		return marshaller;
	}

	private BreedingViewSession createBreedingViewSession(BreedingViewProject project, DataFile data, SSAParameters ssaParameters) {
		BreedingViewSession bvSession = new BreedingViewSession();
		bvSession.setBreedingViewProject(project);
		bvSession.setDataFile(data);
		bvSession.setIbws(ssaParameters);
		return bvSession;
	}

	private BreedingViewProject createBreedingViewProject() {
		Environments environments = this.createEnvironments();
		Design design = this.createDesign();
		Traits traits = this.createTraits();
		DataConfiguration dataConfiguration = this.createDataConfiguration(environments, design, traits);
		Pipelines pipelines = this.createPipelines(dataConfiguration);
		BreedingViewProject project = new BreedingViewProject();
		project.setName(this.breedingViewInput.getBreedingViewAnalysisName());
		project.setVersion("1.2");
		project.setPipelines(pipelines);
		return project;
	}

	private Pipelines createPipelines(DataConfiguration dataConfiguration) {
		Pipelines pipelines = new Pipelines();
		Pipeline pipeline = new Pipeline();
		pipeline.setType("SEA");
		pipeline.setDataConfiguration(dataConfiguration);
		pipelines.add(pipeline);
		return pipelines;
	}

	private SSAParameters createSSAParameters() throws BreedingViewXMLWriterException {
		SSAParameters ssaParameters = new SSAParameters();
		ssaParameters.setWebApiUrl(this.getWebApiUrl());
		ssaParameters.setStudyId(this.breedingViewInput.getStudyId());
		ssaParameters.setInputDataSetId(this.breedingViewInput.getDatasetId());
		ssaParameters.setOutputDataSetId(this.breedingViewInput.getOutputDatasetId());

		Project workbenchProject = this.getLastOpenedProject();
		if (workbenchProject != null) {
			ssaParameters.setWorkbenchProjectId(workbenchProject.getProjectId());
		}

		String installationDirectory = this.getInstallationDirectory();
		String outputDirectory =
				String.format("%s/workspace/%s/breeding_view/output", installationDirectory, workbenchProject.getProjectName());
		ssaParameters.setOutputDirectory(outputDirectory);


		if (Boolean.parseBoolean(this.isServerApp)) {
			ssaParameters.setOutputDirectory(null);
			ssaParameters.setWebApiUrl(null);
		}

		return ssaParameters;
	}

	protected Project getLastOpenedProject() {
		return this.sessionData.getLastOpenedProject();
	}

	protected String getInstallationDirectory() {
		return this.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory();
	}

	protected String getWebApiUrl() {
		String url = this.webApiUrl + "?restartApplication";
		url += this.sessionData.getWorkbenchContextParameters();
		return url;
	}

	private Environments createEnvironments() {
		Environments environments = new Environments();
		environments.setName(this.breedingViewInput.getEnvironment().getName());

		for (SeaEnvironmentModel s : this.breedingViewInput.getSelectedEnvironments()) {
			org.generationcp.commons.sea.xml.Environment env = new org.generationcp.commons.sea.xml.Environment();
			env.setName(s.getEnvironmentName().replace(",", ";"));
			env.setActive(true);
			if (s.getActive()) {
				environments.add(env);
			}
		}
		return environments;
	}

	private Design createDesign() {

		Design design = new Design();
		design.setType(this.breedingViewInput.getDesignType());

		design.setBlocks(this.breedingViewInput.getBlocks());

		design.setReplicates(this.breedingViewInput.getReplicates());

		design.setColumns(this.breedingViewInput.getColumns());

		design.setRows(this.breedingViewInput.getRows());

		design.setColPos(this.breedingViewInput.getColPos());

		design.setRowPos(this.breedingViewInput.getRowPos());

		design.setPlot(this.breedingViewInput.getPlot());

		return design;
	}

	private DataConfiguration createDataConfiguration(Environments environments, Design design, Traits traits) {

		DataConfiguration dataConfiguration = new DataConfiguration();

		dataConfiguration.setEnvironments(environments);

		dataConfiguration.setDesign(design);

		dataConfiguration.setGenotypes(this.breedingViewInput.getGenotypes());

		dataConfiguration.setTraits(traits);

		return dataConfiguration;
	}

	private Traits createTraits() {
		Traits traits = new Traits();
		SortedSet<String> keys = new TreeSet<String>(this.breedingViewInput.getVariatesActiveState().keySet());
		for (String key : keys) {
			if (this.breedingViewInput.getVariatesActiveState().get(key)) {
				Trait trait = new Trait();
				trait.setName(BreedingViewUtil.trimAndSanitizeName(key));
				trait.setActive(true);
				traits.add(trait);
			}
		}
		return traits;
	}

	protected void removePreviousDatastore(String outputDirectory) {
		File dataStoreFile = new File(outputDirectory + "/Datastore.qsv");
		if (dataStoreFile.exists()) {
			dataStoreFile.delete();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// overridden method from interface
	}
}
