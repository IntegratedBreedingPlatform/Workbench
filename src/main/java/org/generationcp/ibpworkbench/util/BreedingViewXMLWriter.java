/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 *
 * @author Kevin L. Manansala
 *         <p/>
 *         This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 *         Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 **************************************************************/

package org.generationcp.ibpworkbench.util;

import org.generationcp.commons.breedingview.xml.Covariate;
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
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Configurable
public class BreedingViewXMLWriter implements InitializingBean, Serializable {

	private static final long serialVersionUID = 8844276834893749854L;

	private static final Logger LOG = LoggerFactory.getLogger(BreedingViewXMLWriter.class);

	private static final String CROP_PLACEHOLDER = "{cropName}";

	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	@Autowired
	private ContextUtil contextUtil;

	@Value("${web.api.url}")
	private String webApiUrl;

	private BreedingViewInput breedingViewInput;

	private final List<Integer> numericTypes;
	private final List<Integer> characterTypes;

	public BreedingViewXMLWriter() {
		this.numericTypes = new ArrayList<>();
		this.characterTypes = new ArrayList<>();

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
		final DataFile data = new DataFile();
		data.setName(this.breedingViewInput.getSourceXLSFilePath());
		final BreedingViewProject project = this.createBreedingViewProject();
		final SSAParameters ssaParameters = this.createSSAParameters();
		final BreedingViewSession bvSession = this.createBreedingViewSession(project, data, ssaParameters);
		this.createProjectXMLFile(bvSession);
		this.removePreviousDatastore(ssaParameters.getOutputDirectory());
	}

	private void createProjectXMLFile(final BreedingViewSession bvSession) throws BreedingViewXMLWriterException {
		// prepare the writing of the xml
		final Marshaller marshaller = this.getMarshaller();
		// write the xml
		final String filePath = this.breedingViewInput.getDestXMLFilePath();
		try {
			new File(new File(filePath).getParent()).mkdirs();
			final FileWriter fileWriter = new FileWriter(filePath);
			BreedingViewXMLWriter.LOG.debug(filePath);
			marshaller.marshal(bvSession, fileWriter);
			fileWriter.flush();
			fileWriter.close();
		} catch (final Exception ex) {
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
		} catch (final JAXBException ex) {
			throw new BreedingViewXMLWriterException("Error with opening JAXB context and marshaller: " + ex.getMessage(), ex);
		}
		return marshaller;
	}

	private BreedingViewSession createBreedingViewSession(
		final BreedingViewProject project, final DataFile data,
		final SSAParameters ssaParameters) {
		final BreedingViewSession bvSession = new BreedingViewSession();
		bvSession.setBreedingViewProject(project);
		bvSession.setDataFile(data);
		bvSession.setIbws(ssaParameters);
		return bvSession;
	}

	private BreedingViewProject createBreedingViewProject() {
		final Environments environments = this.createEnvironments();
		final Design design = this.createDesign();
		final DataConfiguration dataConfiguration = this.createDataConfiguration(environments, design);
		final Pipelines pipelines = this.createPipelines(dataConfiguration);
		final BreedingViewProject project = new BreedingViewProject();
		project.setName(this.breedingViewInput.getBreedingViewAnalysisName());
		project.setVersion("1.2");
		project.setPipelines(pipelines);
		return project;
	}

	private Pipelines createPipelines(final DataConfiguration dataConfiguration) {
		final Pipelines pipelines = new Pipelines();
		final Pipeline pipeline = new Pipeline();
		pipeline.setType("SEA");
		pipeline.setDataConfiguration(dataConfiguration);
		pipelines.add(pipeline);
		return pipelines;
	}

	private SSAParameters createSSAParameters() {
		final SSAParameters ssaParameters = new SSAParameters();
		ssaParameters.setWebApiUrl(this.getWebApiUrl());
		ssaParameters.setStudyId(this.breedingViewInput.getStudyId());
		ssaParameters.setInputDataSetId(this.breedingViewInput.getDatasetId());
		ssaParameters.setOutputDataSetId(this.breedingViewInput.getOutputDatasetId());

		final Project workbenchProject = this.getLastOpenedProject();
		if (workbenchProject != null) {
			ssaParameters.setWorkbenchProjectId(workbenchProject.getProjectId());
		}

		ssaParameters.setOutputDirectory(null);
		ssaParameters.setWebApiUrl(null);

		return ssaParameters;
	}

	protected Project getLastOpenedProject() {
		return this.contextUtil.getProjectInContext();
	}

