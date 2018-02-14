/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ToolUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ToolUtil.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	/**
	 * Launch the specified native tool.
	 *
	 * @param tool
	 * @return the {@link Process} object created when the tool was launched
	 * @throws IOException if an I/O error occurs while trying to launch the tool
	 * @throws IllegalArgumentException if the specified Tool's type is not {@link ToolType#NATIVE}
	 */
	public Process launchNativeTool(final Tool tool) throws IOException {
		if (tool.getToolType() != ToolType.NATIVE) {
			throw new IllegalArgumentException("Tool must be a native tool");
		}

		String parameter = "";
		if (!StringUtil.isEmpty(tool.getParameter())) {
			parameter = tool.getParameter();
		}

		final String toolPath = tool.getPath();
		final File absoluteToolFile = new File(toolPath);

		final ProcessBuilder pb = new ProcessBuilder(toolPath, parameter);
		pb.directory(absoluteToolFile.getParentFile());
		return pb.start();
	}

	public void closeAllNativeTools() throws IOException {
		try {
			final List<Tool> nativeTools = this.workbenchDataManager.getToolsWithType(ToolType.NATIVE);

			for (final Tool tool : nativeTools) {
				this.closeNativeTool(tool);
			}

		} catch (final MiddlewareQueryException e) {
			ToolUtil.LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Close the specified native tool.
	 *
	 * @param tool
	 * @throws IOException if an I/O error occurs while trying to stop the tool
	 * @throws IllegalArgumentException if the specified Tool's type is not {@link ToolType#NATIVE}
	 */
	public void closeNativeTool(final Tool tool) throws IOException {
		if (tool.getToolType() != ToolType.NATIVE) {
			throw new IllegalArgumentException("Tool must be a native tool");
		}

		if (!SystemUtils.IS_OS_WINDOWS) {
			return;
		}

		final String toolPath = tool.getPath();
		final File absoluteToolFile = new File(toolPath);
		final String[] pathTokens = toolPath.split("\\" + File.separator);

		final String executableName = pathTokens[pathTokens.length - 1];

		// taskkill /T /F /IM <exe name>
		final ProcessBuilder pb = new ProcessBuilder("taskkill", "/T", "/F", "/IM", executableName);
		pb.directory(absoluteToolFile.getParentFile());

		final Process process = pb.start();
		try {
			process.waitFor();
		} catch (final InterruptedException e) {
			ToolUtil.LOG.error("Interrupted while waiting for " + tool.getToolName() + " to stop.");
		}
	}

	protected boolean updatePropertyFile(final File propertyFile, final Map<String, String> newPropertyValues) {
		boolean changed = false;

		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			// load the property files
			final Properties properties = new Properties();
			fis = new FileInputStream(propertyFile);
			properties.load(fis);

			// update the property values
			for (final String key : newPropertyValues.keySet()) {
				final String newValue = newPropertyValues.get(key);
				final String oldValue = properties.getProperty(key);

				final boolean equal = newValue == null ? oldValue == null : newValue.equals(oldValue);
				if (!equal) {
					changed = true;
					properties.setProperty(key, newValue);
				}
			}

			// close the file input stream

			// save the new property values
			if (changed) {
				fos = new FileOutputStream(propertyFile);
				properties.store(fos, null);
				fos.flush();
			}
		} catch (final IOException e1) {
			ToolUtil.LOG.error("Cannot update property file: " + propertyFile.getAbsolutePath(), e1);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
					ToolUtil.LOG.error(e.getMessage(), e);
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (final IOException e) {
					ToolUtil.LOG.error(e.getMessage(), e);
				}
			}
		}

		return changed;
	}
	
}
