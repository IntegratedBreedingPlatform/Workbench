
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.generationcp.ibpworkbench.builders.CSVReaderBuilder;
import org.generationcp.ibpworkbench.util.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Parser for Breeding View's output files.
 * 
 * @author Aldrin Batac
 * 
 */
public class BMSOutputParser {

	private static final Logger LOG = LoggerFactory.getLogger(BMSOutputParser.class);

	public static final String ENVIRONMENT_FACTOR = "environmentFactor";

	public static final String ENVIRONMENT_NAMES = "environmentNames";

	public static final String INPUT_DATASET_ID_INFO = "InputDataSetId";

	public static final String OUTPUT_DATASET_ID_INFO = "OutputDataSetId";

	public static final String STUDY_ID_INFO = "StudyId";

	public static final String WORKBENCH_PROJECT_ID_INFO = "WorkbenchProjectId";

	private CSVReaderBuilder csvReaderBuilder;

	private File meansFile;

	private File summaryStatsFile;

	private File outlierFile;

	private File bmsInformationFile;

	private File zipFile;

	private String uploadDirectory = "temp";

	public BMSOutputParser() {
		this.csvReaderBuilder = new CSVReaderBuilder();
	}

	public Map<String, String> parseZipFile(File bmsOutputZipFile) throws ZipFileInvalidContentException {
		this.zipFile = bmsOutputZipFile;
		return this.uncompressTheUploadedZipFile(bmsOutputZipFile);
	}

	public Map<String, Object> extractEnvironmentInfoFromFile() throws IOException {

		Map<String, Object> environmentInfo = new HashMap<>();

		CSVReader reader = this.csvReaderBuilder.build(this.meansFile);
		String nextLine[];

		// The first name in the first record is the environment factor name
		if ((nextLine = reader.readNext()) != null) {
			environmentInfo.put(ENVIRONMENT_FACTOR, nextLine[0]);
		}

		// Get the distinct environment names
		Set<String> environmentNames = new HashSet<>();
		while ((nextLine = reader.readNext()) != null) {
			environmentNames.add(nextLine[0]);
		}

		environmentInfo.put(ENVIRONMENT_NAMES, environmentNames);

		reader.close();

		return environmentInfo;

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

	protected Map<String, String> uncompressTheUploadedZipFile(File zipFile) throws ZipFileInvalidContentException {

		String zipFilePath = zipFile.getAbsolutePath();

		this.bmsInformationFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSInformation", this.uploadDirectory);

		this.meansFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSOutput", this.uploadDirectory);

		this.summaryStatsFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSSummary", this.uploadDirectory);

		this.outlierFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSOutlier", this.uploadDirectory);

		if (this.bmsInformationFile == null) {
			throw new ZipFileInvalidContentException("The zip file " + zipFile.getName() + " is invalid for BMS upload");
		}

		return this.parseBmsInformationTextFile(this.bmsInformationFile);

	}

	protected Map<String, String> parseBmsInformationTextFile(File file) {
		Map<String, String> result = new HashMap<String, String>();

		if (file == null) {
			return result;
		}

		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileReader(file));
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return result;
		}

		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (!line.startsWith("#")) {
					String[] mapping = line.split("=");
					result.put(mapping[0], mapping[1]);
				}
			}
		} finally {
			scanner.close();
		}
		return result;
	}

	public static class ZipFileInvalidContentException extends Exception {

		private static final long serialVersionUID = 1L;

		public ZipFileInvalidContentException() {

		}

		public ZipFileInvalidContentException(String message) {
			super(message);
		}

	}

	public void deleteTemporaryFiles() {

		if (this.zipFile != null && this.zipFile.exists()) {
			this.zipFile.delete();
		}

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

}