	protected String getWebApiUrl() {
		final String url = this.webApiUrl + "?restartApplication";
		final Project project = this.contextUtil.getProjectInContext();

		final String contextParameterString = org.generationcp.commons.util.ContextUtil
			.getContextParameterString(this.contextUtil.getCurrentWorkbenchUserId(), project.getProjectId());

		String webApiUrlWithCropName = this.replaceCropNameInWebApiUrl(url, project.getCropType().getCropName());
		webApiUrlWithCropName += contextParameterString;
		return webApiUrlWithCropName;
	}

	private String replaceCropNameInWebApiUrl(final String webApiUrl, final String cropNameValue) {
		final StringBuilder containerWebApiUrl = new StringBuilder(webApiUrl);

		final int startIndex = containerWebApiUrl.indexOf(BreedingViewXMLWriter.CROP_PLACEHOLDER);
		final int endIndex = startIndex + BreedingViewXMLWriter.CROP_PLACEHOLDER.length();

		containerWebApiUrl.replace(startIndex, endIndex, cropNameValue);
		return containerWebApiUrl.toString();
	}

	Environments createEnvironments() {
		final Environments environments = new Environments();
		environments.setName(this.breedingViewInput.getEnvironment().getName());

		// Trial name attribute is not needed in the BV if the selected
		// environment factor is Trial instance
		if (!this.breedingViewInput.getTrialInstanceName().equals(this.breedingViewInput.getEnvironment().getName())) {
			environments.setTrialName(this.breedingViewInput.getTrialInstanceName());
		}

		for (final SeaEnvironmentModel selectedEnvironment : this.breedingViewInput.getSelectedEnvironments()) {
			final org.generationcp.commons.sea.xml.Environment env = new org.generationcp.commons.sea.xml.Environment();
			env.setName(selectedEnvironment.getEnvironmentName().replace(",", ";"));
			env.setActive(true);

			// Trial name attribute is not needed in the BV if the selected
			// environment factor is Trial instance
			if (!this.breedingViewInput.getTrialInstanceName().equals(this.breedingViewInput.getEnvironment().getName())) {
				env.setTrial(selectedEnvironment.getTrialno());
			}

			if (selectedEnvironment.getActive()) {
				environments.add(env);
			}
		}
		return environments;
	}

	Design createDesign() {

		final Design design = new Design();
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

	protected DataConfiguration createDataConfiguration(final Environments environments, final Design design) {

		final DataConfiguration dataConfiguration = new DataConfiguration();

		dataConfiguration.setEnvironments(environments);

		dataConfiguration.setDesign(design);

		dataConfiguration.setGenotypes(this.breedingViewInput.getGenotypes());

		dataConfiguration.setTraits(this.createTraits());

		dataConfiguration.setCovariates(this.createCovariates());

		return dataConfiguration;
	}

	protected List<Trait> createTraits() {
		final List<Trait> traits = new ArrayList<>();
		final SortedSet<String> keys = new TreeSet<>(this.breedingViewInput.getVariatesSelectionMap().keySet());
		for (final String key : keys) {
			if (this.breedingViewInput.getVariatesSelectionMap().get(key)) {
				final Trait trait = new Trait();
				trait.setName(BreedingViewUtil.trimAndSanitizeName(key));
				trait.setActive(true);
				traits.add(trait);
			}
		}

		// if the list is empty, we must return null so that JAXB will ignore Traits element and won't be included in the generated XML file.
		return traits.isEmpty() ? null : traits;
	}

	protected List<Covariate> createCovariates() {
		final List<Covariate> covariates = new ArrayList<>();
		final SortedSet<String> keys = new TreeSet<>(this.breedingViewInput.getVariatesSelectionMap().keySet());
		for (final String key : keys) {
			if (this.breedingViewInput.getCovariatesSelectionMap().get(key)) {
				final Covariate covariate = new Covariate();
				covariate.setName(BreedingViewUtil.trimAndSanitizeName(key));
				covariate.setActive(true);
				covariates.add(covariate);
			}
		}

		// if the list is empty, we must return null so that JAXB will ignore Covariates element and won't be included in the generated XML file.
		return covariates.isEmpty() ? null : covariates;
	}

	protected void removePreviousDatastore(final String outputDirectory) {
		final File dataStoreFile = new File(outputDirectory + "/Datastore.qsv");
		if (dataStoreFile.exists()) {
			dataStoreFile.delete();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// overridden method from interface
	}

	public void setWebApiUrl(final String webApiUrl) {
		this.webApiUrl = webApiUrl;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setInstallationDirectoryUtil(final InstallationDirectoryUtil installationDirectoryUtil) {
		this.installationDirectoryUtil = installationDirectoryUtil;
	}

	public void setBreedingViewInput(final BreedingViewInput breedingViewInput) {
		this.breedingViewInput = breedingViewInput;
	}
}
