
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.generationcp.ibpworkbench.builders.CSVReaderBuilder;
import org.generationcp.ibpworkbench.util.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Parser for Breeding View's output file.
 * 
 * @author Aldrin Batac
 * 
 */
public class BMSOutputParser {

	private static final String BMS_OUTLIER_FILENAME = "BMSOutlier";

	private static final String BMS_SUMMARY_FILENAME = "BMSSummary";

	private static final Logger LOG = LoggerFactory.getLogger(BMSOutputParser.class);

	private static final String BMS_OUTPUT_FILENAME = "BMSOutput";

	private static final String BMS_INFORMATION_FILENAME = "BMSInformation";

	private static final String INPUT_DATASET_ID_INFO = "InputDataSetId";

	private static final String OUTPUT_DATASET_ID_INFO = "OutputDataSetId";

	private static final String STUDY_ID_INFO = "StudyId";

	private static final String WORKBENCH_PROJECT_ID_INFO = "WorkbenchProjectId";

	private CSVReaderBuilder csvReaderBuilder;

	private File meansFile;

	private File summaryStatsFile;

	private File outlierFile;

	private File bmsInformationFile;

	private File zipFile;

	private String uploadDirectory = "temp";

	private BMSOutputInformation bmsOutputInformation;

	public BMSOutputParser() {
		this.csvReaderBuilder = new CSVReaderBuilder();
	}

	public BMSOutputInformation parseZipFile(File bmsOutputZipFile) throws ZipFileInvalidContentException {
		this.zipFile = bmsOutputZipFile;
		this.bmsOutputInformation = this.uncompressAndParseTheUploadedZipFile(this.zipFile);
		return this.bmsOutputInformation;
	}

	public void extractEnvironmentInfoFromFile(File file, BMSOutputInformation environmentInfo) {

		CSVReader reader;
		try {
			reader = this.csvReaderBuilder.build(file);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return;
		}
		String nextLine[];

		Set<String> environmentNames = new HashSet<>();

		try {

			// The first name in the first record is the environment factor name
			if ((nextLine = reader.readNext()) != null) {
				environmentInfo.setEnvironmentFactorName(nextLine[0]);
			}

			// Get the distinct environment names
			while ((nextLine = reader.readNext()) != null) {
				environmentNames.add(nextLine[0]);
			}

			reader.close();

			environmentInfo.setEnvironmentNames(environmentNames);

		} catch (IOException e) {

			LOG.error(e.getMessage(), e);
		}

	}

	protected BMSOutputInformation uncompressAndParseTheUploadedZipFile(File zipFile) throws ZipFileInvalidContentException {

		BMSOutputInformation environmentInfo = new BMSOutputInformation();

		String zipFilePath = zipFile.getAbsolutePath();

		this.bmsInformationFile = ZipUtil.extractZipSpecificFile(zipFilePath, BMS_INFORMATION_FILENAME, this.uploadDirectory);

		this.meansFile = ZipUtil.extractZipSpecificFile(zipFilePath, BMS_OUTPUT_FILENAME, this.uploadDirectory);

		this.summaryStatsFile = ZipUtil.extractZipSpecificFile(zipFilePath, BMS_SUMMARY_FILENAME, this.uploadDirectory);

		this.outlierFile = ZipUtil.extractZipSpecificFile(zipFilePath, BMS_OUTLIER_FILENAME, this.uploadDirectory);

		if (this.bmsInformationFile == null || this.meansFile == null) {
			throw new ZipFileInvalidContentException("The zip file " + zipFile.getName() + " is invalid for BMS upload");
		}

		this.extractEnvironmentInfoFromFile(this.meansFile, environmentInfo);
		this.extractBmsInformationFromFile(this.bmsInformationFile, environmentInfo);

		return environmentInfo;

	}

	protected void extractBmsInformationFromFile(File file, BMSOutputInformation bmsOutputInformation) {

		if (file == null) {
			return;
		}

		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileReader(file));
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return;
		}

		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (!line.startsWith("#")) {
					String[] mapping = line.split("=");
					this.mapToBmsOutputInformation(mapping[0], mapping[1], bmsOutputInformation);
				}
			}
		} finally {
			scanner.close();
		}
		return;
	}

	protected void mapToBmsOutputInformation(String key, String value, BMSOutputInformation bmsOutputInformation) {

		if (key.equals(INPUT_DATASET_ID_INFO)) {
			bmsOutputInformation.setInputDataSetId(Integer.parseInt(value));
		}

		if (key.equals(OUTPUT_DATASET_ID_INFO)) {
			bmsOutputInformation.setOutputDataSetId(Integer.parseInt(value));
		}

		if (key.equals(STUDY_ID_INFO)) {
			bmsOutputInformation.setStudyId(Integer.parseInt(value));
		}

		if (key.equals(WORKBENCH_PROJECT_ID_INFO)) {
			bmsOutputInformation.setWorkbenchProjectId(Integer.parseInt(value));
		}
	}

	public static class ZipFileInvalidContentException extends Exception {

		private static final long serialVersionUID = 1L;

		public ZipFileInvalidContentException() {

		}

		public ZipFileInvalidContentException(String message) {
			super(message);
		}

	}

	public void deleteUploadedZipFile() {
		if (this.zipFile != null && this.zipFile.exists()) {
			this.zipFile.delete();
		}
	}

	public void deleteTemporaryFiles() {

		if (this.meansFile != null && this.meansFile.exists()) {
			this.meansFile.delete();
		}
		if (this.summaryStatsFile != null && this.summaryStatsFile.exists()) {
			this.summaryStatsFile.delete();
		}
		if (this.outlierFile != null && this.outlierFile.exists()) {
			this.outlierFile.delete();
		}
		if (this.bmsInformationFile != null && this.bmsInformationFile.exists()) {
			this.bmsInformationFile.delete();
		}

	}

	protected void setUploadDirectory(String uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}

	public File getMeansFile() {
		return this.meansFile;
	}

	public File getSummaryStatsFile() {
		return this.summaryStatsFile;
	}

	public File getOutlierFile() {
		return this.outlierFile;
	}

	public BMSOutputInformation getBmsOutputInformation() {
		return this.bmsOutputInformation;
	}

}
