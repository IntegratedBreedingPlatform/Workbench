package org.generationcp.ibpworkbench.germplasm.pedigree;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GraphVizUtilityTest {

	final GraphVizUtility graphVizUtility = new GraphVizUtility();

	@Test
	public void testInitialize() throws IOException {
		final Path currentRelativePath = Paths.get("");
		final Path tempFilePath = Files.createTempFile(currentRelativePath.toAbsolutePath(), RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
		final String fileNamePath = tempFilePath.getFileName().toString();
		graphVizUtility.setGraphvizExecutablePath(fileNamePath);
		graphVizUtility.initialize();

		Assert.assertEquals(tempFilePath.toAbsolutePath().toString(), graphVizUtility.getDotPath());
		Files.delete(tempFilePath);
	}

}
