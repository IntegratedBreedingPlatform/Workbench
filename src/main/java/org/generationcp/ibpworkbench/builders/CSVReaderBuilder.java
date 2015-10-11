
package org.generationcp.ibpworkbench.builders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import au.com.bytecode.opencsv.CSVReader;

/*
 * Builder for creating CSVReader
 */
public class CSVReaderBuilder {

	/**
	 * Constructs CSVReader
	 * 
	 * @throws FileNotFoundException
	 */
	public CSVReader build(File file) throws FileNotFoundException {

		Reader fileReader = new FileReader(file);
		return new CSVReader(fileReader);

	}

}
