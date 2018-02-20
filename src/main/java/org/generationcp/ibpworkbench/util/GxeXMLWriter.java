/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 * @author Sir Aldrin Batac
 *
 *         This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 *         Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 **************************************************************/

package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.generationcp.commons.breedingview.xml.SSAParameters;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.sea.xml.BreedingViewSession;
import org.generationcp.commons.sea.xml.DataConfiguration;
import org.generationcp.commons.sea.xml.DataFile;
import org.generationcp.commons.sea.xml.Environment;
import org.generationcp.commons.sea.xml.Environments;
import org.generationcp.commons.sea.xml.MegaEnvironment;
import org.generationcp.commons.sea.xml.MegaEnvironments;
import org.generationcp.commons.sea.xml.Pipeline;
import org.generationcp.commons.sea.xml.Pipelines;
import org.generationcp.commons.sea.xml.Traits;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

@Configurable
public class GxeXMLWriter implements InitializingBean, Serializable {

	private static final long serialVersionUID = 8866276834893749854L;

	private static final Logger LOG = LoggerFactory.getLogger(GxeXMLWriter.class);

	@Value("${workbench.is.server.app}")
	private String isServerAppString;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	
	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	private final GxeInput gxeInput;

	public GxeXMLWriter(final GxeInput gxeInput) {
		this.gxeInput = gxeInput;
	}

	public void writeProjectXML() throws GxeXMLWriterException {
		final boolean isServerApp = Boolean.parseBoolean(this.isServerAppString);

		final Traits traits = new Traits();
		for (final Trait t : this.gxeInput.getTraits()) {
			final String traitName = BreedingViewUtil.sanitizeName(t.getName());
			t.setBlues(traitName);
			t.setBlups(traitName.replace("_Means", "_BLUPs"));
			t.setName(traitName);
			traits.add(t);
		}

		// create DataFile element
		final DataFile data = new DataFile();
		if (isServerApp) {
			data.setName(new File(this.gxeInput.getSourceCSVFilePath()).getName());
			data.setSummarystats(new File(this.gxeInput.getSourceCSVSummaryStatsFilePath()).getName());
		} else {
			data.setName(this.gxeInput.getSourceCSVFilePath());
			data.setSummarystats(this.gxeInput.getSourceCSVSummaryStatsFilePath());
		}

		final Environments environments = new Environments();
		environments.setName(BreedingViewUtil.sanitizeName(this.gxeInput.getEnvironmentName()));
		environments.setEnvironments(this.gxeInput.getSelectedEnvironments());

		for (final Environment e : environments.getEnvironments()) {
			e.setName(e.getName().replace(",", ";"));
		}

		// create the DataConfiguration element
		final DataConfiguration dataConfiguration = new DataConfiguration();
		dataConfiguration.setName("GxE Analysis");
		dataConfiguration.setEnvironments(environments);
		if (this.gxeInput.getGenotypes() != null) {
			this.gxeInput.getGenotypes().setName(
					BreedingViewUtil.sanitizeName(this.gxeInput.getGenotypes().getName()));
		}
		dataConfiguration.setGenotypes(this.gxeInput.getGenotypes());
		dataConfiguration.setTraits(traits);
		dataConfiguration.setHeritabilities(this.gxeInput.getHeritabilities());

		if (!"None".equalsIgnoreCase(this.gxeInput.getEnvironmentGroup())) {
			final MegaEnvironment megaEnv = new MegaEnvironment();
			final MegaEnvironments megaEnvs = new MegaEnvironments();
			megaEnv.setActive(true);
			megaEnv.setName(BreedingViewUtil.sanitizeName(this.gxeInput.getEnvironmentGroup()));
			megaEnvs.add(megaEnv);
			dataConfiguration.setMegaEnvironments(megaEnvs);
		}

		final Pipelines pipelines = new Pipelines();
		final Pipeline pipeline = new Pipeline();
		pipeline.setType("GXE");
		pipeline.setDataConfiguration(dataConfiguration);
		pipelines.add(pipeline);

		// create the Breeding View project element
		final org.generationcp.commons.sea.xml.BreedingViewProject project = new org.generationcp.commons.sea.xml.BreedingViewProject();
		project.setName(this.gxeInput.getBreedingViewProjectName());
		project.setVersion("1.2");
		project.setPipelines(pipelines);

		final BreedingViewSession bvSession = new BreedingViewSession();
		bvSession.setBreedingViewProject(project);
		bvSession.setDataFile(data);

		final SSAParameters ssaParameters = new SSAParameters();
		// output directory is not needed if deployed on server
		if (!isServerApp) {
			try {
				
				final Tool breedingViewTool = this.workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
				final String outputDirectory =
						this.installationDirectoryUtil.getOutputDirectoryForProjectAndTool(this.gxeInput.getProject(), breedingViewTool);

				ssaParameters.setOutputDirectory(outputDirectory);
			} catch (final Exception e) {
				GxeXMLWriter.LOG.error("Error getting BMS installation directory", e);
			}
		}
		bvSession.setIbws(ssaParameters);

		// prepare the writing of the xml
		JAXBContext context = null;
		Marshaller marshaller = null;
		try {
			context = JAXBContext.newInstance(BreedingViewSession.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (final JAXBException ex) {
			throw new GxeXMLWriterException("Error with opening JAXB context and marshaller: " + ex.getMessage(), ex);
		}

		// write the xml
		try {
			final FileWriter fileWriter = new FileWriter(this.gxeInput.getDestXMLFilePath());
			marshaller.marshal(bvSession, fileWriter);
			fileWriter.flush();
			fileWriter.close();
		} catch (final Exception ex) {
			throw new GxeXMLWriterException(String.format("Error with writing xml to: %s : %s", "", ex.getMessage()), ex);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// do nothing - inherited abstract method
	}
}
