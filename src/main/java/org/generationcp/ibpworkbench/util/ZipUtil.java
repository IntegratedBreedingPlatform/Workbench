
package org.generationcp.ibpworkbench.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtil {

	private static final int BUFFER_SIZE = 4096;
	private static final Logger LOG = LoggerFactory.getLogger(ZipUtil.class);

	/**
	 * Zip it
	 * 
	 * @param zipFile output ZIP file location
	 */
	public void zipIt(String zipFile, List<String> filenameList) {

		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			ZipUtil.LOG.debug("Output to Zip : " + zipFile);

			for (String file : filenameList) {

				File f = new File(file);
				ZipEntry ze = new ZipEntry(f.getName());
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			// remember close it
			zos.close();

			ZipUtil.LOG.debug("Done");
		} catch (IOException ex) {
			ZipUtil.LOG.error(ex.getMessage(), ex);
		}
	}

	public void extractZip(String zipFile, String destination) {

		File file = new File(zipFile);
		InputStream input;

		try {

			input = new FileInputStream(file);
			ZipInputStream zip = new ZipInputStream(input);
			ZipEntry entry = zip.getNextEntry();

			while (entry != null) {

				String filePath = destination + File.separator + entry.getName();

				extractFile(zip, filePath);

				entry = zip.getNextEntry();
			}

			zip.close();
			input.close();

		} catch (FileNotFoundException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		}

	}

	public File extractZipSpecificFile(String zipFile, String fileNameToExtract, String destination) {

		File extractedFile = null;
		File file = new File(zipFile);
		InputStream input;

		try {

			input = new FileInputStream(file);
			ZipInputStream zip = new ZipInputStream(input);
			ZipEntry entry = zip.getNextEntry();

			while (entry != null) {

				if (entry.getName().toLowerCase().contains(fileNameToExtract.toLowerCase())) {
					String filePath = destination + File.separator + RandomUtils.nextInt() + entry.getName();
					this.extractFile(zip, filePath);
					extractedFile = new File(filePath);
					break;
				}
				entry = zip.getNextEntry();
			}

			zip.close();
			input.close();

			if (extractedFile != null) {
				return extractedFile;
			}

		} catch (FileNotFoundException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		}

		return null;

	}

	public void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[ZipUtil.BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

}
