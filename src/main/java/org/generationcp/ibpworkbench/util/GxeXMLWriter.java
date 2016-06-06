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
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
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

	private final GxeInput gxeInput;

	public GxeXMLWriter(GxeInput gxeInput) {
		this.gxeInput = gxeInput;
	}

	public void writeProjectXML() throws GxeXMLWriterException {
		boolean isServerApp = Boolean.parseBoolean(this.isServerAppString);

		Traits traits = new Traits();
		for (Trait t : this.gxeInput.getTraits()) {
			String traitName = BreedingViewUtil.sanitizeName(t.getName());
			t.setBlues(traitName);
			t.setBlups(traitName.replace("_Means", "_BLUPs"));
			t.setName(traitName);
			traits.add(t);
		}

		// create DataFile element
		DataFile data = new DataFile();
		if (isServerApp) {
			data.setName(new File(this.gxeInput.getSourceCSVFilePath()).getName());
			data.setSummarystats(new File(this.gxeInput.getSourceCSVSummaryStatsFilePath()).getName());
		} else {
			data.setName(this.gxeInput.getSourceCSVFilePath());
			data.setSummarystats(this.gxeInput.getSourceCSVSummaryStatsFilePath());
		}

		Environments environments = new Environments();
		environments.setName(BreedingViewUtil.sanitizeName(this.gxeInput.getEnvironmentName()));
		environments.setEnvironments(this.gxeInput.getSelectedEnvironments());

		for (Environment e : environments.getEnvironments()) {
			e.setName(e.getName().replace(",", ";"));
		}

		// create the DataConfiguration element
		DataConfiguration dataConfiguration = new DataConfiguration();
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
			MegaEnvironment megaEnv = new MegaEnvironment();
			MegaEnvironments megaEnvs = new MegaEnvironments();
			megaEnv.setActive(true);
			megaEnv.setName(BreedingViewUtil.sanitizeName(this.gxeInput.getEnvironmentGroup()));
			megaEnvs.add(megaEnv);
			dataConfiguration.setMegaEnvironments(megaEnvs);
		}

		Pipelines pipelines = new Pipelines();
		Pipeline pipeline = new Pipeline();
		pipeline.setType("GXE");
		pipeline.setDataConfiguration(dataConfiguration);
		pipelines.add(pipeline);

		// create the Breeding View project element
		org.generationcp.commons.sea.xml.BreedingViewProject project = new org.generationcp.commons.sea.xml.BreedingViewProject();
		project.setName(this.gxeInput.getBreedingViewProjectName());
		project.setVersion("1.2");
		project.setPipelines(pipelines);

		BreedingViewSession bvSession = new BreedingViewSession();
		bvSession.setBreedingViewProject(project);
		bvSession.setDataFile(data);

		SSAParameters ssaParameters = new SSAParameters();
		// output directory is not needed if deployed on server
		if (!isServerApp) {
			try {
				String installationDirectory = this.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory();
				String outputDirectory =
						String.format("%s/workspace/%s/breeding_view/output", installationDirectory, this.gxeInput.getProject()
								.getProjectName());
				ssaParameters.setOutputDirectory(outputDirectory);
			} catch (Exception e) {
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
